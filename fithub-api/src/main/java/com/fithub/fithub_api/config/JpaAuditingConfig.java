package com.fithub.fithub_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
public class JpaAuditingConfig {
    @Bean
    public AuditorAware<String> auditorProvider() {
        // A lógica aqui dentro é a "alma" do AuditorAware.
        // É aqui que você define COMO o Spring deve obter o nome do usuário.
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.of("SISTEMA"); // Valor padrão se não houver usuário logado
            }
            return Optional.of(authentication.getName()); // Pega o nome do usuário logado
        };
    }
}