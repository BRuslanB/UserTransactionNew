//package lab.solva.user.transaction.config;
//
//import com.datastax.oss.driver.api.core.CqlSession;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class AppConfig {
//
//    /*
//     * Use the standard Cassandra driver API to create a com.datastax.oss.driver.api.core.CqlSession instance.
//     */
//    public @Bean CqlSession session() {
//        return CqlSession.builder().withKeyspace("mykeyspace").build();
//    }
//}
