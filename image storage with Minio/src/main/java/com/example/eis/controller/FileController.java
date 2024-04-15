
package com.example.eis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.eis.models.FileInfo;
import com.example.eis.service.FileService;

import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Value("${minio.bucketName}")
    private String bucketName;

    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, 
                                             @RequestParam("user") String user,
                                             @RequestParam(value = "label", required = false) String label) {
        try {
            FileInfo fileInfo = fileService.uploadFile(file, user, label);
            return ResponseEntity.ok("File uploaded successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file: " + e.getMessage());
        }
    }

    @GetMapping("/{user}/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName,
                                               @PathVariable String user) {
        try {
            FileInfo fileInfo = fileService.downloadFile(fileName, user);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(fileInfo.getContentType()))
                    .body(fileInfo.getFileBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(("Error fetching file: " + e.getMessage()).getBytes());
        }
    }

    @DeleteMapping("/{userName}/{fileName}")
    public ResponseEntity<String> deleteFile(@RequestParam("file") String file, 
                                             @RequestParam("user") String user,
                                             @PathVariable String fileName, 
                                             @PathVariable String userName) {
        //check correspondance between path and request params (protect from wrong deletions)
        if (!file.equals(fileName) || !user.equals(userName))
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unacceptable request");
        try {
            fileService.deleteFile(file, user);
            return ResponseEntity.ok("File deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting file: " + e.getMessage());
        }
    }

    @PutMapping("/{userName}/{fileName}")
    public ResponseEntity<String> deleteFile(@RequestParam("label") String label, 
                                             @PathVariable String fileName, 
                                             @PathVariable String userName) {
        try {
            fileService.updateFile(fileName, userName, label);
            return ResponseEntity.ok("File updated successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating file: " + e.getMessage());
        }
    }


    @GetMapping("/model")
    public ResponseEntity<String> getAllFilesWithMetadata() {
        try {
            Map<String, Map<String, String>> filesWithMetadata = fileService.getAllFilesWithMetadata();
            return ResponseEntity.ok(filesWithMetadata.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{user}")
    public ResponseEntity<String> getUserImages(@PathVariable String user) {
        try {
            Map<String, Map<String, String>> filesWithMetadata = fileService.getUserImages(user);
            return ResponseEntity.ok(filesWithMetadata.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
 
}
