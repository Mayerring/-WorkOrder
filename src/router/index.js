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
          path: 'approval',
          name: 'Approval',
          component: () => import('@/views/approval/Approval.vue'),
          meta: { title: '审批管理', icon: 'Check' }
        },
        {
          path: 'dispatch',
          name: 'Dispatch',
          component: () => import('@/views/dispatch/Dispatch.vue'),
          meta: { title: '派单管理', icon: 'Share' }
        },
        {
          path: 'todo',
          name: 'Todo',
          component: () => import('@/views/todo/Todo.vue'),
          meta: { title: '待办工单', icon: 'List' }
        },
        //确认工单
        {
          path: 'confirm',
          name: 'Confirm',
          component: () => import('@/views/confirm/Confirm.vue'),
          meta: { title: '确认工单', icon: 'List' }
        },
        {
          path: 'statistics',
          name: 'Statistics',
          component: () => import('@/views/statistics/Statistics.vue'),
          meta: { title: '统计报表', icon: 'TrendCharts' }
        },
        {
          path: 'system',
          name: 'System',
          component: () => import('@/views/system/System.vue'),
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
  const token = localStorage.getItem('token')
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth !== false)

  if (to.path === '/login') {
    if (token) {
      next('/dashboard')
    } else {
      next()
    }
  } else if (requiresAuth) {
    if (token) {
      next()
    } else {
      next('/login')
    }
  } else {
    next()
  }
})

export default router
