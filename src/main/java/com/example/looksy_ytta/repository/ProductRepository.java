package com.example.looksy_ytta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.looksy_ytta.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}