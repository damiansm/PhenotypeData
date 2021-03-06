package org.mousephenotype.cda.neo4j.entity;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by ckchen on 14/03/2017.
 */
@NodeEntity
public class Gene {

    @GraphId
    Long id;

    private String mgiAccessionId;
    private String markerType;
    private String markerSymbol;
    private String markerName;
    private String chrId;
    private int chrStart;
    private int chrEnd;
    private String chrStrand;
    private List<String> ensemblGeneId;
    private List<String> markerSynonym;
    private List<String> humanGeneSymbol;

    @Relationship(type = "ENSEMBL_GENE_ID", direction = Relationship.OUTGOING)
    private Set<EnsemblGeneId> ensemblGeneIds;

    @Relationship(type = "MARKER_SYNONYM", direction = Relationship.OUTGOING)
    private Set<MarkerSynonym> markerSynonyms;

    @Relationship(type = "HUMAN_GENE_SYMBOL", direction = Relationship.OUTGOING)
    private Set<HumanGeneSymbol> humanGeneSymbols;

    @Relationship(type = "ALLELE", direction = Relationship.OUTGOING)
    private Set<Allele> alleles;

    @Relationship(type = "DISEASE", direction = Relationship.OUTGOING)
    private Set<DiseaseGene> diseaseGenes;

    @Relationship(type = "MOUSE_PHENOTYPE", direction = Relationship.OUTGOING)
    private Set<Mp> mousePhenotypes;

    @Relationship(type = "HUMAN_PHENOTYPE", direction = Relationship.OUTGOING)
    private Set<Hp> humanPhenotypes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMgiAccessionId() {
        return mgiAccessionId;
    }

    public void setMgiAccessionId(String mgiAccessionId) {
        this.mgiAccessionId = mgiAccessionId;
    }

    public String getMarkerType() {
        return markerType;
    }

    public void setMarkerType(String markerType) {
        this.markerType = markerType;
    }

    public String getMarkerSymbol() {
        return markerSymbol;
    }

    public void setMarkerSymbol(String markerSymbol) {
        this.markerSymbol = markerSymbol;
    }

    public String getMarkerName() {
        return markerName;
    }

    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }

    public String getChrId() {
        return chrId;
    }

    public void setChrId(String chrId) {
        this.chrId = chrId;
    }

    public int getChrStart() {
        return chrStart;
    }

    public void setChrStart(int chrStart) {
        this.chrStart = chrStart;
    }

    public int getChrEnd() {
        return chrEnd;
    }

    public void setChrEnd(int chrEnd) {
        this.chrEnd = chrEnd;
    }

    public String getChrStrand() {
        return chrStrand;
    }

    public void setChrStrand(String chrStrand) {
        this.chrStrand = chrStrand;
    }

    public Set<EnsemblGeneId> getEnsemblGeneIds() {
        return ensemblGeneIds;
    }

    public void setEnsemblGeneIds(Set<EnsemblGeneId> ensemblGeneIds) {
        this.ensemblGeneIds = ensemblGeneIds;
    }

    public Set<MarkerSynonym> getMarkerSynonyms() {
        return markerSynonyms;
    }

    public void setMarkerSynonyms(Set<MarkerSynonym> markerSynonyms) {
        this.markerSynonyms = markerSynonyms;
    }

    public Set<HumanGeneSymbol> getHumanGeneSymbols() {
        return humanGeneSymbols;
    }

    public void setHumanGeneSymbols(Set<HumanGeneSymbol> humanGeneSymbols) {
        this.humanGeneSymbols = humanGeneSymbols;
    }

    public Set<Allele> getAlleles() {
        return alleles;
    }

    public void setAlleles(Set<Allele> alleles) {
        this.alleles = alleles;
    }

    public Set<DiseaseGene> getDiseaseGenes() {
        return diseaseGenes;
    }

    public void setDiseaseGenes(Set<DiseaseGene> diseaseGenes) {
        this.diseaseGenes = diseaseGenes;
    }

    public Set<Mp> getMousePhenotypes() {
        return mousePhenotypes;
    }

    public void setMousePhenotypes(Set<Mp> mousePhenotypes) {
        this.mousePhenotypes = mousePhenotypes;
    }

    public Set<Hp> getHumanPhenotypes() {
        return humanPhenotypes;
    }

    public void setHumanPhenotypes(Set<Hp> humanPhenotypes) {
        this.humanPhenotypes = humanPhenotypes;
    }

    public List<String> getEnsemblGeneId() {
        return ensemblGeneId;
    }

    public void setEnsemblGeneId(List<String> ensemblGeneId) {
        this.ensemblGeneId = ensemblGeneId;
    }

    public List<String> getMarkerSynonym() {
        return markerSynonym;
    }

    public void setMarkerSynonym(List<String> markerSynonym) {
        this.markerSynonym = markerSynonym;
    }

    public List<String> getHumanGeneSymbol() {
        return humanGeneSymbol;
    }

    public void setHumanGeneSymbol(List<String> humanGeneSymbol) {
        this.humanGeneSymbol = humanGeneSymbol;
    }

    @Override
    public String toString() {
        return "Gene{" +
                "id=" + id +
                ", mgiAccessionId='" + mgiAccessionId + '\'' +
                ", markerType='" + markerType + '\'' +
                ", markerSymbol='" + markerSymbol + '\'' +
                ", markerName='" + markerName + '\'' +
                ", chrId='" + chrId + '\'' +
                ", chrStart=" + chrStart +
                ", chrEnd=" + chrEnd +
                ", chrStrand='" + chrStrand + '\'' +
                ", ensemblGeneId=" + ensemblGeneId +
                ", markerSynonym=" + markerSynonym +
                ", humanGeneSymbol=" + humanGeneSymbol +
                ", ensemblGeneIds=" + ensemblGeneIds +
                ", markerSynonyms=" + markerSynonyms +
                ", humanGeneSymbols=" + humanGeneSymbols +
                ", alleles=" + alleles +
                ", diseaseGenes=" + diseaseGenes +
                ", mousePhenotypes=" + mousePhenotypes +
                ", humanPhenotypes=" + humanPhenotypes +
                '}';
    }
}