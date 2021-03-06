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

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.db.beans.SecondaryProjectBean;
import org.mousephenotype.cda.solr.service.*;
import org.mousephenotype.cda.solr.service.dto.BasicBean;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.solr.web.dto.GeneRowForHeatMap;
import org.mousephenotype.cda.solr.web.dto.HeatMapCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.phenotype.chart.PhenomeChartProvider;
import uk.ac.ebi.phenotype.chart.PieChartCreator;
import uk.ac.ebi.phenotype.chart.UnidimensionalChartAndTableProvider;
import uk.ac.ebi.phenotype.error.GenomicFeatureNotFoundException;
import uk.ac.ebi.phenotype.web.dao.SecondaryProjectService;
import uk.ac.ebi.phenotype.web.util.FileExportUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.*;


@Controller
public class SecondaryProjectController {

    private final Logger logger = LoggerFactory.getLogger(SecondaryProjectController.class);

    @Resource(name = "globalConfiguration")
    private Map<String, String> config;

    @Autowired
    AlleleService as;

    @Autowired
    @Qualifier("postqcService")
    PostQcService genotypePhenotypeService;
    @Autowired
    @Qualifier("preqcService")
    PreQcService preQcService;

    @Autowired
    GeneService geneService;

    @Autowired
    MpService mpService;

    @Autowired
    UnidimensionalChartAndTableProvider chartProvider;

    @Autowired
    @Qualifier("idg")
    SecondaryProjectService idg;

    @Autowired
    @Qualifier("threeI")
    SecondaryProjectService threeI;

    @Autowired
    @Qualifier("phenodigmCore")
    SolrClient phenodigmCore;

    private PhenomeChartProvider phenomeChartProvider = new PhenomeChartProvider();

    private HttpHeaders createResponseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.TEXT_PLAIN);
        return responseHeaders;
    }
    
    @RequestMapping(value = "/secondaryproject/idg/mapping", method = RequestMethod.GET)
    public String idgMappingPage( Model model)
            throws SolrServerException, IOException , URISyntaxException {
    	System.out.println("calling idg mapping page");
    	List<String> colorsForPie=new ArrayList<>();
    	 String nonSigColor="'rgb(194, 194, 194)'";
         String sigColor="'rgb(247, 157, 70)'";
         colorsForPie.add(sigColor);
         colorsForPie.add(nonSigColor);
        
        Map<String, Integer> totalHumanLabelToNumber = new LinkedHashMap<>();
        totalHumanLabelToNumber.put("Mouse Orthologs",89);
        totalHumanLabelToNumber.put("No Mouse Orthologs", 11);
        String idgHumanOrthologPie = PieChartCreator.getPieChartForColorList(totalHumanLabelToNumber, "idgHumanOrthologPie", "Human Ortholog Mappings", "",colorsForPie);
        model.addAttribute("idgHumanOrthologPie", idgHumanOrthologPie);
    	return "idgMappings";
    }
    

    @RequestMapping(value = "/secondaryproject/{id}", method = RequestMethod.GET)
    public String loadSecondaryProjectPage(@PathVariable String id, Model model, HttpServletRequest request, RedirectAttributes attributes)
            throws SolrServerException, IOException , URISyntaxException {

       
        if (id.equalsIgnoreCase(SecondaryProjectService.SecondaryProjectIds.IDG.name())) {
            try {
                Set<SecondaryProjectBean> secondaryProjects = idg.getAccessionsBySecondaryProjectId(id);
                Set<String> accessions=SecondaryProjectBean.getAccessionsFromBeans(secondaryProjects);
                //model.addAttribute("genotypeStatusChart", chartProvider.getStatusColumnChart(geneService.getStatusCount(accessions, GeneDTO.LATEST_ES_CELL_STATUS), "Genotype Status Chart", "genotypeStatusChart"));
                HashMap<String, Long> geneStatus = geneService.getStatusCount(accessions, GeneDTO.LATEST_ES_CELL_STATUS);
                HashMap<String, Long> mouseStatus = geneService.getStatusCount(accessions, GeneDTO.LATEST_MOUSE_STATUS);
                HashMap<String, Long> phenoStatus = geneService.getStatusCount(accessions, GeneDTO.LATEST_PHENOTYPE_STATUS);
                HashMap<String, Long> combinedData=new HashMap<>();
                combinedData.putAll(geneStatus);
                combinedData.putAll(mouseStatus);
                combinedData.putAll(phenoStatus);
                
                List<String> colorsForPie=new ArrayList<>();
                
                String nonSigColor="'rgb(194, 194, 194)'";
                String sigColor="'rgb(247, 157, 70)'";
				colorsForPie.add(sigColor);
                colorsForPie.add(nonSigColor);

                Map<String, Integer> totalLabelToNumber = new LinkedHashMap<>();
                totalLabelToNumber.put("IDG gene with IMPC Data",278);
                totalLabelToNumber.put("IDG gene with no IMPC Data", 81);

                String idgOrthologPie = PieChartCreator.getPieChartForColorList(totalLabelToNumber, "idgOrthologPie", "IDG genes IMPC data status", "",colorsForPie);
                model.addAttribute("idgOrthologPie", idgOrthologPie);
                model.addAttribute("idgChartTable", chartProvider.getStatusColumnChart(combinedData, "IDG genes IMPC production status", "idgChart", colorsForPie));

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "idg";
        } else if (id.equalsIgnoreCase(SecondaryProjectService.SecondaryProjectIds.threeI.name()) || id.equalsIgnoreCase("3I")) {
            return "threeI";
        }

        return "";
    }
    
	@RequestMapping("/secondaryproject/export/{project}")
	public void genesExport(@PathVariable String project,
			@RequestParam(required = true, value = "fileType") String fileType,
			@RequestParam(required = true, value = "fileName") String fileName, Model model, HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes attributes)
			throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException,
			GenomicFeatureNotFoundException, IOException, SQLException, SolrServerException {

		System.out.println("export called for secondary project " + project);
		if (project.equalsIgnoreCase(SecondaryProjectService.SecondaryProjectIds.IDG.name())) {

			List<GeneRowForHeatMap> geneRows = idg.getGeneRowsForHeatMap(request);

			List<String> tableHeaders=new ArrayList<>();
			tableHeaders.add("Gene Accession");
            tableHeaders.add("Gene Symbol");
            tableHeaders.add("Human gene");
			tableHeaders.add("Family");
	        tableHeaders.add("Availability");//and 4 tabs to cover all availability options - no I don't like this either JW
            List<String> dataRows = new ArrayList<>();
            List<BasicBean> xAxisBeans = idg.getXAxisForHeatMap();
            List<String> phenotypeHeaders=new ArrayList<>();
            for(BasicBean xAxisBean: xAxisBeans){
             phenotypeHeaders.add(xAxisBean.getName());
            }
            tableHeaders.addAll(phenotypeHeaders);
            dataRows.add(StringUtils.join(tableHeaders, "\t"));
            // dataRows.add(GenePageTableRow.getTabbedHeader());
            for (GeneRowForHeatMap row : geneRows) {
            List<String> phenotypeStatus=new ArrayList<>();
            for(String phenoHeader: phenotypeHeaders){
                HeatMapCell phenotypeStatusCell=row.getXAxisToCellMap().get(phenoHeader);
                phenotypeStatus.add(phenotypeStatusCell.getStatus());
            }

             dataRows.add(row.toTabbedString() + StringUtils.join(phenotypeStatus, "\t"));
            }
			
            String filters = null;
            FileExportUtils.writeOutputFile(response, dataRows, fileType, fileName, filters);
		}
	}
	

}
