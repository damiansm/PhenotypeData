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
package org.mousephenotype.cda.indexers;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.owl.OntologyParser;
import org.mousephenotype.cda.owl.OntologyParserFactory;
import org.mousephenotype.cda.owl.OntologyTermDTO;
import org.mousephenotype.cda.solr.service.dto.ImpressDTO;
import org.mousephenotype.cda.solr.service.dto.ParameterDTO;
import org.mousephenotype.cda.solr.service.dto.PipelineDTO;
import org.mousephenotype.cda.solr.service.dto.ProcedureDTO;
import org.mousephenotype.cda.utilities.RunStatus;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


@EnableAutoConfiguration
public class PipelineIndexer extends AbstractIndexer implements CommandLineRunner {

	private final Logger logger = LoggerFactory.getLogger(PipelineIndexer.class);

	private Connection komp2DbConnection;

	@Autowired
	@Qualifier("komp2DataSource")
	DataSource komp2DataSource;

	@Autowired
	@Qualifier("pipelineCore")
	SolrClient pipelineCore;

	private Map<String, ParameterDTO> paramIdToParameter;
	private Map<String, ProcedureDTO> procedureIdToProcedure;
	private Map<String, PipelineDTO> pipelines;
	private Map<String, ObservationType> parameterToObservationTypeMap;
	private Map<String, String> emapToEmapa;
	private OntologyParser mpParser;
	private OntologyParser emapaParser;
	OntologyParserFactory ontologyParserFactory;

	protected static final int MINIMUM_DOCUMENT_COUNT = 10;


	public PipelineIndexer() {

	}


	public static void main(String[] args) throws IndexerException {
		SpringApplication.run(PipelineIndexer.class, args);
	}


	@Override
	public RunStatus validateBuild()	throws IndexerException {
		return super.validateBuild(pipelineCore);
	}


	private void initialiseSupportingBeans(RunStatus runStatus)
			throws IndexerException{

		try {
			ontologyParserFactory = new OntologyParserFactory(komp2DataSource, owlpath);
			mpParser = ontologyParserFactory.getMpParser();
			emapaParser = ontologyParserFactory.getEmapaParser();
			emapToEmapa = ontologyParserFactory.getEmapToEmapaMap();
			parameterToObservationTypeMap = getObservationTypeMap(runStatus);
			paramIdToParameter = populateParamIdToParameterMap(runStatus);
			addUnits();
			procedureIdToProcedure = populateProcedureIdToProcedureMap(runStatus);
			pipelines = populatePipelineList();
			addAbnormalMaOntology();
			addAbnormalEmapOntology();
		} catch (SQLException | OWLOntologyCreationException | OWLOntologyStorageException | IOException e){
			throw new IndexerException(e);
		}
	}


