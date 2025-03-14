package com.example.quickcash;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import com.example.quickcash.model.SecurityModel;

public class SecurityModelTest {
    SecurityModel securityModel;
    SecurityModel securityModel2;
    SecurityModel securityModel3;
    String q1 = "Me", q2 = "We", q3 = "You";

    @Test
    public void checkIsQuestions() {
        securityModel = new SecurityModel(q1,q2,q3);
        assertTrue(securityModel.isQuestions("Me", "We", "You"));
        assertFalse(securityModel.isQuestions("qw","er","ty"));
    }

    @Test
    public void getQuestion1Test() {
        securityModel = new SecurityModel(q1,q2,q3);
        assertEquals("Me", securityModel.getQuestion1());
        assertNotEquals("qw", securityModel.getQuestion1());
    }
    @Test
    public void getQuestion2Test() {
        securityModel = new SecurityModel(q1,q2,q3);
        assertEquals("We", securityModel.getQuestion2());
        assertNotEquals("qw", securityModel.getQuestion2());
    }
    @Test
    public void getQuestion3Test() {
        securityModel = new SecurityModel(q1,q2,q3);
        assertEquals("You", securityModel.getQuestion3());
        assertNotEquals("qw", securityModel.getQuestion3());
    }
    @Test
    public void isEqualTest() {
        securityModel = new SecurityModel(q1,q2,q3);
        securityModel2 = new SecurityModel(q1,q2,q3);
        securityModel3 = new SecurityModel("wr","on","g");
        assertTrue(securityModel.isEqual(securityModel2));
        assertFalse(securityModel.isEqual(securityModel3));
    }

}
