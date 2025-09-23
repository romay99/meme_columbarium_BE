package com.romay.meme.columbarium.board.repository;

import com.romay.meme.columbarium.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    // Fetch Join 으로 멤버 닉네임까지 한번에 가져오자
    @Query("SELECT b FROM Board b JOIN FETCH b.member")
    Page<Board> findAllWithMember(Pageable pageable);
}
