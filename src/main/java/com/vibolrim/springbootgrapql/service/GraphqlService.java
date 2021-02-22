package com.vibolrim.springbootgrapql.service;

import com.vibolrim.springbootgrapql.model.Book;
import com.vibolrim.springbootgrapql.repository.BookRepository;
import com.vibolrim.springbootgrapql.service.datafetcher.AllBooksDataFetcher;
import com.vibolrim.springbootgrapql.service.datafetcher.BookDataFetcher;
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
import java.util.stream.Stream;

@Service
public class GraphqlService {

    @Value("classpath:books.graphql")
    Resource resource;

    private GraphQL graphQL;

    @Autowired
    private AllBooksDataFetcher allBooksDataFetcher;

    @Autowired
    private BookDataFetcher bookDataFetcher;

    @Autowired
    private BookRepository bookRepository;


    // Load Schema when application start
    @PostConstruct
    private void loadSchema() throws IOException {

        loadDataIntoHSQL();
        File schemaFile = resource.getFile();
        TypeDefinitionRegistry typeDefinitionRegistry = new SchemaParser().parse(schemaFile);
        RuntimeWiring runtimeWiring = buildRunTimeWritting();
        GraphQLSchema graphQLSchema = new SchemaGenerator().makeExecutableSchema(typeDefinitionRegistry,runtimeWiring);
        graphQL= GraphQL.newGraphQL(graphQLSchema).build();
    }

    private void loadDataIntoHSQL() {
        Stream.of(
                new Book("1", "Linux book", "Ray Mesterio", new String[] {
                        "Dev ops"
                }, "2020-02-19"),
                new Book("2", "Java Programming Language", "Jonh Cena", new String[] {
                        "Mina mono", "Leonel messi"
                }, "2020-02-19"),
                new Book("1", "how to sleep well", "Fia", new String[] {
                        "Cris"
                }, "2020-02-19"),
                new Book("1", "IT books", "Test", new String[] {
                        "Dev Test"
                }, "2020-02-19")
        ).forEach(book -> {
            bookRepository.save(book);
        });
    }

    private RuntimeWiring buildRunTimeWritting() {
        return RuntimeWiring.newRuntimeWiring()
                .type("Query", typeWringting ->
                    typeWringting
                            .dataFetcher("allBooks", allBooksDataFetcher)
                            .dataFetcher("book", bookDataFetcher)).build();

    }

    public GraphQL getGraphQL() {
        return graphQL;
    }
}
