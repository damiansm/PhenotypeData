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
package org.mousephenotype.cda.solr.service.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

public class ImageDTO extends ObservationDTO {


	public final static String ID = ObservationDTO.ID;
	public final static String DATASOURCE_ID = ObservationDTO.DATASOURCE_ID;
	public final static String DATASOURCE_NAME = ObservationDTO.DATASOURCE_NAME;
	public final static String PROJECT_ID = ObservationDTO.PROJECT_ID;
	public final static String PROJECT_NAME = ObservationDTO.PROJECT_NAME;
	public final static String PHENOTYPING_CENTER = ObservationDTO.PHENOTYPING_CENTER;
	public final static String PHENOTYPING_CENTER_ID = ObservationDTO.PHENOTYPING_CENTER_ID;
	public final static String GENE_ACCESSION_ID = ObservationDTO.GENE_ACCESSION_ID;
	public final static String GENE_SYMBOL = ObservationDTO.GENE_SYMBOL;
	public final static String ALLELE_ACCESSION_ID = ObservationDTO.ALLELE_ACCESSION_ID;
	public final static String ALLELE_SYMBOL = ObservationDTO.ALLELE_SYMBOL;
	public final static String ZYGOSITY = ObservationDTO.ZYGOSITY;
	public final static String SEX = ObservationDTO.SEX;
	public final static String BIOLOGICAL_MODEL_ID = ObservationDTO.BIOLOGICAL_MODEL_ID;
	public final static String BIOLOGICAL_SAMPLE_ID = ObservationDTO.BIOLOGICAL_SAMPLE_ID;
	public final static String BIOLOGICAL_SAMPLE_GROUP = ObservationDTO.BIOLOGICAL_SAMPLE_GROUP;
	public final static String STRAIN_ACCESSION_ID = ObservationDTO.STRAIN_ACCESSION_ID;
	public final static String STRAIN_NAME = ObservationDTO.STRAIN_NAME;
	public final static String GENETIC_BACKGROUND = ObservationDTO.GENETIC_BACKGROUND;
	public final static String PIPELINE_NAME = ObservationDTO.PIPELINE_NAME;
	public final static String PIPELINE_ID = ObservationDTO.PIPELINE_ID;
	public final static String PIPELINE_STABLE_ID = ObservationDTO.PIPELINE_STABLE_ID;
	public final static String PROCEDURE_ID = ObservationDTO.PROCEDURE_ID;
	public final static String PROCEDURE_NAME = ObservationDTO.PROCEDURE_NAME;
	public final static String PROCEDURE_STABLE_ID = ObservationDTO.PROCEDURE_STABLE_ID;
	public final static String PROCEDURE_GROUP = ObservationDTO.PROCEDURE_GROUP;
	public final static String PARAMETER_ID = ObservationDTO.PARAMETER_ID;
	public final static String PARAMETER_NAME = ObservationDTO.PARAMETER_NAME;
	public final static String PARAMETER_STABLE_ID = ObservationDTO.PARAMETER_STABLE_ID;
	public final static String EXPERIMENT_ID = ObservationDTO.EXPERIMENT_ID;
	public final static String EXPERIMENT_SOURCE_ID = ObservationDTO.EXPERIMENT_SOURCE_ID;
	public final static String OBSERVATION_TYPE = ObservationDTO.OBSERVATION_TYPE;
	public final static String COLONY_ID = ObservationDTO.COLONY_ID;
	public final static String DATE_OF_BIRTH = ObservationDTO.DATE_OF_BIRTH;
	public final static String DATE_OF_EXPERIMENT = ObservationDTO.DATE_OF_EXPERIMENT;
	public final static String POPULATION_ID = ObservationDTO.POPULATION_ID;
	public final static String EXTERNAL_SAMPLE_ID = ObservationDTO.EXTERNAL_SAMPLE_ID;
	public final static String DATA_POINT = ObservationDTO.DATA_POINT;
	public final static String ORDER_INDEX = ObservationDTO.ORDER_INDEX;
	public final static String DIMENSION = ObservationDTO.DIMENSION;
	public final static String TIME_POINT = ObservationDTO.TIME_POINT;
	public final static String DISCRETE_POINT = ObservationDTO.DISCRETE_POINT;
	public final static String CATEGORY = ObservationDTO.CATEGORY;
	public final static String VALUE = ObservationDTO.VALUE;
	public final static String METADATA = ObservationDTO.METADATA;
	public final static String METADATA_GROUP = ObservationDTO.METADATA_GROUP;
	public final static String DOWNLOAD_FILE_PATH = ObservationDTO.DOWNLOAD_FILE_PATH;
	public final static String FILE_TYPE = ObservationDTO.FILE_TYPE;
	public final static String PARAMETER_ASSOCIATION_STABLE_ID = ObservationDTO.PARAMETER_ASSOCIATION_STABLE_ID;
	public final static String PARAMETER_ASSOCIATION_SEQUENCE_ID = ObservationDTO.PARAMETER_ASSOCIATION_SEQUENCE_ID;
	public final static String PARAMETER_ASSOCIATION_DIM_ID = ObservationDTO.PARAMETER_ASSOCIATION_DIM_ID;
	public final static String PARAMETER_ASSOCIATION_NAME = ObservationDTO.PARAMETER_ASSOCIATION_NAME;
	public final static String PARAMETER_ASSOCIATION_VALUE = ObservationDTO.PARAMETER_ASSOCIATION_VALUE;
	public final static String WEIGHT_PARAMETER_STABLE_ID = ObservationDTO.WEIGHT_PARAMETER_STABLE_ID;
	public final static String WEIGHT_DATE = ObservationDTO.WEIGHT_DATE;
	public final static String WEIGHT_DAYS_OLD = ObservationDTO.WEIGHT_DAYS_OLD;
	public final static String WEIGHT = ObservationDTO.WEIGHT;

//	public static final String ANATOMY_ID = ObservationDTO.ANATOMY_ID;
//	public static final String ANATOMY_TERM = ObservationDTO.ANATOMY_TERM;
//	public static final String ANATOMY_ID_TERM = ObservationDTO.ANATOMY_ID_TERM;
//	public static final String ANATOMY_TERM_SYNONYM = ObservationDTO.ANATOMY_TERM_SYNONYM;
//	public static final String INTERMEDIATE_ANATOMY_ID = ObservationDTO.INTERMEDIATE_ANATOMY_ID;
//	public static final String INTERMEDIATE_ANATOMY_TERM = ObservationDTO.INTERMEDIATE_ANATOMY_TERM;
//	public static final String INTERMEDIATE_ANATOMY_TERM_SYNONYM = ObservationDTO.INTERMEDIATE_ANATOMY_TERM_SYNONYM;
//	public static final String TOP_LEVEL_ANATOMY_ID = ObservationDTO.TOP_LEVEL_ANATOMY_ID;
//	public static final String TOP_LEVEL_ANATOMY_TERM = ObservationDTO.TOP_LEVEL_ANATOMY_TERM;
//	public static final String TOP_LEVEL_ANATOMY_TERM_SYNONYM = ObservationDTO.TOP_LEVEL_ANATOMY_TERM_SYNONYM;
//	public static final String SELECTED_TOP_LEVEL_ANATOMY_ID = ObservationDTO.SELECTED_TOP_LEVEL_ANATOMY_ID;
//	public static final String SELECTED_TOP_LEVEL_ANATOMY_TERM = ObservationDTO.SELECTED_TOP_LEVEL_ANATOMY_TERM;
//	public static final String SELECTED_TOP_LEVEL_ANATOMY_TERM_SYNONYM = ObservationDTO.SELECTED_TOP_LEVEL_ANATOMY_TERM_SYNONYM;

//	public static final String MA_ID = ObservationDTO.MA_ID;
//	public static final String MA_TERM = ObservationDTO.MA_TERM;
//	public static final String MA_ID_TERM = "ma_id_term";
//	public static final String MA_TERM_SYNONYM = ObservationDTO.MA_TERM_SYNONYM;
//	public static final String INTERMEDIATE_MA_ID = ObservationDTO.INTERMEDIATE_MA_ID;
//	public static final String INTERMEDIATE_MA_TERM = ObservationDTO.INTERMEDIATE_MA_TERM;
//	public static final String INTERMEDIATE_MA_TERM_SYNONYM = ObservationDTO.INTERMEDIATE_MA_TERM_SYNONYM;
//	public static final String SELECTED_TOP_LEVEL_MA_ID = ObservationDTO.SELECTED_TOP_LEVEL_MA_ID;
//	public static final String SELECTED_TOP_LEVEL_MA_TERM = ObservationDTO.SELECTED_TOP_LEVEL_MA_TERM;
//	public static final String SELECTED_TOP_LEVEL_MA_TERM_SYNONYM = ObservationDTO.SELECTED_TOP_LEVEL_MA_TERM_SYNONYM;
//
//	public static final String EMAP_ID = ObservationDTO.EMAP_ID;
//	public static final String EMAP_TERM = ObservationDTO.EMAP_TERM;
//	public static final String EMAP_ID_TERM = "emap_id_term";
//	public static final String EMAP_TERM_SYNONYM = ObservationDTO.EMAP_TERM_SYNONYM;
//	public static final String INTERMEDIATE_EMAP_ID = ObservationDTO.INTERMEDIATE_EMAP_ID;
//	public static final String INTERMEDIATE_EMAP_TERM = ObservationDTO.INTERMEDIATE_EMAP_TERM;
//	public static final String INTERMEDIATE_EMAP_TERM_SYNONYM = ObservationDTO.INTERMEDIATE_EMAP_TERM_SYNONYM;
//	public static final String SELECTED_TOP_LEVEL_EMAP_ID = ObservationDTO.SELECTED_TOP_LEVEL_EMAP_ID;
//	public static final String SELECTED_TOP_LEVEL_EMAP_TERM = ObservationDTO.SELECTED_TOP_LEVEL_EMAP_TERM;
//	public static final String SELECTED_TOP_LEVEL_EMAP_TERM_SYNONYM = ObservationDTO.SELECTED_TOP_LEVEL_EMAP_TERM_SYNONYM;


