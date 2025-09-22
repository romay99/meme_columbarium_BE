package com.romay.meme.columbarium.like.service;

import com.romay.meme.columbarium.like.entity.Likes;
import com.romay.meme.columbarium.like.repository.LikeRepository;
import com.romay.meme.columbarium.member.dto.CustomUserDetails;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

  private final LikeRepository likeRepository;

  public void addLike(Long memeCode, CustomUserDetails userDetails) {
    if (likeRepository.existsByMemberCodeAndMemeCode(userDetails.getMember().getCode(), memeCode)) {
      return; // 이미 좋아요를 눌렀으면 메서드 종료
    }

    Likes like = Likes.builder()
        .memeCode(memeCode)
        .createdAt(LocalDateTime.now())
        .memberCode(userDetails.getMember().getCode())
        .build();

    likeRepository.save(like);
  }

  @Transactional
  public void removeLike(Long memeCode, CustomUserDetails userDetails) {
    likeRepository.deleteByMemberCodeAndMemeCode(userDetails.getMember().getCode(), memeCode);
  }
}
