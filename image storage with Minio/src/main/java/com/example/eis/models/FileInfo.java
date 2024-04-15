package com.example.eis.models;
import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class FileInfo {
    private String fileName;
    private String contentType;
    private byte[] fileBytes;

}
