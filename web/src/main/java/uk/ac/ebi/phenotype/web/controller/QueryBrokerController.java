/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
package uk.ac.ebi.phenotype.web.controller;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.mousephenotype.cda.solr.SolrUtils;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.SolrIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;


@Controller
public class QueryBrokerController {

	private final Logger log = LoggerFactory.getLogger(this.getClass().getCanonicalName());

	@Autowired
	private SolrIndex solrIndex;

	@Autowired
	private GeneService geneService;

	@Resource(name="globalConfiguration")
	private Map<String, String> config;

	// Use cache to manage queries for minimizing network traffic
	final int MAX_ENTRIES = 600;

	@SuppressWarnings("unchecked")
	Map<String, Object> cache = (Map<String, Object>) Collections.synchronizedMap(new LinkedHashMap<String, Object>(MAX_ENTRIES+1, .75F, true) {
		private static final long serialVersionUID = 1L;

		// This method is called just after a new entry has been added
	    public boolean removeEldestEntry(Map.Entry eldest) {
	        return size() > MAX_ENTRIES;
	    }
	});


	@RequestMapping(value = "/fetchDefaultCore", method = RequestMethod.GET)
	@ResponseBody public String fetchDefaultCore(
			@RequestParam(value = "q", required = true) String query,
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) throws IOException, URISyntaxException  {

		String solrParams = "&rows=0&wt=json&&defType=edismax&qf=auto_suggest&facet.field=docType&facet=on&facet.limit=-1&facet.mincount=1";

		String solrurl = SolrUtils.getBaseURL(solrIndex.getSolrServer("autosuggest")) + "/select?q=" + query + solrParams; // not working, points to omero image baseurl

		//System.out.println("QueryBroker url: "+ solrurl);
		JSONObject json = solrIndex.getResults(solrurl);

		JSONArray docCount = json.getJSONObject("facet_counts").getJSONObject("facet_fields").getJSONArray("docType");
		Map<String, Integer> dc = new HashMap<>();
		for( int i=0; i<docCount.size(); i=i+2){
			dc.put(docCount.get(i).toString(), (Integer) docCount.get(i+1));
		}

		// priority order of facet to be opened based on search result
		String defaultCore = "gene";
		if ( dc.containsKey("gene") ) {
			defaultCore = "gene";
		} else if ( dc.containsKey("mp") ) {
			defaultCore = "mp";
		} else if ( dc.containsKey("disease") ) {
			defaultCore = "disease";
		} else if ( dc.containsKey("anatomy") ) {
			defaultCore = "anatomy";
		} else if ( dc.containsKey("impc_images") ) {
			defaultCore = "impc_images";
		}
//		else if ( dc.containsKey("images") ) {
//			defaultCore = "images";
//		}

		//System.out.println("default core: " + defaultCore);
		return defaultCore;
	}


	/**
	 * Examine or clear cached SOLR queries
	 *
	 * @param clearCache true to clear the cache, false to examine the cached keys
	 */
	@RequestMapping(value = "/querybroker", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> clearCache(
		@RequestParam(value = "clearCache", required = false) Boolean clearCache) {

		JSONObject jsonResponse = new JSONObject();

		if (clearCache!=null && clearCache==true) {
			jsonResponse.put("Details", cache.keySet().size() + " cleared from cache");
			cache.clear();
		} else {
			jsonResponse.put("Details", cache.keySet().size() + " entries in cache");
			jsonResponse.put("Cached Keys", cache.keySet());
		}

		return new ResponseEntity<JSONObject>(jsonResponse, createResponseHeaders(), HttpStatus.CREATED);
	}

	/**
	 * <p>
	 * Return multiple solr json responses from server to avoid multiple calls from client
	 * Using cache to further reduce queries to the SOLR server
	 * </p>
	 *
	 * @param
	 *
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@RequestMapping(value = "/querybroker", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> jsons(
			@RequestParam(value = "q", required = true) String solrParams,
			@RequestParam(value = "subfacet", required = false) String subfacet,
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) throws IOException, URISyntaxException  {

		JSONObject jParams = (JSONObject) JSONSerializer.toJSON(solrParams);

		JSONObject jsonResponse = createJsonResponse(subfacet, jParams, request);

		return new ResponseEntity<JSONObject>(jsonResponse, createResponseHeaders(), HttpStatus.CREATED);
	}

	private HttpHeaders createResponseHeaders(){
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		return responseHeaders;
	}

	public JSONObject createJsonResponse(String subfacet, JSONObject jParams, HttpServletRequest request) throws IOException, URISyntaxException {

		JSONObject jsonResponse = new JSONObject();

		Iterator cores = jParams.keys();

		while(cores.hasNext()) {

			String core  = (String) cores.next();

			String param = jParams.getString(core);

			String url =  SolrUtils.getBaseURL(solrIndex.getSolrServer(core)) + "/select?" + param;
			//System.out.println("QueryBrokerController: "+url);
			String key = core+param;
			Object o = cache.get(key);

			if (o == null && !cache.containsKey(key)) {
			    // Object not in cache. If null is not a possible value in the cache,
			    // the call to cache.contains(key) is not needed
				JSONObject json = solrIndex.getResults(url);
				//System.out.println("JSON: "+ json);
				if ( subfacet == null ){
					// Main facet counts only
					int numFound = json.getJSONObject("response").getInt("numFound");
					jsonResponse.put(core, numFound);

					cache.put(key, numFound);
					//System.out.println("####### Cache for main facet added");
				}
				else {
					JSONObject j = new JSONObject();
					j.put("response", json.getJSONObject("response"));
					j.put("facet_counts", json.getJSONObject("facet_counts"));
					jsonResponse.put(core, j);

					cache.put(key, j);
					//System.out.println("****** Cache for subfacet added");
				}
			}
			else {
				jsonResponse.put(core, o);
				//System.out.println("------ Using cache");
			}
		}
		return jsonResponse;
	}

}
