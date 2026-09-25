package com.ccms.transactionreport.security;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader =
                request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7).trim();

        if (token.isEmpty()) {
            sendUnauthorized(response);
            return;
        }

        try {
            Claims claims = jwtService.getClaims(token);

            String username = claims.getSubject();
            String roleValue = claims.get("role", String.class);
            Long userId = getLongClaim(claims, "userId");
            Long customerId = getLongClaim(claims, "customerId");

            if (username == null || username.isBlank()
                    || roleValue == null
                    || userId == null
                    || claims.getExpiration() == null) {
                sendUnauthorized(response);
                return;
            }

            RoleName role = RoleName.valueOf(roleValue);

            if (role == RoleName.CUSTOMER && customerId == null) {
                sendUnauthorized(response);
                return;
            }

            JwtUserPrincipal principal = new JwtUserPrincipal(
                    userId,
                    username,
                    role,
                    customerId
            );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            List.of(new SimpleGrantedAuthority(
                                    "ROLE_" + role.name()
                            ))
                    );

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (JwtException | IllegalArgumentException exception) {
            SecurityContextHolder.clearContext();
            sendUnauthorized(response);
        }
    }

    private Long getLongClaim(Claims claims, String claimName) {
        Object value = claims.get(claimName);

        if (value instanceof Number number) {
            return number.longValue();
        }

        return null;
    }

    private void sendUnauthorized(HttpServletResponse response)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}