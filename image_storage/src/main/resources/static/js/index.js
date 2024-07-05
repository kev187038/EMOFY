// Array to keep track of pending files
let pendingFiles = [];

document.addEventListener('DOMContentLoaded', function() {
    // Recupera le immagini dell'utente quando la pagina viene caricata
    fetchUserImages();
});

function fetchUserImages() {
    fetch(`/api/images/${userId}`, {
        method: 'GET',
        headers: {
            [csrfHeader]: csrfToken
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to fetch images');
        }
        console.log(response)
        return response.json();
    })
    .then(data => {
        populateImageList(data);
    })
    .catch(error => {
        console.error('Error fetching images:', error);
    });
}

function populateImageList(images) {
    const imageList = document.getElementById('image-list');
    imageList.innerHTML = ''; // Pulisce il contenuto precedente

    Object.keys(images).forEach(fileKey => {
        const fileMetadata = images[fileKey];
        const fileName = fileMetadata.filename;
        const fileLabel = fileMetadata.label;
        const fileTimestamp = fileMetadata.timestamp;

        const listItem = document.createElement('li');
        listItem.className = 'list-group-item list-group-item-action';

        const fileDetails = document.createElement('div');
        fileDetails.innerHTML = `
            <strong>Filename:</strong> ${fileName}<br>
            <strong>Label:</strong> ${fileLabel}<br>
            <strong>Timestamp:</strong> ${fileTimestamp}
        `;
        listItem.appendChild(fileDetails);
        imageList.appendChild(listItem);
    });
}

// Handle click on the local search button
document.getElementById('search-button').addEventListener('click', function() {
    document.getElementById('file-input').click();
});

// Handle changes in the file input
document.getElementById('file-input').addEventListener('change', function(event) {
    const fileList = event.target.files;
    addFilesToList(fileList);
});

// Function to add files to the list and to the pendingFiles array
function addFilesToList(fileList) {
    const imageList = document.getElementById('image-list');
    
    for (const file of fileList) {
        pendingFiles.push(file);

        const listItem = document.createElement('li');
        listItem.textContent = file.name;
        listItem.className = 'list-group-item list-group-item-action';
        imageList.appendChild(listItem);
    }
}

// Handle drag-and-drop events
const dropArea = document.getElementById('drop-area');

dropArea.addEventListener('dragover', (event) => {
    event.preventDefault();
    dropArea.classList.add('bg-light');
});

dropArea.addEventListener('dragleave', () => {
    dropArea.classList.remove('bg-light');
});

dropArea.addEventListener('drop', (event) => {
    event.preventDefault();
    dropArea.classList.remove('bg-light');

    const fileList = event.dataTransfer.files;
    addFilesToList(fileList);
});

// Handle click on the upload button
document.getElementById('upload-button').addEventListener('click', function() {
    uploadFiles();
});

// Function to upload files to the API
function uploadFiles() {
    if (pendingFiles.length > 0) {
        const uploadPromises = pendingFiles.map(file => {
            const formData = new FormData();
            formData.append('file', file);
       
            return fetch(`api/images/${userId}`, {
                method: 'POST',
                headers: {
                    [csrfHeader]: csrfToken
                },
                body: formData,
            })
            .then(response => {
                if (response.ok) {
                    return { fileName: file.name, status: 'uploaded successfully' };
                } else {
                    return { fileName: file.name, status: 'failed to upload' };
                }
            })
            .catch(error => {
                console.error(`Error uploading ${file.name}:`, error);
                return { fileName: file.name, status: 'failed to upload' };
            });
        });
       
        // Wait for all uploads to complete
        Promise.all(uploadPromises)
        .then(results => {
            // Mostra tutti i messaggi di stato
            showStatusMessages(results);
        });
        pendingFiles = [];
    }
}

function showStatusMessages(results) {
    const statusElement = document.getElementById('status-message');
    statusElement.innerHTML = ''; // Pulisce il contenuto precedente

    results.forEach(result => {
        const messageElement = document.createElement('div');
        messageElement.innerText = `${result.fileName}: ${result.status}`;
        messageElement.className = 'status-message-item';

        statusElement.appendChild(messageElement);
    });

    statusElement.style.opacity = '1'; // Mostra il messaggio gradualmente
    statusElement.style.display = 'block';

    // Nascondi il messaggio dopo 3 secondi
    setTimeout(() => {
        statusElement.style.opacity = '0'; // Nascondi gradualmente il messaggio
        setTimeout(() => {
            statusElement.style.display = 'none';
        }, 500); // Dopo 500ms, nascondi il messaggio definitivamente
    }, 3000); // Mostra il messaggio per 3 secondi
}
