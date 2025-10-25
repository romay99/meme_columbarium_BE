package com.romay.meme.columbarium.board.entity;

import com.romay.meme.columbarium.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Board {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long code;
  private String title;
  @Lob
  @Column(columnDefinition = "TEXT") // DB에 따라 필요시 추가
  private String contents;

  private LocalDateTime createAt;

  private Long authorCode; // 작성자 pk

  private boolean isNotice; // 공지 여부

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "authorCode", referencedColumnName = "code", insertable = false, updatable = false)
  private Member member;
}
