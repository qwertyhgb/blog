// 导入Vue Router相关函数

import { createRouter, createWebHistory } from "vue-router";

// 导入用户状态管理store

import { useUserStore } from "../stores/user";

// 路由元信息类型定义
declare module "vue-router" {
  interface RouteMeta {
    // 是否需要登录
    requiresAuth?: boolean;
    // 是否需要管理员权限
    requiresAdmin?: boolean;
    // 访问该路由所需角色列表（如 ['ADMIN', 'USER']）
    roles?: string[];
  }
}

// 创建路由实例
const router = createRouter({
  // 使用HTML5历史模式
  history: createWebHistory(),
  // 定义路由配置
  routes: [
    {
      // 首页路由
      path: "/",
      name: "home",
      // 异步加载首页组件
      component: () => import("../views/Home.vue"),
    },
    {
      // 登录页面路由
      path: "/login",
      name: "login",
      // 异步加载登录页面组件
      component: () => import("../views/Login.vue"),
    },
    {
      // 注册页面路由
      path: "/register",
      name: "register",
      // 异步加载注册页面组件
      component: () => import("../views/Register.vue"),
    },
    {
      // 文章详情页面路由，带动态参数id
      path: "/post/:id",
      name: "post-detail",
      // 异步加载文章详情页面组件
      component: () => import("../views/PostDetail.vue"),
      // 将路由参数作为props传递给组件
      props: true,
    },
    {
      // 文章编辑页面路由，id参数可选（新建时无id）
      path: "/post/edit/:id?",
      name: "post-edit",
      // 异步加载文章编辑页面组件
      component: () => import("../views/PostEdit.vue"),
      // 将路由参数作为props传递给组件
      props: true,
      // 元信息：需要登录才能访问
      meta: { requiresAuth: true },
    },
    {
      // 我的文章页面路由
      path: "/my-posts",
      name: "my-posts",
      // 异步加载我的文章页面组件
      component: () => import("../views/MyPosts.vue"),
      // 元信息：需要登录才能访问
      meta: { requiresAuth: true },
    },
    {
      // 个人资料页面路由
      path: "/profile",
      name: "profile",
      // 异步加载个人资料页面组件
      component: () => import("../views/Profile.vue"),
      // 元信息：需要登录才能访问
      meta: { requiresAuth: true },
    },
    {
      // 分类列表页面路由
      path: "/category",
      name: "categories",
      // 异步加载分类列表页面组件
      component: () => import("../views/Categories.vue"),
    },
    {
      // 分类详情页面路由，带动态参数id
      path: "/category/:id",
      name: "category",
      // 异步加载分类详情页面组件
      component: () => import("../views/Category.vue"),
      // 将路由参数作为props传递给组件
      props: true,
    },
    {
      // 标签列表页面路由
      path: "/tag",
      name: "tags",
      // 异步加载标签列表页面组件
      component: () => import("../views/Tags.vue"),
    },
    {
      // 标签详情页面路由，带动态参数id
      path: "/tag/:id",
      name: "tag",
      // 异步加载标签详情页面组件
      component: () => import("../views/Tag.vue"),
      // 将路由参数作为props传递给组件
      props: true,
    },
    {
      // 管理后台布局路由
      path: "/admin",
      name: "admin-layout",
      // 异步加载管理后台布局组件
      component: () => import("../views/admin/Layout.vue"),
      // 元信息：需要登录和管理员权限才能访问
      meta: { requiresAuth: true, requiresAdmin: true },
      // 嵌套路由配置
      children: [
        {
          // 默认重定向到仪表盘
          path: "",
          redirect: "/admin/dashboard",
        },
        {
          // 仪表盘页面路由
          path: "dashboard",
          name: "admin-dashboard",
          // 异步加载仪表盘页面组件
          component: () => import("../views/admin/Dashboard.vue"),
        },
        {
          // 文章管理页面路由
          path: "posts",
          name: "admin-posts",
          // 异步加载文章管理页面组件
          component: () => import("../views/admin/PostManagement.vue"),
        },
        {
          // 分类管理页面路由
          path: "categories",
          name: "admin-categories",
          // 异步加载分类管理页面组件
          component: () => import("../views/admin/CategoryManagement.vue"),
        },
        {
          // 标签管理页面路由
          path: "tags",
          name: "admin-tags",
          // 异步加载标签管理页面组件
          component: () => import("../views/admin/TagManagement.vue"),
        },
        {
          // 评论管理页面路由
          path: "comments",
          name: "admin-comments",
          // 异步加载评论管理页面组件
          component: () => import("../views/admin/CommentManagement.vue"),
        },
        {
          // 用户管理页面路由
          path: "users",
          name: "admin-users",
          // 异步加载用户管理页面组件
          component: () => import("../views/admin/UserManagement.vue"),
        },
        {
          // 系统设置页面路由
          path: "settings",
          name: "admin-settings",
          // 异步加载系统设置页面组件
          component: () => import("../views/admin/SystemSettings.vue"),
        },
      ],
    },
    {
      // 404页面路由，匹配所有未定义的路径
      path: "/:pathMatch(.*)*",
      name: "not-found",
      // 异步加载404页面组件
      component: () => import("../views/NotFound.vue"),
    },
  ],
});

// 全局前置路由守卫

router.beforeEach(async (to, _from, next) => {
  const userStore = useUserStore();

  const requiresAuth = !!to.meta.requiresAuth;
  const requiresAdmin = !!to.meta.requiresAdmin;
  const requiredRoles = to.meta.roles;

  // 1. 未登录但需要登录
  if (requiresAuth && !userStore.token) {
    next({ name: "login", query: { redirect: to.fullPath } });

    return;
  }

  // 2. 有 token 但还没有用户信息，先拉取一次用户信息
  if (userStore.token && !userStore.userInfo) {
    try {
      await userStore.getCurrentUser();
    } catch (error) {
      console.error("获取用户信息失败:", error);

      if (requiresAuth) {
        next({ name: "login", query: { redirect: to.fullPath } });

        return;
      }
    }
  }

  const role = userStore.userInfo?.role;

  // 3. 检查管理员权限标记
  if (requiresAdmin && role !== "ADMIN") {
    next({ name: "home" });
    return;
  }

  // 4. 检查显式角色列表（如果配置了 meta.roles）
  if (requiredRoles && requiredRoles.length > 0) {
    if (!role || !requiredRoles.includes(role)) {
      next({ name: "home" });
      return;
    }
  }

  next();
});

// 导出路由实例
export default router;
