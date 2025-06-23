package com.example.searchsystem.config;

import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class ElasticBootstrap {

    private final RestClient restClient;

    public ElasticBootstrap(RestClient restClient) {
        this.restClient = restClient;
        initialize(); // gọi trực tiếp trong constructor để chạy trước Spring context
    }

    private void initialize() {
        try {
            Request checkRequest = new Request("HEAD", "/products");
            Response response = restClient.performRequest(checkRequest);

            if (response.getStatusLine().getStatusCode() == 404) {
                String mappingJson = Files.readString(Paths.get("src/main/resources/es/products-mapping.json"));
                Request putRequest = new Request("PUT", "/products");
                putRequest.setJsonEntity(mappingJson);
                restClient.performRequest(putRequest);
                System.out.println("Index 'products' đã được tạo trong config class.");
            } else {
                System.out.println("Index 'products' đã tồn tại (config class).");
            }
        } catch (IOException e) {
            throw new RuntimeException("Lỗi tạo index thủ công", e);
        }
    }
}
