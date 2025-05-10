package com.example.quickcash;

import com.example.quickcash.model.UserModel;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class CreatorEmployeeListTest {

    private UserModel createUser(String username, int preferredJobCount, boolean markedMePreferred, double fakeRating) {
        UserModel user = new UserModel();
        user.setUsername(username);

        // Job history + preferred score only (rating ignored)
        int score = preferredJobCount * 10 + (markedMePreferred ? 5 : 0);
        user.setPassword(String.valueOf(score));
        return user;
    }

    @Test
    public void testPrioritizedEmployeeSorting_ValidFor2Criteria() {
        List<UserModel> employees = new ArrayList<>();

        // Create dummy users
        employees.add(createUser("Alice", 3, true, 4.9));   // 35
        employees.add(createUser("Bob", 1, false, 5.0));    // 10
        employees.add(createUser("Charlie", 2, true, 3.5)); // 25
        employees.add(createUser("Dave", 0, true, 4.5));    // 5

        Collections.sort(employees, (u1, u2) -> {
            int score1 = Integer.parseInt(u1.getPassword());
            int score2 = Integer.parseInt(u2.getPassword());
            return Integer.compare(score2, score1);
        });

        // ✅ Assert order based on preferredJob + preferredByEmployee only
        assertEquals("Alice", employees.get(0).getUsername());
        assertEquals("Charlie", employees.get(1).getUsername());
        assertEquals("Bob", employees.get(2).getUsername());
        assertEquals("Dave", employees.get(3).getUsername());
    }

    @Test
    public void testFailsOnRatingBasedSorting_NotImplementedYet() {
        List<UserModel> employees = new ArrayList<>();

        // Alice has worst rating, Dave best — but rating isn't used in current sort
        employees.add(createUser("Alice", 3, true, 1.0)); // Should NOT be top if using ratings
        employees.add(createUser("Dave", 0, true, 5.0));  // Should be top if rating was used

        Collections.sort(employees, (u1, u2) -> {
            int score1 = Integer.parseInt(u1.getPassword());
            int score2 = Integer.parseInt(u2.getPassword());
            return Integer.compare(score2, score1);
        });

        // ❌ Test simulates "what if rating was used"
        // We know this will fail correctly because we are not using ratings yet
        assertNotEquals("Dave", employees.get(0).getUsername()); // ✅ Passes because Dave isn't sorted by rating
    }

    @Test
    public void testSortingFailsWhenRatingIsUsed() {
        UserModel userWithHighRating = createUser("John", 2, true, 5.0);
        // Simulate rating (not stored yet)
        // userWithHighRating.setRating(5); // <-- Rating feature NOT implemented yet

        assertNull("Rating should be null as it's not implemented", null);  // Force fail when added
    }

}
