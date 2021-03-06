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

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.collections.ArrayStack;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.mousephenotype.cda.neo4j.entity.*;
import org.mousephenotype.cda.neo4j.repository.*;
import org.mousephenotype.cda.solr.generic.util.Tools;
import org.mousephenotype.cda.solr.service.AutoSuggestService;
import org.mousephenotype.cda.solr.service.SolrIndex;

import org.mousephenotype.cda.solr.service.dto.MpDTO;
import org.mousephenotype.cda.solr.service.dto.PipelineDTO;
import org.neo4j.ogm.model.GraphRowListModel;
import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.neo4j.annotation.QueryResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@PropertySource("file:${user.home}/configfiles/${profile}/application.properties")
public class AdvancedSearchController {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getCanonicalName());

    @Autowired
    private Session neo4jSession;

    @Resource(name = "globalConfiguration")
    private Map<String, String> config;

    @Autowired
    private SolrIndex solrIndex;

    @Autowired
    @Qualifier("komp2DataSource")
    private DataSource komp2DataSource;

    @Autowired
    @Qualifier("mpCore")
    private SolrClient mpCore;

    @Autowired
    GeneRepository geneRepository;

    @Autowired
    AlleleRepository alleleRepository;

    @Autowired
    EnsemblGeneIdRepository ensemblGeneIdRepository;

    @Autowired
    MarkerSynonymRepository markerSynonymRepository;

    @Autowired
    HumanGeneSymbolRepository humanGeneSymbolRepository;

    @Autowired
    MpRepository mpRepository;

    @Autowired
    HpRepository hpRepository;

    @Autowired
    OntoSynonymRepository ontoSynonymRepository;

    @Autowired
    DiseaseGeneRepository diseaseGeneRepository;

    @Autowired
    DiseaseModelRepository diseaseModelRepository;

    @Autowired
    MouseModelRepository mouseModelRepository;

    @Autowired
    @Qualifier("autosuggestCore")
    private SolrClient autosuggestCore;


    private String NA = "not available";
    private String hostname = null;
    private String baseUrl = null;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//    @RequestMapping(value = "/graph/nodeName?", method = RequestMethod.POST)
