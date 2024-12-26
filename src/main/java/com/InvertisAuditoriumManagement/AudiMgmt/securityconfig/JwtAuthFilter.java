package com.InvertisAuditoriumManagement.AudiMgmt.securityconfig;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.InvertisAuditoriumManagement.AudiMgmt.serviceimpl.CustomUserService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtHelper jwtHelper;
    private final CustomUserService customUserService;

    public JwtAuthFilter(JwtHelper jwtHelper, CustomUserService customUserService) {
        this.jwtHelper = jwtHelper;
        this.customUserService = customUserService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwtHeader = request.getHeader("Authorization");
        String userId = null;
        String token = null;

        try {
            if (jwtHeader != null && jwtHeader.startsWith("Bearer ")) {
                token = jwtHeader.substring(7);
                userId = jwtHelper.getUserIdFromToken(token);

                if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = customUserService.loadUserByUsername(userId);

                    if (jwtHelper.validate(token, userDetails)) {
                    	UsernamePasswordAuthenticationToken authenticationToken =
                    	        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    	authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    	SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            }
        } catch (MalformedJwtException e) {
            System.err.println("Invalid JWT Token: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        } catch (ExpiredJwtException e) {
            System.err.println("Expired JWT Token: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        } catch (SignatureException e) {
            System.err.println("JWT Token signature validation failed: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        } catch (IllegalArgumentException e) {
            System.err.println("Illegal JWT Token: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        } catch (UsernameNotFoundException e) {
            System.err.println("User not found: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        } catch (JwtException e) {
            System.err.println("JWT Token error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
