package com.spring.__Ecommerce.App.util;

import com.spring.__Ecommerce.App.entity.ProductOrder;
import com.spring.__Ecommerce.App.entity.UserDtls;
import com.spring.__Ecommerce.App.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.Principal;

@Component
public class CommonUtil {
    @Autowired
    private  JavaMailSender mailSender;
    @Autowired
    private UserService userService;
    public boolean sendMail(String url,String reciepentEmail) throws MessagingException, UnsupportedEncodingException {
       MimeMessage message= mailSender.createMimeMessage();
        MimeMessageHelper helper=new MimeMessageHelper(message);
        helper.setFrom("rkranjan7894@gmail.com","Shooping Cart");
        helper.setTo(reciepentEmail);
        String content = "<p>Hello,</p>"+"<p>You have requested to reset your password.</p>"
                +"<p>Click the link below to change your password:</p>"+"<p><a href=\""+url
                +"\">Change my password</a></p>";
        helper.setSubject("Password Reset");
        helper.setText(content,true);
        mailSender.send(message);
        return true;
    }

    public static String generateUrl(HttpServletRequest request) {
        String siteUrl= request.getRequestURL().toString();
        return siteUrl.replace(request.getServletPath(),"");
    }
    String msg=null;
    public Boolean sendMailForProductOrder(ProductOrder order,String status) throws MessagingException, UnsupportedEncodingException {
        msg="<p>Hello [[name]],</p>"
                +"<p>Thank you order <b>[[orderStatus]].</b></p>"
                +"<p><b>Product Details:</b></p>"
                +"<p>Name : [[productName]]</p>"
                +"<p>Category : [[category]]</p>"
                +"<p>Quantity : [[quantity]]</p>"
                +"<p>Price : [[price]]</p>"
                +"<p>Payment Type : [[paymentType]]</p>";
        MimeMessage message= mailSender.createMimeMessage();
        MimeMessageHelper helper=new MimeMessageHelper(message);
        helper.setFrom("rkranjan7894@gmail.com","Shooping Cart");
        helper.setTo(order.getOrderAddress().getEmail());
        msg=msg.replace("[[name]]",order.getOrderAddress().getFirstName());
        msg=msg.replace("[[orderStatus]]",status);
        msg=msg.replace("[[productName]]",order.getProduct().getTitle());
        msg=msg.replace("[[category]]",order.getProduct().getCategory());
        msg=msg.replace("[[quantity]]",order.getQuantity().toString());
        msg=msg.replace("[[price]]",order.getPrice().toString());
        msg=msg.replace("[[paymentType]]",order.getPaymentType());

        helper.setSubject("Product Order Status");
        helper.setText(msg,true);
        mailSender.send(message);
        return true;
    }
    public UserDtls getLoggedInUserDetails(Principal p) {
        String email=p.getName();
        UserDtls userDtls=userService.getUserByEmail(email);
        return userDtls;
    }
}
