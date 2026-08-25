package com.schoolmanagment.userservice.user.repository;

import com.schoolmanagment.userservice.user.entity.PasswordResetToken;
import com.schoolmanagment.userservice.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    void deleteByUser(User user);

//    void deleteByExpiryDateBefore(LocalDateTime now);
}
