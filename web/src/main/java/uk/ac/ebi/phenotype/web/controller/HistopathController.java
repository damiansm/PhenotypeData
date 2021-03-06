package uk.ac.ebi.phenotype.web.controller;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.HistopathService;
import org.mousephenotype.cda.solr.service.ImageService;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.solr.web.dto.HistopathPageTableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Controller
public class HistopathController {

	private final Logger log = LoggerFactory.getLogger(HistopathController.class);

	@Autowired
	HistopathService histopathService;

	@Autowired
	GeneService geneService;

	@Autowired
	ImageService imageService;

	@RequestMapping("/histopath/{acc}")
	public String histopath(@PathVariable String acc, Model model) throws SolrServerException, IOException {
		// exmple Lpin2 MGI:1891341
		GeneDTO gene = geneService.getGeneById(acc);
		model.addAttribute("gene", gene);

		List<ObservationDTO> allObservations = histopathService.getObservationsForHistopathForGene(acc);
		List<ObservationDTO> abnormalObservationsOnly = histopathService
				.screenOutObservationsThatAreNormal(allObservations);
		//get observations that have the same sampleid, sequence_id and anatomy name
		Map<String, List<ObservationDTO>> uniqueSampleSequeneAndAnatomyName = histopathService
				.getUniqueInfo(allObservations);
		
		List<HistopathPageTableRow> histopathRows = histopathService.getTableData(uniqueSampleSequeneAndAnatomyName);
		Set<String> parameterNames = new TreeSet<>();

		// get image data
		List< SolrDocument> histopathImagesForGene = new ArrayList<>();
		for (ObservationDTO obs : allObservations) {
			if(obs.getDownloadFilePath()!=null){
				SolrDocument image = imageService.getImageByDownloadFilePath(obs.getDownloadFilePath());
				
				histopathImagesForGene.add(image);
			}
			
		}

		// chop the parameter names so we have just the beginning as we have
		// parameter names like "Brain - Description" and "Brain - MPATH
		// Diagnostic Term" we want to lump all into Brain related

		for (HistopathPageTableRow row : histopathRows) {
			parameterNames.addAll(row.getParameterNames());

		}

		// Collections.sort(histopathRows, new HistopathAnatomyComparator());

		model.addAttribute("histopathRows", histopathRows);
		model.addAttribute("extSampleIdToObservations", abnormalObservationsOnly);
		model.addAttribute("parameterNames", parameterNames);
		model.addAttribute("histopathImagesForGene",histopathImagesForGene);
		return "histopath";
	}

	@RequestMapping("/histopathsum/{acc}")
	public String histopathSummary(@PathVariable String acc, Model model) throws SolrServerException, IOException {
		// exmple Lpin2 MGI:1891341
		GeneDTO gene = geneService.getGeneById(acc);
		model.addAttribute("gene", gene);

		List<ObservationDTO> allObservations = histopathService.getObservationsForHistopathForGene(acc);
		List<ObservationDTO> extSampleIdToObservations = histopathService
				.screenOutObservationsThatAreNormal(allObservations);
		Map<String, List<ObservationDTO>> uniqueSampleSequeneAndAnatomyName = histopathService
				.getUniqueInfo(allObservations);
		List<HistopathPageTableRow> histopathRows = histopathService.getTableData(uniqueSampleSequeneAndAnatomyName);
		// for the summary we add an extra method to count the significant
		// scores and collapse rows based on Anatomy
		List<HistopathPageTableRow> collapsedRows = histopathService.collapseHistopathTableRows(histopathRows);

		Set<String> parameterNames = new TreeSet<>();
		// chop the parameter names so we have just the beginning as we have
		// parameter names like "Brain - Description" and "Brain - MPATH
		// Diagnostic Term" we want to lump all into Brain related
		for (HistopathPageTableRow row : histopathRows) {
			parameterNames.addAll(row.getParameterNames());

		}

		model.addAttribute("histopathRows", collapsedRows);
		model.addAttribute("extSampleIdToObservations", extSampleIdToObservations);
		model.addAttribute("parameterNames", parameterNames);
		return "histopathSummary";
	}
}
