package com.spring.__Ecommerce.App.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class ProductOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String orderId;
    private Date orderDate;
    @ManyToOne
    private Product product;
    private Double price;
    private Integer quantity;
    @ManyToOne
    private UserDtls user;
    private String status;
    private String paymentType;
    @OneToOne(cascade = CascadeType.ALL)
    private OrderAddress orderAddress;
}
