package com.ccms.customercard.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtService jwtService
    ) throws Exception {

        JwtAuthenticationFilter jwtFilter =
                new JwtAuthenticationFilter(jwtService);

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorize -> authorize

                        // Admin-only management actions
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/customers/**",
                                "/api/cards/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/customers/**",
                                "/api/cards/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/customers/**",
                                "/api/cards/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/customers/**",
                                "/api/cards/**"
                        ).hasRole("ADMIN")

                        // Admin-only list-all endpoints
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/customers",
                                "/api/cards"
                        ).hasRole("ADMIN")

                        // Admin or Customer; controller checks ownership
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/customers/*",
                                "/api/cards/customer/*"
                        ).hasAnyRole("ADMIN", "CUSTOMER")

                        // Card-by-number stays admin-only for now
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/cards/*"
                        ).hasAnyRole("ADMIN", "CUSTOMER")

                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, ex) ->
                                response.setStatus(401)
                        )
                        .accessDeniedHandler((request, response, ex) ->
                                response.setStatus(403)
                        )
                )
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .build();
    }
}