<template>
  <div class="sidebar">
     <el-menu
      default-active="2"
      class="el-menu-vertical-demo"
      @open="handleOpen"
      @close="handleClose"
      background-color="#545c64"
      text-color="#fff"
      active-text-color="#ffd04b">
      <!-- <el-submenu index="1">
        <template slot="title">
          <i class="el-icon-location"></i>
          <span>导航一</span>
        </template>
        <el-menu-item-group>
          <template slot="title">分组一</template>
          <el-menu-item index="1-1">选项1</el-menu-item>
          <el-menu-item index="1-2">选项2</el-menu-item>
        </el-menu-item-group>
        <el-menu-item-group title="分组2">
          <el-menu-item index="1-3">选项3</el-menu-item>
        </el-menu-item-group>
        <el-submenu index="1-4">
          <template slot="title">选项4</template>
          <el-menu-item index="1-4-1">选项1</el-menu-item>
        </el-submenu>
      </el-submenu> -->
      <el-menu-item index="1">
        <i class="el-icon-menu"></i>
        <router-link to="/fsus" tag="li" class="link-li">
            SN 列表
        </router-link>
      </el-menu-item>
      <!-- <el-menu-item index="2">
        <i class="el-icon-menu"></i>
        <router-link to="/fsuInfoList" tag="li" class="link-li">
            SN 基本信息
        </router-link>
      </el-menu-item>
      <el-menu-item index="3">
        <i class="el-icon-menu"></i>
        <router-link to="/fsuPKT" tag="li" class="link-li">
            SN PKT信息
        </router-link>
      </el-menu-item> -->
      <el-menu-item index="4">
        <i class="el-icon-menu"></i>
        <router-link to="/bindSN" tag="li" class="link-li">
            SN 导入
        </router-link>
      </el-menu-item>
      <el-menu-item index="5">
        <i class="el-icon-menu"></i>
        <router-link to="/importDataForTest" tag="li" class="link-li">
            数据导入(测试)
        </router-link>
      </el-menu-item>
      <!-- <el-menu-item index="4">
        <i class="el-icon-setting"></i>
        <span slot="title">导航四</span>
      </el-menu-item>
      <el-menu-item index="5">
        <i class="el-icon-document"></i>
        <span slot="title">导航五</span>
      </el-menu-item>
      <el-menu-item index="6">
        <i class="el-icon-setting"></i>
        <span slot="title">导航六</span>
      </el-menu-item> -->
    </el-menu>
  </div>
</template>

<script>
  export default {
    name: 'sidebar',
    data() {
      return {
      }
    },
    computed: {
      authModules() {
        // return JSON.parse(sessionStorage.getItem('user')).modules;
      },
      defaultActive () {
        // if (this.comFunc.authorityMap(1000, authModules)) {
        //   return '1-1'
        // }
        // return "1-3"
      },
    },
    methods: {
      handleOpen(key, keyPath) {
        // console.log(key, keyPath);
      },
      handleClose(key, keyPath) {
        // console.log(key, keyPath);
      }
    }
  }
</script>

<style lang="stylus" rel="stylesheet/stylus">
  .sidebar
    .icon
      color: #dddddd
      margin-right: 8px
    .el-menu
      width 180px
      .link-li
        display: inline-block
      // .el-submenu__icon-arrow
      //   right 8px
      // .el-submenu
      //   .el-submenu__title
      //     height 40px
      //     line-height 40px
      //   .el-menu-item
      //     height: 40px;
      //     line-height 40px
      //     padding 0 40px 0 50px
      //     min-width 152px
</style>
