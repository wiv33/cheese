package xyz.psawesome.cheese.five.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ConsumerProperties;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import xyz.psawesome.cheese.dto.PBMessage;
import xyz.psawesome.cheese.five.handler.FiveHandler;

import java.util.Properties;

@RequiredArgsConstructor
@Slf4j
@Component
public class KafkaIngest {


    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.consumer.client-id}")
    private String clientId;

    private final ConsumerFactory<String, String> consumerFactory;

    public static final String INGEST_TOPIC = "powerball_5_test";

    private final FiveHandler fiveHandler;

    //    @Bean
    IntegrationFlow fiveIngest() {
        return IntegrationFlow.from(Kafka.messageDrivenChannelAdapter(consumerFactory,
                                KafkaMessageDrivenChannelAdapter.ListenerMode.record, INGEST_TOPIC)
                        .configureListenerContainer(c ->
                                c.ackMode(ContainerProperties.AckMode.MANUAL)
                                        .id("topic1ListenerContainer"))
//                .recoveryCallback(new ErrorMessageSendingRecoverer(errorChannel(),
//                        new RawRecordHeaderErrorMessageStrategy()))
                        .retryTemplate(new RetryTemplate())
                        .filterInRetry(true))
//                 .filter(Message.class, m ->
//                            m.getHeaders().get(KafkaIntegrationHeaders.FLUSH, Integer.class) < 101,
//                    f -> f.throwExceptionOnRejection(true))
                .<String, String>transform(String::toUpperCase)
                .channel(c -> c.queue("listeningFromKafkaResults1"))
                .get();
    }


    private final ObjectMapper objectMapper;

    @Bean
    public IntegrationFlow flow(ConsumerFactory<String, String> cf) {
        ConsumerProperties consumerProperties = new ConsumerProperties(INGEST_TOPIC);
        consumerProperties.setGroupId(groupId);
        consumerProperties.setClientId(clientId);
        var kafkaConsumerProperties = new Properties();
        kafkaConsumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
//        kafkaConsumerProperties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "300");

        consumerProperties.setKafkaConsumerProperties(kafkaConsumerProperties);
        return IntegrationFlow.from(Kafka.inboundChannelAdapter(cf, consumerProperties),
                        e -> e.poller(Pollers.fixedRate(5000)))
                .log("ingest polling")
                .transform(String.class, s -> {
                    try {
                        log.info("ingest message : {}", s);
                        return objectMapper.readValue(s, PBMessage.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .handle(message -> {
                    var payload = (PBMessage) message.getPayload();
                    payload.toFiveResultDocument()
                            .map(s -> fiveHandler.getSyncProcessor().tryEmitNext(s))
                            .subscribe();
                })
                .get();
    }

}
