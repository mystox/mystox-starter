<template>
  <div class="query-form-item">
    <span v-if = "!!label" class="query-form-item-label">{{label}}</span>
    <div class="query-form-item-content">
      <slot></slot>
    </div>
  </div>
</template>

<script>
export default {
  name: 'queryFormItem',
  props: {
    label: String
  }
}
</script>

<style lang='stylus'>
.query-form-item
  margin-right -5px
  display inline-block
  overflow hidden
  .query-form-item-label
    vertical-align 18px
    color #3AB0FF
  .query-form-item-content
    overflow hidden
    display inline-block
    height 30px
    margin 8px 16px 8px 0px
    & > .btn
      min-width 80px
    & > .el-input, & > .el-select .el-input
      width 166px
      border-radius 0
      background-color: #121D33
      color: #FFF
      input
        border-radius 0
        padding 0 6px
        border-color: #121D33
        height 30px
        color #fff
        &:focus
          box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075), 0 0 8px rgba(82, 168, 236, 0.6);
    & > .date-time-picker
      width 166px
      background-color: #121D33
      .el-input__inner
        color #fff
      .el-input__icon
        line-height 30px
        margin-left -5px
      input
        border-radius 0
        padding 0 6px 0 22px
        border-color: #121D33
        height 30px

</style>
