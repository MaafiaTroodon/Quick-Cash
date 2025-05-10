package com.example.quickcash;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.quickcash.validator.JobValidator;

public class JobValidatorTest {

    private JobValidator validator;

    @Before
    public void setUp() {
        validator = new JobValidator();
    }

    @Test
    public void testValidTitle() {
        assertTrue(validator.isValidTitle("Software Engineer"));
    }

    @Test
    public void testInvalidTitle_Empty() {
        assertFalse(validator.isValidTitle(""));
    }

    @Test
    public void testInvalidTitle_Null() {
        assertFalse(validator.isValidTitle(null));
    }

    @Test
    public void testValidDescription_Short() {
        assertTrue(validator.isValidDescription("This is a valid job description."));
    }

    @Test
    public void testValidDescription_MaxLength() {
        assertTrue(validator.isValidDescription("A".repeat(150))); // 150 characters
    }

    @Test
    public void testInvalidDescription_TooLong() {
        assertFalse(validator.isValidDescription("A".repeat(151))); // 151 characters
    }

    @Test
    public void testInvalidDescription_Null() {
        assertFalse(validator.isValidDescription(null));
    }

    @Test
    public void testValidLocation() {
        assertTrue(validator.isValidLocation("New York, NY"));
    }

    @Test
    public void testInvalidLocation_Empty() {
        assertFalse(validator.isValidLocation(""));
    }

    @Test
    public void testInvalidLocation_Null() {
        assertFalse(validator.isValidLocation(null));
    }

    @Test
    public void testValidJobTypes() {
        assertTrue(validator.isValidType("Full-Time"));
        assertTrue(validator.isValidType("Part-Time"));
        assertTrue(validator.isValidType("Contract"));
        assertTrue(validator.isValidType("Freelance"));
        assertTrue(validator.isValidType("Internship"));
        assertTrue(validator.isValidType("Temporary"));
        assertTrue(validator.isValidType("Volunteer"));
    }

    @Test
    public void testInvalidJobType_Empty() {
        assertFalse(validator.isValidType(""));
    }

    @Test
    public void testInvalidJobType_Null() {
        assertFalse(validator.isValidType(null));
    }

    @Test
    public void testInvalidJobType_RandomString() {
        assertFalse(validator.isValidType("RandomType"));
    }

    @Test
    public void testValidSalary_PositiveNumber() {
        assertTrue(validator.isValidSalary("50000"));
    }

    @Test
    public void testValidSalary_Zero() {
        assertTrue(validator.isValidSalary("0"));
    }

    @Test
    public void testInvalidSalary_NegativeNumber() {
        assertFalse(validator.isValidSalary("-100"));
    }

    @Test
    public void testInvalidSalary_Empty() {
        assertFalse(validator.isValidSalary(""));
    }

    @Test
    public void testInvalidSalary_Null() {
        assertFalse(validator.isValidSalary(null));
    }

    @Test
    public void testInvalidSalary_NonNumeric() {
        assertFalse(validator.isValidSalary("abcd"));
    }


    @Test
    public void testValidCompany() {
        assertTrue(validator.isValidCompany("Tech Corp"));
    }

    @Test
    public void testInvalidCompany_Empty() {
        assertFalse(validator.isValidCompany(""));
    }

    @Test
    public void testInvalidCompany_Null() {
        assertFalse(validator.isValidCompany(null));
    }

    @Test
    public void testValidEmployerPhone() {
        assertTrue(validator.isValidEmployerPhone("+1234567890"));
        assertTrue(validator.isValidEmployerPhone("1234567890"));
    }

    @Test
    public void testInvalidEmployerPhone_Empty() {
        assertFalse(validator.isValidEmployerPhone(""));
    }

    @Test
    public void testInvalidEmployerPhone_Null() {
        assertFalse(validator.isValidEmployerPhone(null));
    }

    @Test
    public void testInvalidEmployerPhone_ShortNumber() {
        assertFalse(validator.isValidEmployerPhone("12345"));
    }

    @Test
    public void testInvalidEmployerPhone_LongNumber() {
        assertFalse(validator.isValidEmployerPhone("12345678901234"));
    }

    @Test
    public void testInvalidEmployerPhone_NonNumeric() {
        assertFalse(validator.isValidEmployerPhone("abcd123456"));
    }

    @Test
    public void testValidEmployerName() {
        assertTrue(validator.isValidEmployerName("John Doe"));
    }

    @Test
    public void testInvalidEmployerName_Empty() {
        assertFalse(validator.isValidEmployerName(""));
    }

    @Test
    public void testInvalidEmployerName_Null() {
        assertFalse(validator.isValidEmployerName(null));
    }

}
