package com.example.searchsystem.service;

import com.example.searchsystem.document.ProductDocument;
import com.example.searchsystem.repository.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductSearchService {
    private final ProductSearchRepository productSearchRepository;

    public Page<ProductDocument> search(String keyword, int page, int size) {
        return productSearchRepository.findByNameContainingOrDescriptionContaining(keyword, keyword, PageRequest.of(page, size));
    }

    public ProductDocument save(ProductDocument doc) {
        return productSearchRepository.save(doc);
    }

    public void delete(String id) {
        productSearchRepository.deleteById(id);
    }

    public ProductDocument getById(String id) {
        return productSearchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found in Elasticsearch with id: " + id));
    }
}
