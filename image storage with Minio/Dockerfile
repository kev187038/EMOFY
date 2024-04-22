# Usa un'immagine di base contenente una versione di Java
FROM openjdk:17

# Copia il tuo file JAR all'interno del contenitore
COPY target/emofy-image-storage-0.0.1-SNAPSHOT.jar /app/emofy-image-storage.jar

# Definisci la directory di lavoro
WORKDIR /app

EXPOSE 8080

# Comando di avvio dell'applicazione quando il contenitore viene avviato
CMD ["java", "-jar", "emofy-image-storage.jar"]