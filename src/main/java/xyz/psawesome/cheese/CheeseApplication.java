package xyz.psawesome.cheese;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@EnableReactiveMongoRepositories
@SpringBootApplication
public class CheeseApplication {

    public static void main(String[] args) {
        SpringApplication.run(CheeseApplication.class, args);
    }

}
