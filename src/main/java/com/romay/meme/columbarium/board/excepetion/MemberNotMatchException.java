package com.romay.meme.columbarium.board.excepetion;

public class MemberNotMatchException extends RuntimeException {
    public MemberNotMatchException(String message) {
        super(message);
    }
}
