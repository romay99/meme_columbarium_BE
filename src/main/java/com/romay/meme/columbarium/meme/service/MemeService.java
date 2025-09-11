package com.romay.meme.columbarium.meme.service;

import com.romay.meme.columbarium.meme.entity.Meme;
import com.romay.meme.columbarium.meme.repository.MemeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemeService {

  private final MemeRepository memeRepository;

  public List<Meme> getMemeList() {
    return memeRepository.findAll();
  }

}
