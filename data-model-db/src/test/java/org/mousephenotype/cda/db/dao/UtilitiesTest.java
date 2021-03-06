/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
/**
 * Copyright © 2014 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This test class is intended to run healthchecks against the observation table.
 */

package org.mousephenotype.cda.db.dao;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.config.TestConfig;
import org.mousephenotype.cda.db.impress.Utilities;
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.mousephenotype.cda.db.pojo.Parameter;
import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.enumerations.StageUnitType;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@TestPropertySource("file:${user.home}/configfiles/${profile:dev}/test.properties")
@Import(TestConfig.class)
public class UtilitiesTest {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PhenotypePipelineDAO pDAO;

    @Autowired
    Utilities impressUtilities;

    @Autowired
    JdbcTemplate jdbc1;

    @Autowired
    JdbcTemplate jdbc2;


    @Test
    public void testCheckTypeParameterString() {
        Parameter p = pDAO.getParameterByStableId("ESLIM_003_001_006");
        String value= "2.092";
        ObservationType oType = impressUtilities.checkType(p, value);

        logger.debug("oType = {}", oType.toString());
        assert(oType.equals(ObservationType.time_series));
    }

    @Test
    public void testCheckStageConversion() throws Exception {

        List<String> goodStages = Arrays.asList("9.5", "12.5", "20");
        List<StageUnitType> goodStageUnits = Arrays.asList(StageUnitType.DPC, StageUnitType.DPC, StageUnitType.THEILER);
        List<String> goodTerms = Arrays.asList( "embryonic day 9.5", "embryonic day 12.5", "TS20,embryo");

        List<String> badStages = Arrays.asList( "9.5", "a", "30");
        List<StageUnitType> badStageUnits = Arrays.asList(StageUnitType.THEILER, StageUnitType.THEILER, StageUnitType.DPC);

        for (int i = 0; i<goodStages.size(); i++) {
            String stage = goodStages.get(i);
            StageUnitType stageUnit = goodStageUnits.get(i);

            logger.debug("Testing: " + stage + " " + stageUnit.getStageUnitName());

            // Need a method to convert impress input to representative EFO term
            OntologyTerm term = impressUtilities.getStageTerm(stage, stageUnit);
            org.junit.Assert.assertTrue(term.getName().equals(goodTerms.get(i)));
        }

        for (int i = 0; i<badStages.size(); i++) {
            String stage = badStages.get(i);
            StageUnitType stageUnit = badStageUnits.get(i);

            logger.debug("Testing bad case:" + stage + " " + stageUnit.getStageUnitName());

            // Need a method to convert impress input to represnetative EFO term
            OntologyTerm term = impressUtilities.getStageTerm(stage, stageUnit);
            org.junit.Assert.assertTrue(term==null);
        }
    }


    // Test identical results. No difference is expected.

    @Test
    public void testQueryDiffNoDiffsIdentical() throws Exception{
        SqlUtils sqlUtils = new SqlUtils();

        logger.debug("Testing testQueryDiffNoDiffsIdentical");

        List<String[]> actualResults = sqlUtils.queryDiff(jdbc1, jdbc1, "SELECT message FROM test");

        assert(actualResults.isEmpty());
    }


    // Test more results in jdbc2 than jdbc1. No difference is expected.
    @Test
    public void testQueryDiffNoDiffsMoreResultsInJdbc2() throws Exception{
        SqlUtils sqlUtils = new SqlUtils();

        List<String[]> expectedResults = new ArrayList<>();

        logger.debug("Testing testQueryDiffNoDiffsMoreResultsInJdbc2");

        List<String[]> actualResults = sqlUtils.queryDiff(jdbc1, jdbc2, "SELECT message FROM test");

        assert(actualResults.isEmpty());
    }

    // Test fewer results in jdbc2 than jdbc1. The extra rows in jdbc1 should be returned.
    @Test
    public void testQueryDiffTwoDiffs() throws Exception{
        SqlUtils sqlUtils = new SqlUtils();

        List<String[]> expectedResults = new ArrayList<>();
        expectedResults.add(new String[] { "MESSAGE" } );
        expectedResults.add(new String[] { "dcc line 6" } );
        expectedResults.add(new String[] { "dcc line 7" } );

        logger.debug("Testing testQueryDiffTwoDiffs");

        List<String[]> actualResults = sqlUtils.queryDiff(jdbc2, jdbc1, "SELECT message FROM test");

        assert(actualResults.size() == 3);
        for (int i = 0; i < expectedResults.size(); i++) {
            Assert.assertArrayEquals(expectedResults.get(i), actualResults.get(i));
        }
    }
}
