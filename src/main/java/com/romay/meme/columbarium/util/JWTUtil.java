package com.romay.meme.columbarium.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
public class JWTUtil {

  @Value("${jwt.secret:mySecretKeyForJwtTokenGenerationThatIsVeryLongAndSecure123456789}")
  private String secretKey;

  @Value("${jwt.accessExpiration:3600000}") // 1시간
  private long accessExpiration;

  @Value("${jwt.refreshExpiration:604800000}") // 7일
  private long refreshExpiration;

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(secretKey.getBytes());
  }

  // ==================== 액세스 토큰 ====================
  public String generateAccessToken(String username, List<String> roles) {
    return generateToken(username, roles, accessExpiration);
  }

  // ==================== 리프래시 토큰 ====================
  public String generateRefreshToken(String username) {
    // 리프래시 토큰은 보통 권한 정보 없이 username만 포함
    return generateToken(username, null, refreshExpiration);
  }

  // ==================== 공통 토큰 생성 ====================
  private String generateToken(String username, List<String> roles, long expirationMillis) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + expirationMillis);

    var builder = Jwts.builder()
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(getSigningKey(), SignatureAlgorithm.HS256);

    if (roles != null) {
      builder.claim("roles", roles);
    }

    return builder.compact();
  }

  // ==================== 기존 메서드 재사용 ====================
  public String getUsernameFromToken(String token) {
    return getClaimsFromToken(token).getSubject();
  }

  @SuppressWarnings("unchecked")
  public List<String> getRolesFromToken(String token) {
    return (List<String>) getClaimsFromToken(token).get("roles");
  }

  public Date getExpirationDateFromToken(String token) {
    return getClaimsFromToken(token).getExpiration();
  }

  private Claims getClaimsFromToken(String token) {
    return Jwts.parser()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
  }

  public boolean isTokenExpired(String token) {
    return getExpirationDateFromToken(token).before(new Date());
  }

  public boolean validateToken(String token, String username) {
    return getUsernameFromToken(token).equals(username) && !isTokenExpired(token);
  }
}
