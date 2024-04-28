package com.example.eis;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.web.multipart.MultipartFile;

import com.example.eis.models.FileInfo;
import com.example.eis.service.FileService;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import okhttp3.Headers;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FileServiceTest {

    private FileService fileService;
    private MinioClient minioClient;
    private FileInfo file;
    private final String user = "testUser";

    @BeforeAll
    public void setup() throws InvalidKeyException, NoSuchAlgorithmException, IllegalArgumentException, IOException, MinioException {
        
        // Create a mock MinioClient
        minioClient = mock(MinioClient.class);
        // Create a FileService instance with the mock MinioClient
        fileService = new FileService(minioClient);

        // Set up behavior for the mock MinioClient to return true for bucketExists method
        String bucketName = "test";
        fileService.setBucketName(bucketName);
        when(minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())).thenReturn(true);
    }
    
    @Test
    @Order(1)
    public void testUploadFile() throws IOException, MinioException, InvalidKeyException, NoSuchAlgorithmException {
        // Mock file data
        String fileName = "src/main/resources/static/test.jpg";
        String contentType = "image/jpeg";

        // Read file content
        byte[] fileContent = Files.readAllBytes(Paths.get(fileName));

        // Mock MinioClient behavior
        when(minioClient.putObject(any(PutObjectArgs.class))).thenReturn(null);
        

        // Perform upload
        String label = "testLabel";
        FileInfo fileInfo = fileService.uploadFile(createMockMultipartFile(fileName, contentType, fileContent), this.user, label);

        // Verify file info
        assertEquals(fileName, fileInfo.getFileName());
        assertEquals(contentType, fileInfo.getContentType());
        assertEquals(fileContent.length, fileInfo.getSize());
        assertEquals(label, fileInfo.getLabel());
        assertArrayEquals(fileContent, fileInfo.getFileBytes());
        this.file = fileInfo;
    }

    @Test
    @Order(2)
    public void testDownloadFile() throws IOException, MinioException, InvalidKeyException, NoSuchAlgorithmException {
        // Mock file data
        String fileName = this.file.getFileName();
        String contentType = this.file.getContentType();
        byte[] fileContent = this.file.getFileBytes();
        Long size = this.file.getSize();
        String label = this.file.getLabel();
        String objectName = this.file.getObjectName();

        // Mock MinioClient behavior
        GetObjectResponse getObjectResponse = mock(GetObjectResponse.class);
        Headers headers = Headers.of(
            "Content-Type", contentType,
            "x-amz-meta-label", label,  
            "Content-Length", String.valueOf(size),
            "x-amz-meta-filename", fileName  
        );
        when(getObjectResponse.headers()).thenReturn(headers);
        when(getObjectResponse.readAllBytes()).thenReturn(fileContent); // Define the response for headers() method
        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(getObjectResponse);
    
        // Perform download
        FileInfo fileInfo = fileService.downloadFile(objectName, this.user);

        // Verify file info
        assertEquals(objectName, fileInfo.getObjectName());
        assertEquals(fileName, fileInfo.getFileName());
        assertEquals(contentType, fileInfo.getContentType());
        assertEquals(size, fileInfo.getSize());
        assertEquals(label, fileInfo.getLabel());
        assertArrayEquals(fileContent, fileInfo.getFileBytes());
    }

    @Test
    @Order(3)
    public void testUpdateFile() throws IOException, MinioException, InvalidKeyException, NoSuchAlgorithmException {
        // Mock file data
        String fileName = this.file.getFileName();
        String contentType = this.file.getContentType();
        byte[] fileContent = this.file.getFileBytes();
        Long size = this.file.getSize();
        String objectName = this.file.getObjectName();
        String label = "newLabel";

        // Perform download
        FileInfo fileInfo = fileService.updateFile(objectName, this.user, label);

        // Verify file info
        assertEquals(objectName, fileInfo.getObjectName());
        assertEquals(fileName, fileInfo.getFileName());
        assertEquals(contentType, fileInfo.getContentType());
        assertEquals(size, fileInfo.getSize());
        assertEquals(label, fileInfo.getLabel());
        assertArrayEquals(fileContent, fileInfo.getFileBytes());
        this.file = fileInfo;
    }
    
    //It makes no much sense to try to automatize the result-aggregation functions and the delete. Pretty difficult to simulate

    // Helper method to create a mock MultipartFile
    private MultipartFile createMockMultipartFile(String name, String contentType, byte[] content) {
        return new MultipartFile() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getOriginalFilename() {
                return name;
            }

            @Override
            public String getContentType() {
                return contentType;
            }

            @Override
            public boolean isEmpty() {
                return content == null || content.length == 0;
            }

            @Override
            public long getSize() {
                return content.length;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return content;
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(content);
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {
                // Not implemented for mock
            }
        };
    }
}
