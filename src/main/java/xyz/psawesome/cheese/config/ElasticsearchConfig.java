package xyz.psawesome.cheese.config;

import co.elastic.clients.json.JsonpMapper;
import co.elastic.clients.json.SimpleJsonpMapper;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.TransportOptions;
import co.elastic.clients.transport.rest_client.RestClientOptions;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    @Bean
    public JsonpMapper jsonpMapper() {
        return new JacksonJsonpMapper();
    }

    @Bean
    public TransportOptions transportOptions() {
        return new RestClientOptions(RequestOptions.DEFAULT);
    }

}