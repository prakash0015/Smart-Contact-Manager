package com.contanctmanager.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.contanctmanager.entities.Contact;
import com.contanctmanager.entities.User;
import com.contanctmanager.helper.Message;
import com.contanctmanager.smartDao.ContactRepository;
import com.contanctmanager.smartDao.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private UserRepository userRepository;


    @ModelAttribute
    public void addCommonData(Model model,Principal principal) {
        String username = principal.getName();
        User user = this.userRepository.getUserByUserName(username);
        model.addAttribute("user", user);
    }

    @RequestMapping("/index")
    public String dashboard(Model model) {
        model.addAttribute("title", "User Dashboard");
        return "normal/user_dashboard";
    }

    //handler for adding contact
    @GetMapping("/add_Contact")
    public String addContactForm(Model model) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        return "normal/add_Contact_Form";
    }

    //handler for processing add contact form
    @PostMapping("/process-contact")
    public String processContact(@Valid @ModelAttribute("contact") Contact contact,BindingResult result,@RequestParam("profileImage") MultipartFile multipartFile,Principal principal,Model model,HttpSession session) {
        try {
            if(result.hasErrors()) {
                model.addAttribute("contact", contact);
                session.setAttribute("message", new Message("please fill the form correctly", "danger"));
                return "normal/add_Contact_Form";
            }
            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);

            //processing and uplaoding file
            if(multipartFile.isEmpty()) {
                contact.setImage("contact.jpg");
            }else {
                contact.setImage(multipartFile.getOriginalFilename());
                File file = new ClassPathResource("/static/IMG/").getFile();
                Path path = Paths.get(file.getAbsolutePath()+File.separator+multipartFile.getOriginalFilename());
                Files.copy(multipartFile.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);   
            }
            contact.setUser(user);
            user.getContact().add(contact);
            this.userRepository.save(user);
            System.out.println("contact added to database");
            session.setAttribute("message", new Message("Contact Added Sucessfully", "success"));
        }
        catch(Exception e) {
            e.printStackTrace();
            session.setAttribute("message", new Message("something went worng Try Again!", "danger"));
        }
        return "normal/add_Contact_Form";
    }

    //handler for showing contact
    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") int page,Model model,Principal principal) {
        model.addAttribute("title", "Your Contacts");
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);
        Pageable pageable = PageRequest.of(page,7);
        Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(),pageable);
        model.addAttribute("contacts", contacts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPage", contacts.getTotalPages());
        return "normal/show_contacts";
    }


    //showing particular contact detail
    @GetMapping("/contact/{cId}")
    public String showContactDetail(@PathVariable("cId") int id,Model model,Principal principal) {
        Optional<Contact> contactOptional = this.contactRepository.findById(id);
        Contact contact = contactOptional.get();
        String username = principal.getName();
        User user = this.userRepository.getUserByUserName(username);
        if(user.getId()==contact.getUser().getId()) {
            model.addAttribute("contact", contact);
        }
        else {}
        return "normal/contact_detail";
    }

    //deleting contact
    @PostMapping("/delete/{cId}")
    public String deleteContact(@PathVariable("cId") int id,HttpSession session,Principal principal) throws IOException {
        Optional<Contact> contactOptional = this.contactRepository.findById(id);
        Contact contact = contactOptional.get();
        String username = principal.getName();
        User user = this.userRepository.getUserByUserName(username);
        if(contact==null) {
            return "no such contact found";
        }
        //removing image
        // String img = contact.getImage();
        // if(!img.equals("contact.jpg")) {
        //     File file = new ClassPathResource("static/IMG/").getFile();
        //     Path path = Paths.get(file.getAbsolutePath()+File.separator+img);
        //     Files.delete(path);
        // }
       
        //user can delete only his own contact
        if(user.getId()==contact.getUser().getId()) {
            this.contactRepository.delete(contact);
        }
        return "redirect:/user/show-contacts/0";
    }

    //updating contact
    @PostMapping("/update/{cId}")
    public String updateContact(@PathVariable("cId") int id,Model model) {
        model.addAttribute("title", "Update Your Contact");
        Contact contact = this.contactRepository.findById(id).get();
        model.addAttribute("contact", contact);
        return "normal/update_form";
    }

    //update processing
    @PostMapping("/process-update") 
    public String updateProcess(@ModelAttribute Contact contact,Model model,@RequestParam("profileImage")MultipartFile multipartFile,Principal principal,HttpSession session){
        try {
            Contact oldContact = this.contactRepository.findById(contact.getcId()).get();
            
            if(!multipartFile.isEmpty()) {
                //delete old photo
                File deleteFile = new ClassPathResource("/static/IMG").getFile();
                File file1 = new File(deleteFile,oldContact.getImage());
                file1.delete();

                //update new photo
                File saveFile = new ClassPathResource("/static/IMG/").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+multipartFile.getOriginalFilename());
                Files.copy(multipartFile.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);   
                contact.setImage(multipartFile.getOriginalFilename());
            }
            else {
                contact.setImage(oldContact.getImage());
            }
            User user = this.userRepository.getUserByUserName(principal.getName());
            contact.setUser(user);
            this.contactRepository.save(contact); 
            session.setAttribute("message", new Message("contact updated succesfully","success"));
            }catch(Exception e) {
                session.setAttribute("message",new Message("something went wrong! try again!", "danger"));
            }
        return "redirect:/user/contact/"+contact.getcId();
    }


    //My profile
    @GetMapping("/my_profile")
    public String myProfile(Model model,Principal principal) {
        model.addAttribute("title", "profile page");
        User user = this.userRepository.getUserByUserName(principal.getName());
        model.addAttribute("user", user);
        return "normal/profile";
    }

}
