package com.romay.meme.columbarium.board.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BoardUpdateDto {
    private Long code;
    private String title;
    private String contents;

}
