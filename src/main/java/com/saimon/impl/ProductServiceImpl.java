package com.saimon.impl;

import com.saimon.entity.ProductEntity;
import com.saimon.repository.ProductRepository;
import com.saimon.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository prodRepo;

    @Override
    public List<ProductEntity> getAllProducts() {
        return prodRepo.findAll();
    }

    @Override
    public void create(ProductEntity productEntity) {
        prodRepo.save(productEntity);
    }

    @Override
    public ProductEntity getProductById(Long id) {
        return prodRepo.findById(id).get();
        //return prodRepo.findById(id); //erokom dite chaile returnType Optional<ProductEntity> korte hobe.
    }

    @Override
    public void delete(Long id) {
        prodRepo.deleteById(id);
    }
}
