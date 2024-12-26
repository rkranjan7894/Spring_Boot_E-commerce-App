package com.spring.__Ecommerce.App.repository;

import com.spring.__Ecommerce.App.entity.UserDtls;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserDtls,Integer> {
    public UserDtls findByEmail(String email);
}
