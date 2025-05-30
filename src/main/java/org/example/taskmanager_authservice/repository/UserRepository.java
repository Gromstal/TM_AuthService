package org.example.taskmanager_authservice.repository;

import org.example.taskmanager_authservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    @Query("update User u set u.password=:password where u.email=:email")
    @Modifying
    void updatePasswordByEmail(@Param("email") String email, @Param("password") String password);

    @Query("update User u set u.password=:password where u.username=:username")
    @Modifying
    void updatePasswordByUsername(@Param("username") String username, @Param("password") String password);

    @Query("update User u set u.isVerified=true where u.email=:email")
    @Modifying
    void setIsVerified(@Param("email") String email);
}
