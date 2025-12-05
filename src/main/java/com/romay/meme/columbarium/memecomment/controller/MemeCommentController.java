package com.romay.meme.columbarium.memecomment.controller;

import com.romay.meme.columbarium.member.dto.CustomUserDetails;
import com.romay.meme.columbarium.memecomment.dto.MemeCommentListResponseDto;
import com.romay.meme.columbarium.memecomment.dto.MemeCommentPostDto;
import com.romay.meme.columbarium.memecomment.service.MemeCommentService;
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
@RequestMapping("/comment/meme")
public class MemeCommentController {

  private final MemeCommentService memeCommentService;

  /**
   * 밈 게시판 댓글 작성 메소드
   */
  @PostMapping("/post")
  public ResponseEntity<String> postMemeComment(@RequestBody MemeCommentPostDto dto) {
    // Spring Security의 SecurityContext에서 현재 사용자 정보 가져오기
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

    memeCommentService.postMemeComment(dto, userDetails);
    return ResponseEntity.ok().build();

  }

  /**
   * 밈 게시판 댓글 조회 메서드
   */
  @GetMapping("/list")
  public ResponseEntity<MemeCommentListResponseDto> getMemeCommentList(
      @RequestParam(name = "page") int page,
      @RequestParam(name = "meme") Long orgMemeCode) {
    MemeCommentListResponseDto memeCommentList = memeCommentService.getMemeCommentList(page,
            orgMemeCode);
    return ResponseEntity.ok(memeCommentList);
  }
}