	public static final String FULL_RESOLUTION_FILE_PATH="full_resolution_file_path";

	public static final String OMERO_ID = "omero_id";
	public static final String DOWNLOAD_URL = "download_url";
	public static final String JPEG_URL = "jpeg_url";
	public static final String IMAGE_LINK = "image_link";

	public static final String EFO_ID = "efo_id";
	public static final String UBERON_ID = "uberon_id";

	public static final String SYMBOL_GENE = "symbol_gene";
	public static final String SYMBOL = "symbol";
	public static final String SUBTYPE = "subtype";
	public static final String STATUS = "status";
	public static final String IMITS_PHENOTYPE_STARTED = SangerImageDTO.IMITS_PHENOTYPE_STARTED;
	public static final String IMITS_PHENOTYPE_COMPLETE = SangerImageDTO.IMITS_PHENOTYPE_COMPLETE;
	public static final String IMITS_PHENOTYPE_STATUS = SangerImageDTO.IMITS_PHENOTYPE_STATUS;
	public static final String LEGACY_PHENOTYPE_STATUS = AlleleDTO.LEGACY_PHENOTYPE_STATUS;
	public static final String LATEST_PRODUCTION_CENTRE = SangerImageDTO.LATEST_PRODUCTION_CENTRE;
	public static final String LATEST_PHENOTYPING_CENTRE = SangerImageDTO.LATEST_PHENOTYPING_CENTRE;
	public static final String ALLELE_NAME = SangerImageDTO.ALLELE_NAME;
	public static final String MARKER_SYMBOL = SangerImageDTO.MARKER_SYMBOL;
	public static final String MARKER_NAME = SangerImageDTO.MARKER_NAME;
	public static final String MARKER_SYNONYM = SangerImageDTO.MARKER_SYNONYM;
	public static final String MARKER_TYPE = SangerImageDTO.MARKER_TYPE;
	public static final String HUMAN_GENE_SYMBOL = SangerImageDTO.HUMAN_GENE_SYMBOL;
	public static final String LATEST_PHENOTYPE_STATUS = AlleleDTO.LATEST_PHENOTYPE_STATUS;

