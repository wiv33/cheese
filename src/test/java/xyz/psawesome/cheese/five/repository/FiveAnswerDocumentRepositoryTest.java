package xyz.psawesome.cheese.five.repository;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import xyz.psawesome.cheese.five.entity.FiveResultDocument;

@SpringBootTest
class FiveAnswerDocumentRepositoryTest {

    @Autowired
    ReactiveMongoTemplate cheeseFiveMongoTemplate;

    @Test
    void saveTest() {
        EasyRandom easyRandom = new EasyRandom();
        FiveResultDocument fiveResultDocument = easyRandom.nextObject(FiveResultDocument.class);
        StepVerifier.create(cheeseFiveMongoTemplate.save(Mono.just(fiveResultDocument)))
                    .assertNext(Assertions::assertNotNull)
                    .verifyComplete();

        System.out.println("fiveResult = " + fiveResultDocument);

    }
}