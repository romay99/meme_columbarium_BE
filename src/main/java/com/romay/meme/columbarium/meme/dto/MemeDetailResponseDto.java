package com.romay.meme.columbarium.meme.dto;

import com.romay.meme.columbarium.meme.entity.Meme;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemeDetailResponseDto {

  private Long code;
  private String title;
  private String contents;
  private LocalDate startDate;
  private LocalDate endDate;
  private LocalDateTime createdAt; // 작성일
  private Long version;
  private Long orgMemeCode;
  private LocalDateTime updatedAt;
  private String category; // 글 카테고리
  private Long authorCode; // 작성자 PK
  private String authorNickName; // 작성자 닉네임
  private Long updaterCode; // 수정자 pk
  private String thumbnail; // 썸네일 URL

  private Integer likesCount; // 총 좋아요 갯수
  private boolean isLikes; // 글 조회하는 유저가 좋아요 눌렀는지 여부


  // Entity -> DTO 변환 메서드
  public static MemeDetailResponseDto memeEntityToDto(Meme meme) {
    return MemeDetailResponseDto.builder()
        .thumbnail(meme.getThumbnail())
        .code(meme.getCode())
        .title(meme.getTitle())
        .contents(meme.getContents())
        .startDate(meme.getStartDate())
        .endDate(meme.getEndDate())
        .createdAt(meme.getCreatedAt())
        .version(meme.getVersion())
        .orgMemeCode(meme.getOrgMemeCode())
        .updatedAt(meme.getUpdatedAt())
        .authorCode(meme.getAuthorCode() != null ? meme.getAuthorCode() : null)
        .updaterCode(meme.getUpdaterCode() != null ? meme.getUpdaterCode() : null)
        .build();
  }
}
