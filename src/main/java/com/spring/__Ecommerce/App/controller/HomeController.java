package com.spring.__Ecommerce.App.controller;

import com.spring.__Ecommerce.App.entity.Category;
import com.spring.__Ecommerce.App.entity.Product;
import com.spring.__Ecommerce.App.entity.UserDtls;
import com.spring.__Ecommerce.App.service.CategoryService;
import com.spring.__Ecommerce.App.service.ProductService;
import com.spring.__Ecommerce.App.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.aspectj.apache.bcel.util.ClassPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;
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
    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile file, HttpSession session) throws IOException {
    String imageName=file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
    user.setProfileImage(imageName);
     UserDtls saveUser=userService.saveUser(user);
   if (!ObjectUtils.isEmpty(saveUser)){
    if (!file.isEmpty()){
        //Store the Image File
        File saveFile= new ClassPathResource("static/img").getFile();
        Path path= Paths.get(saveFile.getAbsolutePath()+ File.separator + "profile_img"+File.separator + file.getOriginalFilename());
         System.out.println(path);
        Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
    }
      session.setAttribute("succMsg","Register Successfully");
    }else {
    session.setAttribute("errorMsg","something wrong on server");
   }
        return "redirect:/register";
    }
   }
