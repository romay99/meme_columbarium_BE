package com.romay.meme.columbarium.member.service;

import com.romay.meme.columbarium.exception.MemberNotFoundException;
import com.romay.meme.columbarium.member.dto.LoginRequest;
import com.romay.meme.columbarium.member.dto.LoginResponse;
import com.romay.meme.columbarium.member.dto.SignUpRequest;
import com.romay.meme.columbarium.member.entity.Member;
import com.romay.meme.columbarium.member.entity.Role;
import com.romay.meme.columbarium.member.repository.MemberRepository;
import com.romay.meme.columbarium.util.JWTUtil;
import java.util.List;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final JWTUtil jwtUtil;
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  public LoginResponse login(LoginRequest loginRequest, HttpServletResponse response) {
    try {
      // 인증 시도
      Authentication authentication = authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                      loginRequest.getId(),
                      loginRequest.getPassword()
              )
      );

      // 인증 성공 시 사용자 정보 조회
      Member member = memberRepository.findMemberById(loginRequest.getId())
              .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다."));

      // JWT 액세스 토큰 생성
      String accessToken = jwtUtil.generateAccessToken(
              member.getId(),
              List.of(member.getRole().name())
      );

      // JWT 리프래시 토큰 생성
      String refreshToken = jwtUtil.generateRefreshToken(member.getId());

      // HttpOnly 쿠키로 리프래시 토큰 발급
      Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
      refreshCookie.setHttpOnly(true);
      refreshCookie.setSecure(true); // HTTPS 환경
      refreshCookie.setPath("/");
      refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
      response.addCookie(refreshCookie);

      // 액세스 토큰 + 사용자 정보 반환
      return new LoginResponse(accessToken, member.getNickname());

    } catch (AuthenticationException e) {
      throw new MemberNotFoundException("로그인에 실패했습니다. 아이디 또는 비밀번호를 확인해주세요.");
    }
  }

  public String refreshAccessToken(String refreshToken) {
    // 리프래시 토큰 검증
    if (!jwtUtil.validateToken(refreshToken, jwtUtil.getUsernameFromToken(refreshToken))) {
      throw new IllegalArgumentException("Invalid refresh token");
    }

    String username = jwtUtil.getUsernameFromToken(refreshToken);

    Member member = memberRepository.findMemberById(username)
            .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다."));

    // 새 액세스 토큰 발급
    return jwtUtil.generateAccessToken(username, List.of(member.getRole().name()));
  }

  public void signUp(SignUpRequest request) {
    // 이미 존재하는 아이디 체크
    if (memberRepository.existsMemberById(request.getId())) {
      throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
    }

    Member member = Member.builder()
        .id(request.getId())
        .password(passwordEncoder.encode(request.getPassword())) // 비밀번호 암호화
        .email(request.getEmail())
        .nickname(request.getNickname())
        .role(Role.ROLE_USER) // 기본 권한
        .build();

    memberRepository.save(member);
  }

  public boolean idCheck(String memberId) {
    return memberRepository.existsMemberById(memberId);
  }
}