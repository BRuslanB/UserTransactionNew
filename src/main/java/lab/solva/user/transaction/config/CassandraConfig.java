package lab.solva.user.transaction.config;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.SessionFactory;
import org.springframework.data.cassandra.config.CqlSessionFactoryBean;
import org.springframework.data.cassandra.config.SessionFactoryFactoryBean;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.convert.CassandraConverter;
import org.springframework.data.cassandra.core.convert.MappingCassandraConverter;
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.core.mapping.SimpleUserTypeResolver;

@Configuration
public class CassandraConfig {

    @Value("${CASSANDRA_CONTACT_POINTS}")
    private String contactPoints;

    @Value("${CASSANDRA_KEYSPACE}")
    private String keyspaceName;

    @Value("${CASSANDRA_LOCAL_DATACENTER}")
    private String localDatacenter;

    @Value("${CASSANDRA_PORT}")
    private int port;

    public CassandraConfig() {
    }

    @Bean
    public CqlSessionFactoryBean session() {

        // For debugging
        System.out.println("*** CASSANDRA_CONTACT_POINTS: " + contactPoints);
        System.out.println("*** CASSANDRA_KEYSPACE: " + keyspaceName);
        System.out.println("*** CASSANDRA_LOCAL_DATACENTER: " + localDatacenter);
        System.out.println("*** CASSANDRA_PORT: " + port);

        CqlSessionFactoryBean session = new CqlSessionFactoryBean();

        session.setContactPoints(contactPoints);
        session.setKeyspaceName(keyspaceName);
        session.setLocalDatacenter(localDatacenter);
        session.setPort(port);

        return session;
    }

    @Bean
    public SessionFactoryFactoryBean sessionFactory(CqlSession session, CassandraConverter converter) {

        SessionFactoryFactoryBean sessionFactory = new SessionFactoryFactoryBean();
        sessionFactory.setSession(session);
        sessionFactory.setConverter(converter);

        return sessionFactory;
    }

    @Bean
    public CassandraMappingContext mappingContext() {
        return new CassandraMappingContext();
    }

    @Bean
    public CassandraConverter converter(CqlSession cqlSession, CassandraMappingContext mappingContext) {

        MappingCassandraConverter cassandraConverter = new MappingCassandraConverter(mappingContext);
        cassandraConverter.setUserTypeResolver(new SimpleUserTypeResolver(cqlSession));

        return cassandraConverter;
    }

    @Bean
    public CassandraOperations cassandraTemplate(SessionFactory sessionFactory, CassandraConverter converter) {
        return new CassandraTemplate(sessionFactory, converter);
    }
}
