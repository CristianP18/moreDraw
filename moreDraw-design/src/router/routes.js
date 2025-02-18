const routes = [
  {
    path: "/",
    name: "layout",
    component: () => import("layouts/MainLayout.vue"),
    children: [
      {
        path: "",
        name: "",
        component: () => import("src/pages/DesignDrew.vue"),
        meta: {
          showDrawer: true,
          title: "design",
          icon: "mdi-package-variant",
          iconColor: "orange",
          requiresAuth: true,
          label: "Design",
        },
      },
      {
        path: "login",
        name: "login",
        component: () => import("src/pages/LoginPage.vue"),
        meta: {
          showDrawer: true,
          title: "login",
          icon: "mdi-package-variant",
          iconColor: "orange",
          requiresAuth: true,
          label: "Login",
        },
      },
    ],
  },
  {
    name: "login",
    path: "/login",
    component: () => import("pages/LoginPage.vue"),
    meta: {
      label: "routes.login",
    },
  },
  {
    path: "/:catchAll(.*)*",
    component: () => import("pages/ErrorNotFound.vue"),
    meta: {
      label: "routes.notFound",
    },
  },
];

export default routes;
