<template>
  <div
    class="draggable-item"
    :data-id="item.id"
    :style="itemStyle"
    @mousedown="onMouseDown"
    @click.stop="toggleDimensions"
    @dblclick.stop="handleBringToFront"
    @contextmenu.prevent
  >
    <!-- =======================================================================
       0) ShowItens (para itens customizados que não são shapes básicos)
       ======================================================================= -->
    <ShowItens
      v-if="
        item.type !== 'label' &&
        item.type !== 'image' &&
        item.type !== 'polyline' &&
        item.type !== 'bezier' &&
        item.type !== 'circlePoliline' &&
        item.type !== 'squarePoliline' &&
        item.type !== 'circle' &&
        item.type !== 'triangle' &&
        item.type !== 'line' &&
        item.type !== 'star' &&
        item.type !== 'hexagon' &&
        item.type !== 'pentagon' &&
        item.type !== 'octagon' &&
        item.type !== 'diamond' &&
        item.type !== 'chevronLeft' &&
        item.type !== 'chevronRight' &&
        item.type !== 'chevronUp' &&
        item.type !== 'chevronDown' &&
        item.type !== 'halfStar' &&
        item.type !== 'rectangle' &&
        item.type !== 'container' &&
        item.type !== 'ellipse'
      "
      :item="item"
      class="container-box"
    />

    <!-- =======================================================================
       1) Polilinhas / Bezier / squarePoliline / circlePoliline
       ======================================================================= -->
    <div
      v-if="
        item.type === 'polyline' ||
        item.type === 'bezier' ||
        item.type === 'squarePoliline' ||
        item.type === 'circlePoliline'
      "
      class="svg-wrapper"
    >
      <svg
        ref="svgRef"
        :width="svgWidth"
        :height="svgHeight"
        :viewBox="`0 0 ${svgWidth} ${svgHeight}`"
        xmlns="http://www.w3.org/2000/svg"
        class="geometric-svg"
      >
        <!-- Definição de pattern se existir imagem de fundo -->
        <defs v-if="item.backgroundImage">
          <pattern
            :id="`bg-pattern-${item.id}`"
            patternUnits="userSpaceOnUse"
            :width="svgWidth"
            :height="svgHeight"
          >
            <image
              :href="item.backgroundImage"
              :width="svgWidth"
              :height="svgHeight"
              preserveAspectRatio="none"
            />
          </pattern>
        </defs>

        <!-- Polilinhas (polyline, squarePoliline, circlePoliline) -->
        <g
          v-if="
            item.type === 'polyline' ||
            item.type === 'squarePoliline' ||
            item.type === 'circlePoliline'
          "
        >
          <polyline
            :points="polylinePoints"
            :stroke-linecap="item.roundedLine ? 'round' : 'butt'"
            :stroke-linejoin="item.roundedLine ? 'round' : 'miter'"
            :stroke="item.strokeColor || 'black'"
            :stroke-width="item.strokeWidth || 2"
            :fill="
              item.backgroundImage
                ? 'url(#bg-pattern-' + item.id + ')'
                : item.color || 'transparent'
            "
          />
          <!-- Handles (se enableDimensions) -->
          <g v-if="enableDimensions">
            <circle
              v-for="(pt, idx) in item.path"
              :key="`pt-${idx}`"
              :cx="pt.x"
              :cy="pt.y"
              r="4"
              fill="red"
              stroke="white"
              stroke-width="1"
              @mousedown.stop="startPolylinePointDrag(idx, $event)"
            />
          </g>
        </g>

        <!-- Bezier -->
        <g v-else-if="item.type === 'bezier'">
          <path
            :d="bezierPathD"
            :stroke-linecap="item.roundedLine ? 'round' : 'butt'"
            :stroke-linejoin="item.roundedLine ? 'round' : 'miter'"
            :stroke="item.strokeColor || 'black'"
            :stroke-width="item.strokeWidth || 2"
            :fill="
              item.backgroundImage ? 'url(#bg-pattern-' + item.id + ')' : 'none'
            "
          />
          <!-- Handles (se enableDimensions) -->
          <g v-if="enableDimensions">
            <g
              v-for="(seg, sIndex) in item.segments"
              :key="`bezier-seg-${sIndex}`"
            >
              <line
                v-if="showBezierHandles"
                :x1="seg.x"
                :y1="seg.y"
                :x2="seg.cx1"
                :y2="seg.cy1"
                stroke="#888"
                stroke-dasharray="2,2"
              />
              <line
                v-if="showBezierHandles"
                :x1="seg.x"
                :y1="seg.y"
                :x2="seg.cx2"
                :y2="seg.cy2"
                stroke="#888"
                stroke-dasharray="2,2"
              />

              <!-- Âncora principal -->
              <circle
                class="anchor-point"
                :cx="seg.x"
                :cy="seg.y"
                r="4"
                fill="blue"
                stroke="white"
                stroke-width="1"
                @mousedown.stop="startBezierDrag(sIndex, 'anchor', $event)"
              />
              <!-- Handle 1 -->
              <circle
                v-if="showBezierHandles"
                class="control-point"
                :cx="seg.cx1"
                :cy="seg.cy1"
                r="3"
                fill="orange"
                stroke="white"
                stroke-width="1"
                @mousedown.stop="startBezierDrag(sIndex, 'c1', $event)"
              />
              <!-- Handle 2 -->
              <circle
                v-if="showBezierHandles"
                class="control-point"
                :cx="seg.cx2"
                :cy="seg.cy2"
                r="3"
                fill="green"
                stroke="white"
                stroke-width="1"
                @mousedown.stop="startBezierDrag(sIndex, 'c2', $event)"
              />
            </g>
          </g>
        </g>
      </svg>
    </div>

    <!-- =======================================================================
       2) Imagem
       ======================================================================= -->
    <img
      v-if="item.type === 'image'"
      :src="item.imageUrl"
      class="container-box"
      :style="imageSpecificStyle"
    />

    <!-- =======================================================================
       3) Modelo 3D
       ======================================================================= -->
    <ThreeContainer
      v-if="item.type === '3dModel'"
      class="container-3D"
      :model-url="item.modelUrl"
      :position="item.position"
      :rotation="item.rotation"
      :scale="item.scale"
    />

    <!-- =======================================================================
       4) Rótulo
       ======================================================================= -->
    <span
      v-else-if="item.type === 'label'"
      class="item-label"
      :style="{
        color: item.color || '#000000',
        fontSize: `${item.fontSize || 16}px`,
        width: 'auto',
        height: 'auto',
      }"
      @dblclick="enableLabelEditing"
    >
      <input
        v-if="isEditingLabel"
        :value="item.label"
        class="label-input"
        @input="onLabelInput($event.target.value)"
        @blur="disableLabelEditing"
        @keydown.enter="disableLabelEditing"
      />
      <span v-else>{{ item.label }}</span>
    </span>

    <!-- =======================================================================
       5) Círculo
       ======================================================================= -->
    <div v-else-if="item.type === 'circle'" class="svg-wrapper">
      <svg
        ref="svgRef"
        :width="item.width"
        :height="item.height"
        xmlns="http://www.w3.org/2000/svg"
        class="geometric-svg"
      >
        <!-- Defs para background -->
        <defs v-if="item.backgroundImage">
          <pattern
            :id="`bg-pattern-${item.id}`"
            patternUnits="userSpaceOnUse"
            :width="item.width"
            :height="item.height"
          >
            <image
              :href="item.backgroundImage"
              :width="item.width"
              :height="item.height"
              preserveAspectRatio="none"
            />
          </pattern>
        </defs>

        <circle
          :cx="item.width / 2"
          :cy="item.height / 2"
          :r="Math.min(item.width, item.height) / 2 - item.borderWidth"
          :fill="
            item.backgroundImage
              ? 'url(#bg-pattern-' + item.id + ')'
              : item.color || 'transparent'
          "
          :stroke="item.borderColor || 'black'"
          :stroke-width="item.borderWidth"
        />

        <!-- Handles (se enableDimensions) -->
        <g v-if="enableDimensions">
          <circle
            class="rotate-handle"
            :cx="item.width / 2"
            :cy="-10"
            r="5"
            fill="yellow"
            stroke="black"
            stroke-width="1"
            @mousedown.stop="onRotateMouseDown($event, 'rotation')"
          />
          <!-- Resize Handles -->
          <circle
            class="resize-handle top"
            :cx="item.width / 2"
            :cy="0"
            r="5"
            fill="white"
            stroke="black"
            stroke-width="1"
            @mousedown.stop="onResizeMouseDown('top', $event)"
          />
          <circle
            class="resize-handle bottom"
            :cx="item.width / 2"
            :cy="item.height"
            r="5"
            fill="white"
            stroke="black"
            stroke-width="1"
            @mousedown.stop="onResizeMouseDown('bottom', $event)"
          />
          <circle
            class="resize-handle left"
            :cx="0"
            :cy="item.height / 2"
            r="5"
            fill="white"
            stroke="black"
            stroke-width="1"
            @mousedown.stop="onResizeMouseDown('left', $event)"
          />
          <circle
            class="resize-handle right"
            :cx="item.width"
            :cy="item.height / 2"
            r="5"
            fill="white"
            stroke="black"
            stroke-width="1"
            @mousedown.stop="onResizeMouseDown('right', $event)"
          />
        </g>
      </svg>
    </div>

    <!-- =======================================================================
       5.1) Elipse
       ======================================================================= -->
    <div v-else-if="item.type === 'ellipse'" class="svg-wrapper">
      <svg
        ref="svgRef"
        :width="item.width"
        :height="item.height"
        xmlns="http://www.w3.org/2000/svg"
        class="geometric-svg"
      >
        <!-- Defs para background -->
        <defs v-if="item.backgroundImage">
          <pattern
            :id="`bg-pattern-${item.id}`"
            patternUnits="userSpaceOnUse"
            :width="item.width"
            :height="item.height"
          >
            <image
              :href="item.backgroundImage"
              :width="item.width"
              :height="item.height"
              preserveAspectRatio="none"
            />
          </pattern>
        </defs>

        <ellipse
          :cx="item.width / 2"
          :cy="item.height / 2"
          :rx="item.width / 2 - item.borderWidth"
          :ry="item.height / 2 - item.borderWidth"
          :fill="
            item.backgroundImage
              ? 'url(#bg-pattern-' + item.id + ')'
              : item.color || 'transparent'
          "
          :stroke="item.borderColor || 'black'"
          :stroke-width="item.borderWidth"
        />

        <!-- Handles (se enableDimensions) -->
        <g v-if="enableDimensions">
          <circle
            class="rotate-handle"
            :cx="item.width / 2"
            :cy="-10"
            r="5"
            fill="yellow"
            stroke="black"
            stroke-width="1"
            @mousedown.stop="onRotateMouseDown($event, 'rotation')"
          />
          <!-- Resize Handles -->
          <circle
            class="resize-handle top"
            :cx="item.width / 2"
            :cy="0"
            r="5"
            fill="white"
            stroke="black"
            stroke-width="1"
            @mousedown.stop="onResizeMouseDown('top', $event)"
          />
          <circle
            class="resize-handle bottom"
            :cx="item.width / 2"
            :cy="item.height"
            r="5"
            fill="white"
            stroke="black"
            stroke-width="1"
            @mousedown.stop="onResizeMouseDown('bottom', $event)"
          />
          <circle
            class="resize-handle left"
            :cx="0"
            :cy="item.height / 2"
            r="5"
            fill="white"
            stroke="black"
            stroke-width="1"
            @mousedown.stop="onResizeMouseDown('left', $event)"
          />
          <circle
            class="resize-handle right"
            :cx="item.width"
            :cy="item.height / 2"
            r="5"
            fill="white"
            stroke="black"
            stroke-width="1"
            @mousedown.stop="onResizeMouseDown('right', $event)"
          />
        </g>
      </svg>
    </div>

    <!-- =======================================================================
       6) Triângulo
       ======================================================================= -->
    <div v-else-if="item.type === 'triangle'" class="svg-wrapper">
      <svg
        ref="svgRef"
        :width="item.width"
        :height="item.height"
        xmlns="http://www.w3.org/2000/svg"
        class="geometric-svg"
      >
        <!-- Defs para background -->
        <defs v-if="item.backgroundImage">
          <pattern
            :id="`bg-pattern-${item.id}`"
            patternUnits="userSpaceOnUse"
            :width="item.width"
            :height="item.height"
          >
            <image
              :href="item.backgroundImage"
              :width="item.width"
              :height="item.height"
              preserveAspectRatio="none"
            />
          </pattern>
        </defs>

        <polygon
          :points="`0,${item.height} ${item.width / 2},0 ${item.width},${
            item.height
          }`"
          :fill="
            item.backgroundImage
              ? 'url(#bg-pattern-' + item.id + ')'
              : item.color || 'transparent'
          "
          :stroke="item.borderColor || 'black'"
          :stroke-width="item.borderWidth"
        />

        <!-- Handles (se enableDimensions) -->
        <g v-if="enableDimensions">
          <circle
            class="rotate-handle"
            :cx="item.width / 2"
            :cy="-10"
            r="5"
            fill="yellow"
            stroke="black"
            stroke-width="1"
            @mousedown.stop="onRotateMouseDown($event, 'rotation')"
          />
          <!-- Resize Handles -->
          <circle
            class="resize-handle top-left"
            :cx="0"
            :cy="0"
            r="5"
            fill="white"
            stroke="black"
            stroke-width="1"
            @mousedown.stop="onResizeMouseDown('top-left', $event)"
          />
          <circle
            class="resize-handle bottom-left"
            :cx="0"
            :cy="item.height"
            r="5"
            fill="white"
            stroke="black"
            stroke-width="1"
            @mousedown.stop="onResizeMouseDown('bottom-left', $event)"
          />
          <circle
            class="resize-handle bottom-right"
            :cx="item.width"
            :cy="item.height"
            r="5"
            fill="white"
            stroke="black"
            stroke-width="1"
            @mousedown.stop="onResizeMouseDown('bottom-right', $event)"
          />
        </g>
      </svg>
    </div>

    <!-- =======================================================================
       7) Linha
       ======================================================================= -->
    <div v-else-if="item.type === 'line'" class="svg-wrapper">
      <svg
        ref="svgRef"
        :width="itemWidth"
        :height="itemHeight"
        :style="svgStyle"
        xmlns="http://www.w3.org/2000/svg"
        class="geometric-svg"
      >
        <!-- Definição de pattern se existir imagem de fundo -->
        <defs v-if="item.backgroundImage">
          <pattern
            :id="`bg-pattern-${item.id}`"
            patternUnits="userSpaceOnUse"
            :width="itemWidth"
            :height="itemHeight"
          >
            <image
              :href="item.backgroundImage"
              :width="itemWidth"
              :height="itemHeight"
              preserveAspectRatio="none"
            />
          </pattern>
        </defs>

        <line
          :x1="0"
          :y1="0"
          :x2="lineDeltaX"
          :y2="lineDeltaY"
          :stroke="item.strokeColor || 'black'"
          :stroke-width="item.strokeWidth || 2"
          :stroke-dasharray="item.dashed ? '5,5' : 'none'"
          :stroke-linecap="'round'"
          :stroke-linejoin="'round'"
          :stroke-opacity="item.opacity || 1"
          :fill="
            item.backgroundImage ? 'url(#bg-pattern-' + item.id + ')' : 'none'
          "
        />

        <!-- Handles (se enableDimensions) -->
        <g v-if="enableDimensions">
          <circle
            class="rotate-handle"
            :cx="lineDeltaX / 2"
            :cy="-10"
            r="5"
            fill="yellow"
            stroke="black"
            stroke-width="1"
            @mousedown.stop="onRotateMouseDown($event, 'rotation')"
          />

          <circle
            class="resize-handle-start"
            :cx="0"
            :cy="0"
            r="5"
            fill="white"
            stroke="black"
            stroke-width="1"
            @mousedown.stop="onResizeMouseDown('start', $event)"
          />
          <circle
            class="resize-handle-end"
            :cx="lineDeltaX"
            :cy="lineDeltaY"
            r="5"
            fill="white"
            stroke="black"
            stroke-width="1"
            @mousedown.stop="onResizeMouseDown('end', $event)"
          />
        </g>
      </svg>
    </div>

    <!-- =======================================================================
       8) Figuras Geométricas Estendidas (Star, Hexagon, Pentagon, Rectangle, Container, etc.)
       ======================================================================= -->
    <div v-else-if="isGeometricFigureExtended" class="svg-wrapper">
      <svg
        ref="svgRef"
        :width="item.width"
        :height="item.height"
        xmlns="http://www.w3.org/2000/svg"
        class="geometric-svg"
      >
        <!-- Defs para background -->
        <defs v-if="item.backgroundImage">
          <pattern
            :id="`bg-pattern-${item.id}`"
            patternUnits="userSpaceOnUse"
            :width="item.width"
            :height="item.height"
          >
            <image
              :href="item.backgroundImage"
              :width="item.width"
              :height="item.height"
              preserveAspectRatio="none"
            />
          </pattern>
        </defs>

        <!-- Implementação para 'rectangle' -->
        <g v-if="item.type === 'rectangle'">
          <rect
            :width="item.width"
            :height="item.height"
            :fill="
              item.backgroundImage
                ? 'url(#bg-pattern-' + item.id + ')'
                : item.color || 'transparent'
            "
            :stroke="item.borderColor || 'black'"
            :stroke-width="item.borderWidth"
          />
          <!-- Handles (se enableDimensions) -->
          <g v-if="enableDimensions">
            <circle
              class="rotate-handle"
              :cx="item.width / 2"
              :cy="-10"
              r="5"
              fill="yellow"
              stroke="black"
              stroke-width="1"
              @mousedown.stop="onRotateMouseDown($event, 'rotation')"
            />
            <!-- Resize Handles -->
            <circle
              class="resize-handle top-left"
              :cx="0"
              :cy="0"
              r="5"
              fill="white"
              stroke="black"
              stroke-width="1"
              @mousedown.stop="onResizeMouseDown('top-left', $event)"
            />
            <circle
              class="resize-handle top-right"
              :cx="item.width"
              :cy="0"
              r="5"
              fill="white"
              stroke="black"
              stroke-width="1"
              @mousedown.stop="onResizeMouseDown('top-right', $event)"
            />
            <circle
              class="resize-handle bottom-left"
              :cx="0"
              :cy="item.height"
              r="5"
              fill="white"
              stroke="black"
              stroke-width="1"
              @mousedown.stop="onResizeMouseDown('bottom-left', $event)"
            />
            <circle
              class="resize-handle bottom-right"
              :cx="item.width"
              :cy="item.height"
              r="5"
              fill="white"
              stroke="black"
              stroke-width="1"
              @mousedown.stop="onResizeMouseDown('bottom-right', $event)"
            />
          </g>
        </g>

        <!-- Implementação para 'container' -->
        <g v-else-if="item.type === 'container'">
          <rect
            :width="item.width"
            :height="item.height"
            :fill="
              item.backgroundImage
                ? 'url(#bg-pattern-' + item.id + ')'
                : item.color || 'transparent'
            "
            :stroke="item.borderColor || 'black'"
            :stroke-width="item.borderWidth"
          />
          <!-- Handles (se enableDimensions) -->
          <g v-if="enableDimensions">
            <circle
              class="rotate-handle"
              :cx="item.width / 2"
              :cy="-10"
              r="5"
              fill="yellow"
              stroke="black"
              stroke-width="1"
              @mousedown.stop="onRotateMouseDown($event, 'rotation')"
            />
            <!-- Resize Handles -->
            <circle
              class="resize-handle top-left"
              :cx="0"
              :cy="0"
              r="5"
              fill="white"
              stroke="black"
              stroke-width="1"
              @mousedown.stop="onResizeMouseDown('top-left', $event)"
            />
            <circle
              class="resize-handle top-right"
              :cx="item.width"
              :cy="0"
              r="5"
              fill="white"
              stroke="black"
              stroke-width="1"
              @mousedown.stop="onResizeMouseDown('top-right', $event)"
            />
            <circle
              class="resize-handle bottom-left"
              :cx="0"
              :cy="item.height"
              r="5"
              fill="white"
              stroke="black"
              stroke-width="1"
              @mousedown.stop="onResizeMouseDown('bottom-left', $event)"
            />
            <circle
              class="resize-handle bottom-right"
              :cx="item.width"
              :cy="item.height"
              r="5"
              fill="white"
              stroke="black"
              stroke-width="1"
              @mousedown.stop="onResizeMouseDown('bottom-right', $event)"
            />
          </g>
        </g>

        <!-- Outras Figuras Geométricas -->
        <g v-else>
          <polygon
            :points="
              getPolygonPoints(item.type, item.width, item.height) || '0,0'
            "
            :fill="
              item.backgroundImage
                ? 'url(#bg-pattern-' + item.id + ')'
                : item.color || 'transparent'
            "
            :stroke="item.borderColor || 'black'"
            :stroke-width="item.borderWidth"
          />

          <!-- Handles (se enableDimensions) -->
          <g v-if="enableDimensions">
            <circle
              class="rotate-handle"
              :cx="item.width / 2"
              :cy="-10"
              r="5"
              fill="yellow"
              stroke="black"
              stroke-width="1"
              @mousedown.stop="onRotateMouseDown($event, 'rotation')"
            />

            <!-- Resize Handles -->
            <circle
              v-for="(handle, index) in resizeHandles"
              :key="`resize-${index}`"
              :cx="handle.cx"
              :cy="handle.cy"
              r="5"
              fill="white"
              stroke="black"
              stroke-width="1"
              @mousedown.stop="onResizeMouseDown(handle.direction, $event)"
            />
          </g>
        </g>
      </svg>
    </div>

    <!-- =======================================================================
       9) Handles de Manipulação (Rotação, Redimensionamento, Skew, Perspectiva)
       ======================================================================= -->
    <template
      v-if="
        enableDimensions &&
        !['polyline', 'bezier', 'squarePoliline', 'circlePoliline'].includes(
          item.type
        )
      "
    >
      <!-- Girar Y-axis -->
      <div
        class="rotate-handle y-axis"
        @mousedown.stop.prevent="onRotateMouseDown($event, 'y')"
      />
      <!-- Resize corners -->
      <div
        class="resize-handle top-left"
        @mousedown.stop.prevent="onResizeMouseDown('top-left', $event)"
      />
      <div
        class="resize-handle top-right"
        @mousedown.stop.prevent="onResizeMouseDown('top-right', $event)"
      />
      <div
        class="resize-handle bottom-left"
        @mousedown.stop.prevent="onResizeMouseDown('bottom-left', $event)"
      />
      <div
        class="resize-handle bottom-right"
        @mousedown.stop.prevent="onResizeMouseDown('bottom-right', $event)"
      />
      <div
        class="resize-handle top"
        @mousedown.stop.prevent="onResizeMouseDown('top', $event)"
      />
      <div
        class="resize-handle bottom"
        @mousedown.stop.prevent="onResizeMouseDown('bottom', $event)"
      />
      <div
        class="resize-handle left"
        @mousedown.stop.prevent="onResizeMouseDown('left', $event)"
      />
      <div
        class="resize-handle right"
        @mousedown.stop.prevent="onResizeMouseDown('right', $event)"
      />

      <!-- Rotação central -->
      <div
        class="rotate-handle"
        @mousedown.stop.prevent="onRotateMouseDown($event, 'rotation')"
      />

      <!-- Skew -->
      <div
        class="skew-handle skew-x"
        @mousedown.stop.prevent="onSkewMouseDown('skewX', $event)"
      />
      <div
        class="skew-handle skew-y"
        @mousedown.stop.prevent="onSkewMouseDown('skewY', $event)"
      />

      <!-- Perspectiva -->
      <div
        class="perspective-handle top-left"
        @mousedown.stop.prevent="onPerspectiveMouseDown('topLeft', $event)"
      />
      <div
        class="perspective-handle top-right"
        @mousedown.stop.prevent="onPerspectiveMouseDown('topRight', $event)"
      />
      <div
        class="perspective-handle bottom-left"
        @mousedown.stop.prevent="onPerspectiveMouseDown('bottomLeft', $event)"
      />
      <div
        class="perspective-handle bottom-right"
        @mousedown.stop.prevent="onPerspectiveMouseDown('bottomRight', $event)"
      />
    </template>

    <!-- =======================================================================
       10) Card de Configuração Flutuante
       ======================================================================= -->
    <div
      v-if="isSelected && isCardOpen"
      class="config-card"
      :style="{
        top: cardPosition.y + 'px',
        left: cardPosition.x + 'px',
        width: '300px',
      }"
      @mousedown.stop.prevent="startDragCard"
    >
      <div class="card-header">
        <span>Configurações do Item</span>
        <button @click="closeCard">×</button>
      </div>
      <div class="card-body">
        <!-- Propriedades Gerais -->
        <div class="property-group">
          <label>Rotação (graus):</label>
          <input
            v-model.number="config.rotation"
            type="number"
            @input="updateConfig"
          />
        </div>
        <div class="property-group">
          <label>Skew X (graus):</label>
          <input
            v-model.number="config.skewX"
            type="number"
            @input="updateConfig"
          />
        </div>
        <div class="property-group">
          <label>Skew Y (graus):</label>
          <input
            v-model.number="config.skewY"
            type="number"
            @input="updateConfig"
          />
        </div>
        <div class="property-group">
          <label>Largura (px):</label>
          <input
            v-model.number="config.width"
            type="number"
            @input="updateConfig"
          />
        </div>
        <div class="property-group">
          <label>Altura (px):</label>
          <input
            v-model.number="config.height"
            type="number"
            @input="updateConfig"
          />
        </div>
        <div class="property-group">
          <label>Cor:</label>
          <input v-model="config.color" type="color" @input="updateConfig" />
        </div>
        <div class="property-group">
          <label>Largura da Borda (px):</label>
          <input
            v-model.number="config.borderWidth"
            type="number"
            @input="updateConfig"
          />
        </div>
        <div class="property-group">
          <label>Cor da Borda:</label>
          <input
            v-model="config.borderColor"
            type="color"
            @input="updateConfig"
          />
        </div>

        <!-- Propriedades de Background Image -->
        <div class="property-group">
          <label>Imagem de Fundo (URL):</label>
          <input
            v-model="config.backgroundImage"
            type="text"
            placeholder="URL da Imagem"
            @input="updateConfig"
          />
        </div>

        <!-- Propriedades de Perspectiva -->
        <div class="property-group">
          <label>Rotação X (graus):</label>
          <input
            v-model.number="config.rotateX"
            type="number"
            @input="updateConfig"
          />
        </div>
        <div class="property-group">
          <label>Rotação Y (graus):</label>
          <input
            v-model.number="config.rotateY"
            type="number"
            @input="updateConfig"
          />
        </div>

        <!-- Curvatura (para polylines/beziers) -->
        <div
          v-if="
            item.type === 'bezier' ||
            item.type === 'polyline' ||
            item.type === 'squarePoliline' ||
            item.type === 'circlePoliline'
          "
          class="property-group"
        >
          <button style="margin-bottom: 6px" @click="toggleCurvesInputs">
            {{
              showCurvesInputs
                ? "Esconder Controladores"
                : "Mostrar Controladores"
            }}
          </button>

          <!-- Checkbox para alternar "arredondado" -->
          <div style="margin-bottom: 6px">
            <label>
              <!-- Removido v-model -->
              <input
                type="checkbox"
                :checked="item.roundedLine"
                @change="onRoundedLineChange($event.target.checked)"
              />
              Arredondar a linha
            </label>
          </div>

          <div v-if="showCurvesInputs">
            <label>Controladores de Curva:</label>
            <input
              type="number"
              min="2"
              :value="controlPointsCount"
              @change="updateControlPoints($event.target.value)"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
/* ===========================================================
   IMPORTS
=========================================================== */
import { computed, ref, watch } from "vue";
import ShowItens from "./ShowItens.vue";
import ThreeContainer from "./ThreeContainer.vue";

/* ===========================================================
   PROPS E EMITS
=========================================================== */
const props = defineProps({
  item: Object, // Objeto do item desenhado
  zoomLevel: {
    type: Number,
    default: 1,
  },
  panOffset: {
    type: Object,
    default: () => ({ x: 0, y: 0 }),
  },
  selectedItem: Object, // Item atualmente selecionado
  paintMode: {
    type: Boolean,
    default: false,
  },
  selectedColor: {
    type: String,
    default: "#000000",
  },
  roundedLine: {
    type: Boolean,
    default: false,
  },
  showControllers: {
    type: Boolean,
    default: false,
  },
  controllerMode: {
    type: String,
    default: "dimension", // 'dimension', 'curvature', 'none'
  },
  controllerCount: {
    type: Number,
    default: 2,
  },
});

const emits = defineEmits([
  "select-item",
  "update-item",
  "bring-to-front",
  "send-to-back",
]);

/* ===========================================================
   REFS E VARIÁVEIS REATIVAS
=========================================================== */
const svgRef = ref(null);
const enableDimensions = ref(false);
const showBezierHandles = ref(false);
const isEditingLabel = ref(false);
const isSelected = computed(
  () => props.selectedItem && props.selectedItem.id === props.item.id
);

const isCardOpen = ref(false);
const cardPosition = ref({ x: 100, y: 100 });
const dragStartCard = ref({ x: 0, y: 0 });

// Estado local do item para o Card de Configurações
const config = ref({
  rotation: props.item.rotation || 0,
  skewX: props.item.skewX || 0,
  skewY: props.item.skewY || 0,
  width: props.item.width || 100,
  height: props.item.height || 100,
  color: props.item.color || "#ffffff",
  borderWidth: props.item.borderWidth || 0,
  borderColor: props.item.borderColor || "#000000",
  rotateX: props.item.rotateX || 0,
  rotateY: props.item.rotateY || 0,
  backgroundImage: props.item.backgroundImage || "",
});

/* ===========================================================
   FUNÇÃO PARA "ARREDONDAR A LINHA" SEM v-model
=========================================================== */
function onRoundedLineChange(checked) {
  // Cria um novo item, atualizando a prop
  const updated = { ...props.item, roundedLine: checked };
  emits("update-item", updated);
}

/* ===========================================================
   FIGURAS ESTENDIDAS
=========================================================== */
const isGeometricFigureExtended = computed(() => {
  const extendedTypes = [
    "star",
    "hexagon",
    "pentagon",
    "octagon",
    "diamond",
    "rhombus",
    "parallelogram",
    "trapezoid",
    "cross",
    "chevronLeft",
    "chevronRight",
    "chevronUp",
    "chevronDown",
    "halfStar",
    "arc",
    "heart",
    "plus",
    "moon",
    "gear",
    "waveShape",
    "archimedeanSpiral",
    "zigzag",
    "cube",
    "cylinder",
    "sphere",
    "cone",
    "torus",
    "pyramid",
    "prism",
    "helix",
    "spiral",
    "crescent",
    "flower",
    "sun",
    "cloud",
    "bolt",
    "rectangle",
    "container",
    "ellipse",
  ];
  return extendedTypes.includes(props.item.type);
});

/* ===========================================================
   TAMANHO (SVG) QUANDO LINE, POLYLINE, ETC.
=========================================================== */
const svgWidth = computed(() => {
  // Polilinhas (inclui squarePoliline, circlePoliline)
  if (
    (props.item.type === "polyline" ||
      props.item.type === "squarePoliline" ||
      props.item.type === "circlePoliline") &&
    props.item.path
  ) {
    const xs = props.item.path.map((pt) => pt.x);
    return (Math.max(...xs) || 0) + 50;
  }
  // Bezier
  if (props.item.type === "bezier" && props.item.segments) {
    const allX = props.item.segments.flatMap((seg) => [
      seg.x,
      seg.cx1,
      seg.cx2,
    ]);
    return (Math.max(...allX) || 0) + 50;
  }
  // Figuras
  if (isGeometricFigureExtended.value) {
    return props.item.width || 100;
  }
  // Linha
  if (props.item.type === "line") {
    return itemWidth.value;
  }
  return 100;
});

const svgHeight = computed(() => {
  // Polilinhas
  if (
    (props.item.type === "polyline" ||
      props.item.type === "squarePoliline" ||
      props.item.type === "circlePoliline") &&
    props.item.path
  ) {
    const ys = props.item.path.map((pt) => pt.y);
    return (Math.max(...ys) || 0) + 50;
  }
  // Bezier
  if (props.item.type === "bezier" && props.item.segments) {
    const allY = props.item.segments.flatMap((seg) => [
      seg.y,
      seg.cy1,
      seg.cy2,
    ]);
    return (Math.max(...allY) || 0) + 50;
  }
  // Figuras
  if (isGeometricFigureExtended.value) {
    return props.item.height || 100;
  }
  // Linha
  if (props.item.type === "line") {
    return itemHeight.value;
  }
  return 100;
});

/* ===========================================================
   POLILINE / BEZIER
=========================================================== */
const bezierPathD = computed(() => {
  if (props.item.type !== "bezier" || !props.item.segments?.length) return "";
  const segs = props.item.segments;
  let d = `M ${segs[0].x} ${segs[0].y}`;
  for (let i = 1; i < segs.length; i++) {
    const prev = segs[i - 1];
    const cur = segs[i];
    d += ` C ${prev.cx2} ${prev.cy2}, ${cur.cx1} ${cur.cy1}, ${cur.x} ${cur.y}`;
  }
  return d;
});

const polylinePoints = computed(() => {
  if (
    props.item.type !== "polyline" &&
    props.item.type !== "squarePoliline" &&
    props.item.type !== "circlePoliline"
  ) {
    return "";
  }
  if (!props.item.path) return "";
  return props.item.path.map((pt) => `${pt.x},${pt.y}`).join(" ");
});

/* ===========================================================
   GET POLYGON POINTS (FIGURAS ESTENDIDAS)
=========================================================== */
function getPolygonPoints(type, width, height) {
  // Se for polilinhas "especiais"
  if (type === "circlePoliline" || type === "squarePoliline") {
    return "";
  }

  if (!width || !height) {
    console.warn("getPolygonPoints: width ou height inválidos.", {
      width,
      height,
    });
    return "";
  }

  const centerX = width / 2;
  const centerY = height / 2;
  const radius = Math.min(centerX, centerY) - 5;

  let points = "";
  let sides = 0;

  switch (type) {
    case "star": {
      sides = 5;
      const outerRadius = radius;
      const innerRadius = radius / 2;
      points = createStarPoints(
        centerX,
        centerY,
        outerRadius,
        innerRadius,
        sides
      );
      break;
    }

    case "halfStar": {
      sides = 5;
      const outerRadius = radius;
      const innerRadius = radius / 2;
      // fillFactor de 0.5
      points = createStarPoints(
        centerX,
        centerY,
        outerRadius,
        innerRadius,
        sides,
        0.5
      );
      break;
    }

    case "hexagon": {
      sides = 6;
      points = createRegularPolygonPoints(centerX, centerY, radius, sides);
      break;
    }

    case "pentagon": {
      sides = 5;
      points = createRegularPolygonPoints(centerX, centerY, radius, sides);
      break;
    }

    case "octagon": {
      sides = 8;
      points = createRegularPolygonPoints(centerX, centerY, radius, sides);
      break;
    }

    case "diamond":
    case "rhombus": {
      points = `${centerX},0 ${width},${centerY} ${centerX},${height} 0,${centerY}`;
      break;
    }

    case "parallelogram": {
      const skewOffset = width * 0.2;
      points = `${skewOffset},0 ${width},0 ${
        width - skewOffset
      },${height} 0,${height}`;
      break;
    }

    case "trapezoid": {
      points = `${width * 0.25},0 ${
        width * 0.75
      },0 ${width},${height} 0,${height}`;
      break;
    }

    case "chevronLeft": {
      points = `
      ${width * 0.6},0
      ${width * 0.4},0
      0,${height / 2}
      ${width * 0.4},${height}
      ${width * 0.6},${height}
    `;
      break;
    }

    case "chevronRight": {
      points = `
      ${width * 0.4},0
      ${width * 0.6},0
      ${width},${height / 2}
      ${width * 0.6},${height}
      ${width * 0.4},${height}
    `;
      break;
    }

    case "chevronUp": {
      points = `
      0,${height * 0.6}
      0,${height * 0.4}
      ${width / 2},0
      ${width},${height * 0.4}
      ${width},${height * 0.6}
    `;
      break;
    }

    case "chevronDown": {
      points = `
      0,${height * 0.4}
      0,${height * 0.6}
      ${width / 2},${height}
      ${width},${height * 0.6}
      ${width},${height * 0.4}
    `;
      break;
    }

    case "zigzag": {
      const zigzagCount = 5;
      const zigzagWidth = width / zigzagCount;
      const zigzagPoints = [];
      for (let i = 0; i <= zigzagCount; i++) {
        const px = i * zigzagWidth;
        const py = i % 2 === 0 ? 0 : height;
        zigzagPoints.push(`${px},${py}`);
      }
      points = zigzagPoints.join(" ");
      break;
    }

    case "crescent": {
      const segments = 20;
      const startAngle = Math.PI / 4;
      const endAngle = (3 * Math.PI) / 4;
      const crescentPoints = [];

      for (let i = 0; i <= segments; i++) {
        const angle = startAngle + ((endAngle - startAngle) * i) / segments;
        const px = centerX + radius * Math.cos(angle);
        const py = centerY + radius * Math.sin(angle);
        crescentPoints.push(`${px},${py}`);
      }
      for (let i = segments; i >= 0; i--) {
        const angle = startAngle + ((endAngle - startAngle) * i) / segments;
        const px = centerX + (radius / 2) * Math.cos(angle);
        const py = centerY + (radius / 2) * Math.sin(angle);
        crescentPoints.push(`${px},${py}`);
      }
      points = crescentPoints.join(" ");
      break;
    }

    case "arrowUp":
    case "arrowDown":
    case "arrowLeft":
    case "arrowRight": {
      let arrowPoints = "";
      const arrowWidth = width;
      const arrowHeight = height;
      switch (type) {
        case "arrowUp":
          arrowPoints = `${
            arrowWidth / 2
          },0 ${arrowWidth},${arrowHeight} 0,${arrowHeight}`;
          break;
        case "arrowDown":
          arrowPoints = `0,0 ${arrowWidth},0 ${arrowWidth / 2},${arrowHeight}`;
          break;
        case "arrowLeft":
          arrowPoints = `
          ${arrowWidth},${arrowHeight / 2}
          0,0
          0,${arrowHeight}
        `;
          break;
        case "arrowRight":
          arrowPoints = `
          0,${arrowHeight / 2}
          ${arrowWidth},0
          ${arrowWidth},${arrowHeight}
        `;
          break;
        default:
          console.warn(
            `getPolygonPoints: Tipo de polígono não reconhecido: ${type}`
          );
          return "";
      }
      points = arrowPoints.replace(/\s+/g, " ").trim();
      break;
    }

    default: {
      console.warn(
        `getPolygonPoints: Tipo de polígono não reconhecido: ${type}`
      );
      points = "";
    }
  }

  return points.replace(/\s+/g, " ").trim();
}

/**
 * Cria pontos para uma estrela.
 */
function createStarPoints(
  centerX,
  centerY,
  outerRadius,
  innerRadius,
  points,
  fillFactor = 1
) {
  let starPoints = "";
  const total = points * 2;
  const slice = (Math.PI * 2) / total;

  for (let i = 0; i < total; i++) {
    const angle = slice * i - Math.PI / 2;
    const r = i % 2 === 0 ? outerRadius : innerRadius;
    const x = centerX + r * Math.cos(angle);
    const y = centerY + r * Math.sin(angle);
    starPoints += `${x},${y} `;
  }

  if (fillFactor < 1) {
    const limit = Math.floor(total * fillFactor);
    starPoints = starPoints.trim().split(" ").slice(0, limit).join(" ");
  }

  return starPoints.trim();
}

/**
 * Cria pontos para um polígono regular.
 */
function createRegularPolygonPoints(centerX, centerY, radius, sides) {
  let polygonPoints = "";
  for (let i = 0; i < sides; i++) {
    const angle = (2 * Math.PI * i) / sides - Math.PI / 2;
    const x = centerX + radius * Math.cos(angle);
    const y = centerY + radius * Math.sin(angle);
    polygonPoints += `${x},${y} `;
  }
  return polygonPoints.trim();
}

/* ===========================================================
   EXTRAS: itemWidth, itemHeight, etc. (para line)
=========================================================== */
const itemWidth = computed(() => {
  if (!props.item.endPosition) return props.item.width || 0;
  const distanceX = Math.abs(props.item.endPosition.x - props.item.position.x);
  return distanceX * props.zoomLevel;
});
const itemHeight = computed(() => {
  if (!props.item.endPosition) return props.item.height || 0;
  const distanceY = Math.abs(props.item.endPosition.y - props.item.position.y);
  return distanceY * props.zoomLevel;
});
const lineDeltaX = computed(() => {
  if (!props.item.endPosition) return 0;
  return (props.item.endPosition.x - props.item.position.x) * props.zoomLevel;
});
const lineDeltaY = computed(() => {
  if (!props.item.endPosition) return 0;
  return (props.item.endPosition.y - props.item.position.y) * props.zoomLevel;
});
const svgStyle = { overflow: "visible" };

/* ===========================================================
   ESTILOS GERAIS
=========================================================== */
const itemStyle = computed(() => {
  const x =
    (props.item.position.x + (props.panOffset?.x || 0)) *
    (props.zoomLevel || 1);
  const y =
    (props.item.position.y + (props.panOffset?.y || 0)) *
    (props.zoomLevel || 1);

  const rotation = props.item.rotation || 0;
  const skewX = props.item.skewX || 0;
  const skewY = props.item.skewY || 0;
  const rotateX = props.item.rotateX || 0;
  const rotateY = props.item.rotateY || 0;
  const perspective = 1000;

  return {
    transform: `
    translate(${x}px, ${y}px)
    perspective(${perspective}px)
    rotateX(${rotateX}deg)
    rotateY(${rotateY}deg)
    rotate(${rotation}deg)
    skew(${skewX}deg, ${skewY}deg)
  `,
    width: `${(props.item.width || 0) * (props.zoomLevel || 1)}px`,
    height: `${(props.item.height || 0) * (props.zoomLevel || 1)}px`,
    position: "absolute",
    userSelect: "none",
    cursor: "grab",
    transformStyle: "preserve-3d",
  };
});

/* ===========================================================
   TOGGLE DIMENSIONS
=========================================================== */
function toggleDimensions() {
  enableDimensions.value = !enableDimensions.value;
  emits("select-item", props.item);
}

/* ===========================================================
   EDIÇÃO DE RÓTULO
=========================================================== */
function enableLabelEditing() {
  isEditingLabel.value = true;
  emits("select-item", props.item);
}
function disableLabelEditing() {
  isEditingLabel.value = false;
  emits("update-item", { ...props.item, label: props.item.label });
}

/* ===========================================================
   POLYLINE / BEZIER HANDLES DRAG
=========================================================== */
const isDragging = ref(false);
const draggingIndex = ref(-1);
const draggingHandle = ref(null);

function startPolylinePointDrag(idx, event) {
  isDragging.value = true;
  draggingIndex.value = idx;
  window.addEventListener("mousemove", onPolylineDrag);
  window.addEventListener("mouseup", stopPolylineDrag);
}
function onPolylineDrag(e) {
  if (!isDragging.value || !props.item.path) return;

  // Utilize svgRef para garantir que o SVG está acessível
  const svg = svgRef.value;
  if (!svg) {
    console.error("SVG element not found!");
    return;
  }

  const rect = svg.getBoundingClientRect();
  const x = (e.clientX - rect.left) / props.zoomLevel - props.panOffset.x;
  const y = (e.clientY - rect.top) / props.zoomLevel - props.panOffset.y;

  if (
    draggingIndex.value >= 0 &&
    draggingIndex.value < props.item.path.length
  ) {
    // Cria uma cópia do array path
    const newPath = [...props.item.path];

    // Cria uma cópia do ponto que está sendo alterado
    newPath[draggingIndex.value] = {
      ...newPath[draggingIndex.value],
      x: x,
      y: y,
    };

    // Cria uma cópia do item atualizando apenas a propriedade path
    const updatedItem = { ...props.item, path: newPath };

    emits("update-item", updatedItem);
  }
}
function stopPolylineDrag() {
  isDragging.value = false;
  draggingIndex.value = -1;
  window.removeEventListener("mousemove", onPolylineDrag);
  window.removeEventListener("mouseup", stopPolylineDrag);
}

function startBezierDrag(sIndex, handleType, event) {
  isDragging.value = true;
  draggingIndex.value = sIndex;
  draggingHandle.value = handleType;
  window.addEventListener("mousemove", onBezierDrag);
  window.addEventListener("mouseup", stopBezierDrag);
}
function onBezierDrag(e) {
  if (!isDragging.value || !props.item.segments) return;
  const seg = props.item.segments[draggingIndex.value];
  if (!seg) return;

  // Utilize svgRef para garantir que o SVG está acessível
  const svg = svgRef.value;
  if (!svg) {
    console.error("SVG element not found!");
    return;
  }

  const rect = svg.getBoundingClientRect();
  const x = (e.clientX - rect.left) / props.zoomLevel - props.panOffset.x;
  const y = (e.clientY - rect.top) / props.zoomLevel - props.panOffset.y;

  if (draggingHandle.value === "anchor") {
    seg.x = x;
    seg.y = y;
  } else if (draggingHandle.value === "c1") {
    seg.cx1 = x;
    seg.cy1 = y;
  } else if (draggingHandle.value === "c2") {
    seg.cx2 = x;
    seg.cy2 = y;
  }
  emits("update-item", { ...props.item });
}
function stopBezierDrag() {
  isDragging.value = false;
  draggingIndex.value = -1;
  draggingHandle.value = null;
  window.removeEventListener("mousemove", onBezierDrag);
  window.removeEventListener("mouseup", stopBezierDrag);
}

/* ===========================================================
   WATCH selectedItem -> config
=========================================================== */
watch(
  () => props.selectedItem,
  (newVal) => {
    if (newVal && newVal.id === props.item.id) {
      config.value = {
        rotation: newVal.rotation || 0,
        skewX: newVal.skewX || 0,
        skewY: newVal.skewY || 0,
        width: newVal.width,
        height: newVal.height,
        color: newVal.color || "#ffffff",
        borderWidth: newVal.borderWidth || 0,
        borderColor: newVal.borderColor || "#000000",
        rotateX: newVal.rotateX || 0,
        rotateY: newVal.rotateY || 0,
        backgroundImage: newVal.backgroundImage || "", // Adicionado
      };
      isCardOpen.value = true;
    }
  }
);

/* ===========================================================
   CONFIG CURVATURA
=========================================================== */
const showCurvesInputs = ref(false);
function toggleCurvesInputs() {
  showCurvesInputs.value = !showCurvesInputs.value;
}

const controlPointsCount = computed(() => {
  if (
    props.item.type === "bezier" ||
    props.item.type === "polyline" ||
    props.item.type === "squarePoliline" ||
    props.item.type === "circlePoliline"
  ) {
    return props.item.type === "bezier"
      ? props.item.segments?.length || 0
      : props.item.path?.length || 0;
  }
  return 0;
});

function updateControlPoints(newCount) {
  const count = parseInt(newCount, 10);

  // Cria uma cópia do item
  const updatedItem = { ...props.item };

  if (updatedItem.type === "bezier") {
    // Se não existir segments, inicia com um array vazio;
    // se existir, copia o array para evitar mutação direta.
    updatedItem.segments = updatedItem.segments
      ? [...updatedItem.segments]
      : [];

    while (updatedItem.segments.length < count) {
      updatedItem.segments.push({
        x: 50,
        y: 50,
        cx1: 30,
        cy1: 30,
        cx2: 70,
        cy2: 70,
      });
    }
    while (
      updatedItem.segments.length > count &&
      updatedItem.segments.length > 1
    ) {
      updatedItem.segments.pop();
    }
  } else {
    // Para polylines, squarePoliline, circlePoliline:
    updatedItem.path = updatedItem.path ? [...updatedItem.path] : [];

    while (updatedItem.path.length < count) {
      updatedItem.path.push({ x: 0, y: 0 });
    }
    while (updatedItem.path.length > count && updatedItem.path.length > 2) {
      updatedItem.path.pop();
    }
  }
  emits("update-item", updatedItem);
}

/* ===========================================================
   UPDATE CONFIG
=========================================================== */
function updateConfig() {
  emits("update-item", { ...props.item, ...config.value });
}

/* ===========================================================
   CARD DE CONFIG (ARRASTE)
=========================================================== */
const isDraggingCard = ref(false);
function startDragCard(event) {
  isDraggingCard.value = true;
  dragStartCard.value = {
    x: event.clientX - cardPosition.value.x,
    y: event.clientY - cardPosition.value.y,
  };
  window.addEventListener("mousemove", onDragCard);
  window.addEventListener("mouseup", stopDragCard);
}

function onDragCard(event) {
  if (isDraggingCard.value) {
    cardPosition.value = {
      x: event.clientX - dragStartCard.value.x,
      y: event.clientY - dragStartCard.value.y,
    };
  }
}
function stopDragCard() {
  isDraggingCard.value = false;
  window.removeEventListener("mousemove", onDragCard);
  window.removeEventListener("mouseup", stopDragCard);
}

/* ===========================================================
   ROTATE, SKEW, PERSPECTIVE
=========================================================== */
const isRotating = ref(false);
const rotateStart = ref({ x: 0, y: 0 });
const initialRotation = ref(0);

/**
 * Função para iniciar a rotação.
 * @param {Event} event - Evento de mouse.
 * @param {String} axis - Eixo de rotação.
 */
function onRotateMouseDown(event, axis) {
  isRotating.value = true;
  rotateStart.value = { x: event.clientX, y: event.clientY };
  initialRotation.value = props.item.rotation || 0;
  window.addEventListener("mousemove", onMouseMoveRotate);
  window.addEventListener("mouseup", onMouseUpRotate);
}

function onMouseMoveRotate(event) {
  if (!isRotating.value) return;

  let centerX, centerY;

  if (props.item.type === "image" || props.item.type === "icon") {
    // Para imagens, pegar a posição do próprio elemento HTML
    const img = document.querySelector(`[data-id="${props.item.id}"] img`);
    if (!img) {
      console.error("Image element not found!");
      return;
    }
    const rect = img.getBoundingClientRect();
    centerX = rect.left + rect.width / 2;
    centerY = rect.top + rect.height / 2;
  } else {
    // Para figuras geométricas (SVG)
    const svg = svgRef.value;
    if (!svg) {
      console.error("SVG element not found!");
      return;
    }
    const rect = svg.getBoundingClientRect();
    centerX = rect.left + rect.width / 2;
    centerY = rect.top + rect.height / 2;
  }

  // Cálculo do ângulo de rotação
  const angleRad = Math.atan2(event.clientY - centerY, event.clientX - centerX);
  const degrees = angleRad * (180 / Math.PI);
  const newRotation = degrees - initialRotation.value;

  emits("update-item", { ...props.item, rotation: newRotation });
}

function onMouseUpRotate() {
  isRotating.value = false;
  window.removeEventListener("mousemove", onMouseMoveRotate);
  window.removeEventListener("mouseup", onMouseUpRotate);
}

/* SKEW */
const isSkewing = ref(false);
const skewType = ref("");
const skewStart = ref({ x: 0, y: 0 });
const initialSkew = ref(0);

function onSkewMouseDown(type, event) {
  isSkewing.value = true;
  skewType.value = type;
  skewStart.value = { x: event.clientX, y: event.clientY };
  initialSkew.value = props.item[type] || 0;
  window.addEventListener("mousemove", onMouseMoveSkew);
  window.addEventListener("mouseup", onMouseUpSkew);
}

function onMouseMoveSkew(event) {
  if (!isSkewing.value) return;

  const dx = (event.clientX - skewStart.value.x) / props.zoomLevel;
  const newSkew = initialSkew.value + dx;
  emits("update-item", { ...props.item, [skewType.value]: newSkew });
}

function onMouseUpSkew() {
  isSkewing.value = false;
  skewType.value = "";
  window.removeEventListener("mousemove", onMouseMoveSkew);
  window.removeEventListener("mouseup", onMouseUpSkew);
}

/* PERSPECTIVE */
const isAdjustingPerspective = ref(false);
const perspectiveHandle = ref("");
const perspectiveStart = ref({ x: 0, y: 0 });
const initialPerspective = ref({ rotateX: 0, rotateY: 0 });

function onPerspectiveMouseDown(handle, event) {
  isAdjustingPerspective.value = true;
  perspectiveHandle.value = handle;
  perspectiveStart.value = { x: event.clientX, y: event.clientY };
  initialPerspective.value = {
    rotateX: props.item.rotateX || 0,
    rotateY: props.item.rotateY || 0,
  };
  window.addEventListener("mousemove", onMouseMovePerspective);
  window.addEventListener("mouseup", onMouseUpPerspective);
}

function onMouseMovePerspective(event) {
  if (!isAdjustingPerspective.value) return;

  const dx = (event.clientX - perspectiveStart.value.x) / props.zoomLevel;
  const dy = (event.clientY - perspectiveStart.value.y) / props.zoomLevel;
  let newRotateX = initialPerspective.value.rotateX;
  let newRotateY = initialPerspective.value.rotateY;

  switch (perspectiveHandle.value) {
    case "topLeft":
      newRotateX += dy;
      newRotateY -= dx;
      break;
    case "topRight":
      newRotateX += dy;
      newRotateY += dx;
      break;
    case "bottomLeft":
      newRotateX -= dy;
      newRotateY -= dx;
      break;
    case "bottomRight":
      newRotateX -= dy;
      newRotateY += dx;
      break;
    default:
      break;
  }

  emits("update-item", {
    ...props.item,
    rotateX: newRotateX,
    rotateY: newRotateY,
  });
}

function onMouseUpPerspective() {
  isAdjustingPerspective.value = false;
  perspectiveHandle.value = "";
  window.removeEventListener("mousemove", onMouseMovePerspective);
  window.removeEventListener("mouseup", onMouseUpPerspective);
}

/* ===========================================================
   RESIZE LÓGICA
=========================================================== */
const isResizing = ref(false);
const resizeDirection = ref("");
const resizeStart = ref({ x: 0, y: 0 });
const resizeItemStart = ref({});

function onResizeMouseDown(direction, event) {
  isResizing.value = true;
  resizeDirection.value = direction;
  resizeStart.value = {
    x: event.clientX,
    y: event.clientY,
  };
  resizeItemStart.value = {
    width: props.item.width,
    height: props.item.height,
    x: props.item.position.x,
    y: props.item.position.y,
    endPosition: props.item.endPosition ? { ...props.item.endPosition } : null,
  };
  window.addEventListener("mousemove", onMouseMoveResize);
  window.addEventListener("mouseup", onMouseUpResize);
}

function onMouseMoveResize(event) {
  if (!isResizing.value) return;

  const dx = (event.clientX - resizeStart.value.x) / props.zoomLevel;
  const dy = (event.clientY - resizeStart.value.y) / props.zoomLevel;

  // Se for line
  if (props.item.type === "line" && resizeItemStart.value.endPosition) {
    const newEndPosition = { ...resizeItemStart.value.endPosition };
    if (resizeDirection.value === "bottom-right") {
      newEndPosition.x += dx;
      newEndPosition.y += dy;
    }
    emits("update-item", {
      ...props.item,
      endPosition: newEndPosition,
      width: Math.abs(newEndPosition.x - props.item.position.x),
      height: Math.abs(newEndPosition.y - props.item.position.y),
    });
    return;
  }

  let newWidth = resizeItemStart.value.width;
  let newHeight = resizeItemStart.value.height;
  let newX = resizeItemStart.value.x;
  let newY = resizeItemStart.value.y;

  switch (resizeDirection.value) {
    case "top-left": {
      newWidth -= dx;
      newHeight -= dy;
      newX += dx;
      newY += dy;
      break;
    }
    case "top-right": {
      newWidth += dx;
      newHeight -= dy;
      newY += dy;
      break;
    }
    case "bottom-left": {
      newWidth -= dx;
      newHeight += dy;
      newX += dx;
      break;
    }
    case "bottom-right": {
      newWidth += dx;
      newHeight += dy;
      break;
    }
    case "top": {
      newHeight -= dy;
      newY += dy;
      break;
    }
    case "bottom": {
      newHeight += dy;
      break;
    }
    case "left": {
      newWidth -= dx;
      newX += dx;
      break;
    }
    case "right": {
      newWidth += dx;
      break;
    }
    case "start": {
      // Para linhas
      newX += dx;
      newY += dy;
      newWidth -= dx;
      newHeight -= dy;
      break;
    }
    case "end": {
      // Para linhas
      newWidth += dx;
      newHeight += dy;
      break;
    }
    default:
      break;
  }

  newWidth = Math.max(newWidth, 10);
  newHeight = Math.max(newHeight, 10);
  newX = Math.max(newX, 0);
  newY = Math.max(newY, 0);

  emits("update-item", {
    ...props.item,
    width: newWidth,
    height: newHeight,
    position: { x: newX, y: newY },
  });
}

function onMouseUpResize() {
  isResizing.value = false;
  resizeDirection.value = "";
  window.removeEventListener("mousemove", onMouseMoveResize);
  window.removeEventListener("mouseup", onMouseUpResize);
}

/* ===========================================================
   BOTÃO DIREITO E Z-INDEX
=========================================================== */
const rightClickCount = ref(0);
let rightClickTimer = null;

function onRightClick(event) {
  event.preventDefault();
  rightClickCount.value++;
  if (rightClickCount.value === 2) {
    emits("send-to-back", props.item);
    resetRightClick();
  }
  if (!rightClickTimer) {
    rightClickTimer = setTimeout(() => {
      resetRightClick();
    }, 300);
  }
}

function resetRightClick() {
  rightClickCount.value = 0;
  clearTimeout(rightClickTimer);
  rightClickTimer = null;
}

function handleBringToFront() {
  emits("bring-to-front", props.item);
}

/* ===========================================================
   MOUSE DOWN DO ITEM
=========================================================== */
function onMouseDown(event) {
  // Se quiser alguma lógica adicional
}

/* ===========================================================
   CARD DE CONFIGURAÇÃO
=========================================================== */
function closeCard() {
  isCardOpen.value = false;
}
</script>

<style scoped>
.draggable-item {
  display: flex;
  align-items: center;
  justify-content: center;
  position: absolute;
  cursor: grab;
  user-select: none;
  background: none;
  border: none;
}

.svg-wrapper {
  width: 100%;
  height: 100%;
  overflow: visible;
}

.container-box {
  width: 100%;
  height: 100%;
  background: none;
  border: none;
}

.container-3D {
  width: 100%;
  height: 100%;
  background: none;
  border: none;
}

.item-label {
  font-size: 12px;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.label-input {
  border: none;
  background-color: transparent;
  cursor: text;
  font-size: inherit;
  color: inherit;
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
  outline: none;
  text-align: center;
}

.geometric-svg {
  background: none;
  border: none;
  display: block; /* Remove espaçamento extra */
}

/* Handles de Redimensionamento */
.resize-handle {
  position: absolute;
  width: 10px;
  height: 10px;
  background-color: #fff;
  border: 1px solid #000;
  box-sizing: border-box;
  cursor: pointer;
  z-index: 10;
}
.resize-handle.top-left {
  top: -5px;
  left: -5px;
  cursor: nwse-resize;
}
.resize-handle.top-right {
  top: -5px;
  right: -5px;
  cursor: nesw-resize;
}
.resize-handle.bottom-left {
  bottom: -5px;
  left: -5px;
  cursor: nesw-resize;
}
.resize-handle.bottom-right {
  bottom: -5px;
  right: -5px;
  cursor: nwse-resize;
}
.resize-handle.top {
  top: -5px;
  left: 50%;
  transform: translateX(-50%);
  cursor: ns-resize;
}
.resize-handle.bottom {
  bottom: -5px;
  left: 50%;
  transform: translateX(-50%);
  cursor: ns-resize;
}
.resize-handle.left {
  left: -5px;
  top: 50%;
  transform: translateY(-50%);
  cursor: ew-resize;
}
.resize-handle.right {
  right: -5px;
  top: 50%;
  transform: translateY(-50%);
  cursor: ew-resize;
}

/* Rotação */
.rotate-handle {
  position: absolute;
  top: -20px;
  left: 50%;
  transform: translateX(-50%);
  width: 15px;
  height: 15px;
  background-color: #fff;
  border: 1px solid #000;
  border-radius: 50%;
  cursor: grab;
  z-index: 10;
}
.rotate-handle.y-axis {
  right: -20px;
  top: 50%;
  transform: translateY(-50%);
  width: 15px;
  height: 15px;
  background-color: #00ccff;
  border-radius: 50%;
  cursor: pointer;
  z-index: 100;
}

/* Skew */
.skew-handle {
  position: absolute;
  width: 10px;
  height: 10px;
  background-color: #fff;
  border: 1px solid #000;
  box-sizing: border-box;
  cursor: pointer;
  z-index: 10;
}
.skew-handle.skew-x {
  top: 50%;
  left: -15px;
  transform: translateY(-50%);
  cursor: ew-resize;
}
.skew-handle.skew-y {
  top: -15px;
  left: 50%;
  transform: translateX(-50%);
  cursor: ns-resize;
}
.skew-handle:hover {
  background-color: #000;
}

/* Perspectiva */
.perspective-handle {
  position: absolute;
  width: 10px;
  height: 10px;
  background-color: #ff0;
  border: 1px solid #000;
  box-sizing: border-box;
  cursor: pointer;
  z-index: 10;
}
.perspective-handle.top-left {
  top: -10px;
  left: -10px;
  cursor: nwse-resize;
}
.perspective-handle.top-right {
  top: -10px;
  right: -10px;
  cursor: nesw-resize;
}
.perspective-handle.bottom-left {
  bottom: -10px;
  left: -10px;
  cursor: nesw-resize;
}
.perspective-handle.bottom-right {
  bottom: -10px;
  right: -10px;
  cursor: nwse-resize;
}
.perspective-handle:hover {
  background-color: #000;
}

/* Card de Configuração */
.config-card {
  position: fixed;
  width: 300px;
  background-color: #f9f9f9;
  border: 1px solid #ddd;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  cursor: move;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #eee;
  padding: 10px;
  cursor: grab;
}
.card-header button {
  background: none;
  border: none;
  font-size: 16px;
  cursor: pointer;
}
.card-body {
  padding: 10px;
}
.property-group {
  margin-bottom: 10px;
  display: flex;
  flex-direction: column;
}
.property-group label {
  margin-bottom: 5px;
  font-size: 14px;
}
.property-group input[type="number"],
.property-group input[type="color"],
.property-group input[type="text"] {
  /* Adicionado 'text' para backgroundImage */
  padding: 5px;
  font-size: 14px;
}

/* Ao passar o mouse nos handles */
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

/* Adicionais para Elipses */
.svg-wrapper svg ellipse {
  transition: rx 0.2s, ry 0.2s, fill 0.2s;
}
</style>
