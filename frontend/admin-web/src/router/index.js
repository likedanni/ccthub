import Layout from "@/layout/index.vue";
import { createRouter, createWebHistory } from "vue-router";

const routes = [
  {
    path: "/login",
    name: "Login",
    component: () => import("@/views/Login.vue"),
    meta: { title: "登录" },
  },
  {
    path: "/",
    component: Layout,
    redirect: "/dashboard",
    children: [
      {
        path: "dashboard",
        name: "Dashboard",
        component: () => import("@/views/Dashboard.vue"),
        meta: { title: "仪表盘", icon: "DataLine" },
      },
    ],
  },
  {
    path: "/users",
    component: Layout,
    redirect: "/users/list",
    meta: { title: "用户管理", icon: "User" },
    children: [
      {
        path: "list",
        name: "UserList",
        component: () => import("@/views/users/UserList.vue"),
        meta: { title: "用户列表" },
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem("token");

  if (to.path === "/login") {
    next();
  } else if (!token) {
    next("/login");
  } else {
    next();
  }
});

export default router;
