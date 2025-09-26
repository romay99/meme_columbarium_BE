package com.romay.meme.columbarium.member.controller;

import com.romay.meme.columbarium.member.dto.LoginRequest;
import com.romay.meme.columbarium.member.dto.LoginResponse;
import com.romay.meme.columbarium.member.dto.SignUpRequest;
import com.romay.meme.columbarium.member.service.AuthService;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

  private final AuthService authService;

  /**
   * 로그인 하는 메서드
   *
   * @param request
   * @return
   */
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    LoginResponse result = authService.login(request);
    return ResponseEntity.ok().body(result);
  }

  /**
   * 회원가입 메서드
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
   * @param memberId
   * @return
   */
  @GetMapping("/check-id/{id}")
  public ResponseEntity<Map<String,Boolean>> idCheck(@PathVariable("id")String memberId) {
    boolean exist = authService.idCheck(memberId);
    HashMap<String, Boolean> map = new HashMap<>();

    if (!exist) {
      map.put("available", true);
      return ResponseEntity.ok(map);
    }

    map.put("available",false);
    return ResponseEntity.badRequest().body(map);
  }
}
