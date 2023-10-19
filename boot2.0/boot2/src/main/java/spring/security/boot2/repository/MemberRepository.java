package spring.security.boot2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.security.boot2.models.users.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByNickname(final String nickname);
    Optional<Member> findById(final long id);
}
