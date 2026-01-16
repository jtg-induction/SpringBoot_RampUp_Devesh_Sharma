package com.joshtechnologygroup.minisocial.filter;

import com.joshtechnologygroup.minisocial.service.UserDetailsServiceImpl;
import com.joshtechnologygroup.minisocial.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final HandlerExceptionResolver handlerExceptionResolver;
    JwtUtil jwtUtil;

    private final UserDetailsServiceImpl userDetailsService;

    public JwtFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService, HandlerExceptionResolver handlerExceptionResolver) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        // Extract token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            log.debug("Request missing Authorization header");
            return;
        }

        // Check if token contains correct payload
        String token = authHeader.substring(7);
        String email;
        try {
            email = jwtUtil.extractEmail(token);
        } catch (JwtException e) {
            filterChain.doFilter(request, response);
            log.warn("Failed to parse JWT in Authorization Header: {}", e.getMessage());
            return;
        }

        if (email == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            log.warn("Invalid email or already authenticated");
            filterChain.doFilter(request, response);
            return;
        }

        // Check if user exists
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
            log.info("Successfully authenticated user {}", email);
            filterChain.doFilter(request, response);
        } catch (UsernameNotFoundException ex) {
            log.warn("Can't finx user with extracted email from JWT: {}", email);
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }
    }
}