	@Override
	public RunStatus run() throws IndexerException, SQLException, IOException, SolrServerException {
        documentCount = 0;
        Set<String> noTermSet = new HashSet<>();
        RunStatus runStatus = new RunStatus();
		long start = System.currentTimeMillis();

		try {

			this.komp2DbConnection = komp2DataSource.getConnection();

			initialiseSupportingBeans(runStatus);
			pipelineCore.deleteByQuery("*:*");
			pipelineCore.commit();

			for (PipelineDTO pipeline : pipelines.values()) {

				for (ProcedureDTO procedure : pipeline.getProcedures()){

					List<ParameterDTO> parameters = procedure.getParameters();

					for (ParameterDTO param : parameters) {

						ImpressDTO doc = new ImpressDTO();
						doc.setParameterId(param.getId());
						doc.setParameterName(param.getName());
						doc.setParameterStableId(param.getStableId());
						doc.setParameterStableKey(param.getStableKey());

						doc.setProcedureId(procedure.getId());
						doc.setProcedureName(procedure.getName());
						doc.setProcedureStableId(procedure.getStableId());
						doc.setProcedureStableKey(procedure.getStableKey());

						doc.setPipelineId(pipeline.getId());
						doc.setPipelineName(pipeline.getName());
						doc.setPipelineStableId(pipeline.getStableId());
						doc.setPipelineStableKey(pipeline.getStableKey());

						// ididid to be pipe proc param stable id combination that should be unique and is unique in solr
						String ididid = pipeline.getStableId() + "_" + procedure.getStableId() + "_" + param.getStableId();
						doc.setIdIdId(ididid);

						doc.setRequired(procedure.isRequired());
						//doc.setDescription(procBean.description); -> maybe we don't need this. If we do, should differentiate from parameter description.
						doc.setObservationType(param.getObservationType().name());
						if (param.getUnitX() != null){
							doc.setUnitX(param.getUnitX());
						}
						if (param.getUnitY() != null){
							doc.setUnitY(param.getUnitY());
						}
						doc.setMetadata(param.isMetadata());
						doc.setIncrement(param.isIncrement());
						doc.setHasOptions(param.isOptions());
						doc.setDerived(param.isDerived());
						doc.setMedia(param.isMedia());
						doc.setAnnotate(param.isAnnotate());

						if (param.getCategories().size() > 0){
							doc.setCategories(param.getCategories());
						}

						if(param.getMaId() != null){
							doc.setMaId(param.getMaId());
							doc.setMaName(param.getMaName());
						}

						if (param.getMpIds().size() > 0){
							for (String mpId : param.getMpIds()){
								doc.addMpId(mpId);
								OntologyTermDTO mp = mpParser.getOntologyTerm(mpId);
								if (mp == null) {
									noTermSet.add(pipeline.getName() + ":" + procedure.getName() + ":" + mpId);
									continue;
								}

								doc.addMpTerm(mp.getName());
								if (mp.getIntermediateIds() != null && mp.getIntermediateIds().size() > 0){
									doc.addIntermediateMpId(mp.getIntermediateIds());
									doc.addIntermediateMpTerm(mp.getIntermediateNames());
								}
								if (mp.getTopLevelIds() != null && mp.getTopLevelIds().size() > 0){
									doc.addTopLevelMpId(mp.getTopLevelIds());
									doc.addTopLevelMpTerm(mp.getTopLevelNames());
								}
							}
						}

						if (param.getAbnormalMpId() != null){
							doc.setAbnormalMpId(new ArrayList<String>(param.getAbnormalMpId()));
							for (String mpId: param.getAbnormalMpId()){
								try {
									doc.addAbnormalMpTerm(mpParser.getOntologyTerm(mpId).getName());
								} catch (NullPointerException e) {
									logger.warn(" Cannot get information from mpIdToMp map for id {}", mpId);
								}
							}
						}
						if (param.getIncreasedMpId() != null){
							doc.setIncreasedMpId(new ArrayList<String>(param.getIncreasedMpId()));
							for(String mpId: param.getIncreasedMpId()){
								try {
									doc.addIncreasedMpTerm(mpParser.getOntologyTerm(mpId).getName());
								} catch (NullPointerException e) {
									logger.warn(" Cannot get information from mpIdToMp map for id {}", mpId);
								}
							}
						}
						if (param.getDecreasedMpId()!= null){
							doc.setDecreasedMpId(new ArrayList<String>(param.getDecreasedMpId()));
							for(String mpId: param.getDecreasedMpId()){
								if (mpParser.getOntologyTerm(mpId) != null) {
									try {
										doc.addDecreasedMpTerm(mpParser.getOntologyTerm(mpId).getName());
									} catch (NullPointerException e) {
										logger.warn(" Cannot get information from mpIdToMp map for id {}", mpId);
									}
								} else {
									logger.warn(" Cannot find MP term for MP ID {}", mpId);
								}
							}
						}

						if (doc.getProcedureId() == null){
							System.out.println(doc.getIdidid() + "  " + doc);
						}

						if(param.getEmapId()!=null){

							String emapId = param.getEmapId();
							doc.setEmapId(emapId);

							if ( emapToEmapa.containsKey(emapId)) {
								OntologyTermDTO emapaTerm = emapaParser.getOntologyTerm(emapToEmapa.get(emapId));
								doc.setAnatomyId(emapaTerm.getAccessionId());
								doc.setAnatomyTerm(emapaTerm.getName());
							}
							else {
								logger.debug(" EMAP Id {} is not mapped to an EMAPA Id", emapId);
							}
						}
						pipelineCore.addBean(doc);
						documentCount++;
					}
				}
			}

			List<String> noTermList = new ArrayList<>(noTermSet);
			Collections.sort(noTermList);
			for (String mpId : noTermList) {
                runStatus.addWarning( "No mp term for " + mpId);
			}

			pipelineCore.commit();

		} catch (IOException | SolrServerException | NullPointerException e) {
			e.printStackTrace();
			throw new IndexerException(e);
		}

		if (runStatus.hasWarnings()) {
            runStatus.addWarning("No mp term COUNT: " + noTermSet.size());
        }

        logger.info(" Added {} total beans in {}", documentCount, commonUtils.msToHms(System.currentTimeMillis() - start));
		return runStatus;
	}

