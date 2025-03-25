<template>
  <q-toolbar class="q-pb-md toolbar-responsive">
    <!-- Ícone de Configuração -->
    <q-btn
      flat
      icon="settings"
      :label="$t('design.settings')"
      @click="openSettings"
    />

    <!-- ===================================================================
           Grupo de Ferramentas
      =================================================================== -->
    <q-btn-group unelevated>
      <!-- Dropdown para Selecionar Ícones (exemplo do seu projeto) -->
      <q-btn-dropdown flat icon="place" :label="$t('design.icons')" no-caps>
        <q-list style="min-width: 200px">
          <!-- Submenu Infraestrutura -->
          <q-expansion-item
            :label="$t('design.infrastructure')"
            icon="build"
            expand-separator
          >
            <q-list>
              <q-item
                v-for="icon in iconsByCategory('infraestrutura')"
                :key="icon.type"
                clickable
                @click="addNewItem(icon.type)"
              >
                <q-item-section avatar>
                  <img
                    v-if="icon.icon"
                    :src="icon.icon"
                    alt=""
                    class="icon-image"
                  />
                  <q-icon v-else :name="icon.name" />
                </q-item-section>
                <q-item-section>{{ icon.label }}</q-item-section>
              </q-item>
            </q-list>
          </q-expansion-item>

          <!-- Submenu Natureza -->
          <q-expansion-item
            :label="$t('design.nature')"
            icon="nature"
            expand-separator
          >
            <q-list>
              <q-item
                v-for="icon in iconsByCategory('natureza')"
                :key="icon.type"
                clickable
                @click="addNewItem(icon.type)"
              >
                <q-item-section avatar>
                  <img
                    v-if="icon.icon"
                    :src="icon.icon"
                    alt=""
                    class="icon-image"
                  />
                  <q-icon v-else :name="icon.name" />
                </q-item-section>
                <q-item-section>{{ icon.label }}</q-item-section>
              </q-item>
            </q-list>
          </q-expansion-item>

          <!-- Submenu Estruturas -->
          <q-expansion-item
            :label="$t('design.structures')"
            icon="apartment"
            expand-separator
          >
            <q-list>
              <q-item
                v-for="icon in iconsByCategory('estruturas')"
                :key="icon.type"
                clickable
                @click="addNewItem(icon.type)"
              >
                <q-item-section avatar>
                  <img
                    v-if="icon.icon"
                    :src="icon.icon"
                    alt=""
                    class="icon-image"
                  />
                  <q-icon v-else :name="icon.name" />
                </q-item-section>
                <q-item-section>{{ icon.label }}</q-item-section>
              </q-item>
            </q-list>
          </q-expansion-item>

          <!-- Submenu Pavimentação -->
          <q-expansion-item
            :label="$t('design.pavement')"
            icon="road"
            expand-separator
          >
            <q-list>
              <q-item
                v-for="icon in iconsByCategory('pavimentacao')"
                :key="icon.type"
                clickable
                @click="addNewItem(icon.type)"
              >
                <q-item-section avatar>
                  <img
                    v-if="icon.icon"
                    :src="icon.icon"
                    alt=""
                    class="icon-image"
                  />
                  <q-icon v-else :name="icon.name" />
                </q-item-section>
                <q-item-section>{{ icon.label }}</q-item-section>
              </q-item>
            </q-list>
          </q-expansion-item>

          <!-- Submenu Outros -->
          <q-expansion-item
            :label="$t('design.others')"
            icon="extension"
            expand-separator
          >
            <q-list>
              <q-item
                v-for="icon in iconsByCategory('outros')"
                :key="icon.type"
                clickable
                @click="addNewItem(icon.type)"
              >
                <q-item-section avatar>
                  <img
                    v-if="icon.icon"
                    :src="icon.icon"
                    alt=""
                    class="icon-image"
                  />
                  <q-icon v-else :name="icon.name" />
                </q-item-section>
                <q-item-section>{{ icon.label }}</q-item-section>
              </q-item>
            </q-list>
          </q-expansion-item>
        </q-list>
      </q-btn-dropdown>

      <!-- Botão para Selecionar Figuras Geométricas -->
      <q-btn
        flat
        icon="shapes"
        :label="$t('design.selectGeometry')"
        no-caps
        @click="openGeometrySelector"
      />

      <!-- Novo ícone para abrir seleção de imagens da API -->
      <q-btn
        flat
        icon="photo_library"
        :label="$t('design.selectImages')"
        no-caps
        @click="openImageSelectionDialog"
      />
    </q-btn-group>

    <!-- ===================================================================
        Dropdown para Modos de Desenho
        =================================================================== -->
    <div class="drawing-modes-section">
      <q-btn flat round class="drawing-mode-btn" @click="toggleDrawingMenu">
        <q-icon name="menu" size="32px" class="drawing-mode-icon" />
        <span style="margin-top: -10px">{{ $t("design.drawLine") }}</span>
      </q-btn>
      <div v-if="isDrawingMenuOpen" class="drawing-menu">
        <div
          class="drawing-menu-item"
          :class="{ 'active-drawing-mode': drawingMode === 'line' }"
          @click="selectDrawingMode('line')"
        >
          <q-icon name="edit" size="20px" />
          <span>{{ $t("design.drawLine") }}</span>
        </div>
        <div
          class="drawing-menu-item"
          :class="{ 'active-drawing-mode': drawingMode === 'shape' }"
          @click="selectDrawingMode('shape')"
        >
          <q-icon name="gesture" size="20px" />
          <span>{{ $t("design.drawShape") }}</span>
        </div>
        <div
          class="drawing-menu-item"
          :class="{ 'active-drawing-mode': drawingMode === 'freehand' }"
          @click="selectDrawingMode('freehand')"
        >
          <q-icon name="draw" size="20px" />
          <span>{{ $t("design.drawFreehand") }}</span>
        </div>
        <div
          class="drawing-menu-item"
          :class="{ 'active-drawing-mode': drawingMode === 'bezier' }"
          @click="selectDrawingMode('bezier')"
        >
          <q-icon name="polyline" size="20px" />
          <span>{{ $t("design.drawBezier") }}</span>
        </div>

        <!-- NOVO: Botão para adicionar quadrado com polylines -->
        <div
          class="drawing-menu-item"
          :class="{ 'active-drawing-mode': drawingMode === 'squarePoliline' }"
          @click="selectDrawingMode('squarePoliline')"
        >
          <q-icon name="square" size="20px" />
          <span>{{ $t("design.geomSquarePoly") }}</span>
        </div>

        <!-- NOVO: Botão para adicionar círculo com polylines -->
        <div
          class="drawing-menu-item"
          :class="{ 'active-drawing-mode': drawingMode === 'circlePoliline' }"
          @click="selectDrawingMode('circlePoliline')"
        >
          <q-icon name="circle" size="20px" />
          <span>{{ $t("design.geomCirclePoly") }}</span>
        </div>
      </div>
    </div>

    <!-- Limpar todos os itens -->
    <q-btn
      flat
      icon="image"
      :label="$t('design.cleanImage')"
      @click="cleanImage"
    />

    <!-- Botão para Mostrar/Ocultar botão de Ferramentas 3D -->
    <q-btn
      flat
      :icon="modelStore.showTools ? 'visibility_off' : 'visibility'"
      :label="$t('design.tools')"
      class="q-ml-sm"
      @click="toggleShowBtnTools"
    />

    <!-- Botão de Pintura -->
    <q-btn
      flat
      icon="format_color_fill"
      :label="$t('design.paintObject')"
      :class="{ 'active-paint-mode': isPaintMode }"
      @click="activatePaintMode"
    />

    <!-- Novo Botão para Carregar Imagens -->
    '
    <q-btn
      flat
      icon="image"
      :label="$t('design.selectImage')"
      :loading="loadingAPIImages"
      :disable="loadingAPIImages"
      @click="openImageBackgroundSelectionDialog"
    />

    <!-- Dialog de Seleção de Imagens da API -->
    <q-dialog v-model="dialogSelectImagesBackgroundVisible">
      <q-card style="min-width: 600px; min-height: 400px">
        <q-card-section class="row items-center justify-between modal-header">
          <div class="text-h6">
            {{ $t("design.selectFromAPI") }}
          </div>
          <q-btn
            flat
            icon="close"
            @click="dialogSelectImagesBackgroundVisible = false"
          />
        </q-card-section>
        <q-card-section>
          <div v-if="loadingAPIImages" class="text-center q-pa-md">
            <q-spinner size="50px" />
          </div>
          <div v-else class="q-gutter-sm">
            <div class="row wrap">
              <div
                v-for="img in apiImages.filter((image) => image.type != '2')"
                :key="img.imageId"
                class="column items-center q-pa-sm image-container"
                @click="toggleImageSelection(img)"
              >
                <div class="image-box">
                  <img
                    :src="img.imageUrl"
                    :alt="img.imageName"
                    class="image-content"
                  />
                  <q-icon
                    v-if="selectedImages.includes(img)"
                    name="check_circle"
                    color="primary"
                    size="sm"
                    class="selected-icon"
                  />
                </div>
                <div class="image-name">
                  {{ img.imageName }}
                </div>
              </div>
            </div>
          </div>
        </q-card-section>
        <q-card-actions align="right">
          <q-btn
            flat
            label="Cancelar"
            @click="dialogSelectImagesBackgroundVisible = false"
          />
          <q-btn
            color="primary"
            label="Selecionar"
            :disable="selectedImages.length === 0"
            @click="selectImagesBackground"
          />
        </q-card-actions>
      </q-card>
    </q-dialog>

    <!-- Undo / Redo -->
    <q-btn-group unelevated>
      <q-btn
        flat
        icon="undo"
        :label="$t('design.undo')"
        :disable="!canUndo"
        @click="$emit('undo')"
      />
      <q-btn
        flat
        icon="redo"
        :label="$t('design.redo')"
        :disable="!canRedo"
        @click="$emit('redo')"
      />
    </q-btn-group>

    <!-- Botão para salvar projeto (atualizar) -->
    <q-btn
      flat
      icon="save"
      :label="$t('design.saveProject')"
      @click="$emit('update-project')"
    />

    <!-- Botão para Salvar o Design (PNG ou outro formato) -->
    <q-btn
      flat
      icon="save"
      :label="$t('design.saveDesign')"
      color="primary"
      @click="$emit('save-design')"
    />

    <!-- Botão para recorte (Crop) -->
    <q-btn
      flat
      icon="crop"
      :label="$t('design.cropImage')"
      @click="saveDesignCut"
    />

    <!-- Botão para Upload de Imagem local (arquivo) -->
    <q-btn
      flat
      icon="image"
      :label="$t('design.UploadImage')"
      @click="triggerFileInput"
    />
    <input
      ref="imageInput"
      type="file"
      accept="image/*"
      style="display: none"
      @change="handleImageUpload"
    />

    <!-- Novo Projeto -->
    <q-btn
      flat
      icon="add"
      :label="$t('design.newProject')"
      @click="dialogNewProject = true"
    />

    <!-- Abrir Projeto -->
    <q-btn
      flat
      icon="folder_open"
      :label="$t('design.openProject')"
      @click="openProjectDialog"
    />

    <!-- Configurações de Desenho de Linha -->
    <div v-if="isDrawingLine" class="toolbar-section">
      <label class="toolbar-label">{{ $t("design.LineColor") }}:</label>
      <q-color
        v-model="selectedLineColor"
        class="toolbar-color-picker"
        @input="updateLineColor"
      />
      <label class="toolbar-label">{{ $t("design.LineWidth") }}:</label>
      <q-input
        v-model.number="lineWidth"
        type="number"
        min="1"
        max="20"
        class="toolbar-input"
        @input="updateLineWidth"
      />
      <label class="toolbar-label">{{ $t("design.LineType") }}:</label>
      <q-select
        v-model="lineType"
        :options="lineTypeOptions"
        emit-value
        map-options
        outlined
        dense
        class="toolbar-select"
        @input="updateLineType"
      />
    </div>

    <!-- Dialog de Seleção de Imagens da API -->
    <q-dialog v-model="dialogSelectImagesVisible">
      <q-card style="min-width: 600px; min-height: 400px">
        <q-card-section class="row items-center justify-between modal-header">
          <div class="text-h6">
            {{ $t("design.selectFromAPI") }}
          </div>
          <q-btn flat icon="close" @click="dialogSelectImagesVisible = false" />
        </q-card-section>
        <q-card-section>
          <div v-if="loadingAPIImages" class="text-center q-pa-md">
            <q-spinner size="50px" />
          </div>
          <div v-else class="q-gutter-sm">
            <div class="row wrap">
              <div
                v-for="img in apiImages.filter((image) => image.type != '2')"
                :key="img.imageId"
                class="column items-center q-pa-sm image-container"
                @click="toggleImageSelection(img)"
              >
                <div class="image-box">
                  <img
                    :src="img.imageUrl"
                    :alt="img.imageName"
                    class="image-content"
                  />
                  <q-icon
                    v-if="selectedImages.includes(img)"
                    name="check_circle"
                    color="primary"
                    size="sm"
                    class="selected-icon"
                  />
                </div>
                <div class="image-name">
                  {{ img.imageName }}
                </div>
              </div>
            </div>
          </div>
        </q-card-section>
        <q-card-actions align="right">
          <q-btn
            flat
            label="Cancelar"
            @click="dialogSelectImagesVisible = false"
          />
          <q-btn
            color="primary"
            label="Selecionar"
            :disable="selectedImages.length === 0"
            @click="selectImages"
          />
        </q-card-actions>
      </q-card>
    </q-dialog>

    <!-- Dialog para Novo Projeto -->
    <q-dialog v-model="dialogNewProject">
      <q-card>
        <q-card-section class="row items-center justify-between modal-header">
          <div class="text-h6">
            {{ $t("design.newProject") }}
          </div>
          <q-btn flat icon="close" @click="dialogNewProject = false" />
        </q-card-section>
        <q-card-section>
          <q-form @submit.prevent="emitNewProject">
            <q-input
              v-model="newProjectForm.imageName"
              :label="$t('design.ProjectName')"
              outlined
              required
              class="q-mb-md"
            />
            <q-input
              v-model="newProjectForm.groupBy"
              :label="$t('design.Group')"
              outlined
              required
              class="q-mb-md"
            />
            <q-input
              v-model="newProjectForm.description"
              :label="$t('design.Description')"
              outlined
              type="textarea"
              required
              class="q-mb-md"
            />
            <q-card-actions align="right">
              <q-btn flat label="Cancelar" @click="dialogNewProject = false" />
              <q-btn
                color="primary"
                label="Criar Projeto"
                type="submit"
                unelevated
              />
            </q-card-actions>
          </q-form>
        </q-card-section>
      </q-card>
    </q-dialog>

    <!-- Dialog para Abrir Projeto Existente -->
    <q-dialog v-model="dialogSelectProjectVisible">
      <q-card style="min-width: 600px; min-height: 400px">
        <q-card-section class="row items-center justify-between modal-header">
          <div class="text-h6">
            {{ $t("design.openProject") }}
          </div>
          <q-btn
            flat
            icon="close"
            @click="dialogSelectProjectVisible = false"
          />
        </q-card-section>
        <q-card-section>
          <div v-if="loadingProjects" class="text-center q-pa-md">
            <q-spinner size="50px" />
          </div>
          <div v-else class="q-gutter-sm">
            <div class="row wrap">
              <div
                v-for="proj in filteredProjects"
                :key="proj.imageId"
                class="column items-center q-pa-sm image-container"
                @click="selectProject(proj)"
              >
                <div class="image-box">
                  <img
                    :src="proj.imageUrl"
                    :alt="proj.imageName"
                    class="image-content"
                  />
                  <q-icon
                    v-if="selectedProject === proj"
                    name="check_circle"
                    color="primary"
                    size="sm"
                    class="selected-icon"
                  />
                </div>
                <div class="image-name">
                  {{ proj.imageName }}
                </div>
              </div>
            </div>
          </div>
        </q-card-section>
        <q-card-actions align="right">
          <q-btn
            flat
            label="Cancelar"
            @click="dialogSelectProjectVisible = false"
          />
          <q-btn
            color="primary"
            label="Abrir Projeto"
            :disable="!selectedProject"
            @click="emitOpenProject"
          />
        </q-card-actions>
      </q-card>
    </q-dialog>

    <!-- Dialog para Selecionar Figuras Geométricas -->
    <q-dialog v-model="dialogGeometrySelectorVisible">
      <q-card style="min-width: 600px; max-height: 80vh; overflow-y: auto">
        <q-card-section class="row items-center justify-between modal-header">
          <div class="text-h6">
            {{ $t("design.selectGeometry") }}
          </div>
          <q-btn
            flat
            icon="close"
            @click="dialogGeometrySelectorVisible = false"
          />
        </q-card-section>
        <q-card-section class="q-pt-none">
          <geometry-selector
            @add-item="handleAddGeometryItem"
            @close="dialogGeometrySelectorVisible = false"
          />
        </q-card-section>
      </q-card>
    </q-dialog>
  </q-toolbar>
