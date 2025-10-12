package com.romay.meme.columbarium.meme.repository;

import com.romay.meme.columbarium.meme.entity.Meme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemeRepositoryCustom {

  Page<Meme> findBySearchAndSort(String keyword, String sortOption, Pageable pageable);
}
