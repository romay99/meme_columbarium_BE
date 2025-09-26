package com.romay.meme.columbarium.boardcomment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BoardCommentPostDto {

  private Long boardCode;
  private String contents;


}
