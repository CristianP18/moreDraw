<template>
    <q-page class="page q-px-lg">
        <!-- Botão para Upload de Imagem -->
        <q-btn
            flat
            icon="cloud_upload"
            label="Upload Imagem"
            color="primary"
            @click="triggerFileInput"
        >
            <input
                ref="fileInput"
                type="file"
                accept="image/*"
                style="display: none;"
                multiple
                @change="handleFileChange"
            >
        </q-btn>

        <!-- Botão para limpar as imagens -->
        <q-btn
            flat
            icon="delete"
            label="Limpar Imagens"
            color="negative"
            @click="clearUploadedImages"
        />

        <!-- Botão para salvar as imagens na array -->
        <q-btn
            flat
            icon="save"
            label="Salvar Imagens"
            color="positive"
            @click="saveImages"
        />

        <!-- Área de drop para imagens -->
        <div
            ref="dropzone"
            class="dropzone"
            @dragover.prevent
            @drop="handleDrop"
        >
            <p>Arraste e solte suas imagens aqui</p>

            <!-- Visualização das imagens carregadas -->
            <div
                v-for="(image, index) in uploadedImages"
                :key="index"
                class="uploaded-image"
                :style="{
                    backgroundImage: `url(${image.url})`,
                    width: image.width + 'px',
                    height: image.height + 'px',
                    opacity: image.opacity
                }"
                @dblclick="removeImage(index)"
            />
        </div>
    </q-page>
</template>

<script setup>
import { ref } from 'vue';

const uploadedImages = ref([]); // Array to store uploaded images
const savedImages = ref([]); // Array to save images
const fileInput = ref(null);

// Function to trigger the file input
function triggerFileInput() {
    fileInput.value.click();
}

// Function to handle file change
function handleFileChange(event) {
    const files = event.target.files;
    processFiles(files);
}

// Function to process the selected images
function processFiles(files) {
    for (const file of files) {
        if (file.type.startsWith('image/')) {
            const reader = new FileReader();
            reader.onload = (e) => {
                uploadedImages.value.push({
                    url: e.target.result,
                    x: 0,
                    y: 0,
                    width: 300, // Default width
                    height: 200, // Default height
                    opacity: 1,
                });
            };
            reader.readAsDataURL(file);
        }
    }
}

// Function to handle drop events for drag-and-drop
function handleDrop(event) {
    event.preventDefault();
    const files = event.dataTransfer.files;
    processFiles(files);
}

// Function to remove an image from the array
function removeImage(index) {
    uploadedImages.value.splice(index, 1);
}

// Function to clear all uploaded images
function clearUploadedImages() {
    uploadedImages.value = [];
}

// Function to save the images to a separate array
function saveImages() {
    savedImages.value = [...uploadedImages.value];
    alert('Imagens salvas com sucesso!');
}
</script>

  <style scoped>
  .dropzone {
    position: relative;
    width: 100%;
    height: 300px;
    border: 2px dashed #ddd;
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 20px;
    cursor: pointer;
  }

  .uploaded-image {
    position: relative;
    background-size: cover;
    background-repeat: no-repeat;
    background-position: center;
    cursor: pointer;
    border: 1px solid #000;
    margin: 5px;
  }
  </style>
