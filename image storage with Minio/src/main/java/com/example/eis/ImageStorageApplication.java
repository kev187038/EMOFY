package com.example.eis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.example.eis.minio.MinioProperties;



@SpringBootApplication
@EnableConfigurationProperties(MinioProperties.class)
public class ImageStorageApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImageStorageApplication.class, args);
    }

}
