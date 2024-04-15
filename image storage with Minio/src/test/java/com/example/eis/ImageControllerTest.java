/*
package com.example.eis;

import com.example.eis.controller.ImageController;
import io.minio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.beans.factory.annotation.Autowired;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@SpringBootTest
@AutoConfigureMockMvc
public class ImageControllerTest {

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private ImageController imageController;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

	

    @Test
    public void testUploadImage() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test".getBytes());

        when(minioClient.putObject(any(PutObjectArgs.class))).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/images/upload")
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Immagine caricata con successo!"));
    }

    @Test
    public void testGetImage() throws Exception {
        byte[] imageData = "test.jpg".getBytes();
        GetObjectResponse getObjectResponse = mock(GetObjectResponse.class);
        when(getObjectResponse.readAllBytes()).thenReturn(imageData);
    
        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(getObjectResponse);
    
        mockMvc.perform(MockMvcRequestBuilders.get("/api/images/test.jpg"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().bytes(imageData));
    }

}
*/