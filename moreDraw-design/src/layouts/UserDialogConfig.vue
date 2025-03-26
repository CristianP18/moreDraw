<template>
  <q-card flat bordered class="row no-wrap q-pa-md no-shadow">
    <div class="column justify-center">
      <div class="text-subtitle1 text-weight-bold q-mt-sm q-mb-xs q-ml-sm">
        {{ translation[locale].changeTheme }}
      </div>
      <div class="text-space">
        <q-toggle
          v-model="toggleDark"
          checked-icon="mdi-weather-night"
          color="primary"
          size="sm"
          class="q-ml-sm q-mr-sm"
          unchecked-icon="mdi-weather-sunny"
          :label="
            toggleDark
              ? `Dark ${translation[locale].enable}`
              : `Light ${translation[locale].enable}`
          "
        />
      </div>

      <div class="text-subtitle1 q-mt-md q-mb-xs q-ml-sm text-weight-bold">
        {{ translation[locale].changeLanguage }}
      </div>
      <q-select
        v-model="locale"
        :options="localeOptions"
        bottom-slots
        dense
        options-dense
        emit-value
        map-options
        transition-show="jump-up"
        transition-hide="jump-up"
        color="primary"
        class="q-mal-md q-mt-sm q-mr-sm"
        @update:model-value="setLanguage"
      >
        <template #prepend>
          <q-icon name="mdi-translate" size="xs" @click.stop />
        </template>
        <template #option="scope">
          <q-item v-bind="scope.itemProps">
            <q-item-section>
              <q-item-label>{{ scope.opt.label }}</q-item-label>
            </q-item-section>
          </q-item>
        </template>
      </q-select>
    </div>

    <q-separator v-if="isLoggedIn" vertical inset class="q-mx-lg" />

    <div v-if="isLoggedIn" class="column items-center justify-center">
      <q-avatar color="grey-6" size="72px">
        <img
          :src="userInfo?.picUser || 'src/assets/img/image.svg'"
          alt="Avatar"
        />
      </q-avatar>

      <div class="text-subtitle1 text-center q-mt-sm q-mb-md">
        {{
          !$q.screen.lt.sm && userInfo?.name?.length < 19
            ? userInfo.name
            : userInfo?.name?.split(" ")[0] || ""
        }}
      </div>

      <q-btn flat :label="translation[locale].logout" @click="logout" />
    </div>
  </q-card>
</template>

<script>
import {
  useQuasar,
  QToggle,
  QSelect,
  QSeparator,
  QAvatar,
  QItem,
  QItemSection,
  QItemLabel,
  QIcon,
} from "quasar";
import { language } from "../language/language";
import { getCookie } from "../utils/getCookie";

export default {
  name: "UserDialogConfig",
  components: {
    QToggle,
    QSelect,
    QSeparator,
    QAvatar,
    QItem,
    QItemSection,
    QItemLabel,
    QIcon,
  },
  props: {
    userInfo: {
      type: Object,
      default: () => ({}),
    },
  },
  emits: ["logout", "changeLanguage"],
  data() {
    let isDark = getCookie("DarkMode") === "true";
    if (isDark === undefined) isDark = false;

    return {
      translation: language,
      languageSystem: "pt-BR",
      toggleDark: isDark,
      $q: useQuasar(),
      locale: "pt-BR",
      localeOptions: [
        { value: "en-US", label: "English" },
        { value: "pt-BR", label: "Português" },
        { value: "es-ES", label: "Español" },
      ],
    };
  },
  computed: {
    isLoggedIn() {
      return !!localStorage.getItem("jwt");
    },
  },
  watch: {
    toggleDark(val) {
      this.$q.dark.set(val);
      document.cookie = `DarkMode=${this.toggleDark.toString()}; domain=${
        process.env.NODE_ENV === "production" ? "myspark.com.br" : ""
      }; path=/`;
    },
  },
  mounted() {
    this.languageSystem = getCookie("Language");
    if (this.languageSystem === undefined) {
      this.locale = "pt-BR";
    } else {
      this.locale = this.languageSystem;
    }
  },
  methods: {
    logout() {
      localStorage.removeItem("jwt");
      localStorage.removeItem("apiKey");
      localStorage.removeItem("picture");
      localStorage.removeItem("nameUser");
      this.$emit("logout");
      // ✅ Redireciona para a página de login
      this.$router.push("/login");
    },
    setLanguage() {
      document.cookie = `Language=${this.locale}; domain=${
        process.env.NODE_ENV === "production" ? "myspark.com.br" : ""
      }; path=/`;
      this.$emit("changeLanguage");
    },
  },
};
</script>

<style scoped>
.flex {
  display: flex;
}
.flex-center {
  align-items: center;
  justify-content: center;
}
</style>
