package com.fithub.fithub_api.infraestructure.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    // Define a pasta onde os ficheiros vão ficar
    private final Path fileStorageLocation;

    public FileStorageService() {
        // Cria a pasta "uploads" na raiz do projeto se não existir
        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Não foi possível criar a diretoria de uploads.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        try {
            // Normaliza o nome do ficheiro
            String originalFileName = file.getOriginalFilename();

            // Gera um nome único: UUID + Extensão (ex: 550e8400-e29b... .jpg)
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String newFileName = UUID.randomUUID().toString() + fileExtension;

            // Caminho de destino
            Path targetLocation = this.fileStorageLocation.resolve(newFileName);

            // Copia o ficheiro (substituindo se existir com o mesmo nome, o que é improvável com UUID)
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return newFileName;
        } catch (IOException ex) {
            throw new RuntimeException("Não foi possível guardar o ficheiro " + file.getOriginalFilename(), ex);
        }
    }
}