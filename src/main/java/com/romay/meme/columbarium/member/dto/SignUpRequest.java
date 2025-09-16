package com.romay.meme.columbarium.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class SignUpRequest {

  private String id;
  private String password;
  private String email;
  private String nickname;
}