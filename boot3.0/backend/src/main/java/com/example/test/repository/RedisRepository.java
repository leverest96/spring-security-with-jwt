package com.example.test.repository;

import com.example.test.domain.redis.AccessToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisRepository extends CrudRepository<AccessToken, String> {
}