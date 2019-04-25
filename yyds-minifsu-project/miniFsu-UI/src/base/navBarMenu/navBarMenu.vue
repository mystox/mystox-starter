<template>
  <div class="navBarMenu">
    <el-menu default-active="main" class="el-menu-demo" mode="horizontal" :router="true">
      <el-submenu index = "main">
        <template slot="title">云服务</template>
        <el-submenu v-for="menu in menus" :key="menu.title" :index="menu.url">
          <template slot="title">
            <i :class="'el-menu--sprite ' + menu.url"></i>
            <span>{{menu.title}}</span>
          </template>
          <el-menu-item v-for="m in menu.submenu" :key = "m.title" :index="m.url">{{m.title}}</el-menu-item>
        </el-submenu>
      </el-submenu>
    </el-menu>
  </div>
</template>

<script>
export default {
  name: 'navBarMenu',
  data () {
    return {}
  },
  methods: {},
  props: {
    menus: {
      required: true
    }
  }

}
</script>

<style lang = 'stylus'>
.navBarMenu
  width 102px
  float left
  > .el-menu
    border none
    > .el-submenu > .el-submenu__title,&
      color #fff
      background-color transparent
      height 56px
      line-height 56px
      color #ddd
      &:hover
        color #008df5
      > i
        display none
.el-submenu__title:hover,.el-submenu.is-opened .el-submenu__title
  background-color #008DF5
  color #fff
  .el-submenu__icon-arrow
    color #fff
  .el-menu--sprite
    background-position-x 0px
.el-menu--horizontal
  border-bottom none
  &.el-zoom-in-top-leave
    .el-menu--horizontal
      display none !important
.el-menu--sprite
  background-image url('./sprite.png')
  background-repeat no-repeat
  display inline-block
  width 30px
  height 30px
.el-menu--sprite.scene
  background-position -30px 0px
.el-menu--sprite.alarm
  background-position -30px -30px
.el-menu--sprite.data
  background-position -30px -58px
.el-menu--sprite.battery
  background-position -30px -90px
.el-menu--sprite.configuration
  background-position -30px -120px
.el-menu--sprite.monitor
  background-position -30px -150px
.el-menu--sprite.entrance
  background-position -30px -180px
.el-menu--sprite.energy
  background-position -30px -210px
.el-menu--sprite.log
  background-position -30px -240px
.el-menu--sprite.report
  background-position -30px -270px
.el-menu--sprite.config
  background-position -30px -300px
.el-menu--sprite.setting
  background-position -30px -330px
.el-menu--sprite.backCenter
  background-position -30px -360px
.el-menu--sprite.backConfig
  background-position -30px -390px
.el-menu--sprite.backServer
  background-position -30px -420px
.el-menu--sprite.home
  background-position -30px -450px
.el-menu--sprite.backUser
  background-position -30px -480px
.el-menu--sprite.operation
  background-position -30px -510px

</style>
