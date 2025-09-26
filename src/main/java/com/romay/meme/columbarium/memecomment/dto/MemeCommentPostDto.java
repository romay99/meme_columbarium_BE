package com.romay.meme.columbarium.memecomment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemeCommentPostDto {

  private Long memeCode;
  private String contents;

}
