package com.InvertisAuditoriumManagement.AudiMgmt.securityconfig;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtHelper {

    private final String SECRET_KEY = "nhfbdvhofuvbuhbdcibuefvbiudbfvknefefnfeubicnfrfvkbgbbjnngnpngfngighbgbugbbudbfuvbuvbdfuybvudfv";
    private final Integer TOKEN_TIME_VALIDITY = 5 * 60 * 60; // 5 hours in seconds

    public String getUserIdFromToken(String token) {
        return getClaims(token, Claims::getSubject);
    }

    public Date getExpirationFromToken(String token) {
        return getClaims(token, Claims::getExpiration);
    }

    public <T> T getClaims(String token, Function<Claims, T> claimResolver) {
        final Claims claims = getAllClaims(token);
        return claimResolver.apply(claims);
    }

    public Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .setAllowedClockSkewSeconds(TOKEN_TIME_VALIDITY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Key getSignKey() {
        byte[] key = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(key);
    }

    public String generateToken(String userId) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_TIME_VALIDITY * 1000))
                .signWith(getSignKey())
                .compact();
    }

    public boolean isExpired(String token) {
        return getExpirationFromToken(token).before(new Date());
    }

    public boolean validate(String token, UserDetails userDetails) {
        String username = getUserIdFromToken(token);
        System.out.println("Token Username: " + username);
        System.out.println("UserDetails Username: " + userDetails.getUsername());

        return username.equals(userDetails.getUsername()) && !isExpired(token);
    }

	
   
}
