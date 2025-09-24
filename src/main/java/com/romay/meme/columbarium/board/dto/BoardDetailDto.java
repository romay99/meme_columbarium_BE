package com.romay.meme.columbarium.board.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class BoardDetailDto {
    private Long code;
    private String title;
    private String contents;
    private LocalDateTime createdAt;
    private Long authorCode;
    private String authorNickName;
}
