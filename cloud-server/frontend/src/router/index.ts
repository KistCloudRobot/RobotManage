import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import {store} from '@/state/store';

// @ts-ignore
// @ts-ignore
const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    redirect: "/main",
    name: "home"
  },
  {
    path: "/main",
    name: "메인",
    meta: {
      authRequired: true,
      authorization: []
    },
    component: () => import("../views/pages/main/Main.vue"),
  },
  {
    path: "/admin/user",
    name: "사용자관리",
    meta: {
      authRequired: true,
      authorization: ['ADMIN']
    },
    component: () => import("../views/pages/admin/User.vue"),
  },
  {
    path: "/container",
    name: "컨테이너",
    meta: {
      authRequired: true,
      authorization: ['USER', 'ADMIN']
    },
    component: () => import("../views/pages/manage/Manage_Container.vue"),
  },
  {
    path: "/manage/test",
    name: "환경파일",
    meta: {
      authRequired: true,
      authorization: ['USER', 'ADMIN']
    },
    component: () => import("../views/pages/manage/Manage_File.vue"),
  },
  {
    path: "/manage/robot-identification",
    name: "로봇식별정보",
    meta: {
      authRequired: true,
      authorization: ['USER', 'ADMIN']
    },
    component: () => import("../views/pages/manage/Manage_identification.vue"),
  },
  {
    path: "/manage/dispatcher",
    // name: "디스패처",
    meta: {
      authRequired: true,
      authorization: ['USER', 'ADMIN']
    },
    component: () => import("../views/pages/manage/Manage_Dispatcher.vue"),
  },
  {
    path: "/manage/file",
    name: "환경파일2",
    meta: {
      authRequired: true,
      authorization: ['USER', 'ADMIN']
    },
    component: () => import("../views/pages/manage/Manage_File.vue"),
  },
  { /* MDK-OAuth Redirector. */
    path: '/oauth2/redirect',
    name: 'Auth Handler',
    meta: {
      authRequired: false
    },
    //component: OAuth2RedirectHandler
    component: () => import('../mdk/OAuth2RedirectHandler.vue')
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/pages/account/login.vue'),
  },
  {
    path: "/logout",
    name: 'logout',
    component: () => import('../views/pages/account/login.vue'),
    meta: {
      authRequired: true
    },
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('../views/pages/account/register.vue'),
  },
  {
    path: '/forgot-password',
    name: 'Forgot-password',
    component: () => import('../views/pages/account/forgot-password.vue'),
  },

];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

// router.beforeResolve(async (routeTo, routeFrom, next) => {
//   // Create a `beforeResolve` hook, which fires whenever
//   // `beforeRouteEnter` and `beforeRouteUpdate` would. This
//   // allows us to ensure data is fetched even when params change,
//   // but the resolved route does not. We put it in `meta` to
//   // indicate that it's a hook we created, rather than part of
//   // Vue Router (yet?).
//   try {
//     // For each matched route...
//     for (const route of routeTo.matched) {
//       await new Promise((resolve, reject) => {
//         // If a `beforeResolve` hook is defined, call it with
//         // the same arguments as the `beforeEnter` hook.
//         if (route.meta && route.meta.beforeResolve) {
//           route.meta.beforeResolve(routeTo, routeFrom, (...args) => {
//             // If the user chose to redirect...
//             if (args.length) {
//               // If redirecting to the same route we're coming from...
//               // Complete the redirect.
//               next(...args)
//               reject(new Error('Redirected'))
//             } else {
//               resolve()
//             }
//           })
//         } else {
//           // Otherwise, continue resolving the route.
//           resolve()
//         }
//       })
//     }
//     // If a `beforeResolve` hook chose to redirect, just return.
//   } catch (error) {
//     return
//   }
//
//   // If we reach this point, continue resolving the route.
//   next()
// })


export default router;
