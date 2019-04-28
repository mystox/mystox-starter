<template>
  <div class="operation-wrapper flex flex-content-between" :style="mapStyle">
    <div class="query flex ">
      <slot name="query"></slot>
    </div>
    <div class="operate">
      <slot name="operate"></slot>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'operation-bar-layout',
    props: ['mapStyle']
  }
</script>

<style lang="stylus" rel="stylesheet/stylus">
  .operation-wrapper
    width: 100%;
    margin: 0
    .operate
      white-space: nowrap;  // 右侧的操作按钮不换行
</style>
