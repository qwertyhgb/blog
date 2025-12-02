import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/Home.vue')
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/Login.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/Register.vue')
    },
    {
      path: '/post/:id',
      name: 'post-detail',
      component: () => import('../views/PostDetail.vue'),
      props: true
    },
    {
      path: '/post/edit/:id?',
      name: 'post-edit',
      component: () => import('../views/PostEdit.vue'),
      props: true,
      meta: { requiresAuth: true }
    },
    {
      path: '/my-posts',
      name: 'my-posts',
      component: () => import('../views/MyPosts.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('../views/Profile.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/category',
      name: 'categories',
      component: () => import('../views/Categories.vue')
    },
    {
      path: '/category/:id',
      name: 'category',
      component: () => import('../views/Category.vue'),
      props: true
    },
    {
      path: '/tag',
      name: 'tags',
      component: () => import('../views/Tags.vue')
    },
    {
      path: '/tag/:id',
      name: 'tag',
      component: () => import('../views/Tag.vue'),
      props: true
    },
    {
      path: '/admin',
      name: 'admin-layout',
      component: () => import('../views/admin/Layout.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
      children: [
        {
          path: '',
          redirect: '/admin/dashboard'
        },
        {
          path: 'dashboard',
          name: 'admin-dashboard',
          component: () => import('../views/admin/Dashboard.vue')
        },
        {
          path: 'posts',
          name: 'admin-posts',
          component: () => import('../views/admin/PostManagement.vue')
        },
        {
          path: 'categories',
          name: 'admin-categories',
          component: () => import('../views/admin/CategoryManagement.vue')
        },
        {
          path: 'tags',
          name: 'admin-tags',
          component: () => import('../views/admin/TagManagement.vue')
        },
        {
          path: 'comments',
          name: 'admin-comments',
          component: () => import('../views/admin/CommentManagement.vue')
        },
        {
          path: 'users',
          name: 'admin-users',
          component: () => import('../views/admin/UserManagement.vue')
        },
        {
          path: 'settings',
          name: 'admin-settings',
          component: () => import('../views/admin/SystemSettings.vue')
        }
      ]
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('../views/NotFound.vue')
    }
  ]
})

// 路由守卫
router.beforeEach(async (to, _from, next) => {
  const userStore = useUserStore()

  // 检查是否需要登录
  if (to.meta.requiresAuth && !userStore.token) {
    next({ name: 'login', query: { redirect: to.fullPath } })
    return
  }

  // 如果有 token 但没有用户信息，先获取用户信息
  if (userStore.token && !userStore.userInfo) {
    try {
      await userStore.getCurrentUser()
    } catch (error) {
      // 获取用户信息失败，可能 token 已过期
      console.error('获取用户信息失败:', error)
      if (to.meta.requiresAuth) {
        next({ name: 'login', query: { redirect: to.fullPath } })
        return
      }
    }
  }

  // 检查是否需要管理员权限
  if (to.meta.requiresAdmin && userStore.userInfo?.role !== 'ADMIN') {
    next({ name: 'home' })
    return
  }

  next()
})

export default router