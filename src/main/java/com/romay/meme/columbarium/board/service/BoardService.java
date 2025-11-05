package com.romay.meme.columbarium.board.service;

import com.romay.meme.columbarium.board.dto.*;
import com.romay.meme.columbarium.board.entity.Board;
import com.romay.meme.columbarium.board.excepetion.MemberNotMatchException;
import com.romay.meme.columbarium.board.repository.BoardRepository;
import com.romay.meme.columbarium.boardcomment.repository.BoardCommentRepository;
import com.romay.meme.columbarium.exception.BoardNotFoundException;
import com.romay.meme.columbarium.member.dto.CustomUserDetails;
import java.util.ArrayList;
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
    int pageSize = 10; // 한번에 보여줄 데이터
    Pageable pageable = PageRequest.of(page - 1, pageSize);

    // 1️⃣ 공지글만 DB에서 바로 조회
    List<Board> noticeList = boardRepository.findAllNoticeWithMember();

    // 2️⃣ 일반 글 조회 (페이징 적용)
    Page<Board> boardPage = boardRepository.findAllWithMember(pageable);
    List<Board> regularList = boardPage.getContent().stream()
        .toList();

    // 3️⃣ 공지 + 일반 글 합치기
    List<Board> combinedList = new ArrayList<>();
    combinedList.addAll(noticeList);
    combinedList.addAll(regularList);

    // 4️⃣ DTO 변환
    List<BoardListDto> list = combinedList.stream()
        .map(item -> BoardListDto.builder()
            .code(item.getCode())
            .title(item.getTitle())
            .contents(item.getContents())
            .createdAt(item.getCreateAt())
            .authorNickName(item.getMember().getNickname())
            .notice(item.isNotice()) // React에서 스타일 적용 가능
            .build())
        .toList();

    // 5️⃣ 전체 카운트
    long totalCount = boardRepository.count();

    return BoardListResponseDto.builder()
        .data(list)
        .page(page)
        .totalPages((int) Math.ceil((double) totalCount / pageSize))
        .totalCount(totalCount)
        .build();
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
    if (board.getMember().getCode() != userDetails.getMember().getCode()) {
      throw new MemberNotMatchException("본인의 글만 삭제할 수 있습니다.");
    }

    log.info("Delete Board : " + board.getTitle() + " By " + userDetails.getMember().getCode()
        + " User");

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
    log.info("Update Board : " + board.getTitle() + " By " + userDetails.getMember().getCode()
        + " User");
  }
}
