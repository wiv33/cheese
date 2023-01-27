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
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import xyz.psawesome.cheese.five.entity.FiveResultDocument;
import xyz.psawesome.cheese.five.entity.FiveType;

import java.time.Duration;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class CheeseFiveResultProvider implements CheeseFiveResultOperations {
    private final RestHighLevelClient client;
    public static final String INDEX_NAME = "cheese_five_results";
    private final ObjectMapper objectMapper;

    @Override
    public Mono<FiveResultDocument> addDocument(FiveResultDocument fiveResult) {
        var result = objectMapper.convertValue(fiveResult, new TypeReference<Map<String, Object>>() {
        });

        var indexRequest = new IndexRequest(INDEX_NAME)
                .id(fiveResult.getFiveResultId())
                .source(result, XContentType.JSON);

        return Mono.create(sink -> {
            var actionListener = new ActionListener<IndexResponse>() {
                @Override
                public void onResponse(IndexResponse indexResponse) {
                    log.info("response : {}", indexResponse.toString());
                    sink.success(fiveResult);
                }

                @Override
                public void onFailure(Exception e) {
                    log.info("on failure : {}", e.getMessage());
                }
            };
            client.indexAsync(indexRequest, RequestOptions.DEFAULT, actionListener);
        });
    }

    @Override
    public Flux<FiveResultDocument> searchTermQueryByAlgorithmAndFiveType(String algorithm, FiveType fiveType) {
        var builder = new SearchSourceBuilder();
        var boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder
                .must(QueryBuilders.matchQuery("algorithm", algorithm))
                .must(QueryBuilders.matchQuery("fiveType", fiveType));
        builder.query(boolQueryBuilder);
        return getFiveResult(builder);
    }

    @Override
    public Flux<FiveResultDocument> searchMatchPhraseAlgorithm(String algorithm, FiveType fiveType) {
        var builder = new SearchSourceBuilder();
        var boolQueryBuilder = new BoolQueryBuilder()
                .must(QueryBuilders.matchPhraseQuery("algorithm", algorithm))
                .must(QueryBuilders.matchPhraseQuery("fiveType", fiveType));

        return getFiveResult(builder.query(boolQueryBuilder));
    }

    @Override
    public Mono<FiveResultDocument> searchMatchPhraseAlgorithmMono(String algorithm, FiveType fiveType) {
        var boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder
                .must(QueryBuilders.matchQuery("algorithm", algorithm))
                .must(QueryBuilders.matchQuery("fiveType", fiveType));
        return getResultMono(new SearchSourceBuilder().query(boolQueryBuilder))
                .timeout(Duration.ofSeconds(3))
                ;
    }

    @Override
    public Mono<FiveResultDocument> searchLastResult(FiveType fiveType) {
        var builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.termQuery("fiveType", fiveType));
        return getResultMono(builder);
    }

    private Flux<FiveResultDocument> getFiveResult(SearchSourceBuilder builder) {
        var searchRequest = new SearchRequest(INDEX_NAME);
        builder.sort(new FieldSortBuilder("createdAt").order(SortOrder.DESC));
        searchRequest.source(builder);
        log.info("Search JSON query: {}", searchRequest.source().toString());
        return Flux.create(sink -> {
            var actionListener = new ActionListener<SearchResponse>() {
                @Override
                public void onResponse(SearchResponse searchResponse) {
                    for (SearchHit hit : searchResponse.getHits()) {
                        try {
                            var fiveResultDocument = objectMapper.readValue(hit.getSourceAsString(), FiveResultDocument.class);
                            fiveResultDocument.setNew(false);
                            sink.next(fiveResultDocument);
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


    private Mono<FiveResultDocument> getResultMono(SearchSourceBuilder builder) {
        var searchRequest = new SearchRequest(INDEX_NAME);
        builder.sort(new FieldSortBuilder("createdAt").order(SortOrder.DESC));
        searchRequest.source(builder);
        log.info("five mono Search JSON query: {}", searchRequest.source().toString());
        return Mono.create(sink -> {
            var actionListener = new ActionListener<SearchResponse>() {
                @Override
                public void onResponse(SearchResponse searchResponse) {
                    for (SearchHit hit : searchResponse.getHits()) {
                        try {
                            var fiveResultDocument = objectMapper.readValue(hit.getSourceAsString(), FiveResultDocument.class);
                            sink.success(fiveResultDocument);
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
