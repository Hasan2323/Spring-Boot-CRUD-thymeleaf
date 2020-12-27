package com.saimon.service;

import com.saimon.entity.ProductEntity;

import java.util.List;

public interface ProductService {

    List<ProductEntity> getAllProducts();

    void createProduct(ProductEntity productEntity);

    ProductEntity getProductById(Long id);

    void deleteProduct(Long id);

}
