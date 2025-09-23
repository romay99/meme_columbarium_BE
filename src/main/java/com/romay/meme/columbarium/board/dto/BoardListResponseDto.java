package com.romay.meme.columbarium.board.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class BoardListResponseDto {

  private Integer page; // 현재 페이지
  private Integer totalPages; // 총 페이지 갯수
  private Long totalCount; // 총 게시글 갯수
  private List<BoardListDto> data;
}