</template>

<script setup>
/* ============================================
     IMPORTS
  ============================================ */
import { ref, computed } from "vue";
import { useModelStore } from "src/stores/mode3D.js";
import { useQuasar, QDialog, QCard, QCardSection, QCardActions } from "quasar";

/* ============================================
     IMPORT LOCAL ASSETS (EXEMPLOS)
  ============================================ */
import trailerIcon from "src/assets/objects/truck02.svg";
import truckIcon from "src/assets/objects/truck01.svg";
import yardIcon from "src/assets/objects/yardSpaces.png";
import treeIcon from "src/assets/objects/tree.png";
import wallDarkIcon from "src/assets/objects/wall_dark.png";
import wallLightIcon from "src/assets/objects/wall_light.png";
import arbustoIcon from "src/assets/objects/Arbusto.png";
import asfaltoVerticalIcon from "src/assets/objects/asfalto - vertical.png";
import asfaltoIcon from "src/assets/objects/asfalto.png";
import calcadaHorizontalIcon from "src/assets/objects/calçada_horizontal.png";
import calcadaIcon from "src/assets/objects/calçada.png";
import canteiroHorizontalIcon from "src/assets/objects/canteiro_horizontal.png";
import canteiroIcon from "src/assets/objects/canteiro.png";
import ceuIcon from "src/assets/objects/céu.jpg";
import divisaoVagasIcon from "src/assets/objects/divisão de vagas.png";
import empresaIcon from "src/assets/objects/Empresa.png";
import estradaCinzaIcon from "src/assets/objects/estrada_cinza.png";
import estradaIcon from "src/assets/objects/estrada.png";
import gramaIcon from "src/assets/objects/Grama.png";
import gramadoDarkIcon from "src/assets/objects/gramado_dark.png";
import greenGrasIcon from "src/assets/objects/green_gras.png";
import image2Icon from "src/assets/objects/image (2).png";
import image3Icon from "src/assets/objects/image (3).png";
import image720Icon from "src/assets/objects/image_720.png";
import imageFinalCleaned2Icon from "src/assets/objects/image_final_cleaned_2.png";
import montanhaIcon from "src/assets/objects/Montanha.png";
import muroIcon from "src/assets/objects/muro.png";
import objectRetryProcessedIcon from "src/assets/objects/object_retry_processed.png";
import paperTextureIcon from "src/assets/objects/papertexture-2061711_1280.jpg";
import pastoIcon from "src/assets/objects/pasto.png";
import patio02Icon from "src/assets/objects/patio02.png";
import pessegueiroIcon from "src/assets/objects/Pessegueiro.png";
import pond312465Icon from "src/assets/objects/pond-312465_640.png";
import pond1346068Icon from "src/assets/objects/pond-1346068_640.jpg";
import predioIcon from "src/assets/objects/Predio.png";
import savanaIcon from "src/assets/objects/Savana.png";
import telhadoZincoCopiaIcon from "src/assets/objects/telhado_zinco - Copia.png";
import telhadoZincoIcon from "src/assets/objects/telhado_zinco.png";
import texture1033755Icon from "src/assets/objects/texture-1033755_1280.jpg";
import vagasIcon from "src/assets/objects/vagas.png";
import vagas02Icon from "src/assets/objects/vagas02.png";
import vagsTrucksLate3ralIcon from "src/assets/objects/vagsTrucks_Late3ral.png";
import vagsTrucksIcon from "src/assets/objects/vagsTrucks.png";
import vehicleNewRetryProcessedIcon from "src/assets/objects/vehicle_new_retry_processed.png";
import yard03Icon from "src/assets/objects/yard03.png";
import { createAxiosInstance } from "../../api/axiosInstance";

