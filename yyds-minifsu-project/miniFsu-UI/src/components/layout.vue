<template>
  <div class="container flex">

    <div class="side-wrapper">
      <v-sidebar></v-sidebar>
    </div>

    <!-- <div class="header-wrapper">
      <v-header></v-header>
    </div> -->

    <div class="content-wrapper">
      <!--<keep-alive>-->
        <router-view></router-view>
      <!--</keep-alive>-->
    </div>

  </div>
</template>

<script>
  import sidebar from './sidebar/sidebar.vue';
  // import header from './header/header.vue';
  export default {
    name: 's-main-layout',
    components: {
      'v-sidebar': sidebar,
      // 'v-header': header
    },
  }
</script>

<style lang="stylus" rel="stylesheet/stylus">
  .container
    width: 100%
    height: 100%
    .side-wrapper
      position: absolute;
      top: 56px;
      bottom: 0
      overflow: auto
      background: #545c64
    .header-wrapper
      position absolute
      left: 0
      top: 0
      right: 0
      height: 56px
    .content-wrapper
      display flex
      position absolute
      left: 180px
      top: 56px
      right: 0
      bottom: 0
      background: #f3f3f3
      // overflow: auto;
      &>div
        min-width:  1000px;
        width: 100%;
        margin: 15px 20px 0 20px;
        flex: 1
</style>
