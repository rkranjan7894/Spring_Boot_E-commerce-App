package com.spring.__Ecommerce.App.controller;

import com.spring.__Ecommerce.App.entity.Category;
import com.spring.__Ecommerce.App.entity.Product;
import com.spring.__Ecommerce.App.entity.UserDtls;
import com.spring.__Ecommerce.App.service.CategoryService;
import com.spring.__Ecommerce.App.service.ProductService;
import com.spring.__Ecommerce.App.service.UserService;
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
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;
    @ModelAttribute
    public void getUserDetails(Principal p, Model m){
        if (p!=null){
            String email=p.getName();
            UserDtls userDtls=userService.getUserByEmail(email);
            m.addAttribute("user",userDtls);
        }
        List<Category> allActiveCategory=categoryService.getAllActiveCategory();
        m.addAttribute("categorys",allActiveCategory);
    }
    @GetMapping("/")
    public String index(){
        return "admin/index";
    }
    @GetMapping("/addProduct")
    public String addProduct(Model m){
        List<Category> catagories = categoryService.getAllCategory();
        m.addAttribute("catagories",catagories);
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
    @PostMapping("/saveProduct")
    public String saveProduct(@ModelAttribute Product product,@RequestParam("file") MultipartFile image,HttpSession session) throws IOException {
        String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();
        product.setImage(imageName);
        product.setDiscount(0);
        product.setDiscountPrice(product.getPrice());
       Product saveProduct= productService.saveProduct(product);
       if (!ObjectUtils.isEmpty(saveProduct)){
           session.setAttribute("succMsg","Product Saved Success");
           //Store the Image File
           File saveFile= new ClassPathResource("static/img").getFile();
           Path path= Paths.get(saveFile.getAbsolutePath()+ File.separator + "product_img"+File.separator + image.getOriginalFilename());
           //System.out.println(path);
           Files.copy(image.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
       }else {
           session.setAttribute("errorMsg","Something wrong on server");
       }
        return "redirect:/admin/addProduct";
    }
    @GetMapping("/products")
    public String viewProducts(Model m){
        m.addAttribute("products",productService.getAllProducts());
        return "admin/products";
    }
    @GetMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable int id,HttpSession session){
        Boolean deleteProduct=productService.deleteProduct(id);
        if (deleteProduct){
            session.setAttribute("succMsg","category delete success");
        }else {
            session.setAttribute("errorMsg","something wrong on server");
        }

        return "redirect:/admin/products";
    }
    @GetMapping("/editProduct/{id}")
    public String editProduct(@PathVariable int id,Model m){
        m.addAttribute("product",productService.getProductById(id));
        m.addAttribute("categories",categoryService.getAllCategory());
        return "admin/edit_product";
    }


    @PostMapping("/updateProduct")
    public String updateProduct(
            @ModelAttribute Product product,
            @RequestParam("file") MultipartFile file,
            HttpSession session) throws IOException {

        // Retrieve the existing product
        Product oldProduct = productService.getProductById(product.getId());

        // Determine the image to use
        String image = file.isEmpty() ? oldProduct.getImage() : file.getOriginalFilename();
    if (! ObjectUtils.isEmpty(product)) {
        // Update fields if product is valid
        oldProduct.setTitle(product.getTitle());
        oldProduct.setDescription(product.getDescription());
        oldProduct.setCategory(product.getCategory());
        oldProduct.setPrice(product.getPrice());
        oldProduct.setStock(product.getStock());
        oldProduct.setImage(image);
        oldProduct.setIsActive(product.getIsActive());
        oldProduct.setDiscount(product.getDiscount());

        // 5=100*(5/100); 100-5=95
        Double discount = product.getPrice() * (product.getDiscount() / 100.0);  //Discount Formula
        Double discountPrice= product.getPrice()-discount;
        oldProduct.setDiscountPrice(discountPrice);

    }
    if (product.getDiscount() < 0 || product.getDiscount() > 100) {
        session.setAttribute("errorMsg", "invalid Discount.");
    }else {
        // Save the updated product
        Product updatedProduct = productService.saveProduct(oldProduct);

        if (!ObjectUtils.isEmpty(updatedProduct)) {   //if (updatedProduct != null) {
            if (!file.isEmpty()) {
                // Store the image file
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath()
                        + File.separator + "product_img"
                        + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            session.setAttribute("succMsg", "Product updated successfully.");
        } else {
            session.setAttribute("errorMsg", "Something went wrong on the server.");
        }
    }
        return "redirect:/admin/editProduct/" + product.getId();
    }
     @GetMapping("/users")
    public String getAllUsers(Model m){
        List<UserDtls> users=userService.getUsers("ROLE_USER");
        m.addAttribute("users",users);
     return "/admin/users";
    }
    @GetMapping("/updateSts")
    public String updateUserAccountStatus(@RequestParam Boolean status,Integer id,HttpSession session){
        Boolean f=userService.updateAccountStatus(id,status);
        if (f){
            session.setAttribute("succMsg","Account Status Updated");
        }else {
            session.setAttribute("errorMsg","Something wrong on server");
        }
        return "redirect:/admin/users";
    }
}
