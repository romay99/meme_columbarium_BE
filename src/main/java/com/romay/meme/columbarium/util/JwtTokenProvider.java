package com.romay.meme.columbarium.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
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

  // Authorization 헤더에서 Bearer 토큰 추출
  public String getJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
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

  //만료된 토큰에서 사용자명 추출
  public String getUsernameFromExpiredToken(String token) {
    try {
      Claims claims = Jwts.parser()
          .setSigningKey(getSigningKey())
          .build()
          .parseClaimsJws(token)
          .getBody();
      return claims.getSubject(); // 정상 토큰이면 여기서 반환
    } catch (ExpiredJwtException e) {
      // 만료 토큰일 경우에도 Claims 접근 가능
      Claims claims = e.getClaims();
      return claims.getSubject();
    } catch (JwtException e) {
      // 변조되거나 잘못된 토큰
      return null;
    }
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
      return false;
    }
  }

  // 토큰 유효성 검증 (Only meme/getinfo)
  public boolean validateTokenForGetMemeInfo(String token) {
    try {
      Jwts.parser()
          .setSigningKey(getSigningKey())
          .build()
          .parseClaimsJws(token);
      return true;
    } catch (ExpiredJwtException e) {
      throw e;
    } catch (Exception e) {
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