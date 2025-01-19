package com.spring.__Ecommerce.App.controller;

import com.spring.__Ecommerce.App.entity.*;
import com.spring.__Ecommerce.App.service.CartService;
import com.spring.__Ecommerce.App.service.CategoryService;
import com.spring.__Ecommerce.App.service.OrderService;
import com.spring.__Ecommerce.App.service.UserService;
import com.spring.__Ecommerce.App.util.CommonUtil;
import com.spring.__Ecommerce.App.util.OrderStatus;
import jakarta.mail.MessagingException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.List;
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserService userService;
    @Autowired
    private CartService cartService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @GetMapping("/")
    public String home(){
return "user/home";
    }
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
    @GetMapping("/addCart")
    public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid,HttpSession session) {
        Cart saveCart = cartService.saveCart(pid, uid);
        if (ObjectUtils.isEmpty(saveCart)) {
            session.setAttribute("errorMsg", "Product add to cart failed");
        }else {
            session.setAttribute("succMsg", "Product added to cart");
        }
        return "redirect:/product/" + pid;
    }
    @GetMapping("/cart")
    public String loadCartPage(Principal p,Model m){
        UserDtls user=getLoggedInUserDetails(p);
       List<Cart> carts= cartService.getCartsByUser(user.getId());
       m.addAttribute("carts",carts);
       if (carts.size()>0) {
           Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
           m.addAttribute("totalOrderPrice", totalOrderPrice);
       }
        return "/user/cart";
    }
    @GetMapping("/cartQuantityUpdate")
    public String updateCartQuantity(@RequestParam String sy,@RequestParam Integer cid){
        cartService.updateQuantity(sy,cid);
        return "redirect:/user/cart";
    }

    private UserDtls getLoggedInUserDetails(Principal p) {
        String email=p.getName();
        UserDtls userDtls=userService.getUserByEmail(email);
        return userDtls;
    }
    @GetMapping("/orders")
    public String orderPage(Principal p,Model m){
        UserDtls user=getLoggedInUserDetails(p);
        List<Cart> carts= cartService.getCartsByUser(user.getId());
        m.addAttribute("carts",carts);
        if (carts.size()>0) {
            Double OrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
            Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice() +50+15;
            m.addAttribute("OrderPrice", OrderPrice);
            m.addAttribute("totalOrderPrice", totalOrderPrice);
        }
        return "/user/order";
    }

    @PostMapping("/save-order")
    public String saveOrder(@ModelAttribute OrderRequest request,Principal p) throws MessagingException, UnsupportedEncodingException {
        UserDtls user=getLoggedInUserDetails(p);
        orderService.saveOrder(user.getId(),request);
        return "redirect:/user/success";
    }
    @GetMapping("/success")
    private String loadSuccess(){
        return "/user/success";
    }
    @GetMapping("/user-orders")
    private String myOrder(Model m,Principal p){
        UserDtls loginUser=getLoggedInUserDetails(p);
       List<ProductOrder> orders= orderService.getOrdersByUser(loginUser.getId());
       m.addAttribute("orders",orders);
        return "/user/my_orders";
    }
    @GetMapping("/update-status")
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
        return "redirect:/user/user-orders";
    }
    @GetMapping("/profile")
    public String profile(){
        return "/user/profile";
    }
    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute UserDtls user, @RequestParam MultipartFile img,HttpSession session){
      UserDtls updateUserProfile=  userService.updateUserProfile(user,img);
        if (ObjectUtils.isEmpty(updateUserProfile)){
            session.setAttribute("errorMsg","Profile not Updated");
        }else {
            session.setAttribute("succMsg","Profile Updated");
        }
        return "redirect:/user/profile";
    }
    @PostMapping("/change-password")
    public String changePassword(@RequestParam String newPassword,@RequestParam String currentPassword,Principal p,HttpSession session){
       UserDtls loggedInUserDetails= getLoggedInUserDetails(p);
       boolean matches=passwordEncoder.matches(currentPassword,loggedInUserDetails.getPassword());
       if (matches){
           String encodePassword=passwordEncoder.encode(newPassword);
           loggedInUserDetails.setPassword(encodePassword);
          UserDtls updateUser=userService.updateUser(loggedInUserDetails);
          if (ObjectUtils.isEmpty(updateUser)){
              session.setAttribute("errorMsg","Password not Updated !! Error in server");
          }else {
              session.setAttribute("succMsg","Password Updated Successfully");
          }
       }else {
           session.setAttribute("errorMsg","Current Password Incorrect");
       }
        return "redirect:/user/profile";
    }

}

