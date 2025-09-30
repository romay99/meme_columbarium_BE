package com.romay.meme.columbarium.meme.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class MemeUpdateHistoryDto {
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private String category;
    private Long categoryCode;
    private String modifier;
    private LocalDateTime updateAt;
}
