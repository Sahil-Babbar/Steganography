package com.stegnography.domain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.math.BigInteger;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {
    private String firstName;
    private String lastName;
    @Id
    @Column(unique = true)
    private String email;
    private BigInteger phone_no;

    private String password;

//    public User() {
//
//    }

//    public User(int id, String firstName, String lastName, String email, String phone_no) {
//        this.id = id;
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.email = email;
//        this.phone_no = phone_no;
//    }
//
//    @Id
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public String getFirstName() {
//        return firstName;
//    }
//
//    public void setFirstName(String firstName) {
//        this.firstName = firstName;
//    }
//
//    public String getLastName() {
//        return lastName;
//    }
//
//    public void setLastName(String lastName) {
//        this.lastName = lastName;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getPhone_no() {
//        return phone_no;
//    }
//
//    public void setPhone_no(String phone_no) {
//        this.phone_no = phone_no;
//    }
}
