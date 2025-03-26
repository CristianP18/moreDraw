import { createRouter, createWebHistory } from "vue-router";
import routes from "./routes"; // ✅ importa o array corretamente

const router = createRouter({
  history: createWebHistory(),
  routes, // ✅ esse tem que ser um array!
});

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem("jwt");

  if (to.meta.requiresAuth && !token) {
    return next({ name: "login" });
  }

  if (to.name === "login" && token) {
    return next({ name: "design" });
  }

  return next();
});

export default router;
