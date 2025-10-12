package com.romay.meme.columbarium.meme.entity;

import com.romay.meme.columbarium.category.entity.Category;
import com.romay.meme.columbarium.member.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
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

  private String thumbnail; // 밈 게시글에는 썸네일이 필수!

  private LocalDate endDate; // 밈이 망한 날짜

  private Long orgMemeCode; // 원본 글 PK

  private Long version; // 글 버전

  private LocalDateTime updatedAt;

  private Long categoryCode; // 글 카테고리

  private Long authorCode; // 작성자 PK

  private Long updaterCode; // 수정자 pk

  private Boolean latest; // 가장 최신버전글인지

  private Integer likesCount; // 좋아요 갯수

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "categoryCode", referencedColumnName = "code", insertable = false, updatable = false)
  private Category category;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "authorCode", referencedColumnName = "code", insertable = false, updatable = false)
  private Member member;

}
