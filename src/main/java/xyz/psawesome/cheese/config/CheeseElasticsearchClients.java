package xyz.psawesome.cheese.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.TransportOptions;
import co.elastic.clients.transport.rest_client.RestClientOptions;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.common.Nullable;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.ClientLogger;
import org.springframework.data.elasticsearch.client.elc.AutoCloseableElasticsearchClient;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.support.HttpHeaders;
import org.springframework.util.Assert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Utility class to create the different Elasticsearch clients
 *
 * @author Peter-Josef Meisch
 * @since 4.4
 */
public final class CheeseElasticsearchClients {
    /**
     * Name of whose value can be used to correlate log messages for this request.
     */
    private static final String LOG_ID_ATTRIBUTE = CheeseElasticsearchClients.class.getName() + ".LOG_ID";
    private static final String X_SPRING_DATA_ELASTICSEARCH_CLIENT = "X-SpringDataElasticsearch-Client";
    private static final String IMPERATIVE_CLIENT = "imperative";
    private static final String REACTIVE_CLIENT = "reactive";

    /**
     * Creates a new {@link ReactiveElasticsearchClient}
     *
     * @param clientConfiguration configuration options, must not be {@literal null}.
     * @return the {@link ReactiveElasticsearchClient}
     */
    public static ReactiveElasticsearchClient createReactive(ClientConfiguration clientConfiguration) {

        Assert.notNull(clientConfiguration, "clientConfiguration must not be null");

        return createReactive(getRestClient(clientConfiguration), null);
    }

    /**
     * Creates a new {@link ReactiveElasticsearchClient}
     *
     * @param clientConfiguration configuration options, must not be {@literal null}.
     * @param transportOptions    options to be added to each request.
     * @return the {@link ReactiveElasticsearchClient}
     */
    public static ReactiveElasticsearchClient createReactive(ClientConfiguration clientConfiguration,
                                                             @Nullable TransportOptions transportOptions) {

        Assert.notNull(clientConfiguration, "ClientConfiguration must not be null!");

        return createReactive(getRestClient(clientConfiguration), transportOptions);
    }

    /**
     * Creates a new {@link ReactiveElasticsearchClient}.
     *
     * @param restClient the underlying {@link RestClient}
     * @return the {@link ReactiveElasticsearchClient}
     */
    public static ReactiveElasticsearchClient createReactive(RestClient restClient) {
        return createReactive(restClient, null);
    }

    /**
     * Creates a new {@link ReactiveElasticsearchClient}.
     *
     * @param restClient       the underlying {@link RestClient}
     * @param transportOptions options to be added to each request.
     * @return the {@link ReactiveElasticsearchClient}
     */
    public static ReactiveElasticsearchClient createReactive(RestClient restClient,
                                                             @Nullable TransportOptions transportOptions) {
        return new ReactiveElasticsearchClient(getElasticsearchTransport(restClient, REACTIVE_CLIENT, transportOptions));
    }

    /**
     * Creates a new imperative {@link ElasticsearchClient}
     *
     * @param clientConfiguration configuration options, must not be {@literal null}.
     * @return the {@link ElasticsearchClient}
     */
    public static ElasticsearchClient createImperative(ClientConfiguration clientConfiguration) {
        return createImperative(getRestClient(clientConfiguration), null);
    }

    /**
     * Creates a new imperative {@link ElasticsearchClient}
     *
     * @param clientConfiguration configuration options, must not be {@literal null}.
     * @param transportOptions    options to be added to each request.
     * @return the {@link ElasticsearchClient}
     */
    public static ElasticsearchClient createImperative(ClientConfiguration clientConfiguration,
                                                       TransportOptions transportOptions) {
        return createImperative(getRestClient(clientConfiguration), transportOptions);
    }

    /**
     * Creates a new imperative {@link ElasticsearchClient}
     *
     * @param restClient the RestClient to use
     * @return the {@link ElasticsearchClient}
     */
    public static ElasticsearchClient createImperative(RestClient restClient) {
        return createImperative(restClient, null);
    }

