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
  private Long orgVersionCode;
  private LocalDateTime updatedAt;
  private String category; // 글 카테고리
  private Long authorCode; // 작성자 PK
  private Long updaterCode; // 수정자 pk


  // Entity -> DTO 변환 메서드
  public static MemeDetailResponseDto memeEntityToDto(Meme meme) {
    return MemeDetailResponseDto.builder()
        .code(meme.getCode())
        .title(meme.getTitle())
        .contents(meme.getContents())
        .startDate(meme.getStartDate())
        .endDate(meme.getEndDate())
        .createdAt(meme.getCreatedAt())
        .version(meme.getVersion())
        .orgVersionCode(meme.getOrgVersionCode())
        .updatedAt(meme.getUpdatedAt())
        .authorCode(meme.getAuthorCode() != null ? meme.getAuthorCode() : null)
        .updaterCode(meme.getUpdaterCode() != null ? meme.getUpdaterCode() : null)
        .build();
  }
}
