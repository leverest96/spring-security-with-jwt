package com.example.test.service;

import com.example.test.domain.redis.AccessToken;
import com.example.test.repository.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisRepository redisRepository;

    @Transactional
    public void saveTokenInfo(long employeeId, String refreshToken, long memberId) {
        redisRepository.save(new AccessToken(employeeId, refreshToken, memberId));
    }

    @Transactional
    public void removeRefreshToken(long memberId) {
        redisRepository.findByMemberId(memberId).ifPresent(redisRepository::delete);
    }
}
