package com.InvertisAuditoriumManagement.AudiMgmt.otpconfig;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.InvertisAuditoriumManagement.AudiMgmt.entity.EmailUpdateToken;
import com.InvertisAuditoriumManagement.AudiMgmt.entity.PasswordResetToken;
import com.InvertisAuditoriumManagement.AudiMgmt.repository.EmailUpdateTokenRepository;
import com.InvertisAuditoriumManagement.AudiMgmt.repository.PasswordResetTokenRepository;

@Component
public class OtpCleanupScheduler {

    private final EmailUpdateTokenRepository emailUpdateTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public OtpCleanupScheduler(EmailUpdateTokenRepository emailUpdateTokenRepository,PasswordResetTokenRepository passwordResetTokenRepository) {
    	this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailUpdateTokenRepository = emailUpdateTokenRepository;
    }

    @Scheduled(fixedRate = 60000) 
    public void deleteExpiredOtps() {
        LocalDateTime now = LocalDateTime.now();
        List<EmailUpdateToken> expiredTokens = emailUpdateTokenRepository.findAllExpiredTokens(now);
        
        if (!expiredTokens.isEmpty()) {
            emailUpdateTokenRepository.deleteAll(expiredTokens);
            System.out.println("Deleted expired OTP tokens at: " + now);
        }
    }
    @Scheduled(fixedRate = 60000) 
    public void deleteExpiredOtpsForForgottedPassword() {
    	LocalDateTime now = LocalDateTime.now();
    	List<PasswordResetToken> expiredToken = this.passwordResetTokenRepository.findAllExpiredTokens(now);
    	if(!expiredToken.isEmpty()) {
    		this.passwordResetTokenRepository.deleteAll(expiredToken);
    		System.out.println("Deleted expired OTP tokens at: " + now);
    	}
    	
    }
}
