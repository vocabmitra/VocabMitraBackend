package com.PixBrew.vocabMitra.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
//@ToString
public class UserVocabProgressAndBookMarks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long progressId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Vocabs vocabs;
    // Feature 1: The Bookmark
    @Column(columnDefinition = "boolean default false")
    private boolean isBookmarked;
    // Feature 2: The Learned Status
    @Column(columnDefinition = "boolean default false")
    private boolean isLearned;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime interactedAt;
    @UpdateTimestamp
    private LocalDateTime lastModifiedAt;
}
