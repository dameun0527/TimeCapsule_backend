package com.example.timecapsule_backend.config.security;

import com.example.timecapsule_backend.config.jwt.JwtAuthenticationFilter;
import com.example.timecapsule_backend.config.jwt.JwtAuthorizationFilter;
import com.example.timecapsule_backend.config.jwt.JwtExceptionFilter;
import com.example.timecapsule_backend.config.jwt.JwtUtil;
import com.example.timecapsule_backend.config.security.handler.CustomAccessDeniedHandler;
import com.example.timecapsule_backend.config.security.handler.CustomAuthenticationEntryPoint;
import com.example.timecapsule_backend.config.security.handler.SecurityResponseHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                                           JwtUtil jwtUtil,
                                                           ObjectMapper objectMapper,
                                                           SecurityResponseHandler securityResponseHandler) {
        return new JwtAuthenticationFilter(authenticationManager, jwtUtil, objectMapper, securityResponseHandler);
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter(AuthenticationManager authenticationManager,
                                                         JwtUtil jwtUtil) {
        return new JwtAuthorizationFilter(authenticationManager, jwtUtil);
    }

    @Bean
    public JwtExceptionFilter jwtExceptionFilter(SecurityResponseHandler securityResponseHandler) {
        return new JwtExceptionFilter(securityResponseHandler);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager)
            throws Exception {
        log.debug("Security FilterChain 등록");
        http
                .headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .csrf(AbstractHttpConfigurer::disable) // csf 비활성화
                .cors(cors -> cors.configurationSource(configurationSource()))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable) // formLogin 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) // 브라우저 팝업창 사용자 인증 진행 비활성화
                .addFilter(jwtAuthenticationFilter(authenticationManager, jwtUtil, objectMapper, securityResponseHandler()))
                .addFilter(jwtAuthorizationFilter(authenticationManager, jwtUtil))
                .addFilterBefore(jwtExceptionFilter(securityResponseHandler()), JwtAuthorizationFilter.class)
                .exceptionHandling(handler -> handler
                        .authenticationEntryPoint(customAuthenticationEntryPoint())
                        .accessDeniedHandler(customAccessDeniedHandler()));
        return http.build();


    }

    public CorsConfigurationSource configurationSource() {
        log.debug("filterChain에 configurationSource cors 설정 등록");

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*"); // 모든 메서드 허용
        configuration.addAllowedOriginPattern("*"); // 모든 주소 허용
        configuration.setAllowCredentials(true); // 쿠키 요청 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 주소에 대해 cors 정책 설정
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityResponseHandler securityResponseHandler() {
        return new SecurityResponseHandler(objectMapper);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CustomAccessDeniedHandler customAccessDeniedHandler() {
        return new CustomAccessDeniedHandler(securityResponseHandler());
    }

    @Bean
    public CustomAuthenticationEntryPoint customAuthenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint(securityResponseHandler());
    }
}
