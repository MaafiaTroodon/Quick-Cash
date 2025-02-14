package com.example.quickcash;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import com.example.quickcash.validator.Validator;

public class ValidatorTest {
    Validator validator;

    @Before
    public void setup() { validator = new Validator(); }

    @Test
    public void checkIfEmailIsEmpty() {
        Assert.assertTrue(validator.isEmptyEmailAddress(""));
    }

    @Test
    public void checkIfEmailIsValid() {
        assertTrue(validator.isValidEmailAddress("abc123@dal.ca"));
    }

    @Test
    public void checkIfEmailIsNotValid() {
        assertFalse(validator.isValidEmailAddress("abc.123dal.ca"));
    }

    @Test
    public void checkIfPasswordIsValid() {
        assertTrue(validator.isValidPassword("pass122!@"));
        assertTrue(validator.isValidPassword("pass!@122"));
    }

    @Test
    public void checkIfPasswordIsNotValid() {
        assertFalse(validator.isValidPassword("pa1223"));
        assertFalse(validator.isValidPassword("!@#!@#!@#"));
    }

    @Test
    public void checkIfRoleIsValid() {
        //incomplete. Roles may vary depending on the role names.
        assertTrue(validator.isValidRole("Creator"));
        assertTrue(validator.isValidRole("Searcher"));
    }
}
