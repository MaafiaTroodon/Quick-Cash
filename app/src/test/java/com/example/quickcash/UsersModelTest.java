package com.example.quickcash;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import com.example.quickcash.model.SecurityModel;
import com.example.quickcash.model.UserModel;

public class UsersModelTest {
    UserModel user;
    String userName = "user", email = "email123@email.com", password = "my!pass123", roleCreator = "Creator", roleSearcher = "Searcher";


    @Before
    public void setup() { user = new UserModel(userName, email, password, roleCreator); }

    @Test
    public void getUsernameTest() {
        assertEquals(userName, user.getUsername());
        assertNotEquals("wrong", user.getUsername());
    }
    @Test
    public void setUsernameTest() {
        user.setUsername("newUser");
        assertEquals("newUser", user.getUsername());
        assertNotEquals("wrong", user.getUsername());
    }
    @Test
    public void getEmailTest() {
        assertEquals(email, user.getEmail());
        assertNotEquals("wrong", user.getEmail());
    }
    @Test
    public void setEmailTest() {
        user.setEmail("newEmail");
        assertEquals("newEmail", user.getEmail());
        assertNotEquals("wrong", user.getEmail());
    }
    @Test
    public void getRoleTest() {
        assertEquals(roleCreator, user.getRole());
        assertNotEquals(roleSearcher, user.getRole());
        assertNotEquals("wrong", user.getRole());
    }
    @Test
    public void setRoleTest() {
        user.setRole(roleSearcher);
        assertEquals(roleSearcher, user.getRole());
        assertNotEquals("wrong", user.getRole());
    }
    @Test
    public void getPasswordTest() {
        assertEquals(password, user.getPassword());
        assertNotEquals("wrong", user.getPassword());
    }
    @Test
    public void setPasswordTest() {
        user.setPassword("newPassword");
        assertEquals("newPassword", user.getPassword());
        assertNotEquals("wrong", user.getPassword());
    }
    @Test
    public void SecurityAnsTest() {
        SecurityModel securityModel = new SecurityModel("Me", "We", "You");
        user.setSecurityAns(securityModel);
        assertTrue(securityModel.isEqual(user.getSecurityAns()));
    }
}
