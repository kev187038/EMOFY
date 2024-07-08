// Variabili globali
let allImages = [];
let currentIndex = 0;
const imagesPerLoad = 3;

document.addEventListener('DOMContentLoaded', function() {
    fetchAllUserImages();
    setupEventListeners();
});

function setupEventListeners() {
    document.getElementById('logout-button').addEventListener('click', logout);
    document.getElementById('load-more').addEventListener('click', loadMoreImages);
}

function logout() {  
    fetch('logout', {
        method: 'POST',
        headers: {
            [csrfHeader]: csrfToken,
            ["userId"]: userId
        }
    })
    .then(response => {
        if (response.ok) {
            window.location.href = "/";
        } else {
            console.error('Logout failed');
        }
    })
    .catch(error => console.error('Error:', error));
}

function fetchAllUserImages() {
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
        allImages = Object.entries(data.data);
        loadMoreImages();
    })
    .catch(error => {
        console.error('Error fetching images:', error);
    });
}

function loadMoreImages() {
    const imageContainer = document.getElementById('image-container');
    const endIndex = Math.min(currentIndex + imagesPerLoad, allImages.length);
    
    for (let i = currentIndex; i < endIndex; i++) {
        const [fileKey, fileMetadata] = allImages[i];
        const fileName = fileMetadata.filename;

        const col = document.createElement('div');
        col.className = 'col-md-4';

        const card = document.createElement('div');
        card.className = 'card image-card';

        const img = document.createElement('img');
        img.className = 'card-img-top';
        img.alt = fileName;

        // Aggiungi l'evento click all'immagine
        img.addEventListener('click', function() {
            window.location.href = `/?fileKey=${encodeURIComponent(fileKey)}`;
        });

        // Fetch the actual image
        fetch(`/api/images/${fileKey}`, {
            method: 'GET',
            headers: {
                [csrfHeader]: csrfToken
            }
        })
        .then(response => response.blob())
        .then(blob => {
            img.src = URL.createObjectURL(blob);
        });

        const cardBody = document.createElement('div');
        cardBody.className = 'card-body';

        const cardTitle = document.createElement('h5');
        cardTitle.className = 'card-title';
        cardTitle.textContent = fileName;

        cardBody.appendChild(cardTitle);
        card.appendChild(img);
        card.appendChild(cardBody);
        col.appendChild(card);
        imageContainer.appendChild(col);
    }

    currentIndex = endIndex;

    if (currentIndex >= allImages.length) {
        document.getElementById('load-more').style.display = 'none';
    }
}

