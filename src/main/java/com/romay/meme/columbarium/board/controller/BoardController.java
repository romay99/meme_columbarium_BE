package com.romay.meme.columbarium.board.controller;

import com.romay.meme.columbarium.board.dto.BoardListResponseDto;
import com.romay.meme.columbarium.board.dto.BoardPostDto;
import com.romay.meme.columbarium.board.service.BoardService;
import com.romay.meme.columbarium.member.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    /**
     * 자유게시판 글 작성
     * @param dto
     * @return
     */
    public ResponseEntity<String> postBoard(@RequestBody BoardPostDto dto){
        // Spring Security의 SecurityContext에서 현재 사용자 정보 가져오기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

        boardService.postBoard(dto, userDetails);

        return ResponseEntity.ok().build();
    }

    /**
     * 자유게시판 글 목록 불러오기
     * @param page
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<BoardListResponseDto> getBoardList(@RequestParam(name = "page") int page){
        BoardListResponseDto boardList = boardService.getBoardList(page);

        return ResponseEntity.ok().body(boardList);
    }
}
