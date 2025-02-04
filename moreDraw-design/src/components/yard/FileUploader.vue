<template>
    <div
        class="drop-zone"
        @dragover.prevent="handleDragOver"
        @drop.prevent="handleDrop"
        @click="triggerFileInput"
    >
        <p>Arraste e solte uma pasta com modelo 3D aqui ou clique para selecionar.</p>
        <input
            ref="fileInput"
            type="file"
            class="file-input"
            multiple
            webkitdirectory
            @change="onFileInputChange"
        >
    </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { Notify } from 'quasar';
import * as THREE from 'three';
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader.js';
import { OBJLoader } from 'three/examples/jsm/loaders/OBJLoader.js';
import { MTLLoader } from 'three/examples/jsm/loaders/MTLLoader.js';

// Referência ao input de arquivo
const fileInput = ref(null);

// Armazenamento de arquivos MTL e texturas
const localFiles = reactive({
    mtl: null,
    textures: {},
});

// Função para abrir o seletor de arquivos
const triggerFileInput = () => {
    fileInput.value.click();
};

// Funções de Drag-and-Drop
function handleDragOver(event) {
    event.preventDefault();
    event.dataTransfer.dropEffect = 'copy';
}

function handleDrop(event) {
    event.preventDefault();
    const files = event.dataTransfer.files;
    if (files.length > 0) {
        handleFiles(files);
    }
}

function onFileInputChange(event) {
    const files = event.target.files;
    if (files.length > 0) {
        handleFiles(files);
    }
}

function handleFiles(files) {
    const fileArray = Array.from(files);
    const fileMap = {};

    // Mapeia os arquivos por extensão
    fileArray.forEach((file) => {
        const extension = file.name.split('.').pop().toLowerCase();
        if (!fileMap[extension]) fileMap[extension] = [];
        fileMap[extension].push(file);
    });

    // Verificar se é um OBJ com MTL e Texturas
    if (fileMap.obj) {
        const objFile = fileMap.obj[0];
        let mtlFile = null;
        if (fileMap.mtl) {
            mtlFile = fileMap.mtl[0];
            localFiles.mtl = mtlFile;
        }

        // Armazenar texturas
        if (fileMap.jpg || fileMap.png || fileMap.jpeg) {
            const textureFiles = [...(fileMap.jpg || []), ...(fileMap.png || []), ...(fileMap.jpeg || [])];
            textureFiles.forEach((file) => {
                localFiles.textures[file.name] = file;
            });
        }

        // Emitir evento com os arquivos OBJ, MTL e texturas
        loadOBJModel(objFile, mtlFile);
    } else if (fileMap.gltf || fileMap.glb) {
        const gltfFile = fileMap.gltf ? fileMap.gltf[0] : fileMap.glb[0];
        loadGLTFModel(gltfFile);
    } else {
        Notify.create({
            message: 'Formato de arquivo não suportado ou arquivos necessários ausentes.',
            color: 'negative',
            position: 'top',
        });
    }
}

// Função para carregar modelos OBJ com MTL e texturas
function loadOBJModel(objFile, mtlFile) {
    const objLoader = new OBJLoader();

    if (mtlFile) {
        const mtlLoader = new MTLLoader();
        const mtlUrl = URL.createObjectURL(mtlFile);

        mtlLoader.load(mtlUrl, (materials) => {
            materials.preload();
            objLoader.setMaterials(materials);

            const objUrl = URL.createObjectURL(objFile);
            objLoader.load(objUrl, (object) => {
                if (object) {
                    scene.add(object);
                    Notify.create({ message: 'Modelo OBJ com materiais carregado com sucesso!', color: 'positive' });
                }
                URL.revokeObjectURL(objUrl);
            });
        }, undefined, (error) => {
            Notify.create({ message: `Erro ao carregar MTL: ${error.message}`, color: 'negative' });
            URL.revokeObjectURL(mtlUrl);
        });
    } else {
        const objUrl = URL.createObjectURL(objFile);
        objLoader.load(objUrl, (object) => {
            if (object) {
                scene.add(object);
                Notify.create({ message: 'Modelo OBJ carregado com sucesso!', color: 'positive' });
            }
            URL.revokeObjectURL(objUrl);
        });
    }
}

// Função para carregar modelos GLTF/GLB
function loadGLTFModel(gltfFile) {
    const loader = new GLTFLoader();
    const url = URL.createObjectURL(gltfFile);

    loader.load(url, (gltf) => {
        scene.add(gltf.scene);
        Notify.create({ message: 'Modelo GLTF/GLB carregado com sucesso!', color: 'positive' });
        URL.revokeObjectURL(url);
    }, undefined, (error) => {
        Notify.create({ message: `Erro ao carregar GLTF/GLB: ${error.message}`, color: 'negative' });
        URL.revokeObjectURL(url);
    });
}
</script>

<style scoped>
.drop-zone {
  width: 100%;
  height: 100px;
  border: 2px dashed #ccc;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  background-color: rgba(255, 255, 255, 0.7);
  transition: background-color 0.3s;
}

.drop-zone:hover {
  background-color: rgba(255, 255, 255, 0.9);
}

.file-input {
  display: none;
}
</style>
