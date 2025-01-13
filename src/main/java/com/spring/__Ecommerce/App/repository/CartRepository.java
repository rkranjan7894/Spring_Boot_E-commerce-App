package com.spring.__Ecommerce.App.repository;

import com.spring.__Ecommerce.App.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart,Integer> {
    public Cart findByProductIdAndUserId(Integer productId, Integer userId);
    public Integer countByUserId(Integer userId);
    public List<Cart> findByUserId(Integer userId);
}
