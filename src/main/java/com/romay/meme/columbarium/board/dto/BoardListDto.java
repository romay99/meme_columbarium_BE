package com.romay.meme.columbarium.board.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class BoardListDto {
    private Long code;
    private String title;
    private String contents;
    private LocalDateTime createdAt;
    private String authorNickName; // 글 작성자 닉네임
}
