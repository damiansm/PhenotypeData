package org.mousephenotype.cda.owl;

import org.semanticweb.owlapi.model.OWLClass;

import java.util.*;

/**
 * Created by ilinca on 10/08/2016.
 */
public class OntologyTermDTO {

    String               accessionId;
    String               name;
    Set<String>          synonyms;
    Set<String>          narrowSynonyms;
    Set<OntologyTermDTO> equivalentClasses; // from equivalent classes
    Set<String>          childIds;
    Set<String>          childNames;
    Set<String>          parentIds;
    Set<String>          parentNames;
    Set<String>          alternateIds;
    Set<String>          considerIds;
    Set<String>          broadSynonyms;
    Set<String>          intermediateIds;
    Set<String>          intermediateNames;
    Set<String>          intermediateSynonyms;
    Set<String>         topLevelNames;
    Set<String>         topLevelIds;
    Set<String>         topLevelSynonyms;
    Set<String>         topLevelTermIdsConcatenated; // concatenated for search/ autosuggest/ something CK does
    String              replacementAccessionId;
    String              definition;
    boolean             isObsolete;
    OWLClass            cls;
    Map<Integer, List<Integer>> pathsToRoot; // <nodeId, <nodeids>>
    String              seachJson;
    String              childrenJson;
    String              scrollToNode;

    public OntologyTermDTO(){

        synonyms = new HashSet<>();
        narrowSynonyms = new HashSet<>();
        equivalentClasses = new HashSet<>();
        childIds = new HashSet<>();
        childNames = new HashSet<>();
        parentIds = new HashSet<>();
        parentNames = new HashSet<>();
        alternateIds = new HashSet<>();
        considerIds = new HashSet<>();
        broadSynonyms = new HashSet<>();
        intermediateIds = new HashSet<>();
        intermediateNames = new HashSet<>();
        intermediateSynonyms = new HashSet<>();
        topLevelNames = new HashSet<>();
        topLevelIds = new HashSet<>();
        topLevelSynonyms = new HashSet<>();
        topLevelTermIdsConcatenated = new HashSet<>();
    }

    public String getSeachJson() {
        return seachJson;
    }

    public void setSeachJson(String seachJson) {
        this.seachJson = seachJson;
    }

    public String getChildrenJson() {
        return childrenJson;
    }

    public void setChildrenJson(String childrenJson) {
        this.childrenJson = childrenJson;
    }

    public String getScrollToNode() {
        return scrollToNode;
    }

    public void setScrollToNode(String scrollToNode) {
        this.scrollToNode = scrollToNode;
    }

    public Set<Integer> getNodeIds(){
        return pathsToRoot == null ? null : pathsToRoot.keySet();
    }

    public Set<OntologyTermDTO> getEquivalentClasses() {
        return equivalentClasses;
    }

    public void setEquivalentClasses(Set<OntologyTermDTO> equivalentClasses) { this.equivalentClasses = equivalentClasses; }

    public OWLClass getCls() {
        return cls;
    }

    public void setCls(OWLClass cls) {
        this.cls = cls;
    }

    public String getReplacementAccessionId() {
        return replacementAccessionId;
    }

    public void setReplacementAccessionId(String replacementAccessionId) {   this.replacementAccessionId = replacementAccessionId; }

    public boolean isObsolete() {
        return isObsolete;
    }

    public void setObsolete(boolean obsolete) {
        isObsolete = obsolete;
    }

    public Set<String> getIntermediateSynonyms() {
        return intermediateSynonyms;
    }

    public void setIntermediateSynonyms(Set<String> intermediateSynonyms) {
        this.intermediateSynonyms = intermediateSynonyms;
    }

    public Map<Integer, List<Integer>> getPathsToRoot() {
        return pathsToRoot;
    }

    public void setPathsToRoot(Map<Integer, List<Integer>> pathsToRoot) {
        this.pathsToRoot = pathsToRoot;
    }

    public void addPathsToRoot(Integer nodeId, List<Integer> pathToRoot) {
        if (this.pathsToRoot == null){
            this.pathsToRoot = new HashMap<>();
        }
        this.pathsToRoot.put(nodeId, pathToRoot);
    }

    public Set<String> getTopLevelTermIdsConcatenated() {
        return topLevelTermIdsConcatenated;
    }

    public void setTopLevelTermIdsConcatenated(Set<String> topLevelMpTermIds) {
        this.topLevelTermIdsConcatenated = topLevelMpTermIds;
    }

    public void addTopLevelTermIdsConcatenated(String term, String id) {
        if (this.topLevelTermIdsConcatenated == null) { this.topLevelTermIdsConcatenated = new HashSet<>();}
        this.topLevelTermIdsConcatenated.add( concatForSearch(id,term));
    }

    public Set<String> getIntermediateIds() {
        return intermediateIds;
    }

    public void setIntermediateIds(Set<String> intermediateIds) {
        this.intermediateIds = intermediateIds;
    }

