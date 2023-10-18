package spring.security.boot2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.security.boot2.domain.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(final String username);
}
