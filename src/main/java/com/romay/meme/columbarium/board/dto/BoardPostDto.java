package com.romay.meme.columbarium.board.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BoardPostDto {
    private String title;
    private String contents;
    private LocalDateTime createAt;
}
