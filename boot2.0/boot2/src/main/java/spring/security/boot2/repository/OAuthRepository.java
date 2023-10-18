package spring.security.boot2.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuthRepository extends JpaRepository<OAuth, Long> {
    Optional<OAuth> findByIdentifierAndProvider(final String identifier, final String provider);
}
