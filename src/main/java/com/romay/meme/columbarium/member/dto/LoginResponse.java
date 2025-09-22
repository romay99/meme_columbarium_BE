package com.romay.meme.columbarium.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

  private String token;
  private String type;
  private String nickname;

  public LoginResponse(String token, String nickname) {
    this.token = token;
    this.type = "Bearer";
    this.nickname = nickname;
  }
}