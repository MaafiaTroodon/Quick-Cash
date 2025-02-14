package com.example.quickcash;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.espresso.assertion.ViewAssertions;

import com.example.quickcash.ui.ResetPasswordActivity;
import com.example.quickcash.ui.LoginActivity;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.Espresso.onView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ResetPasswordActivityTest {

    @Rule
    public ActivityScenarioRule<ResetPasswordActivity> activityRule = new ActivityScenarioRule<>(ResetPasswordActivity.class);

    @Test
    public void testUIElementsDisplayed() {
        // Check if all input fields are visible
        onView(withId(R.id.emailInput)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.answer1)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.answer2)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.answer3)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.newPassword)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.confirmPassword)).check(ViewAssertions.matches(isDisplayed()));

        // Check if buttons are visible
        onView(withId(R.id.buttonSubmit)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.buttonBackToLogin)).check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void testUserCanEnterEmail() {
        // Check if the user can enter email
        onView(withId(R.id.emailInput)).perform(typeText("user@example.com"), closeSoftKeyboard());
        onView(withId(R.id.emailInput)).check(ViewAssertions.matches(ViewMatchers.withText("user@example.com")));
    }

    @Test
    public void testUserCanEnterSecurityAnswers() {
        // Check if the user can enter answers to security questions
        onView(withId(R.id.answer1)).perform(typeText("answer1"), closeSoftKeyboard());
        onView(withId(R.id.answer1)).check(ViewAssertions.matches(ViewMatchers.withText("answer1")));

        onView(withId(R.id.answer2)).perform(typeText("answer2"), closeSoftKeyboard());
        onView(withId(R.id.answer2)).check(ViewAssertions.matches(ViewMatchers.withText("answer2")));

        onView(withId(R.id.answer3)).perform(typeText("answer3"), closeSoftKeyboard());
        onView(withId(R.id.answer3)).check(ViewAssertions.matches(ViewMatchers.withText("answer3")));
    }

    @Test
    public void testUserCanEnterNewPassword() {
        // Check if the user can enter a new password
        onView(withId(R.id.newPassword)).perform(typeText("NewPassword123!"), closeSoftKeyboard());
        onView(withId(R.id.newPassword)).check(ViewAssertions.matches(ViewMatchers.withText("NewPassword123!")));
    }

    @Test
    public void testUserCanEnterConfirmPassword() {
        onView(withId(R.id.confirmPassword)).perform(typeText("NewPassword123!"), closeSoftKeyboard());
        onView(withId(R.id.confirmPassword)).check(ViewAssertions.matches(ViewMatchers.withText("NewPassword123!")));
    }

    @Test
    public void testSubmitButtonClickable() {
        // Simulate filling out the form and clicking the submit button
        onView(withId(R.id.emailInput)).perform(typeText("user@example.com"), closeSoftKeyboard());
        onView(withId(R.id.answer1)).perform(typeText("answer1"), closeSoftKeyboard());
        onView(withId(R.id.answer2)).perform(typeText("answer2"), closeSoftKeyboard());
        onView(withId(R.id.answer3)).perform(typeText("answer3"), closeSoftKeyboard());
        onView(withId(R.id.newPassword)).perform(typeText("NewPassword123!"), closeSoftKeyboard());
        onView(withId(R.id.confirmPassword)).perform(typeText("NewPassword123!"), closeSoftKeyboard());

        // Check if the submit button is displayed and clickable
        onView(withId(R.id.buttonSubmit)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.buttonSubmit)).perform(click());
    }

    @Test
    public void testBackButtonClickable() {
        // Check if the back button is clickable
        onView(withId(R.id.buttonBackToLogin)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.buttonBackToLogin)).perform(click());
    }

    @Test
    public void testNavigateBackToLogin() {
        // Simulate clicking the back button to navigate to LoginActivity
        onView(withId(R.id.buttonBackToLogin)).perform(click());

        // Verify that the LoginActivity is opened
        intended(hasComponent(LoginActivity.class.getName()));
    }

    @Test
    public void testResetPasswordSuccess() {
        // Simulate entering correct data and submitting
        onView(withId(R.id.emailInput)).perform(typeText("user@example.com"), closeSoftKeyboard());
        onView(withId(R.id.answer1)).perform(typeText("answer1"), closeSoftKeyboard());
        onView(withId(R.id.answer2)).perform(typeText("answer2"), closeSoftKeyboard());
        onView(withId(R.id.answer3)).perform(typeText("answer3"), closeSoftKeyboard());
        onView(withId(R.id.newPassword)).perform(typeText("NewPassword123!"), closeSoftKeyboard());
        onView(withId(R.id.confirmPassword)).perform(typeText("NewPassword123!"), closeSoftKeyboard());

        // Simulate clicking submit and ensure success
        onView(withId(R.id.buttonSubmit)).perform(click());
    }
}
