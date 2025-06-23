package com.example.searchsystem.repository;

import com.example.searchsystem.document.ProductDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, String> {
    Page<ProductDocument> findByNameContainingOrDescriptionContaining(String name, String desc, Pageable pageable);
}
