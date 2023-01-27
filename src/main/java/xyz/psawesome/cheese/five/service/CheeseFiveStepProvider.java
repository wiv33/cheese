package xyz.psawesome.cheese.five.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import xyz.psawesome.cheese.five.entity.FiveStepDocument;
import xyz.psawesome.cheese.five.entity.FiveType;

import java.time.Duration;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class CheeseFiveStepProvider implements CheeseFiveStepOperations {
    private final RestHighLevelClient client;
    public static final String INDEX_NAME = "cheese_five_steps";

    private final ObjectMapper objectMapper;

    @Override
    public Mono<FiveStepDocument> addDocument(FiveStepDocument fiveStepDocument) {
        var result = objectMapper.convertValue(fiveStepDocument, new TypeReference<Map<String, Object>>() {

        });

        var indexRequest = new IndexRequest(INDEX_NAME)
                .id(fiveStepDocument.getFiveStepId())
                .source(result, XContentType.JSON);

        return Mono.create(sink -> {
            var listener = new ActionListener<IndexResponse>() {
                @Override
                public void onResponse(IndexResponse indexResponse) {
                    sink.success(fiveStepDocument);
                }

                @Override
                public void onFailure(Exception e) {

                }
            };
            client.indexAsync(indexRequest, RequestOptions.DEFAULT, listener);
        });
    }

    @Override
    public Flux<FiveStepDocument> searchById(String fiveStepId) {
        var builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.termQuery("_id", fiveStepId));
        return getStepFlux(builder);
    }

    @Override
    public Mono<FiveStepDocument> searchByIdMono(String fiveStepId) {
        var builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.termQuery("_id", fiveStepId));
        return getStepMono(builder)
                .timeout(Duration.ofSeconds(3));
    }

    @Override
    public Flux<FiveStepDocument> searchLastStep(String userId, String subnetId, FiveType fiveType) {
        var builder = new SearchSourceBuilder();
        var boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.matchQuery("userId", userId))
                .must(QueryBuilders.matchQuery("subnetId", subnetId))
                .must(QueryBuilders.matchQuery("fiveType", fiveType))
        ;
        builder.query(boolQueryBuilder)
        ;

        return getStepFlux(builder)
                .switchIfEmpty(Flux.just(FiveStepDocument.forInitSave(userId, subnetId, "", fiveType)))
                .log("last step search ->>>>>>")
                ;
    }

    @Override
    public Mono<FiveStepDocument> searchLastMono(String userId, String subnetId, FiveType fiveType) {
        var builder = new SearchSourceBuilder();
        var boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder
                .must(QueryBuilders.matchQuery("userId", userId))
                .must(QueryBuilders.matchQuery("subnetId", subnetId))
                .must(QueryBuilders.matchQuery("fiveType", fiveType));
        builder.query(boolQueryBuilder);
        return getStepMono(builder)
                .log("search last mono ->>>>>>>>>>>")
                .timeout(Duration.ofSeconds(3))
                ;
    }

    @Override
    public Flux<FiveStepDocument> searchLastStepByAlgorithm(String connectionId, String subnetId, String algorithm, FiveType fiveType) {
        var builder = new SearchSourceBuilder();
        var boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder
                .must(QueryBuilders.termQuery("userId", connectionId))
                .must(QueryBuilders.termQuery("subnetId", subnetId))
                .must(QueryBuilders.termQuery("algorithm", algorithm))
                .must(QueryBuilders.termQuery("fiveType", fiveType));
        builder.query(boolQueryBuilder);
        return getStepFlux(builder)
                .log("search result ->>>>>>>>>>>")
                ;
    }

    @Override
    public Mono<FiveStepDocument> searchLastStepMonoByAlgorithm(String userId, String subnetId, String algorithm, FiveType fiveType) {
        var boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder
                .must(QueryBuilders.matchQuery("userId", userId))
                .must(QueryBuilders.matchQuery("subnetId", subnetId))
                .must(QueryBuilders.matchQuery("algorithm", algorithm))
                .must(QueryBuilders.matchQuery("fiveType", fiveType));
        return getStepMono(
                new SearchSourceBuilder().query(boolQueryBuilder)
        ).timeout(Duration.ofSeconds(3));
    }

    private Flux<FiveStepDocument> getStepFlux(SearchSourceBuilder builder) {
        var searchRequest = new SearchRequest(INDEX_NAME);
        builder.sort(new FieldSortBuilder("createdAt").order(SortOrder.DESC));
//        builder.size(1);
        searchRequest.source(builder);
        log.info("step flux Search JSON query: {}", searchRequest.source().toString());
        return Flux.create(sink -> {
            var actionListener = new ActionListener<SearchResponse>() {
                @Override
                public void onResponse(SearchResponse searchResponse) {
                    for (SearchHit hit : searchResponse.getHits()) {
                        try {
                            var fiveStepDoc = objectMapper.readValue(hit.getSourceAsString(), FiveStepDocument.class);
                            fiveStepDoc.setNew(false);
                            log.info("Got five step doc for flux: {}", fiveStepDoc);
                            sink.next(fiveStepDoc);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    sink.complete();
                }

                @Override
                public void onFailure(Exception e) {
                    sink.error(e);
                }
            };
            client.searchAsync(searchRequest, RequestOptions.DEFAULT, actionListener);
        });
    }

    private Mono<FiveStepDocument> getStepMono(SearchSourceBuilder builder) {
        var searchRequest = new SearchRequest(INDEX_NAME);
        builder.sort(new FieldSortBuilder("createdAt").order(SortOrder.DESC));
        searchRequest.source(builder);
        log.info("step mono Search JSON query: {}", searchRequest.source().toString());
        return Mono.create(sink -> {
            var actionListener = new ActionListener<SearchResponse>() {
                @Override
                public void onResponse(SearchResponse searchResponse) {
                    for (SearchHit hit : searchResponse.getHits()) {
                        try {
                            var fiveStepDoc = objectMapper.readValue(hit.getSourceAsString(), FiveStepDocument.class);
                            log.info("Got five step doc for mono: {}", fiveStepDoc);
                            fiveStepDoc.setNew(false);
                            sink.success(fiveStepDoc);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                @Override
                public void onFailure(Exception e) {
                    sink.error(e);
                }
            };
            client.searchAsync(searchRequest, RequestOptions.DEFAULT, actionListener);
        });
    }
}

