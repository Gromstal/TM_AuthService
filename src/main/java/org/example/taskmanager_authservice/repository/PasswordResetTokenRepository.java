package org.example.taskmanager_authservice.repository;

import org.example.taskmanager_authservice.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken,Long> {
    Optional<PasswordResetToken> findByToken(String token);

    @Query ("update PasswordResetToken v set v.isUsed=true where v.email=:email")
    @Modifying
    void updateIsUsed(@Param ("email") String email);

}
