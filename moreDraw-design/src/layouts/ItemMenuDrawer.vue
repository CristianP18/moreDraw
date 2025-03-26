<template>
  <q-item
    v-if="!item.children"
    :key="item.name"
    clickable
    dense
    :class="
      $route.name === item.name
        ? $q.dark.isActive
          ? 'item-active-dark'
          : 'item-active-light'
        : ''
    "
    @click="routerPush(item.name)"
  >
    <q-item-section avatar class="full-width">
      <div class="row items-center full-width justify-between">
        <div v-if="item.icon">
          <q-icon size="xs" :name="item.icon" />
        </div>
        <div :class="{ 'q-ml-md': item.icon }" class="col">
          <q-item-label style="word-break: break-all">
            {{ item.label }}
          </q-item-label>
          <q-item-label
            v-if="item.body"
            caption
            lines="1"
            style="word-break: break-all"
          >
            {{ item.body }}
          </q-item-label>
        </div>
        <q-badge v-if="item.badge">
          {{ item.badge }}
        </q-badge>
      </div>
    </q-item-section>
  </q-item>
</template>

<script>
import {
  useQuasar,
  QItemSection,
  QItemLabel,
  QItem,
  QBadge,
  QIcon,
} from "quasar";

export default {
  name: "ItemMenuDrawer",
  components: { QItemSection, QItemLabel, QItem, QBadge, QIcon },
  props: {
    item: {
      type: Object,
      required: true,
    },
    icon: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      $q: useQuasar(),
    };
  },
  methods: {
    routerPush(value) {
      this.$router.push({ name: value });
    },
  },
};
</script>
