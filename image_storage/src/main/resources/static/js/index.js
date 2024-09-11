// Array to keep track of pending files
let pendingFiles = [];
// Verifica lo stato iniziale della media query
const mediaQuery = window.matchMedia('(max-width: 768px)');

document.getElementById('logout-button').addEventListener('click', function() {  
    fetch('logout', {
        method: 'POST',
        headers: {
            [csrfHeader]: csrfToken
        }
    })
    .then(response => {
        if (response.ok) {
            location.reload();
        } else {
            console.error('Logout failed');
        }
    })
    .catch(error => console.error('Error:', error));
});


// Aggiungi questa funzione all'inizio del file index.js
function getUrlParameter(name) {
    name = name.replace(/[\[]/, '\\[').replace(/[\]]/, '\\]');
    var regex = new RegExp('[\\?&]' + name + '=([^&#]*)');
    var results = regex.exec(location.search);
    return results === null ? '' : decodeURIComponent(results[1].replace(/\+/g, ' '));
}

// Modifica la funzione esistente DOMContentLoaded
document.addEventListener('DOMContentLoaded', function() {
    const fileKey = getUrlParameter('fileKey');
    if (fileKey) {
        displayImage(fileKey);
    } 
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
        return response.json();
    })
    .then(data => {
        populateImageList(data.data);
    })
    .catch(error => {
        console.error('Error fetching images:', error);
    });
}

function populateImageList(images) {
    const imageList = document.getElementById('image-list');
    imageList.innerHTML = ''; 

    Object.keys(images).forEach(fileKey => {
        const fileMetadata = images[fileKey];
        const fileName = fileMetadata.filename;

        const listItem = document.createElement('li');
        listItem.className = 'list-group-item list-group-item-action';
        listItem.setAttribute('data-file-key', fileKey); 

        const fileDetails = document.createElement('div');
        fileDetails.className = 'file-details'; // Add this class
        fileDetails.textContent = fileName; 

        listItem.addEventListener('click', function() {
            displayImage(fileKey); // Gestisce il click sull'immagine
        });


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
        listItem.className = 'list-group-item list-group-item-action';
        
        const fileDetails = document.createElement('div');
        fileDetails.className = 'file-details'; // Add this class
        fileDetails.textContent = file.name;
        
        listItem.appendChild(fileDetails);
        imageList.insertBefore(listItem, imageList.firstChild); // Add to the top of the list
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

// Function to convert an image file (PNG or JPG) to Base64
function getBase64ImageFromFile(file) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onload = function(event) {
            const img = new Image();
            img.onload = function() {
                const canvas = document.createElement("canvas");
                canvas.width = img.naturalWidth;
                canvas.height = img.naturalHeight;
                const ctx = canvas.getContext("2d");
                ctx.drawImage(img, 0, 0);

                // Detect the file type and create a data URL accordingly
                const dataURL = canvas.toDataURL(file.type);
                resolve(dataURL.replace(/^data:image\/(png|jpeg);base64,/, "")); // Handles both PNG and JPEG
            };
            img.onerror = function(err) {
                reject(err);
            };
            img.src = event.target.result; // Load the image with the file data
        };
        reader.onerror = function(error) {
            reject(error);
        };
        reader.readAsDataURL(file); // Read the file as a data URL
    });
}

// Function to handle file uploads
function uploadFiles() {
    if (pendingFiles.length > 0) {
        const uploadPromises = pendingFiles.map(file => {
            return getBase64ImageFromFile(file) // Convert the file to Base64
                .then(base64Image => {

                    // Send a request to /detect_face
                    return fetch(detectFaceEndpoint, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify({ image: base64Image })
                    })
                    .then(response => response.json())
                    .then(data => {
                        if (data.face_detected) {
                            // If a face is detected, proceed with the file upload
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
                            });
                        } else {
                            return { fileName: file.name, status: 'No face detected, upload skipped' };
                        }
                    });
                })
                .catch(error => {
                    console.error(`Error processing ${file.name}:`, error);
                    return { fileName: file.name, status: 'failed to process' };
                });
        });

        // Wait for all uploads to complete
        Promise.all(uploadPromises)
        .then(results => {
            // Execute fetchUserImages after a timeout
            setTimeout(fetchUserImages, 1000);

            // Display all status messages
            showStatusMessages(results);

            // Clear pendingFiles after upload completion
            pendingFiles = [];
        });
    }
}


