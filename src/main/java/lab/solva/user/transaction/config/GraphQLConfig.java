package lab.solva.user.transaction.config;

import graphql.kickstart.servlet.GraphQLHttpServlet;
import graphql.kickstart.tools.SchemaParser;
import graphql.schema.GraphQLSchema;
import lab.solva.user.transaction.resolver.MutationResolver;
import lab.solva.user.transaction.resolver.QueryResolver;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SuppressWarnings("unused")
public class GraphQLConfig {
    @Bean
    public GraphQLSchema graphQLSchema(MutationResolver mutationResolver, QueryResolver queryResolver) {

        return SchemaParser.newParser()
                .files("graphql/schema.graphqls") // Specify the path to the schema
                .resolvers(mutationResolver, queryResolver)
                .build()
                .makeExecutableSchema();
    }

    @Bean
    public GraphQLHttpServlet graphQLHttpServlet(GraphQLSchema graphQLSchema) {
        return GraphQLHttpServlet.with(graphQLSchema);
    }

    @Bean
    public ServletRegistrationBean<GraphQLHttpServlet> customGraphQLServletRegistrationBean(GraphQLHttpServlet graphQLHttpServlet) {
        return new ServletRegistrationBean<>(graphQLHttpServlet, "/graphql");
    }
}
