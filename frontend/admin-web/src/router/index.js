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
  {
    path: "/scenic",
    component: Layout,
    redirect: "/scenic/list",
    meta: { title: "景区管理", icon: "Location" },
    children: [
      {
        path: "list",
        name: "ScenicList",
        component: () => import("@/views/scenic/ScenicList.vue"),
        meta: { title: "景区列表" },
      },
    ],
  },
  {
    path: "/tickets",
    component: Layout,
    redirect: "/tickets/list",
    meta: { title: "票务管理", icon: "Ticket" },
    children: [
      {
        path: "list",
        name: "TicketList",
        component: () => import("@/views/tickets/TicketList.vue"),
        meta: { title: "票种管理" },
      },
      {
        path: "create",
        name: "TicketCreate",
        component: () => import("@/views/tickets/TicketForm.vue"),
        meta: { title: "创建票种" },
        hidden: true,
      },
      {
        path: "edit/:id",
        name: "TicketEdit",
        component: () => import("@/views/tickets/TicketForm.vue"),
        meta: { title: "编辑票种" },
        hidden: true,
      },
      {
        path: "prices/:ticketId",
        name: "TicketPrices",
        component: () => import("@/views/tickets/TicketPriceCalendar.vue"),
        meta: { title: "票价日历" },
        hidden: true,
      },
    ],
  },
  {
    path: "/orders",
    component: Layout,
    redirect: "/orders/list",
    meta: { title: "订单管理", icon: "Document" },
    children: [
      {
        path: "list",
        name: "OrderList",
        component: () => import("@/views/orders/OrderList.vue"),
        meta: { title: "订单列表" },
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
