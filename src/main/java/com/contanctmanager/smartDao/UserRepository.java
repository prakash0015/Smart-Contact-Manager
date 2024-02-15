package com.contanctmanager.smartDao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.contanctmanager.entities.User;

public interface UserRepository extends JpaRepository<User,Integer> {
    @Query("select u from User u WHERE u.email = :email")
    public User getUserByUserName(@Param("email") String email);
}
