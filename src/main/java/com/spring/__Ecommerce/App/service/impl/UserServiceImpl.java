package com.spring.__Ecommerce.App.service.impl;

import com.spring.__Ecommerce.App.entity.UserDtls;
import com.spring.__Ecommerce.App.repository.UserRepository;
import com.spring.__Ecommerce.App.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public UserDtls saveUser(UserDtls user) {
      user.setRole("ROLE_USER");
      String encodePassword=passwordEncoder.encode(user.getPassword());
      user.setPassword(encodePassword);
       UserDtls saveUser= userRepository.save(user);
        return saveUser;
    }
}