//    public ResponseEntity<String> dataTableNeo4jBq(
//            @RequestParam(value = "docType", required = true) String docType,
//            @RequestParam(value = "q", required = true) String query,
//            HttpServletResponse response,
//            Model model) throws IOException, URISyntaxException, SolrServerException{
//
//        String sortStr = "&sort=score desc";
//        String solrBq = null;
//        String qfStr = query.contains("*") ? "auto_suggest" : "string auto_suggest";
//
////        if ( thisInput.hasClass('srchMp') ){
////            docType = 'mp';
////            solrBq = "&bq=mp_term:*^90 mp_term_synonym:*^80 mp_narrow_synonym:*^75";
////        }
////        else if ( thisInput.hasClass('srchHp') ){
////            docType = 'hp';
////            solrBq = "&bq=hp_term:*^90 hp_term_synonym:*^80";
////        }
////        else if ( thisInput.hasClass('srchDiseaseModel') ){
////            docType = 'disease';
////        }
////
////        thisInput.autocomplete({
////                source: function( request, response ) {
////            var qfStr = request.term.indexOf("*") != -1 ? "auto_suggest" : "string auto_suggest";
////            // var facetStr = "&facet=on&facet.field=docType&facet.mincount=1&facet.limit=-1";
////            var sortStr = "&sort=score desc";
////            // alert(solrUrl + "/autosuggest/select?rows=10&fq=docType:" + docType + "&wt=json&qf=" + qfStr + "&defType=edismax" + solrBq + sortStr);                        )
////
////            $.ajax({
////                    //url: solrUrl + "/autosuggest/select?wt=json&qf=string auto_suggest&defType=edismax" + solrBq,
////                    url: solrUrl + "/autosuggest/select?rows=10&fq=docType:" + docType + "&wt=json&qf=" + qfStr + "&defType=edismax" + solrBq + sortStr,
////                    dataType
//
//        if(docType.equals("mp")){
//            solrBq = "&bq=mp_term:*^90 mp_term_synonym:*^80 mp_narrow_synonym:*^75";
////            autoSuggestService.getMpTermByKeyword(docType, qfStr, solrBq);
//            String solrurl = SolrUtils.getBaseURL(solrIndex.getSolrServer("autosuggest"))
//                    + "/select?q=" + query +
//
//            //System.out.println("QueryBroker url: "+ solrurl);
//            JSONObject json = solrIndex.getResults(solrurl);
//        }
//        return null;
//    }

        @RequestMapping(value="/meshtree", method=RequestMethod.GET)
    public String loadmeshtreePage(
            @RequestParam(value = "core", required = false) String core,
            HttpServletRequest request,
            Model model) {

        String outputFieldsHtml = Tools.fetchOutputFieldsCheckBoxesHtml(core);
        model.addAttribute("outputFields", outputFieldsHtml);

        return "treetest";
    }

    @RequestMapping(value="/batchQuery3", method=RequestMethod.GET)
    public String loadBatchQueryPage3(
            @RequestParam(value = "core", required = false) String core,
            @RequestParam(value = "fllist", required = false) String fllist,
            @RequestParam(value = "idlist", required = false) String idlist,
            HttpServletRequest request,
            Model model) {

        String outputFieldsHtml = Tools.fetchOutputFieldsCheckBoxesHtml2(core);
        model.addAttribute("outputFields", outputFieldsHtml);

        if ( idlist != null) {
            model.addAttribute("core", core);
            model.addAttribute("fllist", fllist);
            model.addAttribute("idlist", idlist);
        }

        return "batchQuery3";
    }
    @RequestMapping(value="/advancedSearch", method=RequestMethod.GET)
    public String loadAdvSrchPage(
            HttpServletRequest request,
            Model model) {

        return "advancedSearch";
    }
    @RequestMapping(value="/batchQuery2", method=RequestMethod.GET)
    public String loadBatchQueryPage2(
            @RequestParam(value = "core", required = false) String core,
            @RequestParam(value = "fllist", required = false) String fllist,
            @RequestParam(value = "idlist", required = false) String idlist,
            HttpServletRequest request,
            Model model) {

        String outputFieldsHtml = Tools.fetchOutputFieldsCheckBoxesHtml2(core);
        model.addAttribute("outputFields", outputFieldsHtml);

        if ( idlist != null) {
            model.addAttribute("core", core);
            model.addAttribute("fllist", fllist);
            model.addAttribute("idlist", idlist);
        }

        return "batchQuery2";
    }


    @RequestMapping(value = "/dataTable_bq2", method = RequestMethod.POST)
    public ResponseEntity<String> dataTableNeo4jBq(
            @RequestParam(value = "idlist", required = true) String idlistStr,
            @RequestParam(value = "datatypeProperty", required = true) String datatypeProperty,
            @RequestParam(value = "dataType", required = true) String dataType,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) throws IOException, URISyntaxException, SolrServerException {

        System.out.println("dataType: " +  dataType);;
        System.out.println("idlist: " + idlistStr);
        JSONObject dp = (JSONObject) JSONSerializer.toJSON(datatypeProperty);
        Set<String> labels = dp.keySet();
        System.out.println("Labels: "+ dp.keySet());

        if (dataType.equals("geneChr")){
            // convert coordiantes range to list of mouse gene ids
            String[] parts = idlistStr.replaceAll("\"","").split(":");
            String chr = parts[0].replace("chr","");
            String[] se = parts[1].split("-");
            String start = se[0];
            String end = se[1];
            String mode = "nonExport";
            List<String> geneIds = solrIndex.fetchQueryIdsFromChrRange(chr, start, end, mode);
            idlistStr = StringUtils.join(geneIds, ",");
        }

//        String cypher = null;
//        if (labels.size() == 2 && labels.contains("Gene") && labels.contains("Allele")){
//            cypher = "MATCH (g:Gene)-[:ALLELE]->(a:Allele)"
//        }

        return null;
    }

    @RequestMapping(value = "/fetchmpid", method = RequestMethod.GET)
    public ResponseEntity<String> fetchmpid(
            @RequestParam(value = "name", required = true) String termName,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) throws Exception {

        //System.out.println("****"+ termName);

        List<MpDTO> mp = getMpIdByTerm(termName);
        String mpId = null;

        if (mp.size() == 0){
            // use parent instead if term does not exits in slim (mpCore) but in narrowSynonym in the autoSuggestCore
            List<String> narrowMapping = new ArrayList<>();
            termName = mapNarrowSynonym2MpTerm(narrowMapping, termName, autosuggestCore);
            mp = getMpIdByTerm(termName);
            mpId = "narrow synonym of " + mp.get(0).getMpId()+ "," + termName;

            System.out.println(mpId + "," + termName);
        }
        else {
            mpId = mp.get(0).getMpId();
            System.out.println(mpId);
        }

        return new ResponseEntity<String>(mpId, createResponseHeaders(), HttpStatus.CREATED);
    }


    @RequestMapping(value = "/dataTableNeo4jAdvSrch", method = RequestMethod.POST)
    public ResponseEntity<String> advSrchDataTableJson2(
            @RequestParam(value = "params", required = true) String params,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) throws Exception {

        baseUrl = request.getAttribute("baseUrl").toString();
        hostname = request.getAttribute("mappedHostname").toString();

//        System.out.println("hostname: " + hostname);
//        System.out.println("baseUrl: " + baseUrl);

        JSONObject jParams = (JSONObject) JSONSerializer.toJSON(params);
        System.out.println(jParams.toString());

        String content = null;
        String fileType = null;
        JSONObject jcontent = fetchGraphDataAdvSrch(jParams, fileType);

        System.out.println("narrow synonym msg: " + jcontent.get("narrowMapping"));
        return new ResponseEntity<String>(jcontent.toString(), createResponseHeaders(), HttpStatus.CREATED);
    }

    public List<MpDTO> getMpIdByTerm(String termName) throws IOException, SolrServerException {
        //System.out.println("****"+ termName);
        SolrQuery query = new SolrQuery()
                .setQuery("mp_term:\"" + termName + "\"")
                .setFields("mp_id");

        System.out.println("query for : " + termName + " with " + query);

        List<MpDTO> mp = mpCore.query(query).getBeans(MpDTO.class);

        return mp;
    }

    public JSONObject fetchGraphDataAdvSrch(JSONObject jParams, String fileType) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, IOException, SolrServerException {

        JSONArray properties = jParams.getJSONArray("properties");
        System.out.println("columns: " + properties);

        Map<String, String> dtypeMap = new HashMap<>();
        JSONArray dataTypes = jParams.getJSONArray("dataTypes");
        List<String> dts = new ArrayList<>();
        for (int d = 0; d < dataTypes.size(); d++){
            dts.add(dataTypes.get(d).toString());
            dtypeMap.put("a", "nodes.alleles");
            dtypeMap.put("sr", "nodes.srs");
            dtypeMap.put("mp", "nodes.mps");
        }
        String returnDtypes = StringUtils.join(dts, ", ");
        System.out.println("return types: " + returnDtypes);

        String significant = composeSignificance(jParams);
        String phenotypeSexes = composePhenotypeSexStr(jParams);
        String parameter = composeParameter(jParams);
        String pvalues = composePvalues(jParams);
        String chrRange = composeChrRangeStr(jParams);
        String geneList = composeGeneListStr(jParams);
        String genotypes = composeGenotypesStr(jParams);
        String alleleTypes = composeAlleleTypesStr(jParams);

        // disease
        String phenodigmScore = composePhenodigmScoreStr(jParams);  // always non-empty
        String diseaseGeneAssociation = composeDiseaseGeneAssociation(jParams);
        String humanDiseaseTerm = composeHumanDiseaseTermStr(jParams);

        Boolean noMpChild = jParams.containsKey("noMpChild") ? true : false;

        String mpStr = null;
        if (jParams.containsKey("srchMp")) {
            mpStr = jParams.getString("srchMp");
        }

        //String mpStr = "  ( APERT-CROUZON DISEASE, INCLUDED  AND mpb alcohokl  )OR  mapasdfkl someting "; // (a and b) or c
        //String mpStr = " dfkdfkdfk kdkk AND ( sfjjjj ii OR kkkk, ) "; // a and (b or c)
        //String mpStr = " ( asdfsdaf OR erueuru asdfsda ,  sdfa ) AND sakdslfjie dfd ";  // (a or b) and c
        //String mpStr = " asdfsdaf OR  ( erueuru asdfsda ,  sdfa AND sakdslfjie dfd ) ";  // a or (b and c)
        //String mpStr = "asdfsdaf AND erueuru asdfsda ,  sdfa AND sakdslfjie dfd "; // a and b and c
        //String mpStr = "asdfsdaf AND erueuru asdfsda ,  sdfa  "; // a and b
        //String mpStr = "asdfsdaf OR erueuru asdfsda ,  sdfa OR sakdslfjie dfd ";  // a or b or c
        //String mpStr = "asdfsdaf OR erueuru asdfsda ,  sdfa ";  // a or b

        System.out.println("mp query: "+ mpStr);

        String regex_aAndb_Orc = "\\s*\\(([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*\\b(AND|and)\\b\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\)\\s*\\b(OR|or)\\b\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*";
        String regex_aAnd_bOrc = "\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*\\b(AND|and)\\b\\s*\\(([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*\\b(OR|or)\\b\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\)\\s*";
        String regex_aOrb_andc = "\\s*\\(([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*\\b(OR|or)\\b\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\)\\s*\\b(AND|and)\\b\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*";
        String regex_aOr_bAndc = "\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*\\b(OR|or)\\b\\s*\\(([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*\\b(AND|and)\\b\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\)\\s*";
        String regex_aAndb = "\\s*\\(*([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*\\b(AND|and)\\b\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\)*\\s*";
        String regex_aAndbAndc = "\\s*\\(*([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*\\b(AND|and)\\b\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\)*\\s*\\b(AND|and)\\b\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\)*\\s*";
        String regex_aOrb = "\\s*\\(*([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*\\b(OR|or)\\b\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\)*\\s*";
        String regex_aOrbOrc = "\\s*\\(*([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*\\b(OR|or)\\b\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\)*\\s*\\b(OR|or)\\b\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\)*\\s*";

        HashedMap params = new HashedMap();

        Result result = null;

        String sortStr = " ORDER BY g.markerSymbol ";
        String query = null;


        String geneToMpPath = noMpChild ? "MATCH (g:Gene)<-[:GENE]-(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp) "
                : " MATCH (g:Gene)<-[:GENE]-(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp)-[:PARENT*0..]->(mp0:Mp) ";

        String mpToGenePath = noMpChild ? "MATCH (mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g:Gene) "
                : " MATCH (mp0:Mp)<-[:PARENT*0..]-(mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g:Gene) ";

        String geneToDmPathClause = " OPTIONAL MATCH (g)<-[:GENE]-(dm:DiseaseModel)-[:MOUSE_PHENOTYPE]->(dmp:Mp) WHERE "
                + phenodigmScore + diseaseGeneAssociation + humanDiseaseTerm;


        String pvaluesA = "";
        String pvaluesB = "";
        String pvaluesC = "";
        List<String> narrowMapping = new ArrayList<>();

        long begin = System.currentTimeMillis();

        if (mpStr == null && ! humanDiseaseTerm.isEmpty()){

            String where = noMpChild ? " WHERE mp.mpTerm =~ '.*' " : " WHERE mp0.mpTerm =~ '.*' ";

            query = mpToGenePath + "<-[:GENE]-(dm:DiseaseModel)-[:MOUSE_PHENOTYPE]->(dmp:Mp)"
                + where
                + " AND " + significant + phenotypeSexes + parameter + pvalues + chrRange + geneList + genotypes + alleleTypes
                + " AND " + phenodigmScore + diseaseGeneAssociation + humanDiseaseTerm
                + " AND dmp.mpTerm in mp.mpTerm";

            query += fileType != null ?
                   // " RETURN distinct a, g, sr, collect(distinct mp), collect(distinct dm)" + sortStr :
                    " RETURN distinct " + returnDtypes + sortStr :
                    " RETURN collect(distinct a), collect(distinct g), collect(distinct sr), collect(distinct mp), collect(distinct dm)";

            System.out.println("Query: "+ query);
            result = neo4jSession.query(query, params);
        }
        else if (mpStr == null ){

            String where = noMpChild ? " WHERE mp.mpTerm =~ '.*' " : " WHERE mp0.mpTerm =~ '.*' ";

            if (geneList.isEmpty()) {
                query = mpToGenePath
                        + where
                        + " AND " + significant + phenotypeSexes + parameter + pvalues + chrRange + geneList + genotypes + alleleTypes
                        + " WITH g, a, mp, sr, "
                        + " extract(x in collect(distinct mp) | x.mpTerm) as mps "
                        + geneToDmPathClause
                        + " AND dmp.mpTerm IN mps";
            }
            else {
                query = geneToMpPath
                        + where
                        + " AND " + significant + phenotypeSexes + parameter + pvalues + chrRange + geneList + genotypes + alleleTypes
                        + " WITH g, a, mp, sr, "
                        + " extract(x in collect(distinct mp) | x.mpTerm) as mps "
                        + geneToDmPathClause
                        + " AND dmp.mpTerm IN mps";
            }

            query += fileType != null ?
                    //" RETURN distinct a, g, sr, collect(distinct mp), collect(distinct dm)" + sortStr :
                    " RETURN distinct " + returnDtypes + sortStr :
                    " RETURN collect(distinct a), collect(distinct g), collect(distinct sr), collect(distinct mp), collect(distinct dm)";

            System.out.println("Query: "+ query);
            result = neo4jSession.query(query, params);
        }
        else if (! mpStr.contains("AND") && ! mpStr.contains("OR") ) {
            // single mp term

            mpStr = mpStr.trim();
            mpStr = mapNarrowSynonym2MpTerm(narrowMapping, mpStr, autosuggestCore);

            params.put("mpA", mpStr);
            logger.info("A: '{}'", mpStr);


            String whereClause = noMpChild ? " WHERE mp.mpTerm = '" + params.get("mpA") + "'" : " WHERE mp0.mpTerm = '" + params.get("mpA") + "'";

            if (geneList.isEmpty()) {
                query = mpToGenePath
                        //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') "
                        + whereClause
                        + " AND " + significant + phenotypeSexes + parameter + pvalues + chrRange + geneList + genotypes + alleleTypes
                        + " WITH g, a, mp, sr, "
                        + " extract(x in collect(distinct mp) | x.mpTerm) as mps "
                        + geneToDmPathClause
                        + " AND dmp.mpTerm IN mps";
            }
            else {
                query = geneToMpPath
                        //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') "
                        + whereClause
                        + " AND " + significant + phenotypeSexes + parameter + pvalues + chrRange + geneList + genotypes + alleleTypes
                        + " WITH g, a, mp, sr, "
                        + " extract(x in collect(distinct mp) | x.mpTerm) as mps "
                        + geneToDmPathClause
                        + " AND dmp.mpTerm IN mps";
            }

            query += fileType != null ?
                    //" RETURN distinct a, g, sr, collect(distinct mp), collect(distinct dm)" + sortStr :
                    " RETURN distinct " + returnDtypes + sortStr :
                    " RETURN collect(distinct a), collect(distinct g), collect(distinct sr), collect(distinct mp), collect(distinct dm)";

            System.out.println("Query: "+ query);
            result =  neo4jSession.query(query, params);
        }
        else if (mpStr.matches(regex_aAndb_Orc)) {
            System.out.println("matches (a and b) or c"); // due to join empty list to a non-empty list evals to empty, convert this to (a or c) + (b or c)

            Pattern pattern = Pattern.compile(regex_aAndb_Orc);
            Matcher matcher = pattern.matcher(mpStr);

            while (matcher.find()) {
                System.out.println("found: " + matcher.group(0));
                String mpA = matcher.group(1).trim();;
                String mpB = matcher.group(3).trim();;
                String mpC = matcher.group(5).trim();;
                logger.info("A: '{}', B: '{}', C: '{}'", mpA, mpB, mpC);

                params.put("mpA", mapNarrowSynonym2MpTerm(narrowMapping, mpA, autosuggestCore));
                params.put("mpB", mapNarrowSynonym2MpTerm(narrowMapping, mpB, autosuggestCore));
                params.put("mpC", mapNarrowSynonym2MpTerm(narrowMapping, mpC, autosuggestCore));

                pvaluesA = composePvalues(params.get("mpA").toString(), jParams);
                pvaluesB = composePvalues(params.get("mpB").toString(), jParams);
                pvaluesC = composePvalues(params.get("mpC").toString(), jParams);
            }

            String whereClause1 = noMpChild ? " WHERE ((mp.mpTerm = '" + params.get("mpA") + "'" + pvaluesA + ") OR (mp.mpTerm ='" + params.get("mpC") + "'" + pvaluesC + ")) "
                    : " WHERE ((mp0.mpTerm = '" + params.get("mpA") + "'" + pvaluesA + ") OR (mp0.mpTerm ='" + params.get("mpC") + "'" + pvaluesC + "))";

            String whereClause2 = noMpChild ? " WHERE ((mp.mpTerm = '" + params.get("mpB") + "'" + pvaluesB + ") OR (mp.mpTerm ='" + params.get("mpC") + "'" + pvaluesC + ")) "
                    : " WHERE ((mp0.mpTerm = '" + params.get("mpB") + "'" + pvaluesB + ") OR (mp0.mpTerm ='" + params.get("mpC") + "'" + pvaluesC + "))";


            String matchClause1 = noMpChild ? " MATCH (mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g) "
                    : " MATCH (mp0:Mp)<-[:PARENT*0..]-(mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g) ";

            String matchClause2 = noMpChild ? " MATCH (g)<-[:GENE]-(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp) "
                    : " MATCH (g)<-[:GENE]-(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp)-[:PARENT*0..]->(mp0:Mp) ";

            if (geneList.isEmpty()){

                // collect() will be null if one of the list is empty, to avoid this, use OPTIONAL MATCH instead

                query = mpToGenePath
                        //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') "
                        + whereClause1
                        + " AND " + significant + phenotypeSexes + parameter + chrRange + geneList + genotypes + alleleTypes
                        + " WITH g, collect({genes:g, alleles:a, srs:sr, mps:mp}) as list1 "

                        + matchClause1
                        //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*') "
                        + whereClause2
                        + " AND " + significant + phenotypeSexes + parameter + chrRange + geneList + genotypes + alleleTypes
                        + " WITH g, list1, collect({genes:g, alleles:a, srs:sr, mps:mp}) as list2 "
                        + " WHERE ALL (x IN list1 WHERE x IN list2) "
                        + " WITH g, list1+list2 as list "
                        + " UNWIND list as nodes "

                        + " WITH g, nodes, extract(x in collect(distinct nodes.mps) | x.mpTerm) as mps "
                        + geneToDmPathClause
                        + " AND dmp.mpTerm IN mps";

            }
            else {
                query = geneToMpPath
                        //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') "
                        + whereClause1
                        + " AND " + significant + phenotypeSexes + parameter + chrRange + geneList + genotypes + alleleTypes
                        + " WITH g, collect({genes:g, alleles:a, srs:sr, mps:mp}) as list1 "

                        + matchClause2
                        //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*') "
                        + whereClause2
                        + " AND " + significant + phenotypeSexes + parameter + chrRange + geneList + genotypes + alleleTypes
                        + " WITH g, list1, collect({genes:g, alleles:a, srs:sr, mps:mp}) as list2 "
                        + " WHERE ALL (x IN list1 WHERE x IN list2) "
                        + " WITH g, list1+list2 as list "
                        + " UNWIND list as nodes "

                        + " WITH g, nodes, extract(x in collect(distinct nodes.mps) | x.mpTerm) as mps "
                        + geneToDmPathClause
                        + " AND dmp.mpTerm IN mps";
            }

            dts = new ArrayList<>();
            for (int d = 0; d < dataTypes.size(); d++){
                String dt = dataTypes.get(d).toString();
                dts.add(dtypeMap.containsKey(dt) ? dt : dtypeMap.get(dt));
            }
            returnDtypes = StringUtils.join(dts, ", ");

            query += fileType != null ?
                    //" RETURN distinct nodes.alleles, g, nodes.srs, collect(distinct nodes.mps), collect(distinct dm)" + sortStr
                    //" RETURN distinct nodes.alleles, g, nodes.srs, nodes.mps, dm" + sortStr :
                    " RETURN distinct " + returnDtypes + sortStr :
                    " RETURN collect(distinct nodes.alleles), collect(distinct g), collect(distinct nodes.srs), collect(distinct nodes.mps), collect(distinct dm)";

            System.out.println("Query: "+ query);
            result =  neo4jSession.query(query, params);
        }
        else if (mpStr.matches(regex_aOr_bAndc)) {
            System.out.println("matches a or (b and c)"); // due to join empty list to a non-empty list evals to empty, convert this to (b or a) + (c or a)

            Pattern pattern = Pattern.compile(regex_aOr_bAndc);
            Matcher matcher = pattern.matcher(mpStr);

            while (matcher.find()) {
                System.out.println("found: " + matcher.group(0));
                String mpA = matcher.group(1).trim();
                String mpB = matcher.group(3).trim();;
                String mpC = matcher.group(5).trim();;

                logger.info("A: '{}', B: '{}', C: '{}'", mpA, mpB, mpC);

                params.put("mpA", mapNarrowSynonym2MpTerm(narrowMapping, mpA, autosuggestCore));
                params.put("mpB", mapNarrowSynonym2MpTerm(narrowMapping, mpB, autosuggestCore));
                params.put("mpC", mapNarrowSynonym2MpTerm(narrowMapping, mpC, autosuggestCore));

                pvaluesA = composePvalues(params.get("mpA").toString(), jParams);
                pvaluesB = composePvalues(params.get("mpB").toString(), jParams);
                pvaluesC = composePvalues(params.get("mpC").toString(), jParams);
            }

            String whereClause1 = noMpChild ? " WHERE ((mp.mpTerm = '" + params.get("mpB") + "'" + pvaluesB + ") OR (mp.mpTerm ='" + params.get("mpA") + "'" + pvaluesA + ")) "
                    : " WHERE ((mp0.mpTerm = '" + params.get("mpB") + "'" + pvaluesB + ") OR (mp0.mpTerm ='" + params.get("mpA") + "'" + pvaluesA + "))";

            String whereClause2 = noMpChild ? " WHERE ((mp.mpTerm = '" + params.get("mpC") + "'" + pvaluesC + ") OR (mp.mpTerm ='" + params.get("mpA") + "'" + pvaluesA + ")) "
                    : " WHERE ((mp0.mpTerm = '" + params.get("mpC") + "'" + pvaluesC + ") OR (mp0.mpTerm ='" + params.get("mpA") + "'" + pvaluesA + "))";


            String matchClause1 = noMpChild ? " MATCH (mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g) "
                    : " MATCH (mp0:Mp)<-[:PARENT*0..]-(mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g) ";

            String matchClause2 = noMpChild ? " MATCH (g)<-[:GENE]-(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp) "
                    : " MATCH (g)<-[:GENE]-(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp)-[:PARENT*0..]->(mp0:Mp) ";

            if (geneList.isEmpty()){

                // collect() will be null if one of the list is empty, to avoid this, use OPTIONAL MATCH instead

                query = mpToGenePath
                        //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') "
                        + whereClause1
                        + " AND " + significant + phenotypeSexes + parameter + chrRange + geneList + genotypes + alleleTypes
                        + " WITH g, collect({genes:g, alleles:a, srs:sr, mps:mp}) as list1 "

                        + matchClause1
                        //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*') "
                        + whereClause2
                        + " AND " + significant + phenotypeSexes + parameter + chrRange + geneList + genotypes + alleleTypes
                        + " WITH g, list1, collect({genes:g, alleles:a, srs:sr, mps:mp}) as list2 "
                        + " WHERE ALL (x IN list1 WHERE x IN list2) "
                        + " WITH g, list1+list2 as list "
                        + " UNWIND list as nodes "

                        + " WITH g, nodes, extract(x in collect(distinct nodes.mps) | x.mpTerm) as mps "
                        + geneToDmPathClause
                        + " AND dmp.mpTerm IN mps";

            }
            else {
                query = geneToMpPath
                        //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') "
                        + whereClause1
                        + " AND " + significant + phenotypeSexes + parameter + chrRange + geneList + genotypes + alleleTypes
                        + " WITH g, collect({genes:g, alleles:a, srs:sr, mps:mp}) as list1 "

                        + matchClause2
                        //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*') "
                        + whereClause2
                        + " AND " + significant + phenotypeSexes + parameter + chrRange + geneList + genotypes + alleleTypes
                        + " WITH g, list1, collect({genes:g, alleles:a, srs:sr, mps:mp}) as list2 "
                        + " WHERE ALL (x IN list1 WHERE x IN list2) "
                        + " WITH g, list1+list2 as list "
                        + " UNWIND list as nodes "

                        + " WITH g, nodes, extract(x in collect(distinct nodes.mps) | x.mpTerm) as mps "
                        + geneToDmPathClause
                        + " AND dmp.mpTerm IN mps";
            }

            dts = new ArrayList<>();
            for (int d = 0; d < dataTypes.size(); d++){
                String dt = dataTypes.get(d).toString();
                dts.add(dtypeMap.containsKey(dt) ? dt : dtypeMap.get(dt));
            }
            returnDtypes = StringUtils.join(dts, ", ");

            query += fileType != null ?
                    //" RETURN distinct nodes.alleles, g, nodes.srs, collect(distinct nodes.mps), collect(distinct dm)" + sortStr
                    //" RETURN distinct nodes.alleles, g, nodes.srs, nodes.mps, dm" + sortStr :
                    " RETURN distinct " + returnDtypes + sortStr :
                    " RETURN collect(distinct nodes.alleles), collect(distinct g), collect(distinct nodes.srs), collect(distinct nodes.mps), collect(distinct dm)";

            System.out.println("Query: "+ query);
            result =  neo4jSession.query(query, params);
        }
        else if (mpStr.matches(regex_aOrb_andc)) {
            System.out.println("matches (a or b) and c");

            Pattern pattern = Pattern.compile(regex_aOrb_andc);
            Matcher matcher = pattern.matcher(mpStr);

            while (matcher.find()) {
                System.out.println("found: " + matcher.group(0));
                String mpA = matcher.group(1).trim();
                String mpB = matcher.group(3).trim();;
                String mpC = matcher.group(5).trim();;
                logger.info("A: '{}', B: '{}', C: '{}'", mpA, mpB, mpC);

                params.put("mpA", mapNarrowSynonym2MpTerm(narrowMapping, mpA, autosuggestCore));
                params.put("mpB", mapNarrowSynonym2MpTerm(narrowMapping, mpB, autosuggestCore));
                params.put("mpC", mapNarrowSynonym2MpTerm(narrowMapping, mpC, autosuggestCore));

                pvaluesA = composePvalues(params.get("mpA").toString(), jParams);
                pvaluesB = composePvalues(params.get("mpB").toString(), jParams);
                pvaluesC = composePvalues(params.get("mpC").toString(), jParams);
            }

            String whereClause1 = noMpChild ? " WHERE ((mp.mpTerm = '" + params.get("mpA") + "'" + pvaluesA + ") OR (mp.mpTerm ='" + params.get("mpB") + "'" + pvaluesB + ")) "
                    : " WHERE ((mp0.mpTerm = '" + params.get("mpA") + "'" + pvaluesA + ") OR (mp0.mpTerm ='" + params.get("mpB") + "'" + pvaluesB + ")) ";

            String whereClause2 = noMpChild ? " WHERE mp.mpTerm = '" + params.get("mpC") + "'" + pvaluesC : " WHERE mp0.mpTerm = '" + params.get("mpC") + "'" + pvaluesC;

            String matchClause1 = noMpChild ? " MATCH (mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g) "
                    : " MATCH (mp0:Mp)<-[:PARENT*0..]-(mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g) ";

            String matchClause2 = noMpChild ? " MATCH (g)<-[:GENE]-(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp) "
                    : " MATCH (g)<-[:GENE]-(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp)-[:PARENT*0..]->(mp0:Mp) ";

            if (geneList.isEmpty()){
                query = mpToGenePath
                        //+ "WHERE (mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') OR mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*')) "
                        + whereClause1
                        + " AND " + significant + phenotypeSexes + parameter + chrRange + geneList + genotypes + alleleTypes
                        + " WITH g, collect({alleles:a, srs:sr, mps:mp}) as list1 "

                        + matchClause1
                        //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpC}+'.*') "
                        + whereClause2
                        + " AND " + significant + phenotypeSexes + parameter + chrRange + geneList + genotypes + alleleTypes
                        + " WITH g, list1, collect({alleles:a, srs:sr, mps:mp}) as list2 "
                        + " WHERE ALL (x IN list1 WHERE x IN list2) "
                        + " WITH g, list1+list2 as list "
                        + " UNWIND list as nodes "
                        + " WITH g, nodes, extract(x in collect(distinct nodes.mps) | x.mpTerm) as mps "
                        + geneToDmPathClause
                        + " AND dmp.mpTerm IN mps";
            }
            else {
                query = geneToMpPath
                        //+ "WHERE (mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') OR mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*')) "
                        + whereClause1
                        + " AND " + significant + phenotypeSexes + parameter + chrRange + geneList + genotypes + alleleTypes
                        + " WITH g, collect({alleles:a, srs:sr, mps:mp}) as list1 "

                        + matchClause2
                        //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpC}+'.*') "
                        + whereClause2
                        + " AND " + significant + phenotypeSexes + parameter + chrRange + geneList + genotypes + alleleTypes
                        + " WITH g, list1, collect({alleles:a, srs:sr, mps:mp}) as list2 "
                        + " WHERE ALL (x IN list1 WHERE x IN list2) "
                        + " WITH g, list1+list2 as list "
                        + " UNWIND list as nodes "
                        + " WITH g, nodes, extract(x in collect(distinct nodes.mps) | x.mpTerm) as mps "
                        + geneToDmPathClause
                        + " AND dmp.mpTerm IN mps";
            }

            dts = new ArrayList<>();
            for (int d = 0; d < dataTypes.size(); d++){
                String dt = dataTypes.get(d).toString();
                dts.add(dtypeMap.containsKey(dt) ? dt : dtypeMap.get(dt));
            }
            returnDtypes = StringUtils.join(dts, ", ");

            query += fileType != null ?
                    //" RETURN distinct nodes.alleles, g, nodes.srs, collect(distinct nodes.mps), collect(distinct dm)" + sortStr :
                    //" RETURN distinct nodes.alleles, g, nodes.srs, nodes.mps, dm" + sortStr :
                    " RETURN distinct " + returnDtypes + sortStr :
                    " RETURN collect(distinct nodes.alleles), collect(distinct g), collect(distinct nodes.srs), collect(distinct nodes.mps), collect(distinct dm)";

            System.out.println("Query: "+ query);
            result =  neo4jSession.query(query, params);
        }
        else if (mpStr.matches(regex_aAnd_bOrc)) {
            System.out.println("matches a and (b or c)");

            Pattern pattern = Pattern.compile(regex_aAnd_bOrc);
            Matcher matcher = pattern.matcher(mpStr);

            while (matcher.find()) {
                System.out.println("found: " + matcher.group(0));
                String mpA = matcher.group(1).trim();
                String mpB = matcher.group(3).trim();;
                String mpC = matcher.group(5).trim();;
                //logger.info("A: '{}', B: '{}', C: '{}'", mpA, mpB, mpC);

                params.put("mpA", mapNarrowSynonym2MpTerm(narrowMapping, mpA, autosuggestCore));
                params.put("mpB", mapNarrowSynonym2MpTerm(narrowMapping, mpB, autosuggestCore));
                params.put("mpC", mapNarrowSynonym2MpTerm(narrowMapping, mpC, autosuggestCore));

                pvaluesA = composePvalues(params.get("mpA").toString(), jParams);
                pvaluesB = composePvalues(params.get("mpB").toString(), jParams);
                pvaluesC = composePvalues(params.get("mpC").toString(), jParams);
            }

            String whereClause1 = noMpChild ? " WHERE ((mp.mpTerm = '" + params.get("mpB") + "'" + pvaluesB + ") OR (mp.mpTerm ='" + params.get("mpC") + "'" + pvaluesC + ")) "
                    : " WHERE ((mp0.mpTerm = '" + params.get("mpB") + "'" + pvaluesB + ") OR (mp0.mpTerm ='" + params.get("mpC") + "'" + pvaluesC + ")) ";

            String whereClause2 = noMpChild ? " WHERE mp.mpTerm = '" + params.get("mpA") + "'" + pvaluesA : " WHERE mp0.mpTerm = '" + params.get("mpA") + "'" + pvaluesA;

            String matchClause1 = noMpChild ? " MATCH (mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g) "
                    : " MATCH (mp0:Mp)<-[:PARENT*0..]-(mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g) ";

            String matchClause2 = noMpChild ? " MATCH (g)<-[:GENE]-(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp) "
                    : " MATCH (g)<-[:GENE]-(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp)-[:PARENT*0..]->(mp0:Mp) ";

            if (geneList.isEmpty()){
                query = mpToGenePath
                        //+ "WHERE (mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*') OR mp0.mpTerm =~ ('(?i)'+'.*'+{mpC}+'.*')) "
                        + whereClause1
                        + " AND " + significant + phenotypeSexes + parameter + chrRange + geneList + genotypes + alleleTypes
                        + " WITH g, collect({alleles:a, srs:sr, mps:mp}) as list1 "

                        + matchClause1
                        //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') "
                        + whereClause2
                        + " AND " + significant + phenotypeSexes + parameter + chrRange + geneList + genotypes + alleleTypes
                        + " WITH g, list1, collect({alleles:a, srs:sr, mps:mp}) as list2 "
                        + " WHERE ALL (x IN list1 WHERE x IN list2) "
                        + " WITH g, list1+list2 as list "
                        + " UNWIND list as nodes "
                        + " WITH g, nodes, extract(x in collect(distinct nodes.mps) | x.mpTerm) as mps "
                        + geneToDmPathClause
                        + " AND dmp.mpTerm IN mps";
            }
            else {
                query = geneToMpPath
                        //+ "WHERE (mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*') OR mp0.mpTerm =~ ('(?i)'+'.*'+{mpC}+'.*')) "
                        + whereClause1
                        + " AND " + significant + phenotypeSexes + parameter + chrRange + geneList + genotypes + alleleTypes
                        + " WITH g, collect({alleles:a, srs:sr, mps:mp}) as list1 "

                        + matchClause2
                        //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') "
                        + whereClause2
                        + " AND " + significant + phenotypeSexes + parameter + chrRange + geneList + genotypes + alleleTypes
                        + " WITH g, list1, collect({alleles:a, srs:sr, mps:mp}) as list2 "
                        + " WHERE ALL (x IN list1 WHERE x IN list2) "
                        + " WITH g, list1+list2 as list "
                        + " UNWIND list as nodes "
                        + " WITH g, nodes, extract(x in collect(distinct nodes.mps) | x.mpTerm) as mps "
                        + geneToDmPathClause
                        + " AND dmp.mpTerm IN mps";
            }

            dts = new ArrayList<>();
            for (int d = 0; d < dataTypes.size(); d++){
                String dt = dataTypes.get(d).toString();
                dts.add(dtypeMap.containsKey(dt) ? dt : dtypeMap.get(dt));
            }
            returnDtypes = StringUtils.join(dts, ", ");

            query += fileType != null ?
                    //" RETURN distinct nodes.alleles, g, nodes.srs, collect(distinct nodes.mps), collect(distinct dm)" + sortStr
                    //" RETURN distinct nodes.alleles, g, nodes.srs, nodes.mps, dm" + sortStr :
                    " RETURN distinct " + returnDtypes + sortStr :
                    " RETURN collect(distinct nodes.alleles), collect(distinct g), collect(distinct nodes.srs), collect(distinct nodes.mps), collect(distinct dm)";


            System.out.println("Query: "+ query);
            result =  neo4jSession.query(query, params);
        }

        else if (mpStr.matches(regex_aAndbAndc)) {
            System.out.println("matches a and b and c");

            Pattern pattern = Pattern.compile(regex_aAndbAndc);
            Matcher matcher = pattern.matcher(mpStr);;

            while (matcher.find()) {
                System.out.println("found: " + matcher.group(0));
                String mpA = matcher.group(1).trim();
                String mpB = matcher.group(3).trim();
                String mpC = matcher.group(5).trim();
                logger.info("A: '{}', B: '{}', C: '{}'", mpA, mpB, mpC);

                params.put("mpA", mapNarrowSynonym2MpTerm(narrowMapping, mpA, autosuggestCore));
                params.put("mpB", mapNarrowSynonym2MpTerm(narrowMapping, mpB, autosuggestCore));
                params.put("mpC", mapNarrowSynonym2MpTerm(narrowMapping, mpC, autosuggestCore));

                pvaluesA = composePvalues(params.get("mpA").toString(), jParams);
                pvaluesB = composePvalues(params.get("mpB").toString(), jParams);
                pvaluesC = composePvalues(params.get("mpC").toString(), jParams);
            }

            String whereClause1 = noMpChild ? " WHERE mp.mpTerm = '" + params.get("mpA") + "'" + pvaluesA : " WHERE mp0.mpTerm = '" + params.get("mpA") + "'" + pvaluesA;
            String whereClause2 = noMpChild ? " WHERE mp.mpTerm = '" + params.get("mpB") + "'" + pvaluesB : " WHERE mp0.mpTerm = '" + params.get("mpB") + "'" + pvaluesB;
            String whereClause3 = noMpChild ? " WHERE mp.mpTerm = '" + params.get("mpC") + "'" + pvaluesC : " WHERE mp0.mpTerm = '" + params.get("mpC") + "'" + pvaluesC;

            String mpMatchClause = noMpChild ? " MATCH (mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g) "
                    : " MATCH (mp0:Mp)<-[:PARENT*0..]-(mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g) ";

            query = mpToGenePath
                    //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') "
                    + whereClause1
                    + " AND " + significant + phenotypeSexes + parameter + chrRange + geneList + genotypes + alleleTypes
                    + " WITH g, collect({alleles:a, mps:mp, srs:sr}) as list1 "

                    + mpMatchClause
                    //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*') "
                    + whereClause2
                    + " AND " + significant + phenotypeSexes + parameter + chrRange + geneList + genotypes + alleleTypes
                    + " WITH g, list1, collect({alleles:a, mps:mp, srs:sr}) as list2 "

                    + mpMatchClause
                    //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpC}+'.*') "
                    + whereClause3
                    + " AND " + significant + phenotypeSexes + parameter + chrRange + geneList + genotypes + alleleTypes
                    + " WITH g, list1, list2, collect({alleles:a, mps:mp, srs:sr}) as list3 "
                    + " WHERE ALL (x IN list1 WHERE x IN list2) AND ALL (x IN list2 WHERE x IN list3) "
                    + " WITH g, list1+list2+list3 as list "
                    + " UNWIND list as nodes "
                    + " WITH g, nodes, extract(x in collect(distinct nodes.mps) | x.mpTerm) as mps "
                    + geneToDmPathClause
                    + " AND dmp.mpTerm IN mps";

            dts = new ArrayList<>();
            for (int d = 0; d < dataTypes.size(); d++){
                String dt = dataTypes.get(d).toString();
                dts.add(dtypeMap.containsKey(dt) ? dt : dtypeMap.get(dt));
            }
            returnDtypes = StringUtils.join(dts, ", ");

            query += fileType != null ?
                    //" RETURN distinct nodes.alleles, g, nodes.srs, collect(distinct nodes.mps), collect(distinct dm)" + sortStr
                    //" RETURN distinct nodes.alleles, g, nodes.srs, nodes.mps, dm" + sortStr :
                    " RETURN distinct " + returnDtypes + sortStr :
                    " RETURN collect(distinct nodes.alleles), collect(distinct g), collect(distinct nodes.srs), collect(distinct nodes.mps), collect(distinct dm)";

            System.out.println("Query: "+ query);
            result =  neo4jSession.query(query, params);
        }
        else if (mpStr.matches(regex_aAndb)) {
            System.out.println("matches a and b");

            Pattern pattern = Pattern.compile(regex_aAndb);
            Matcher matcher = pattern.matcher(mpStr);

            while (matcher.find()) {
                String mpA = matcher.group(1).trim();;
                String mpB = matcher.group(3).trim();;
                logger.info("A: '{}', B: '{}'", mpA, mpB);

                params.put("mpA", mapNarrowSynonym2MpTerm(narrowMapping, mpA, autosuggestCore));
                params.put("mpB", mapNarrowSynonym2MpTerm(narrowMapping, mpB, autosuggestCore));

                pvaluesA = composePvalues(params.get("mpA").toString(), jParams);
                pvaluesB = composePvalues(params.get("mpB").toString(), jParams);
            }

            String whereClause1 = noMpChild ? " WHERE mp.mpTerm = '" + params.get("mpA") + "'" + pvaluesA : " WHERE mp0.mpTerm = '" + params.get("mpA") + "'" + pvaluesA;
            String whereClause2 = noMpChild ? " WHERE mp.mpTerm = '" + params.get("mpB") + "'" + pvaluesB : " WHERE mp0.mpTerm = '" + params.get("mpB") + "'" + pvaluesB;

            String mpMatchClause = noMpChild ? " MATCH (mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g) "
                    : " MATCH (mp0:Mp)<-[:PARENT*0..]-(mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g) ";

            query = mpToGenePath
                  //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') "
                  + whereClause1
                  + " AND " + significant + phenotypeSexes + parameter + chrRange + geneList + genotypes + alleleTypes
                  + " WITH g, collect({alleles:a, mps:mp, srs:sr}) as list1 "

                  + mpMatchClause
                  //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*') "
                  + whereClause2
                  + " AND " + significant + phenotypeSexes + parameter + chrRange + geneList + genotypes + alleleTypes
                  + " WITH g, list1, collect({alleles:a, mps:mp, srs:sr}) as list2 "
                  + " WHERE ALL (x IN list1 WHERE x IN list2) "
                  + " WITH g, list1+list2 as list "
                  + " UNWIND list as nodes "
                  + " WITH g, nodes, extract(x in collect(distinct nodes.mps) | x.mpTerm) as mps "
                  + geneToDmPathClause
                  + " AND dmp.mpTerm IN mps";

            //query += fileType != null ? " RETURN distinct nodes.alleles, g, nodes.srs, collect(distinct nodes.mps), collect(distinct dm)" + sortStr
              //      : " RETURN collect(distinct nodes.alleles), collect(distinct g), collect(distinct nodes.srs), collect(distinct nodes.mps), collect(distinct dm)";

            dts = new ArrayList<>();
            for (int d = 0; d < dataTypes.size(); d++){
                String dt = dataTypes.get(d).toString();
                dts.add(dtypeMap.containsKey(dt) ? dt : dtypeMap.get(dt));
            }
            returnDtypes = StringUtils.join(dts, ", ");

            query += fileType != null ?
                    //" RETURN distinct nodes.alleles, g, nodes.srs, collect(distinct nodes.mps), collect(distinct dm)" + sortStr
                    //" RETURN distinct nodes.alleles, g, nodes.srs, nodes.mps, dm" + sortStr :
                    " RETURN distinct " + returnDtypes + sortStr :
                    " RETURN collect(distinct nodes.alleles), collect(distinct g), collect(distinct nodes.srs), collect(distinct nodes.mps), collect(distinct dm)";

            System.out.println("Query: "+ query);
            result =  neo4jSession.query(query, params);
        }
        else if (mpStr.matches(regex_aOrbOrc)) {
            System.out.println("matches a or b or c");

            Pattern pattern = Pattern.compile(regex_aOrbOrc);
            Matcher matcher = pattern.matcher(mpStr);
            while (matcher.find()) {
                System.out.println("found: " + matcher.group(0));
                String mpA = matcher.group(1).trim();
                String mpB = matcher.group(3).trim();
                String mpC = matcher.group(5).trim();
                logger.info("A: '{}', B: '{}', C: '{}'", mpA, mpB, mpC);

                params.put("mpA", mapNarrowSynonym2MpTerm(narrowMapping, mpA, autosuggestCore));
                params.put("mpB", mapNarrowSynonym2MpTerm(narrowMapping, mpB, autosuggestCore));
                params.put("mpC", mapNarrowSynonym2MpTerm(narrowMapping, mpC, autosuggestCore));

                pvaluesA = composePvalues(params.get("mpA").toString(), jParams);
                pvaluesB = composePvalues(params.get("mpB").toString(), jParams);
                pvaluesC = composePvalues(params.get("mpC").toString(), jParams);
            }

            if (geneList.isEmpty()){
                query = mpToGenePath;
            }
            else {
                query = geneToMpPath;
            }

            String whereClause = noMpChild ? " WHERE ((mp.mpTerm = '" + params.get("mpA") + "'" + pvaluesA + ") OR (mp.mpTerm ='" + params.get("mpB") + "'" + pvaluesB + ") OR (mp.mpTerm = '" + params.get("mpC") + "'" + pvaluesC + ") ) "
                    : " WHERE ((mp0.mpTerm = '" + params.get("mpA") + "'" + pvaluesA + ") OR (mp0.mpTerm ='" + params.get("mpB") + "'" + pvaluesB + ") OR (mp.mpTerm = '" + params.get("mpC") + "'" + pvaluesC + ")) ";

            System.out.println("A:  "+ pvaluesA);
            System.out.println("B:  "+ pvaluesB);
            System.out.println("whereClause: " + whereClause);

            // using regular expression to match mp term name drastically lowers the performance, use exact match instead
            query +=
                  //  " WHERE (mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') OR mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*') OR mp0.mpTerm =~ ('(?i)'+'.*'+{mpC}+'.*')) "
                  whereClause
                + " AND " + significant + phenotypeSexes + parameter + chrRange + geneList + genotypes + alleleTypes
                + " WITH g, a, sr, mp, extract(x in collect(distinct mp) | x.mpTerm) as mps "
                + geneToDmPathClause
                + " AND dmp.mpTerm IN mps";

            query += fileType != null ?
                    //" RETURN distinct a, g, sr, collect(distinct mp), collect(distinct dm)" + sortStr :
                    " RETURN distinct " + returnDtypes + sortStr :
                    " RETURN collect(distinct a), collect(distinct g), collect(distinct sr), collect(distinct mp), collect(distinct dm)";

            System.out.println("Query: "+ query);
            result =  neo4jSession.query(query, params);

        }
        else if (mpStr.matches(regex_aOrb)) {
            System.out.println("matches a or b");

            Pattern pattern = Pattern.compile(regex_aOrb);
            Matcher matcher = pattern.matcher(mpStr);

            while (matcher.find()) {
                String mpA = matcher.group(1).trim();
                String mpB = matcher.group(3).trim();
                logger.info("A: '{}', B: '{}'", mpA, mpB);

                params.put("mpA", mapNarrowSynonym2MpTerm(narrowMapping, mpA, autosuggestCore));
                params.put("mpB", mapNarrowSynonym2MpTerm(narrowMapping, mpB, autosuggestCore));

                pvaluesA = composePvalues(params.get("mpA").toString(), jParams);
                pvaluesB = composePvalues(params.get("mpB").toString(), jParams);
            }

            if (geneList.isEmpty()){
                query = mpToGenePath;
            }
            else {
                query = geneToMpPath;
            }

            String whereClause = noMpChild ? " WHERE ((mp.mpTerm = '" + params.get("mpA") + "'" + pvaluesA + ") OR (mp.mpTerm ='" + params.get("mpB") + "'" + pvaluesB + ")) "
                    : " WHERE ((mp0.mpTerm = '" + params.get("mpA") + "'" + pvaluesA + ") OR (mp0.mpTerm ='" + params.get("mpB") + "'" + pvaluesB + "))";
            query +=
                  //  " WHERE (mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') OR mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*')) "
                  whereClause
                + " AND " + significant + phenotypeSexes + parameter + chrRange + geneList + genotypes + alleleTypes
                + " WITH g, a, sr, mp, extract(x in collect(distinct mp) | x.mpTerm) as mps "
                + geneToDmPathClause
                + " AND dmp.mpTerm IN mps";

            query += fileType != null ?
                    //" RETURN distinct a, g, sr, collect(distinct mp), collect(distinct dm)" + sortStr :
                    " RETURN distinct " + returnDtypes + sortStr :
                    " RETURN collect(distinct a), collect(distinct g), collect(distinct sr), collect(distinct mp), collect(distinct dm)";

            System.out.println("Query: "+ query);
            result =  neo4jSession.query(query, params);
        }

        long end = System.currentTimeMillis();
        System.out.println("Done with query in " + (end - begin) + " ms");

        int rowCount = 0;
        JSONObject j = new JSONObject();
        j.put("aaData", new Object[0]);
        j.put("iDisplayStart", 0);
        j.put("iDisplayLength", 10);
        j.put("narrowMapping", StringUtils.join(narrowMapping, ", "));

        List<String> rowDataExport = new ArrayList<>(); // for export
        List<String> rowDataOverview = new ArrayList<>(); // for overview
        List<String> dtypes = Arrays.asList("Allele", "Gene", "Mp", "DiseaseModel", "StatisticalResult");

        long tstart = System.currentTimeMillis();
        if (fileType != null){

            List<String> cols = new ArrayList<>();
            Map<String, List<String>> node2Properties = new LinkedHashMap<>();

            for (String dtype : dtypes){

                node2Properties.put(dtype, new ArrayList<String>());

                if (jParams.containsKey(dtype)) {
                    for (Object obj : jParams.getJSONArray(dtype)) {
                        String colName = obj.toString();

                        //System.out.println("colname: " + colName);
                        if (colName.equals("alleleSymbol") && !jParams.getJSONArray(dtype).contains("alleleMgiAccessionId")) {
                            cols.add(colName);
                            cols.add("alleleMgiAccessionId");
                            node2Properties.get(dtype).add(colName);
                            node2Properties.get(dtype).add("alleleMgiAccessionId");
                        } else if (colName.equals("markerSymbol") && !jParams.getJSONArray(dtype).contains("mgiAccessionId")) {
                            cols.add(colName);
                            cols.add("mgiAccessionId");
                            node2Properties.get(dtype).add(colName);
                            node2Properties.get(dtype).add("mgiAccessionId");
                        } else if (colName.equals("mpTerm") && !jParams.getJSONArray(dtype).contains("mpId")) {
                            cols.add(colName);
                            cols.add("mpId");
                            node2Properties.get(dtype).add(colName);
                            node2Properties.get(dtype).add("mpId");
                        } else if (colName.equals("diseaseTerm") && !jParams.getJSONArray(dtype).contains("diseaseId")) {
                            cols.add(colName);
                            cols.add("diseaseId");
                            node2Properties.get(dtype).add(colName);
                            node2Properties.get(dtype).add("diseaseId");
                        } else {
                            cols.add(colName);
                            node2Properties.get(dtype).add(colName);
                        }
                    }
                }
            }

            rowDataExport.add(StringUtils.join(cols, "\t")); // column

            for (Map<String,Object> row : result) {
                //System.out.println(row.toString());
                //System.out.println("cols: " + row.size());

                List<String> data = new ArrayList<>(); // for export

                Map<String, Set<String>> colValMap = new TreeMap();

                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    //System.out.println(entry.getKey() + " / " + entry.getValue());

                    if (entry.getValue() != null) {

                        if (entry.getKey().startsWith("collect")) {
                            List<Object> objs = (List<Object>) entry.getValue();
                            for (Object obj : objs) {
                                populateColValMapAdvSrch(node2Properties, obj, colValMap, jParams, fileType);
                            }
                        } else {
                            Object obj = entry.getValue();
                            populateColValMapAdvSrch(node2Properties, obj, colValMap, jParams, fileType);
                        }
                    }
                }
                //-------- start of export
                //System.out.println("colValMap: " + colValMap.toString());

                //System.out.println("cols: " + cols);
                if (colValMap.size() > 0) {
                    for (String col : cols) {
                        //System.out.println("col now-1: " + col);
                        if (colValMap.containsKey(col)) {
                            //System.out.println("col now-2: " + col);
                            List<String> vals = new ArrayList<>(colValMap.get(col));
                            //System.out.println("vals: "+ vals);

                            if (fileType.equals("html")){
                                data.add("<td>" + StringUtils.join(vals, "|") + "</td>");
                            }
                            else {
                                data.add(StringUtils.join(vals, "|"));
                            }

                        }
                    }
                    //System.out.println("row: " + data);
                }

                if (fileType.equals("html")){
                    rowDataExport.add("<tr>" + StringUtils.join(data, "") + "<tr>");
                }
                else {
                    rowDataExport.add(StringUtils.join(data, "\t"));
                }
            }
            j.put("rows", rowDataExport);
        }
        else {

            // overview

            List<String> cols = new ArrayList<>();
            Map<String, List<String>> node2Properties = new LinkedHashMap<>();

            for (String dtype : dtypes) {

                node2Properties.put(dtype, new ArrayList<String>());

                if (jParams.containsKey(dtype)) {
                    for (Object obj : jParams.getJSONArray(dtype)) {
                        String colName = obj.toString();
                        cols.add(colName);
                        node2Properties.get(dtype).add(colName);
                    }
                }
            }

            //System.out.println("columns: " + cols);
            Map<String, Set<String>> colValMap = new TreeMap<>(); // for export

            for (Map<String, Object> row : result) {
                //System.out.println(row.toString());
                //System.out.println("cols: " + row.size());

                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    //System.out.println(entry.getKey() + " / " + entry.getValue());

                    if (entry.getValue() != null && ! entry.getValue().toString().startsWith("[Ljava.lang.Object")) {
                        if (entry.getKey().startsWith("collect")) {
                            List<Object> objs = (List<Object>) entry.getValue();
                            for (Object obj : objs) {
                                populateColValMapAdvSrch(node2Properties, obj, colValMap, jParams, fileType);
                            }
                        } else {
                            Object obj = entry.getValue();
                            populateColValMapAdvSrch(node2Properties, obj, colValMap, jParams, fileType);
                        }
                    }
                }
            }

            //System.out.println("keys: "+ colValMap.keySet());
            // for overview

            for (String col : cols){
                if (colValMap.containsKey(col)) {
                    List<String> vals = new ArrayList<>(colValMap.get(col));

                    int valSize = vals.size();

                    if (valSize > 2) {
                        // add showmore
                        vals.add("<button rel=" + valSize + " class='showMore'>show all (" + valSize + ")</button>");
                    }
                    if (valSize == 1) {
                        rowDataOverview.add(StringUtils.join(vals, ""));
                    } else {
                        rowDataOverview.add("<ul>" + StringUtils.join(vals, "") + "</ul>");
                    }

                    //System.out.println("col: " + col);
                    if (col.equals("ontoSynonym")) {
                        System.out.println(col + " -- " + vals);
                    }
                }
                else {
                    rowDataOverview.add(NA);
                }
            }

            System.out.println("rows done");

            j.put("iTotalRecords", rowCount);
            j.put("iTotalDisplayRecords", rowCount);
            j.getJSONArray("aaData").add(rowDataOverview);

            //System.out.println(j.toString());
        }
        long tend = System.currentTimeMillis();

        System.out.println((tend - tstart) + " ms taken");
        return j;
    }

    private String mapNarrowSynonym2MpTerm(List<String> narrowMapping, String mpTerm, SolrClient autosuggestCore) throws IOException, SolrServerException {

        String mpStr = null;
        SolrQuery query = new SolrQuery();
        query.setQuery("\"" + mpTerm + "\"");
        query.set("qf", "auto_suggest");
        query.set("defType", "edismax");
        query.setStart(0);
        query.setRows(100);
        query.setFilterQueries("docType:mp");

        QueryResponse response = autosuggestCore.query(query);
        System.out.println("response: " + response);
        SolrDocumentList results = response.getResults();

        for(SolrDocument doc : results){
            if (doc.containsKey("mp_term") && doc.getFieldValue("mp_term").equals(mpTerm)){
                System.out.println(doc.getFieldValue("mp_term"));
                mpStr = mpTerm;
                break;
            }
            else if (doc.containsKey("mp_narrow_synonym") && doc.getFieldValue("mp_narrow_synonym").equals(mpTerm)){
                System.out.println("NS: "+ doc.getFieldValue("mp_narrow_synonym"));
                mpStr = doc.getFieldValue("mp_term").toString();
                narrowMapping.add(mpTerm + " is not directly annotated in IMPC and is a child term of " + mpStr);
                break;
            }
        }

        return mpStr;
    }
    
    private String composeParameter(JSONObject jParams){
        String parameter = "";
        if (jParams.containsKey("srchPipeline")) {
            String name = jParams.getString("srchPipeline");
            parameter = " AND sr.parameterName ='" + name + "' ";
        }
        return parameter;
    }

    private String composePvalues(JSONObject jParams){
        String pvalues = "";

        if (jParams.containsKey("lowerPvalue")) {
            double lowerPvalue = jParams.getDouble("lowerPvalue");
            pvalues = " AND sr.pvalue > " + lowerPvalue + " ";
        }
        if (jParams.containsKey("upperPvalue")) {
            double upperPvalue = jParams.getDouble("upperPvalue");
            pvalues += " AND sr.pvalue < " + upperPvalue + " ";
        }

        return pvalues;
    }

    private String composePvalues(String mpTerm, JSONObject jParams) {
        String pvalues = "";

        List<String> pvals = new ArrayList<>();
        if (jParams.containsKey("pvaluesMap")) {
            JSONObject map = jParams.getJSONObject("pvaluesMap").getJSONObject(mpTerm);
            System.out.println("MAP: " + map.toString());
            if (map.containsKey("lowerPvalue")){
                double lowerPvalue = map.getDouble("lowerPvalue");
                pvals.add("sr.pvalue > " + lowerPvalue);
            }
            if (map.containsKey("upperPvalue")){
                double upperPvalue = map.getDouble("upperPvalue");
                pvals.add("sr.pvalue < " + upperPvalue);
            }
        }

        if (pvals.size() > 0){
            pvalues = " AND (" +  StringUtils.join(pvals, " AND ") + ")";
        }
        System.out.println("----- pvalue: " + pvalues);
        return pvalues;
    }

    private String composeSignificance(JSONObject jParams) {
        String significantPvalue = "";

        if (jParams.containsKey("onlySignificantPvalue")) {
            significantPvalue = jParams.getBoolean("onlySignificantPvalue") == true ? " sr.significant = true " : " exists(sr.significant) ";
        }
        return significantPvalue;
    }

    private String composePhenotypeSexStr(JSONObject jParams){

        String phenotypeSex = "";
        if (jParams.containsKey("phenotypeSex")) {
            String sex = jParams.getString("phenotypeSex");

            if (sex.equals("female")){
                phenotypeSex = " AND none (tag IN sr.phenotypeSex WHERE tag IN ['male','both']) ";
            }
            else if (sex.equals("male")){
                phenotypeSex = " AND none (tag IN sr.phenotypeSex WHERE tag IN ['female','both']) ";
            }
            else if (sex.equals("both")) {
                phenotypeSex = " AND ('both' IN sr.phenotypeSex) ";
            }
        }
        return phenotypeSex;
    }

    private String composeChrRangeStr(JSONObject jParams){
        String chrRange = "";

        if (jParams.containsKey("chrRange")) {
            String range = jParams.getString("chrRange");
            if (range.matches("^chr(\\w*,?\\w*):(\\d+)-(\\d+)$")) {
                System.out.println("find chr range");

                Pattern pattern = Pattern.compile("^chr(\\w*,?\\w*):(\\d+)-(\\d+)$");
                Matcher matcher = pattern.matcher(range);
                while (matcher.find()) {
                    System.out.println("found: " + matcher.group(1));
                    String regionId = matcher.group(1);
                    int regionStart = Integer.parseInt(matcher.group(2));
                    int regionEnd = Integer.parseInt(matcher.group(3));


                    String[] ids = org.apache.commons.lang3.StringUtils.split(regionId, ",");
                    List<String> chrs = new ArrayList<>();
                    for (int i=0; i<ids.length; i++){
                        chrs.add("'" + ids[i] + "'");
                    }
                    regionId = org.apache.commons.lang3.StringUtils.join(chrs, ",");

                    chrRange = " AND g.chrId IN [" + regionId + "] AND g.chrStart >= " + regionStart + " AND g.chrEnd <= " + regionEnd + " ";
                }
            }
        }
        else if (jParams.containsKey("chr")) {
                chrRange = " AND g.chrId IN " + jParams.getString("chr") + " ";
        }

        return chrRange;
    }

    private String composeGeneListStr(JSONObject jParams) {
        String genelist = "";

        if (jParams.containsKey("mouseGeneList")) {
           // genelist = "AND g.markerSymbol in [" +
            List<String> list = new ArrayList<>();

            for (Object name : jParams.getJSONArray("mouseGeneList")){
               list.add("'" + name.toString() + "'");
           }

           String glist = StringUtils.join(list, ",");
           boolean isSym = glist.contains("MGI:") ? false : true; // best estimation, assuming users won't mix id and symbol in the search. If they do? well, they ask for it!
           if (isSym) {
               genelist = " AND g.markerSymbol in [" + glist + "] ";
           }
           else {
               genelist = " AND g.mgiAccessionId in [" + glist + "] ";
           }
        }
        else if (jParams.containsKey("humanGeneList")) {
            List<String> list = new ArrayList<>();

            for (Object name : jParams.getJSONArray("humanGeneList")){
                list.add("'" + name.toString() + "'");
            }

            if (list.size() > 0) {
                genelist = " AND ANY (hs in g.humanGeneSymbol WHERE hs IN [" + StringUtils.join(list, ", ") + "]) ";
            }
        }

        return genelist;
    }

    private String composeGenotypesStr(JSONObject jParams) {
        String genotypes = "";

        if (jParams.containsKey("genotypes")) {
            // genelist = "AND g.markerSymbol in [" +
            List<String> list = new ArrayList<>();
            for (Object name : jParams.getJSONArray("genotypes")) {
                list.add("'" + name.toString() + "'");
            }
            if (list.size() > 0) {
                genotypes = " AND sr.zygosity IN [" + StringUtils.join(list, ",") + "]";
            }
        }
        return genotypes;
    }

    private String composeAlleleTypesStr(JSONObject jParams){
        String alleleTypes = "";

        Map<String, String> alleleTypeMapping = new HashMap<>();
        alleleTypeMapping.put("CRISPR(em)", "Endonuclease-mediated"); // mutation_type
        alleleTypeMapping.put("KOMP", "");  // empty allele_type
        alleleTypeMapping.put("KOMP.1", ".1");
        alleleTypeMapping.put("EUCOMM A", "a");
        alleleTypeMapping.put("EUCOMM B", "b");
        alleleTypeMapping.put("EUCOMM C", "c");
        alleleTypeMapping.put("EUCOMM D", "d");
        alleleTypeMapping.put("EUCOMM E", "e");

        List<String> mutationTypes = new ArrayList<>();

        if (jParams.containsKey("alleleTypes")) {
            // genelist = "AND g.markerSymbol in [" +
            List<String> list = new ArrayList<>();
            for (Object name : jParams.getJSONArray("alleleTypes")) {
                String atype = name.toString();

                if (atype.equals("CRISPR(em)")){
                    mutationTypes.add("'" + alleleTypeMapping.get(atype) + "'");
                }
                else {
                    list.add("'" + alleleTypeMapping.get(atype) + "'");
                }
            }

            if (list.size() > 0){
                alleleTypes = "a.alleleType IN [" + StringUtils.join(list, ",") + "]";
                if (mutationTypes.size() > 0) {
                    alleleTypes += " OR a.mutationType IN [" + StringUtils.join(mutationTypes, ",") + "]";
                }
                alleleTypes = " AND (" + alleleTypes + ") ";
            }
            else {
                if (mutationTypes.size() > 0) {
                    alleleTypes += "a.mutationType IN [" + StringUtils.join(mutationTypes, ",") + "]";
                }
                alleleTypes = " AND (" + alleleTypes + ") ";
            }
        }
        return alleleTypes;
    }

    private String composePhenodigmScoreStr(JSONObject jParams){
        String phenodigmScore = "";

        if (jParams.containsKey("phenodigmScore")) {
            String[] scores = jParams.getString("phenodigmScore").split(",");
            int low = Integer.parseInt(scores[0]);
            int high = Integer.parseInt(scores[1]);
            phenodigmScore = " dm.diseaseToModelScore >= " + low + " AND dm.diseaseToModelScore <= " + high + " ";
        }
        return phenodigmScore;
    }
    private String composeDiseaseGeneAssociation(JSONObject jParams){
        String diseaeGeneAssoc = "";

        if (jParams.containsKey("diseaseGeneAssociation")) {
            JSONArray assocs = jParams.getJSONArray("diseaseGeneAssociation");

            if (assocs.size() < 2 ){
                if (assocs.get(0).toString().equals("humanCurated")){
                    diseaeGeneAssoc = " AND dm.humanCurated = true ";
                }
                else {
                    diseaeGeneAssoc = " AND dm.humanCurated = false ";
                }
            }
        }
        return diseaeGeneAssoc;
    }
    private String composeHumanDiseaseTermStr(JSONObject jParams){
        String humanDiseaseTerm = "";

        if (jParams.containsKey("srchDiseaseModel")) {
            String name = jParams.getString("srchDiseaseModel");
            humanDiseaseTerm = " AND dm.diseaseTerm = '" + name.toString() + "' ";
        }
        return humanDiseaseTerm;
    }
   

    @RequestMapping(value = "/dataTableNeo4jBq", method = RequestMethod.POST)
    public ResponseEntity<String> bqDataTableJson2(
            @RequestParam(value = "idlist", required = true) String idlistStr,
            @RequestParam(value = "properties", required = true) String properties,
            @RequestParam(value = "datatypeProperties", required = true) String datatypeProperties,
            @RequestParam(value = "dataType", required = true) String dataType,
            @RequestParam(value = "childLevel", required = false) String childLevel,
           // @RequestParam(value = "chrRange", required = false) String chrRange,
            @RequestParam(value = "chr", required = false) String chr,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) throws Exception {

        System.out.println("dataType: " +  dataType);

        if (dataType.equals("EnsemblGeneId")){
            dataType = "Gene";
        }

        if (childLevel != null){
            System.out.println("MP childLevel: " + childLevel);
        }

        JSONObject jDatatypeProperties = (JSONObject) JSONSerializer.toJSON(datatypeProperties);
        System.out.println(jDatatypeProperties.toString());

        System.out.println("properties: "+ properties);

        Set<String> labels = new LinkedHashSet<>(jDatatypeProperties.keySet());
        System.out.println("Labels: "+ labels);

        System.out.println("idlistStr: " + idlistStr);
        Set<String> idlist = new LinkedHashSet<>();
        if (idlistStr.matches("^\".*\"$")){
            // ontology term name is quoted

            idlist.add(idlistStr.replaceAll("\"",""));
        }
        else {
            idlist = new LinkedHashSet<>(Arrays.asList(idlistStr.split(",")));
        }
        System.out.println("idlist: " + idlist);

        String content = null;

        String regionId = null;
        int regionStart = 0;
        int regionEnd = 0;

        if (idlistStr.matches("^chr(\\w*):(\\d+)-(\\d+)$") ) {
            System.out.println("find chr range");

            Pattern pattern = Pattern.compile("^chr(\\w*):(\\d+)-(\\d+)$");
            Matcher matcher = pattern.matcher(idlistStr);
            while(matcher.find()) {
                System.out.println("found: " + matcher.group(1));
                regionId = matcher.group(1);
                regionStart = Integer.parseInt(matcher.group(2));
                regionEnd = Integer.parseInt(matcher.group(3));
            }

            String mode = "nonExport";
        }

        // chr filter for mp, hp, disease input type
        if (chr != null){
            regionId = chr;
            System.out.println("chr filter: " + chr);
        }
        else {
            System.out.println("chr filter is null");
        }

        baseUrl = request.getAttribute("baseUrl").toString();

        content = fetchGraphData(dataType, idlist, labels, jDatatypeProperties, properties, regionId, regionStart, regionEnd, childLevel);

        return new ResponseEntity<String>(content, createResponseHeaders(), HttpStatus.CREATED);
    }

    public String fetchGraphData(String dataType, Set<String> srchKw, Set<String> labels, JSONObject jDatatypeProperties, String properties, String regionId, int regionStart, int regionEnd, String childLevel) throws Exception {

        List<String> cols = new ArrayList<>();
        for (String property : properties.split(",")){
            cols.add(property);
        }

        int rowCount = 0;
        JSONObject j = new JSONObject();
        j.put("aaData", new Object[0]);

        for(String kw : srchKw) {

            Map<String, Set<String>> colValMap = new HashedMap();

            colValMap.put("searchBy", new TreeSet<>());
            colValMap.get("searchBy").add(kw);

            rowCount++;

            System.out.println("-- Working on " + kw);
            System.out.println("-- region id " + regionId);

            List<Object> objs = null;

            if (kw.matches("^chr(\\w*):(\\d+)-(\\d+)$") && dataType.equals("Gene")) {
                objs = geneRepository.findDataByChrRange(regionId, regionStart, regionEnd);
            }
            else if (kw.startsWith("MGI:")){
                objs = geneRepository.findDataByMgiId(kw);
            }
            else if (dataType.equals("Gene")) {
                objs = geneRepository.findDataByMarkerSymbol(kw);
            }
            else if (kw.startsWith("ENSMUSG")){
                objs = ensemblGeneIdRepository.findDataByEnsemblGeneId(kw);
            }

            // DiseaseModel ID
            else if ((kw.startsWith("OMIM:") || kw.startsWith("ORPHANET:") || kw.startsWith("DECIPHER:")) && regionId != null){
                System.out.println("search disease " + kw + " and chr " + regionId);
                objs = diseaseModelRepository.findDataByDiseaseIdChr(kw, regionId);
                System.out.println("objs found: "+objs.size());
            }
            else if (kw.startsWith("OMIM:") || kw.startsWith("ORPHANET:") || kw.startsWith("DECIPHER:")){
                System.out.println("search disease");
                objs = diseaseModelRepository.findDataByDiseaseId(kw);
            }
            // DiseaseModel term
            else if (dataType.equals("DiseaseModel") && regionId != null) {
                System.out.println("Disease id normal");
                objs = diseaseModelRepository.findDataByDiseaseTermChr(kw, regionId);
            }
            else if (dataType.equals("DiseaseModel")) {
                System.out.println("Disease term normal");
                objs = diseaseModelRepository.findDataByDiseaseTerm(kw);
            }

            // MP ID
            else if (kw.startsWith("MP:") && regionId != null && childLevel != null) {

                if (childLevel.equals("all")) {
                    System.out.println("MP id with region and ALL children");
                    objs = mpRepository.findAllChildrenMpsByMpIdChr(kw, regionId);
                }
                else {
                    System.out.println("MP id with region and " + childLevel + " children level");
                    int level = Integer.parseInt(childLevel);
                    objs = mpRepository.findChildrenMpsByMpIdChr(kw, regionId, level);
                }
                objs = fetchTerms(objs);
            }
            else if (kw.startsWith("MP:") && regionId != null && childLevel == null) {
                System.out.println("MP id with region");
                objs = mpRepository.findDataByMpIdChr(kw, regionId);
            }
            else if (kw.startsWith("MP:") && regionId == null && childLevel != null) {
                if (childLevel.equals("all")){
                    System.out.println("MP id with ALL children");

                    // first get all children mps (including self)
                    objs = mpRepository.findAllChildrenMpsByMpId(kw);
                    System.out.println("1. Got " + objs.size() + "children");
                }
                else if (childLevel != "0"){
                    System.out.println("MP Id with " + childLevel + " children level");

                    // first get all children mps (including self)
                    int level = Integer.parseInt(childLevel);
                    objs = mpRepository.findChildrenMpsByMpId(kw, level);
                    System.out.println("2. Got " + objs.size() + "children");
                }

                // then query data by each mp and put together
                objs = fetchTerms(objs);
            }
            else if (kw.startsWith("MP:") && regionId == null && childLevel == null){
                System.out.println("MP id normal");
                objs = mpRepository.findDataByMpId(kw);
            }

            // MP term
            else if (dataType.equals("MP") && regionId != null && childLevel != null) {
                System.out.println("MP term with all children and chr filter");
                if (childLevel.equals("all")) {
                    objs = mpRepository.findAllChildrenMpsByMpTermChr(kw, regionId);
                }
                else if (childLevel != "0"){
                    System.out.println("MP term with " + childLevel + " children level and chr filter");

                    // first get all children mps (including self)
                    int level = Integer.parseInt(childLevel);
                    objs = mpRepository.findChildrenMpsByMpTermChr(kw, regionId, level);
                }
                // then query data by each mp and put together
                objs = fetchTerms(objs);
            }
            else if (dataType.equals("MP") && regionId != null && childLevel == null){
                System.out.println("MP term with region");
                objs = mpRepository.findDataByMpTermChr(kw, regionId);
            }
            else if (dataType.equals("Mp") && regionId == null && childLevel != null) {
                if (childLevel.equals("all")){
                    System.out.println("MP term with ALL children");

                    // first get all children mps (including self)
                    objs = mpRepository.findAllChildrenMpsByMpTerm(kw);
                }
                else if (childLevel != "0"){
                    System.out.println("MP term with " + childLevel + " children level");

                    // first get all children mps (including self)
                    int level = Integer.parseInt(childLevel);
                    objs = mpRepository.findChildrenMpsByMpTerm(kw, level);
                }

                // then query data by each mp and put together
                objs = fetchTerms(objs);
            }
            else if (dataType.equals("Mp") && regionId == null && childLevel == null) {
                System.out.println("MP term normal");
                objs = mpRepository.findDataByMpTerm(kw);
            }
            // HP ID
            else if (kw.startsWith("HP:") && regionId != null && childLevel != null) {
                if (childLevel.equals("all")) {
                    System.out.println("HP id with region and ALL children");
                    objs = hpRepository.findAllChildrenHpsByHpIdChr(kw, regionId);
                }
                else {
                    System.out.println("HP id with region and " + childLevel + " children level");
                    int level = Integer.parseInt(childLevel);
                    objs = hpRepository.findChildrenHpsByHpIdChr(kw, regionId, level);
                }

                // then query data by each mp and put together
                objs = fetchTerms(objs);
            }
            else if (kw.startsWith("HP:") && regionId != null && childLevel == null) {
                System.out.println("HP id with region");
                objs = hpRepository.findDataByHpIdChr(kw, regionId);
            }
            else if (kw.startsWith("HP:") && regionId == null && childLevel != null) {
                if (childLevel.equals("all")){
                    System.out.println("HP id with ALL children");

                    // first get all children hps (including self)
                    objs = hpRepository.findAllChildrenHpsByHpId(kw);
                    System.out.println("1. Got " + objs.size() + "children");
                }
                else if (childLevel != "0"){
                    System.out.println("HP Id with " + childLevel + " children level");

                    // first get all children hps (including self)
                    int level = Integer.parseInt(childLevel);
                    objs = hpRepository.findChildrenHpsByHpId(kw, level);

                    System.out.println("2. Got " + objs.size() + "children");
                }

                // then query data by each mp and put together
                objs = fetchTerms(objs);
            }
            else if (kw.startsWith("HP:") && regionId == null && childLevel == null){
                System.out.println("HP id normal");
                objs = hpRepository.findDataByHpId(kw);
            }

            // HP term
            else if (dataType.equals("HP") && regionId != null && childLevel != null) {
                System.out.println("HP term with all children and chr filter");
                if (childLevel.equals("all")) {
                    objs = hpRepository.findAllChildrenHpsByHpTermChr(kw, regionId);
                }
                else if (childLevel != "0"){
                    System.out.println("HP term with " + childLevel + " children level and chr filter");

                    // first get all children hps (including self)
                    int level = Integer.parseInt(childLevel);
                    objs = hpRepository.findChildrenHpsByHpTermChr(kw, regionId, level);
                }
                // then query data by each hp and put together
                objs = fetchTerms(objs);
            }
            else if (dataType.equals("HP") && regionId != null && childLevel == null){
                System.out.println("HP term with region");
                objs = hpRepository.findDataByHpTermChr(kw, regionId);
            }
            else if (dataType.equals("Hp") && regionId == null && childLevel != null) {
                if (childLevel.equals("all")){
                    System.out.println("HP term with ALL children");

                    // first get all children hps (including self)
                    objs = hpRepository.findAllChildrenHpsByHpTerm(kw);
                }
                else if (childLevel != "0"){
                    System.out.println("HP term with " + childLevel + " children level");

                    // first get all children hps (including self)
                    int level = Integer.parseInt(childLevel);
                    objs = hpRepository.findChildrenHpsByHpTerm(kw, level);
                }

                // then query data by each hp and put together
                objs = fetchTerms(objs);
            }
            else if (dataType.equals("Hp") && regionId == null && childLevel == null) {
                System.out.println("HP term normal");
                objs = hpRepository.findDataByHpTerm(kw);
            }
            else if (dataType.equals("HumanGeneSymbol")){
                objs = humanGeneSymbolRepository.findDataByHumanGeneSymbol(kw);
            }


            System.out.println("Data objects found: "+ objs.size());

            populateColValMap(objs, colValMap, jDatatypeProperties);

            System.out.println("About to prepare for rows");


            List<String> rowData = new ArrayList<>();
            for (String col : cols){
                if (colValMap.containsKey(col)) {
                    List<String> vals = new ArrayList<>(colValMap.get(col));

                    int valSize = vals.size();

                    if (valSize > 2) {
                        // add showmore
                        vals.add("<button rel=" + valSize + " class='showMore'>show all (" + valSize + ")</button>");
                    }
                    if (valSize == 1) {
                        rowData.add(StringUtils.join(vals, ""));
                    } else {
                        rowData.add("<ul>" + StringUtils.join(vals, "") + "</ul>");
                    }

                    //System.out.println("col: " + col);
                    if (col.equals("ontoSynonym")) {
                        System.out.println(col + " -- " + vals);
                    }
                }
                else {
                    rowData.add(NA);
                }

            }
            j.getJSONArray("aaData").add(rowData);

        }

        System.out.println("rows done");

        j.put("iTotalRecords", rowCount);
        j.put("iTotalDisplayRecords", rowCount);


        return j.toString();
    }

    private List<Object> fetchTerms(List<Object> objs){
        
        List<Object> childTerms = new ArrayList<>();
        for(Object o : objs){
            if (o.getClass().getSimpleName().equals("Mp")) {
                Mp m = (Mp) o;
                childTerms.addAll(mpRepository.findDataByMpId(m.getMpId()));
            }
            else {
                Hp h = (Hp) o;
                childTerms.addAll(hpRepository.findDataByHpId(h.getHpId()));
            }
        }
        return childTerms;
    }

    public void populateColValMapAdvSrch(Map<String, List<String>> node2Properties,  Object obj, Map<String, Set<String>> colValMap, JSONObject jParam, String fileType) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        String className = obj.getClass().getSimpleName();

        if (jParam.containsKey(className)) {

            List<String> nodeProperties = node2Properties.get(className);
//            System.out.println("className: " + className);
//            System.out.println("properties:" + nodeProperties);

            if (className.equals("Gene")) {
                Gene g = (Gene) obj;
                getValues(nodeProperties, g, colValMap, fileType, jParam);
            }
            else if (className.equals("EnsemblGeneId")) {
                EnsemblGeneId ensg = (EnsemblGeneId) obj;
                getValues(nodeProperties, ensg, colValMap, fileType, jParam);
            }
            else if (className.equals("MarkerSynonym")) {
                MarkerSynonym m = (MarkerSynonym) obj;
                getValues(nodeProperties, m, colValMap, fileType, jParam);
            }
            else if (className.equals("HumanGeneSymbol")) {
                HumanGeneSymbol hg = (HumanGeneSymbol) obj;
                getValues(nodeProperties, hg, colValMap, fileType, jParam);
            }
            else if (className.equals("DiseaseModel")) {
                DiseaseModel dm = (DiseaseModel) obj;
                getValues(nodeProperties, dm, colValMap, fileType, jParam);
            }
            else if (className.equals("MouseModel")) {
                MouseModel mm = (MouseModel) obj;
                getValues(nodeProperties, mm, colValMap, fileType, jParam);
            }
            else if (className.equals("Allele")) {
                Allele allele = (Allele) obj;
                getValues(nodeProperties, allele, colValMap, fileType, jParam);
            }
            else if (className.equals("Mp")) {
                Mp mp = (Mp) obj;
                getValues(nodeProperties, mp, colValMap, fileType, jParam);
            }
            else if (className.equals("StatisticalResult")) {
                StatisticalResult sr = (StatisticalResult) obj;
                getValues(nodeProperties, sr, colValMap, fileType, jParam);
            }
            else if (className.equals("OntoSynonym")) {
                OntoSynonym ontosyn = (OntoSynonym) obj;
                getValues(nodeProperties, ontosyn, colValMap, fileType, jParam);
            }
            else if (className.equals("Hp")) {
                Hp hp = (Hp) obj;
                getValues(nodeProperties, hp, colValMap, fileType, jParam);
            }
        }

    }

    public void populateColValMap(List<Object> objs, Map<String, Set<String>> colValMap, JSONObject jDatatypeProperties) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        String fileType = null;
        for (Object obj : objs) {
            String className = obj.getClass().getSimpleName();

            if (jDatatypeProperties.containsKey(className)) {

                //System.out.println("className: " + className);

                List<String> nodeProperties = jDatatypeProperties.getJSONArray(className);

                if (className.equals("Gene")) {
                    Gene g = (Gene) obj;
                    getValues(nodeProperties, g, colValMap, fileType, jDatatypeProperties);  // convert to class ???
                }
                else if (className.equals("EnsemblGeneId")) {
                    EnsemblGeneId ensg = (EnsemblGeneId) obj;
                    getValues(nodeProperties, ensg, colValMap, fileType, jDatatypeProperties);
                }
                else if (className.equals("MarkerSynonym")) {
                    MarkerSynonym m = (MarkerSynonym) obj;
                    getValues(nodeProperties, m, colValMap, fileType, jDatatypeProperties);
                }
                else if (className.equals("HumanGeneSymbol")) {
                    HumanGeneSymbol hg = (HumanGeneSymbol) obj;
                    getValues(nodeProperties, hg, colValMap, fileType, jDatatypeProperties);
                }
                else if (className.equals("DiseaseModel")) {
                    DiseaseModel dm = (DiseaseModel) obj;
                    getValues(nodeProperties, dm, colValMap, fileType, jDatatypeProperties);
                }
                else if (className.equals("MouseModel")) {
                    MouseModel mm = (MouseModel) obj;
                    getValues(nodeProperties, mm, colValMap, fileType, jDatatypeProperties);
                }
                else if (className.equals("Allele")) {
                    Allele allele = (Allele) obj;
                    getValues(nodeProperties, allele, colValMap, fileType, jDatatypeProperties);
                }
                else if (className.equals("Mp")) {
                    Mp mp = (Mp) obj;
                    getValues(nodeProperties, mp, colValMap, fileType, jDatatypeProperties);
                }
                else if (className.equals("OntoSynonym")) {
                    OntoSynonym ontosyn = (OntoSynonym) obj;
                    getValues(nodeProperties, ontosyn, colValMap, fileType, jDatatypeProperties);
                }
                else if (className.equals("Hp")) {
                    Hp hp = (Hp) obj;
                    getValues(nodeProperties, hp, colValMap, fileType, jDatatypeProperties);
                }
            }
        }
        
    }

    public void getValues(List<String> nodeProperties, Object o, Map<String, Set<String>> colValMap, String fileType, JSONObject jParam) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        int showCutOff = 3;

        if (hostname == null) {
            hostname = jParam.getString("hostname").toString();
        }
        if ( ! hostname.startsWith("http")){
            hostname = "http:" + hostname;
        }
        if  (baseUrl == null) {
            baseUrl = jParam.getString("baseUrl").toString();
        }

        //System.out.println("TEST hostname: " + hostname);

        String geneBaseUrl = baseUrl + "/genes/";
        String alleleBaseUrl = baseUrl + "/alleles/"; //MGI:2676828/tm1(mirKO)Wtsi
        String mpBaseUrl = baseUrl + "/phenotypes/";
        String diseaseBaseUrl = baseUrl + "/disease/";
        String ensemblGeneBaseUrl = "http://www.ensembl.org/Mus_musculus/Gene/Summary?db=core;g=";


        for(String property : nodeProperties) {

            if (property.equals("topLevelMpId")){
                property = "topLevelStatus";
            }

            char first = Character.toUpperCase(property.charAt(0));
            String property2 = first + property.substring(1);
            //System.out.println("property method: " + property2);

            Method method = o.getClass().getMethod("get"+property2);
            if (! colValMap.containsKey(property)) {
                colValMap.put(property, new LinkedHashSet<>());
            }
            String colVal = NA;

//            if (method.invoke(o) == null){
//                System.out.println(property + " is null");
//            }

            try {
                colVal = method.invoke(o).toString();
                //System.out.println(property + " : " +  colVal);

            } catch(Exception e) {
               // System.out.println(property + " set to " + colVal);
            }


            if (! colVal.isEmpty()) {
                if (property.equals("markerSymbol")){
                    Gene gene = (Gene) o;
                    if (fileType == null || fileType.equals("html")){
                        String mgiAcc = gene.getMgiAccessionId();
                        colVal = "<a target='_blank' href='" + hostname + geneBaseUrl + mgiAcc + "'>" + colVal + "</a>";
                    }
                }
                else if (property.equals("mgiAccessionId")){
                    if (fileType != null  && ! fileType.equals("html")){
                        colVal = hostname + geneBaseUrl + colVal;
                    }
                }
                else if (property.equals("mpTerm")){
                    Mp mp = (Mp) o;
                    if (fileType == null || fileType.equals("html")) {
                        String mpId = mp.getMpId();
                        colVal = "<a target='_blank' href='" + hostname + mpBaseUrl + mpId + "'>" + colVal + "</a>";
                    }
                }
//                else if (property.equals("mpId")){
//                    if (isExport){
//                        colVal = hostname + mpBaseUrl + colVal;
//                    }
//                }
                else if (property.equals("diseaseTerm")){
                    DiseaseModel dm = (DiseaseModel) o;

                    if (fileType == null || fileType.equals("html")) {
                        String dId = dm.getDiseaseId();
                        colVal = "<a target='_blank' href='" + hostname + diseaseBaseUrl + dId + "'>" + colVal + "</a>";
                    }
                }
                // multiple http links won't work in excel cell
//                else if (property.equals("diseaseId")){
//                    if (isExport){
//                        colVal = hostname + diseaseBaseUrl + colVal;
//                    }
//                }
                else if (property.equals("alleleSymbol")){

                    Allele al = (Allele) o;
                    if (fileType == null || fileType.equals("html")) {

                        int index = colVal.indexOf('<');
                        String wantedSymbol = colVal.replace(colVal.substring(0, index+1), "").replace(">","");
                        colVal = Tools.superscriptify(colVal);
                        String aid = al.getMgiAccessionId() + "/" + wantedSymbol;
                        colVal = "<a target='_blank' href='" + hostname + alleleBaseUrl + aid + "'>" + colVal + "</a>";
                    }
                }
                else if (property.equals("alleleMgiAccessionId")){

                    if (fileType != null && ! fileType.equals("html")){
                        Allele al = (Allele) o;
                        String asym = al.getAlleleSymbol();
                        int index = asym.indexOf('<');
                        String wantedSymbol = asym.replace(asym.substring(0, index+1), "").replace(">","");
                        String aid = al.getMgiAccessionId() + "/" + wantedSymbol;

                       // System.out.println("asym: " + asym + " wanted: " + wantedSymbol);
                        colVal = hostname + alleleBaseUrl + aid;
                    }
                }
                else if (property.equals("ensemblGeneId")){
                    colVal = colVal.replaceAll("\\[", "").replaceAll("\\]","");
                    colVal = "<a target='_blank' href='" + ensemblGeneBaseUrl + colVal + "'>" + colVal + "</a>";
                }
                else if (property.equals("markerSynonym") || property.equals("humanGeneSymbol")){
                    colVal = colVal.replaceAll("\\[", "").replaceAll("\\]","");
                }
                else if (property.equals("parameterName")){
                    StatisticalResult sr = (StatisticalResult) o;
                    String procedureName = sr.getProcedureName();

                    if (fileType == null || fileType.equals("html")){
                        colVal = "<b>(" + procedureName + ")</b> " + colVal;
                    }
                    else {
                        colVal = "(" + procedureName + ") " + colVal;
                    }
                }
                else if (property.equals("pvalue")){
                    if (fileType == null || fileType.equals("html")) {
                        colVal = "<span class='pv'>" + colVal + "</span>";
                    }
                }


                if (property.equals("diseaseClasses")) {

                    List<String> dcls = Arrays.asList(StringUtils.split(colVal, ","));
                    List<String> vals = new ArrayList<>();

                    if (fileType == null){
                        for (String dcl : dcls) {
                            vals.add("<li>" + dcl + "</li>");
                        }
                        colValMap.get(property).addAll(vals);
                    }
                    else {
                        for (String dcl : dcls) {
                            vals.add(dcl);
                        }
                        colValMap.get(property).add(colVal);
                    }
                }
                else if (fileType == null || fileType.equals("html")){
                    colValMap.get(property).add("<li>" + colVal + "</li>");
                }
                else {
                    colValMap.get(property).add(colVal);
                }

               // System.out.println("colval: "+colValMap);

            }
        }

    }

    public String lowercaseListStr(String idlist) {
        List<String> lst = Arrays.asList(StringUtils.split(idlist,","));
        List<String> lst2 = new ArrayList<>();
        for (String s : lst){
            lst2.add("lower(" + s + ")");
        }
        return StringUtils.join(lst2, ",");
    }

    private HttpHeaders createResponseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();

        // this returns json, but utf encoding failed
        //responseHeaders.setContentType(MediaType.APPLICATION_JSON);

        // this returns html string, not json, and is utf encoded
        responseHeaders.add("Content-Type", "text/html; charset=utf-8");


        return responseHeaders;
    }


    @RequestMapping(value = "/chrlen", method = RequestMethod.GET)
    public @ResponseBody
    String chrlen(
            @RequestParam(value = "chr", required = false) String chr,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) throws IOException, URISyntaxException, SolrServerException, SQLException {

        //fetchChrLenJson();
        Connection connKomp2 = komp2DataSource.getConnection();
        String len = null;

        if (chr == null){
            String sql = "select name, length from seq_region where name not like '%patch'";

            List<String> nameLen = new ArrayList<>();
            try (PreparedStatement p = connKomp2.prepareStatement(sql)) {
                ResultSet resultSet = p.executeQuery();

                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String length = Integer.toString(resultSet.getInt("length"));
                    nameLen.add("<span class='chrname'>Chr: " + name + "</span><span class='chrlen'>" + length + "</span>");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            len = StringUtils.join(nameLen, "<br>");

        }
        else {
            String sql = "SELECT length FROM seq_region WHERE name ='" + chr + "'";

            try (PreparedStatement p = connKomp2.prepareStatement(sql)) {
                ResultSet resultSet = p.executeQuery();

                while (resultSet.next()) {
                    len = Integer.toString(resultSet.getInt("length"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return len;
    }
}