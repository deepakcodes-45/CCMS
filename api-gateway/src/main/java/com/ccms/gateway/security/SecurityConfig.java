package com.ccms.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            JwtService jwtService
    ) {
        JwtAuthenticationWebFilter jwtFilter =
                new JwtAuthenticationWebFilter(jwtService);

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

                .securityContextRepository(
                        NoOpServerSecurityContextRepository.getInstance()
                )

                .authorizeExchange(exchange -> exchange
                        .pathMatchers(
                                HttpMethod.POST,
                                "/api/auth/login"
                        ).permitAll()

                        .pathMatchers(
                                HttpMethod.POST,
                                "/api/auth/register/admin",
                                "/api/auth/register/customer"
                        ).hasRole("ADMIN")

                        .pathMatchers("/api/**").authenticated()

                        .anyExchange().authenticated()
                )

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(
                                (exchange, authException) -> {
                                    exchange.getResponse()
                                            .setStatusCode(
                                                    HttpStatus.UNAUTHORIZED
                                            );

                                    return exchange.getResponse()
                                            .setComplete();
                                }
                        )

                        .accessDeniedHandler(
                                (exchange, deniedException) -> {
                                    exchange.getResponse()
                                            .setStatusCode(
                                                    HttpStatus.FORBIDDEN
                                            );

                                    return exchange.getResponse()
                                            .setComplete();
                                }
                        )
                )

                .addFilterAt(
                        jwtFilter,
                        SecurityWebFiltersOrder.AUTHENTICATION
                )

                .build();
    }
}