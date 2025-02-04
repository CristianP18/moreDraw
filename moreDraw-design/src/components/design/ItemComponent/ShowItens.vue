<template>
    <div
        class="item-container"
        :style="containerStyle"
    >
        <!-- Renderização dos ícones filtrados -->
        <img
            v-for="(icon, index) in filteredIcons"
            :key="index"
            :src="icon.src"
            :alt="icon.alt"
            class="item-icon"
            :style="imageStyle"
        >

        <!-- Renderizar Imagem Específica -->
        <img
            v-if="item.type === 'image'"
            :src="item.imageUrl"
            class="container-box"
            :style="imageSpecificStyle"
        >
    </div>
</template>

<script setup>
import { computed } from 'vue';

// Importar todos os ícones necessários
import trailerIcon from 'src/assets/objects/truck02.svg';
import truckIcon from 'src/assets/objects/truck01.svg';
import yardIcon from 'src/assets/objects/yardSpaces.png';
import treeIcon from 'src/assets/objects/tree.png';
import wallDarkIcon from 'src/assets/objects/wall_dark.png';
import wallLightIcon from 'src/assets/objects/wall_light.png';
import arbustoIcon from 'src/assets/objects/Arbusto.png';
import asfaltoVerticalIcon from 'src/assets/objects/asfalto - vertical.png';
import asfaltoIcon from 'src/assets/objects/asfalto.png';
import calcadaHorizontalIcon from 'src/assets/objects/calçada_horizontal.png';
import calcadaIcon from 'src/assets/objects/calçada.png';
import canteiroHorizontalIcon from 'src/assets/objects/canteiro_horizontal.png';
import canteiroIcon from 'src/assets/objects/canteiro.png';
import ceuIcon from 'src/assets/objects/céu.jpg';
import divisaoVagasIcon from 'src/assets/objects/divisão de vagas.png';
import empresaIcon from 'src/assets/objects/Empresa.png';
import estradaCinzaIcon from 'src/assets/objects/estrada_cinza.png';
import estradaIcon from 'src/assets/objects/estrada.png';
import gramaIcon from 'src/assets/objects/Grama.png';
import gramadoDarkIcon from 'src/assets/objects/gramado_dark.png';
import greenGrasIcon from 'src/assets/objects/green_gras.png';
import image2Icon from 'src/assets/objects/image (2).png';
import image3Icon from 'src/assets/objects/image (3).png';
import image720Icon from 'src/assets/objects/image_720.png';
import imageFinalCleaned2Icon from 'src/assets/objects/image_final_cleaned_2.png';
import montanhaIcon from 'src/assets/objects/Montanha.png';
import muroIcon from 'src/assets/objects/muro.png';
import objectRetryProcessedIcon from 'src/assets/objects/object_retry_processed.png';
import paperTextureIcon from 'src/assets/objects/papertexture-2061711_1280.jpg';
import pastoIcon from 'src/assets/objects/pasto.png';
import patio02Icon from 'src/assets/objects/patio02.png';
import pessegueiroIcon from 'src/assets/objects/Pessegueiro.png';
import pond312465Icon from 'src/assets/objects/pond-312465_640.png';
import pond1346068Icon from 'src/assets/objects/pond-1346068_640.jpg';
import predioIcon from 'src/assets/objects/Predio.png';
import savanaIcon from 'src/assets/objects/Savana.png';
import telhadoZincoCopiaIcon from 'src/assets/objects/telhado_zinco - Copia.png';
import telhadoZincoIcon from 'src/assets/objects/telhado_zinco.png';
import texture1033755Icon from 'src/assets/objects/texture-1033755_1280.jpg';
import vagasIcon from 'src/assets/objects/vagas.png';
import vagas02Icon from 'src/assets/objects/vagas02.png';
import vagsTrucksLate3ralIcon from 'src/assets/objects/vagsTrucks_Late3ral.png';
import vagsTrucksIcon from 'src/assets/objects/vagsTrucks.png';
import vehicleNewRetryProcessedIcon from 'src/assets/objects/vehicle_new_retry_processed.png';
import yard03Icon from 'src/assets/objects/yard03.png';

// Definir as props
const props = defineProps({
    item: {
        type: Object,
        required: true,
    },
});

