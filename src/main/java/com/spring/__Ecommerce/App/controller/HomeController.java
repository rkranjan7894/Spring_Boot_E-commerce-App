package com.spring.__Ecommerce.App.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
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
    String products(){
        return "product";
    }
    @GetMapping("/product")
    String product(){
        return "view_product";
    }
}
