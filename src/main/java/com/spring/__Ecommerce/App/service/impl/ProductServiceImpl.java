package com.spring.__Ecommerce.App.service.impl;

import com.spring.__Ecommerce.App.entity.Product;
import com.spring.__Ecommerce.App.repository.ProductRepository;
import com.spring.__Ecommerce.App.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }
}
