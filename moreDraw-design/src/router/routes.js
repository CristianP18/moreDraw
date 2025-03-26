const routes = [
  {
    path: "/",
    redirect: () => {
      const token = localStorage.getItem("jwt");
      return token ? "/design" : "/login";
    },
  },
  {
    path: "/",
    component: () => import("layouts/MainLayout.vue"),
    children: [
      {
        path: "design",
        name: "design",
        component: () => import("src/pages/DesignDrew.vue"),
        meta: {
          requiresAuth: true,
          showDrawer: true, // ✅ indica que pode aparecer no menu
          icon: "mdi-draw", // ou outro ícone do Quasar/Material Icons
          label: "Design", // ✅ nome que vai aparecer no menu
        },
      },
    ],
  },
  {
    path: "/login",
    name: "login",
    component: () => import("src/pages/LoginPage.vue"),
  },
  {
    path: "/:catchAll(.*)*",
    name: "notFound",
    component: () => import("src/pages/ErrorNotFound.vue"),
  },
];

export default routes;
