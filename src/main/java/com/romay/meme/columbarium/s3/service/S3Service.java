package com.romay.meme.columbarium.s3.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

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

  @PostConstruct
  public void init() {
    BasicAWSCredentials creds = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
    s3client = AmazonS3ClientBuilder.standard()
        .withRegion(Regions.fromName(awsRegion))
        .withCredentials(new AWSStaticCredentialsProvider(creds))
        .build();
  }

  /**
   * temp 업로드
   */
  public String uploadTempFile(MultipartFile file) {
    File convertFile = convertMultiPartToFile(file);
    String fileName = "temp/" + generateFileName(file);
    s3client.putObject(new PutObjectRequest(bucketName, fileName, convertFile)
        .withCannedAcl(CannedAccessControlList.PublicRead));
    convertFile.delete();
    log.info("Temp Upload Success : " + file.getOriginalFilename());
    return s3client.getUrl(bucketName, fileName).toString();
  }

  /**
   * thumbnail 업로드
   */
  public String uploadThumbnailFile(MultipartFile file, Long memeCode) {
    File convertFile = convertMultiPartToFile(file);
    String fileName = "thumbnail/" + memeCode + "/" + generateFileName(file);
    s3client.putObject(new PutObjectRequest(bucketName, fileName, convertFile)
        .withCannedAcl(CannedAccessControlList.PublicRead));
    convertFile.delete();
    log.info("Temp thumbnail Success : " + file.getOriginalFilename());
    return s3client.getUrl(bucketName, fileName).toString();
  }

  /**
   * S3 객체 삭제
   */
  public void deleteFile(String fileKey) {
    s3client.deleteObject(new DeleteObjectRequest(bucketName, fileKey));
    log.info("Deleted S3 Object : " + fileKey);
  }

  /**
   * S3 copy (임시 → 정식 경로)
   */
  public String moveTempToPost(Long postId, String tempUrl) {
    String tempKey = tempUrl.substring(tempUrl.indexOf("/temp/") + 1); // temp/uuid.png
    String postKey = "posts/" + postId + "/" + tempKey.substring(tempKey.indexOf("/") + 1);

    // Copy
    CopyObjectRequest copyReq = new CopyObjectRequest(bucketName, tempKey, bucketName, postKey);
    s3client.copyObject(copyReq);

    // Delete original temp
    deleteFile(tempKey);

    String postUrl = s3client.getUrl(bucketName, postKey).toString();
    log.info("Moved temp {} → post {} ", tempUrl, postUrl);
    return postUrl;
  }

  private File convertMultiPartToFile(MultipartFile file) {
    File convFile = new File(
        System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
    try (FileOutputStream fos = new FileOutputStream(convFile)) {
      fos.write(file.getBytes());
    } catch (IOException e) {
      throw new RuntimeException("파일 변환 실패", e);
    }
    return convFile;
  }

  private String generateFileName(MultipartFile file) {
    return UUID.randomUUID().toString() + getFileExtension(file.getOriginalFilename());
  }

  private String getFileExtension(String filename) {
    int dotIndex = filename.lastIndexOf(".");
    if (dotIndex != -1) {
      return filename.substring(dotIndex);
    }
    return "";
  }
}
