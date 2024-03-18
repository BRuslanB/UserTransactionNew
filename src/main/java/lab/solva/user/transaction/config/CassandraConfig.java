//package lab.solva.user.transaction.config;
//
//import com.datastax.oss.driver.api.core.CqlSession;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
//
//@Configuration
//public class CassandraConfig extends AbstractCassandraConfiguration {
//
//    @Override
//    protected String getKeyspaceName() {
//        return "mykeyspace";
//    }
//
//    @Bean
//    public CqlSession cqlSession() {
//        return new LocalCqlSessionSessionBuilder()
//                .addContactPoints("localhost")
//                .withLocalDatacenter("datacenter1")
//                .build();
//    }
//
//    @Bean
//    public DefaultCqlSessionFactory cqlSessionFactory(CqlSession cqlSession) {
//        return new DefaultCqlSessionFactory(cqlSession);
//    }
//}



//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
//import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
//
//@Configuration
//public class CassandraConfig extends AbstractCassandraConfiguration {
//
//    /*
//     * Provide a contact point to the configuration.
//     */
//    @Override
//    public String getContactPoints() {
//        return "localhost";
//    }
//
//    /*
//     * Provide a keyspace name to the configuration.
//     */
//    @Override
//    public String getKeyspaceName() {
//        return "mykeyspace";
//    }
//}
