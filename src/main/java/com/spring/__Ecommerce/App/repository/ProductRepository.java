package com.spring.__Ecommerce.App.repository;

import com.spring.__Ecommerce.App.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Integer> {
}
