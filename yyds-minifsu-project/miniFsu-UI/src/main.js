// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'

// use element-ui
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css'
Vue.use(ElementUI);

// i18n
import VueI18n from 'vue-i18n';
import lang from './lang'
Vue.use(VueI18n);
const i18n = new VueI18n({
  locale: 'zh-CN',
  messages: lang
})

// reset.css
import 'reset.css'

// baseStyle.css
import '@/common/baseStyle.styl'

// font
import '@/common/font/iconfont.css'

// custom base component
import baseComponent from '@/base'
Vue.use(baseComponent);

// use custom filter
import filter from '@/utils/filter.js'
Vue.use(filter);

// use custom $api
import httpApi from '@/utils/httpApi.js'
Vue.use(httpApi);

Vue.config.productionTip = false;

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  i18n,
  components: { App },
  template: '<App/>'
})
