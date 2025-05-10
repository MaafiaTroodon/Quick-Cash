package com.example.quickcash;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.example.quickcash.ui.CreatorDashboard;
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
public class LoginActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testLoginButton() {
        // Check if the login button is visible
        onView(withId(R.id.buttonLogin))
                .check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void testLoginFailsAndDoesNotNavigateToDashboard() {
        // Initialize Intents for verification
        Intents.init();

        // Simulate typing invalid credentials and clicking login
        onView(withId(R.id.editTextEmail)).perform(typeText("invalid@example.com"), closeSoftKeyboard());
        onView(withId(R.id.editTextPassword)).perform(typeText("wrongpassword"), closeSoftKeyboard());
        onView(withId(R.id.buttonLogin)).perform(click());

        // Verify that the Dashboard activity was NOT started
        intended(hasComponent(CreatorDashboard.class.getName()), Intents.times(0));

        // Release Intents
        Intents.release();
    }


    @Test
    public void testLoginPassAndNavigatesToDashboard() throws InterruptedException, UiObjectNotFoundException {
        // Simulate typing invalid credentials and clicking login
        onView(withId(R.id.editTextEmail)).perform(typeText("taiki@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.editTextPassword)).perform(typeText("Taiki123@"), closeSoftKeyboard());
        onView(withId(R.id.buttonLogin)).perform(click());

        Thread.sleep(5000);
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject allowLocationButton = device.findObject(new UiSelector().text("While using the app"));
        if (allowLocationButton.exists() && allowLocationButton.isEnabled()) {
            allowLocationButton.click();
        }
        onView(withId(R.id.dashboardLayout))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}
