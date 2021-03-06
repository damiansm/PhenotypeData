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
 */

package org.mousephenotype.cda.indexers;

import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.config.TestConfigIndexers;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.indexers.exceptions.InvalidCoreNameException;
import org.mousephenotype.cda.indexers.exceptions.MissingRequiredArgumentException;
import org.mousephenotype.cda.solr.generic.util.JSONRestUtil;
import org.mousephenotype.cda.solr.service.GeneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

/**
 *
 * @author mrelac
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {TestConfigIndexers.class} )
@TestPropertySource(locations = {"file:${user.home}/configfiles/${profile:dev}/test.properties"})
@Transactional
public class IndexerManagerTest {

    @Autowired
    protected GeneService geneService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected String externalDevUrl = "http://ves-ebi-d0:8090/mi/impc/dev/solr";

    public IndexerManagerTest() {
    }

    public final String NO_DEPS_ERROR_MESSAGE = "Invalid argument 'nodeps' specified with empty core list.";

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }


    // Consult IndexerManager.parseCommandLine() javadoc for derived test cases.


    /***********************************************************************************/
    /*    THE FOLLOWING TESTS GENERATE EXPECTED EXCEPTIONS AND THUS DO NOT BUILD       */
    /*    ANY CORES. THEY ARE INTENDED TO TEST THE SPECIFIED COMMAND-LINE PARAMETERS   */
    /*    FOR INVALID COMMAND-LINE OPTIONS.                                            */
    /***********************************************************************************/


     /**
      * Test invoking static main with no arguments.
      *
      * Expected results: STATUS_NO_ARGUMENT.
      */
     @Test
//@Ignore
    public void testStaticNoArgs() throws IOException, SolrServerException, SQLException, URISyntaxException {
        String testName = "testStaticNoArgs";
        System.out.println("-------------------" + testName + "-------------------");
        System.out.println("Command line = ");
        int retVal =  IndexerManager.mainReturnsStatus(new String[]{});

        switch (retVal) {
            case IndexerManager.STATUS_NO_ARGUMENT:
                break;

            default:
                fail("Expected STATUS_NO_ARGUMENT");
                break;
        }
    }

     /**
      * Test invoking IndexerManagerInstance with no arguments.
      *
      * Expected results: MissingRequiredArgumentException.
      */
     @Test
//@Ignore
    public void testInstanceNoArgs() {
        String testName = "testInstanceNoArgs";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        IndexerManager indexerManager = new IndexerManager();

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            if (ie.getCause() instanceof MissingRequiredArgumentException) {
                // Do nothing. This is what we expect.
                return;
            }
        }

        fail("Expected MissingRequiredArgumentException");
    }



     /**
      * Test invoking static main with invalid nodeps
      *
      * Expected results: STATUS_NO_ARGUMENT.
      */
     @Test
//@Ignore
     public void testStaticNoCoresNodeps() throws IOException, SolrServerException, SQLException, URISyntaxException {
        String testName = "testStaticNoCoresNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String args[] = { "--nodeps" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        int retVal =  IndexerManager.mainReturnsStatus(args);

        switch (retVal) {
            case IndexerManager.STATUS_NO_ARGUMENT:
                break;

            default:
                fail("Expected STATUS_NO_ARGUMENT");
        }
     }

     /**
      * Test invoking IndexerManager instance with invalid nodeps argument specified
      *
      * Expected results: MissingRequiredArgumentException.
      */
     @Test
//@Ignore
     public void testInstanceNoCoresNodeps() {
        String testName = "testInstanceNoCoresNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String args[] = { "--nodeps" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        IndexerManager indexerManager = new IndexerManager();

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            if (ie.getCause() instanceof MissingRequiredArgumentException) {
                // Do nothing. This is the expected exception.
            } else {
                fail("Expected MissingRequiredArgumentException");
            }
        } catch (Exception e) {
            fail("Expected MissingRequiredArgumentException");
        }
     }

     /**
      * Test invoking static main with invalid core name.
      *
      * Expected results: STATUS_INVALID_CORE_NAME.
      */
     @Test
