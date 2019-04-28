<template>
  <div class="header">
    <div v-if="comFunc.authorityMap(2000, authModules)" class="brand" style="background: #ffffff">
      <img src="./logo-cmcc.png" alt="">
    </div>
    <div v-else-if="comFunc.authorityMap([1000, 4000], authModules)" class="brand" style="background: #256AAA;">
      <img style="width: 130px;" src="./logo-home.png" alt="logo">
    </div>
    <ul class="nav-bar">
      <li class="userInfo">
        <span class="icon iconfont icon-user icon-lg"></span>
       {{username ? username : '-'}}
      </li>
     <!--  <li @click="dialogVisible = true">
        <i class="icon iconfont icon-shuoming icon-lg"></i><span >{{$t('NAV.NAV_INSTRUCTION')}}</span>
      </li> -->
      <li v-if="authModules && comFunc.authorityMap(null, authModules)">
        <el-select v-model="selectedLang"  @change="handleSelectedChange">
          <el-option v-for="item in langOptions"
                     :label="item.label"
                     :key="item.value"
                     :value="item.value">
          </el-option>
        </el-select>
      </li>

      <li class="logout" @click="toLogout()">
        <i class="icon iconfont icon-tuichu icon-lg"></i><span >{{$t('NAV.NAV_LOGOUT')}}</span>
      </li>
    </ul>
    <el-dialog
      title="提示"
      :visible.sync="dialogVisible"
      width="100%">
      <v-instruction></v-instruction>
    </el-dialog>
  </div>
</template>

<script>
  import Instruction from '../instruction/instruction'
  export default {
    name: 'v-header',
    components: {
      "v-instruction": Instruction,
    },
    props: {
    },
    data () {
      return {
        activeIndex: '1',
        activeIndex2: '1',
        dialogVisible: false,
        langOptions: [{
          value: 'zh-CN',
          label: '简体中文'
        },{
          value: 'en',
          label: 'English'
        }],
        selectedLang: this.$storage.getLocalItem('localLanguage')
      };
    },
    computed: {
      username() {
        let username = "";
        username = JSON.parse(sessionStorage.getItem("username"));
        return username
      },
      authModules() {
        return JSON.parse(sessionStorage.getItem('user')).modules;
      },
  },
    methods: {
      handleSelectedChange(val) {
        this.$i18n.locale = val;
        this.$storage.setLocalItem('localLanguage', val);  // 让页面重新加载的时候语言保持一致
      },
      /*
       * 注销登录
       * */
      toLogout() {
        sessionStorage.removeItem('user');    // 清除session中的用户信息
        this.$router.push('/');    // 路由跳转到登录页。
        window.location.reload();
      },
      /*
      * 选中导航之后触发
      * */
      handleSelect(key, keyPath) {
        // console.log(key, keyPath);
      },
    },
    created() {

    }
  }
</script>

<style lang="stylus" rel="stylesheet/stylus">
  .header
    position: relative;
    height: 56px;
    background: #ffffff;
    display flex
    .brand
      height: 56px
      text-align center
      width: 180px;
      img
        vertical-align -28px;
        width: 180px;
    .el-dialog__wrapper
      min-width: 1280px;
      z-index: 1000;
      .el-dialog
        background: transparent;
        margin-top: 20vh !important;
        box-shadow: 0 0px 0px rgba(0,0,0,0);
        .el-dialog__header
          display: none;
        .el-dialog__body
          padding: 0 !important;
        .el-dialog_footer
          display: none;
    .icon
      color: #2368B0
    .nav-bar
      height: 56px;
      line-height: 56px;
      position: absolute;
      right: 22px;
      li
        margin-right: 20px;
        display: inline-block;
        &:nth-of-type(2)
          cursor: pointer;
        &:nth-of-type(3)
          cursor: pointer;
        &:nth-of-type(4)
          cursor: pointer;
        span
          margin-left: 6px;
        .el-input
          input
            width: 100px;
            border: transparent;
</style>
