package com.maloney.auth_service.repository;

import com.maloney.auth_service.entity.Token;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query(value = "SELECT * FROM tokens WHERE user_id = :userId AND revoked = false AND expired = false", nativeQuery = true)
    List<Token> findAllValidTokensByUser(@Param("userId") Long userId);

    Optional<Token> findByToken(String token);
}