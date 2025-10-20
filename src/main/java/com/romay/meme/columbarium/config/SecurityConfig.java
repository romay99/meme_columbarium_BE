package com.romay.meme.columbarium.config;

import com.romay.meme.columbarium.filter.JwtAuthenticationFilter;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // 허용할 Origin 추가 (로컬 + Vercel)
    configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:3000",
            "https://meme-columbarium-fe.vercel.app"
    ));

    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }


  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
            // CORS 적용
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // CSRF 비활성화 (API 서버용)
            .csrf(csrf -> csrf.disable())
            // JWT 사용 시 세션 상태 무시
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 인증/권한 설정
            .authorizeHttpRequests(authz -> authz
                    .requestMatchers(
                            "/meme/categories",
                            "/meme/info",
                            "/meme/list",
                            "/member/login",
                            "/member/signup",
                            "/member/check-id/**",   // <--- 여기 수정됨
                            "/comment/meme/list",
                            "/board/list",
                            "/board/info",
                            "/comment/board/list",
                            "/meme/history",
                            "/member/refresh"
                    ).permitAll()
                    .anyRequest().authenticated()
            )
            // JWT 필터 적용
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            // OPTIONS 요청 허용
            .httpBasic().disable();

    return http.build();
  }
}