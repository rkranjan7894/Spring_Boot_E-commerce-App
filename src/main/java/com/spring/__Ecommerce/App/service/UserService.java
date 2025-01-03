package com.spring.__Ecommerce.App.service;

import com.spring.__Ecommerce.App.entity.UserDtls;

import java.util.List;

public interface UserService {
    public UserDtls saveUser(UserDtls user);
    public UserDtls getUserByEmail(String email);
    public List<UserDtls> getUsers(String role);

    Boolean updateAccountStatus(Integer id, Boolean status);
}
