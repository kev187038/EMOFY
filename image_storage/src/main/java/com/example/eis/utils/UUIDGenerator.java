package com.example.eis.utils;

import java.util.UUID;

public class UUIDGenerator {
    private static UUIDGenerator instance = null;
    private long lastTimestamp = -1L;

    // Costruttore privato per impedire la creazione di istanze esterne
    private UUIDGenerator() {}

    public static synchronized UUIDGenerator getInstance() {
        if (instance == null) {
            instance = new UUIDGenerator();
        }
        return instance;
    }

    public synchronized String generateUUID() {
        long currentTimestamp = System.currentTimeMillis();

        // Se il timestamp attuale è lo stesso del precedente,
        // aumenta leggermente il timestamp per garantire l'unicità
        if (currentTimestamp == lastTimestamp) {
            currentTimestamp++;
        } else if (currentTimestamp < lastTimestamp) {
            // In caso di retrodatazione dell'orologio di sistema,
            // aumenta drasticamente il timestamp per evitare ID duplicati
            currentTimestamp = lastTimestamp + 1;
        }

        lastTimestamp = currentTimestamp;

        // Genera UUID v1 basato sul timestamp corrente
        UUID uuid = UUID.randomUUID();
        return new UUID(currentTimestamp, uuid.getLeastSignificantBits()).toString();
    }
}
