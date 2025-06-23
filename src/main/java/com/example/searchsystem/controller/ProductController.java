package com.example.searchsystem.controller;

import com.example.searchsystem.document.ProductDocument;
import com.example.searchsystem.entity.Product;
import com.example.searchsystem.service.ProductSearchService;
import com.example.searchsystem.service.ProductService;
import com.example.searchsystem.service.ProductSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ProductSearchService productSearchService;
    private final ProductSyncService productSyncService;

    @PostMapping
    public Product save(@RequestBody Product product) {
        Product saved = productService.save(product);
        productSyncService.sync(saved);
        return saved;
    }

    @GetMapping
    public List<Product> getAll() {
        return productService.getAll();
    }

    @GetMapping("/search")
    public Page<ProductDocument> search(@RequestParam String keyword,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        return productSearchService.searchWithPagination(keyword, page, size);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
        productSyncService.deleteFromElasticsearch(id);
    }
}
