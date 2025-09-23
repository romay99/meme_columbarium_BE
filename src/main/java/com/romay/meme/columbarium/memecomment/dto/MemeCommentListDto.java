package com.romay.meme.columbarium.memecomment.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemeCommentListDto {

  private Long code;
  private String contents;
  private LocalDateTime createAt;

  private Long memeCode;
  private String authorNickName; // 작성자 닉네임

}
