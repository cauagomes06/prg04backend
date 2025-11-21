package com.fithub.fithub_api.infraestructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Mapeia o URL /imagens/** para a pasta fisica "uploads"
        String uploadPath = Paths.get("uploads").toAbsolutePath().toUri().toString();


        if (!uploadPath.endsWith("/")) {
            uploadPath += "/";
        }

        registry.addResourceHandler("/imagens/**")
                .addResourceLocations(uploadPath);
    }
}