//@Ignore
     public void testStaticInvalidCoreName() throws IOException, SolrServerException, SQLException, URISyntaxException {
        String testName = "testStaticInvalidCoreName";
        System.out.println("-------------------" + testName + "-------------------");
        String args[] = { "--cores=junk" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        int retVal =  IndexerManager.mainReturnsStatus(args);

        switch (retVal) {
            case IndexerManager.STATUS_INVALID_CORE_NAME:
                break;

            default:
                fail("Expected STATUS_INVALID_CORE_NAME");
        }
     }

     /**
      * Test invoking static main with invalid core name.
      *
      * Expected results: InvalidCoreNameException.
      */
    @Test
//@Ignore
    public void testInstanceInvalidCoreName() {
        String testName = "testInstanceInvalidCoreName";
        System.out.println("-------------------" + testName + "-------------------");
        String args[] = { "--cores=junk" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        IndexerManager indexerManager = new IndexerManager();

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            if (ie.getCause() instanceof InvalidCoreNameException) {
                // Do nothing. This is the expected exception.
            } else {
                fail("Expected InvalidCoreNameException");
            }
        } catch (Exception e) {
            fail("Expected InvalidCoreNameException");
        }
     }

    /**
      * Test invoking IndexerManager instance with no 'cores=' argument.
      *
      * Expected results: MissingRequiredArgumentException.
      */
    @Test
//@Ignore
    public void testInstanceNoCores() {
        String testName = "testInstanceNoCores";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = { };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        IndexerManager indexerManager = new IndexerManager();

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            if (ie.getCause() instanceof MissingRequiredArgumentException) {
                // Do nothing. This is the expected exception.
            } else {
                fail("Expected MissingRequiredArgumentException");
            }
        } catch (Exception e) {
            fail("Expected MissingRequiredArgumentException");
        }
    }

     /**
      * Test invoking IndexerManager instance with empty 'cores=' argument.
      *
      * Expected results: MissingRequiredArgumentException.
      */
     @Test
//@Ignore
    public void testInstanceEmptyCoresNoEquals() {
        String testName = "testInstanceEmptyCoresNoEquals";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--cores" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        IndexerManager indexerManager = new IndexerManager();

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            if (ie.getCause() instanceof MissingRequiredArgumentException) {
                // Do nothing. This is the expected exception.
            } else {
                fail("Expected MissingRequiredArgumentException");
            }
        } catch (Exception e) {
            fail("Expected MissingRequiredArgumentException");
        }
    }

     /**
      * Test invoking IndexerManager instance with empty 'cores=' argument.
      *
      * Expected results: MissingRequiredArgumentException.
      */
     @Test
//@Ignore
    public void testInstanceEmptyCores() {
        String testName = "testInstanceEmptyCores";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--cores=" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        IndexerManager indexerManager = new IndexerManager();

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            if (ie.getCause() instanceof MissingRequiredArgumentException) {
                // Do nothing. This is the expected exception.
            } else {
                fail("Expected MissingRequiredArgumentException");
            }
        } catch (Exception e) {
            fail("Expected MissingRequiredArgumentException");
        }
    }

     /**
      * Test invoking IndexerManager instance with empty 'cores=' argument, --nodeps BEFORE --cores.
      *
      * Expected results: MissingRequiredArgumentException.
      */
     @Test
//@Ignore
    public void testInstanceEmptyCoresNoEqualsNodepsBeforeCores() {
        String testName = "testInstanceEmptyCoresNoEqualsNodepsBeforeCores";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--nodeps", "--cores" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        IndexerManager indexerManager = new IndexerManager();

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            if (ie.getCause() instanceof MissingRequiredArgumentException) {
                // Do nothing. This is the expected exception.
            } else {
                fail(ie.getLocalizedMessage());
            }
        } catch (Exception e) {
            fail(e.getLocalizedMessage());
        }
    }

     /**
      * Test invoking IndexerManager instance with empty 'cores=' argument, --nodeps BEFORE --cores.
      *
      * Expected results: MissingRequiredArgumentException.
      */
     @Test
//@Ignore
    public void testInstanceEmptyCoresNodepsBeforeCores() {
        String testName = "testInstanceEmptyCoresNodepsBeforeCores";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--nodeps", "--cores=" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        IndexerManager indexerManager = new IndexerManager();

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            if (ie.getCause() instanceof MissingRequiredArgumentException) {
                // Do nothing. This is the expected exception.
            } else {
                fail("Expected MissingRequiredArgumentException");
            }
        } catch (Exception e) {
            fail("Expected MissingRequiredArgumentException");
        }
    }

     /**
      * Test invoking static main with --all and --cores=ma
      *
      * Expected results: STATUS_VALIDATION_ERROR.
      */
     @Test
