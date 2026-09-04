package com.PixBrew.vocabMitra.repository;

import com.PixBrew.vocabMitra.entity.Vocabs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocabsRepository extends JpaRepository<Vocabs, Long> {
    boolean existsByVocabAndMeaning(String vocab, String meaning);


    List<Vocabs> findByVocabContainingIgnoreCase(String qVocab);

    Page<Vocabs> findByUseCaseTagIgnoreCase(String useCaseTag, Pageable pageable);

    // This custom PostgreSQL query checks if the exact word exists in the comma-separated list
    // It prevents "CAT" from accidentally matching "AFCAT"
    @Query(value = "SELECT * FROM vocabs WHERE string_to_array(UPPER(use_case_tag), ', ') @> ARRAY[UPPER(:tag)]",
            nativeQuery = true)
    Page<Vocabs> findByExactTagInList(@Param("tag") String tag, Pageable pageable);

}

//add custom query for user Search (we can use method like .contains ())
