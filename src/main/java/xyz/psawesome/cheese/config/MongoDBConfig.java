package xyz.psawesome.cheese.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import xyz.psawesome.cheese.CheeseApplication;

@Configuration
@EnableReactiveMongoAuditing
@EnableReactiveMongoRepositories(basePackageClasses = CheeseApplication.class)
public class MongoDBConfig extends AbstractReactiveMongoConfiguration {

    public static final String DB_NAME = "cheese-five";

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create();
    }

    @Bean
    public ReactiveMongoTemplate fiveMongoTemplate(MongoClient mongoClient) {
        return new ReactiveMongoTemplate(mongoClient, DB_NAME);
    }

    @NotNull
    @Override
    protected String getDatabaseName() {
        return DB_NAME;
    }
}
