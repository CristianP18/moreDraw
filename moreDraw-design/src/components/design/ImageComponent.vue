<template>
  <div class="q-pa-md container-imagem-yard">
    <!-- Botões Principais -->
    <div class="botoes-acoes">
      <q-btn
        color="primary"
        :label="$t('image.createNew')"
        class="q-mr-sm"
        :loading="loadingCreate"
        :disable="loadingCreate"
        @click="dialogCreateVisible = true"
      />
      <q-btn
        color="secondary"
        :label="$t('image.manageImages')"
        :loading="loadingManage"
        :disable="loadingManage"
        @click="openManageDialog"
      />
    </div>

    <!-- Diálogo de Criação -->
    <q-dialog v-model="dialogCreateVisible">
      <q-card class="modal-card">
        <q-card-section class="row items-center justify-between modal-header">
          <div class="text-h6">
            {{ $t("image.createTitle") }}
          </div>
          <q-btn
            flat
            icon="close"
            :disable="loadingCreate"
            @click="dialogCreateVisible = false"
          />
        </q-card-section>

        <q-card-section>
          <q-form @submit.prevent="createImage">
            <q-input
              v-model="createForm.imageName"
              :label="$t('image.imageName')"
              outlined
              required
              class="q-mb-md"
              :disable="loadingCreate"
            />
            <q-input
              v-model="createForm.groupBy"
              :label="$t('image.group')"
              outlined
              required
              class="q-mb-md"
              :disable="loadingCreate"
            />
            <q-input
              v-model="createForm.description"
              :label="$t('image.description')"
              outlined
              type="textarea"
              required
              class="q-mb-md"
              :disable="loadingCreate"
            />

            <!-- Upload da Imagem -->
            <q-file
              v-model="createSelectedFile"
              :label="$t('image.upload')"
              filled
              bottom-slots
              accept="image/*"
              class="q-mb-md"
              :disable="loadingCreate"
            >
              <template #prepend>
                <q-icon name="image" />
              </template>
            </q-file>

            <!-- Preview da Imagem -->
            <div v-if="previewImage">
              <img :src="previewImage" alt="Preview" class="imagem-preview" />
            </div>

            <q-card-actions align="right">
              <q-btn
                flat
                :label="$t('image.cancel')"
                :disable="loadingCreate"
                @click="dialogCreateVisible = false"
              />
              <q-btn
                color="primary"
                :label="$t('image.register')"
                type="submit"
                unelevated
                :loading="loadingCreate"
                :disable="loadingCreate"
              />
            </q-card-actions>
          </q-form>
        </q-card-section>
      </q-card>
    </q-dialog>

    <!-- Diálogo de Gerenciamento de Imagens -->
    <q-dialog v-model="dialogManageVisible" persistent>
      <q-card style="min-width: 500px; min-height: 300px">
        <q-card-section class="row items-center justify-between modal-header">
          <div class="text-h6">
            {{ $t("image.manageTitle") }}
          </div>
          <q-btn
            flat
            icon="close"
            :disable="loadingManage"
            @click="dialogManageVisible = false"
          />
        </q-card-section>

        <q-card-section>
          <div v-if="loadingImages" class="text-center q-pa-md">
            <q-spinner size="50px" />
          </div>
          <div v-else class="row wrap q-gutter-sm">
            <div
              v-for="img in images"
              :key="img.imageId"
              class="column items-center imagem-item"
              @dblclick="onImageDoubleClick(img)"
            >
              <div class="image-container" style="position: relative">
                <img
                  :src="getImageUrl(img.imageUrl)"
                  :alt="img.imageName"
                  class="imagem-preview-lista"
                />

                <!-- Botão de editar imagem, aparece se for a imagem selecionada no double click -->
                <q-btn
                  v-if="
                    imageToEditImage && imageToEditImage.imageId === img.imageId
                  "
                  size="sm"
                  flat
                  icon="edit"
                  color="primary"
                  style="
                    position: absolute;
                    top: 5px;
                    right: 5px;
                    background: rgba(255, 255, 255, 0.8);
                    border-radius: 50%;
                  "
                  :loading="
                    loadingUpdateImage &&
                    imageToEditImage.imageId === img.imageId
                  "
                  :disable="
                    loadingUpdateImage &&
                    imageToEditImage.imageId === img.imageId
                  "
                  @click.stop="openEditImageDialog(img)"
                />
              </div>
              <div class="row justify-between q-mt-xs w-100">
                <q-btn
                  icon="edit"
                  flat
                  size="sm"
                  :loading="loadingUpdate && editForm.imageId === img.imageId"
                  :disable="loadingUpdate"
                  @click.stop="editImageAttributes(img)"
                />
                <q-btn
                  icon="delete"
                  flat
                  size="sm"
                  color="negative"
                  :loading="
                    loadingDelete && imageToDelete?.imageId === img.imageId
                  "
                  :disable="loadingDelete"
                  @click.stop="confirmDeleteImage(img)"
                />
              </div>
            </div>
          </div>
        </q-card-section>
      </q-card>
    </q-dialog>

    <!-- Diálogo de Edição de Atributos -->
    <q-dialog v-model="dialogEditVisible">
      <q-card class="modal-card">
        <q-card-section class="row items-center justify-between modal-header">
          <div class="text-h6">
            {{ $t("image.editAttributes") }} - {{ editForm.imageName }}
          </div>
          <q-btn
            flat
            icon="close"
            :disable="loadingUpdate"
            @click="dialogEditVisible = false"
          />
        </q-card-section>

        <q-card-section>
          <q-form @submit.prevent="updateImage">
            <q-input
              v-model="editForm.imageName"
              :label="$t('image.imageName')"
              outlined
              required
              class="q-mb-md"
              :disable="loadingUpdate"
            />
            <q-input
              v-model="editForm.groupBy"
              :label="$t('image.group')"
              outlined
              required
              class="q-mb-md"
              :disable="loadingUpdate"
            />
            <q-input
              v-model="editForm.description"
              :label="$t('image.description')"
              outlined
              type="textarea"
              required
              class="q-mb-md"
              :disable="loadingUpdate"
            />

            <q-card-actions align="right">
              <q-btn
                flat
                :label="$t('image.cancel')"
                :disable="loadingUpdate"
                @click="dialogEditVisible = false"
              />
              <q-btn
                color="primary"
                :label="$t('image.save')"
                type="submit"
                unelevated
                :loading="loadingUpdate"
                :disable="loadingUpdate"
              />
            </q-card-actions>
          </q-form>
        </q-card-section>
      </q-card>
    </q-dialog>

    <!-- Diálogo de Edição de Imagem -->
    <q-dialog v-model="dialogEditImageVisible">
      <q-card class="modal-card">
        <q-card-section class="row items-center justify-between modal-header">
          <div class="text-h6">
            {{ $t("image.editImage") }} -
            {{ imageToEditImage ? imageToEditImage.imageName : "" }}
          </div>
          <q-btn
            flat
            icon="close"
            :disable="loadingUpdateImage"
            @click="dialogEditImageVisible = false"
          />
        </q-card-section>

        <q-card-section>
          <q-form @submit.prevent="updateImageImage">
            <q-file
              v-model="editImageFiles"
              :label="$t('image.selectNewImage')"
              filled
              bottom-slots
              :counter="true"
              accept="image/*"
              class="q-mb-md"
              :disable="loadingUpdateImage"
            >
              <template #prepend>
                <q-icon name="image" />
              </template>
            </q-file>

            <!-- Prévia da nova imagem (se houver upload) -->
            <div v-if="previewEditImage" class="q-mb-md">
              <img
                :src="previewEditImage"
                alt="Prévia Nova Imagem"
                class="imagem-preview"
              />
            </div>

            <q-card-actions align="right">
              <q-btn
                flat
                :label="$t('image.cancel')"
                :disable="loadingUpdateImage"
                @click="dialogEditImageVisible = false"
              />
              <q-btn
                color="primary"
                :label="$t('image.saveImage')"
                type="submit"
                unelevated
                :loading="loadingUpdateImage"
                :disable="loadingUpdateImage"
              />
            </q-card-actions>
          </q-form>
        </q-card-section>
      </q-card>
    </q-dialog>

    <!-- Diálogo de Exclusão -->
    <q-dialog v-model="dialogDeleteVisible">
      <q-card>
        <q-card-section class="row items-center justify-between modal-header">
          <div class="text-h6">
            {{ $t("image.confirmDelete") }}
          </div>
          <q-btn
            flat
            icon="close"
            :disable="loadingDelete"
            @click="dialogDeleteVisible = false"
          />
        </q-card-section>

        <q-card-section>
          <div>
            {{
              $t("image.deleteConfirmation", {
                imageName: imageToDelete ? imageToDelete.imageName : "",
              })
            }}
          </div>
        </q-card-section>

        <q-card-actions align="right">
          <q-btn
            flat
            :label="$t('image.cancel')"
            :disable="loadingDelete"
            @click="dialogDeleteVisible = false"
          />
          <q-btn
            flat
            :label="$t('image.delete')"
            color="negative"
            :loading="loadingDelete"
            :disable="loadingDelete"
            @click="deleteImage"
          />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </div>
