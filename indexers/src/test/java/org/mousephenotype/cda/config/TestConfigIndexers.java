package org.mousephenotype.cda.config;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.mousephenotype.cda.owl.OntologyParserFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.util.Properties;


/**
 * TestConfig sets up the in memory database for supporting the database tests.
 * @author jmason
 */

@Configuration
@EnableTransactionManagement
@EnableAutoConfiguration
@ComponentScan({"org.mousephenotype.cda"})
@EnableSolrRepositories(basePackages = {"org.mousephenotype.cda.solr.repositories"}, multicoreSupport = true)
public class TestConfigIndexers {

	public static final int QUEUE_SIZE = 10000;
	public static final int THREAD_COUNT = 3;

	@NotNull
	@Value("${buildIndexesSolrUrl}")
	private String writeSolrBaseUrl;

	@Value("http:${solr_url}")
	String solrBaseUrl;

	@NotNull
	@Value("${owlpath}")
	protected String owlpath;

	// Required for spring-data-solr repositories
	@Bean
	public SolrClient solrClient() { return new HttpSolrClient(solrBaseUrl); }

	@Bean
	public SolrOperations solrTemplate() { return new SolrTemplate(solrClient()); }

	@Bean
	@Primary
	@ConfigurationProperties(prefix = "datasource.komp2")
	public DataSource komp2DataSource() {
		DataSource ds = DataSourceBuilder.create().build();
		return ds;
	}

	@Bean
	@ConfigurationProperties(prefix = "datasource.admintools")
	public DataSource admintoolsDataSource() {
		DataSource ds = DataSourceBuilder.create().build();
		return ds;
	}


	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setDataSource(komp2DataSource());
		emf.setPackagesToScan(new String[]{"org.mousephenotype.cda.db.pojo", "org.mousephenotype.cda.db.entity"});

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		emf.setJpaVendorAdapter(vendorAdapter);
		emf.setJpaProperties(buildHibernateProperties());

		return emf;
	}

	protected Properties buildHibernateProperties() {
		Properties hibernateProperties = new Properties();

		hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
		hibernateProperties.setProperty("hibernate.show_sql", "false");
		hibernateProperties.setProperty("hibernate.use_sql_comments", "false");
		hibernateProperties.setProperty("hibernate.format_sql", "false");
		hibernateProperties.setProperty("hibernate.generate_statistics", "false");
		hibernateProperties.setProperty("hibernate.current_session_context_class", "thread");

		return hibernateProperties;
	}

	@Bean
	@Primary
	@PersistenceContext(name = "komp2Context")
	public LocalContainerEntityManagerFactoryBean emf(EntityManagerFactoryBuilder builder) {
		return builder
			.dataSource(komp2DataSource())
			.packages("org.mousephenotype.cda.db")
			.persistenceUnit("komp2")
			.build();
	}

	@Bean(name = "sessionFactory")
	public LocalSessionFactoryBean sessionFactory() {
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(komp2DataSource());
		sessionFactory.setPackagesToScan("org.mousephenotype.cda.db");
		return sessionFactory;
	}

	@Bean
	@Qualifier("phenodigmIndexingSolrClient")
	public SolrClient phenodigmCore() {
		return new ConcurrentUpdateSolrClient(writeSolrBaseUrl + "/phenodigm", QUEUE_SIZE, THREAD_COUNT);
	}

	@Bean
    public OntologyParserFactory ontologyParserFactory() {
	    return new OntologyParserFactory(komp2DataSource(), owlpath);
    }
}