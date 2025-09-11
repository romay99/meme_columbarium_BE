package com.romay.meme.columbarium.meme.controller;

import com.romay.meme.columbarium.meme.entity.Meme;
import com.romay.meme.columbarium.meme.service.MemeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meme")
public class MemeController {

  private final MemeService memeService;

  @GetMapping("/list")
  public ResponseEntity<List<Meme>> getMemeList(
      @RequestParam(name = "page") int page
  ) {
    List<Meme> memeList = memeService.getMemeList();
    return ResponseEntity.ok(memeList);
  }

}
