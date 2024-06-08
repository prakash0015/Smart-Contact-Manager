package com.contanctmanager.smartDao;

import com.contanctmanager.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.contanctmanager.entities.Contact;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact,Integer>{
    //pagination
    @Query("select c from Contact c where c.user.id = :userId")
    public Page<Contact> findContactsByUser(@Param("userId")int userId,Pageable pageable);

    //search
    public List<Contact> findByNameAndUser(String name, User user);
}
