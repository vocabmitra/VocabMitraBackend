package com.PixBrew.vocabMitra.entity;

import com.PixBrew.vocabMitra.type.VocabType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Getter
@Setter
public class Vocabs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String vocab;
    @Enumerated(EnumType.STRING)
    private VocabType vocabType;
    private String useCaseTag;
    private String trick;
    private String meaning;
    private String example;
    private String message;
    private LocalDateTime updatedAt;
}
