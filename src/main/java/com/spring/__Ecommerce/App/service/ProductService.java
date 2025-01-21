package com.spring.__Ecommerce.App.service;

import com.spring.__Ecommerce.App.entity.Category;
import com.spring.__Ecommerce.App.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface ProductService {
    public Product saveProduct(Product product);
    public List<Product> getAllProducts();
    public Boolean deleteProduct(int id);
    public Product getProductById(int id);
    public List<Product> getAllActiveProducts(String category);
    public List<Product> searchProduct(String ch);
    public Page<Product> getAllActiveProductPagination(Integer pageNo,Integer pageSize,String category);
}
