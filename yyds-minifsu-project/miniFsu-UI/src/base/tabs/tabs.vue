<template>
  <el-tabs class = 'tabs' :type="type" :value = 'value' @input="handleInput" @tab-click="handleTabClick" >
    <slot />
  </el-tabs>
</template>

<script>
export default {
  name: 'tabs',
  methods: {
    handleInput (v) {
      this.$emit('input', v)
    },
    handleTabClick (v) {
      this.$emit('tab-click', v)
    }
  },
  props: ['type', 'value']
}
</script>

<style lang = 'stylus'>
.tabs
  display flex
  flex-direction column
  .el-tabs__content
    flex 1
    .el-tab-pane
      height 100%
      & > *
        height 100%
  .el-tabs__nav-scroll
    margin-bottom: 1px;
  &.el-tabs--card>.el-tabs__header
    margin-bottom: 0;
    border-bottom-color: #3AB0FF;
    .el-tabs__item, .el-tabs__nav //
      border none
      border-left none
      border-right none
      background-color #152545
      border-radius 0
    .el-tabs__item
      padding: 5px 12px;
      height 30px
      line-height: 20px;
      color: #fff;
      &.is-active, &:hover
        background-color: #3AB0FF;
.full-screen
  .el-tab-pane, .el-tabs__content
    overflow: visible; // 为了让alarm/list中的table能够透过tabs显示
</style>
