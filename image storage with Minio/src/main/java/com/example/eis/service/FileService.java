package com.example.eis.service;

import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.Item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.eis.models.FileInfo;
import com.example.eis.utils.UUIDGenerator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

@Service
public class FileService {

    @Value("${minio.bucketName}")
    private String bucketName;

    private final MinioClient minioClient;

    public FileService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    /**
     * Uploads a file to the MinIO server.
     *
     * @param file  The file to upload.
     * @param user  The user uploading the file.
     * @param label The label associated with the file (optional).
     * @return A FileInfo object representing the uploaded file.
     * @throws IOException              If an I/O error occurs.
     * @throws MinioException           If an error occurs in MinIO.
     * @throws InvalidKeyException      If the MinIO access key is invalid.
     * @throws NoSuchAlgorithmException If the specified algorithm for encryption is not available.
     * @throws IllegalArgumentException If the arguments are invalid.
     */
    public FileInfo uploadFile(MultipartFile file, String user, String label) throws IOException, MinioException, InvalidKeyException, NoSuchAlgorithmException, IllegalArgumentException {

        Map<String, String> metadata = new HashMap<String, String>();
        if (label != null)
            metadata.put("label", label);
        metadata.put("fileName", file.getOriginalFilename());

        String fileId = UUIDGenerator.getInstance().generateUUID();

        minioClient.putObject(PutObjectArgs.builder()
                   .bucket(bucketName)
                   .object(user + "/" + fileId)
                   .stream(file.getInputStream(), file.getSize(), -1)
                   .contentType(file.getContentType())
                   .userMetadata(metadata)
                   .build());
        return new FileInfo(file.getOriginalFilename(), file.getContentType(), file.getBytes());
    }

    /**
     * Downloads a file from the MinIO server.
     *
     * @param fileName The name of the file to download.
     * @param user     The user requesting the file.
     * @return A FileInfo object representing the downloaded file.
    */
    public FileInfo downloadFile(String fileName, String user) throws IOException, MinioException, InvalidKeyException, NoSuchAlgorithmException, IllegalArgumentException {
        String objectName = fileName;
        GetObjectResponse objectResponse = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(user + "/" + objectName)
                        .build());
        byte[] fileBytes = objectResponse.readAllBytes();
        String contentType = objectResponse.headers().get("Content-Type");

        return new FileInfo(fileName, contentType, fileBytes);
    }

    /**
     * Retrieves image names with their metadata for the specified user.
     *
     * @param user The user for whom to retrieve image names and metadata.
     * @return A map containing image names as keys and their metadata as values.
    */
    private Map<String, Map<String, String>> retriveImagesNames(String user) throws IOException, MinioException, InvalidKeyException, NoSuchAlgorithmException, IllegalArgumentException {
        Map<String, Map<String, String>> filesWithMetadata = new HashMap<>();
        Queue<String> directoriesToExplore = new LinkedList<>();

        // Add the main directory (bucket) to the queue
        directoriesToExplore.add(user.equals("model") ? "" : user + "/");

        while (!directoriesToExplore.isEmpty()) {
            String currentDirectory = directoriesToExplore.poll();

            Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                                                .bucket(bucketName)
                                                .prefix(currentDirectory)
                                                .build());
            for (Result<Item> result : results) {
                Item item = result.get();
                String objectName = item.objectName();
                // Check if it's a directory
                if (item.isDir()) {
                    // Add the directory to the queue for further exploration
                    directoriesToExplore.add(objectName);
                } else {
                    // Retrieve object metadata
                    StatObjectResponse objectStat = minioClient.statObject(StatObjectArgs.builder()
                                                        .bucket(bucketName)
                                                        .object(objectName)
                                                        .build());
                    Map<String, String> metadata = new HashMap<>(objectStat.userMetadata());
                    // Add timestamp to the new map
                    metadata.put("timestamp", objectStat.lastModified().toString());

                    filesWithMetadata.put(objectName, metadata);
                }
            }
        }

        return filesWithMetadata;
    }

    /**
     * Retrieves all files with their metadata from the MinIO server.
     *
    */
    public Map<String, Map<String, String>> getAllFilesWithMetadata() throws IOException, MinioException, InvalidKeyException, NoSuchAlgorithmException, IllegalArgumentException {
        return this.retriveImagesNames("model");
    }

    /**
     * Retrieves images for the specified user with their metadata, ordered by timestamp in descending order.
     *
    */
    public Map<String, Map<String, String>> getUserImages(String user) throws IOException, MinioException, InvalidKeyException, NoSuchAlgorithmException, IllegalArgumentException {
        // Get files with metadata
        Map<String, Map<String, String>> filesWithMetadata = retriveImagesNames(user);

        // Create a new LinkedHashMap to maintain insertion order of elements
        LinkedHashMap<String, Map<String, String>> sortedFilesWithMetadata = new LinkedHashMap<>();

        // Sort the map by timestamp in descending order
        filesWithMetadata.entrySet().stream()
                          .sorted(Map.Entry.comparingByValue(Comparator.comparing(m -> m.get("timestamp"), Comparator.reverseOrder())))
                          .forEachOrdered(entry -> sortedFilesWithMetadata.put(entry.getKey(), entry.getValue()));

        return sortedFilesWithMetadata;
    }

    public void deleteFile(String fileName, String user) throws IOException, MinioException, InvalidKeyException, NoSuchAlgorithmException, IllegalArgumentException {
        // Componi il nome completo dell'oggetto nel bucket
        String objectName = user + "/" + fileName;
    
        // Elimina l'oggetto dal server MinIO
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());
    }

    public void updateFile(String fileName, String userName, String newLabel) throws IOException, MinioException, InvalidKeyException, NoSuchAlgorithmException, IllegalArgumentException {
        // Componi il nome completo dell'oggetto nel bucket
        String objectName = userName + "/" + fileName;
    
        // Ottieni i metadati dell'oggetto
        StatObjectResponse objectStat = minioClient.statObject(StatObjectArgs.builder()
                                                .bucket(bucketName)
                                                .object(objectName)
                                                .build());
        Map<String, String> metadata = new HashMap<>(objectStat.userMetadata());
    
        // Aggiorna la label nei metadati
        metadata.put("label", newLabel);
    
        // Aggiorna solo i metadati dell'oggetto sul server MinIO
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(new ByteArrayInputStream(new byte[0]), 0, -1) // Stream vuoto per mantenere il contenuto originale
                        .contentType("application/octet-stream") // Tipo di contenuto necessario per il metodo putObject
                        .userMetadata(metadata)
                        .build());
    }
                        
    
}