	public static final String MP_ID_TERM = "mp_id_term";

	public static final String INCREMENT_VALUE = "increment_value";
	public static final String STAGE = "stage";


//	@Field(ANATOMY_ID)
//	private ArrayList<String> anatomyId;
//
//	@Field(ANATOMY_TERM)
//	private List<String> anatomyTerm;
//
//	@Field(ANATOMY_ID_TERM)
//	private List<String> anatomyIdTerm;
//
//	@Field(ANATOMY_TERM_SYNONYM)
//	private List<String> anatomyTermSynonym;


//	@Field(MA_ID_TERM)
//	private List<String> maIdTerm;
//
//	@Field(EMAP_ID_TERM)
//	private List<String> emapIdTerm;

	@Field(MpDTO.MP_ID)
	private ArrayList<String> mpTermIds;

	@Field(MpDTO.MP_TERM)
	private List<String> mpTerm;

	@Field(MP_ID_TERM)
	private List<String> mpIdTerm;

	@Field(MpDTO.MP_TERM_SYNONYM)
	private List<String> mpTermSynonym;

	public List<String> getMpIdTerm() {
		return mpIdTerm;
	}

	public void setMpIdTerm(List<String> mpIdTerm) {
		this.mpIdTerm = mpIdTerm;
	}

//	public List<String> getEmapIdTerm() {
//		return emapIdTerm;
//	}
//
//	public void setEmapIdTerm(List<String> emapIdTerm) {
//		this.emapIdTerm = emapIdTerm;
//	}

