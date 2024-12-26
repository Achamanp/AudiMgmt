package com.InvertisAuditoriumManagement.AudiMgmt.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.InvertisAuditoriumManagement.AudiMgmt.entity.EmailUpdateToken;
import com.InvertisAuditoriumManagement.AudiMgmt.entity.User;
@Repository
public interface EmailUpdateTokenRepository extends JpaRepository<EmailUpdateToken, Long> {
    EmailUpdateToken findByUser(User user);
    EmailUpdateToken findByOtp(Integer otp);
    
    @Query("SELECT e FROM EmailUpdateToken e WHERE e.expirationTime < :now")
    List<EmailUpdateToken> findAllExpiredTokens(@Param("now") LocalDateTime now);
    
    void deleteAllByExpirationTimeBefore(LocalDateTime now);
}
