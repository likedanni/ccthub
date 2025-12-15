<template>
  <el-container class="layout-container">
    <el-aside width="200px">
      <div class="logo">
        <h2>长治文旅平台</h2>
      </div>
      <el-menu :default-active="activeMenu" router background-color="#304156" text-color="#bfcbd9"
        active-text-color="#409EFF">
        <el-menu-item index="/dashboard">
          <el-icon>
            <DataLine />
          </el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-sub-menu index="/users">
          <template #title>
            <el-icon>
              <User />
            </el-icon>
            <span>用户管理</span>
          </template>
          <el-menu-item index="/users/list">用户列表</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="/scenic">
          <template #title>
            <el-icon>
              <Location />
            </el-icon>
            <span>景区管理</span>
          </template>
          <el-menu-item index="/scenic/list">景区列表</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="/tickets">
          <template #title>
            <el-icon>
              <Ticket />
            </el-icon>
            <span>票务管理</span>
          </template>
          <el-menu-item index="/tickets/list">票种管理</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="/orders">
          <template #title>
            <el-icon>
              <Document />
            </el-icon>
            <span>订单管理</span>
          </template>
          <el-menu-item index="/orders/list">订单列表</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="/payments">
          <template #title>
            <el-icon>
              <Wallet />
            </el-icon>
            <span>支付管理</span>
          </template>
          <el-menu-item index="/payments/list">支付流水</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="/refunds">
          <template #title>
            <el-icon>
              <RefreshLeft />
            </el-icon>
            <span>退款管理</span>
          </template>
          <el-menu-item index="/refunds/list">退款申请</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header>
        <div class="header-content">
          <span>管理后台</span>
          <div class="user-info">
            <el-dropdown @command="handleCommand">
              <span class="el-dropdown-link">
                <el-icon>
                  <Avatar />
                </el-icon>
                管理员
                <el-icon class="el-icon--right">
                  <ArrowDown />
                </el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>

      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const activeMenu = computed(() => route.path)

const handleCommand = (command) => {
  if (command === 'logout') {
    localStorage.removeItem('token')
    ElMessage.success('退出登录成功')
    router.push('/login')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.el-aside {
  background-color: #304156;
  color: #fff;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  background-color: #2b3a4b;
  color: #fff;
}

.logo h2 {
  font-size: 16px;
  margin: 0;
}

.el-header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.el-dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
