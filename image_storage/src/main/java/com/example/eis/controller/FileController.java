package com.example.eis.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.eis.models.FileInfo;
import com.example.eis.service.FileService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Image Management")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Value("${minio.bucketName}")
    private String bucketName;

    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }
    
    @Operation(summary = "Upload an image")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
            description = "The image has been uploaded to the bucket",
            content = {@Content(mediaType = "application/json")}),
        @ApiResponse(responseCode = "500",
            description = "The image could not be uploaded",
            content = {@Content(mediaType = "application/json")})
    })
    @PostMapping(value = "/images/{user}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestPart("file") MultipartFile file,
            @PathVariable String user,
            @RequestParam(value = "label", required = false) String label) {
            
        // Get the authenticated user from the SecurityContextHolder object
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUser = authentication.getName();
        if (!authenticatedUser.equals(user)) {
            // Error: the authenticated user does not correspond to the current user
            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.UNAUTHORIZED.value());
            response.put("message", "You are not authorized to perform this action");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        try {
            System.out.println("request " + file+ " " + user);
            FileInfo fileInfo = fileService.uploadFile(file, user, label);
            logger.info("[EMOFY] Image uploaded successfully by user {}: {}", user, fileInfo.getObjectName());

            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.OK.value());
            response.put("message", "File uploaded successfully!");
            response.put("fileInfo", fileInfo);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("[EMOFY] User {} received error: {}", user, e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("message", "Error uploading file: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "Download an image")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
        description = "The requested image is retrieved",
        content = {@Content(mediaType = "application/octet-stream")}),
        @ApiResponse(responseCode = "500",
        description = "The image could not be retrieved",
        content = {@Content(mediaType = "application/json")})
    })
    @GetMapping(value = "/images/{user}/{fileName}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName,
                                               @PathVariable String user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUser = authentication.getName();
        if (!authenticatedUser.equals(user)) {
            // The authenticated user does not correspond to the specified user, so return an error
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        try {
            FileInfo fileInfo = fileService.downloadFile(fileName, user);
            logger.info("[EMOFY] Image retrieved successfully by user {}: {}", user, fileInfo.getObjectName());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(fileInfo.getContentType()))
                    .body(fileInfo.getFileBytes());
            
        } catch (Exception e) {
            logger.error("[EMOFY] User {} while retrieving {} received error: {}", user, fileName, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(("Error fetching file: " + e.getMessage()).getBytes());
        }
    }

    @Operation(summary = "Delete an image")
    @DeleteMapping("/images/{userName}/{fileName}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
        description = "The requested image is deleted",
        content = {@Content(mediaType = "application/json")}),
        @ApiResponse(responseCode = "500",
        description = "The image could not be deleted",
        content = {@Content(mediaType = "application/json")})
    })
    public ResponseEntity<Map<String, Object>> deleteFile(@RequestParam("file") String file, 
                                             @RequestParam("user") String user,
                                             @PathVariable String fileName, 
                                             @PathVariable String userName) {
        
        // Get the authenticated user from the SecurityContextHolder object
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUser = authentication.getName();

        // Verify if the authenticated user corresponds to the specified user
        if (!authenticatedUser.equals(user)) {
            // The authenticated user does not correspond to the specified user, so return an error
            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.UNAUTHORIZED.value());
            response.put("message", "You are not authorized to perform this action");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Check correspondence between path and request params (protect from wrong deletions)
        if (!file.equals(fileName) || !user.equals(userName)) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.UNAUTHORIZED.value());
            response.put("message", "Unacceptable request");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            fileService.deleteFile(file, user);
            logger.info("[EMOFY] Image deleted successfully by user {}: {}", user, fileName);

            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.OK.value());
            response.put("message", "File deleted successfully!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("[EMOFY] User {} while deleting {} received error: {}", user, fileName, e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("message", "Error deleting file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "Update an image")
    @PutMapping("/images/{user}/{fileName}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
        description = "The requested image is updated",
        content = {@Content(mediaType = "application/json")}),
        @ApiResponse(responseCode = "500",
        description = "The image could not be updated",
        content = {@Content(mediaType = "application/json")})
    })
    public ResponseEntity<Map<String, Object>> updateFile(@RequestParam("label") String label, 
                                             @PathVariable String fileName, 
                                             @PathVariable String user) {
        // Get the authenticated user from the SecurityContextHolder object
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUser = authentication.getName();

        // Verify if the authenticated user corresponds to the specified user
        if (!authenticatedUser.equals(user)) {
            // The authenticated user does not correspond to the specified user, so return an error
            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.UNAUTHORIZED.value());
            response.put("message", "You are not authorized to perform this action");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            FileInfo fileInfo = fileService.updateFile(fileName, user, label);
            logger.info("[EMOFY] Image updated successfully by user {}: {}", user, fileInfo.getObjectName());

            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.OK.value());
            response.put("message", "File updated successfully!");
            response.put("fileInfo", fileInfo);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("[EMOFY] User {} while updating {} received error: {}", user, fileName, e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("message", "Error updating file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "Get all images with metadata")
    @GetMapping("/model")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
        description = "All the images names and metadata are returned",
        content = {@Content(mediaType = "application/json")}),
        @ApiResponse(responseCode = "500",
        description = "Images and metadata could not be retrieved",
        content = {@Content(mediaType = "application/json")})
    })
    public ResponseEntity<Map<String, Object>> getAllFilesWithMetadata() {
        try {
            Map<String, Map<String, String>> filesWithMetadata = fileService.getAllFilesWithMetadata();

            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.OK.value());
            response.put("data", filesWithMetadata);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("[EMOFY] Model received error: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("message", "Error retrieving files with metadata: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "Get user images")
    @GetMapping("/images/{user}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
        description = "All the images names and metadata of a specific user are returned",
        content = {@Content(mediaType = "application/json")}),
        @ApiResponse(responseCode = "500",
        description = "Images and metadata could not be retrieved",
        content = {@Content(mediaType = "application/json")})
    })
    public ResponseEntity<Map<String, Object>> getUserImages(@PathVariable String user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUser = authentication.getName();

        // Verify if the authenticated user corresponds to the specified user
        if (!authenticatedUser.equals(user)) {
            // The authenticated user does not correspond to the specified user, so return an error
            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.UNAUTHORIZED.value());
            response.put("message", "You are not authorized to perform this action");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            Map<String, Map<String, String>> filesWithMetadata = fileService.getUserImages(user);
            logger.info("[EMOFY] User {} retrieved all the images", user);

            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.OK.value());
            response.put("data", filesWithMetadata);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            logger.error("[EMOFY] User {} while retrieving all images received error: {}", user, e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("message", "Error retrieving user images: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
