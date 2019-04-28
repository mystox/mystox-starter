<template>
  <div class="login">
    <el-row>
      <el-col :span="24">
        <div class="form-wrapper">
          <!--logo-->
          <div class="login-logo">
            <!-- <img src="./logo-login.png" alt=""> -->
            <h1 class="login-logo-text">miniFsu</h1>
          </div>
          <!--表单-->
          <el-form :model="ruleForm" :rules="loginRules" ref="loginForm" label-width="" class="login-form">
            <!--select-->
            <!--<el-form-item label="" prop="selectedVal">-->
              <!--<el-select v-model="ruleForm.selectedVal" placeholder="语言" @change="handleLanguageSelected">-->
                <!--<el-option v-for="item in langOptions" :key="item.value" :label="item.label"-->
                           <!--:value="item.value"></el-option>-->
              <!--</el-select>-->
            <!--</el-form-item>-->
            <!--&lt;!&ndash;inputs&ndash;&gt;-->
            <el-form-item label="" prop="username">
              <el-input type="text" v-model="ruleForm.username" placeholder="用户名"></el-input>
            </el-form-item>
            <el-form-item label="" prop="password">
              <!-- element-ui 封装组建的时候，阻止了事件传递。所以需所以需要加上native修饰符 -->
              <el-input type="password"
                v-model="ruleForm.password"
                auto-complete="off"
                @keyup.enter.native="submitForm('loginForm')"
                placeholder="密码">
              </el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary"
                @click="submitForm('loginForm')"
                >
                登录
              </el-button>
              <!--<el-button type="primary" @click="submitForm('ruleForm')">登录</el-button>-->
            </el-form-item>
            <el-form-item>
              <el-button type="info" @click="resetForm('loginForm')">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
  export default {
    name: 's-login',
    components: {},
    props: {},
    data () {
      let checkName = (rule, value, callback) => {
        if (!value) {
          return callback(new Error('用户名不可为空'));
        }
        setTimeout(() => {    // 默认添加匹配效果 转圈动画效果，XXXs后消失
          callback();
        }, 1000);
      };

      let validatePass = (rule, value, callback) => {
        if (value === '') {
          callback(new Error('密码不可为空'));
        };
        let byteNum = this.comFunc.getByteNum(value);
        if (this.comFunc.getByteNum(value) > 20) {
          callback(new Error('密码输入过长'));
        }
        setTimeout(() => {
          callback(); // 运行回调，加载动画
        }, 1000);
      };

      return {
        langOptions: [
          {value: 'zh-CN',label: '简体中文'},
          {value: 'en',label: 'English'}
        ],
        user: {},
        ruleForm: {
          selectedVal: '',
          username: '',
          password: '',
        },
        loginRules: {
          username: [
            {validator: checkName, trigger: 'change'}
          ],
          password: [
            {validator: validatePass, trigger: 'change'}
          ],
        }
      }
    },
    methods: {
      handleLanguageSelected(val) {
        this.$i18n.locale = val;
        this.$storage.setLocalItem('localLanguage', val);  // 让页面重新加载的时候语言保持一致
      },
      toLogin() {
        let params = {
          username: this.ruleForm.username,
          password: this.ruleForm.password
        };
        this.$router.push('/fsus');
        // this.appHttp.postLogin(params).then(function (data) {
        //   // this.$storage.setSessionItem('user', data);
        //   // this.$storage.setSessionItem('username', this.ruleForm.username);
        //   this.$emit('user', this.user);
        //   this.$router.push('/main');
        // }.bind(this))
      },
      submitForm(formName) {
        this.toLogin();
        // this.$refs[formName].validate( (valid)=> {
          // valid && this.toLogin();
        // })
      },
      resetForm(formName) {
        this.$refs[formName].resetFields();
      }
    },
  }
</script>

<style lang="stylus" rel="stylesheet/stylus">
  .login
    height 100%
    // background: url(./bg.png) no-repeat
    background-size: 100% 100%
    .form-wrapper
      width: 334px
      height: 380px
      margin-top: 100px
      margin-left: auto
      margin-right: auto
      padding: 30px 30px 10px 30px
      background: #FFFFFF
      border-radius: 2px
      box-shadow: 0px 0px 30px 16px #dddddd
      // box-shadow: 0px 0px 10px 5px #3FA0F9;
      .login-logo
        margin-top: 15px
        height: 70px
        text-align: center
        .login-logo-text
          color: #4283C0;
          font-weight 600
          font-size 45px
        img
          width: 215px
          height: 37px
      .login-form
        .el-select
          width: 100%
        .el-form-item
          .el-button
            width: 100%
        .el-form-item:last-of-type
          .el-button
            background: #FFFFFF;
            border-color: #FFFFFF;
            color: #4382C6;
</style>
