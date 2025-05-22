// router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/views/Layout.vue'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/',
      component: Layout,
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('@/views/Dashboard.vue'),
          meta: { title: '工作台', icon: 'HomeFilled' }
        },
        {
          path: 'workorder',
          name: 'WorkOrder',
          component: () => import('@/views/workorder/Index.vue'),
          redirect: { name: 'WorkOrderList' },
          meta: { title: '工单管理', icon: 'Document' },
          children: [
            {
              path: 'list',
              name: 'WorkOrderList',
              component: () => import('@/views/workorder/List.vue'),
              meta: { title: '工单列表' }
            },
            {
              path: 'create',
              name: 'WorkOrderCreate',
              component: () => import('@/views/workorder/Create.vue'),
              meta: { title: '发起工单' }
            },
            {
              path: 'detail/:id',
              name: 'WorkOrderDetail',
              component: () => import('@/views/workorder/Detail.vue'),
              meta: { title: '工单详情' }
            }
          ]
        },
        {
          path: 'todo',
          name: 'Todo',
          component: () => import('@/views/todo/Index.vue'),
          meta: { title: '待办事项', icon: 'List' }
        },
        {
          path: 'approval',
          name: 'Approval',
          component: () => import('@/views/approval/Index.vue'),
          meta: { title: '审批管理', icon: 'Check' },
          children: [
            {
              path: 'pending',
              name: 'ApprovalPending',
              component: () => import('@/views/approval/Pending.vue'),
              meta: { title: '待审批' }
            },
            {
              path: 'processed',
              name: 'ApprovalProcessed',
              component: () => import('@/views/approval/Processed.vue'),
              meta: { title: '已处理' }
            }
          ]
        },
        {
          path: 'dispatch',
          name: 'Dispatch',
          component: () => import('@/views/dispatch/Index.vue'),
          meta: { title: '派单管理', icon: 'Share' }
        },
        {
          path: 'statistics',
          name: 'Statistics',
          component: () => import('@/views/statistics/Index.vue'),
          meta: { title: '统计报表', icon: 'TrendCharts' }
        },
        {
          path: 'system',
          name: 'System',
          component: () => import('@/views/system/Index.vue'),
          meta: { title: '系统设置', icon: 'Setting' },
          children: [
            {
              path: 'users',
              name: 'Users',
              component: () => import('@/views/system/Users.vue'),
              meta: { title: '用户管理' }
            },
            {
              path: 'roles',
              name: 'Roles',
              component: () => import('@/views/system/Roles.vue'),
              meta: { title: '角色权限' }
            },
            {
              path: 'organization',
              name: 'Organization',
              component: () => import('@/views/system/Organization.vue'),
              meta: { title: '组织架构' }
            },
            {
              path: 'workflow',
              name: 'Workflow',
              component: () => import('@/views/system/Workflow.vue'),
              meta: { title: '流程配置' }
            }
          ]
        }
      ]
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth !== false)

  if (requiresAuth && !userStore.isLoggedIn) {
    next('/login')
  } else {
    next()
  }
})

export default router
