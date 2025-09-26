package com.romay.meme.columbarium.like.repository;

import com.romay.meme.columbarium.like.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Likes, Long> {

  void deleteByMemberCodeAndMemeCode(Long memberCode, Long memeCode);

  Long countByMemeCode(Long memeCode);

  boolean existsByMemberCodeAndMemeCode(Long memberCode, Long memeCode);
}
