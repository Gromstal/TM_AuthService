package org.example.taskmanager_authservice.repository;

import org.example.taskmanager_authservice.entity.VerificationMailToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VerificationMailTokenRepository extends JpaRepository<VerificationMailToken,Long> {
    Optional<VerificationMailToken> findByToken(String token);

    @Query ("update VerificationMailToken v set v.isUsed=true where v.email=:email")
    @Modifying
    void updateIsUsed(@Param ("email") String email);

}
