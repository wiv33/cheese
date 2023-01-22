package xyz.psawesome.cheese.five.repository;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import xyz.psawesome.cheese.five.entity.FiveResultDocument;

import static xyz.psawesome.cheese.config.MongoDBConfig.DB_NAME;

@SpringBootTest
class FiveAnswerDocumentRepositoryTest {

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    @Autowired
    MongoClient mongoClient;
    @Test
    void saveTest() {
//        EasyRandom easyRandom = new EasyRandom();
//        FiveResultDocument fiveResultDocument = easyRandom.nextObject(FiveResultDocument.class);
//        StepVerifier.create(reactiveMongoTemplate.save(Mono.just(fiveResultDocument)))
//                    .assertNext(Assertions::assertNotNull)
//                    .verifyComplete();

        var reactiveMongoTemplate1 = new ReactiveMongoTemplate(new SimpleReactiveMongoDatabaseFactory(mongoClient, DB_NAME));
        reactiveMongoTemplate1.findAll(FiveResultDocument.class)
                .subscribe(System.out::println);

//        System.out.println("fiveResult = " + fiveResultDocument);
    }
}