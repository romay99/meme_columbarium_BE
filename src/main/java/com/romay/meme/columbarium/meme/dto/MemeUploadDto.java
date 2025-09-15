package com.romay.meme.columbarium.meme.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MemeUploadDto {

  private LocalDate startDate;
  private LocalDate endDate;
  private String contents;
  private String title;
  private Long category;

  @JsonSetter("startDate")
  public void setStartDate(String startDate) {
    // React에서 "2020-02" → LocalDate 변환
    this.startDate = LocalDate.parse(startDate + "-01");
  }

  @JsonSetter("endDate")
  public void setEndDate(String endDate) {
    this.endDate = LocalDate.parse(endDate + "-01");
  }

}
