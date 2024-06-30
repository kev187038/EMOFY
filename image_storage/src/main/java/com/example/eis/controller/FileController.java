
package com.example.eis.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperties;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Image Management")
public class FileController {

    private static final Logger logger = LogManager.getLogger(FileController.class);

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
            content = {@Content(mediaType = "text/plain")}), // Specifica il tipo di dati della risposta come stringa
        @ApiResponse(responseCode = "500",
            description = "The image could not be uploaded",
            content = {@Content(mediaType = "text/plain")})
    })
    @PostMapping(value = "/images/{user}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(
            @RequestPart("file") MultipartFile file,
            @PathVariable String user,
            @RequestParam(value = "label", required = false) String label) {
        
        // Ottieni l'utente autenticato dall'oggetto SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUser = authentication.getName();
        if (!authenticatedUser.equals(user)) {
            // L'utente autenticato non corrisponde all'utente specificato nella richiesta, quindi restituisci un errore
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to perform this action");
        }
        try {
            System.out.println("request " + file+ " " + user);
            FileInfo fileInfo = fileService.uploadFile(file, user, label);
            logger.info("Image uploaded successfully by user {}: {}", user, fileInfo.getObjectName());
            return ResponseEntity.ok("File uploaded successfully!");
        } catch (Exception e) {
            logger.error("User {} recieved error: {}", user, e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file: " + e.getMessage());
        }
    }

    @Operation(summary = "Download an image")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
        description = "The requested image is retrieved",
        content = {@Content(mediaType = "image")}),
        @ApiResponse(responseCode = "500",
        description = "The image could not be retrieved",
        content = {@Content(mediaType = "text/plain")})
    })
    @GetMapping(value = "/images/{user}/{fileName}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName,
                                               @PathVariable String user) {
                                                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUser = authentication.getName();
        if (!authenticatedUser.equals(user)) {
            // L'utente autenticato non corrisponde all'utente specificato nella richiesta, quindi restituisci un errore
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        try {
            FileInfo fileInfo = fileService.downloadFile(fileName, user);
            logger.info("Image retrieved successfully by user {}: {}", user, fileInfo.getObjectName());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(fileInfo.getContentType()))
                    .body(fileInfo.getFileBytes());
            
        } catch (Exception e) {
            logger.error("User {} while retrieving {} recieved error: {}", user, fileName, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(("Error fetching file: " + e.getMessage()).getBytes());
        }
    }

    @Operation(summary = "Delete an image")
    @DeleteMapping("/images/{userName}/{fileName}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
        description = "The requested image is deleted",
        content = {@Content(mediaType = "text/plain")}),
        @ApiResponse(responseCode = "500",
        description = "The image could not be deleted",
        content = {@Content(mediaType = "text/plain")})
    })
    public ResponseEntity<String> deleteFile(@RequestParam("file") String file, 
                                             @RequestParam("user") String user,
                                             @PathVariable String fileName, 
                                             @PathVariable String userName) {
        
        // Ottieni l'utente autenticato dall'oggetto SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUser = authentication.getName();

        // Verifica se l'utente autenticato corrisponde all'utente specificato nella richiesta
        if (!authenticatedUser.equals(user)) {
            // L'utente autenticato non corrisponde all'utente specificato nella richiesta, quindi restituisci un errore
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to perform this action");
        }
        //check correspondance between path and request params (protect from wrong deletions)
        if (!file.equals(fileName) || !user.equals(userName))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unacceptable request");
        
        try {
            fileService.deleteFile(file, user);
            logger.info("Image deleted successfully by user {}: {}", user, fileName);
            return ResponseEntity.ok("File deleted successfully!");
        } catch (Exception e) {
            logger.error("User {} while deleting {} recieved error: {}", user, fileName, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting file: " + e.getMessage());
        }
    }

    @Operation(summary = "Update an image")
    @PutMapping("/images/{user}/{fileName}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
        description = "The requested image is updated",
        content = {@Content(mediaType = "text/plain")}),
        @ApiResponse(responseCode = "500",
        description = "The image could not be updated",
        content = {@Content(mediaType = "text/plain")})
    })
    public ResponseEntity<String> updateFile(@RequestParam("label") String label, 
                                             @PathVariable String fileName, 
                                             @PathVariable String user) {
        // Ottieni l'utente autenticato dall'oggetto SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUser = authentication.getName();

        // Verifica se l'utente autenticato corrisponde all'utente specificato nella richiesta
        if (!authenticatedUser.equals(user)) {
            // L'utente autenticato non corrisponde all'utente specificato nella richiesta, quindi restituisci un errore
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to perform this action");
        }
        try {
            FileInfo fileInfo = fileService.updateFile(fileName, user, label);
            logger.info("Image updated successfully by user {}: {}", user, fileInfo.getObjectName());
            return ResponseEntity.ok("File updated successfully!");
        } catch (Exception e) {
            logger.error("User {} while updating {} recieved error: {}", user, fileName, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating file: " + e.getMessage());
        }
    }


    @Operation(summary = "Get all images with metadata")
    @GetMapping("/model")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
        description = "All the images names and metadata are returned",
        content = {@Content(mediaType = "text/plain")}),
        @ApiResponse(responseCode = "500",
        description = "Images and metadata could not be retrieved",
        content = {@Content(mediaType = "text/plain")})
    })
    public ResponseEntity<String> getAllFilesWithMetadata() {
        try {
            Map<String, Map<String, String>> filesWithMetadata = fileService.getAllFilesWithMetadata();
            return ResponseEntity.ok(filesWithMetadata.toString());
        } catch (Exception e) {
            logger.error("Model recieved error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @Operation(summary = "Get user images")
    @GetMapping("/images/{user}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
        description = "All the images names and metadata of a specific user are returned",
        content = {@Content(mediaType = "text/plain")}),
        @ApiResponse(responseCode = "500",
        description = "Images and metadata could not be retrieved",
        content = {@Content(mediaType = "text/plain")})
    })
    public ResponseEntity<String> getUserImages(@PathVariable String user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUser = authentication.getName();

        // Verifica se l'utente autenticato corrisponde all'utente specificato nella richiesta
        if (!authenticatedUser.equals(user)) {
            // L'utente autenticato non corrisponde all'utente specificato nella richiesta, quindi restituisci un errore
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to perform this action");
        }
        try {
            Map<String, Map<String, String>> filesWithMetadata = fileService.getUserImages(user);
            logger.info("User {} retrieved all the images", user);
            return ResponseEntity.ok(filesWithMetadata.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            logger.error("User {} while retrieving all images recieved error: {}", user, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
 
}
