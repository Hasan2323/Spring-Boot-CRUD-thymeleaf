package com.saimon.repository;

import com.saimon.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
}
