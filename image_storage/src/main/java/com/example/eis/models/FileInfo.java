package com.example.eis.models;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class FileInfo {
    private String objectName;
    private String fileName;
    private String contentType;
    private byte[] fileBytes;
    private Long size;
    private String label; // Campo opzionale

    // Costruttore con label opzionale
    public FileInfo(String objectName, String fileName, String contentType, byte[] fileBytes, Long size) {
        this.objectName = objectName;
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileBytes = fileBytes;
        this.size = size;
        this.label = null; // Inizialmente impostato a null
    }
}
