package com.contanctmanager.controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.contanctmanager.entities.User;
import com.contanctmanager.helper.Message;
import com.contanctmanager.smartDao.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


@Controller
public class HomeControllers {

     @Autowired
     private UserRepository userRepository;
     @Autowired
     private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Home-Smart Contact Manager");
        return "home";
   }

   @GetMapping("/about")
   public String about() {
        return "about";
   }

   @GetMapping("/signUp")
   public String signUp(Model model) {
    model.addAttribute("title", "Register - Smart Contact Manager");
    model.addAttribute("user", new User());
    return "signUp";
   }


   //handler for registering user
   @PostMapping("/do_register")
   public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result,@RequestParam(value="agreement",defaultValue = "false")boolean agreement,Model model,HttpSession session,@RequestParam("profileImage") MultipartFile multipartFile) {
    try {
      System.out.println("agreement "+agreement);
      User user1 = this.userRepository.getUserByUserName(user.getEmail());
      if(!agreement) {
          System.out.println("you have not agreed terms and conditions");
          throw new Exception("you have not agreed terms and conditions");
     }
      if(user1 != null) {
            // If user exists, set the flag and return to sign-up page
            model.addAttribute("userExists", true);
            return "signUp";
      }
     
      if(result.hasErrors()) {    
          model.addAttribute("user", user);
          return "signUp";
      }
      //processing and uplaoding file
      if(multipartFile.isEmpty()) {
          user.setImageUrl("user.png");
      }
      if(!multipartFile.isEmpty()) {
          user.setImageUrl(multipartFile.getOriginalFilename());
          File file = new ClassPathResource("/static/IMG").getFile();
          Path path = Paths.get(file.getAbsolutePath()+File.separator+multipartFile.getOriginalFilename());
          Files.copy(multipartFile.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
      }
      user.setRole("USER_ROLE");
      user.setEnabled(true);
      user.setPassword(passwordEncoder.encode(user.getPassword()));
      this.userRepository.save(user);
      model.addAttribute("user",new User());
      session.setAttribute("message",new Message("sucessfully registered","alert-sucess"));
    } catch (Exception e) {
      e.printStackTrace();
      model.addAttribute("user", user);
      session.setAttribute("message", new Message("something went wrong!! "+e.getMessage(),"alert-danger"));
    }
    finally {
      session.removeAttribute("message");
    }
      return "signUp";
   }


   //handler for custom login
   @GetMapping("/signin")
   public String customLogin() {
     return "login";
   }

   @GetMapping("/logout")
   public String lo() {
     return "login";
   }
}
