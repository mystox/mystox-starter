import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

let login = r=> require.ensure([], ()=> r(require('@/components/login/login.vue')), 'login');
let layout = r=> require.ensure([], ()=> r(require('@/components/layout.vue')), 'layout');
let fsus = r=> require.ensure([], ()=> r(require('@/components/fsus.vue')), 'fsus');
let fsuInfoList = r=> require.ensure([], ()=> r(require('@/components/fsuInfoList.vue')), 'fsuInfoList');
let fsuPKT = r=> require.ensure([], ()=> r(require('@/components/fsuPKT.vue')), 'fsuPKT');
let devices = r=> require.ensure([], ()=> r(require('@/components/devices.vue')), 'devices');
let points = r=> require.ensure([], ()=> r(require('@/components/points.vue')), 'points');
let bindSN = r=> require.ensure([], ()=> r(require('@/components/bindSN.vue')), 'bindSN');
let importDataForTest = r=> require.ensure([], ()=> r(require('@/components/importDataForTest.vue')), 'importDataForTest');
let setAlarms = r=> require.ensure([], ()=> r(require('@/components/setAlarms.vue')), 'setAlarms');
let alarms = r=> require.ensure([], ()=> r(require('@/components/alarms.vue')), 'alarms');

export default new Router({
  // mode: 'history',
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'login',
      component: login
    },
    {
      path: '/main',
      component: layout,
      // name: 'main',  // 要有默认子路由，父路由的name就要去掉
      children: [
        {
          path: '/fsus',
          name: 'fsus',
          component: fsus
        },
        {
          path: '/fsuInfoList',
          name: 'fsuInfoList',
          component: fsuInfoList
        },
        {
          path: '/fsuPKT',
          name: 'fsuPKT',
          component: fsuPKT
        },
        {
          path: '/devices',
          name: 'devices',
          component: devices
        },
        {
          path: '/points',
          name: 'points',
          component: points
        },
        {
          path: '/setAlarms',
          name: 'setAlarms',
          component: setAlarms
        },
        {
          path: '/alarms',
          name: 'alarms',
          component: alarms
        },
        {
          path: '/bindSN',
          name: 'bindSN',
          component: bindSN
        },
        {
          path: '/importDataForTest',
          name: 'importDataForTest',
          component: importDataForTest
        },
      ]
    }
  ]
})
