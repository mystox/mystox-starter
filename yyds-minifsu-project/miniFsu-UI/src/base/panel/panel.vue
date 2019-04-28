<template>
  <div class="panel card-bg">
    <div class="page-header">
      <h3 v-if = "name">{{name}}</h3>
      <slot name = 'btn-group' class="btn-group" />
    </div>
    <div class="page-body">
      <slot></slot>
    </div>
  </div>
</template>

<script>
export default {
  name: 'panel',
  props: {
    name: String
  }
}
</script>

<style lang = 'stylus'>
.panel
  display flex
  flex-direction column
  .page-header
    height 51px
    padding 0 16px
    background #152545
    border-bottom 1px solid #3F4C66
    display flex
    justify-content space-between
    align-items center
    color: #DDD;
    h3
      font-weight normal
      display inline
    h3
      line-height 18px
      font-size 18px
    s
      display inline-block

  .page-body
    flex 1
    display flex
    flex-direction column
    .table-box
      flex 1

</style>
