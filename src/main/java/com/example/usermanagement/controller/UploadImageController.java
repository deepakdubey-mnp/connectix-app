package com.example.usermanagement.controller;

import com.example.usermanagement.dto.ImageDto;
import com.example.usermanagement.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Controller for handling file uploads from mobile apps.
 */
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor

@Tag(name = "File Upload", description = "APIs for uploading files from mobile applications")
public class UploadImageController {

    private final ImageService imageService;

    @RequestMapping(method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Upload an image", description = "Upload an image file from a mobile application")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
        @ApiResponse(responseCode = "400", description = "File is empty, not an image, or invalid request"),
        @ApiResponse(responseCode = "500", description = "Failed to upload file")
    })
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("Only image files are allowed");
            }
            imageService.uploadImage(file);
            return ResponseEntity.ok("Image uploaded successfully: " +file.getOriginalFilename());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to upload file: " + e.getMessage());
        }
    }
}