//@Ignore
    public void testStaticAllAndCores() throws IOException, SolrServerException, SQLException, URISyntaxException {
        String testName = "testStaticAllAndCores";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = { "--all", "--cores=ma" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        int retVal =  IndexerManager.mainReturnsStatus(args);

        switch (retVal) {
            case IndexerManager.STATUS_VALIDATION_ERROR:
                break;

            default:
                fail("Expected STATUS_VALIDATION_ERROR");
                break;
        }
    }

     /**
      * Test invoking static main with --all and --nodeps
      *
      * Expected results: STATUS_VALIDATION_ERROR.
      */
     @Test
//@Ignore
    public void testStaticAllAndNodeps() throws IOException, SolrServerException, SQLException, URISyntaxException {
        String testName = "testStaticAllAndNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = { "--all", "--nodeps" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        int retVal =  IndexerManager.mainReturnsStatus(args);

        switch (retVal) {
            case IndexerManager.STATUS_VALIDATION_ERROR:
                break;

            default:
                fail("Expected STATUS_VALIDATION_ERROR");
                break;
        }
    }

     /**
      * Test invoking static main with --all and --nodeps
      *
      * Expected results: STATUS_VALIDATION_ERROR.
      */
     @Test
//@Ignore
    public void testStaticDailyAndNodeps() throws IOException, SolrServerException, SQLException, URISyntaxException {
        String testName = "testStaticDailyAndNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = { "--daily", "--nodeps" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        int retVal =  IndexerManager.mainReturnsStatus(args);

        switch (retVal) {
            case IndexerManager.STATUS_VALIDATION_ERROR:
                break;

            default:
                fail("Expected STATUS_VALIDATION_ERROR");
                break;
        }
    }

     /**
      * Test invoking static main with --all and --cores=ma
      *
      * Expected results: STATUS_VALIDATION_ERROR.
      */
     @Test
//@Ignore
    public void testStaticDailyAndCores() throws IOException, SolrServerException, SQLException, URISyntaxException {
        String testName = "testStaticDailyAndCores";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = { "--daily", "--cores=ma" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        int retVal =  IndexerManager.mainReturnsStatus(args);

        switch (retVal) {
            case IndexerManager.STATUS_VALIDATION_ERROR:
                break;

            default:
                fail("Expected STATUS_VALIDATION_ERROR");
                break;
        }
    }

     /**
      * Test invoking static main with --all and --cores=ma
      *
      * Expected results: STATUS_VALIDATION_ERROR.
      */
     @Test
//@Ignore
    public void testStaticAllAndDaily() throws IOException, SolrServerException, SQLException, URISyntaxException {
        String testName = "testStaticAllAndDaily";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = { "--all", "--daily" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        int retVal =  IndexerManager.mainReturnsStatus(args);

        switch (retVal) {
            case IndexerManager.STATUS_VALIDATION_ERROR:
                break;

            default:
                fail("Expected STATUS_VALIDATION_ERROR");
                break;
        }
    }

     /**
      * Test invoking static main with --all and --nodeps
      *
      * Expected results: STATUS_VALIDATION_ERROR.
      */
     @Test
//@Ignore
    public void testStaticAllAndDailyAndNodeps() throws IOException, SolrServerException, SQLException, URISyntaxException {
        String testName = "testStaticAllAndDailyAndNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = { "--all", "--daily", "--nodeps" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        int retVal =  IndexerManager.mainReturnsStatus(args);

        switch (retVal) {
            case IndexerManager.STATUS_VALIDATION_ERROR:
                break;

            default:
                fail("Expected STATUS_VALIDATION_ERROR");
                break;
        }
    }

    /************************************************************************************************/
    /*    THE FOLLOWING TESTS ARE NOT EXPECTED TO GENERATE EXCEPTIONS; THUS THEY CAN                */
    /*    BUILD CORES. SINCE IT IS NOT THE JOB OF THE TESTS TO BUILD THE CORES, ONLY                */
    /*    THE initialise() METHOD IS RUN; THE run() METHOD THAT ACTUALLY BUILDS THE CORES           */
    /*    IS NOT RUN. THESE THEY ARE INTENDED TO TEST THE SPECIFIED COMMAND-LINE PARAMETERS         */
    /*    FOR COMMAND-LINE OPTIONS AND TO TEST THAT ONLY THE EXPECTED CORES WOULD BE BUILT.         */
    /*    testStaticXxx VERSIONS OF THESE TESTS CANNOT BE RUN BECAUSE THE run() METHOD IS ALWAYS    */
    /*    CALLED AUTOMATICALLY, THERE BEING NO WAY TO SUPPRESS IT.                                  */
    /************************************************************************************************/


     /**
      * Test invoking IndexerManager instance starting at the first core (the
      * observation core).
      *
      * Expected results: cores observation to autosuggest ready to run.
      */
     @Test
//@Ignore
     public void testInstanceFirstAllCore() {
        String testName = "testInstanceFirstCore";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--cores=pipeline" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        IndexerManager indexerManager = new IndexerManager();

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }

        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        assertArrayEquals(IndexerManager.allCoresArray, actualCores);
     }

     /**
      * Test invoking IndexerManager instance starting at the first core (the
      * observation core), using the nodeps option.
      *
      * Expected results: the single observation core, ready to run.
      */
     @Test
//@Ignore
     public void testInstanceFirstCoreNodeps() {
        String testName = "testInstanceFirstCoreNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--cores=experiment", "--nodeps" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        IndexerManager indexerManager = new IndexerManager();

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }

        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        String[] expectedCores = new String[] { IndexerManager.OBSERVATION_CORE };
        assertArrayEquals(expectedCores, actualCores);
     }

     /**
      * Test invoking IndexerManager instance starting at the first daily core
      * (the preqc core).
      *
      * Expected results: All of the cores from preqc to autosuggest, ready to run.
      */
     @Test
//@Ignore
     public void testInstanceFirstDailyCore() {
        String testName = "testInstanceFirstDailyCore";
        System.out.println("-------------------" + testName + "-------------------");

         String[] args = new String[] { "--cores=allele2" };
         System.out.println("Command line = " + StringUtils.join(args, ","));
         IndexerManager indexerManager = new IndexerManager();

         // Determine which cores to build.
         try {
             indexerManager.initialise(args);
         } catch (IndexerException ie) {
             fail(ie.getLocalizedMessage());
         }

         String[] actualCores = indexerManager.getCores().toArray(new String[0]);
         assertArrayEquals(IndexerManager.dailyCoresArray, actualCores);
     }

     /**
      * Test invoking IndexerManager instance starting at the first daily core
      * (the preqc core), using the nodeps option.
      *
      * Expected results: the single preqc core, ready to run.
      */
     @Test
//@Ignore
     public void testInstanceFirstDailyCoreNodeps() {
        String testName = "testInstanceFirstDailyCoreNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--cores=preqc", "--nodeps" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        IndexerManager indexerManager = new IndexerManager();

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }

        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        String[] expectedCores = new String[] { IndexerManager.PREQC_CORE };
        assertArrayEquals(expectedCores, actualCores);
     }

     /**
      * Test invoking IndexerManager instance starting at the last core (the
      * autosuggest core).
      *
      * Expected results: the single autosuggest core, ready to run.
      */
     @Test
