package com.spring.__Ecommerce.App.service.impl;


import com.spring.__Ecommerce.App.entity.Product;
import com.spring.__Ecommerce.App.repository.ProductRepository;
import com.spring.__Ecommerce.App.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Override
    public List<Product> getAllActiveProducts(String category) {
        List<Product> products = null;
        if (ObjectUtils.isEmpty(category)){
          products=productRepository.findByIsActiveTrue();
        }else {
            products=productRepository.findByCategory(category);
        }
        return products;
    }

    @Autowired
    private ProductRepository productRepository;
    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Page<Product> getAllProductsPagination(Integer pageNo, Integer pageSize) {
       Pageable pageable= PageRequest.of(pageNo,pageSize);
        return productRepository.findAll(pageable);
    }

    @Override
    public Boolean deleteProduct(int id) {
        Product product= productRepository.findById(id).orElse(null);
        if (!ObjectUtils.isEmpty(product)){
            productRepository.delete(product);
            return true;
        }
        return false;
    }

    @Override
    public Product getProductById(int id) {
       Product product= productRepository.findById(id).orElse(null);
        return product;
    }

    @Override
    public List<Product> searchProduct(String ch) {
     return productRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch,ch);
    }

    @Override
    public Page<Product> searchProductPagination(Integer pageNo, Integer pageSize, String ch) {
      Pageable pageable =  PageRequest.of(pageNo,pageSize);
       return productRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch,ch,pageable);
    }

    @Override
    public Page<Product> getAllActiveProductPagination(Integer pageNo, Integer pageSize,String category) {
        Pageable pageable= PageRequest.of(pageNo,pageSize);
        Page <Product> pageProduct = null;
        if (ObjectUtils.isEmpty(category)){
            pageProduct=productRepository.findByIsActiveTrue(pageable);
        }else {
            pageProduct=productRepository.findByCategory(pageable,category);
        }
        return pageProduct;
    }

    @Override
    public Page<Product> searchActiveProductPagination(Integer pageNo, Integer pageSize, String ch) {
        Pageable pageable=PageRequest.of(pageNo,pageSize);
     return productRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch,ch,pageable);
    }
}
