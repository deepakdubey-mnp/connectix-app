package com.example.usermanagement.util;

import java.io.File;
import java.io.IOException;

import net.coobird.thumbnailator.Thumbnails;

public class ImageCompressor {
    public static File compressImage(File inputFile) throws IOException {
        File outputFile = new File(inputFile.getParent(), "compressed_" + inputFile.getName() + ".webp");

        Thumbnails.of(inputFile)
                .size(1080, 1080)          // enforce consistent dimensions
                .outputFormat("jpg")      // optimized format for mobile
                .outputQuality(0.8)        // compression level (80%)
                .toFile(outputFile);

        return outputFile;
    }
}