//@Ignore
     public void testInstanceLastCore() {
        String testName = "testInstanceLastCore";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--cores=autosuggest" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        IndexerManager indexerManager = new IndexerManager();

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }

        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        String[] expectedCores = new String[] { IndexerManager.AUTOSUGGEST_CORE };
        assertArrayEquals(expectedCores, actualCores);
     }

     /**
      * Test invoking IndexerManager instance starting at the last core (the
      * autosuggest core), using the nodeps option.
      *
      * Expected results: the single autosuggest core, ready to run.
      */
     @Test
//@Ignore
     public void testInstanceLastCoreNodeps() {
        String testName = "testInstanceLastCoreNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--cores=autosuggest", "--nodeps" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        IndexerManager indexerManager = new IndexerManager();

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }

        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        String[] expectedCores = new String[] { IndexerManager.AUTOSUGGEST_CORE };
        assertArrayEquals(expectedCores, actualCores);
     }

     /**
      * Test invoking IndexerManager instance specifying specific cores
      *
      * Expected results: the specified cores, ready to run.
      */
     @Test
//@Ignore
     public void testInstanceMultipleCores() {
        String testName = "testInstanceMultipleCores";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--cores=pipeline,allele,impc_images,anatomy,disease,mp" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        IndexerManager indexerManager = new IndexerManager();

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }

        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        String[] expectedCores = new String[] {
          IndexerManager.PIPELINE_CORE
        , IndexerManager.ALLELE_CORE
        , IndexerManager.IMPC_IMAGES_CORE
        , IndexerManager.ANATOMY_CORE
        , IndexerManager.DISEASE_CORE
        , IndexerManager.MP_CORE
        };
        assertArrayEquals(expectedCores, actualCores);
     }

     /**
      * Test invoking IndexerManager instance specifying specific cores), using
      * the nodeps option.
      *
      * Expected results: the specified cores, ready to run.
      */
     @Test
