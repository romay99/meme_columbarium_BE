package com.romay.meme.columbarium.board.controller;

import com.romay.meme.columbarium.board.dto.BoardListResponseDto;
import com.romay.meme.columbarium.board.dto.BoardPostDto;
import com.romay.meme.columbarium.board.service.BoardService;
import com.romay.meme.columbarium.member.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

  private final BoardService boardService;

  /**
   * 자유게시판 글 작성
   */
  @PostMapping("/post")
  public ResponseEntity<String> postBoard(@RequestBody BoardPostDto dto) {
    // Spring Security의 SecurityContext에서 현재 사용자 정보 가져오기
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

    boardService.postBoard(dto, userDetails);

    return ResponseEntity.ok().build();
  }

  /**
   * 자유게시판 글 목록 불러오기
   */
  @GetMapping("/list")
  public ResponseEntity<BoardListResponseDto> getBoardList(@RequestParam(name = "page") int page) {
    BoardListResponseDto boardList = boardService.getBoardList(page);

    return ResponseEntity.ok().body(boardList);
  }

  /**
   * 이미지 드래그 앤 드롭으로 업로드하는 API (S3 사용)
   *
   * @param file 프론트에서 날아온 이미지 파일
   * @return S3 에 저장된 이미지 URL Return
   */
  @PreAuthorize("hasRole('USER')")
  @PostMapping("/image")
  public ResponseEntity<String> imageUpload(@RequestParam("file") MultipartFile file) {
    String imageUrl = boardService.imageUpload(file); // image upload
    return ResponseEntity.ok(imageUrl);
  }
}
