package com.romay.meme.columbarium.meme.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemeListResponseDto {

  Integer page; // 현재 페이지
  Integer totalPages; // 총 페이지 갯수
  Long totalCount; // 총 게시글 갯수
  List<MemeListDto> data;
}
