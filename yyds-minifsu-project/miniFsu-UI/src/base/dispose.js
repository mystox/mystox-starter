// 只在云管中心使用
export default {
  install (Vue) {
    delete Vue.prototype.$tableFilter
    delete Vue.prototype.$tree
    for (var i in Vue.prototype.$api) {
      if (Vue.prototype.$api[i].toDelete) {
        delete Vue.prototype.$api[i];
      }
    }
  }
}
