package com.example.test.repository;

import com.example.test.domain.Member;
import com.example.test.domain.enums.LoginType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmailAndLoginType(String email, LoginType loginType);
    Optional<Member> findByLoginId(String loginId);
}
