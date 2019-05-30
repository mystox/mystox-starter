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