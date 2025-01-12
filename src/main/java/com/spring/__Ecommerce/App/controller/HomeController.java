package com.spring.__Ecommerce.App.controller;

import com.spring.__Ecommerce.App.entity.Category;
import com.spring.__Ecommerce.App.entity.Product;
import com.spring.__Ecommerce.App.entity.UserDtls;
import com.spring.__Ecommerce.App.service.CategoryService;
import com.spring.__Ecommerce.App.service.ProductService;
import com.spring.__Ecommerce.App.service.UserService;
import com.spring.__Ecommerce.App.util.CommonUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.aspectj.apache.bcel.util.ClassPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
public class HomeController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @ModelAttribute
    public void getUserDetails(Principal p,Model m){
        if (p!=null){
            String email=p.getName();
            UserDtls userDtls=userService.getUserByEmail(email);
            m.addAttribute("user",userDtls);
        }
        List<Category> allActiveCategory=categoryService.getAllActiveCategory();
        m.addAttribute("categorys",allActiveCategory);
    }
    @GetMapping("/")
    String index(){
        return "index";
    }
    @GetMapping("/signin")
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
    //Forgot Password
    @GetMapping("/forgot-password")
    public String showForgotPassword(){
        return "forgot_password";
    }
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, HttpSession session, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        UserDtls userByEmail=userService.getUserByEmail(email);
        if (ObjectUtils.isEmpty(userByEmail)){
            session.setAttribute("errorMsg","Invalid Email");
        }else {
            String resetToken=UUID.randomUUID().toString();
            userService.updateUserResetToken(email,resetToken);
            //Generate URL : http://localhost:8080/reset-password?token=sfghdjnvbknuythgsvvchjbv
            String url  =  CommonUtil.generateUrl(request)+"/reset-password?token="+resetToken;
           Boolean sendMail= commonUtil.sendMail(url,email);
           if (sendMail){
               session.setAttribute("succMsg","Please check your email..Password Reset link send");
           }else {
               session.setAttribute("errorMsg","Something wrong on server ! Email not send");

           }
        }
        return "redirect:/forgot-password";
    }
    //Reset Password
    @GetMapping("/reset-password")
    public String showResetPassword(@RequestParam String token,HttpSession session,Model m){
        UserDtls userByToken=userService.getUserByToken(token);
        if (userByToken==null){
            m.addAttribute("msg","Your link is invalid or expired !!");
            return "message";
        }
       m.addAttribute("token",token);
        return "reset_password";
    }
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token,@RequestParam String password, HttpSession session,Model m){
        UserDtls userByToken=userService.getUserByToken(token);
        if (userByToken==null){
            m.addAttribute("errorMsg","Your link is invalid or expired !!");
            return "message";
        }else {
          userByToken.setPassword(passwordEncoder.encode(password));
          userByToken.setResetToken(null);
          userService.updateUser(userByToken);
          m.addAttribute("msg","Password Change Successfully");
            return "message";
        }

    }
   }
