package com.romay.meme.columbarium.boardcomment.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BoardCommentListDto {

  private Long code;
  private String contents;
  private LocalDateTime createdAt;

  private Long boardCode;
  private String authorNickName; // 작성자 닉네임

}
