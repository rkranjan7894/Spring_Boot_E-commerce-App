package com.spring.__Ecommerce.App.controller;

import com.spring.__Ecommerce.App.entity.Category;
import com.spring.__Ecommerce.App.service.CategoryService;
import jakarta.servlet.http.HttpSession;
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

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private CategoryService categoryService;
    @GetMapping("/")
    public String index(){
        return "admin/index";
    }
    @GetMapping("/addProduct")
    public String addProduct(){
        return "admin/add_product";
    }
    @GetMapping("/addCategory")
    public String addCategory(Model m){
        m.addAttribute("categorys",categoryService.getAllCategory());
        return "admin/add_category";
    }
    @PostMapping("/saveCategory")
    public String saveCategory(@ModelAttribute Category category,@RequestParam("file")MultipartFile file, HttpSession session) throws IOException {

        String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
        category.setImageName(imageName);

        Boolean existCategory = categoryService.existCategory(category.getName());
     if (existCategory){
         session.setAttribute("errorMsg","Category Name already exists");
     }else {
         Category saveCategory=categoryService.saveCategory(category);
         if (ObjectUtils.isEmpty(saveCategory)){
             session.setAttribute("errorMsg","Not saved ! internal server error");
         }else {
             //Store the Image File
             File saveFile= new ClassPathResource("static/img").getFile();
             Path path= Paths.get(saveFile.getAbsolutePath()+ File.separator + "category_img"+File.separator + file.getOriginalFilename());
            // System.out.println(path);
             Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
             session.setAttribute("succMsg","Saved Successfully");
         }
     }
        return "redirect:/admin/addCategory";
    }
    @GetMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable int id,HttpSession session){
        Boolean deleteCategory=categoryService.deleteCategory(id);
            if (deleteCategory){
                session.setAttribute("succMsg","category delete success");
            }else {
                session.setAttribute("errorMsg","something wrong on server");
            }

    return "redirect:/admin/addCategory";
    }
    @GetMapping("/editCategory/{id}")
    public String editCategory(@PathVariable int id,Model m){
        m.addAttribute("category",categoryService.getCategoryById(id));

        return "admin/edit_category";
    }
    @PostMapping("/updateCategory")
    public String updateCategory(@ModelAttribute Category category,@RequestParam("file") MultipartFile file,HttpSession session) throws IOException {
       Category oldCategory= categoryService.getCategoryById(category.getId());
       String imageName=file.isEmpty() ? oldCategory.getImageName(): file.getOriginalFilename();
       if (!ObjectUtils.isEmpty(category)){
           oldCategory.setName(category.getName());
           oldCategory.setIsActive(category.getIsActive());
           oldCategory.setImageName(imageName);
       }
       Category updateCategory=categoryService.saveCategory(oldCategory);
       if (!ObjectUtils.isEmpty(updateCategory)){
           if (! file.isEmpty()){
               //Store the Image File
               File saveFile= new ClassPathResource("static/img").getFile();
               Path path= Paths.get(saveFile.getAbsolutePath()+ File.separator + "category_img"+File.separator + file.getOriginalFilename());
             //  System.out.println(path);
               Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
           }
           session.setAttribute("succMsg","Category update success");
       }else {
           session.setAttribute("errorMsg","something wrong on server");

       }
        return "redirect:/admin/editCategory/"+ category.getId();
    }
}
