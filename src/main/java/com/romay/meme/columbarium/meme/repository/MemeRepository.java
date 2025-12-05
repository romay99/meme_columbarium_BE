package com.romay.meme.columbarium.meme.repository;

import com.romay.meme.columbarium.meme.entity.Meme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MemeRepository extends JpaRepository<Meme, Long>, MemeRepositoryCustom {

  // Fetch Join 으로 카테고리까지 한번에 가져오자
  @Query("SELECT m from Meme m JOIN FETCH m.category WHERE m.latest = true ORDER BY m.createdAt DESC")
  Page<Meme> findAllWithCategory(Pageable pageable);

  // Fetch Join 으로 카테고리까지 한번에 가져오자
  @Query("SELECT m from Meme m JOIN FETCH m.category WHERE m.latest = true ORDER BY m.likesCount DESC")
  Page<Meme> findAllWithCategoryOrderByLikes(Pageable pageable);

  // Fetch Join 으로 수정자까지 한번에 가져오자
  @Query("SELECT m from Meme m JOIN FETCH m.member WHERE m.latest = false AND m.orgMemeCode = :code ORDER BY m.version DESC")
  Page<Meme> findHistory(Pageable pageable, @Param("code") Long memeCode);

  @Query("SELECT m.likesCount from Meme m WHERE m.orgMemeCode= :code AND m.code =:code")
  int findOrgMemeLikesCount(@Param("code") Long orgMemeCode, @Param("code") Long memeCode);

  @Query("SELECT m from Meme m JOIN FETCH m.member WHERE m.code = :code")
  Optional<Meme> findByIdWithMember(@Param("code") Long memeCode);
}
