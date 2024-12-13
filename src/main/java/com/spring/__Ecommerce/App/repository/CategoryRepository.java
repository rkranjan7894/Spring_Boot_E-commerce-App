package com.spring.__Ecommerce.App.repository;

import com.spring.__Ecommerce.App.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Integer> {
    public Boolean existsByName(String name);
}