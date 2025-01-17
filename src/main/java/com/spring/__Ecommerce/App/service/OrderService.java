package com.spring.__Ecommerce.App.service;

import com.spring.__Ecommerce.App.entity.OrderRequest;
import com.spring.__Ecommerce.App.entity.ProductOrder;

import java.util.List;

public interface OrderService {
    public void saveOrder(Integer userid, OrderRequest orderRequest);
    public List<ProductOrder> getOrdersByUser(Integer userId);
    public Boolean updateOrderStatus(Integer id,String status);
    public List<ProductOrder> getAllOrders();
}
