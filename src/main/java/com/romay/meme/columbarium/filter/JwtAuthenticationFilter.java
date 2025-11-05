package com.romay.meme.columbarium.filter;

import com.romay.meme.columbarium.member.service.CustomUserDetailsService;
import com.romay.meme.columbarium.util.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider tokenProvider;
  private final CustomUserDetailsService customUserDetailsService;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    try {
      // 1. Authorization 헤더에서 JWT 토큰 추출
      String jwt = tokenProvider.getJwtFromRequest(request);

      // 2. 토큰이 있고 유효한 경우
      if (jwt != null && tokenProvider.validateToken(jwt)) {
        String username = tokenProvider.getUsernameFromToken(jwt);

        // Spring Security Context에 인증 정보 저장
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 다음 필터로 진행
        filterChain.doFilter(request, response);
      } else {
        // 3. 토큰이 없거나 유효하지 않으면 401 반환
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\": \"인증이 필요합니다.\"}");
        return; // 필터 체인 중단
      }

    } catch (ExpiredJwtException e) {
      // 4. 토큰 만료 시 401 반환
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/json;charset=UTF-8");
      response.getWriter().write("{\"error\": \"토큰이 만료되었습니다.\", \"code\": \"TOKEN_EXPIRED\"}");
      return;
    } catch (Exception e) {
      // 5. 기타 예외
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/json;charset=UTF-8");
      response.getWriter().write("{\"error\": \"유효하지 않은 토큰입니다.\"}");
      return;
    }
  }


  // JwtAuthenticationFilter 내부
  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();

    // permitAll 경로 모두 추가
    return path.startsWith("/api/auth/login") ||
        path.startsWith("/api/auth/refresh") ||
        path.startsWith("/api/public/") ||

        path.startsWith("/meme/categories") ||
        path.startsWith("/meme/info") ||
        path.startsWith("/meme/list") ||
        path.startsWith("/member/login") ||
        path.startsWith("/member/signup") ||
        path.startsWith("/member/check-id") ||
        path.startsWith("/comment/meme/list") ||
        path.startsWith("/board/list") ||
        path.startsWith("/board/info") ||
        path.startsWith("/comment/board/list") ||
        path.startsWith("/meme/history") ||
        path.startsWith("/member/refresh") ||
        path.startsWith("/member/check-nick-name") ||
        path.startsWith("/member/logout");
  }
}