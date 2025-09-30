package com.romay.meme.columbarium.meme.service;

import com.romay.meme.columbarium.category.dto.CategoryResponseDto;
import com.romay.meme.columbarium.category.entity.Category;
import com.romay.meme.columbarium.category.repository.CategoryRepository;
import com.romay.meme.columbarium.exception.MemberNotFoundException;
import com.romay.meme.columbarium.exception.MemeNotFoundException;
import com.romay.meme.columbarium.like.repository.LikeRepository;
import com.romay.meme.columbarium.member.dto.CustomUserDetails;
import com.romay.meme.columbarium.member.entity.Member;
import com.romay.meme.columbarium.member.repository.MemberRepository;
import com.romay.meme.columbarium.meme.dto.*;
import com.romay.meme.columbarium.meme.entity.Meme;
import com.romay.meme.columbarium.meme.repository.MemeRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemeService {

  private final MemeRepository memeRepository;
  private final CategoryRepository categoryRepository;
  private final MemberRepository memberRepository;
  private final LikeRepository likeRepository;

  public MemeListResponseDto getMemeList(int page) {
    int pageSize = 10; // 한번에 가져올 데이터는 10개 고정
    Pageable pageable = PageRequest.of(page - 1, pageSize); // 페이지는 0부터 시작

    Page<Meme> memePage = memeRepository.findAllWithCategory(pageable);

    // DTO 로 변환
    List<MemeListDto> dtoList = memePage.getContent()
        .stream().map(
            item -> {
              return MemeListDto.builder()
                  .code(item.getCode())
                  .title(item.getTitle())
                  .startDate(item.getStartDate())
                  .endDate(item.getEndDate())
                  .category(item.getCategory().getName())
                  .categoryCode(item.getCategory().getCode())
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

  public MemeDetailResponseDto getMemeInfo(Long memeCode, CustomUserDetails userDetails) {
    Meme meme = memeRepository.findById(memeCode)
        .orElseThrow(() -> new MemeNotFoundException("존재하지 않는 밈 입니다."));

    MemeDetailResponseDto dto = MemeDetailResponseDto.memeEntityToDto(meme);
    Member author = memberRepository.findById(dto.getAuthorCode()).orElseThrow(
        () -> new MemberNotFoundException("존재하지 않는 글 작성자입니다.")
    );

    dto.setAuthorNickName(author.getNickname()); // DTO 에 작성자 닉네임 추가

    // Category 이름 가져오기
    Category category = categoryRepository.findById(meme.getCategoryCode()).get();
    dto.setCategory(category.getName());

    if (userDetails != null) {
      // 좋아요 했는지 여부 조회
      dto.setLikes(likeRepository.
          existsByMemberCodeAndMemeCode(userDetails.getMember().getCode(), memeCode));
    }

    dto.setLikesCount(likeRepository.countByMemeCode(memeCode)); // 좋아요 총 갯수 조회

    return dto; // DTO 로 변환 후 return
  }

  @Transactional
  public void uploadMeme(MemeUploadDto uploadDto, CustomUserDetails userDetails) {
    // TODO 썸네일 업로드 기능 만들어야함

    Meme meme = Meme.builder()
//      .thumbnail(thumbnail) 썸네일 업로드 기능 만들쟈
        .authorCode(userDetails.getMember().getCode())
        .startDate(uploadDto.getStartDate())
        .endDate(uploadDto.getEndDate())
        .contents(uploadDto.getContents())
        .title(uploadDto.getTitle())
        .categoryCode(uploadDto.getCategory())
        .createdAt(LocalDateTime.now())
        .version(1L)
        .latest(true)
        .build();

    memeRepository.save(meme); // DB 에 save
    meme.setOrgMemeCode(meme.getCode()); // 더티 체킹으로 자동 update

    log.info("memeUpload! : " + uploadDto.getTitle() + " author : " + userDetails.getMember()
        .getNickname());

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

  @Transactional
  public void updateMeme(MemeUpdateDto dto, CustomUserDetails userDetails) {
    // TODO 밈 수정하는 기능 만들어야함

    // 1수정할 글과 JWT 토큰 유저 검증? ( 사용자가 익명사용자인지만 체크하면 될듯?? )

    //2 수정 기록 남기기
    // 새로운 글을 남기는 것으로 수정 기록을 남긴다.
    // 추후 동시성 문제 해결해야 함

    //3 수정하기
    Meme orgMeme = memeRepository.findById(dto.getCode()).orElseThrow(
        () -> new MemeNotFoundException("존재하지 않는 밈 입니다.")
    );

    Meme newMeme = Meme.builder()
        .title(dto.getTitle())
        .contents(dto.getContents())
        .startDate(dto.getStartDate())
        .endDate(dto.getEndDate())
        .orgMemeCode(orgMeme.getCode())
        .version(orgMeme.getVersion() + 1)
        .createdAt(orgMeme.getCreatedAt())
        .updatedAt(LocalDateTime.now())
        .categoryCode(dto.getCategory())
        .authorCode(orgMeme.getAuthorCode())
        .updaterCode(userDetails.getMember().getCode())
        .latest(true)
        .build();

    memeRepository.save(newMeme); // 새로운 밈 insert
    orgMeme.setLatest(false); // 기존 밈 latest 변수 false 로 변경

    //4 로그 남기기
    log.info(
        "Meme updated : = " + dto.getCode() + " by " + userDetails.getMember().getCode() + " User");

  }

  public MemeUpdateHistoryListDto getUpdateHistoryList(int page, Long memeCode) {
    int pageSize = 10; // 한번에 가져올 데이터는 10개 고정
    Pageable pageable = PageRequest.of(page - 1, pageSize); // 페이지는 0부터 시작

    Page<Meme> memePage = memeRepository.findHistory(pageable,memeCode);

    // DTO 로 변환
    List<MemeUpdateHistoryDto> dtoList = memePage.getContent()
            .stream().map(
                    item -> {
                      return MemeUpdateHistoryDto.builder()
                              .title(item.getTitle())
                              .startDate(item.getStartDate())
                              .endDate(item.getEndDate())
                              .category(item.getCategory().getName())
                              .categoryCode(item.getCategory().getCode())
                              .modifier(item.getMember().getNickname())
                              .updateAt(item.getUpdatedAt())
                              .build();
                    }
            ).toList();

    MemeUpdateHistoryListDto dto = MemeUpdateHistoryListDto.builder()
            .data(dtoList)
            .page(page)
            .totalPages(memePage.getTotalPages())
            .totalCount(memePage.getTotalElements())
            .build();
    return dto;
  }
}
