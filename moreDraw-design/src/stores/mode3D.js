// src/stores/mode3D.js
import { defineStore } from 'pinia';

export const useModelStore = defineStore('modelStore', {
    state: () => ({
        currentModel: null,
        showTools: false, // Variável para controlar a visibilidade dos botões de comando
    }),
    actions: {
        setCurrentModel(modelData) {
            this.currentModel = modelData;
        },
        toggleShowTools() {
            this.showTools = !this.showTools;
        },
        setShowTools(value) {
            this.showTools = value;
        },
    },
});
