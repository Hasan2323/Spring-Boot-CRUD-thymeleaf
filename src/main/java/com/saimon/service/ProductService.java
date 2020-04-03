package com.saimon.service;

import com.saimon.entity.ProductEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductService {

    public List<ProductEntity> getAllProducts();

    public void create(ProductEntity productEntity);

    public ProductEntity getProductById(Long id);

    public void delete(Long id);

}
