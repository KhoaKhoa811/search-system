package com.example.searchsystem.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.example.searchsystem.document.ProductDocument;
import com.example.searchsystem.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ElasticsearchClient elasticsearchClient;

    private static final String INDEX_NAME = "products";

    // Method 1: Search with pagination returning ProductDocument
    public Page<ProductDocument> searchWithPagination(String keyword, int page, int size) {
        try {
            Query query = QueryBuilders.bool()
                    .should(QueryBuilders.match(b -> b.field("name").query(keyword)))
                    .should(QueryBuilders.match(b -> b.field("description").query(keyword)))
                    .minimumShouldMatch("1")
                    .build()._toQuery();

            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index(INDEX_NAME)
                    .query(query)
                    .from(page * size)
                    .size(size)
            );

            SearchResponse<ProductDocument> response = elasticsearchClient.search(searchRequest, ProductDocument.class);

            List<ProductDocument> results = response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());

            return new PageImpl<>(results, PageRequest.of(page, size), response.hits().total().value());

        } catch (IOException e) {
            throw new RuntimeException("Error searching products", e);
        }
    }

    // CRUD Operations for ProductDocument
    public ProductDocument save(ProductDocument doc) {
        try {
            elasticsearchClient.index(i -> i
                    .index(INDEX_NAME)
                    .id(doc.getId())
                    .document(doc)
            );
            return doc;
        } catch (IOException e) {
            throw new RuntimeException("Error saving product", e);
        }
    }

    public void delete(String id) {
        try {
            elasticsearchClient.delete(d -> d
                    .index(INDEX_NAME)
                    .id(id)
            );
        } catch (IOException e) {
            throw new RuntimeException("Error deleting product", e);
        }
    }

    public ProductDocument getById(String id) {
        try {
            var response = elasticsearchClient.get(g -> g
                    .index(INDEX_NAME)
                    .id(id), ProductDocument.class);

            if (response.found()) {
                return response.source();
            } else {
                throw new RuntimeException("Product not found in Elasticsearch with id: " + id);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error getting product", e);
        }
    }
}