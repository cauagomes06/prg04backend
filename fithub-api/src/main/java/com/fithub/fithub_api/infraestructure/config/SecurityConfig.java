package com.fithub.fithub_api.infraestructure.config;

import com.fithub.fithub_api.infraestructure.jwt.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration; // 1. IMPORTE
import org.springframework.web.cors.CorsConfigurationSource; // 2. IMPORTE
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // 3. IMPORTE
import java.util.Arrays; 
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize

                        // Endpoints públicos (Registo, Login, Planos, etc.)
                        .requestMatchers(HttpMethod.POST, "/api/usuarios/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/usuarios").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/planos/buscar").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/ranking").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/exercicios").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/perfis").permitAll()

                        // ---> NOVA LINHA: Liberar a alteração de perfil <---
                        // O "*" serve como coringa para qualquer ID que você passar
                        .requestMatchers(HttpMethod.PATCH, "/api/usuarios/*/alterar-perfil").permitAll()

                        .requestMatchers("/imagens/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // Qualquer outra rota precisa de autenticação
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Define as origens permitidas (o seu front-end)
        // "*" permite TUDO, o que é bom para desenvolvimento.
        // Para produção, mude para: "http://seu-site.com"
        configuration.setAllowedOrigins(Arrays.asList("*"));

        // Define os métodos HTTP permitidos (GET, POST, etc.)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Define os cabeçalhos permitidos
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "x-auth-token"));

        // Expõe cabeçalhos (se necessário, ex: para o token)
        configuration.setExposedHeaders(List.of("x-auth-token"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica esta configuração a todas as rotas da sua API
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}