package com.romay.meme.columbarium.board.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class BoardListResponseDto {
    Integer page; // 현재 페이지
    Integer totalPages; // 총 페이지 갯수
    Long totalCount; // 총 게시글 갯수
    List<BoardListDto> data;
}
