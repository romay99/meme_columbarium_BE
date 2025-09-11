package com.romay.meme.columbarium.meme.repository;

import com.romay.meme.columbarium.meme.entity.Meme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemeRepository extends JpaRepository<Meme, Long> {

}
