package com.example.usermanagement.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageDto {

    private String originalFilename;

    private String storedFilePath;

    private String contentType;

    private Long fileSize;

}
