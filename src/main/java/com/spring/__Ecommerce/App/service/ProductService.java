package com.spring.__Ecommerce.App.service;

import com.spring.__Ecommerce.App.entity.Category;
import com.spring.__Ecommerce.App.entity.Product;

import java.util.List;

public interface ProductService {
    public Product saveProduct(Product product);
    public List<Product> getAllProducts();
    public Boolean deleteProduct(int id);
    public Product getProductById(int id);
}
