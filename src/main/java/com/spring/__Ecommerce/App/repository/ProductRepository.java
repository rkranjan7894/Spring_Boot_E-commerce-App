package com.spring.__Ecommerce.App.repository;

import com.spring.__Ecommerce.App.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Integer> {

    List<Product> findByIsActiveTrue();

    List<Product> findByCategory(String category);
}