//@Ignore
     public void testInstanceMultipleCoresNodeps() {
        String testName = "testInstanceMultipleCoresNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--cores=pipeline,preqc,allele,impc_images,anatomy,disease,mp", "--nodeps" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        IndexerManager indexerManager = new IndexerManager();

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }

        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        String[] expectedCores = new String[] {
          IndexerManager.PIPELINE_CORE
        , IndexerManager.PREQC_CORE
        , IndexerManager.ALLELE_CORE
        , IndexerManager.IMPC_IMAGES_CORE
        , IndexerManager.ANATOMY_CORE
        , IndexerManager.DISEASE_CORE
        , IndexerManager.MP_CORE
        };
        assertArrayEquals(expectedCores, actualCores);
     }

     /**
      * Test invoking IndexerManager instance  using the --all argument.
      *
      * Expected results: cores observation to autosuggest ready to run.
      */
     @Test
//@Ignore
     public void testInstanceAll() {
        String testName = "testInstanceAll";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--all" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        IndexerManager indexerManager = new IndexerManager();

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }

        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        assertArrayEquals(IndexerManager.allCoresArray, actualCores);
     }

     /**
      * Test invoking IndexerManager instance  using the --daily argument.
      *
      * Expected results: cores preqc to autosuggest ready to run.
      */
     @Test
