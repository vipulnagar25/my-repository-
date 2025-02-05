package com.reactive.orgi_portal.service;

import com.reactive.orgi_portal.service.datafetcher.AllOgriUsersDataFetcher;
import com.reactive.orgi_portal.service.datafetcher.OgriUsersDataFetcher;
//import com.reactive.orgi_portal.service.datafetcher.SaveOgriUsersDataFetcher;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

@Service
public class GraphqlOgriUserService {
    @Value("classpath:schema.graphqls")
    Resource resource;

    private GraphQL graphQL;

    @Autowired
    AllOgriUsersDataFetcher allOgriUsersDataFetcher;
    @Autowired
    OgriUsersDataFetcher ogriUsersDataFetcher;


    @PostConstruct
    private void loadSchema() throws IOException {
        File schemaFile = resource.getFile();
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(schemaFile);
        RuntimeWiring wiring = buildRuntimeWiring();
        GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(typeRegistry, wiring);
        graphQL = GraphQL.newGraphQL(schema).build();
    }

    private RuntimeWiring buildRuntimeWiring() {
        return RuntimeWiring.newRuntimeWiring().type
                ("Query", typeWiring -> typeWiring
                        .dataFetcher("allOgriUsers", allOgriUsersDataFetcher).dataFetcher("ogriUser", ogriUsersDataFetcher)
                )

                .build();
    }


    public GraphQL getGraphQL() {
        return graphQL;
    }
}
