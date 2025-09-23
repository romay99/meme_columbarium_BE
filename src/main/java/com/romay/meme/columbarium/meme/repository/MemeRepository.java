package com.romay.meme.columbarium.meme.repository;

import com.romay.meme.columbarium.meme.entity.Meme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MemeRepository extends JpaRepository<Meme, Long> {

  // Fetch Join 으로 카테고리까지 한번에 가져오자
  @Query("SELECT m from Meme m JOIN FETCH m.category ORDER BY m.createdAt DESC")
  Page<Meme> findAllWithCategory(Pageable pageable);
}
