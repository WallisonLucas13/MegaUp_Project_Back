package com.example.MegaUp_Server.security.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationProvider provider;

    private final com.example.MegaUp_Server.security.configs.JwtAuthenticationFilter filter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf()
                .disable()
                .cors()
                .and()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.POST, "/api/login")
                .permitAll()
                .requestMatchers(HttpMethod.POST, "/api/login/access")
                .permitAll()
                .requestMatchers(HttpMethod.POST, "/api/register")
                .hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/users")
                .hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/delete/**")
                .hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/user/access")
                .hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/Clientes/New")
                .hasRole("USER")
                .requestMatchers(HttpMethod.PUT, "/Clientes/Edit")
                .hasRole("USER")
                .requestMatchers(HttpMethod.DELETE, "/Clientes/Delete")
                .hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/Servicos/New")
                .hasRole("USER")
                .requestMatchers(HttpMethod.PUT, "/Servicos/Edit")
                .hasRole("USER")
                .requestMatchers(HttpMethod.DELETE, "/Servicos/Delete")
                .hasRole("USER")
                .requestMatchers(HttpMethod.PUT, "/Servicos/MaoDeObra")
                .hasRole("USER")
                .requestMatchers(HttpMethod.PUT, "/Servicos/Desconto")
                .hasRole("USER")
                .requestMatchers(HttpMethod.PUT, "/Servicos/Entrada/**")
                .hasRole("USER")
                .requestMatchers(HttpMethod.PUT, "/Servicos/PagamentoFinal/**")
                .hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/Servicos/Orcamento")
                .hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/Servicos/Etapa")
                .hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/Material/New")
                .hasRole("USER")
                .requestMatchers(HttpMethod.PUT, "/Material/Edit")
                .hasRole("USER")
                .requestMatchers(HttpMethod.DELETE, "/Material/Delete")
                .hasRole("USER")
                .anyRequest()
                .authenticated()
                .and()
                .exceptionHandling()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(provider)
                .addFilterAfter(filter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
