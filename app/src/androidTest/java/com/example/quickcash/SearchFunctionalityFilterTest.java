package com.example.quickcash;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.espresso.intent.Intents;

import com.example.quickcash.ui.FilterPage;
import com.example.quickcash.ui.SearcherDashboard;
import com.example.quickcash.ui.LoginActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SearchFunctionalityFilterTest {

    @Rule
    public ActivityScenarioRule<SearcherDashboard> activityRule =
            new ActivityScenarioRule<>(SearcherDashboard.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    /**
     * Test if all UI elements are displayed properly.
     */
    @Test
    public void testUIElementsDisplayed() {
        onView(withId(R.id.searchInput)).check(matches(isDisplayed()));
        onView(withId(R.id.searchButton)).check(matches(isDisplayed()));
        onView(withId(R.id.filterButton)).check(matches(isDisplayed()));
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
        onView(withId(R.id.LogOut)).check(matches(isDisplayed()));
    }

    /**
     * Test if search functionality works.
     */
        @Test
        public void testFilterFunctionality() {
            // Start the SearcherDashboard activity
            onView(withId(R.id.filterButton)).perform(ViewActions.click()); // Click the filter button

            // Check that the FilterPage is opened
            intended(IntentMatchers.hasComponent(FilterPage.class.getName()));

            // Fill out filter fields on FilterPage
            onView(withId(R.id.etJobTitle)).perform(ViewActions.typeText("Software Engineer"), ViewActions.closeSoftKeyboard());
            onView(withId(R.id.etMinSalary)).perform(ViewActions.typeText("5"), ViewActions.closeSoftKeyboard());
            onView(withId(R.id.etMaxSalary)).perform(ViewActions.typeText("100"), ViewActions.closeSoftKeyboard());
            onView(withId(R.id.etCompany)).perform(ViewActions.typeText("soft inc"), ViewActions.closeSoftKeyboard());
            onView(withId(R.id.etLocation)).perform(ViewActions.typeText("Halifax, NS"), ViewActions.closeSoftKeyboard());

            // Apply filters
            onView(withId(R.id.btnApplyFilters)).perform(ViewActions.click());

            // Check that the filtered job list is updated accordingly (assuming RecyclerView is populated)
            onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.scrollToPosition(0)); // Scroll to the first item

            // Optionally, check if a filtered job is displayed in the RecyclerView
            // Example: Check that a job with "Developer" is displayed
            onView(allOf(ViewMatchers.withId(R.id.jobTitle), withText("Software Engineer"))).check(ViewAssertions.matches(isDisplayed()));

        }


    /**
     * Test if the Logout button works and navigates to LoginActivity.
     */
    @Test
    public void testLogoutButtonNavigatesToLogin() {
        onView(withId(R.id.LogOut)).perform(click());

        // Verify navigation to LoginActivity
        intended(hasComponent(LoginActivity.class.getName()));
    }


}
