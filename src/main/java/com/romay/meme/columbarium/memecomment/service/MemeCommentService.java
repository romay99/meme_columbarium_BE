package com.romay.meme.columbarium.memecomment.service;

import com.romay.meme.columbarium.exception.MemeNotFoundException;
import com.romay.meme.columbarium.member.dto.CustomUserDetails;
import com.romay.meme.columbarium.meme.repository.MemeRepository;
import com.romay.meme.columbarium.memecomment.dto.MemeCommentListDto;
import com.romay.meme.columbarium.memecomment.dto.MemeCommentListResponseDto;
import com.romay.meme.columbarium.memecomment.dto.MemeCommentPostDto;
import com.romay.meme.columbarium.memecomment.entity.MemeComment;
import com.romay.meme.columbarium.memecomment.repository.MemeCommentRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemeCommentService {

  private final MemeCommentRepository memeCommentRepository;
  private final MemeRepository memeRepository;

  public void postMemeComment(MemeCommentPostDto dto, CustomUserDetails userDetails) {
    if (!memeRepository.existsById(dto.getOrgMemeCode())){
      throw new MemeNotFoundException("존재하지 않는 밈 게시물입니다.");
    }
    MemeComment entity = MemeComment.builder()
        .contents(dto.getContents())
        .createdAt(LocalDateTime.now())
        .memeCode(dto.getOrgMemeCode())
        .authorCode(userDetails.getMember().getCode())
        .build();

    memeCommentRepository.save(entity);

    log.info("postMemeComment : " + dto.getContents() + " postMemeComment Author : "
        + userDetails.getMember().getNickname());
  }

  public MemeCommentListResponseDto getMemeCommentList(int page, Long orgMemeCode) {
    int pageSize = 10; // 댓글 10개씩 가져오기
    Pageable pageable = PageRequest.of(page - 1, pageSize);

    Page<MemeComment> comments = memeCommentRepository.findAllWithMemberByMemeCode(orgMemeCode,
        pageable);

    // DTO 로 변환
    List<MemeCommentListDto> dtoList = comments.getContent()
        .stream().map(
            item -> {
              return MemeCommentListDto.builder()
                  .code(item.getCode())
                  .contents(item.getContents())
                  .createdAt(item.getCreatedAt())
                  .memeCode(item.getMemeCode())
                  .authorNickName(item.getMember().getNickname())
                  .build();
            }
        ).toList();

    MemeCommentListResponseDto dto = MemeCommentListResponseDto.builder()
        .data(dtoList)
        .page(page)
        .totalPages(comments.getTotalPages())
        .totalCount(comments.getTotalElements())
        .build();

    return dto;
  }
}
