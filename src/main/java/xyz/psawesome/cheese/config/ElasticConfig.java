package xyz.psawesome.cheese.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.elasticsearch.client.RestClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchConfiguration;
import org.springframework.data.elasticsearch.config.EnableReactiveElasticsearchAuditing;
import org.springframework.data.elasticsearch.repository.config.EnableReactiveElasticsearchRepositories;
import org.springframework.data.elasticsearch.support.HttpHeaders;
import org.springframework.util.Assert;


@Slf4j
@Configuration
@EnableReactiveElasticsearchAuditing
@EnableReactiveElasticsearchRepositories
public class ElasticConfig extends ReactiveElasticsearchConfiguration {
    @Value("${spring.elasticsearch.uris}")
    private String uris;

    @NotNull
    @Override
    public ClientConfiguration clientConfiguration() {
        var httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", ContentType.APPLICATION_JSON.getMimeType());
        //var of = RestClientOptions.of(new RestClientOptions(new RestClientOptions(new Request())));
        return ClientConfiguration.builder() //
                .connectedTo(uris)
                .withClientConfigurer(clientConfigurer -> {
                    return clientConfigurer;
                })
                .withDefaultHeaders(httpHeaders)
                .build();
    }

    /**
     * Provides the underlying low level RestClient.
     *
     * @param clientConfiguration configuration for the client, must not be {@literal null}
     * @return RestClient
     */

    @Primary
    @Bean
    public RestClient elasticsearchRestClient(ClientConfiguration clientConfiguration) {
        Assert.notNull(clientConfiguration, "clientConfiguration must not be null");
        return CheeseElasticsearchClients.getRestClient(clientConfiguration);
    }

    @Override
    public ReactiveElasticsearchClient reactiveElasticsearchClient(RestClient restClient) {
        return CheeseElasticsearchClients.createReactive(restClient, null);
    }


}