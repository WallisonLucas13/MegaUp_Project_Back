package com.example.MegaUp_Server.security.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationProvider provider;
    private final JwtAuthenticationFilter filter;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(auth -> auth
                        // Autenticação pública
                        .requestMatchers(HttpMethod.POST, "/api/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/user/access").permitAll()
                        // Endpoints administrativos
                        .requestMatchers(HttpMethod.POST, "/api/register").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
                        // Operações de escrita exigem ROLE_USER
                        .requestMatchers(HttpMethod.POST, "/clientes").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/clientes/**").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/clientes/**").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/servicos/**").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/servicos/**").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/servicos/**").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/materiais/**").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/materiais/**").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/materiais/**").hasRole("USER")
                        // Operações de leitura exigem USER ou READ
                        .requestMatchers(HttpMethod.GET, "/clientes").hasAnyRole("USER", "READ")
                        .requestMatchers(HttpMethod.GET, "/servicos/**").hasAnyRole("USER", "READ")
                        .requestMatchers(HttpMethod.GET, "/materiais/**").hasAnyRole("USER", "READ")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(
                        (req, res, e) -> res.sendError(401, "Unauthorized")))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(provider)
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
