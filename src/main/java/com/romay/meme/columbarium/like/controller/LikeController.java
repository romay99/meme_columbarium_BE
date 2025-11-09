package com.romay.meme.columbarium.like.controller;

import com.romay.meme.columbarium.like.dto.LikesRequestDto;
import com.romay.meme.columbarium.like.service.LikeService;
import com.romay.meme.columbarium.member.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
public class LikeController {

  private final LikeService likeService;

  /**
   * 좋아요 추가
   */
  @PostMapping("/add")
  public ResponseEntity<String> addLike(@RequestBody LikesRequestDto dto) {
    // Spring Security의 SecurityContext에서 현재 사용자 정보 가져오기
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

    likeService.addLike(dto.getMemeCode(), userDetails);
    return ResponseEntity.ok().build();

  }

  /**
   * 좋아요 삭제
   */
  @PostMapping("/rm")
  public ResponseEntity<String> removeLike(@RequestBody LikesRequestDto dto) {
    // Spring Security의 SecurityContext에서 현재 사용자 정보 가져오기
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

    likeService.removeLike(dto.getMemeCode(), userDetails);
    return ResponseEntity.ok().build();
  }

  /**
   * 좋아요 유무 검증 메서드 true = 좋아요 했음 false = 좋아요 하지 않음
   */
  @GetMapping("/check")
  public ResponseEntity<Boolean> checkLike(
      @RequestParam Long orgMemeCode
  ) {
    // Spring Security의 SecurityContext에서 현재 사용자 정보 가져오기
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

    boolean isLiked = likeService.checkLike(orgMemeCode, userDetails);
    return ResponseEntity.ok(isLiked);
  }
}
