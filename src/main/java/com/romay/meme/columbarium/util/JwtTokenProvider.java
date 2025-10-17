package com.romay.meme.columbarium.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  @Value("${jwt.secret:121fqfqfqwiofukjzxjfhfhukajqfhqjwkfhjkzxfqqfzxfwqfzxfzfafqf11111111}")
  private String jwtSecret;

  @Value("${jwt.access-token-expiration:900000}")
  private long accessTokenExpiration; // 15분 (900000ms)

  @Value("${jwt.refresh-token-expiration:604800000}")
  private long refreshTokenExpiration; // 7일 (604800000ms)

  // Secret Key 생성
  private Key getSigningKey() {
    byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  // Access Token 생성
  public String generateAccessToken(String username) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + accessTokenExpiration);

    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .claim("tokenType", "ACCESS")
        .signWith(getSigningKey(), SignatureAlgorithm.HS512)
        .compact();
  }

  // Refresh Token 생성
  public String generateRefreshToken(String username) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + refreshTokenExpiration);

    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .claim("tokenType", "REFRESH")
        .signWith(getSigningKey(), SignatureAlgorithm.HS512)
        .compact();
  }

  // 토큰에서 사용자명 추출
  public String getUsernameFromToken(String token) {
    Claims claims = Jwts.parser()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();

    return claims.getSubject();
  }

  // 토큰 유효성 검증
  public boolean validateToken(String token) {
    try {
      Jwts.parser()
          .setSigningKey(getSigningKey())
          .build()
          .parseClaimsJws(token);
      return true;
    } catch (ExpiredJwtException e) {
      throw e; // 필터에서 처리하도록 다시 던짐
    } catch (Exception e) {
      System.out.println("유효하지 않은 토큰: " + e.getMessage());
      return false;
    }
  }

  // 토큰 만료 여부 확인
  public boolean isTokenExpired(String token) {
    try {
      Claims claims = Jwts.parser()
          .setSigningKey(getSigningKey())
          .build()
          .parseClaimsJws(token)
          .getBody();
      return claims.getExpiration().before(new Date());
    } catch (ExpiredJwtException e) {
      return true;
    }
  }

  // 토큰에서 만료 시간 가져오기
  public Date getExpirationFromToken(String token) {
    Claims claims = Jwts.parser()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
    return claims.getExpiration();
  }
}