// Definir a lista de ícones
const iconList = [
    { type: 'trailer', src: trailerIcon, alt: 'Carreta' },
    { type: 'truck', src: truckIcon, alt: 'Caminhão' },
    { type: 'yard', src: yardIcon, alt: 'Yard' },
    { type: 'tree', src: treeIcon, alt: 'Árvore' },
    { type: 'wallDark', src: wallDarkIcon, alt: 'Parede Escura' },
    { type: 'wallLight', src: wallLightIcon, alt: 'Parede Clara' },
    { type: 'arbusto', src: arbustoIcon, alt: 'Arbusto' },
    { type: 'asfaltoVertical', src: asfaltoVerticalIcon, alt: 'Asfalto Vertical' },
    { type: 'asfalto', src: asfaltoIcon, alt: 'Asfalto' },
    { type: 'calcadaHorizontal', src: calcadaHorizontalIcon, alt: 'Calçada Horizontal' },
    { type: 'calcada', src: calcadaIcon, alt: 'Calçada' },
    { type: 'canteiroHorizontal', src: canteiroHorizontalIcon, alt: 'Canteiro Horizontal' },
    { type: 'canteiro', src: canteiroIcon, alt: 'Canteiro' },
    { type: 'ceu', src: ceuIcon, alt: 'Céu' },
    { type: 'divisaoVagas', src: divisaoVagasIcon, alt: 'Divisão de Vagas' },
    { type: 'empresa', src: empresaIcon, alt: 'Empresa' },
    { type: 'estradaCinza', src: estradaCinzaIcon, alt: 'Estrada Cinza' },
    { type: 'estrada', src: estradaIcon, alt: 'Estrada' },
    { type: 'grama', src: gramaIcon, alt: 'Grama' },
    { type: 'gramadoDark', src: gramadoDarkIcon, alt: 'Gramado Dark' },
    { type: 'greenGras', src: greenGrasIcon, alt: 'Green Gras' },
    { type: 'image2', src: image2Icon, alt: 'Image 2' },
    { type: 'image3', src: image3Icon, alt: 'Image 3' },
    { type: 'image720', src: image720Icon, alt: 'Image 720' },
    { type: 'imageFinalCleaned2', src: imageFinalCleaned2Icon, alt: 'Image Final Cleaned 2' },
    { type: 'montanha', src: montanhaIcon, alt: 'Montanha' },
    { type: 'muro', src: muroIcon, alt: 'Muro' },
    { type: 'objectRetryProcessed', src: objectRetryProcessedIcon, alt: 'Object Retry Processed' },
    { type: 'paperTexture', src: paperTextureIcon, alt: 'Paper Texture' },
    { type: 'pasto', src: pastoIcon, alt: 'Pasto' },
    { type: 'patio02', src: patio02Icon, alt: 'Patio 02' },
    { type: 'pessegueiro', src: pessegueiroIcon, alt: 'Pessegueiro' },
    { type: 'pond312465', src: pond312465Icon, alt: 'Pond 312465' },
    { type: 'pond1346068', src: pond1346068Icon, alt: 'Pond 1346068' },
    { type: 'predio', src: predioIcon, alt: 'Prédio' },
    { type: 'savana', src: savanaIcon, alt: 'Savana' },
    { type: 'telhadoZincoCopia', src: telhadoZincoCopiaIcon, alt: 'Telhado Zinco Copia' },
    { type: 'telhadoZinco', src: telhadoZincoIcon, alt: 'Telhado Zinco' },
    { type: 'texture1033755', src: texture1033755Icon, alt: 'Texture 1033755' },
    { type: 'vagas', src: vagasIcon, alt: 'Vagas' },
    { type: 'vagas02', src: vagas02Icon, alt: 'Vagas02' },
    { type: 'vagsTrucksLate3ral', src: vagsTrucksLate3ralIcon, alt: 'Vags Trucks Late3ral' },
    { type: 'vagsTrucks', src: vagsTrucksIcon, alt: 'Vags Trucks' },
    { type: 'vehicleNewRetryProcessed', src: vehicleNewRetryProcessedIcon, alt: 'Vehicle New Retry Processed' },
    { type: 'yard03', src: yard03Icon, alt: 'Yard03' },
];

// Computed property para filtrar os ícones com base no tipo do item
const filteredIcons = computed(() => iconList.filter((icon) => icon.type === props.item.type));
</script>

  <style scoped>
  .item-container {
      position: relative;
      width: 100%;
      height: 100%;
  }

  .item-icon {
      width: 100%;
      height: 100%;
  }

  .container-box {
      width: 100%;
      height: 100%;
  }
  </style>
