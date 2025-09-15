package com.romay.meme.columbarium.meme.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Meme {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long code;

  private String title;

  @Lob
  private String contents; // 게시글 내용 (마크다운)

  private LocalDateTime createdAt; // 글 작성일

  private LocalDate startDate; // 밈이 흥한날짜

  private LocalDate endDate; // 밈이 망한 날짜

  private Long orgVersionCode; // 원본 글 PK

  private Long version; // 글 버전

  private LocalDateTime updatedAt;

  private Long categoryCode; // 글 카테고리

  private Long authorCode; // 작성자 PK

  private Long updaterCode; // 수정자 pk

}
