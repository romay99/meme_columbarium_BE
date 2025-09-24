package com.romay.meme.columbarium.boardcomment.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BoardCommentListResponseDto {

  private Integer page; // 현재 페이지
  private Integer totalPages; // 총 페이지 갯수
  private Long totalCount; // 총 댓글 갯수
  private List<BoardCommentListDto> data;

}