</template>

<script setup>
import { ref, watch } from "vue";
import { useQuasar } from "quasar";
import { createAxiosInstance } from "../../api/axiosInstance";

const baseApiUrl = "";

// Instância do Quasar para notificações
const $q = useQuasar();

// Instância do Axios para a API 'yard'
const axiosInstance = createAxiosInstance("yard");

// Estados dos diálogos
const dialogCreateVisible = ref(false);
const dialogManageVisible = ref(false);
const dialogEditVisible = ref(false);
const dialogEditImageVisible = ref(false);
const dialogDeleteVisible = ref(false);

// Formulário de criação
const createForm = ref({
  imageName: "",
  groupBy: "",
  description: "",
});
const createSelectedFile = ref(null);
const previewImage = ref("");

// Estados de carregamento
const loadingCreate = ref(false);
const loadingManage = ref(false);
const loadingUpdate = ref(false);
const loadingUpdateImage = ref(false);
const loadingDelete = ref(false);

// Formulário de edição de atributos
const editForm = ref({
  imageId: null,
  imageName: "",
  groupBy: "",
  description: "",
});

// Edição de imagem separada
const imageToEditImage = ref(null);
const editImageFiles = ref(null);
const previewEditImage = ref("");

// Lista de imagens para gerenciamento
const images = ref([]);
const loadingImages = ref(false);

