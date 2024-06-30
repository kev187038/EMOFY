package com.example.eis.utils;

import org.apache.tika.Tika;

public class ImageDetector {
    public static boolean isImage(byte[] bs) {
        Tika tika = new Tika();
        String mimeType = tika.detect(bs);
        return mimeType != null && mimeType.startsWith("image/");
      
    }
}
