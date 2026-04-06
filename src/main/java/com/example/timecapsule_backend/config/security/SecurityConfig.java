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

    public static final String[] PUBLIC_URLS = {
            "/api/auth/**",
            "/actuator/health",
            "/actuator/prometheus",
            "/api/email/performance-test/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api-docs/**",
            "/v3/api-docs/**",
            "/webjars/**"
    };

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                                           JwtUtil jwtUtil,
                                                           ObjectMapper objectMapper,
                                                           SecurityResponseHandler securityResponseHandler) {
        return new JwtAuthenticationFilter(authenticationManager, jwtUtil, objectMapper, securityResponseHandler);
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter(JwtUtil jwtUtil) {
        return new JwtAuthorizationFilter(jwtUtil);
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
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(configurationSource()))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .addFilter(jwtAuthenticationFilter(authenticationManager, jwtUtil, objectMapper, securityResponseHandler()))
                .addFilterAfter(jwtAuthorizationFilter(jwtUtil), JwtAuthenticationFilter.class)
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
        configuration.addAllowedMethod("*");
        configuration.addAllowedOriginPattern("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
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
