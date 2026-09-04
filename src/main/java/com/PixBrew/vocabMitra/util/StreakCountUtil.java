package com.PixBrew.vocabMitra.util;

import com.PixBrew.vocabMitra.entity.Users;
import com.PixBrew.vocabMitra.service.UsersService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class StreakCountUtil {

    private final UsersService usersService;

    //The streak counting helper class
    @Transactional
    public void updateUserStreak(Users user) {
        LocalDate today = LocalDate.now();
        LocalDate lastActive = user.getLastActiveDate();

        if (lastActive == null) {
            // SCENARIO 1: Brand-new user, first activity ever
            user.setCurrentStreak(1);
            user.setMaxStreak(1);
        } else if (lastActive.equals(today)) {
            // SCENARIO 2: They already studied today. Do nothing!
            return;
        } else if (lastActive.equals(today.minusDays(1))) {
            // SCENARIO 3: They studied yesterday. Streak continues!
            int newStreak = user.getCurrentStreak() + 1;
            user.setCurrentStreak(newStreak);

            if (newStreak > user.getMaxStreak()) {
                user.setMaxStreak(newStreak);
            }
        } else {
            // SCENARIO 4: They missed a day. Streak broken. Start over at 1.
            user.setCurrentStreak(1);
        }

        // Always update the last active date to today
        user.setLastActiveDate(today);
    }
}
