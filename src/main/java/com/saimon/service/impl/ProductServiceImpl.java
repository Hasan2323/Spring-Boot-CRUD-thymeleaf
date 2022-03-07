package com.saimon.service.impl;

import com.saimon.entity.ProductEntity;
import com.saimon.repository.ProductRepository;
import com.saimon.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll(Sort.by("id").descending());
    }

    @Override
    public void createProduct(ProductEntity productEntity) {
        productRepository.save(productEntity);
    }

    @Override
    public ProductEntity getProductById(Long id) {
        return productRepository.findById(id).get();
        //return prodRepo.findById(id); //erokom dite chaile returnType Optional<ProductEntity> korte hobe.
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public HttpResponse<String> getDataViaAPI(String url) {

        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();
        HttpClient client = HttpClient.newBuilder().build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();
            String responseBody = response.body();

            log.info("Status Code : {}", statusCode);
            log.info("Response Body : {}", responseBody);

            // just for curiosity
            HttpHeaders headers = response.headers();
            URI uri = response.uri();
            HttpClient.Version version = response.version();
            Optional<HttpResponse<String>> previousResponse = response.previousResponse();
            HttpRequest request1 = response.request();

            log.info("headers : {}", headers);
            log.info("uri : {}", uri);
            log.info("version : {}", version);
            log.info("request1 : {}", request1);


            return response;
        } catch (IOException | InterruptedException e) {
            log.error("sending request error : {}", e.getMessage(), e);
            throw new RuntimeException("sending request error");
        }
    }
}