    /**
     * Populate ParamDbIdToParameter
     *
     * @param runStatus instance to which warnings and errors are added
     *
     * @return ParamDbIdToParameter map
     */
	protected Map<String, ParameterDTO> populateParamIdToParameterMap(RunStatus runStatus) {

		Map<String, ParameterDTO> localParamDbIdToParameter = new HashMap<>();
		String queryString = "SELECT * FROM phenotype_parameter";

		try (PreparedStatement p = komp2DbConnection.prepareStatement(queryString)) {
			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				ParameterDTO param = new ParameterDTO();
				// store the row in a map of column names to values
				String id = resultSet.getString("stable_id");
				param.setName(resultSet.getString("name"));
				param.setId(resultSet.getInt("id"));
				param.setStableId(resultSet.getString("stable_id"));
				param.setStableKey(resultSet.getInt("stable_key"));
				param.setDataType(resultSet.getString("datatype"));
				param.setParameterType(resultSet.getString("parameter_type"));
				param.setMetadata(resultSet.getBoolean("metadata"));
				param.setDerived(resultSet.getBoolean("derived"));
				param.setRequired(resultSet.getBoolean("required"));
				param.setIncrement(resultSet.getBoolean("increment"));
				param.setOptions(resultSet.getBoolean("options"));
				param.setMedia(resultSet.getBoolean("media"));
				param.setAnnotate(resultSet.getBoolean("annotate"));
				param.setObservationType(assignType(param, runStatus));
				if (param.getObservationType() == null){
                    runStatus.addWarning(" Observation type is NULL for :" + param.getStableId() + "  " + param.getObservationType());
				}
				localParamDbIdToParameter.put(id, param);
			}

            if (localParamDbIdToParameter.size() < 5000) {
                runStatus.addWarning(" localParamDbIdToParameter # records = " + localParamDbIdToParameter.size() + ". Expected at least 5000 records.");
            }

		} catch (Exception e) {
			e.printStackTrace();
		}

