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
package org.mousephenotype.cda.db.dao;

/**
 *
 * Phenotype call manager interface
 *
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since May 2012
 */

import org.mousephenotype.cda.db.pojo.Datasource;
import org.mousephenotype.cda.db.pojo.Parameter;
import org.mousephenotype.cda.db.pojo.PhenotypeCallSummary;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;

import java.sql.SQLException;
import java.util.List;


public interface PhenotypeCallSummaryDAO extends HibernateDAO {

	/**
	 * Get all phenotype call summaries
	 * @return all phenotype call summaries
	 */
	public List<PhenotypeCallSummary> getAllPhenotypeCallSummaries();

	/**
	 * Find all phenotype call summaries by accession id
	 * @param accId the accession ID associated to the call
	 * @param dbId designates the type of gene id e.g. MGI
	 * @return a list of matching phenotype calls
	 */
	public List<PhenotypeCallSummary> getPhenotypeCallByAccession(String accId, int dbId);

	public List<PhenotypeCallSummary> getPhenotypeCallByAccession(String accId);

	public List<PhenotypeCallSummary> getPhenotypeCallByPhenotypingCenterAndPipeline(String phenotypingCenter, String pipelineStableId);

	/**
	 * Find all phenotype call summaries by MP accession id and external_db_id
	 * @param accId the MP accession ID associated to the call
	 * @param dbId is the external db id of MP
	 * @return a list of matching phenotype calls
	 */
	public List<PhenotypeCallSummary> getPhenotypeCallByMPAccession(String accId, int dbId);

	/**
	 * Delete all Phenotype call summaries by project
	 * @param parameter
	 *
	 * @param datasource
	 * @return the count of rows deleted
	 * @throws SQLException
	 */
	public void deletePhenotypeCallSummariesByDatasource(Datasource datasource, Parameter parameter) throws SQLException;

	public int deletePhenotypeCallSummariesByDatasourceParameterSexZygosity(Datasource datasource, Parameter parameter, SexType sex, ZygosityType zygosity);

	public void deleteCategoricalResultsByParameter(Parameter parameter) throws SQLException;
	public void deleteUnidimensionalResultsByParameter(Parameter parameter) throws SQLException;

	public void deleteCategoricalResults() throws SQLException;
	public void deleteUnidimensionalResults() throws SQLException;
}
