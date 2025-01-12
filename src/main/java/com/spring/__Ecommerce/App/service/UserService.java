package com.spring.__Ecommerce.App.service;

import com.spring.__Ecommerce.App.entity.UserDtls;

import java.util.List;

public interface UserService {
    public UserDtls saveUser(UserDtls user);
    public UserDtls getUserByEmail(String email);
    public List<UserDtls> getUsers(String role);

    Boolean updateAccountStatus(Integer id, Boolean status);
    public void increaseFailedAttempt(UserDtls user);
    public void userAccountLock(UserDtls user);
    public boolean unlockAccountTimeExpired(UserDtls user);
    public void resetAttempt(int userId);
   public void updateUserResetToken(String email, String resetToken);
   public UserDtls getUserByToken(String token);
   public UserDtls updateUser(UserDtls user);
}
