package com.romay.meme.columbarium.meme.dto;

import java.time.LocalDate;
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
public class MemeListDto {

  private Long code;
  private String title;
  private LocalDate startDate; // 밈이 흥한날짜
  private LocalDate endDate; // 밈이 망한 날짜

  private Long categoryCode; // 카테고리 PK
  private String category; // 밈 카테고리

}
