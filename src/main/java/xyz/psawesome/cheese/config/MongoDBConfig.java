package xyz.psawesome.cheese.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoDriverInformation;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.bson.UuidRepresentation;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import xyz.psawesome.cheese.CheeseApplication;


@Slf4j
@Primary
@Configuration
@EnableReactiveMongoAuditing
@EnableReactiveMongoRepositories(basePackageClasses = CheeseApplication.class)
@ComponentScan
public class MongoDBConfig extends AbstractReactiveMongoConfiguration {

    public static final String DB_NAME = "cheese_five";

    @Autowired
    Environment environment;


    @Primary
    @Bean
    public MongoClient mongoClient() {
        var driverName = environment.getProperty("cheese-mongo.driver-name");
        var driverPlatform = environment.getProperty("cheese-mongo.driver-platform");
        var driverVersion = environment.getProperty("cheese-mongo.driver-version");

        var uri = environment.getProperty("spring.data.mongodb.uri");
        var host = environment.getProperty("spring.data.mongodb.host");
        var port = environment.getProperty("spring.data.mongodb.port");
        var database = environment.getProperty("spring.data.mongodb.database");
        var username = environment.getProperty("spring.data.mongodb.username");
        var password = environment.getProperty("spring.data.mongodb.password");

        return MongoClients.create(
                MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString(
                                String.format("mongodb://%s:%s/%s?authSource=admin", host, port, database))
                        ).uuidRepresentation(UuidRepresentation.STANDARD)
                        .build(),
                MongoDriverInformation.builder()
                        .driverName(driverName)
                        .driverPlatform(driverPlatform)
                        .driverVersion(driverVersion)
                        .build());
    }

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate(MongoClient mongoClient) {
        return new ReactiveMongoTemplate(mongoClient, DB_NAME);
    }


    @Primary
    @Bean
    public MongoClientSettingsBuilderCustomizer customizer(@Value("${spring.data.mongodb.uri}") String uri) {
        ConnectionString connection = new ConnectionString(uri);

        log.info("mongo customizer uri : {}", uri);
        return settings -> settings.applyConnectionString(connection);
    }

    @NotNull
    @Override
    protected String getDatabaseName() {
        return DB_NAME;
    }
}
