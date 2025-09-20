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

  public LoginResponse login(LoginRequest loginRequest) {
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

      // JWT 토큰 생성
      String token = jwtUtil.generateToken(
          member.getId(),
          List.of(member.getRole().name())
      );

      return new LoginResponse(token, member.getId(), member.getNickname(),
          member.getRole().name());

    } catch (AuthenticationException e) {
      throw new MemberNotFoundException("로그인에 실패했습니다. 아이디 또는 비밀번호를 확인해주세요.");
    }
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