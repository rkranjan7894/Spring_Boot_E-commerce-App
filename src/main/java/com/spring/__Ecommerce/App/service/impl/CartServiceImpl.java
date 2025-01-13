package com.spring.__Ecommerce.App.service.impl;

import com.spring.__Ecommerce.App.entity.Cart;
import com.spring.__Ecommerce.App.entity.Product;
import com.spring.__Ecommerce.App.entity.UserDtls;
import com.spring.__Ecommerce.App.repository.CartRepository;
import com.spring.__Ecommerce.App.repository.ProductRepository;
import com.spring.__Ecommerce.App.repository.UserRepository;
import com.spring.__Ecommerce.App.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Override
    public Cart saveCart(Integer productId, Integer userId) {
        UserDtls userDtls = userRepository.findById(userId).get();
        Product product = productRepository.findById(productId).get();
        Cart cartStatus = cartRepository.findByProductIdAndUserId(productId, userId);
        Cart cart = null;
        if (ObjectUtils.isEmpty(cartStatus)) {
            cart = new Cart();
            cart.setProduct(product);
            cart.setUser(userDtls);
            cart.setQuantity(1);
            cart.setTotalPrice(1 * product.getDiscountPrice());
        } else {
            cart = cartStatus;
            cart.setQuantity(cart.getQuantity() + 1);
            cart.setTotalPrice(cart.getQuantity() * cart.getProduct().getDiscountPrice());
        }
        Cart saveCart = cartRepository.save(cart);
        return saveCart;
    }
    @Override
    public List<Cart> getCartsByUser(Integer userId) {

        return null;
    }
}