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
  {
    path: "/payments",
    component: Layout,
    redirect: "/payments/list",
    meta: { title: "支付管理", icon: "Wallet" },
    children: [
      {
        path: "list",
        name: "PaymentList",
        component: () => import("@/views/PaymentList.vue"),
        meta: { title: "支付流水" },
      },
    ],
  },
  {
    path: "/refunds",
    component: Layout,
    redirect: "/refunds/list",
    meta: { title: "退款管理", icon: "RefreshLeft" },
    children: [
      {
        path: "list",
        name: "RefundList",
        component: () => import("@/views/RefundList.vue"),
        meta: { title: "退款申请" },
      },
    ],
  },
  {
    path: "/wallet",
    component: Layout,
    redirect: "/wallet/list",
    meta: { title: "钱包管理", icon: "CreditCard" },
    children: [
      {
        path: "list",
        name: "WalletList",
        component: () => import("@/views/wallet/WalletList.vue"),
        meta: { title: "用户钱包" },
      },
      {
        path: "transactions",
        name: "WalletTransactions",
        component: () => import("@/views/wallet/TransactionList.vue"),
        meta: { title: "钱包流水" },
      },
    ],
  },
  {
    path: "/points",
    component: Layout,
    redirect: "/points/list",
    meta: { title: "积分管理", icon: "Star" },
    children: [
      {
        path: "list",
        name: "PointsList",
        component: () => import("@/views/points/PointsList.vue"),
        meta: { title: "用户积分" },
      },
      {
        path: "transactions",
        name: "PointsTransactions",
        component: () => import("@/views/points/TransactionList.vue"),
        meta: { title: "积分流水" },
      },
    ],
  },
  {
    path: "/coupons",
    component: Layout,
    redirect: "/coupons/list",
    meta: { title: "优惠券管理", icon: "Discount" },
    children: [
      {
        path: "list",
        name: "CouponList",
        component: () => import("@/views/coupons/CouponList.vue"),
        meta: { title: "优惠券列表" },
      },
      {
        path: "user",
        name: "UserCouponList",
        component: () => import("@/views/coupons/UserCouponList.vue"),
        meta: { title: "用户优惠券" },
      },
    ],
  },
  {
    path: "/activities",
    component: Layout,
    redirect: "/activities/list",
    meta: { title: "活动管理", icon: "Medal" },
    children: [
      {
        path: "list",
        name: "ActivityList",
        component: () => import("@/views/activity/ActivityManage.vue"),
        meta: { title: "活动列表" },
      },
    ],
  },
  {
    path: "/seckills",
    component: Layout,
    redirect: "/seckills/list",
    meta: { title: "秒杀管理", icon: "Timer" },
    children: [
      {
        path: "list",
        name: "SeckillList",
        component: () => import("@/views/seckill/SeckillManage.vue"),
        meta: { title: "秒杀列表" },
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