import GeometrySelector from "./GeometrySelector.vue";

/* ============================================
     PROPS & EMITS
  ============================================ */
const props = defineProps({
  canUndo: Boolean,
  canRedo: Boolean,
  drawingMode: {
    type: String,
    default: null, // "line", "shape", "freehand", "bezier", etc.
  },
});

const emits = defineEmits([
  "add-item",
  "set-drawing-mode",
  "undo",
  "redo",
  "save-design",
  "save-design-cut",
  "activate-paint-mode",
  "add-image",
  "add-image-background",
  "clean-items",
  "save-project",
  "open-project",
  "update-project",
  "toggle-line-drawing",
  "update-line-color",
  "update-line-width",
  "update-line-dash-style",
  "update-line-type",
  "clear-canvas",
  "save-canvas",
  "add-geometry-with-polylines",
]);

/* ============================================
     REFS & ESTADOS
  ============================================ */
const $q = useQuasar();
const axiosInstance = createAxiosInstance("yard");
const modelStore = useModelStore();

const isDrawingMenuOpen = ref(false);
const isShapeDropdownOpen = ref(false);
const isPaintMode = ref(false);

// Upload de imagens (API e local)
const imageInput = ref(null);
const dialogSelectImagesVisible = ref(false);
const dialogSelectImagesBackgroundVisible = ref(false);
const apiImages = ref([]);
const loadingAPIImages = ref(false);
const selectedImages = ref([]);
const isImageSelectorOpen = ref(false);