    public void addIntermediateIds(String intermediateId) {
        if (this.intermediateIds == null){ this.intermediateIds = new HashSet<>();}
        this.intermediateIds.add(intermediateId);
    }

    public void addIntermediateSynonyms(Collection<String> intermediateSyn) {
        if (this.intermediateSynonyms == null){ this.intermediateSynonyms = new HashSet<>();}
        this.intermediateSynonyms.addAll(intermediateSyn);
    }

    public Set<String> getIntermediateNames() {
        return intermediateNames;
    }

    public void setIntermediateNames(Set<String> intermediateNames) {
        this.intermediateNames = intermediateNames;
    }
    public void addIntermediateNames(String intermediateName) {
        if (this.intermediateNames == null){ this.intermediateNames = new HashSet<>();}
        this.intermediateNames.add(intermediateName);
    }

    public Set<String> getTopLevelNames() {
        return topLevelNames;
    }

    public void setTopLevelNames(Set<String> topLevelNames) {
        this.topLevelNames = topLevelNames;
    }

    public Set<String> getTopLevelSynonyms() {  return topLevelSynonyms;  }

    public void setTopLevelSynonyms(Set<String> topLevelSynonyms) {  this.topLevelSynonyms = topLevelSynonyms; }

    public void addTopLevelId(String topLevelId){
        if (this.topLevelIds == null) { this.topLevelIds = new HashSet<>();}
        this.topLevelIds.add(topLevelId);
    }
    public void addTopLevelName(String name){
        if (this.topLevelNames == null) { this.topLevelNames = new HashSet<>();}
        this.topLevelNames.add(name);
    }
    public void addTopLevelSynonym(Collection<String> synonyms){
        if (this.topLevelSynonyms == null) { this.topLevelSynonyms = new HashSet<>();}
        this.topLevelSynonyms.addAll(synonyms);
    }


    public Set<String> getTopLevelIds() {
        return topLevelIds;
    }

    public void setTopLevelIds(Set<String> topLevelIds) {
        this.topLevelIds = topLevelIds;
    }

    public String getAccessionId() {
        return accessionId;
    }

    public void setAccessionId(String accessonId) {
        this.accessionId = accessonId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(Set<String> synonyms) {
        this.synonyms = synonyms;
    }

    public Set<String> getConsiderIds() {
        return considerIds;
    }

    public Set<String> getNarrowSynonyms() {
        return narrowSynonyms;
    }

    public void setNarrowSynonyms(Set<String> narrowSynonyms) {
        this.narrowSynonyms = narrowSynonyms;
    }

    public Set<String> getAlternateIds() {
        return alternateIds;
    }

    public void setAlternateIds(Set<String> alternateIds) {
        this.alternateIds = alternateIds;
    }

    public void setConsiderIds(Set<String> considerIds) {
        this.considerIds = considerIds;
    }

    public Set<String> getBroadSynonyms() {
        return broadSynonyms;
    }

    public void setBroadSynonyms(Set<String> broadSynonyms) {
        this.broadSynonyms = broadSynonyms;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }


    public Set<String> getChildNames() {  return childNames;  }

    public void setChildNames(Set<String> childNames) {   this.childNames = childNames;  }

    public Set<String> getParentIds() {  return parentIds;}

    public void setParentIds(Set<String> parentIds) { this.parentIds = parentIds; }

    public void addParentId(String parentId) {
        if (this.parentIds == null){
            this.parentIds = new HashSet<>();
        }
        this.parentIds.add(parentId);
    }

    public void addParentName(String parentName) {
        if (this.parentNames == null){
            this.parentNames = new HashSet<>();
        }
        this.parentNames.add(parentName);
    }

    public void addChildName(String childName) {
        if (this.childNames == null){
            this.childNames = new HashSet<>();
        }
        this.childNames.add(childName);
    }

    public void addChildId(String childId) {
        if (this.childIds == null){
            this.childIds = new HashSet<>();
        }
        this.childIds.add(childId);
    }

    public Set<String> getParentNames() { return parentNames; }

    public void setParentNames(Set<String> parentNames) {  this.parentNames = parentNames; }

    public Set<String> getChildIds() {
        return childIds;
    }

    public void setChildIds(Set<String> childClasses) {
        this.childIds = childClasses;
    }

    @Override
    public String toString() {
        return "OntologyTermDTO{" +
                "accessonId='" + accessionId + '\'' +
                ", name='" + name + '\'' +
                ", synonyms=" + synonyms +
                ", narrowSynonyms=" + narrowSynonyms +
                ", equivalentClasses=" + equivalentClasses +
                ", alternateIds=" + alternateIds +
                ", considerIds=" + considerIds +
                ", broadSynonyms=" + broadSynonyms +
                ", replacementAccessionId='" + replacementAccessionId + '\'' +
                ", definition='" + definition + '\'' +
                ", isObsolete=" + isObsolete +
                ", cls=" + cls +
                '}';
    }


    /**
     * CK uses fields like this for the search/suggest/result display
     * @return
     */
    private String concatForSearch(String id, String name){
        return id + "___" + name;
    }

}
