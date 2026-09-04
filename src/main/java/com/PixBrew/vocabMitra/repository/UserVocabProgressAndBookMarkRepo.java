package com.PixBrew.vocabMitra.repository;

import com.PixBrew.vocabMitra.entity.UserVocabProgressAndBookMarks;
import com.PixBrew.vocabMitra.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserVocabProgressAndBookMarkRepo extends JpaRepository<UserVocabProgressAndBookMarks, Long> {
    List<UserVocabProgressAndBookMarks> findByUser(long id);

    // Spring Data JPA automatically writes the SQL for this based on the method name!
    List<UserVocabProgressAndBookMarks> findByUserIdAndVocabsIdIn(long userId, List<Long> vocabIds);

    UserVocabProgressAndBookMarks findByUserIdAndVocabsId(long userId, Long vocabIds);

    // Passing the whole User object is the safest way to ensure Spring maps the foreign key correctly
    long countByUserAndIsBookmarkedTrue(Users user);

    long countByUserAndIsLearnedTrue(Users user);

    // Spring automatically handles the LIMIT, OFFSET, and WHERE clauses!
    Page<UserVocabProgressAndBookMarks> findByUserIdAndIsBookmarkedTrue(Long userId, Pageable pageable);

    Page<UserVocabProgressAndBookMarks> findByUserIdAndIsLearnedTrue(Long userId, Pageable pageable);

    //for future use in development
    // Efficiently counts the learners directly in SQL without loading heavy Java objects
//    @Query("SELECT COUNT(p) FROM UserVocabProgress p WHERE p.vocab.id = :vocabId AND p.isLearned = true")
//    long countLearnedUsersForVocab(@Param("vocabId") Long vocabId);
}
