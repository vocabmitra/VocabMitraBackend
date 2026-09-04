package com.PixBrew.vocabMitra.entity;

import com.PixBrew.vocabMitra.type.RoleType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@NoArgsConstructor
//@RequiredArgsConstructor
@Builder
@Getter
@Setter
@AllArgsConstructor
public class Users implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String firstName;
    private String lastName;
    @Column(unique = true)// the joinColumn will create a separate index
    private String username;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
//    @UpdateTimestamp
//    private LocalDateTime lastLoggedIn;
    @Enumerated(EnumType.STRING)
    private RoleType role;
    @Column(columnDefinition = "integer default 0")
    private int currentStreak = 0;
    @Column(columnDefinition = "integer default 0")
    private int maxStreak = 0;
    // Tracks the exact calendar day they last interacted with a vocab
    private LocalDate lastActiveDate;
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserVocabProgressAndBookMarks> userVocabProgressAndBookMarksList = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }
}