// Novo projeto
const dialogNewProject = ref(false);
const newProjectForm = ref({
  imageName: "",
  groupBy: "",
  description: "",
  type: "2",
});

// Selecionar projeto
const dialogSelectProjectVisible = ref(false);
const images = ref([]);
const loadingManage = ref(false);
const loadingImages = ref(false);
const selectedProject = ref(null);

// Filtro de projetos
const filteredProjects = computed(() =>
  images.value.filter(
    (img) => typeof img.items === "string" && img.items.length > 30
  )
);

// Linha
const isDrawingLine = ref(false);
const selectedLineColor = ref("#000000");
const lineWidth = ref(2);
const lineType = ref("solid");

const lineTypeOptions = [
  { label: "Solid (Normal)", value: "solid" },
  { label: "Dashed (Tracejado)", value: "dashed" },
  { label: "Dotted (Pontilhado)", value: "dotted" },
];

/* ============================================
     LISTA DE ÍCONES
  ============================================ */
const availableIcons = [
  {
    icon: yardIcon,
    name: "yard",
    label: "",
    type: "yard",
    category: "infraestrutura",
  },
  {
    icon: trailerIcon,
    name: "trailer",
    label: "Carreta",
    type: "trailer",
    category: "infraestrutura",
  },
  {
    icon: truckIcon,
    name: "truck",
    label: "Caminhão",
    type: "truck",
    category: "infraestrutura",
  },
  {
    icon: empresaIcon,
    name: "empresa",
    label: "Empresa",
    type: "empresa",
    category: "infraestrutura",
  },

  {
    icon: treeIcon,
    name: "tree",
    label: "Árvore",
    type: "tree",
    category: "natureza",
  },
  {
    icon: arbustoIcon,
    name: "arbusto",
    label: "Arbusto",
    type: "arbusto",
    category: "natureza",
  },
  {
    icon: gramaIcon,
    name: "grama",
    label: "Grama",
    type: "grama",
    category: "natureza",
  },
  {
    icon: gramadoDarkIcon,
    name: "gramadoDark",
    label: "Gramado Dark",
    type: "gramadoDark",
    category: "natureza",
  },
  {
    icon: greenGrasIcon,
    name: "greenGras",
    label: "Green Gras",
    type: "greenGras",
    category: "natureza",
  },
  {
    icon: pastoIcon,
    name: "pasto",
    label: "Pasto",
    type: "pasto",
    category: "natureza",
  },
  {
    icon: montanhaIcon,
    name: "montanha",
    label: "Montanha",
    type: "montanha",
    category: "natureza",
  },
  {
    icon: pessegueiroIcon,
    name: "pessegueiro",
    label: "Pessegueiro",
    type: "pessegueiro",
    category: "natureza",
  },
  {
    icon: savanaIcon,
    name: "savana",
    label: "Savana",
    type: "savana",
    category: "natureza",
  },
  {
    icon: pond312465Icon,
    name: "pond312465",
    label: "Lago 312465",
    type: "pond312465",
    category: "natureza",
  },
  {
    icon: pond1346068Icon,
    name: "pond1346068",
    label: "Lago 1346068",
    type: "pond1346068",
    category: "natureza",
  },

  {
    icon: wallDarkIcon,
    name: "wallDark",
    label: "Parede Escura",
    type: "wallDark",
    category: "estruturas",
  },
  {
    icon: wallLightIcon,
    name: "wallLight",
    label: "Parede Clara",
    type: "wallLight",
    category: "estruturas",
  },
  {
    icon: muroIcon,
    name: "muro",
    label: "Muro",
    type: "muro",
    category: "estruturas",
  },
  {
    icon: predioIcon,
    name: "predio",
    label: "Prédio",
    type: "predio",
    category: "estruturas",
  },
  {
    icon: telhadoZincoCopiaIcon,
    name: "telhadoZincoCopia",
    label: "Telhado Zinco Cópia",
    type: "telhadoZincoCopia",
    category: "estruturas",
  },
  {
    icon: telhadoZincoIcon,
    name: "telhadoZinco",
    label: "Telhado Zinco",
    type: "telhadoZinco",
    category: "estruturas",
  },
  {
    icon: objectRetryProcessedIcon,
    name: "objectRetryProcessed",
    label: "Objeto Processado",
    type: "objectRetryProcessed",
    category: "estruturas",
  },
  {
    icon: patio02Icon,
    name: "patio02",
    label: "Pátio 02",
    type: "patio02",
    category: "estruturas",
  },
  {
    icon: vagasIcon,
    name: "vagas",
    label: "Vagas",
    type: "vagas",
    category: "estruturas",
  },
  {
    icon: vagas02Icon,
    name: "vagas02",
    label: "Vagas 02",
    type: "vagas02",
    category: "estruturas",
  },
  {
    icon: vagsTrucksLate3ralIcon,
    name: "vagsTrucksLate3ral",
    label: "Vagas Caminhões Lateral",
    type: "vagsTrucksLate3ral",
    category: "estruturas",
  },
  {
    icon: vagsTrucksIcon,
    name: "vagsTrucks",
    label: "Vagas Caminhões",
    type: "vagsTrucks",
    category: "estruturas",
  },

  {
    icon: asfaltoVerticalIcon,
    name: "asfaltoVertical",
    label: "Asfalto Vertical",
    type: "asfaltoVertical",
    category: "pavimentacao",
  },
  {
    icon: asfaltoIcon,
    name: "asfalto",
    label: "Asfalto",
    type: "asfalto",
    category: "pavimentacao",
  },
  {
    icon: calcadaHorizontalIcon,
    name: "calcadaHorizontal",
    label: "Calçada Horizontal",
    type: "calcadaHorizontal",
    category: "pavimentacao",
  },
  {
    icon: calcadaIcon,
    name: "calcada",
    label: "Calçada",
    type: "calcada",
    category: "pavimentacao",
  },
  {
    icon: canteiroHorizontalIcon,
    name: "canteiroHorizontal",
    label: "Canteiro Horizontal",
    type: "canteiroHorizontal",
    category: "pavimentacao",
  },
  {
    icon: canteiroIcon,
    name: "canteiro",
    label: "Canteiro",
    type: "canteiro",
    category: "pavimentacao",
  },
  {
    icon: estradaCinzaIcon,
    name: "estradaCinza",
    label: "Estrada Cinza",
    type: "estradaCinza",
    category: "pavimentacao",
  },
  {
    icon: estradaIcon,
    name: "estrada",
    label: "Estrada",
    type: "estrada",
    category: "pavimentacao",
  },

  { icon: ceuIcon, name: "ceu", label: "Céu", type: "ceu", category: "outros" },
  {
    icon: divisaoVagasIcon,
    name: "divisaoVagas",
    label: "Divisão de Vagas",
    type: "divisaoVagas",
    category: "outros",
  },
  {
    icon: image2Icon,
    name: "image2",
    label: "Imagem 2",
    type: "image2",
    category: "outros",
  },
  {
    icon: image3Icon,
    name: "image3",
    label: "Imagem 3",
    type: "image3",
    category: "outros",
  },
  {
    icon: image720Icon,
    name: "image720",
    label: "Imagem 720",
    type: "image720",
    category: "outros",
  },
  {
    icon: imageFinalCleaned2Icon,
    name: "imageFinalCleaned2",
    label: "Imagem Limpa 2",
    type: "imageFinalCleaned2",
    category: "outros",
  },
  {
    icon: paperTextureIcon,
    name: "paperTexture",
    label: "Textura de Papel",
    type: "paperTexture",
    category: "outros",
  },
  {
    icon: texture1033755Icon,
    name: "texture1033755",
    label: "Textura 1033755",
    type: "texture1033755",
    category: "outros",
  },
  {
    icon: vehicleNewRetryProcessedIcon,
    name: "vehicleNewRetryProcessed",
    label: "Veículo Processado",
    type: "vehicleNewRetryProcessed",
    category: "outros",
  },
  {
    icon: yard03Icon,
    name: "yard03",
    label: " 03",
    type: "yard03",
    category: "outros",
  },
];

