package com.example.quickcash;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.allOf;


import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.example.quickcash.ui.LoginActivity;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;

public class ApplyJobTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule = new ActivityScenarioRule<>(LoginActivity.class);


    @Test
    public void testJobApply() throws InterruptedException, UiObjectNotFoundException {

        onView(withId(R.id.editTextEmail)).perform(typeText("aka@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.editTextPassword)).perform(typeText("AkashAkash@"), closeSoftKeyboard());
        onView(withId(R.id.buttonLogin)).perform(click());

        Thread.sleep(1000);
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject allowLocationButton = device.findObject(new UiSelector().text("While using the app"));
        if (allowLocationButton.exists() && allowLocationButton.isEnabled()) {
            allowLocationButton.click();
        }

        onView(allOf(
                withId(R.id.applyButton),
                withText("Apply")
        ))
                .check(matches(isDisplayed()));

        Thread.sleep(1000);
    }

    @Test
    public void testJobApplied() throws InterruptedException, UiObjectNotFoundException {

        onView(withId(R.id.editTextEmail)).perform(typeText("aka@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.editTextPassword)).perform(typeText("AkashAkash@"), closeSoftKeyboard());
        onView(withId(R.id.buttonLogin)).perform(click());

        Thread.sleep(1000);
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject allowLocationButton = device.findObject(new UiSelector().text("While using the app"));
        if (allowLocationButton.exists() && allowLocationButton.isEnabled()) {
            allowLocationButton.click();
        }

        onView(withIndex(withText("Applied"), 0))
                .check(matches(isDisplayed()));


        Thread.sleep(1000);
    }

    public static Matcher<View> withIndex(final Matcher<View> matcher, final int index) {
        return new TypeSafeMatcher<View>() {
            int currentIndex = 0;
            @Override public void describeTo(Description description) {
                description.appendText("with index " + index + ": ");
                matcher.describeTo(description);
            }
            @Override public boolean matchesSafely(View view) {
                if (matcher.matches(view)) {
                    return currentIndex++ == index;
                }
                return false;
            }
        };
    }



}
