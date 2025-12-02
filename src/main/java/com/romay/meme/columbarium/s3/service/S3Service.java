package com.romay.meme.columbarium.s3.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class S3Service {

  private AmazonS3 s3client;

  @Value("${aws.accessKey}")
  private String awsAccessKey;

  @Value("${aws.secretKey}")
  private String awsSecretKey;

  @Value("${aws.region}")
  private String awsRegion;

  @Value("${aws.s3.bucketName}")
  private String bucketName;

  private static final Set<String> ALLOWED_EXT = Set.of(".png", ".jpg", ".jpeg", ".gif");

  private static final Map<String, byte[]> MAGIC_NUMBERS = Map.of(
          "png", new byte[]{(byte)0x89, 0x50, 0x4E, 0x47},
          "jpg", new byte[]{(byte)0xFF, (byte)0xD8, (byte)0xFF},
          "jpeg", new byte[]{(byte)0xFF, (byte)0xD8, (byte)0xFF},
          "gif", "GIF89a".getBytes()
  );

  @PostConstruct
  public void init() {
    BasicAWSCredentials creds = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
    s3client = AmazonS3ClientBuilder.standard()
            .withRegion(Regions.fromName(awsRegion))
            .withCredentials(new AWSStaticCredentialsProvider(creds))
            .build();
  }

  // ==============================
  //  temp 파일 업로드
  // ==============================
  public String uploadTempFile(MultipartFile file) {

    validateImageFile(file);

    File convertFile = convertMultiPartToFile(file);
    String fileName = "temp/" + generateSafeFileName(file);

    // PublicRead 제거 → 보안 강화
    PutObjectRequest request = new PutObjectRequest(bucketName, fileName, convertFile)
            .withCannedAcl(CannedAccessControlList.Private);

    s3client.putObject(request);
    convertFile.delete();

    log.info("Temp Upload Success : {}", file.getOriginalFilename());
    return s3client.getUrl(bucketName, fileName).toString();
  }

  // ==============================
  //  thumbnail 업로드
  // ==============================
  public String uploadThumbnailFile(MultipartFile file, Long memeCode) {

    validateImageFile(file);

    File convertFile = convertMultiPartToFile(file);
    String fileName = "thumbnail/" + memeCode + "/" + generateSafeFileName(file);

    // PublicRead 제거 → Private로 업로드
    PutObjectRequest request = new PutObjectRequest(bucketName, fileName, convertFile)
            .withCannedAcl(CannedAccessControlList.Private);

    s3client.putObject(request);
    convertFile.delete();

    log.info("Thumbnail Upload Success : {}", file.getOriginalFilename());
    return s3client.getUrl(bucketName, fileName).toString();
  }

  // ==============================
  //  파일 삭제
  // ==============================
  public void deleteFile(String fileKey) {
    s3client.deleteObject(new DeleteObjectRequest(bucketName, fileKey));
    log.info("Deleted S3 Object : {}", fileKey);
  }

  // ==============================
  //  temp → post 이동
  // ==============================
  public String moveTempToPost(Long postId, String tempUrl) {

    String tempKey = extractKeyFromUrl(tempUrl);

    if (!tempKey.startsWith("temp/")) {
      throw new IllegalArgumentException("Temp 파일이 아님");
    }

    String fileName = tempKey.substring(tempKey.lastIndexOf("/") + 1);
    String postKey = "posts/" + postId + "/" + fileName;

    CopyObjectRequest copyReq = new CopyObjectRequest(bucketName, tempKey, bucketName, postKey);
    s3client.copyObject(copyReq);

    deleteFile(tempKey);

    String postUrl = s3client.getUrl(bucketName, postKey).toString();
    log.info("Moved temp {} → post {}", tempUrl, postUrl);

    return postUrl;
  }

  // ==============================
  //   내부 유틸
  // ==============================

  private void validateImageFile(MultipartFile file) {

    if (file == null || file.isEmpty()) {
      throw new IllegalArgumentException("업로드한 파일이 없습니다.");
    }

    String ext = getFileExtension(file.getOriginalFilename()).toLowerCase();
    if (!ALLOWED_EXT.contains(ext)) {
      throw new IllegalArgumentException("지원하지 않는 이미지 확장자입니다.");
    }

    validateMagicNumber(file);

    if (file.getSize() > 5 * 1024 * 1024) {
      throw new IllegalArgumentException("이미지 파일 크기는 최대 5MB까지 가능합니다.");
    }
  }

  private void validateMagicNumber(MultipartFile file) {
    try {
      byte[] bytes = file.getBytes();

      boolean match = false;

      for (byte[] magic : MAGIC_NUMBERS.values()) {
        if (bytes.length < magic.length) continue;

        boolean isMatch = true;
        for (int i = 0; i < magic.length; i++) {
          if ((bytes[i] & 0xFF) != (magic[i] & 0xFF)) {
            isMatch = false;
            break;
          }
        }

        if (isMatch) {
          match = true;
          break;
        }
      }

      if (!match) {
        throw new IllegalArgumentException("파일 헤더가 정상 이미지가 아닙니다.");
      }

    } catch (IOException e) {
      throw new IllegalArgumentException("파일 검증 실패", e);
    }
  }

  private String extractKeyFromUrl(String url) {
    int idx = url.indexOf(".com/");
    if (idx == -1) {
      throw new IllegalArgumentException("URL 파싱 실패");
    }
    return url.substring(idx + 5);
  }

  private File convertMultiPartToFile(MultipartFile file) {
    File convFile = new File(
            System.getProperty("java.io.tmpdir") + "/" + UUID.randomUUID()
    );
    try (FileOutputStream fos = new FileOutputStream(convFile)) {
      fos.write(file.getBytes());
    } catch (IOException e) {
      throw new RuntimeException("파일 변환 실패", e);
    }
    return convFile;
  }

  private String generateSafeFileName(MultipartFile file) {
    return UUID.randomUUID() + getFileExtension(file.getOriginalFilename());
  }

  private String getFileExtension(String filename) {
    int dotIndex = filename.lastIndexOf(".");
    if (dotIndex != -1) {
      return filename.substring(dotIndex).toLowerCase();
    }
    return "";
  }
}