    /**
     * Creates a new imperative {@link ElasticsearchClient}
     *
     * @param restClient       the RestClient to use
     * @param transportOptions options to be added to each request.
     * @return the {@link ElasticsearchClient}
     */
    public static ElasticsearchClient createImperative(RestClient restClient,
                                                       @Nullable TransportOptions transportOptions) {

        Assert.notNull(restClient, "restClient must not be null");

        ElasticsearchTransport transport = getElasticsearchTransport(restClient, IMPERATIVE_CLIENT, transportOptions);

        return new AutoCloseableElasticsearchClient(transport);
    }

    /**
     * Creates a low level {@link RestClient} for the given configuration.
     *
     * @param clientConfiguration must not be {@literal null}
     * @return the {@link RestClient}
     */
    public static RestClient getRestClient(ClientConfiguration clientConfiguration) {

        return getRestClientBuilder(clientConfiguration)
                .setDefaultHeaders(List.of(new BasicHeader("Content-Type", ContentType.APPLICATION_JSON.toString())).toArray(Header[]::new))
                .build();
    }

    private static RestClientBuilder getRestClientBuilder(ClientConfiguration clientConfiguration) {
        HttpHost[] httpHosts = formattedHosts(clientConfiguration.getEndpoints(), clientConfiguration.useSsl()).stream()
                .map(HttpHost::create).toArray(HttpHost[]::new);
        RestClientBuilder builder = RestClient.builder(httpHosts);
        var httpClientConfigCallback = (RestClientBuilder.HttpClientConfigCallback) httpClientBuilder ->
                httpClientBuilder
                        .setDefaultCredentialsProvider(new BasicCredentialsProvider())
                        // this request & response header manipulation helps get around newer (>=7.16) versions
                        // of elasticsearch-java client not working with older (<7.14) versions of Elasticsearch
                        // server
                        .setDefaultHeaders(
                                List.of(
                                        new BasicHeader(
                                                "Content-Type", ContentType.APPLICATION_JSON.toString())))
                        .addInterceptorLast(
                                (HttpResponseInterceptor)
                                        (response, context) ->
                                                response.addHeader("X-Elastic-Product", "Elasticsearch"));

        builder.setHttpClientConfigCallback(httpClientConfigCallback);
        if (clientConfiguration.getPathPrefix() != null) {
            builder.setPathPrefix(clientConfiguration.getPathPrefix());
        }

        HttpHeaders headers = clientConfiguration.getDefaultHeaders();

        if (!headers.isEmpty()) {
            builder.setDefaultHeaders(toHeaderArray(headers));
        }

        builder.setHttpClientConfigCallback(clientBuilder -> {
            clientConfiguration.getSslContext().ifPresent(clientBuilder::setSSLContext);
            clientConfiguration.getHostNameVerifier().ifPresent(clientBuilder::setSSLHostnameVerifier);
            clientBuilder.addInterceptorLast(new CustomHeaderInjector(clientConfiguration.getHeadersSupplier()));

            if (ClientLogger.isEnabled()) {
                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();

                clientBuilder.addInterceptorLast((HttpRequestInterceptor) interceptor);
                clientBuilder.addInterceptorLast((HttpResponseInterceptor) interceptor);
            }
            clientBuilder
                    .setDefaultCredentialsProvider(new BasicCredentialsProvider())
                    // this request & response header manipulation helps get around newer (>=7.16) versions
                    // of elasticsearch-java client not working with older (<7.14) versions of Elasticsearch
                    // server
                    .setDefaultHeaders(
                            List.of(
                                    new BasicHeader(
                                            "Content-Type", ContentType.APPLICATION_JSON.toString())))
                    .addInterceptorLast(
                            (HttpResponseInterceptor)
                                    (response, context) ->
                                            response.addHeader("X-Elastic-Product", "Elasticsearch"));

            RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
            Duration connectTimeout = clientConfiguration.getConnectTimeout();

            if (!connectTimeout.isNegative()) {
                requestConfigBuilder.setConnectTimeout(Math.toIntExact(connectTimeout.toMillis()));
            }

            Duration socketTimeout = clientConfiguration.getSocketTimeout();

            if (!socketTimeout.isNegative()) {
                requestConfigBuilder.setSocketTimeout(Math.toIntExact(socketTimeout.toMillis()));
                requestConfigBuilder.setConnectionRequestTimeout(Math.toIntExact(socketTimeout.toMillis()));
            }

            clientBuilder.setDefaultRequestConfig(requestConfigBuilder.build());

            clientConfiguration.getProxy().map(HttpHost::create).ifPresent(clientBuilder::setProxy);

            for (ClientConfiguration.ClientConfigurationCallback<?> clientConfigurer : clientConfiguration
                    .getClientConfigurers()) {
                if (clientConfigurer instanceof ElasticsearchHttpClientConfigurationCallback restClientConfigurationCallback) {
                    clientBuilder = restClientConfigurationCallback.configure(clientBuilder);
                }
            }

            return clientBuilder;
        });

        for (ClientConfiguration.ClientConfigurationCallback<?> clientConfigurationCallback : clientConfiguration
                .getClientConfigurers()) {
            if (clientConfigurationCallback instanceof ElasticsearchRestClientConfigurationCallback configurationCallback) {
                builder = configurationCallback.configure(builder);
            }
        }
        return builder;
    }