/* ============================================
     MÉTODOS
  ============================================ */
// (1) Abrir configurações
function openSettings() {
  console.log("Abrindo configurações...");
}

// (2) Modo de desenho
function setDrawingMode(mode) {
  isShapeDropdownOpen.value = false;
  emits("set-drawing-mode", mode);
}

// (3) Adicionar novo item
function addNewItem(type) {
  emits("add-item", type);
}

// (4) Pintura
function activatePaintMode() {
  isPaintMode.value = !isPaintMode.value;
  emits("activate-paint-mode", isPaintMode.value);
}

// (5) Limpar imagem
function cleanImage() {
  emits("clean-items");
}

// (6) Mostrar/ocultar ferramentas 3D
function toggleShowBtnTools() {
  modelStore.toggleShowTools();
}

// (7) Selecionar imagens da API
function openImageSelectionDialog() {
  dialogSelectImagesVisible.value = true;
  loadImages();
}
// (7) Selecionar imagens da API
function openImageBackgroundSelectionDialog() {
  dialogSelectImagesBackgroundVisible.value = true;
  loadImages();
}
async function loadImages() {
  loadingAPIImages.value = true;
  try {
    const response = await axiosInstance.get("/image/add");
    apiImages.value = response.data.content.images;
  } catch (error) {
    console.error("Erro ao carregar imagens:", error);
    $q.notify({ message: "Erro ao carregar imagens.", color: "negative" });
  }
  loadingAPIImages.value = false;
}
function toggleImageSelection(img) {
  const index = selectedImages.value.indexOf(img);
  if (index > -1) {
    selectedImages.value.splice(index, 1);
  } else {
    selectedImages.value.push(img);
  }
}
function selectImages() {
  selectedImages.value.forEach((img) => {
    emits("add-image", img.imageUrl);
  });
  selectedImages.value = [];
  dialogSelectImagesVisible.value = false;
}

