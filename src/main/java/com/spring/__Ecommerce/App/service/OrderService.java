package com.spring.__Ecommerce.App.service;

import com.spring.__Ecommerce.App.entity.OrderRequest;
import com.spring.__Ecommerce.App.entity.ProductOrder;

public interface OrderService {
    public void saveOrder(Integer userid, OrderRequest orderRequest);
}
