package com.example.searchsystem.service;

import com.example.searchsystem.document.ProductDocument;
import com.example.searchsystem.entity.Product;
import com.example.searchsystem.repository.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductSyncService {
    private final ProductSearchRepository productSearchRepository;

    public void sync(Product product) {
        ProductDocument doc = ProductDocument.builder()
                .id(String.valueOf(product.getId()))
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
        productSearchRepository.save(doc);
    }

    public void deleteFromElasticsearch(Long productId) {
        productSearchRepository.deleteById(String.valueOf(productId));
    }
}
