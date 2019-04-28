<template>
  <el-breadcrumb separator="/" style="margin-bottom: 15px">
      <el-breadcrumb-item v-for="(item, index) in urls" :key="index" :to="{path: item.path, query: item.query}">{{item.name}}</el-breadcrumb-item>
  </el-breadcrumb>
</template>

<script>
export default {
  name: 'sc-breadcrumb',
  data () {
    return {}
  },
  props: {
    // 带有默认值的对象
    // obj: {
    //   type: Object,
    //   // 对象或数组默认值必须从一个工厂函数获取
    //   default: function () {
    //     return { message: 'hello' }
    //   }
    // },
    urls: {
      type: Array,
      default: function() {
        return [
          {name: '首页', path: '/'},
          {name: '数据展示页', path: undefined} // 路由跳转的值为undefined 路由不跳转。
        ]
      }
    }
  },
  mounted () {
  }
}
</script>

<style scoped lang = "stylus">

</style>
