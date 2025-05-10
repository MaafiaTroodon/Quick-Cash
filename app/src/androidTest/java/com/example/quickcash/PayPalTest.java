package com.example.quickcash;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.quickcash.ui.Paypal;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PayPalTest {

    @Rule
    public ActivityScenarioRule<Paypal> activityRule =
            new ActivityScenarioRule<>(Paypal.class);

    @Test
    public void testEmptyAmountShowsError() {
        // Clear the input
        onView(withId(R.id.editTextNumber)).perform(clearText());

        // Click pay button
        onView(withId(R.id.payButton)).perform(click());

        // Verify error
        onView(withId(R.id.editTextNumber))
                .check(matches(hasErrorText("Please enter an amount")));
    }

    @Test
    public void testZeroAmountShowsError() {
        // Enter 0
        onView(withId(R.id.editTextNumber)).perform(typeText("0"), closeSoftKeyboard());
        onView(withId(R.id.payButton)).perform(click());

        // Verify error
        onView(withId(R.id.editTextNumber))
                .check(matches(hasErrorText("Amount must be greater than 0")));
    }

    @Test
    public void verifyInitialScreenComponents() {
        // Verify all elements are displayed on launch
        onView(withId(R.id.payForJobCompletion))
                .check(matches(isDisplayed()))
                .check(matches(withText("Pay for Job Completion")));

        onView(withId(R.id.textView))
                .check(matches(isDisplayed()))
                .check(matches(withText("Enter the payment value:")));

        onView(withId(R.id.editTextNumber))
                .check(matches(isDisplayed()));

        onView(withId(R.id.payButton))
                .check(matches(isDisplayed()))
                .check(matches(withText("Pay with PayPal")));
    }

    @Test
    public void testEmptyAmountValidation() {
        Paypal[] activity = new Paypal[1];
        activityRule.getScenario().onActivity(a -> activity[0] = a);

        onView(withId(R.id.editTextNumber))
                .perform(clearText());

        onView(withId(R.id.payButton))
                .perform(click());

        try {
            onView(withText("Please enter an amount"))
                    .inRoot(withDecorView(not(is(activity[0].getWindow().getDecorView()))))
                    .check(matches(isDisplayed()));
        }
        catch (NoMatchingViewException e) {
            onView(withId(R.id.editTextNumber))
                    .check(matches(hasErrorText("Please enter an amount")));
        }
    }

    @Test
    public void testValidPaymentFlow() {
        onView(withId(R.id.editTextNumber))
                .perform(typeText("10000"), closeSoftKeyboard());
        onView(withId(R.id.payButton)).perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withText("Pay with Card")).check(matches(isDisplayed()));
    }
}
