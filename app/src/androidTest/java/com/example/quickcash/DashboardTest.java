package com.example.quickcash;

import android.content.Context;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import com.example.quickcash.ui.Dashboard;
import com.example.quickcash.ui.LoginActivity;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import org.junit.Before;
import org.junit.After;

import static androidx.test.espresso.assertion.ViewAssertions.matches;

/**
 * Instrumented test for Dashboard activity.
 * Ensures UI elements are present and functionality is working as expected.
 */
@RunWith(AndroidJUnit4.class)
public class DashboardTest {

    @Rule
    public ActivityScenarioRule<Dashboard> activityRule = new ActivityScenarioRule<>(Dashboard.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void useAppContext() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.quickcash", appContext.getPackageName());
    }

    @Test
    public void testUIElementsAreDisplayed() {
        Espresso.onView(ViewMatchers.withId(R.id.heading))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.onView(ViewMatchers.withId(R.id.LogOut))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testUserCanSeeDashboardAndLogout()  {
        Espresso.onView(ViewMatchers.withId(R.id.heading))
                .check(matches(ViewMatchers.isDisplayed()));

        final Dashboard[] activityHolder = new Dashboard[1];
        activityRule.getScenario().onActivity(activity -> activityHolder[0] = activity);
        final android.view.View decorView = activityHolder[0].getWindow().getDecorView();

        Espresso.onView(ViewMatchers.withId(R.id.LogOut))
                .perform(ViewActions.click());

        Intents.intended(IntentMatchers.hasComponent(LoginActivity.class.getName()));

    }
}


