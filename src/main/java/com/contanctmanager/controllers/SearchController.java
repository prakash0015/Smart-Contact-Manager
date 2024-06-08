package com.contanctmanager.controllers;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.contanctmanager.entities.Contact;
import com.contanctmanager.entities.User;
import com.contanctmanager.smartDao.ContactRepository;
import com.contanctmanager.smartDao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

import static org.springframework.http.ResponseEntity.*;

@RestController
public class SearchController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;

    @GetMapping("/search/{query}")
    public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal) {
        System.out.println(query);
        User user = this.userRepository.getUserByUserName(principal.getName());
        List<Contact> contacts = this.contactRepository.findByNameAndUser(query,user);
        return ResponseEntity.ok(contacts);
    }
}