function selectImagesBackground() {
  selectedImages.value.forEach((img) => {
    emits("add-image-background", img.imageUrl);
  });
  selectedImages.value = [];
  dialogSelectImagesBackgroundVisible.value = false;
}

// (8) Novo projeto
function emitNewProject() {
  emits("save-project", { ...newProjectForm.value });
  dialogNewProject.value = false;
  newProjectForm.value = {
    imageName: "",
    groupBy: "",
    description: "",
    type: "2",
  };
}

// (9) Abrir projeto
function openProjectDialog() {
  dialogSelectProjectVisible.value = true;
  loadProjects();
}
async function loadProjects() {
  loadingManage.value = true;
  loadingImages.value = true;
  try {
    const response = await axiosInstance.get("/image/add");
    images.value = response.data.content.images;
  } catch (error) {
    console.error("Erro ao carregar imagens:", error);
    $q.notify({ message: "Erro ao carregar imagens.", color: "negative" });
  }
  loadingImages.value = false;
  loadingManage.value = false;
}
function selectProject(proj) {
  selectedProject.value = proj;
}
function emitOpenProject() {
  if (selectedProject.value) {
    dialogSelectProjectVisible.value = false;
    emits("open-project", selectedProject.value);
    selectedProject.value = null;
  }
}

// (10) Upload de imagem local
function triggerFileInput() {
  imageInput.value.click();
}
function handleImageUpload(event) {
  const file = event.target.files[0];
  if (file) {
    const reader = new FileReader();
    reader.onload = (e) => {
      const imageUrl = e.target.result;
      emits("add-image", imageUrl);
    };
    reader.readAsDataURL(file);
  }
}

