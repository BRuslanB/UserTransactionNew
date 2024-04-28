package lab.solva.user.transaction.config;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.internal.core.metadata.token.ReplicationFactor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.SessionFactory;
import org.springframework.data.cassandra.config.CqlSessionFactoryBean;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.config.SessionFactoryFactoryBean;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.convert.CassandraConverter;
import org.springframework.data.cassandra.core.convert.MappingCassandraConverter;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
import org.springframework.data.cassandra.core.cql.keyspace.KeyspaceOption;
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.core.mapping.SimpleUserTypeResolver;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import java.util.List;
import java.util.Map;

@Configuration
@EnableCassandraRepositories(basePackages = { "org.springframework.data.cassandra.example" })
public class CassandraConfig {

    @Bean
    public CqlSessionFactoryBean session() {

        CqlSessionFactoryBean session = new CqlSessionFactoryBean();
        session.setContactPoints("127.0.0.1");
        session.setKeyspaceName("mykeyspace");
        session.setLocalDatacenter("datacenter1");
        session.setPort(9042);

//        Map<String, Object> replication = new HashMap<>();
//        replication.put("class", "SimpleStrategy");
//        replication.put("replication_factor", 1);
//
//        session.setKeyspaceCreations(Collections.singletonList(
//                CreateKeyspaceSpecification.createKeyspace("mykeyspace")
//                        .ifNotExists()
//                        .with(KeyspaceOption.REPLICATION, replication)
//        ));

//        session.setKeyspaceCreations(List.of(
//                CreateKeyspaceSpecification.createKeyspace("mykeyspace")
//                        .ifNotExists()
//                        .with(KeyspaceOption.REPLICATION, "{'class': 'SimpleStrategy', 'replication_factor' : 1}")
//        ));

//        session.setKeyspaceCreations(List.of(
//                CreateKeyspaceSpecification.createKeyspace("mykeyspace")
//                        .ifNotExists()
//                        .with(KeyspaceOption.REPLICATION, Map.of(
//                                "class", "SimpleStrategy",
//                                "replication_factor", 1
//                        ))
//        ));

//        session.setKeyspaceCreations(List.of(
//                CreateKeyspaceSpecification.createKeyspace("mykeyspace")
//                        .ifNotExists()
//                        .with(KeyspaceOption.REPLICATION, KeyspaceOption.ReplicationStrategy.SIMPLE_STRATEGY)
//                        .with(KeyspaceOption.DURABLE_WRITES, true)
//        ));

        return session;
    }

    @Bean
    public SessionFactoryFactoryBean sessionFactory(CqlSession session, CassandraConverter converter) {

        SessionFactoryFactoryBean sessionFactory = new SessionFactoryFactoryBean();
        sessionFactory.setSession(session);
        sessionFactory.setConverter(converter);
//        sessionFactory.setSchemaAction(SchemaAction.NONE);
        sessionFactory.setSchemaAction(SchemaAction.CREATE_IF_NOT_EXISTS);

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
