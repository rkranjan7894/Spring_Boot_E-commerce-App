package com.spring.__Ecommerce.App.controller;

import com.spring.__Ecommerce.App.entity.Category;
import com.spring.__Ecommerce.App.entity.Product;
import com.spring.__Ecommerce.App.entity.ProductOrder;
import com.spring.__Ecommerce.App.entity.UserDtls;
import com.spring.__Ecommerce.App.service.*;
import com.spring.__Ecommerce.App.util.CommonUtil;
import com.spring.__Ecommerce.App.util.OrderStatus;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
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
    @Autowired
    private OrderService orderService;
    @Autowired
    CartService cartService;
    @Autowired
    private CommonUtil commonUtil;
    @ModelAttribute
    public void getUserDetails(Principal p, Model m){
        if (p!=null){
            String email=p.getName();
            UserDtls userDtls=userService.getUserByEmail(email);
            m.addAttribute("user",userDtls);
            Integer countCart= cartService.getCountCart(userDtls.getId());
            m.addAttribute("countCart",countCart);
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
    public String addCategory(Model m,@RequestParam(name="pageNo",defaultValue = "0")Integer pageNo,
                              @RequestParam(name = "pageSize",defaultValue = "3")Integer pageSize){
        Page<Category> page= categoryService.getAllCategoryPagination(pageNo,pageSize);
        List<Category> categorys=page.getContent();
        m.addAttribute("categorys",categorys);
        m.addAttribute("pageNo",page.getNumber());
        m.addAttribute("pageSize",pageSize);
        m.addAttribute("totalElements",page.getTotalElements());
        m.addAttribute("totalPages",page.getTotalPages());
        m.addAttribute("isFirst",page.isFirst());
        m.addAttribute("isLast",page.isLast());
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
    public String viewProducts(Model m,@RequestParam(defaultValue = "") String ch){
        List<Product> products=null;

        if (ch!=null && ch.length()>0){
            products =productService.searchProduct(ch.trim());
        }else {
           products=productService.getAllProducts();
        }
        m.addAttribute("products",products);
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
    @GetMapping("/orders")
    public String getAllOrders(Model m){
           List<ProductOrder> allOrders=orderService.getAllOrders();
           m.addAttribute("orders",allOrders);
           m.addAttribute("srch",false);
        return "/admin/orders";
    }
    @PostMapping("/update-order-status")
    public  String  updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st,HttpSession session){
        OrderStatus[] values=OrderStatus.values();
        String status=null;
        for (OrderStatus orderSt :values){
            if (orderSt.getId().equals(st)){
                status=orderSt.getName();
            }
        }
        ProductOrder updateOrder=orderService.updateOrderStatus(id,status);
        try {
            commonUtil.sendMailForProductOrder(updateOrder,status);

        }catch (Exception e){
            e.printStackTrace();
        }
        if (!ObjectUtils.isEmpty(updateOrder)){
            session.setAttribute("succMsg","Status Updated");
        }else {
            session.setAttribute("errorMsg","Status not Updated");
        }
        return "redirect:/admin/orders";
    }
    @GetMapping("/search-order")
    public String searchProduct(@RequestParam String orderId,Model m,HttpSession session){
        if (orderId!=null && orderId.length() > 0) {
            ProductOrder order = orderService.getOrdersByOrderId(orderId.trim());
            if (ObjectUtils.isEmpty(order)) {
                session.setAttribute("errorMsg", "Incorrect orderId");
                m.addAttribute("orderDtls", null);
            } else {
                m.addAttribute("orderDtls", order);
            }
            m.addAttribute("srch", true);
        }else {
            List<ProductOrder> allOrders=orderService.getAllOrders();
            m.addAttribute("orders",allOrders);
            m.addAttribute("srch",false);
        }
        return "/admin/orders";
    }
}
