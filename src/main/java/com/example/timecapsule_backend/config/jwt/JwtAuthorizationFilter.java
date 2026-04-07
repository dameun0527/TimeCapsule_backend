package com.example.timecapsule_backend.config.jwt;

import com.example.timecapsule_backend.config.security.SecurityConfig;
import com.example.timecapsule_backend.config.security.loginUser.LoginUser;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String servletPath = request.getServletPath();

        boolean isPublic = Arrays.stream(SecurityConfig.PUBLIC_URLS)
                .anyMatch(pattern -> pathMatcher.match(pattern, uri) || pathMatcher.match(pattern, servletPath));
        log.info("인증 제외 체크 - URI: {}, ServletPath: {}, 결과: {}", uri, servletPath, isPublic);
        return isPublic;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            if (!isHeaderValid(request)) {
                throw new JwtException("Authorization 헤더가 누락되었습니다.");
            }

            String token = jwtUtil.substringToken(request.getHeader(JwtVo.HEADER));
            LoginUser loginUser = jwtUtil.validateToken(token);

            Authentication authentication = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("유저 인증 완료 - userId : {}, role : {}", loginUser.getUser().getId(), loginUser.getUser().getRole());

            chain.doFilter(request, response);
        } catch (JwtException e) {
            throw e;
        } catch (Exception e) {
            log.error("인가 중 서버 및 내부 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    private boolean isHeaderValid(HttpServletRequest request) {
        String header = request.getHeader(JwtVo.HEADER);
        return header != null && header.startsWith(JwtVo.TOKEN_PREFIX);
    }
}
