package com.romay.meme.columbarium.boardcomment.service;

import com.romay.meme.columbarium.boardcomment.dto.BoardCommentListDto;
import com.romay.meme.columbarium.boardcomment.dto.BoardCommentListResponseDto;
import com.romay.meme.columbarium.boardcomment.dto.BoardCommentPostDto;
import com.romay.meme.columbarium.boardcomment.entity.BoardComment;
import com.romay.meme.columbarium.boardcomment.repository.BoardCommentRepository;
import com.romay.meme.columbarium.member.dto.CustomUserDetails;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardCommentService {

  private static final Logger log = LoggerFactory.getLogger(BoardCommentService.class);
  private final BoardCommentRepository boardCommentRepository;

  public void postBoardComment(BoardCommentPostDto dto, CustomUserDetails userDetails) {
    BoardComment boardComment = BoardComment.builder()
        .contents(dto.getContents())
        .createdAt(LocalDateTime.now())
        .boardCode(dto.getBoardCode())
        .authorCode(userDetails.getMember().getCode())
        .build();

    boardCommentRepository.save(boardComment);
    log.info("postBoardComment : " + dto.getContents() + " postBoardComment Author : "
        + userDetails.getMember().getNickname());
  }

  public BoardCommentListResponseDto getBoardCommentList(int page, Long boardCode) {
    int pageSize = 10; // 댓글 10개씩 받아오기
    Pageable pageable = PageRequest.of(page - 1, pageSize);

    Page<BoardComment> comments = boardCommentRepository.findAllWithMemberByBoardCode(boardCode,
        pageable);

    List<BoardCommentListDto> dtoList = comments.getContent()
        .stream().map(
            item -> {
              return BoardCommentListDto.builder()
                  .code(item.getCode())
                  .contents(item.getContents())
                  .createdAt(item.getCreatedAt())
                  .boardCode(item.getBoardCode())
                  .authorNickName(item.getMember().getNickname())
                  .build();
            }
        ).toList();

    BoardCommentListResponseDto dto = BoardCommentListResponseDto.builder()
        .data(dtoList)
        .page(page)
        .totalPages(comments.getTotalPages())
        .totalCount(comments.getTotalElements())
        .build();

    return dto;
  }
}
