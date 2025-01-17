package com.spring.__Ecommerce.App.service;

import com.spring.__Ecommerce.App.entity.OrderRequest;
import com.spring.__Ecommerce.App.entity.ProductOrder;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface OrderService {
    public void saveOrder(Integer userid, OrderRequest orderRequest) throws MessagingException, UnsupportedEncodingException;
    public List<ProductOrder> getOrdersByUser(Integer userId);
    public ProductOrder updateOrderStatus(Integer id,String status);
    public List<ProductOrder> getAllOrders();
}
