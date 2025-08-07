package com.umc.tomorrow.global.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    // 키와 값을 Redis에 저장하고 만료 시간 설정
    public void set(String key, String value, Duration expirationTime) {
        redisTemplate.opsForValue().set(key, value, expirationTime);
    }

    // 키로 값을 조회
    public String get(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return value == null ? null : (String) value;
    }

    // 키를 삭제
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}