		localParamDbIdToParameter = addCategories(localParamDbIdToParameter);
		localParamDbIdToParameter = addMpTerms(localParamDbIdToParameter);
		return localParamDbIdToParameter;

	}

	private void addUnits() {

		String query = "SELECT * FROM phenotype_parameter pp LEFT OUTER JOIN phenotype_parameter_lnk_increment ppli on pp.id = ppli.parameter_id " +
				"LEFT OUTER JOIN phenotype_parameter_increment ppi ON ppli.increment_id = ppi.id ORDER BY pp.stable_id; ";

		try (PreparedStatement p = komp2DbConnection.prepareStatement(query)) {

			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				ParameterDTO param = paramIdToParameter.get(resultSet.getString("stable_id"));
				if (resultSet.getString("increment_unit") != null) {
					param.setUnitX(resultSet.getString("increment_unit"));
					param.setUnitY(resultSet.getString("unit"));
				} else {
					param.setUnitX(resultSet.getString("unit"));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
		/**
	 * @since 2015/07/27
	 * @author tudose
	 * @param stableIdToParameter
	 * @return
	 */
	protected Map<String, ParameterDTO> addCategories(Map<String, ParameterDTO> stableIdToParameter){

		Map<String, ParameterDTO> localIdToParameter = new HashMap<>(stableIdToParameter);
		String queryString = "SELECT stable_id, o.name AS cat_name, o.description AS cat_description "
				+ " FROM phenotype_parameter p "
				+ " INNER JOIN phenotype_parameter_lnk_option l ON l.parameter_id=p.id "
				+ " INNER JOIN phenotype_parameter_option o ON o.id=l.option_id "
				+ " ORDER BY stable_id ASC;";

		try (PreparedStatement p = komp2DbConnection.prepareStatement(queryString)) {

			ResultSet resultSet = p.executeQuery();
			ParameterDTO param = null;

			while (resultSet.next()) {

				String paramId = resultSet.getString("stable_id");
				if (param == null || !param.getStableId().equalsIgnoreCase(paramId)){
					param = stableIdToParameter.get(paramId);
				}

				param.addCategories(getCategory(resultSet));
				localIdToParameter.put(param.getStableId(), param);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return localIdToParameter;
	}

	/**
	 * @since 2015/07/27
	 * @author tudose
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
	protected String getCategory (ResultSet resultSet)
	throws SQLException{

		String name = resultSet.getString("cat_name");
		String description = resultSet.getString("cat_description");
		if (name.matches("[0-9]+")){
			return description;
		}

		return name;

	}


	/**
	 * @since 2015/07/27
	 * @author tudose
	 * @param stableIdToParameter
	 * @return
	 */
	protected Map<String, ParameterDTO> addMpTerms(Map<String, ParameterDTO> stableIdToParameter){

		String queryString = "SELECT stable_id, ontology_acc, event_type FROM phenotype_parameter pp "
				+ "	INNER JOIN phenotype_parameter_lnk_ontology_annotation l ON l.parameter_id=pp.id "
				+ " INNER JOIN phenotype_parameter_ontology_annotation ppoa ON l.annotation_id=ppoa.id "
				+ " WHERE ontology_db_id=5 "
				+ " ORDER BY stable_id ASC; ";

		Map<String, ParameterDTO> localIdToParameter = new HashMap<>(stableIdToParameter);

		try (PreparedStatement p = komp2DbConnection.prepareStatement(queryString)) {

			ResultSet resultSet = p.executeQuery();
			ParameterDTO param = null;

			while (resultSet.next()) {

				String paramId = resultSet.getString("stable_id");
				if (param == null || !param.getStableId().equalsIgnoreCase(paramId)) {
					param = stableIdToParameter.get(paramId);
				}

				String type = resultSet.getString("event_type");
				String mpId = resultSet.getString("ontology_acc");
				if (type == null) {
					param.addMpIds(mpId);
				} else if (type.equalsIgnoreCase("abnormal") && (param.getAbnormalMpId() == null || !param.getAbnormalMpId().contains(mpId))){
					param.addAbnormalMpId(mpId);
				} else if(type.equalsIgnoreCase("increased") && (param.getIncreasedMpId() == null ||!param.getIncreasedMpId().contains(mpId))){
					param.addIncreasedMpId(mpId);
				} else if (type.equalsIgnoreCase("decreased") && (param.getDecreasedMpId() == null || !param.getDecreasedMpId().contains(mpId))){
					param.addDecreasedMpId(mpId);
				}

				param.addMpIds(resultSet.getString("ontology_acc"));
				localIdToParameter.put(param.getStableId(), param);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return localIdToParameter;

	}


	protected Map<String, Set<String>> populateProcedureToParameterMap(RunStatus runStatus) {

		Map<String, Set<String>> procIdToParams = new HashMap<>();

		// This is the minumum number of unique procedures produced by this query on 14-Jan-2016, rounded down (GROUP BY procedure_id)
		// Harwell Ageing Screen parameters removed 2016-08-24
        final int MIN_ROW_COUNT = 150;
		String queryString = "SELECT procedure_id, parameter_id, pp.stable_id as parameter_stable_id, pproc.stable_id as procedure_stable_id "
				+ " FROM phenotype_procedure_parameter ppp "
				+ " INNER JOIN phenotype_parameter pp ON pp.id=ppp.parameter_id "
				+ " INNER JOIN phenotype_procedure pproc ON pproc.id=ppp.procedure_id";

		try (PreparedStatement p = komp2DbConnection.prepareStatement(queryString)) {

			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				Set<String> parameterIds = new HashSet<>();
				String paramId = resultSet.getString("parameter_stable_id");
				String procId = resultSet.getString("procedure_stable_id");

				if (procIdToParams.containsKey(procId)) {
					parameterIds = procIdToParams.get(procId);
				}
				parameterIds.add(paramId);
				procIdToParams.put(procId, parameterIds);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

        if (procIdToParams.size() < MIN_ROW_COUNT) {
            runStatus.addWarning(" procIdToParams # records = " + procIdToParams.size() + ". Expected at least " + MIN_ROW_COUNT + " records.");
        }

		return procIdToParams;
	}


	protected Map<String, ProcedureDTO> populateProcedureIdToProcedureMap(RunStatus runStatus) {

		Map<String, Set<String>> procIdToParams = populateProcedureToParameterMap(runStatus);

		Map<String, ProcedureDTO> procedureIdToProcedureMap = new HashMap<>();
		String queryString = "SELECT id as pproc_id, stable_id, name, stable_key, is_mandatory, description, concat(name, '___', stable_id) as proc_name_id "
				+ "FROM phenotype_procedure";

		try (PreparedStatement p = komp2DbConnection.prepareStatement(queryString)) {

			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				ProcedureDTO proc = new ProcedureDTO();
				proc.setStableId(resultSet.getString("stable_id"));
				proc.setId(resultSet.getInt("pproc_id"));
				proc.setName(resultSet.getString("name"));
				proc.setStableKey(resultSet.getInt("stable_key"));
				proc.setProcNameId(resultSet.getString("proc_name_id"));
				proc.setRequired(resultSet.getBoolean("is_mandatory"));
				proc.setDescription(resultSet.getString("description"));
				for (String parameterId : procIdToParams.get(proc.getStableId())){
					proc.addParameter(paramIdToParameter.get(parameterId));
				}
				procedureIdToProcedureMap.put(resultSet.getString("stable_id"), proc);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

        if (procedureIdToProcedureMap.size() < 150) {
            runStatus.addWarning(" procedureIdToProcedureMap # records = " + procedureIdToProcedureMap.size() + ". Expected at least 150 records.");
        }

		return procedureIdToProcedureMap;
	}


	protected Map<String, PipelineDTO> populatePipelineList() {

		Map<String, PipelineDTO> procIdToPipelineMap = new HashMap<>();
		String queryString = "SELECT pproc.stable_id as procedure_stable_id, ppipe.name as pipe_name, ppipe.id as pipe_id, ppipe.stable_id as pipe_stable_id, "
				+ " ppipe.stable_key AS pipe_stable_key, concat(ppipe.name, '___', pproc.name, '___', pproc.stable_id) AS pipe_proc_sid "
				+ " FROM phenotype_procedure pproc INNER JOIN phenotype_pipeline_procedure ppproc ON pproc.id=ppproc.procedure_id "
				+ " INNER JOIN phenotype_pipeline ppipe ON ppproc.pipeline_id=ppipe.id"
				+ " WHERE ppipe.db_id=6 ORDER BY ppipe.id ASC ";

		try (PreparedStatement p = komp2DbConnection.prepareStatement(queryString)) {

			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {

				String pipelineStableId = resultSet.getString("pipe_stable_id");
				PipelineDTO pipe = new PipelineDTO();

				if (procIdToPipelineMap.containsKey(pipelineStableId)){
					pipe = procIdToPipelineMap.get(pipelineStableId);
				}
				pipe.setId(resultSet.getInt("pipe_id"));
				pipe.setName(resultSet.getString("pipe_name"));
				pipe.setStableKey(resultSet.getInt("pipe_stable_key"));
				pipe.setStableId(resultSet.getString("pipe_stable_id"));
				pipe.addProcedure(procedureIdToProcedure.get(resultSet.getString("procedure_stable_id")));
				procIdToPipelineMap.put(pipelineStableId, pipe);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return procIdToPipelineMap;

	}


	protected void addAbnormalMaOntology(){

		String sqlQuery="SELECT pp.id as id, ot.name as name, stable_id, ontology_acc FROM phenotype_parameter pp "
				+ "	INNER JOIN phenotype_parameter_lnk_ontology_annotation pploa ON pp.id = pploa.parameter_id "
				+ " INNER JOIN phenotype_parameter_ontology_annotation ppoa ON ppoa.id = pploa.annotation_id "
				+ " INNER JOIN ontology_term ot ON ot.acc = ppoa.ontology_acc "
				+ " WHERE ppoa.ontology_db_id=8 LIMIT 10000";

		try (PreparedStatement p = komp2DbConnection.prepareStatement(sqlQuery)) {

			ResultSet resultSet = p.executeQuery();
			while (resultSet.next()) {
				String parameterId = resultSet.getString("stable_id");
				paramIdToParameter.get(parameterId).setMaId(resultSet.getString("ontology_acc"));
				paramIdToParameter.get(parameterId).setMaName(resultSet.getString("name"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

protected void addAbnormalEmapOntology(){

		String sqlQuery="SELECT pp.id as id, ot.name as name, stable_id, ontology_acc FROM phenotype_parameter pp "
				+ "	INNER JOIN phenotype_parameter_lnk_ontology_annotation pploa ON pp.id = pploa.parameter_id "
				+ " INNER JOIN phenotype_parameter_ontology_annotation ppoa ON ppoa.id = pploa.annotation_id "
				+ " INNER JOIN ontology_term ot ON ot.acc = ppoa.ontology_acc "
				+ " WHERE ppoa.ontology_db_id=14";
		//14 db id is emap

		try (PreparedStatement p = komp2DbConnection.prepareStatement(sqlQuery)) {

			ResultSet resultSet = p.executeQuery();
			while (resultSet.next()) {
				String parameterId = resultSet.getString("stable_id");

				paramIdToParameter.get(parameterId).setEmapId(resultSet.getString("ontology_acc"));
				paramIdToParameter.get(parameterId).setEmapName(resultSet.getString("name"));

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	/**@since 2015
	 * @author tudose
	 * @param parameter
     * @param runStatus a valid <code>RunStatus</code> instance
	 * @return
	 * @throws SolrServerException
	 */
	// Method copied from org.mousephenotype.cda.db.impress.Utilities.
	// Adjusted to avoid use of Parameter entity obj.
	// Method should only be used at indexing time. After that query pipeline core to find type.
	protected ObservationType assignType(ParameterDTO parameter, RunStatus runStatus)
	throws SolrServerException {

		Map<String, String> MAPPING = new HashMap<>();
		MAPPING.put("M-G-P_022_001_001_001", "FLOAT");
		MAPPING.put("M-G-P_022_001_001", "FLOAT");
		MAPPING.put("ESLIM_006_001_035", "FLOAT");
		MAPPING = Collections.unmodifiableMap(MAPPING);

		ObservationType observationType = null;
		String datatype = parameter.getDataType();

		if (MAPPING.containsKey(parameter.getStableId())) {
			datatype = MAPPING.get(parameter.getStableId());
		}

		if (parameter.isMetadata()) {

			observationType = ObservationType.metadata;

		} else {

			if (parameter.isOptions()) {

				observationType = ObservationType.categorical;

			} else {

				if (datatype.equals("TEXT")) {

					observationType = ObservationType.text;

				} else if (datatype.equals("DATETIME") || datatype.equals("DATE")  || datatype.equals("TIME")) {

					observationType = ObservationType.datetime;

				} else if (datatype.equals("BOOL")) {

					observationType = ObservationType.categorical;

				} else if (datatype.equals("FLOAT") || datatype.equals("INT")) {

					if (parameter.isIncrement()) {

						observationType = ObservationType.time_series;

					} else {

						observationType = ObservationType.unidimensional;

					}

				} else if (datatype.equals("IMAGE") || (datatype.equals("") && parameter.getName().contains("images"))) {

					observationType = ObservationType.image_record;

				} else if (datatype.equals("") && !parameter.isOptions() && !parameter.getName().contains("images")) {

					/* Look up in observation core. If we have a value the observation type will be correct.
					 * If not use the approximation below (categorical will always be missed).
					 * See declaration of checkType(param, value) in impress utils.
					 */
					// Do not use the Service here because it has likely not been loaded yet (or is pointing to the wrong context
					// ObservationType obs = os.getObservationTypeForParameterStableId(parameter.getStableId());
//					logger.info("  Getting type from observations for parameter {}: Got {}", parameter.getStableId(), parameterToObservationTypeMap.get(parameter.getStableId()));
					ObservationType obs = parameterToObservationTypeMap.get(parameter.getStableId());
					if (obs != null){
						observationType = obs;
					} else {
						if (parameter.isIncrement()) {
							observationType = ObservationType.time_series;
						} else {
							observationType = ObservationType.unidimensional;
						}
					}

				} else {
					runStatus.addWarning(" Unknown data type : " + datatype  + " " + parameter.getStableId());
				}
			}
		}

		return observationType;
	}

	private Map<String,ObservationType> getObservationTypeMap(RunStatus runStatus){

		Map<String,ObservationType> map = new HashMap<>();
		String query= "select distinct parameter_stable_id, observation_type from observation where observation.missing != 1";

		try (PreparedStatement p = komp2DbConnection.prepareStatement(query)) {

			ResultSet resultSet = p.executeQuery();
			while (resultSet.next()) {
				String parameterId = resultSet.getString("parameter_stable_id");
				String obsType=resultSet.getString("observation_type");

				ObservationType obType;
				try {
					obType = ObservationType.valueOf(obsType);
					map.put(parameterId, obType);
				} catch (IllegalArgumentException e) {
					runStatus.addWarning(" No ObservationType found for parameter: " + parameterId);
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;
	}
}
