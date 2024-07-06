// Array to keep track of pending files
let pendingFiles = [];
// Verifica lo stato iniziale della media query
const mediaQuery = window.matchMedia('(max-width: 768px)');


document.addEventListener('DOMContentLoaded', function() {
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

// Function to upload files to the API
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
            // Run fetchUserImages after the timeout
            setTimeout(fetchUserImages, 1000);

            // Show all status messages
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
        return response.blob();
    })
    .then(blob => {
        const imageUrl = URL.createObjectURL(blob);
        const selectedImage = document.getElementById('selected-image');
        selectedImage.src = imageUrl;

        // Show the selected-image container and the filters dropdown
        const selectedImageContainer = document.getElementById('selected-image-container');
        selectedImageContainer.style.display = 'block';

        const filtersDropDown = document.getElementById('filters-dropdown-container');
        filtersDropDown.style.display = 'block';

        // Hide the drop-area and upload-button
        const dropArea = document.getElementById('drop-area');
        dropArea.style.display = 'none';

        const uploadButton = document.getElementById('upload-button');
        uploadButton.style.display = 'none';

        // show filters div for small screens
        if (mediaQuery.matches) {
            document.getElementById('filters').style.display = 'block';
        }

    })
    .catch(error => {
        console.error('Error fetching image:', error);
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
                console.log(i)
                const fileKey = items[i].getAttribute('data-file-key');
                displayImage(fileKey);
                searchedItems.add(i); // Add to the set of searched items
                break; // Stop after finding the first match
            }
        }
    }
});
