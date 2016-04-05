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
package uk.ac.ebi.phenotype.web.util;

import org.mousephenotype.cda.db.dao.AnalyticsDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Wrap the data release version from the database
 */
@Component
public class DataReleaseVersionManager {

	@Autowired
	private AnalyticsDAO analyticsDAO;

	private String releaseVersion="";

	public DataReleaseVersionManager() {}

	public String getReleaseVersion() {
		// 0.1% of the time or on first request, refresh the version number
		if (releaseVersion.equals("") || Math.random()<0.001) {
			releaseVersion = analyticsDAO.getCurrentRelease();
		}

		return releaseVersion;
	}


	public void setReleaseVersion(String releaseVersion) {

		this.releaseVersion = releaseVersion;
	}
}
