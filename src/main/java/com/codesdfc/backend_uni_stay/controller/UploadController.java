package com.codesdfc.backend_uni_stay.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/uploads")
public class UploadController {

    private final Path rootLocation;

    public UploadController() {
        this.rootLocation = Paths.get("uploads").toAbsolutePath().normalize();
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear el directorio de uploads", e);
        }
    }

    @PostMapping(
            value = "/alojamientos",
            consumes = { "multipart/form-data" }
    )
    public ResponseEntity<List<String>> uploadAlojamientoImages(
            @RequestParam("files") List<MultipartFile> files
    ) {
        if (files == null || files.isEmpty()) {
            return ResponseEntity.badRequest().body(List.of());
        }

        List<String> fileNames = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            String originalName = StringUtils.cleanPath(file.getOriginalFilename());
            String ext = "";

            int dotIndex = originalName.lastIndexOf('.');
            if (dotIndex >= 0) {
                ext = originalName.substring(dotIndex); // incluye el punto
            }

            String baseName = (dotIndex >= 0) ? originalName.substring(0, dotIndex) : originalName;
            String timestamp = LocalDateTime.now().format(formatter);

            String newFileName = baseName + "_" + timestamp + ext;

            try {
                Path target = rootLocation.resolve(newFileName);
                Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
                fileNames.add(newFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new ResponseEntity<>(fileNames, HttpStatus.OK);
    }

    // ENDPOINT PARA FOTO DE PERFIL
    @PostMapping(
            value = "/perfiles",
            consumes = { "multipart/form-data" }
    )
    public ResponseEntity<String> uploadFotoPerfil(
            @RequestParam("file") MultipartFile file
    ) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("Archivo vacío");
        }

        try {
            Path perfilDir = rootLocation.resolve("perfiles");
            Files.createDirectories(perfilDir);

            String originalName = StringUtils.cleanPath(file.getOriginalFilename());
            String ext = "";

            int dotIndex = originalName.lastIndexOf('.');
            if (dotIndex >= 0) {
                ext = originalName.substring(dotIndex);
            }

            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
            String newFileName = "perfil_" + timestamp + ext;

            Path target = perfilDir.resolve(newFileName);

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok(newFileName);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al subir la foto de perfil");
        }
    }

}
