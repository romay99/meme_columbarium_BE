package com.romay.meme.columbarium.memecomment.repository;

import com.romay.meme.columbarium.memecomment.entity.MemeComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemeCommentRepository extends JpaRepository<MemeComment, Long> {

  // Fetch Join 으로 멤버 닉네임까지 한번에 가져오자
  @Query("SELECT c from MemeComment c JOIN FETCH c.member "
      + "WHERE c.memeCode = :meme ORDER BY c.createdAt ASC")
  Page<MemeComment> findAllWithMemberByMemeCode(@Param("meme") Long orgMemeCode, Pageable pageable);

}
