package com.PixBrew.vocabMitra.dto;

import com.PixBrew.vocabMitra.entity.Users;
import com.PixBrew.vocabMitra.entity.Vocabs;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
public class UserVocabProgressResponseDto {


}
