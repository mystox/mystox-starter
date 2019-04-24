<template>
  <div class="query-form">
    <slot></slot>
  </div>
</template>

<script>
export default {
  name: 'queryForm',
  data () {
    return {}
  }
}
</script>

<style lang='stylus'>
gap = 24px
.query-form
  padding (gap / 2) 0 (gap / 2) 30px
</style>
