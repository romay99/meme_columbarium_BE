package com.romay.meme.columbarium.member.controller;

import com.romay.meme.columbarium.member.dto.LoginRequest;
import com.romay.meme.columbarium.member.dto.LoginResponse;
import com.romay.meme.columbarium.member.dto.SignUpRequest;
import com.romay.meme.columbarium.member.service.AuthService;
import jakarta.servlet.http.Cookie;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

  private final AuthService authService;

  @PostMapping("/check-verify")
  public ResponseEntity<String> checkVerify() {
    // 단순 토큰 검사용 api
    // 필터에서 토큰을 검증하니 여기까지와서 아래 return 을 받아가면 토큰이 정상이라도 판정
    return ResponseEntity.ok("인증 성공!");
  }

  /**
   * 로그인 하는 메서드
   *
   * @param request
   * @return
   */
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(
      @RequestBody LoginRequest request,
      HttpServletResponse response // ← 쿠키 전송용
  ) {
    LoginResponse result = authService.login(request, response); // HttpServletResponse 전달
    return ResponseEntity.ok().body(result);
  }

  @PostMapping("/logout")
  public ResponseEntity<String> logout(HttpServletResponse response) {

    // HttpOnly 쿠키 삭제
    Cookie cookie = new Cookie("refreshToken", null);
    cookie.setHttpOnly(true);
    cookie.setSecure(true); // https 환경이면 true
    cookie.setPath("/");    // 전체 경로에서 쿠키 삭제
    cookie.setMaxAge(0);    // 즉시 만료
    response.addCookie(cookie);

    return ResponseEntity.ok().body("{\"message\": \"로그아웃 성공\"}");
  }


  /**
   * 리프래시 토큰으로 액세스 토큰 재발급
   *
   * @param response     새 액세스 토큰을 담아서 반환
   * @param refreshToken 쿠키에 담긴 HttpOnly 리프래시 토큰
   * @return 새 액세스 토큰
   */
  @PostMapping("/refresh")
  public ResponseEntity<Map<String, String>> refreshToken(
      @CookieValue(name = "refreshToken", required = false) String refreshToken,
      HttpServletResponse response
  ) {
    if (refreshToken == null) {
      return ResponseEntity.status(401).body(Map.of("error", "Refresh token is missing"));
    }

    // 새 액세스 토큰 발급
    String newAccessToken = authService.refreshAccessToken(refreshToken);

    return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
  }

  /**
   * 회원가입 메서드
   *
   * @param request
   * @return
   */
  @PostMapping("/signup")
  public ResponseEntity<String> signUp(@RequestBody SignUpRequest request) {
    authService.signUp(request);
    return ResponseEntity.ok("회원가입에 성공하였습니다! 로그인을 진행해 주세요");
  }

  /**
   * 아이디 중복체크 메서드
   *
   * @param memberId
   * @return
   */
  @GetMapping("/check-id/{id}")
  public ResponseEntity<Map<String, Boolean>> idCheck(@PathVariable("id") String memberId) {
    boolean exist = authService.idCheck(memberId);
    HashMap<String, Boolean> map = new HashMap<>();

    if (!exist) {
      map.put("available", true);
      return ResponseEntity.ok(map);
    }

    map.put("available", false);
    return ResponseEntity.badRequest().body(map);
  }
}
