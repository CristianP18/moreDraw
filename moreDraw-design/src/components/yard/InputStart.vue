<template>
  <q-page>
    <q-toolbar>
      <!-- Seletores -->
      <div>
        <q-btn @click="toggleSelector('cds')" label="Selecionar CD" />
        <q-select
          v-if="isOpen.cds"
          v-model="selectedItem.cds"
          :options="formattedCdsList"
          label="Selecionar CD"
          emit-value
          @input="showDetails('cds')"
        />
      </div>

      <div>
        <q-btn
          @click="toggleSelector('deliveries')"
          label="Selecionar Delivery"
        />
        <q-select
          v-if="isOpen.deliveries"
          v-model="selectedItem.deliveries"
          :options="formattedDeliveriesList"
          label="Selecionar Delivery"
          emit-value
          @input="showDetails('deliveries')"
        />
      </div>

      <div>
        <q-btn
          @click="toggleSelector('destinators')"
          label="Selecionar Destinator"
        />
        <q-select
          v-if="isOpen.destinators"
          v-model="selectedItem.destinators"
          :options="formattedDestinatorsList"
          label="Selecionar Destinator"
          emit-value
          @input="showDetails('destinators')"
        />
      </div>

      <div>
        <q-btn @click="toggleSelector('docks')" label="Selecionar Dock" />
        <q-select
          v-if="isOpen.docks"
          v-model="selectedItem.docks"
          :options="formattedDocksList"
          label="Selecionar Dock"
          emit-value
          @input="showDetails('docks')"
        />
      </div>

      <div>
        <q-btn @click="toggleSelector('drivers')" label="Selecionar Driver" />
        <q-select
          v-if="isOpen.drivers"
          v-model="selectedItem.drivers"
          :options="formattedDriversList"
          label="Selecionar Driver"
          emit-value
          @input="showDetails('drivers')"
        />
      </div>

      <!-- Botão para abrir o componente DockConfig -->
      <q-btn
        label="Configuração"
        color="primary"
        class="q-ml-sm"
        @click="toggleConfig"
      />
    </q-toolbar>

    <!-- Exibir detalhes do item selecionado -->
    <div v-if="selectedItemDetails">
      <p><strong>Detalhes do Item Selecionado:</strong></p>
      <pre>{{ selectedItemDetails }}</pre>
    </div>

    <!-- Exibe o DockController ou DockConfig -->
    <div v-if="!showConfig">
      <DockController
        :docks="docksList"
        :deliveries="deliveriesList"
        :cds="cdsList"
        :destinators="destinatorsList"
        :drivers="driversList"
      />
    </div>
    <div v-else>
      <DockConfig :docks="docksList" @toggle-config="toggleConfig" />
    </div>
    <!-- Tabela de Rotas -->
    <RoutePlannerTable
      :routePlanner="routePlanner"
      @update-route-planner="updateRoutePlanner"
    />
  </q-page>
</template>

<script>
import axios from "axios";
import DockController from "./DockController.vue";
import DockConfig from "./DockConfig.vue";
import RoutePlannerTable from "./RoutePlannerTable.vue";

export default {
  components: {
    DockController,
    DockConfig,
  },
  data() {
    return {
      cdsList: [],
      deliveriesList: [],
      destinatorsList: [],
      docksList: [],
      driversList: [],
      selectedItem: {
        cds: null,
        deliveries: null,
        destinators: null,
        docks: null,
        drivers: null,
      },
      selectedItemDetails: null,
      isOpen: {
        cds: false,
        deliveries: false,
        destinators: false,
        docks: false,
        drivers: false,
      },
      showConfig: false, // Flag para alternar entre DockController e DockConfig
    };
  },
  computed: {
    formattedCdsList() {
      return this.cdsList.map((item) => ({
        label: item.name,
        value: item,
      }));
    },
    formattedDeliveriesList() {
      return this.deliveriesList.map((item) => ({
        label: `${item.origin} / ${item.destination}`,
        value: item,
      }));
    },
    formattedDestinatorsList() {
      return this.destinatorsList.map((item) => ({
        label: item.name,
        value: item,
      }));
    },
    formattedDocksList() {
      return this.docksList.map((item) => ({
        label: item.dockName,
        value: item,
      }));
    },
    formattedDriversList() {
      return this.driversList.map((item) => ({
        label: item.name,
        value: item,
      }));
    },
  },
  methods: {
    toggleSelector(type) {
      Object.keys(this.isOpen).forEach((key) => {
        this.isOpen[key] = key === type ? !this.isOpen[key] : false;
      });
    },
    showDetails(type) {
      this.selectedItemDetails = JSON.stringify(
        this.selectedItem[type],
        null,
        2
      );
    },
    toggleConfig() {
      this.showConfig = !this.showConfig;
    },
    async fetchData() {
      try {
        const responses = await Promise.all([
          axios.get("http://localhost:8082/api/cds"),
          axios.get("http://localhost:8082/api/deliveries"),
          axios.get("http://localhost:8082/api/destinators"),
          axios.get("http://localhost:8082/api/docks"),
          axios.get("http://localhost:8082/api/drivers"),
          axios.get("http://localhost:8082/api/vehicles"),
          axios.get("http://localhost:8082/api/trailers"),
        ]);

        this.cdsList = responses[0].data;
        this.deliveriesList = responses[1].data;
        this.destinatorsList = responses[2].data;
        this.docksList = responses[3].data;
        this.driversList = responses[4].data;

        console.log("Listas atualizadas:", {
          cdsList: this.cdsList,
          deliveriesList: this.deliveriesList,
          destinatorsList: this.destinatorsList,
          docksList: this.docksList,
          driversList: this.driversList,
        });
      } catch (error) {
        console.error("Erro ao buscar os dados: ", error);
      }
    },
  },
  mounted() {
    this.fetchData();
  },
};
</script>

<style scoped>
.q-toolbar {
  background-color: #1976d2;
  color: white;
  display: flex;
  flex-wrap: wrap;
}
.q-page {
  padding: 20px;
}
</style>
