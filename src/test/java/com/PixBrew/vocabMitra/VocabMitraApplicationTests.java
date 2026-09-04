package com.PixBrew.vocabMitra;

import com.PixBrew.vocabMitra.entity.UserVocabProgressAndBookMarks;
import com.PixBrew.vocabMitra.entity.Users;
import com.PixBrew.vocabMitra.entity.Vocabs;
import com.PixBrew.vocabMitra.repository.UsersRepository;
import com.PixBrew.vocabMitra.repository.VocabsRepository;
import com.PixBrew.vocabMitra.type.RoleType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;

@SpringBootTest
class VocabMitraApplicationTests {
    static {
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("GMT+5:30"));
        System.setProperty("user.timezone", "GMT+5:30");
    }

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private VocabsRepository vocabsRepository;

	@Test
	void contextLoads() {
	}
    @Test
    void addUserTest(){
        Users newUser = Users.builder()
                .firstName("Neal")
                .lastName("Chakravarty")
                .email("neal@gmail.com")
                .password("neal005")
                .role(RoleType.USER)
                .username("neal005")
                .build();
        Users savedUser = usersRepository.save(newUser);
        System.out.println(savedUser);
    }
    @Test
    @Rollback(false) // Ensures the data actually stays in the database after the test runs
    void seedUserAndProgressTest() {

        // 1. Create the base User
        Users newUser = Users.builder()
                .firstName("Aarav")
                .lastName("Sharma")
                .email("aarav.sharma@example.com")
                .password("securepass123")
                .role(RoleType.USER)
                .username("aarav_codes")
                .userVocabProgressAndBookMarksList(new ArrayList<>()) // Ensures the list is initialized
                .build();

        // 2. Fetch some existing vocabularies from the database
        // (This assumes you already seeded the vocabs table with IDs 1 and 2)
        Vocabs vocab1 = vocabsRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Vocab 1 not found!"));
        Vocabs vocab2 = vocabsRepository.findById(2L)
                .orElseThrow(() -> new RuntimeException("Vocab 2 not found!"));

        // 3. Create the progress records
        UserVocabProgressAndBookMarks progress1 = UserVocabProgressAndBookMarks.builder()
                .vocabs(vocab1)
                .isBookmarked(true)
                .isLearned(false)
                .build();

        UserVocabProgressAndBookMarks progress2 = UserVocabProgressAndBookMarks.builder()
                .vocabs(vocab2)
                .isBookmarked(true)
                .isLearned(true)
                .build();

        // 4. Link the two sides of the bidirectional relationship
        progress1.setUser(newUser);
        progress2.setUser(newUser);

        newUser.getUserVocabProgressAndBookMarksList().add(progress1);
        newUser.getUserVocabProgressAndBookMarksList().add(progress2);

        // 5. Save the User (Because of CascadeType.ALL, this saves the progress records too!)
        Users savedUser = usersRepository.save(newUser);

        // 6. Verify in the console
        System.out.println("✅ Saved User ID: " + savedUser.getId());
        System.out.println("✅ Progress Records Saved: " + savedUser.getUserVocabProgressAndBookMarksList().size());
    }
}
