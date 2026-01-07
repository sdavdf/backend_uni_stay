package com.codesdfc.backend_uni_stay.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArchivoService {

    private final Path rootLocation = Paths.get("uploads");

    public String guardarImagen(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("Archivo vacío");
        }

        if (!Files.exists(rootLocation)) {
            Files.createDirectories(rootLocation);
        }

        String originalName = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = "";

        int dot = originalName.lastIndexOf(".");
        if (dot >= 0) {
            extension = originalName.substring(dot);
        }

        String filename = UUID.randomUUID() + extension;
        Path destino = rootLocation.resolve(filename);

        Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

        // Solo devolvemos el nombre; el front armará la URL
        return filename;
    }
}
