package com.romay.meme.columbarium.board.service;

import com.romay.meme.columbarium.board.dto.*;
import com.romay.meme.columbarium.board.entity.Board;
import com.romay.meme.columbarium.board.excepetion.MemberNotMatchException;
import com.romay.meme.columbarium.board.repository.BoardRepository;
import com.romay.meme.columbarium.boardcomment.repository.BoardCommentRepository;
import com.romay.meme.columbarium.exception.BoardNotFoundException;
import com.romay.meme.columbarium.member.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {

  private final BoardRepository boardRepository;
  private final BoardCommentRepository boardCommentRepository;

  public void postBoard(BoardPostDto dto, CustomUserDetails userDetails) {

    Board board = Board.builder()
        .title(dto.getTitle())
        .contents(dto.getContents())
        .createAt(LocalDateTime.now())
        .authorCode(userDetails.getMember().getCode())
        .build();

    boardRepository.save(board);
    log.info(
        "Board posted: " + board.getTitle() + " author : " + userDetails.getMember().getNickname());
  }

  public BoardListResponseDto getBoardList(int page) {
    int pageSize = 10; // 한번에 표출해주는 데이터는 10개
    Pageable pageable = PageRequest.of(page - 1, pageSize); // 페이지는 0부터 시작

    Page<Board> boardPage = boardRepository.findAllWithMember(pageable);

    // DTO 로 변환 후 반환
    List<BoardListDto> list = boardPage.getContent().stream().map(
        item -> BoardListDto.builder()
            .code(item.getCode())
            .title(item.getTitle())
            .contents(item.getContents())
            .createdAt(item.getCreateAt())
            .authorNickName(item.getMember().getNickname())
            .build()).toList();

    BoardListResponseDto response = BoardListResponseDto.builder()
        .data(list)
        .page(page)
        .totalPages(boardPage.getTotalPages())
        .totalCount(boardPage.getTotalElements())
        .build();

    return response;
  }

  public String imageUpload(MultipartFile file) {
    // TODO 이미지 업로드 기능 작업해야함
    return "";
  }

  public BoardDetailDto getBoardInfo(Long boardCode) {
    Board board = boardRepository.findByCodeWithMember(boardCode).orElseThrow(
            () -> new BoardNotFoundException("존재하지 않는 글입니다.")
    );

    BoardDetailDto dto = BoardDetailDto.builder()
            .code(board.getCode())
            .title(board.getTitle())
            .contents(board.getContents())
            .createdAt(board.getCreateAt())
            .authorCode(board.getMember().getCode())
            .authorNickName(board.getMember().getNickname())
            .build();

    return dto;
  }

  public void deleteBoard(Long boardCode, CustomUserDetails userDetails) {
    Board board = boardRepository.findById(boardCode).orElseThrow(
            () -> new BoardNotFoundException("존재하지 않는 게시글입니다.")
    );

    // 본인 글인지 체크
    if(board.getMember().getCode() != userDetails.getMember().getCode()) {
      throw new MemberNotMatchException("본인의 글만 삭제할 수 있습니다.");
    }

    log.info("Delete Board : " + board.getTitle() + " By " + userDetails.getMember().getCode() + " User");

    boardRepository.delete(board);
    boardCommentRepository.deleteAllByBoardCode(boardCode);


  }

  public void updateBoard(BoardUpdateDto dto, CustomUserDetails userDetails) {
    Board board = boardRepository.findById(dto.getCode()).orElseThrow(
            () -> new BoardNotFoundException("존재하지 않는 게시글 입니다.")
    );

    if (board.getMember().getCode() != userDetails.getMember().getCode()) {
      throw new MemberNotMatchException("자신의 글만 수정할 수 있습니다.");
    }

    Board update = Board.builder()
            .code(board.getCode())
            .title(dto.getTitle())
            .contents(dto.getContents())
            .createAt(board.getCreateAt())
            .authorCode(board.getAuthorCode())
            .member(board.getMember())
            .build();

    boardRepository.save(update);
    log.info("Update Board : " + board.getTitle() + " By " + userDetails.getMember().getCode() + " User");
  }
}
