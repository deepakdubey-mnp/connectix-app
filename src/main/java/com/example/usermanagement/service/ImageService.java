package com.example.usermanagement.service;

import com.example.usermanagement.dto.ImageDto;
import com.example.usermanagement.entity.Image;
import com.example.usermanagement.repository.ImageRepository;
import com.example.usermanagement.util.FileUtils;
import com.example.usermanagement.util.ImageCompressor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import net.coobird.thumbnailator.Thumbnails;

/**
 * Service class for handling image upload operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private static final String UPLOAD_DIR = "C:\\Connectix\\SourceCode\\data-store\\brand\\";
    private static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/png", "image/gif", "image/webp", "image/bmp"};

    private final ImageRepository imageRepository;

    /**
     * Validates if the uploaded file is an image.
     *
     * @param contentType the content type of the file
     * @return true if the file is an allowed image type, false otherwise
     */
    public boolean isValidImageType(String contentType) {
        if (contentType == null) {
            return false;
        }
        for (String allowedType : ALLOWED_IMAGE_TYPES) {
            if (contentType.equals(allowedType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generates a unique file path based on date and UUID.
     *
     * @param originalFilename the original filename
     * @return the full path where the file will be stored
     * @throws IOException if directory creation fails
     */
    public Path generateUploadPath(String originalFilename) throws IOException {
        // Generate unique ID and timestamp
        String uniqueId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String dateFolder = now.format(formatter);

        // Create directory structure
        Path uploadPath = Paths.get(UPLOAD_DIR, dateFolder);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Get file extension
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = uniqueId + fileExtension;

        return uploadPath.resolve(newFilename);
    }

    /**
     * Saves the image file to disk and stores metadata in the database.
     *
     * @param file the multipart file to upload
     * @return the saved Image entity with database ID
     * @throws IOException if file operations fail
     * @throws IllegalArgumentException if file is invalid
     */
    public Image uploadImage(MultipartFile file) throws IOException, IllegalArgumentException {
        // Validate file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        String contentType = file.getContentType();
        if (!isValidImageType(contentType)) {
            throw new IllegalArgumentException("Only image files are allowed. Accepted types: JPEG, PNG, GIF, WebP, BMP");
        }
        // Generate upload path
        Path filePath = generateUploadPath(file.getOriginalFilename());

        // Save file to disk
        Files.write(filePath, file.getBytes());



        // Generate special image path with UUID and timestamp
        String uniqueId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String dateFolder = now.format(formatter);

        // Create upload directory structure: uploads/brand/yyyy/MM/dd/uuid_originalfilename
        Path uploadPath = Paths.get(UPLOAD_DIR, dateFolder);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Get file extension
        String filename = file.getOriginalFilename();
        String fileExtension = filename.substring(filename.lastIndexOf("."));
        String newFilename = uniqueId + fileExtension;

        // Save the file
        File rawFile = FileUtils.convertMultipartFileToFile(file);
        File compressedFile = ImageCompressor.compressImage(rawFile);

        Path compressedFilePath = uploadPath.resolve(compressedFile.getName());
        Files.write(compressedFilePath, file.getBytes());

        // Save image metadata to database
        Image image = new Image();
        image.setOriginalFilename(filename);
        image.setFileSize(file.getSize());
        image.setStoredFilePath(filePath.toString());
        image.setContentType(contentType);
        return imageRepository.save(image);
    }

    public static void compressAndResize(File inputFile, File outputFile) throws IOException {
        // Resize to max 1080x1080, compress to 80% quality, convert to WebP
        Thumbnails.of(inputFile)
                .size(1080, 1080)          // enforce consistent dimensions
                .outputFormat("webp")      // optimized format for mobile
                .outputQuality(0.8)        // compression level (80%)
                .toFile(outputFile);
    }

    /**
     * Retrieves all uploaded images from the database.
     *
     * @return list of all Image entities
     */
    public List<Image> getAllImages() {
        return imageRepository.findAll();
    }

    /**
     * Retrieves a specific image by its ID.
     *
     * @param id the image ID
     * @return the Image entity if found
     */
    public Image getImageById(Long id) {
        return imageRepository.findById(id).orElse(null);
    }

    /**
     * Deletes an image from the database and optionally from disk.
     *
     * @param id the image ID
     * @param deleteFromDisk whether to also delete the file from disk
     * @return true if deletion was successful, false if image not found
     */
    public boolean deleteImage(Long id, boolean deleteFromDisk) {
        Image image = getImageById(id);
        if (image == null) {
            return false;
        }

        if (deleteFromDisk) {
            try {
                Path filePath = Paths.get(image.getStoredFilePath());
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }
            } catch (IOException e) {
                // Log error but continue with database deletion
                System.err.println("Failed to delete file from disk: " + e.getMessage());
            }
        }

        imageRepository.deleteById(id);
        return true;
    }
}