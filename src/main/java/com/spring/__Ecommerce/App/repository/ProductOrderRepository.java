package com.spring.__Ecommerce.App.repository;

import com.spring.__Ecommerce.App.entity.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOrderRepository extends JpaRepository<ProductOrder,Integer> {
}
