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

package org.mousephenotype.cda.indexers.beans;

import java.util.ArrayList;
import java.util.List;

import org.mousephenotype.cda.db.dao.EmapOntologyDAO;
import org.mousephenotype.cda.db.dao.MaOntologyDAO;

/**
 * This class encapsulates the methods necessary to serve up individual lists of
 * ma <code>OntologyTermBean</code> components. Without this wrapper, ontology
 * detail data, such as synonyms, subsets, parents, children, etc. in an
 * <code>OntologyTermBean</code> list must be picked out individually, each in
 * its own loop.
 * 
 * @author mrelac
 */
public class OntologyTermHelperEmap extends OntologyTermHelper {
    
    public OntologyTermHelperEmap(EmapOntologyDAO emapOntologyService, String id) {
        super(emapOntologyService, id);
    }

    public List<String> getSubsets() {
        return ((EmapOntologyDAO) ontologyService).getSubset(id, new ArrayList<String>());
    }
    
}
