package xyz.psawesome.cheese.config;

import co.elastic.clients.json.JsonpMapper;
import co.elastic.clients.json.SimpleJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.Endpoint;
import co.elastic.clients.transport.TransportOptions;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.NoArgsConstructor;
import org.elasticsearch.client.RestClient;
import org.jetbrains.annotations.Nullable;
import org.springframework.cloud.function.json.JsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.support.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

@Configuration
public class CheeseRestClientTransport extends RestClientTransport {

    @Override
    public RestClient restClient() {
        return super.restClient();
    }

    @Override
    public RestClientTransport withRequestOptions(@Nullable TransportOptions options) {
        return super.withRequestOptions(options);
    }

    @Override
    public JsonpMapper jsonpMapper() {
        return super.jsonpMapper();
    }

    @Override
    public TransportOptions options() {
        return super.options();
    }

    @Override
    public void close() throws IOException {
        super.close();
    }

    @Override
    public <RequestT, ResponseT, ErrorT> ResponseT performRequest(RequestT request, Endpoint<RequestT, ResponseT, ErrorT> endpoint, @Nullable TransportOptions options) throws IOException {
        return super.performRequest(request, endpoint, options);
    }

    @Override
    public <RequestT, ResponseT, ErrorT> CompletableFuture<ResponseT> performRequestAsync(RequestT request, Endpoint<RequestT, ResponseT, ErrorT> endpoint, @Nullable TransportOptions options) {
        return super.performRequestAsync(request, endpoint, options);
    }

    public CheeseRestClientTransport(RestClient restClient, JsonpMapper mapper, @Nullable TransportOptions options) {
        super(restClient, mapper, Objects.requireNonNullElse(options, null).with(s -> s.addHeader("Content-Type", "application/json")));
    }
}