// (11) Filtrar ícones por categoria
function iconsByCategory(category) {
  return availableIcons.filter((icon) => icon.category === category);
}

// (12) Crop
function saveDesignCut() {
  emits("save-design-cut");
}

// (13) Configurações de Desenho de Linha
function updateLineType(type) {
  emits("update-line-type", type);
}
function updateLineColor() {
  emits("update-line-color", selectedLineColor.value);
}
function updateLineWidth() {
  emits("update-line-width", lineWidth.value);
}

// (14) Seleção de Figuras Geométricas
const dialogGeometrySelectorVisible = ref(false);
function openGeometrySelector() {
  dialogGeometrySelectorVisible.value = true;
}
function handleAddGeometryItem(type) {
  emits("add-item", type);
  dialogGeometrySelectorVisible.value = false;
}

// (15) Toggle do menu de desenho
function toggleDrawingMenu() {
  isDrawingMenuOpen.value = !isDrawingMenuOpen.value;
}
function selectDrawingMode(mode) {
  isDrawingMenuOpen.value = false;
  setDrawingMode(mode);
}
</script>

<style scoped>
/* Responsividade extra para a toolbar */
.toolbar-responsive {
  flex-wrap: wrap;
}
@media (max-width: 600px) {
  .toolbar-responsive {
    flex-direction: column;
    align-items: flex-start;
  }
}

