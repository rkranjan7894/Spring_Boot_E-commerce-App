package com.spring.__Ecommerce.App.service.impl;

import com.spring.__Ecommerce.App.entity.UserDtls;
import com.spring.__Ecommerce.App.repository.UserRepository;
import com.spring.__Ecommerce.App.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDtls saveUser(UserDtls user) {
       UserDtls saveUser= userRepository.save(user);
        return saveUser;
    }
}