    private static ElasticsearchTransport getElasticsearchTransport(RestClient restClient,
                                                                    String clientType,
                                                                    @Nullable TransportOptions transportOptions) {

        TransportOptions.Builder transportOptionsBuilder = transportOptions != null ? transportOptions.toBuilder()
                : new RestClientOptions(RequestOptions.DEFAULT)
                .toBuilder();
        transportOptionsBuilder.addHeader("X-Elastic-Product", "Elasticsearch");
        ContentType jsonContentType = ContentType.APPLICATION_JSON;

        Consumer<String> setHeaderIfNotPresent = header -> {
            if (transportOptionsBuilder.build().headers().stream() //
                    .noneMatch((h) -> h.getKey().equalsIgnoreCase(header))) {
                // need to add the compatibility header, this is only done automatically when not passing in custom options.
                // code copied from RestClientTransport as it is not available outside the package
                transportOptionsBuilder.addHeader(header, jsonContentType.toString());
            }
        };

        setHeaderIfNotPresent.accept("Content-Type");
        setHeaderIfNotPresent.accept("Accept");

        TransportOptions transportOptionsWithHeader = transportOptionsBuilder
                .addHeader(X_SPRING_DATA_ELASTICSEARCH_CLIENT, clientType)
                .build();
        return new RestClientTransport(restClient, new JacksonJsonpMapper(), transportOptionsWithHeader);
    }

    private static List<String> formattedHosts(List<InetSocketAddress> hosts, boolean useSsl) {
        return hosts.stream().map(it -> (useSsl ? "https" : "http") + "://" + it.getHostString() + ":" + it.getPort())
                .collect(Collectors.toList());
    }

    private static org.apache.http.Header[] toHeaderArray(HttpHeaders headers) {
        return headers.entrySet().stream() //
                .flatMap(entry -> entry.getValue().stream() //
                        .map(value -> new BasicHeader(entry.getKey(), value))) //
                .toArray(org.apache.http.Header[]::new);
    }

    /**
     * Logging interceptors for Elasticsearch client logging.
     *
     * @since 4.4
     * @deprecated since 5.0
     */
    @Deprecated
    private static class HttpLoggingInterceptor implements HttpResponseInterceptor, HttpRequestInterceptor {

