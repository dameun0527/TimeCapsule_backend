package com.example.timecapsule_backend.config.jwt;

public interface JwtVo {

    public String HEADER = "Authorization";
    public String TOKEN_PREFIX = "Bearer ";
    public Long EXPIRED_TIME = 1000 * 60 * 60 * 24 * 7L;
}