/*******************************************************************************
 * Copyright © 2015 EMBL - European Bioinformatics Institute
 * <p>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this targetFile except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.cda.reports;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.reports.support.LoadValidateCountsQuery;
import org.mousephenotype.cda.reports.support.LoadsQueryDelta;
import org.mousephenotype.cda.reports.support.ReportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.beans.Introspector;
import java.io.IOException;
import java.util.List;

/**
 ** This report produces a file of cda_base database differences between a previous version and the current one.
 *
 * Created by mrelac on 24/07/2015.
 */
@Component
public class ExtractValidateCdabaseReport extends AbstractReport {

    private Logger   logger   = LoggerFactory.getLogger(this.getClass());
    private LoadValidateCountsQuery loadValidateCountsQuery;

    @Autowired
    @NotNull
    @Qualifier("jdbcCdabasePrevious")
    private JdbcTemplate jdbcPrevious;

    @Autowired
    @NotNull
    @Qualifier("jdbcCdabaseCurrent")
    private JdbcTemplate jdbcCurrent;

    /********************
     * DATABASE: cda_base
     ********************
    */
    private final double DELTA = 0.8;
    private LoadsQueryDelta[] queries = new LoadsQueryDelta[] {
            new LoadsQueryDelta("allele COUNTS", DELTA, "SELECT count(*) FROM allele"),
            new LoadsQueryDelta("biological_model COUNTS", DELTA, "SELECT count(*) FROM biological_model"),
            new LoadsQueryDelta("biological_model_allele COUNTS", DELTA, "SELECT count(*) FROM biological_model_allele"),
            new LoadsQueryDelta("biological_model_genomic_feature COUNTS", DELTA, "SELECT count(*) FROM biological_model_genomic_feature"),
            new LoadsQueryDelta("biological_model_phenotype COUNTS", DELTA, "SELECT count(*) FROM biological_model_phenotype"),
            new LoadsQueryDelta("external_db COUNTS", DELTA, "SELECT count(*) FROM external_db"),
            new LoadsQueryDelta("genomic_feature COUNTS", DELTA, "SELECT count(*) FROM genomic_feature"),
            new LoadsQueryDelta("ontology_term COUNTS", DELTA, "SELECT count(*) FROM ontology_term"),
            new LoadsQueryDelta("organisation COUNTS", DELTA, "SELECT count(*) FROM organisation"),
            new LoadsQueryDelta("project COUNTS", DELTA, "SELECT count(*) FROM project"),
            new LoadsQueryDelta("strain COUNTS", DELTA, "SELECT count(*) FROM strain")
    };

    @Override
    protected void initialise(String[] args) throws ReportException {
        super.initialise(args);
        loadValidateCountsQuery = new LoadValidateCountsQuery(jdbcPrevious, jdbcCurrent, csvWriter);
        loadValidateCountsQuery.addQueries(queries);
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass()));
    }

    public void run(String[] args) throws ReportException {

        List<String> errors = parser.validate(parser.parse(args));
        if ( ! errors.isEmpty()) {
            logger.error("ExtractValidateDccReport parser validation error: " + StringUtils.join(errors, "\n"));
            return;
        }
        initialise(args);

        long start = System.currentTimeMillis();

        try {
            String db1Name = jdbcPrevious.getDataSource().getConnection().getCatalog();
            String db2Name = jdbcCurrent.getDataSource().getConnection().getCatalog();
            logger.info("VALIDATION STARTED AGAINST DATABASES {} AND {}", db1Name, db2Name);

        } catch (Exception e) { }

        loadValidateCountsQuery.execute();

        try {
            csvWriter.close();
        } catch (IOException e) {
            throw new ReportException("Exception closing csvWriter: " + e.getLocalizedMessage());
        }

        log.info(String.format("Finished. [%s]", commonUtils.msToHms(System.currentTimeMillis() - start)));
    }
}