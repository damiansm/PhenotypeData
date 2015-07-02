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
package org.mousephenotype.cda.dao;

/**
*
* Sequence region data access manager implementation.
*
* @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
* @since February 2012
*/

import org.hibernate.SessionFactory;
import org.mousephenotype.cda.pojo.SequenceRegion;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Repository
public class SequenceRegionDAOImpl extends HibernateDAOImpl implements
		SequenceRegionDAO {

	public SequenceRegionDAOImpl() {
		
	}
	
	/**
	 * Creates a new Hibernate sequence region data access manager.
	 * @param sessionFactory the Hibernate session factory
	 */
	public SequenceRegionDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<SequenceRegion> getAllSequenceRegions() {
		return getCurrentSession().createQuery("from SequenceRegion").list();
	}

	@Transactional(readOnly = true)
	public SequenceRegion getSequenceRegionByName(String name) {
		return (SequenceRegion) getCurrentSession().createQuery("from SequenceRegion as region where region.name= ?").setString(0, name).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public HashMap<String, SequenceRegion> getSequenceRegionsMap() {
		HashMap<String, SequenceRegion> map = new HashMap<String, SequenceRegion>();
		List<SequenceRegion> l = getCurrentSession().createQuery("from SequenceRegion").list();
		for (SequenceRegion r: l) {
			map.put(r.getName(), r);
		}
		return map;
	}

}