	public ArrayList<String> getMpTermIds() {
		return mpTermIds;
	}

	public void setMpTermIds(ArrayList<String> mpTermIds) {
		this.mpTermIds = mpTermIds;
	}

	public List<String> getMpTerm() {
		return mpTerm;
	}

	public void setMpTerm(List<String> mpTerm) {
		this.mpTerm = mpTerm;
	}


	public List<String> getMpTermSynonym() {
		return mpTermSynonym;
	}

	public void setMpTermSynonym(List<String> mpTermSynonym) {
		this.mpTermSynonym = mpTermSynonym;
	}



	@Field(FULL_RESOLUTION_FILE_PATH)
	private String fullResolutionFilePath;

	@Field(OMERO_ID)
	private int omeroId;

	@Field(DOWNLOAD_URL)
	private String downloadUrl;

	@Field(IMAGE_LINK)
	private String imageLink;

	@Field(JPEG_URL)
	private String jpegUrl;

	@Field(EFO_ID)
	private List<String> efoId;

	@Field(UBERON_ID)
	private List<String> uberonId;



	@Field(SYMBOL_GENE)
	private String symbolGene;//for search and annotation view

	@Field(STATUS)
	private List<String> status;

	@Field(IMITS_PHENOTYPE_STARTED)
	private List<String> imitsPhenotypeStarted;

	@Field(IMITS_PHENOTYPE_COMPLETE)
	private List<String> imitsPhenotypeComplete;

	@Field(IMITS_PHENOTYPE_STATUS)
	private List<String> imitsPhenotypeStatus;

	@Field(LEGACY_PHENOTYPE_STATUS)
	private Integer legacyPhenotypeStatus;

	@Field(LATEST_PRODUCTION_CENTRE)
	private List<String> latestProductionCentre;

	@Field(LATEST_PHENOTYPING_CENTRE)
	private List<String> latestPhenotypingCentre;

	@Field(ALLELE_NAME)
	private List<String> alleleName;

	@Field(MARKER_SYMBOL)
	private List<String> markerSymbol;