function showStatusMessages(results) {
    const statusElement = document.getElementById('status-message');
    statusElement.innerHTML = ''; // Clear previous content

    results.forEach(result => {
        const messageElement = document.createElement('div');
        messageElement.innerText = `${result.fileName}: ${result.status}`;
        messageElement.className = 'status-message-item';

        statusElement.appendChild(messageElement);
    });

    statusElement.style.opacity = '1'; // Show message gradually
    statusElement.style.display = 'block';

    // Hide the message after 3 seconds
    setTimeout(() => {
        statusElement.style.opacity = '0'; // Gradually hide the message
        setTimeout(() => {
            statusElement.style.display = 'none';
        }, 500); // After 500ms, hide the message permanently
    }, 3000); // Show the message for 3 seconds
}

function displayImage(fileKey) {
    const labelElement = document.querySelector('label[for="image-label"]');
    labelElement.innerHTML = "Select the correct emotion!"
    fetch(`/api/images/${fileKey}`, {
        method: 'GET',
        headers: {
            [csrfHeader]: csrfToken
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to fetch image');
        }
        const label = response.headers.get('X-File-Label'); // Get the label from headers
        const file_name = response.headers.get('X-File-Name'); // Get the original file name
        const selectedImage = document.getElementById('selected-image');
        selectedImage.setAttribute('data-file-name', file_name);
        document.getElementById('store-button').style.display = 'none';

        const labelDropdown = document.getElementById('image-label');
        if (label) {
            labelDropdown.value = label;
        } else {
            labelDropdown.value = "";
        }
        return response.blob();
    })
    .then(blob => {
        const imageUrl = URL.createObjectURL(blob);
        var selectedImage = document.getElementById('selected-image');
        selectedImage.src = imageUrl;
        selectedImage.setAttribute('data-file-key', fileKey);

        // Convert the blob to an image element
        const img = new Image();
        img.onload = function() {
            // Use getBase64Image to convert image to Base64
            const base64Image = getBase64Image(img);
                
            // Fetch to /detect_emotion
            fetch(detectEmotionEndpoint, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ 
                    image: base64Image,
                    fileKey: fileKey  // Include the fileKey in the request
                })
            })
            .then(response => response.json())
            .then(data => {
                const labelDropdown = document.getElementById('image-label');
                selectedImage = document.getElementById('selected-image');

                if (data.emotion && data.fileKey === selectedImage.getAttribute('data-file-key')) {
                    labelElement.innerHTML = "The predicted emotion is " + data.emotion.emotion + ". Select the correct one!";
                } else {
                    console.error('No emotion detected.');
                    showStatusMessages([{ fileName: selectedImage.getAttribute('data-file-name'), status: 'No emotion detected.' }]);
                }
            })
            .catch(error => {
                console.error('Error detecting emotion:', error);
                showStatusMessages([{ fileName: selectedImage.getAttribute('data-file-name'), status: `Failed to detect emotion: ${error.message}` }]);
            });
        };
        img.src = imageUrl;

        // Show the selected-image container and the filters dropdown
        const selectedImageContainer = document.getElementById('selected-image-container');
        selectedImageContainer.style.display = 'block';

        const imageLabelContainer = document.getElementById('image-label-container');
        imageLabelContainer.style.display = 'block';

        const deleteImageButton = document.getElementById('delete-image-button');
        deleteImageButton.style.display = 'block';

        const filtersDropDown = document.getElementById('filters-dropdown-container');
        filtersDropDown.style.display = 'block';

        // Hide the drop-area and upload-button
        const dropArea = document.getElementById('drop-area');
        dropArea.style.display = 'none';

        const uploadButton = document.getElementById('upload-button');
        uploadButton.style.display = 'none';

        // Show filters div for small screens
        if (mediaQuery.matches) {
            document.getElementById('filters').style.display = 'block';
        }

    })
    .catch(error => {
        console.error('Error fetching image:', error);
        showStatusMessages([{ fileName: 'unknown', status: `Failed to fetch image: ${error.message}` }]);
    });
}

