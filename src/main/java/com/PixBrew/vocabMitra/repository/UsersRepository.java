package com.PixBrew.vocabMitra.repository;

import com.PixBrew.vocabMitra.entity.Users;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    boolean existsByUsernameAndEmail(String username, String email);

    Users findByUsername(String username);

    // The EntityGraph tells Hibernate to eagerly fetch the 'vocabProgresses'
    // list in a single SQL JOIN behind the scenes.
//    @EntityGraph(attributePaths = {"vocabProgresses", "vocabProgresses.vocab"})
//    Optional<Users> findByUsername(String username);
//
//    // Or if you prefer fetching by ID:
//    @EntityGraph(attributePaths = {"vocabProgresses", "vocabProgresses.vocab"})
//    Optional<Users> findById(Long id);
}