	@Field(MARKER_NAME)
	private List<String> markerName;

	@Field(MARKER_SYNONYM)
	private List<String> markerSynonym;

	@Field(MARKER_TYPE)
	private String markerType;

	@Field(HUMAN_GENE_SYMBOL)
	private List<String> humanGeneSymbol;

	@Field(SYMBOL)
	private String symbol;

	@Field(SUBTYPE)
	private String subtype;

	@Field(INCREMENT_VALUE)
	private Integer increment;

	@Field(STAGE)
	private String stage;

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public int getIncrement() {
		return increment;
	}

	public void setIncrement(int increment) {
		this.increment = increment;
	}

	@Field(LATEST_PHENOTYPE_STATUS)
	private List<String> latestPhenotypeStatus;


	public String getImageLink() {
		return imageLink;
	}

	public void setImageLink(String imageLink) {
		this.imageLink = imageLink;
	}

	public String getSubtype() {

		return subtype;
	}

	public List<String> getMarkerName() {

		return markerName;
	}

	public void setMarkerName(List<String> markerName) {

		this.markerName = markerName;
	}

	public List<String> getMarkerSymbol() {

		return markerSymbol;
	}

	public void setMarkerSymbol(List<String> markerSymbol) {

		this.markerSymbol = markerSymbol;
	}

	public void setSubtype(String subtype) {

		this.subtype = subtype;
	}

	public String getSymbolGene() {
		if((this.getGeneSymbol()!=null)&&(this.getGeneAccession()!=null)){
			this.symbolGene=this.getGeneSymbol()+"_"+this.getGeneAccession();
			}
		return this.symbolGene;
	}


	public List<String> getEfoId() {

		return efoId;
	}

	public void setEfoId(List<String> efoId) {

		this.efoId = efoId;
	}

	public void addEfoId(String id) {
		if(this.efoId==null){
			this.efoId=new ArrayList<>();
		}
		this.efoId.add(id);
	}

	public List<String> getUberonId() {

		return uberonId;
	}

	public void setUberonId(List<String> uberonId) {

		this.uberonId = uberonId;
	}

	public void addUberonId(String id) {
		if(this.uberonId==null){
			this.uberonId=new ArrayList<>();
		}
		this.uberonId.add(id);
	}

//	public List<String> getMaIdTerm() {
//
//		return maIdTerm;
//	}
//
//	public void setMaIdTerm(List<String> maIdTerms) {
//
//		this.maIdTerm = maIdTerms;
//	}

	public String getDownloadUrl() {

		return downloadUrl;
	}

	public int getOmeroId() {

		return omeroId;
	}

	public void setOmeroId(int omeroId) {

		this.omeroId = omeroId;
	}

	public String getFullResolutionFilePath() {

		return fullResolutionFilePath;
	}

	public void setFullResolutionFilePath(String fullResolutionFilePath) {

		this.fullResolutionFilePath = fullResolutionFilePath;
	}

	public ImageDTO(){
		super();
	}

	public void setDownloadUrl(String downloadUrl) {

		this.downloadUrl=downloadUrl;
	}

	public void setJpegUrl(String jpegUrl) {

		this.jpegUrl=jpegUrl;
	}

	public String getJpegUrl() {

		return jpegUrl;
	}

	public void addStatus(String status1) {

		if(this.status==null){
			status=new ArrayList<String>();
		}
		status.add(status1);
	}

	public void addImitsPhenotypeStarted(String imitsPhenotypeStarted1) {

		if(this.imitsPhenotypeStarted==null){
			this.imitsPhenotypeStarted=new ArrayList<String>();
		}
		this.imitsPhenotypeStarted.add(imitsPhenotypeStarted1);
	}

	public void addImitsPhenotypeComplete(String imitsPhenotypeComplete1) {

		if(this.imitsPhenotypeComplete==null){
			this.imitsPhenotypeComplete=new ArrayList<String>();
		}
		this.imitsPhenotypeComplete.add(imitsPhenotypeComplete1);
	}