document.getElementById('image-label').addEventListener('change', function() {
    const selectedLabel = this.value;
    const fileKey = document.getElementById('selected-image').getAttribute('data-file-key'); // Assumi che tu abbia un modo per ottenere il fileKey dell'immagine selezionata

    if (selectedLabel) {
        updateImageLabel(fileKey, selectedLabel);
    }
});

function updateImageLabel(fileKey, label) {
    const url = `api/images/${fileKey}`;

    const formData = new FormData();
    formData.append('label', label);

    fetch(url, {
        method: 'PUT',
        headers: {
            [csrfHeader]: csrfToken
        },
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.status === 200) {
            console.log('Label updated successfully');
            setTimeout(fetchUserImages, 1000);
        } else {
            console.error('Failed to update label:', data.message);
        }
    })
    .catch(error => {
        console.error('Error updating label:', error);
    });
}

document.getElementById('delete-image-button').addEventListener('click', function() {
    const fileKey = document.getElementById('selected-image').getAttribute('data-file-key');
    deleteImage(fileKey);
});

function deleteImage(fileKey) {
    const file = fileKey.split('/').pop();

    const url = `api/images/${fileKey}`;

    const data = {
        file: file,
        user: userId
    };

    fetch(url, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken
        },
        body: JSON.stringify(data)
    })
    .then(response => response.json())
    .then(data => {
        if (data.status === 200) {
            console.log('Image deleted successfully');
            window.history.replaceState({}, document.title, window.location.pathname);
            setTimeout(location.reload(), 1000);
        } else {
            console.error('Failed to delete image:', data.message);
        }
    })
    .catch(error => {
        console.error('Error deleting image:', error);
    });
}

let previousSearchTerm = '';
let searchedItems = new Set();

document.getElementById('search-input').addEventListener('keydown', function(event) {
    if (event.key === 'Enter') { // Check if the pressed key is Enter
        const searchTerm = this.value.toLowerCase();
        const imageList = document.getElementById('image-list');
        const items = imageList.getElementsByTagName('li');

        // If the search term has changed, reset the searched items set
        if (searchTerm !== previousSearchTerm) {
            searchedItems = new Set();
        }

        previousSearchTerm = searchTerm;

        for (let i = 0; i < items.length; i++) {
            const fileDetailsElement = items[i].getElementsByClassName('file-details')[0];
            const fileDetails = fileDetailsElement.innerText.toLowerCase();

            // Skip already searched items
            if (searchedItems.has(i)) {
                continue;
            }

            if (fileDetails.includes(searchTerm)) {
                const fileKey = items[i].getAttribute('data-file-key');
                displayImage(fileKey);
                searchedItems.add(i); // Add to the set of searched items
                break; // Stop after finding the first match
            }
        }
    }
});

document.getElementById('filters-dropdown').addEventListener('change', function(event) {
    const selectedFilter = this.value;
    const selectedImage = document.getElementById('selected-image');
    const fileKey = selectedImage.getAttribute('data-file-key');

    if (selectedFilter && fileKey) {
        const base64Image = getBase64Image(selectedImage);
        applyFilter(fileKey, selectedFilter, base64Image);
    }
});