        @Override
        public void process(HttpRequest request, HttpContext context) throws IOException {

            String logId = (String) context.getAttribute(LOG_ID_ATTRIBUTE);

            if (logId == null) {
                logId = ClientLogger.newLogId();
                context.setAttribute(LOG_ID_ATTRIBUTE, logId);
            }

            String headers = Arrays.stream(request.getAllHeaders())
                    .map(header -> header.getName()
                            + ((header.getName().equals("Authorization")) ? ": *****" : ": " + header.getValue()))
                    .collect(Collectors.joining(", ", "[", "]"));

            if (request instanceof HttpEntityEnclosingRequest entityRequest
                    && ((HttpEntityEnclosingRequest) request).getEntity() != null) {

                HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                entity.writeTo(buffer);

                if (!entity.isRepeatable()) {
                    entityRequest.setEntity(new ByteArrayEntity(buffer.toByteArray()));
                }

                ClientLogger.logRequest(logId, request.getRequestLine().getMethod(), request.getRequestLine().getUri(), "",
                        headers, buffer::toString);
            } else {
                ClientLogger.logRequest(logId, request.getRequestLine().getMethod(), request.getRequestLine().getUri(), "",
                        headers);
            }
        }

        @Override
        public void process(HttpResponse response, HttpContext context) throws IOException {

            String logId = (String) context.getAttribute(LOG_ID_ATTRIBUTE);

            String headers = Arrays.stream(response.getAllHeaders())
                    .map(header -> header.getName()
                            + ((header.getName().equals("Authorization")) ? ": *****" : ": " + header.getValue()))
                    .collect(Collectors.joining(", ", "[", "]"));

            // no way of logging the body, in this callback, it is not read yet, later there is no callback possibility in
            // RestClient or RestClientTransport
            ClientLogger.logRawResponse(logId, response.getStatusLine().getStatusCode(), headers);
        }
    }

    /**
     * Interceptor to inject custom supplied headers.
     *
     * @since 4.4
     */
    private static class CustomHeaderInjector implements HttpRequestInterceptor {

        public CustomHeaderInjector(Supplier<HttpHeaders> headersSupplier) {
            this.headersSupplier = headersSupplier;
        }

        private final Supplier<HttpHeaders> headersSupplier;

        @Override
        public void process(HttpRequest request, HttpContext context) {
            HttpHeaders httpHeaders = headersSupplier.get();

            if (httpHeaders != null && !httpHeaders.isEmpty()) {
                Arrays.stream(toHeaderArray(httpHeaders)).forEach(request::addHeader);
            }
        }
    }

    /**
     * {@link org.springframework.data.elasticsearch.client.ClientConfiguration.ClientConfigurationCallback} to configure
     * the Elasticsearch RestClient's Http client with a {@link HttpAsyncClientBuilder}
     *
     * @since 4.4
     */
    public interface ElasticsearchHttpClientConfigurationCallback
            extends ClientConfiguration.ClientConfigurationCallback<HttpAsyncClientBuilder> {

        static ElasticsearchHttpClientConfigurationCallback from(
                Function<HttpAsyncClientBuilder, HttpAsyncClientBuilder> httpClientBuilderCallback) {

            Assert.notNull(httpClientBuilderCallback, "httpClientBuilderCallback must not be null");

            return httpClientBuilderCallback::apply;
        }
    }

    /**
     * {@link org.springframework.data.elasticsearch.client.ClientConfiguration.ClientConfigurationCallback} to configure
     * the RestClient client with a {@link RestClientBuilder}
     *
     * @since 5.0
     */
    public interface ElasticsearchRestClientConfigurationCallback
            extends ClientConfiguration.ClientConfigurationCallback<RestClientBuilder> {

        static ElasticsearchRestClientConfigurationCallback from(
                Function<RestClientBuilder, RestClientBuilder> restClientBuilderCallback) {

            Assert.notNull(restClientBuilderCallback, "restClientBuilderCallback must not be null");

            return restClientBuilderCallback::apply;
        }
    }
}