	public void addImitsPhenotypeStatus(String imitsPhenotypeStatus1) {

		if(this.imitsPhenotypeStatus==null){
			this.imitsPhenotypeStatus=new ArrayList<String>();
		}
		this.imitsPhenotypeStatus.add(imitsPhenotypeStatus1);
	}

	public void setLegacyPhenotypeStatus(Integer legacyPhenotypeStatus) {

		this.legacyPhenotypeStatus=legacyPhenotypeStatus;
	}

	public Integer getLegacyPhenotypeStatus() {

		return legacyPhenotypeStatus;
	}

	public void setLatestProductionCentre(List<String> latestProductionCentre) {

		this.latestProductionCentre=latestProductionCentre;
	}

	public void setLatestPhenotypingCentre(List<String> latestPhenotypingCentre) {

		this.latestPhenotypingCentre=latestPhenotypingCentre;
	}

	public void setAlleleName(List<String> alleleName) {

		this.alleleName=alleleName;
	}

	public void addMarkerName(String markerName) {
		if(this.markerName==null){
			this.markerName=new ArrayList<>();
		}
		this.markerName.add(markerName);
	}

	public void addMarkerSynonym(List<String> markerSynonym) {
		if(this.markerSynonym==null){
			this.markerSynonym=new ArrayList<>();
		}
		this.markerSynonym.addAll(markerSynonym);
	}

	public void addMarkerType(String markerType) {

		this.markerType=markerType;
	}

	public void addHumanGeneSymbol(List<String> humanGeneSymbol) {

		if(this.humanGeneSymbol==null){
			this.humanGeneSymbol=new ArrayList<String>();
		}
		this.humanGeneSymbol.addAll(humanGeneSymbol);
	}

	public void addSymbol(String markerName) {

		this.symbol=markerName;
	}

	public void setLatestPhenotypeStatus(List<String> latestPhenotypeStatus) {

		this.latestPhenotypeStatus=latestPhenotypeStatus;
	}

	public void addLatestPhenotypeStatus(String latestPhenotypeStatus) {
		if(this.latestPhenotypeStatus==null){
			this.latestPhenotypeStatus=new ArrayList<String>();
		}
		this.latestPhenotypeStatus.add(latestPhenotypeStatus);
	}

	public String getSymbol() {
		// TODO Auto-generated method stub
		return symbol;
	}

	public void setSymbolGene(String symbolGene) {
		this.symbolGene=symbolGene;

	}

//	public List<String> getMaTerm() {
//		return maTerm;
//	}
//
//	public void setMaTerm(List<String> maTerm) {
//		this.maTerm = maTerm;
//	}
//
//	public List<String> getMaTermSynonym() {
//		return maTermSynonym;
//	}
//
//	public void setMaTermSynonym(List<String> maTermSynonym) {
//		this.maTermSynonym = maTermSynonym;
//	}

