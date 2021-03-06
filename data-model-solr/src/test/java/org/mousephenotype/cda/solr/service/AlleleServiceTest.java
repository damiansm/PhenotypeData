package org.mousephenotype.cda.solr.service;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.solr.TestConfigSolr;
import org.mousephenotype.cda.solr.service.dto.AlleleDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners( { DependencyInjectionTestExecutionListener.class })
@ContextConfiguration(classes={TestConfigSolr.class})
@TestPropertySource(locations = {"file:${user.home}/configfiles/${profile:dev}/test.properties"})
public class AlleleServiceTest extends TestCase {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	AlleleService as;

	@Test
//	@Ignore
	public void testGetStatusCount() throws Exception {

		logger.debug("Testing the getStatusCount method");

		// 2015-07-06 Data comes from the current allele core
		Set<String> genes = new HashSet<>();
		genes.add("MGI:1328357");
		genes.add("MGI:2442115");
		genes.add("MGI:105090");
		final int expectedStatusCount = genes.size();

		final HashMap<String, Long> actualStatusCount = as.getStatusCount(genes, AlleleDTO.LATEST_PHENOTYPE_STATUS);
		logger.debug("status count is: " + actualStatusCount);

		assertTrue("Expected at least " + expectedStatusCount + " 'Phenotyping Complete' statuses but found " + actualStatusCount, actualStatusCount.get("Phenotyping Complete") >= expectedStatusCount);
	}
}