package com.romay.meme.columbarium.meme.controller;

import com.romay.meme.columbarium.category.dto.CategoryResponseDto;
import com.romay.meme.columbarium.meme.dto.MemeDetailResponseDto;
import com.romay.meme.columbarium.meme.dto.MemeListResponseDto;
import com.romay.meme.columbarium.meme.dto.MemeUploadDto;
import com.romay.meme.columbarium.meme.service.MemeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meme")
@CrossOrigin(origins = "http://localhost:3000") // React 개발 서버 허용
public class MemeController {

  private final MemeService memeService;

  /**
   * 밈 게시판 메인페이지 호출 API
   *
   * @param page 조회할 페이지 번호
   * @return page 번호에 해당하는 밈 리스트 return
   */
  @GetMapping("/list")
  public ResponseEntity<MemeListResponseDto> getMemeList(
      @RequestParam(name = "page") int page
  ) {
    MemeListResponseDto memeList = memeService.getMemeList(page);// 호출
    return ResponseEntity.ok(memeList);
  }

  /**
   * 밈 상세보기 페이지 API
   *
   * @param memeCode 상세조회할 밈 PK
   * @return DTO 로 변환 후 리턴
   */
  @GetMapping("/info")
  public ResponseEntity<MemeDetailResponseDto> getMemeInfo(@RequestParam("code") Long memeCode) {
    MemeDetailResponseDto memeInfo = memeService.getMemeInfo(memeCode);
    return ResponseEntity.ok(memeInfo);
  }

  /**
   * 이미지 드래그 앤 드롭으로 업로드하는 API (S3 사용)
   *
   * @param file 프론트에서 날아온 이미지 파일
   * @return S3 에 저장된 이미지 URL Return
   */
  @PostMapping("/image")
  public ResponseEntity<String> imageUpload(@RequestParam("file") MultipartFile file) {
    String imageUrl = memeService.imageUpload(file); // image upload
    return ResponseEntity.ok(imageUrl);
  }

  /**
   * 밈 게시글 업로드 하는 기능
   *
   * @param uploadDto 업로드 하는 DTO
   */
  @PostMapping("/upload")
  public ResponseEntity<String> uploadMeme(@RequestBody MemeUploadDto uploadDto) {
    memeService.uploadMeme(uploadDto);
    return ResponseEntity.ok().build();
  }

  /**
   * 밈 카테고리 리스트 return 하는 메서드
   *
   * @return 밈 카테고리 리스트 return 해줌
   */
  @GetMapping("/categories")
  public ResponseEntity<List<CategoryResponseDto>> getCategories() {
    List<CategoryResponseDto> categories = memeService.getCategories();
    return ResponseEntity.ok(categories);
  }

}