	public String getExpression(String maId){

		int pos = maId.indexOf(maId);
		return getParameterAssociationValue().get(pos);

	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ImageDTO imageDTO = (ImageDTO) o;

		if (omeroId != imageDTO.omeroId) return false;
		if (mpTermIds != null ? !mpTermIds.equals(imageDTO.mpTermIds) : imageDTO.mpTermIds != null) return false;
		if (mpTerm != null ? !mpTerm.equals(imageDTO.mpTerm) : imageDTO.mpTerm != null) return false;
		if (mpIdTerm != null ? !mpIdTerm.equals(imageDTO.mpIdTerm) : imageDTO.mpIdTerm != null) return false;
		if (mpTermSynonym != null ? !mpTermSynonym.equals(imageDTO.mpTermSynonym) : imageDTO.mpTermSynonym != null)
			return false;
		if (fullResolutionFilePath != null ? !fullResolutionFilePath.equals(imageDTO.fullResolutionFilePath) : imageDTO.fullResolutionFilePath != null)
			return false;
		if (downloadUrl != null ? !downloadUrl.equals(imageDTO.downloadUrl) : imageDTO.downloadUrl != null)
			return false;
		if (imageLink != null ? !imageLink.equals(imageDTO.imageLink) : imageDTO.imageLink != null) return false;
		if (jpegUrl != null ? !jpegUrl.equals(imageDTO.jpegUrl) : imageDTO.jpegUrl != null) return false;
		if (efoId != null ? !efoId.equals(imageDTO.efoId) : imageDTO.efoId != null) return false;
		if (uberonId != null ? !uberonId.equals(imageDTO.uberonId) : imageDTO.uberonId != null) return false;
		if (symbolGene != null ? !symbolGene.equals(imageDTO.symbolGene) : imageDTO.symbolGene != null) return false;
		if (status != null ? !status.equals(imageDTO.status) : imageDTO.status != null) return false;
		if (imitsPhenotypeStarted != null ? !imitsPhenotypeStarted.equals(imageDTO.imitsPhenotypeStarted) : imageDTO.imitsPhenotypeStarted != null)
			return false;
		if (imitsPhenotypeComplete != null ? !imitsPhenotypeComplete.equals(imageDTO.imitsPhenotypeComplete) : imageDTO.imitsPhenotypeComplete != null)
			return false;
		if (imitsPhenotypeStatus != null ? !imitsPhenotypeStatus.equals(imageDTO.imitsPhenotypeStatus) : imageDTO.imitsPhenotypeStatus != null)
			return false;
		if (legacyPhenotypeStatus != null ? !legacyPhenotypeStatus.equals(imageDTO.legacyPhenotypeStatus) : imageDTO.legacyPhenotypeStatus != null)
			return false;
		if (latestProductionCentre != null ? !latestProductionCentre.equals(imageDTO.latestProductionCentre) : imageDTO.latestProductionCentre != null)
			return false;
		if (latestPhenotypingCentre != null ? !latestPhenotypingCentre.equals(imageDTO.latestPhenotypingCentre) : imageDTO.latestPhenotypingCentre != null)
			return false;
		if (alleleName != null ? !alleleName.equals(imageDTO.alleleName) : imageDTO.alleleName != null) return false;
		if (markerSymbol != null ? !markerSymbol.equals(imageDTO.markerSymbol) : imageDTO.markerSymbol != null)
			return false;
		if (markerName != null ? !markerName.equals(imageDTO.markerName) : imageDTO.markerName != null) return false;
		if (markerSynonym != null ? !markerSynonym.equals(imageDTO.markerSynonym) : imageDTO.markerSynonym != null)
			return false;
		if (markerType != null ? !markerType.equals(imageDTO.markerType) : imageDTO.markerType != null) return false;
		if (humanGeneSymbol != null ? !humanGeneSymbol.equals(imageDTO.humanGeneSymbol) : imageDTO.humanGeneSymbol != null)
			return false;
		if (symbol != null ? !symbol.equals(imageDTO.symbol) : imageDTO.symbol != null) return false;
		if (subtype != null ? !subtype.equals(imageDTO.subtype) : imageDTO.subtype != null) return false;
		if (increment != null ? !increment.equals(imageDTO.increment) : imageDTO.increment != null) return false;
		if (stage != null ? !stage.equals(imageDTO.stage) : imageDTO.stage != null) return false;
		return !(latestPhenotypeStatus != null ? !latestPhenotypeStatus.equals(imageDTO.latestPhenotypeStatus) : imageDTO.latestPhenotypeStatus != null);

	}