function getBase64Image(img) {
    const canvas = document.createElement("canvas");
    canvas.width = img.naturalWidth; // Usa le dimensioni naturali dell'immagine
    canvas.height = img.naturalHeight;

    const ctx = canvas.getContext("2d");
    ctx.drawImage(img, 0, 0);

    const dataURL = canvas.toDataURL("image/png");
    return dataURL.replace(/^data:image\/(png|jpg);base64,/, "");
}

function applyFilter(fileKey, filter, base64Image) {
    const payload = {
        image: base64Image,
        filter: filter
    };

    fetch(filterEndpoint, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to apply filter');
        }
        return response.json();
    })
    .then(data => {
        if (data.filtered_image) {
            const imageUrl = `data:image/png;base64,${data.filtered_image}`;
            
            const selectedImage = document.getElementById('selected-image');
            selectedImage.src = imageUrl;
            document.getElementById('filters-dropdown').value = ''; 
            document.getElementById('store-button').style.display = 'block';
        }
    })
    .catch(error => {
        console.error('Error applying filter:', error);
    });
}

document.getElementById('store-button').addEventListener('click', function() {
    const selectedImage = document.getElementById('selected-image');
    const base64Image = getBase64Image(selectedImage);
    const originalFileName = selectedImage.getAttribute('data-file-name');

    // Separate the file name and the extension
    const fileNameWithoutExtension = originalFileName.replace(/\.[^/.]+$/, "");
    const fileExtension = originalFileName.match(/\.[^/.]+$/)[0];
    
    // Generate the new file name for the filtered image
    const newFileName = `${fileNameWithoutExtension} filtered${fileExtension}`;
    uploadFilteredImage(base64Image, newFileName);
});

function uploadFilteredImage(base64Image, fileName) {
    // Step 1: Send the image to /detect_face endpoint
    fetch(detectFaceEndpoint, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ image: base64Image })
    })
    .then(response => response.json())
    .then(data => {
        if (data.face_detected) {
            // Step 2: If a face is detected, convert base64 to Blob and upload the image
            const blob = base64ToBlob(base64Image, 'image/png');
            uploadImage(blob, fileName);

        } else {
            // No face detected, update status message
            showStatusMessages([{ fileName, status: 'No face detected, upload skipped' }]);
        }
    })
    .catch(error => {
        // Handle fetch or processing errors
        console.error('Error detecting face:', error);
        showStatusMessages([{ fileName, status: `Failed to process: ${error.message}` }]);
    });
}

function uploadImage(blob, fileName) {
    const formData = new FormData();
    formData.append('file', blob, fileName);

    fetch(`/api/images/${userId}`, {
        method: 'POST',
        headers: {
            [csrfHeader]: csrfToken
        },
        body: formData,
    })
    .then(response => {
        if (response.ok) {
            return { fileName: fileName, status: 'uploaded successfully' };
        } else {
            return { fileName: fileName, status: 'failed to upload' };
        }
    })
    .then(result => {
        // Update the UI or show a message after the upload
        showStatusMessages([result]);
        
        const selectedImage = document.getElementById('selected-image');
        displayImage(selectedImage.getAttribute('data-file-key'));

        // Refresh the image list
        setTimeout(fetchUserImages, 1000);
    })
    .catch(error => {
        console.error(`Error uploading ${fileName}:`, error);
        showStatusMessages([{ fileName: fileName, status: 'failed to upload' }]);
    });
}


function base64ToBlob(base64, mimeType) {
    const sliceSize = 512;
    const byteCharacters = atob(base64);
    const byteArrays = [];
    for (let offset = 0; offset < byteCharacters.length; offset += sliceSize) {
        const slice = byteCharacters.slice(offset, offset + sliceSize);
        const byteNumbers = new Array(slice.length);
        for (let i = 0; i < slice.length; i++) {
            byteNumbers[i] = slice.charCodeAt(i);
        }
        const byteArray = new Uint8Array(byteNumbers);
        byteArrays.push(byteArray);
    }
    return new Blob(byteArrays, { type: mimeType });
}

document.getElementById('help-button').addEventListener('click', function() {
    window.location.href = '/help';
});
