package com.spring.__Ecommerce.App.controller;

import com.spring.__Ecommerce.App.entity.Category;
import com.spring.__Ecommerce.App.entity.Product;
import com.spring.__Ecommerce.App.service.CategoryService;
import com.spring.__Ecommerce.App.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @GetMapping("/")
    String index(){
        return "index";
    }
    @GetMapping("/login")
    String login(){
        return "login";
    }
    @GetMapping("/register")
    String register(){
        return "register";
    }
    @GetMapping("/products")
    String products(Model m, @RequestParam(value = "category",defaultValue = "")String category){
      List<Category> categories= categoryService.getAllActiveCategory();
     List<Product> products=productService.getAllActiveProducts(category);
     m.addAttribute("categories",categories);
     m.addAttribute("products",products);
     m.addAttribute("paramValue",category);
        return "product";
    }
    @GetMapping("/product/{id}")
    String product(@PathVariable int id,Model m){
      Product productById=productService.getProductById(id);
      m.addAttribute("product",productById);
        return "view_product";
    }
}
