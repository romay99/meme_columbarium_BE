package com.romay.meme.columbarium.boardcomment.entity;

import com.romay.meme.columbarium.member.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class BoardComment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long code;

  private String contents;
  private LocalDateTime createdAt;

  private Long boardCode; // 자유게시판 PK
  private Long authorCode; // 사용자 PK

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "authorCode", referencedColumnName = "code", insertable = false, updatable = false)
  private Member member;
}
