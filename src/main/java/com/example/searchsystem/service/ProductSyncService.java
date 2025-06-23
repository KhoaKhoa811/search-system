package com.example.searchsystem.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.example.searchsystem.document.ProductDocument;
import com.example.searchsystem.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ProductSyncService {

    private final ElasticsearchClient elasticsearchClient;

    private static final String INDEX_NAME = "products";

    public void sync(Product product) {
        ProductDocument doc = ProductDocument.builder()
                .id(String.valueOf(product.getId()))
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
        try {
            elasticsearchClient.index(i -> i
                    .index(INDEX_NAME)
                    .id(doc.getId())
                    .document(doc)
            );
        } catch (IOException e) {
            throw new RuntimeException("Error syncing product to Elasticsearch", e);
        }
    }

    public void deleteFromElasticsearch(Long productId) {
        try {
            elasticsearchClient.delete(d -> d
                    .index(INDEX_NAME)
                    .id(String.valueOf(productId))
            );
        } catch (IOException e) {
            throw new RuntimeException("Error deleting product from Elasticsearch", e);
        }
    }
}
