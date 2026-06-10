package com.het.buspasssystem.repository;

import com.het.buspasssystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository
        extends JpaRepository<User, Integer> {

    User findByEmailAndPassword(String email, String password);

    User findByFullName(String fullname);

    User findByEmail(String email);

}
