package com.contanctmanager.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name="CONTACT")
public class Contact {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int cId;
    @NotBlank(message = "this field cannot be empty")
    private String name;
    private String secondName;
    @NotBlank(message = "this field cannot be empty")
    private String work;
    @Column(unique=true)
    @NotBlank(message = "this field cannot be blank")
    private String email;
    @NotBlank(message = "this field cannot be blank")
    private String phone;
    private String image;
    @Column(length = 50000)
    private String description;
    @ManyToOne
    private User user;
   
    public Contact(int cId, String name, String secondName, String work, String email, String phone, String image,
            String description) {
        this.cId = cId;
        this.name = name;
        this.secondName = secondName;
        this.work = work;
        this.email = email;
        this.phone = phone;
        this.image = image;
        this.description = description;
    }
    public int getcId() {
        return cId;
    }
    public void setcId(int cId) {
        this.cId = cId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSecondName() {
        return secondName;
    }
    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }
    public String getWork() {
        return work;
    }
    public void setWork(String work) {
        this.work = work;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
     public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public Contact() {
    }
    @Override
    public String toString() {
        return "Contanct [cId=" + cId + ", name=" + name + ", secondName=" + secondName + ", work=" + work + ", email="
                + email + ", phone=" + phone + ", image=" + image + ", description=" + description + "]";
    }
    
}
