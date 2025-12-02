package com.romay.meme.columbarium.boardcomment.repository;

import com.romay.meme.columbarium.boardcomment.entity.BoardComment;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {

  // Fetch Join 으로 멤버 닉네임까지 한번에 가져오자
  @Query("SELECT c from BoardComment c JOIN FETCH c.member "
      + "WHERE c.boardCode = :board ORDER BY c.createdAt ASC")
  Page<BoardComment> findAllWithMemberByBoardCode(@Param("board") Long boardCode,
      Pageable pageable);

  @Modifying
  @Transactional
  @Query("DELETE FROM BoardComment c WHERE c.boardCode = :boardCode")
  int deleteAllByBoardCode(@Param("boardCode") Long boardCode);
}
