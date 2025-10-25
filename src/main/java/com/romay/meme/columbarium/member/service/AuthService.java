package com.romay.meme.columbarium.member.service;

import com.romay.meme.columbarium.exception.MemberNotFoundException;
import com.romay.meme.columbarium.member.dto.LoginRequest;
import com.romay.meme.columbarium.member.dto.LoginResponse;
import com.romay.meme.columbarium.member.dto.SignUpRequest;
import com.romay.meme.columbarium.member.entity.Member;
import com.romay.meme.columbarium.member.entity.Role;
import com.romay.meme.columbarium.member.repository.MemberRepository;
import com.romay.meme.columbarium.util.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final JwtTokenProvider jwtTokenProvider;
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  public LoginResponse login(LoginRequest loginRequest, HttpServletResponse response) {
    // 사용자 조회
    Member member = memberRepository.findMemberById(loginRequest.getId())
        .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다."));

    // 비밀번호 검증
    if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
      throw new MemberNotFoundException("로그인에 실패했습니다. 아이디 또는 비밀번호를 확인해주세요.");
    }

    // JWT 액세스 토큰 생성
    String accessToken = jwtTokenProvider.generateAccessToken(member.getId());

    // JWT 리프래시 토큰 생성
    String refreshToken = jwtTokenProvider.generateRefreshToken(member.getId());

    // HttpOnly 쿠키로 리프래시 토큰 발급
    Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
    refreshCookie.setHttpOnly(true);
    refreshCookie.setSecure(true); // TODO 운영에는 true로!
//    refreshCookie.setSecure(false); // HTTPS 환경에서만 true
    refreshCookie.setPath("/");
    refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
    refreshCookie.setAttribute("SameSite", "None");
    response.addCookie(refreshCookie);

    // 액세스 토큰 + 사용자 정보 반환
    return new LoginResponse(accessToken, member.getNickname());
  }

  public String refreshAccessToken(String refreshToken) {
    // 리프래시 토큰 검증
    if (!jwtTokenProvider.validateToken(refreshToken)) {
      throw new IllegalArgumentException("유효하지 않은 리프래시 토큰입니다.");
    }

    // 토큰에서 사용자명 추출
    String username = jwtTokenProvider.getUsernameFromToken(refreshToken);

    // 사용자 존재 확인
    Member member = memberRepository.findMemberById(username)
        .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다."));

    // 새 액세스 토큰 발급
    return jwtTokenProvider.generateAccessToken(username);
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

  public boolean isNicknameAvailable(String nickname) {
    return memberRepository.existsMemberByNickname(nickname);
  }
}