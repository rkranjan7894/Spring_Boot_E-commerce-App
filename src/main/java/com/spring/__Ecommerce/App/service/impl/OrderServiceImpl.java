package com.spring.__Ecommerce.App.service.impl;

import com.spring.__Ecommerce.App.entity.Cart;
import com.spring.__Ecommerce.App.entity.OrderAddress;
import com.spring.__Ecommerce.App.entity.OrderRequest;
import com.spring.__Ecommerce.App.entity.ProductOrder;
import com.spring.__Ecommerce.App.repository.CartRepository;
import com.spring.__Ecommerce.App.repository.ProductOrderRepository;
import com.spring.__Ecommerce.App.service.OrderService;
import com.spring.__Ecommerce.App.util.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private ProductOrderRepository orderRepository;
    @Autowired
    private CartRepository cartRepository;

    @Override
    public void saveOrder(Integer userid, OrderRequest orderRequest) {
       List<Cart> carts= cartRepository.findByUserId(userid);
       for (Cart cart:carts){
       ProductOrder order=new ProductOrder();
       order.setOrderId(UUID.randomUUID().toString());
       order.setOrderDate(new Date());

       order.setProduct(cart.getProduct());
       order.setPrice(cart.getProduct().getDiscountPrice());

       order.setQuantity(cart.getQuantity());
       order.setUser(cart.getUser());

       order.setStatus(OrderStatus.IN_PROGRESS.getName());
       order.setPaymentType(orderRequest.getPaymentType());

           OrderAddress address=new OrderAddress();
           address.setFirstName(orderRequest.getFirstName());
           address.setLastName(orderRequest.getLastName());
           address.setEmail(orderRequest.getEmail());
           address.setMobileNo(orderRequest.getMobileNo());
           address.setAddress(orderRequest.getAddress());
           address.setCity(orderRequest.getCity());
           address.setState(orderRequest.getState());
           address.setPincode(orderRequest.getPincode());

           order.setOrderAddress(address);
           orderRepository.save(order);

       }
    }
}
