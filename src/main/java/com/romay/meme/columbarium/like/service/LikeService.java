package com.romay.meme.columbarium.like.service;

import com.romay.meme.columbarium.exception.MemeNotFoundException;
import com.romay.meme.columbarium.like.entity.Likes;
import com.romay.meme.columbarium.like.repository.LikeRepository;
import com.romay.meme.columbarium.member.dto.CustomUserDetails;
import com.romay.meme.columbarium.meme.entity.Meme;
import com.romay.meme.columbarium.meme.repository.MemeRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

  private final LikeRepository likeRepository;
  private final MemeRepository memeRepository;

  @Transactional
  public void addLike(Long memeCode, CustomUserDetails userDetails) {
    Meme meme = memeRepository.findById(memeCode) // 지금 밈
            .orElseThrow(
                    () ->
                            new MemeNotFoundException("존재하지 않는 밈 게시글 입니다.")
            );

    Meme orgMeme = memeRepository.findById(meme.getOrgMemeCode()) // 원래 밈
            .orElseThrow(
                    () ->
                            new MemeNotFoundException("존재하지 않는 밈 게시글 입니다.")
            );

    if (likeRepository.existsByMemberCodeAndMemeCode(userDetails.getMember().getCode(), orgMeme.getCode())) {
      return; // 이미 좋아요를 눌렀으면 메서드 종료
    }

    Likes like = Likes.builder()
        .memeCode(orgMeme.getCode())
        .createdAt(LocalDateTime.now())
        .memberCode(userDetails.getMember().getCode())
        .build();

    meme.setLikesCount(meme.getLikesCount() + 1); // 최근 밈 좋아요 수 증가

    likeRepository.save(like);
  }

  @Transactional
  public void removeLike(Long memeCode, CustomUserDetails userDetails) {
    Meme meme = memeRepository.findById(memeCode)
        .orElseThrow(() -> new MemeNotFoundException("존재하지 않는 밈 게시글 입니다."));

    meme.setLikesCount(meme.getLikesCount() - 1); // 좋아요 수 감소

    likeRepository.deleteByMemberCodeAndMemeCode(userDetails.getMember().getCode(), meme.getOrgMemeCode());
  }
}