// Imagem a excluir
const imageToDelete = ref(null);

// Função auxiliar para montar URL da imagem
const getImageUrl = (url) => {
  if (!url) return "";
  if (url.startsWith("http")) {
    return url;
  }
  return baseApiUrl + url;
};

// Função para abrir o diálogo de gerenciamento e carregar imagens
const openManageDialog = async () => {
  dialogManageVisible.value = true;
  await loadImages();
};

// Carrega imagens existentes
const loadImages = async () => {
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
};

// Watch para preview de imagem no cadastro
watch(createSelectedFile, (newFile) => {
  if (newFile) {
    const file = Array.isArray(newFile) ? newFile[0] : newFile;
    previewImage.value = file ? URL.createObjectURL(file) : "";
  } else {
    previewImage.value = "";
  }
});

// Watch para preview de imagem na edição
watch(editImageFiles, (newFile) => {
  if (newFile) {
    const file = Array.isArray(newFile) ? newFile[0] : newFile;
    previewEditImage.value = file ? URL.createObjectURL(file) : "";
  } else {
    previewEditImage.value = "";
  }
});

// Criar Image (POST com form-data)
const createImage = async () => {
  loadingCreate.value = true;

  const file = Array.isArray(createSelectedFile.value)
    ? createSelectedFile.value[0]
    : createSelectedFile.value;

  if (!file) {
    $q.notify({ message: "Selecione uma imagem (upload).", color: "warning" });
    loadingCreate.value = false;
    return;
  }

  const fd = new FormData();
  fd.append("file", file);
  fd.append(
    "jsonImage",
    JSON.stringify({
      imageName: createForm.value.imageName,
      groupBy: createForm.value.groupBy,
      description: createForm.value.description,
    })
  );

  try {
    await axiosInstance.post("/image", fd, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    $q.notify({ message: "Image criado com sucesso!", color: "positive" });
    dialogCreateVisible.value = false;
    createForm.value = { imageName: "", groupBy: "", description: "" };
    createSelectedFile.value = null;
    previewImage.value = "";
    await loadImages();
  } catch (error) {
    console.error(
      "Erro ao criar Image:",
      error.response ? error.response.data : error
    );
    $q.notify({ message: "Erro ao criar Image.", color: "negative" });
  }

  loadingCreate.value = false;
};

// Abrir diálogo de edição de atributos
const editImageAttributes = (img) => {
  editForm.value = {
    imageId: img.imageId,
    imageName: img.imageName,
    groupBy: img.groupBy,
    description: img.description,
  };
  dialogEditVisible.value = true;
};

// Atualizar atributos do Image (PUT)
const updateImage = async () => {
  loadingUpdate.value = true;
  try {
    await axiosInstance.put(`/image/${editForm.value.imageId}`, {
      imageName: editForm.value.imageName,
      groupBy: editForm.value.groupBy,
      description: editForm.value.description,
    });
    $q.notify({
      message: "Atributos atualizados com sucesso!",
      color: "positive",
    });
    dialogEditVisible.value = false;
    await loadImages();
  } catch (error) {
    console.error("Erro ao atualizar atributos:", error);
    $q.notify({
      message: "Erro ao atualizar atributos do Image.",
      color: "negative",
    });
  }
  loadingUpdate.value = false;
};

// Duplo clique na imagem para seleção da imagem a editar
const onImageDoubleClick = (img) => {
  openEditImageDialog(img);
};

// Abrir diálogo de edição de imagem
const openEditImageDialog = (img) => {
  imageToEditImage.value = img;
  editImageFiles.value = null;
  previewEditImage.value = "";
  dialogEditImageVisible.value = true;
};

// Atualização da imagem via form-data (PUT)
const updateImageImage = async () => {
  loadingUpdateImage.value = true;

  if (!imageToEditImage.value) {
    $q.notify({
      message: "Nenhuma imagem selecionada para edição.",
      color: "warning",
    });
    loadingUpdateImage.value = false;
    return;
  }

  const file =
    editImageFiles.value && Array.isArray(editImageFiles.value)
      ? editImageFiles.value[0]
      : editImageFiles.value;

  if (!file) {
    $q.notify({
      message: "Selecione uma imagem para atualizar.",
      color: "warning",
    });
    loadingUpdateImage.value = false;
    return;
  }

  const fd = new FormData();
  fd.append("file", file);
  fd.append(
    "jsonImage",
    JSON.stringify({
      imageId: imageToEditImage.value.imageId,
    })
  );

  try {
    await axiosInstance.put(
      `/image/picture/${imageToEditImage.value.imageId}`,
      fd,
      {
        headers: { "Content-Type": "multipart/form-data" },
      }
    );
    $q.notify({ message: "Imagem atualizada com sucesso!", color: "positive" });
    dialogEditImageVisible.value = false;
    imageToEditImage.value = null;
    editImageFiles.value = null;
    previewEditImage.value = "";
    await loadImages();
  } catch (error) {
    console.error("Erro ao atualizar imagem:", error);
    $q.notify({
      message: "Erro ao atualizar imagem do Image.",
      color: "negative",
    });
  }
  loadingUpdateImage.value = false;
};

// Confirmar exclusão da imagem
const confirmDeleteImage = (img) => {
  imageToDelete.value = img;
  dialogDeleteVisible.value = true;
};

// Excluir Imagem (DELETE)
const deleteImage = async () => {
  loadingDelete.value = true;
  if (!imageToDelete.value) {
    loadingDelete.value = false;
    return;
  }
  try {
    await axiosInstance.delete(`/image/${imageToDelete.value.imageId}`);
    $q.notify({ message: "Imagem excluída com sucesso!", color: "positive" });
    dialogDeleteVisible.value = false;
    imageToDelete.value = null;
    await loadImages();
  } catch (error) {
    console.error("Erro ao excluir imagem:", error);
    $q.notify({ message: "Erro ao excluir imagem.", color: "negative" });
  }
  loadingDelete.value = false;
};
</script>

<style scoped>
.container-imagem-yard {
  background: #f5f5f5;
  border-radius: 8px;
  padding: 16px;
}

.botoes-acoes {
  margin-bottom: 16px;
  display: flex;
  gap: 8px;
}

.modal-card {
  width: 600px;
  max-width: 90vw;
}

.modal-header {
  background: #eeeeee;
  border-radius: 8px 8px 0 0;
}

.q-mb-md {
  margin-bottom: 1rem;
}

.imagem-preview {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border: 1px solid #ccc;
}

.imagem-preview-lista {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border: 1px solid #ccc;
}

.imagem-item {
  width: 100px;
  margin-bottom: 20px;
  position: relative;
  cursor: pointer;
}

.w-100 {
  width: 100%;
}
</style>