/* Estilos para as imagens na toolbar */
.icon-image {
  width: 32px;
  height: 32px;
  object-fit: contain;
  transition: transform 0.2s ease-in-out;
}
.icon-image:hover {
  transform: scale(2.5);
}

.active-paint-mode {
  background-color: #e0f7fa;
}
.active-drawing-mode {
  background-color: #e0f7fa;
}

.q-item-section:last-child {
  display: flex;
  align-items: center;
}
.q-item-section > span {
  font-size: 14px;
  margin-left: 10px;
}

/* Container principal para cada imagem e nome */
.image-container {
  width: 120px;
  cursor: pointer;
  border: 1px solid #ccc;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8px;
  box-sizing: border-box;
  transition: box-shadow 0.2s ease-in-out;
}
.image-container:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}
.image-box {
  width: 100%;
  height: 100px;
  position: relative;
  overflow: hidden;
  border-radius: 4px;
  background-color: #f9f9f9;
}
.image-content {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.2s ease-in-out;
}
.image-content:hover {
  transform: scale(1.05);
}
.selected-icon {
  position: absolute;
  top: 5px;
  left: 5px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 50%;
}
.image-name {
  margin-top: 8px;
  text-align: center;
  font-size: 14px;
  word-wrap: break-word;
  color: #333;
}

/* Responsividade para dispositivos menores */
@media (max-width: 600px) {
  .image-container {
    width: 100px;
  }
  .image-box {
    height: 80px;
  }
  .image-name {
    font-size: 12px;
  }
}

.resize-handle:hover,
.rotate-handle:hover,
.skew-handle:hover,
.perspective-handle:hover {
  background-color: #000;
}

/* Bezier Points */
.anchor-point {
  cursor: pointer;
}
.control-point {
  cursor: pointer;
}

/* Estilos para os dialogs */
.modal-header {
  border-bottom: 1px solid #e0e0e0;
  padding-bottom: 10px;
}

/* Estilos para Configurações de Desenho de Linha */
.toolbar-section {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-left: 20px;
}

.toolbar-label {
  font-size: 14px;
  color: #555;
}

.toolbar-color-picker {
  width: 40px;
  height: 40px;
  padding: 0;
}

.toolbar-input {
  width: 60px;
}

.toolbar-select {
  width: 120px;
}

.drawing-modes-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
}

.drawing-mode-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 0;
  border: none;
  background: transparent;
  cursor: pointer;
  transition: transform 0.2s ease-in-out;
}

.drawing-mode-icon {
  margin-bottom: 4px;
  color: #000; /* Ajuste a cor conforme necessário */
}

.drawing-menu {
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  border-radius: 4px;
  overflow: hidden;
  z-index: 10;
  margin-top: 8px;
  width: 180px;
}

.drawing-menu-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.drawing-menu-item:hover {
  background-color: #f0f0f0;
}

.drawing-menu-item.active-drawing-mode {
  background-color: #e0f7fa;
}

.drawing-menu-item span {
  margin-left: 8px;
  font-size: 14px;
}
</style>
