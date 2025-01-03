package com.spring.__Ecommerce.App.repository;

import com.spring.__Ecommerce.App.entity.UserDtls;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<UserDtls,Integer> {
    public UserDtls findByEmail(String email);

    List<UserDtls> findByRole(String role);
}
