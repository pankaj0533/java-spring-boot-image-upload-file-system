package com.image.demo.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.image.demo.entity.Image;
import com.image.demo.repo.ImageRepo;

@Service
public class ImageService {

	@Autowired
	private ImageRepo imageRepo;
	
    private final Path uploadDir = Paths.get("src/main/resources/static/uploads");
	

	public ResponseEntity<String> saveImages(MultipartFile file) {
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
        Path filePath = uploadDir.resolve(filename);

        try {
            Files.createDirectories(uploadDir); // Ensure the directory exists
            Files.deleteIfExists(filePath);
            Files.copy(file.getInputStream(), filePath);
            // Save file metadata
            Image metadata = new Image();
            metadata.setFilename(filename);
            metadata.setPath(filePath.toString());
            metadata.setUploadDate(LocalDateTime.now());
            imageRepo.save(metadata);
            return ResponseEntity.status(HttpStatus.CREATED).body("File uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed");
        }
	}
	

	public ResponseEntity<Resource> getImageByFileName(String filename) {
		Path filePath = uploadDir.resolve(filename);
		Resource resource;

		try {
		    resource = new UrlResource(filePath.toUri());
		    if (resource.exists()) {
		        return ResponseEntity.ok()
		                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
		                .body(resource);
		    } else {
		        return ResponseEntity.notFound().build();
		    }
		} catch (IOException e) {
		    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

}
