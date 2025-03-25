const routes = [
  {
    path: "/",
    component: () => import("layouts/MainLayout.vue"),
    children: [
      {
        path: "design",
        name: "design",
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
    ],
  },
  {
    path: "/login",
    name: "login",
    component: () => import("src/pages/LoginPage.vue"),
    meta: {
      label: "routes.login",
    },
  },
  {
    path: "/:catchAll(.*)*",
    name: "notFound",
    component: () => import("src/pages/ErrorNotFound.vue"),
    meta: {
      label: "routes.notFound",
    },
  },
];

export default routes;
