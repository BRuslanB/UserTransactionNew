//package lab.solva.user.transaction.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.cassandra.config.CqlSessionFactoryBean;
//
//@Configuration
//public class FactoryBeanAppConfig {
//
//    /*
//     * Factory bean that creates the com.datastax.oss.driver.api.core.CqlSession instance
//     */
//    @Bean
//    public CqlSessionFactoryBean session() {
//
//        CqlSessionFactoryBean session = new CqlSessionFactoryBean();
//        session.setContactPoints("localhost");
//        session.setKeyspaceName("mykeyspace");
//
//        return session;
//    }
//}
