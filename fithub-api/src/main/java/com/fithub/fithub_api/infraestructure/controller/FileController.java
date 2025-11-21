package com.fithub.fithub_api.infraestructure.controller;

import com.fithub.fithub_api.infraestructure.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        // 1. Guarda o ficheiro
        String fileName = fileStorageService.storeFile(file);

        // 2. Gera o URL de acesso (assumindo que vamos configurar o padrão /imagens/**)
        // Em produção, isto seria um URL completo do S3 ou similar
        String fileUrl = "/imagens/" + fileName;

        Map<String, String> response = new HashMap<>();
        response.put("fileName", fileName);
        response.put("url", fileUrl);

        return ResponseEntity.ok(response);
    }
}