	@Override
	public int hashCode() {
		int result = mpTermIds != null ? mpTermIds.hashCode() : 0;
		result = 31 * result + (mpTerm != null ? mpTerm.hashCode() : 0);
		result = 31 * result + (mpIdTerm != null ? mpIdTerm.hashCode() : 0);
		result = 31 * result + (mpTermSynonym != null ? mpTermSynonym.hashCode() : 0);
		result = 31 * result + (fullResolutionFilePath != null ? fullResolutionFilePath.hashCode() : 0);
		result = 31 * result + omeroId;
		result = 31 * result + (downloadUrl != null ? downloadUrl.hashCode() : 0);
		result = 31 * result + (imageLink != null ? imageLink.hashCode() : 0);
		result = 31 * result + (jpegUrl != null ? jpegUrl.hashCode() : 0);
		result = 31 * result + (efoId != null ? efoId.hashCode() : 0);
		result = 31 * result + (uberonId != null ? uberonId.hashCode() : 0);
		result = 31 * result + (symbolGene != null ? symbolGene.hashCode() : 0);
		result = 31 * result + (status != null ? status.hashCode() : 0);
		result = 31 * result + (imitsPhenotypeStarted != null ? imitsPhenotypeStarted.hashCode() : 0);
		result = 31 * result + (imitsPhenotypeComplete != null ? imitsPhenotypeComplete.hashCode() : 0);
		result = 31 * result + (imitsPhenotypeStatus != null ? imitsPhenotypeStatus.hashCode() : 0);
		result = 31 * result + (legacyPhenotypeStatus != null ? legacyPhenotypeStatus.hashCode() : 0);
		result = 31 * result + (latestProductionCentre != null ? latestProductionCentre.hashCode() : 0);
		result = 31 * result + (latestPhenotypingCentre != null ? latestPhenotypingCentre.hashCode() : 0);
		result = 31 * result + (alleleName != null ? alleleName.hashCode() : 0);
		result = 31 * result + (markerSymbol != null ? markerSymbol.hashCode() : 0);
		result = 31 * result + (markerName != null ? markerName.hashCode() : 0);
		result = 31 * result + (markerSynonym != null ? markerSynonym.hashCode() : 0);
		result = 31 * result + (markerType != null ? markerType.hashCode() : 0);
		result = 31 * result + (humanGeneSymbol != null ? humanGeneSymbol.hashCode() : 0);
		result = 31 * result + (symbol != null ? symbol.hashCode() : 0);
		result = 31 * result + (subtype != null ? subtype.hashCode() : 0);
		result = 31 * result + (increment != null ? increment.hashCode() : 0);
		result = 31 * result + (stage != null ? stage.hashCode() : 0);
		result = 31 * result + (latestPhenotypeStatus != null ? latestPhenotypeStatus.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "ImageDTO{" +
				"mpTermIds=" + mpTermIds +
				", mpTerm=" + mpTerm +
				", mpIdTerm=" + mpIdTerm +
				", mpTermSynonym=" + mpTermSynonym +
				", fullResolutionFilePath='" + fullResolutionFilePath + '\'' +
				", omeroId=" + omeroId +
				", downloadUrl='" + downloadUrl + '\'' +
				", imageLink='" + imageLink + '\'' +
				", jpegUrl='" + jpegUrl + '\'' +
				", efoId=" + efoId +
				", uberonId=" + uberonId +
				", symbolGene='" + symbolGene + '\'' +
				", status=" + status +
				", imitsPhenotypeStarted=" + imitsPhenotypeStarted +
				", imitsPhenotypeComplete=" + imitsPhenotypeComplete +
				", imitsPhenotypeStatus=" + imitsPhenotypeStatus +
				", legacyPhenotypeStatus=" + legacyPhenotypeStatus +
				", latestProductionCentre=" + latestProductionCentre +
				", latestPhenotypingCentre=" + latestPhenotypingCentre +
				", alleleName=" + alleleName +
				", markerSymbol=" + markerSymbol +
				", markerName=" + markerName +
				", markerSynonym=" + markerSynonym +
				", markerType='" + markerType + '\'' +
				", humanGeneSymbol=" + humanGeneSymbol +
				", symbol='" + symbol + '\'' +
				", subtype='" + subtype + '\'' +
				", increment=" + increment +
				", stage='" + stage + '\'' +
				", latestPhenotypeStatus=" + latestPhenotypeStatus +
				'}';
	}


	public void setMpTermId(ArrayList<String> mpTermIds) {
		this.mpTermIds=mpTermIds;

	}





}
