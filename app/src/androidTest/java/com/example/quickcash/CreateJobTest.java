package com.example.quickcash;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.annotation.NonNull;
import androidx.test.espresso.matcher.ViewMatchers;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.quickcash.database.Firebase;
import com.example.quickcash.ui.CreatorDashboard;
import com.example.quickcash.ui.LoginActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class CreateJobTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule = new ActivityScenarioRule<>(LoginActivity.class);

    private String jobPostId;
    private Firebase firebase = new Firebase();

    @Test
    public void testErrorJobCreation() throws InterruptedException {

        onView(withId(R.id.editTextEmail)).perform(typeText("taiki@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.editTextPassword)).perform(typeText("Taiki123@"), closeSoftKeyboard());
        onView(withId(R.id.buttonLogin)).perform(click());

        Thread.sleep(1000);
        onView(withId(R.id.dashboardLayout))
                .check(matches(ViewMatchers.isDisplayed()));

        onView(withId(R.id.jobCreationButton)).perform(click());
        onView(withId(R.id.jobTitle)).perform(typeText("Software Engineer"), closeSoftKeyboard());
        onView(withId(R.id.jobDescription)).perform(typeText("Come work for us we are hiring!"), closeSoftKeyboard());
        onView(withId(R.id.jobLocation)).perform(typeText("Halifax, NS"), closeSoftKeyboard());
        onView(withId(R.id.companyName)).perform(typeText("Software inc"), closeSoftKeyboard());

        onView(withId(R.id.jobType)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Select Job Type"))).perform(click());

        onView(withId(R.id.salary)).perform(typeText("24.60"), closeSoftKeyboard());
        onView(withId(R.id.submitButton)).perform(click());

        Thread.sleep(1000);
    }


    @Test
    public void testValidJobCreation() throws InterruptedException {
        onView(withId(R.id.editTextEmail)).perform(typeText("taiki@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.editTextPassword)).perform(typeText("Taiki123@"), closeSoftKeyboard());
        onView(withId(R.id.buttonLogin)).perform(click());

        Thread.sleep(1000);
        onView(withId(R.id.dashboardLayout))
                .check(matches(ViewMatchers.isDisplayed()));

        onView(withId(R.id.jobCreationButton)).perform(click());
        onView(withId(R.id.jobTitle)).perform(typeText("Software Engineer"), closeSoftKeyboard());
        onView(withId(R.id.jobDescription)).perform(typeText("Come work for us we are hiring!"), closeSoftKeyboard());
        onView(withId(R.id.jobLocation)).perform(typeText("Halifax, NS"), closeSoftKeyboard());
        onView(withId(R.id.companyName)).perform(typeText("Software inc"), closeSoftKeyboard());
        onView(withId(R.id.jobType)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Full-Time"))).perform(click());
        onView(withId(R.id.salary)).perform(typeText("24.60"), closeSoftKeyboard());
        onView(withId(R.id.submitButton)).perform(click());

        Thread.sleep(1000);

        onView(withId(R.id.dashboardLayout))
                .check(matches(ViewMatchers.isDisplayed()));

        fetchLatestJobId();
    }

    private void fetchLatestJobId() throws InterruptedException {
        DatabaseReference jobsRef = firebase.getDb().getReference("Jobs"); // Fix path case sensitivity
        CountDownLatch latch = new CountDownLatch(1); // Blocks until ID is retrieved

        jobsRef.orderByChild("employerEmail").equalTo("taiki@gmail.com") // Filter by email
                .limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot jobSnapshot : dataSnapshot.getChildren()) {
                                jobPostId = jobSnapshot.getKey();
                            }
                        }
                        latch.countDown();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        System.err.println("Error fetching job ID: " + databaseError.getMessage());
                        latch.countDown();
                    }
                });

        latch.await();
    }


    @After
    public void tearDown() throws InterruptedException {
        if (jobPostId != null) {
            DatabaseReference jobsRef = firebase.getDb().getReference("Jobs");
            jobsRef.child(jobPostId)
                    .removeValue()
                    .addOnSuccessListener(aVoid -> {
                        System.out.println("Test job deleted: " + jobPostId);
                    })
                    .addOnFailureListener(e -> {
                        System.err.println("Error deleting job post: " + e.getMessage());
                    });
        } else {
            System.err.println("Warning: No job ID found, so no job was deleted.");
        }
    }
}
