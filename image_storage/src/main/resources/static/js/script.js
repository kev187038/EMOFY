document.getElementById('search-button').addEventListener('click', function() {
    document.getElementById('file-input').click();
});

document.getElementById('file-input').addEventListener('change', function(event) {
    const fileList = event.target.files;
    const imageList = document.getElementById('image-list');

    for (const file of fileList) {
        const listItem = document.createElement('li');
        listItem.textContent = file.name;
        listItem.className = 'list-group-item list-group-item-action';
        imageList.appendChild(listItem);
    }
});

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
    const imageList = document.getElementById('image-list');

    for (const file of fileList) {
        const listItem = document.createElement('li');
        listItem.textContent = file.name;
        listItem.className = 'list-group-item list-group-item-action';
        imageList.appendChild(listItem);
    }
});


