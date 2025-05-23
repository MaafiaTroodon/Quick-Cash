package com.example.quickcash;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

import android.content.Intent;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.example.quickcash.R;
import com.example.quickcash.ui.CreatorDashboard;
import com.example.quickcash.ui.SearcherDashboard;
import com.example.quickcash.ui.LoginActivity;
import com.example.quickcash.ui.SearcherDashboard;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DashboardTest {

    @Rule
    public ActivityTestRule<SearcherDashboard> activitySearcherRule = new ActivityTestRule<>(SearcherDashboard.class, true, false);
    public ActivityTestRule<CreatorDashboard> activityCreatorRule = new ActivityTestRule<>(CreatorDashboard.class, true, false);

    @Before
    public void setUp() {
        Intents.init();
        FirebaseAuth.getInstance().signInAnonymously(); // Ensure user is logged in before test
        activitySearcherRule.launchActivity(new Intent());
        activityCreatorRule.launchActivity(new Intent());
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testLogoutConfirmationDialogAppears() throws UiObjectNotFoundException {
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject allowLocationButton = device.findObject(new UiSelector().text("While using the app"));
        if (allowLocationButton.exists() && allowLocationButton.isEnabled()) {
            allowLocationButton.click();
        }
        // Click the logout button
        onView(withId(R.id.LogOut)).perform(click());

        // Check if the confirmation dialog is displayed
        onView(withText("Are you sure you want to log out?")).check(matches(isDisplayed()));

        // Check if "Yes" and "No" buttons are displayed
        onView(withText("Yes")).check(matches(isDisplayed()));
        onView(withText("No")).check(matches(isDisplayed()));
    }

    @Test
    public void testLogoutConfirmed() {
        // Click the logout button
        onView(withId(R.id.LogOut)).perform(click());

        // Click "Yes" to confirm logout
        onView(withText("Yes")).perform(click());

        // Verify that LoginActivity is launched
        intended(hasComponent(LoginActivity.class.getName()));
    }

    @Test
    public void testLogoutCancelled() {
        // Click the logout button
        onView(withId(R.id.LogOut)).perform(click());

        // Click "No" to cancel logout
        onView(withText("No")).perform(click());

        // Ensure the dashboard is still displayed
        onView(withId(R.id.heading)).check(matches(isDisplayed()));
    }
}
