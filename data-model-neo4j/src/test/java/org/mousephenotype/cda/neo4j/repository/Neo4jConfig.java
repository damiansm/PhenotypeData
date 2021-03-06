
package org.mousephenotype.cda.neo4j.repository;

import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;

import javax.validation.constraints.NotNull;

/**
 * Created by jmason on 18/03/2017.
 */
@Configuration
@EnableNeo4jRepositories(basePackages = "org.mousephenotype.cda.neo4j.repository")
//@ComponentScan(value = "org.mousephenotype.cda.neo4j")
@PropertySource("file:${user.home}/configfiles/${profile:dev}/test.properties")
public class Neo4jConfig {

    @NotNull
    @Value("${neo4jDbPath}")
    private String neo4jDbPath2;

    @NotNull
    @Value("${owlpath}")
    protected String owlpath;

    @Bean
    public org.neo4j.ogm.config.Configuration getConfiguration() {
        org.neo4j.ogm.config.Configuration config = new org.neo4j.ogm.config.Configuration();

        // To persist the database, uncomment this section
        //String pathToDb = Paths.get(".").toAbsolutePath().normalize().toString() + "/target//Users/ckchen/Documents/Neo4j/impc.neo4";
        String pathToDb = neo4jDbPath2;
        config
                .driverConfiguration()
                .setDriverClassName("org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver")
                .setURI("file://" + pathToDb);

        System.out.println(config);

        return config;
    }




    @Bean
    public SessionFactory sessionFactory() {
        return new SessionFactory(getConfiguration(), "org.mousephenotype.cda.neo4j");  // both entity and repository
    }

    @Bean
    public Neo4jTransactionManager transactionManager() {
        return new Neo4jTransactionManager(sessionFactory());
    }

}
