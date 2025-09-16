package com.romay.meme.columbarium.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JWTUtil {

  @Value("${jwt.secret:mySecretKeyForJwtTokenGenerationThatIsVeryLongAndSecure123456789}")
  private String secretKey;

  @Value("${jwt.expiration:86400000}") // 24시간
  private long expiration;

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(secretKey.getBytes());
  }

  // JWT 토큰 생성
  public String generateToken(String username, List<String> roles) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + expiration);

    return Jwts.builder()
        .setSubject(username)
        .claim("roles", roles)
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  // JWT 토큰에서 사용자명 추출
  public String getUsernameFromToken(String token) {
    return getClaimsFromToken(token).getSubject();
  }

  // JWT 토큰에서 권한 추출
  @SuppressWarnings("unchecked")
  public List<String> getRolesFromToken(String token) {
    return (List<String>) getClaimsFromToken(token).get("roles");
  }

  // JWT 토큰에서 만료일 추출
  public Date getExpirationDateFromToken(String token) {
    return getClaimsFromToken(token).getExpiration();
  }

  // JWT 토큰에서 클레임 추출
  private Claims getClaimsFromToken(String token) {
    return Jwts.parser()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  // JWT 토큰 만료 여부 확인
  public boolean isTokenExpired(String token) {
    Date expiration = getExpirationDateFromToken(token);
    return expiration.before(new Date());
  }

  // JWT 토큰 유효성 검증
  public boolean validateToken(String token, String username) {
    String tokenUsername = getUsernameFromToken(token);
    return (tokenUsername.equals(username) && !isTokenExpired(token));
  }
}