//@Ignore
     public void testInstanceDaily() {
        String testName = "testInstanceDaily";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--daily" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        IndexerManager indexerManager = new IndexerManager();

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }

        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        assertArrayEquals(IndexerManager.dailyCoresArray, actualCores);
     }

     /**
      * Test invoking static main with --cores=ma --nodeps --deploy
      *
      * Expected results: ma core to be created, built, and deployed.
      */
     @Test
	@Ignore
    public void testStaticBuildAndDeploy() throws IOException, SolrServerException, SQLException, URISyntaxException {
        String testName = "testStaticBuildAndDeploy";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = { "--cores=ma", "--nodeps" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        int retVal =  IndexerManager.mainReturnsStatus(args);

        switch (retVal) {
            case IndexerManager.STATUS_OK:
                break;

            default:
                fail("Expected STATUS_OK");
                break;
        }
    }


    /************************************************************************************************/
    /*    THE FOLLOWING TESTS ARE NOT EXPECTED TO GENERATE EXCEPTIONS. THEY TEST THE BUILDING OF    */
    /*    THE CORES. SOME CORES TAKE A LONG TIME TO BUILD, SO ONLY CORES WITH A SHORT BUILD TIME    */
    /*    WILL BE INCLUDED HERE.                                                                    */
    /************************************************************************************************/


     /**
      * Test invoking static main specifying single core, using the nodeps option.
      *
      * Expected results: The specified core to be built.
      */
	@Ignore
     @Test
     public void testStaticBuildSingleCoreNodeps() throws IOException, SolrServerException, SQLException, URISyntaxException {
        String testName = "testStaticBuildSingleCoreNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--cores=anatomy", "--nodeps" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        int retVal =  IndexerManager.mainReturnsStatus(args);

        switch (retVal) {
            case IndexerManager.STATUS_OK:
                break;

            default:
                fail("Expected STATUS_OK but found " + IndexerManager.getStatusCodeName(retVal));
        }
     }

     /**
      * Test invoking IndexerManager instance specifying a single core, using
      * the nodeps option.
      *
      * Expected results: The specified core to be built.
     * @throws SQLException
      */
	@Ignore
     @Test
     public void testInstanceBuildSingleCoreNodeps() throws SQLException, IOException, SolrServerException, URISyntaxException {
        String testName = "testInstanceBuildSingleCoreNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--cores=anatomy", "--nodeps" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        IndexerManager indexerManager = new IndexerManager();

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }

        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        String[] expectedCores = new String[] { IndexerManager.ANATOMY_CORE};
        assertArrayEquals(expectedCores, actualCores);

        // Initialise, validate, and build the cores.
        try {
	        indexerManager.run();
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }
     }

     /**
      * Test invoking static main specifying multiple cores, using the nodeps option.
      *
      * Expected results: The specified cores to be built.
      */
	@Ignore
     @Test
     public void testStaticBuildMultipleCoresNodeps() throws IOException, SolrServerException, SQLException, URISyntaxException {
        String testName = "testStaticBuildMultipleCoresNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--cores=anatomy,anatomy", "--nodeps" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        int retVal =  IndexerManager.mainReturnsStatus(args);

        switch (retVal) {
            case IndexerManager.STATUS_OK:
                break;

            default:
                fail("Expected STATUS_OK but found " + IndexerManager.getStatusCodeName(retVal));
        }
     }


     /**
      * Test invoking IndexerManager instance specifying multiple cores, using
      * the nodeps option.
      *
      * Expected results: All of the specified cores built.
     * @throws SQLException
      */
	@Ignore
     @Test
     public void testInstanceBuildMultipleCoresNodeps() throws SQLException, IOException, SolrServerException, URISyntaxException {
        String testName = "testInstanceBuildMultipleCoresNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--cores=anatomy,anatomy", "--nodeps" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        IndexerManager indexerManager = new IndexerManager();

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }

        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        String[] expectedCores = new String[] { IndexerManager.ANATOMY_CORE, IndexerManager.ANATOMY_CORE};
        assertArrayEquals(expectedCores, actualCores);

        // Initialise, validate, and build the cores.
        try {
            indexerManager.run();
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }
     }

	@Ignore
     @Test
     public void testGetSolrCoreDocumentCount() throws Exception {
         String querySegment = "/allele/select?q=*:*&rows=0&wt=json";
         String query = externalDevUrl + querySegment;
        JSONObject alleleResults = JSONRestUtil.getResults(query);

        Integer numFound = (Integer)alleleResults.getJSONObject("response").get("numFound");
        if (numFound == null)
            fail("Unable to fetch number of documents.");
        else if (numFound <= 0)
            fail("Expected at least 1 document. Document count = " + numFound);
     }

     /**
      * Build daily cores. NOTE: This test is not meant to be run with the
      * test suite, as it takes a long time to complete. It is here to permit
      * building core(s) quickly and easily.
      *
      * Expected results: The specified cores to be built.
      */
//     @Test
//     public void testStaticBuildDailyCores() {
//        String testName = "testStaticBuildDailyCores";
//        System.out.println("-------------------" + testName + "-------------------");
//        String[] args = new String[] { "--cores=ma,mp,disease", "--nodeps" };
//        System.out.println("Command line = " + StringUtils.join(args, ","));
//        int retVal =  IndexerManager.mainReturnsStatus(args);
//
//        switch (retVal) {
//            case IndexerManager.STATUS_OK:
//                break;
//
//            default:
//                fail("Expected STATUS_OK but found " + IndexerManager.getStatusCodeName(retVal));
//        }
//     }

//     /**
//      * Build all cores. NOTE: This test is not meant to be run with the
//      * test suite, as it takes a long time to complete. It is here to permit
//      * building core(s) quickly and easily.
//      *
//      * Expected results: The specified cores to be built.
//      */
//     @Test
//     public void testStaticBuildCores() {
//        String testName = "testStaticBuildCores";
//        System.out.println("-------------------" + testName + "-------------------");
//        String[] args = new String[] { "--cores=experiment" };
//        logger.info("Command line = " + StringUtils.join(args, ","));
//        int retVal =  IndexerManager.mainReturnsStatus(args);
//
//        switch (retVal) {
//            case IndexerManager.STATUS_OK:
//                break;
//
//            default:
//                fail("Expected STATUS_OK but found " + IndexerManager.getStatusCodeName(retVal));
//        }
//     }
}
