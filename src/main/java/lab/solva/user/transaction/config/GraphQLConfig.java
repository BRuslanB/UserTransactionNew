package lab.solva.user.transaction.config;

import graphql.kickstart.servlet.GraphQLHttpServlet;
import graphql.kickstart.tools.SchemaParser;
import graphql.schema.GraphQLSchema;
import lab.solva.user.transaction.resolver.MutationResolver;
import lab.solva.user.transaction.resolver.QueryResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GraphQLConfig {

//    private final MutationResolver mutationResolver;
//    private final QueryResolver queryResolver;

    @Bean
    public GraphQLSchema graphQLSchema(MutationResolver mutationResolver, QueryResolver queryResolver) {
//        SchemaParserDictionary dictionary = new SchemaParserDictionary();
//        dictionary.add(ErrorMutation.class);
//        dictionary.add(SuccessMutation.class);

        return SchemaParser.newParser()
                .files("graphql/schema.graphqls") // Указываем путь к схеме
                .resolvers(mutationResolver, queryResolver)
//                .dictionary(dictionary.getDictionary())
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
