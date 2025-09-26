package com.romay.meme.columbarium.boardcomment.controller;

import com.romay.meme.columbarium.boardcomment.dto.BoardCommentListResponseDto;
import com.romay.meme.columbarium.boardcomment.dto.BoardCommentPostDto;
import com.romay.meme.columbarium.boardcomment.service.BoardCommentService;
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
@RequestMapping("/comment/board")
public class BoardCommentController {

  private final BoardCommentService boardCommentService;

  /**
   * 자유게시판 게시판 댓글 작성 메소드
   */
  @PostMapping("/post")
  public ResponseEntity<String> postBoardComment(@RequestBody BoardCommentPostDto dto) {
    // Spring Security의 SecurityContext에서 현재 사용자 정보 가져오기
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

    boardCommentService.postBoardComment(dto, userDetails);
    return ResponseEntity.ok().build();

  }

  /**
   * 자유 게시판 댓글 조회 메서드
   */
  @GetMapping("/list")
  public ResponseEntity<BoardCommentListResponseDto> getMemeCommentList(
      @RequestParam(name = "page") int page,
      @RequestParam(name = "board") Long boardCode) {
    BoardCommentListResponseDto boardCommentList = boardCommentService.getBoardCommentList(page,
        boardCode);
    return ResponseEntity.ok(boardCommentList);
  }

}
