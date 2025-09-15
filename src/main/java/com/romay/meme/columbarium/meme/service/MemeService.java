package com.romay.meme.columbarium.meme.service;

import com.romay.meme.columbarium.category.dto.CategoryResponseDto;
import com.romay.meme.columbarium.category.entity.Category;
import com.romay.meme.columbarium.category.repository.CategoryRepository;
import com.romay.meme.columbarium.exception.MemeNotFoundException;
import com.romay.meme.columbarium.meme.dto.MemeDetailResponseDto;
import com.romay.meme.columbarium.meme.dto.MemeListDto;
import com.romay.meme.columbarium.meme.dto.MemeListResponseDto;
import com.romay.meme.columbarium.meme.dto.MemeUploadDto;
import com.romay.meme.columbarium.meme.entity.Meme;
import com.romay.meme.columbarium.meme.repository.MemeRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemeService {

  private final MemeRepository memeRepository;
  private final CategoryRepository categoryRepository;

  public MemeListResponseDto getMemeList(int page) {
    int pageSize = 10; // 한번에 가져올 데이터는 10개 고정
    Pageable pageable = PageRequest.of(page - 1, pageSize); // 페이지는 0부터 시작

    Page<Meme> memePage = memeRepository.findAll(pageable);

    // DTO 로 변환
    List<MemeListDto> dtoList = memePage.getContent()
        .stream().map(
            item -> {
              return MemeListDto.builder()
                  .code(item.getCode())
                  .title(item.getTitle())
                  .startDate(item.getStartDate())
                  .endDate(item.getEndDate())
                  .build();
            }
        ).toList();

    MemeListResponseDto dto = MemeListResponseDto.builder()
        .data(dtoList)
        .page(page)
        .totalPages(memePage.getTotalPages())
        .totalCount(memePage.getTotalElements())
        .build();

    return dto;
  }

  public String imageUpload(MultipartFile file) {
    try {
      // TODO 이미지 업로드 구현해야함
      log.info("image Upload Success : " + file.getOriginalFilename());
    } catch (Exception e) {
      log.error("image Upload Error : " + e.getMessage());
    }
    return "https://placehold.co/600x400"; // 임시 이미지 url return
  }

  public MemeDetailResponseDto getMemeInfo(Long memeCode) {
    Meme meme = memeRepository.findById(memeCode)
        .orElseThrow(() -> new MemeNotFoundException("존재하지 않는 밈 입니다."));

    MemeDetailResponseDto dto = MemeDetailResponseDto.memeEntityToDto(meme);

    // Category 이름 가져오기
    Category category = categoryRepository.findById(meme.getCategoryCode()).get();
    dto.setCategory(category.getName());

    return dto; // DTO 로 변환 후 return
  }

  public void uploadMeme(MemeUploadDto uploadDto) {
    log.info("memeUpload!" + uploadDto.getTitle());
    System.out.println(uploadDto);
  }

  public List<CategoryResponseDto> getCategories() {
    List<Category> allCategory = categoryRepository.findAll();

    List<CategoryResponseDto> response = new ArrayList<>();
    for (Category category : allCategory) {
      // 카테고리 PK , 이름 세팅
      CategoryResponseDto responseDto = new CategoryResponseDto();
      responseDto.setName(category.getName());
      responseDto.setCode(category.getCode());

      response.add(responseDto);
    }
    return response;
  }
}
