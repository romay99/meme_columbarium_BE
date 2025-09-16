package com.romay.meme.columbarium.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

  private String token;
  private String type;
  private String id;
  private String nickname;
  private String role;

  public LoginResponse(String token, String id, String nickname, String role) {
    this.token = token;
    this.type = "Bearer";
    this.id = id;
    this.nickname = nickname;
    this.role = role;
  }
}