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
import com.romay.meme.columbarium.s3.service.S3Service;
import com.romay.meme.columbarium.util.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
  private final S3Service s3Service;
  private final JwtTokenProvider jwtTokenProvider;

  public MemeListResponseDto getMemeList(String keyWord, int page, String sort) {
    int pageSize = 15; // 한번에 가져올 데이터는 15개 고정
    Pageable pageable = PageRequest.of(page - 1, pageSize); // 페이지는 0부터 시작
    Page<Meme> memePage = null;

    memePage = memeRepository.findBySearchAndSort(keyWord, sort, pageable);

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
      String uploadFileUrl = s3Service.uploadTempFile(file);

      return uploadFileUrl;
    } catch (Exception e) {
      log.error("image Upload Error : " + e.getMessage());
    }
    return "";
  }

  public MemeDetailResponseDto getMemeInfo(Long memeCode, HttpServletRequest request) {
    // TODO 여기부분 fetch join 으로 meme 이랑 member(작성자) 한번에 가져오자
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

    // 좋아요 했는지 안했는지 여부 체크
    String jwt = jwtTokenProvider.getJwtFromRequest(request);
    boolean token = false;
    String newAccessToken = null;

    try {
      boolean result = jwtTokenProvider.validateTokenForGetMemeInfo(jwt);
      if (result) {
        token = true; // 토큰이 정상적으로 검증되었을때에만 토큰 flag 를 true로 설정
      }
    } catch (ExpiredJwtException e) {
      // 액세스 토큰이 만료되었을 경우 새로 발급받아서 줌
      String username = jwtTokenProvider.getUsernameFromExpiredToken(jwt);
      newAccessToken = jwtTokenProvider.generateAccessToken(username);
      dto.setNewAccessToken(newAccessToken);
      token = true;
    } catch (Exception e) {
      token = false;
    }

    if (token) { // 정상적인 토큰이거나, 토큰을 새로 발급했을때에만 좋아요 여부 조회
      // 어차피 인증 성공했기 때문에 만료 버전으로 사용자명 추출
      String username = jwtTokenProvider.getUsernameFromExpiredToken(jwt);
      Member member = memberRepository.findMemberById(username).orElseThrow(
          () -> new MemberNotFoundException("존재하지 않는 사용자입니다.")
      );
      dto.setLikes(likeRepository.
          existsByMemberCodeAndMemeCode(member.getCode(), meme.getOrgMemeCode()));
    }

    dto.setLikesCount(meme.getLikesCount()); // 좋아요 총 갯수 조회

    return dto; // DTO 로 변환 후 return
  }

  @Transactional
  public void uploadMeme(MemeUploadDto uploadDto, CustomUserDetails userDetails,
      MultipartFile thumbnail) {

    Meme meme = Meme.builder()
        .authorCode(userDetails.getMember().getCode())
        .startDate(uploadDto.getStartDate())
        .endDate(uploadDto.getEndDate())
        .contents(uploadDto.getContents())
        .title(uploadDto.getTitle())
        .categoryCode(uploadDto.getCategory())
        .createdAt(LocalDateTime.now())
        .likesCount(0)
        .version(1L)
        .latest(true)
        .build();

    memeRepository.save(meme);
    meme.setOrgMemeCode(meme.getCode()); // 더티 체킹으로 자동 update

    String thumbnailUrl = s3Service.uploadThumbnailFile(thumbnail, meme.getOrgMemeCode());
    meme.setThumbnail(thumbnailUrl); // 썸네일 URL 설정

    Pattern pattern = Pattern.compile("https?://[^\\s)]+\\.(png|jpg|jpeg|gif)");
    Matcher matcher = pattern.matcher(meme.getContents());

    // S3 Copy tmp 파일을 -> 해당 meme 에 대한 이미지 파일로 변경
    while (matcher.find()) {
      String url = matcher.group();
      if (url.contains("/temp/")) {
        // 3️⃣ temp → posts/postId/ 이동 + URL 치환
        String newUrl = s3Service.moveTempToPost(meme.getOrgMemeCode(), url);
        meme.setContents(meme.getContents().replace(url, newUrl));
      }
    }

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
        .orgMemeCode(orgMeme.getOrgMemeCode())
        .version(orgMeme.getVersion() + 1)
        .createdAt(orgMeme.getCreatedAt())
        .updatedAt(LocalDateTime.now())
        .categoryCode(dto.getCategory())
        .authorCode(orgMeme.getAuthorCode())
        .likesCount(orgMeme.getLikesCount())
        .updaterCode(userDetails.getMember().getCode())
        .thumbnail(orgMeme.getThumbnail())
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

    Page<Meme> memePage = memeRepository.findHistory(pageable, memeCode);

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
                  .version(item.getVersion())
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
