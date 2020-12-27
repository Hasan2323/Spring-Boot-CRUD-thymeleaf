package com.saimon.service.impl;

import com.saimon.entity.ProductEntity;
import com.saimon.repository.ProductRepository;
import com.saimon.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll();
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
}
