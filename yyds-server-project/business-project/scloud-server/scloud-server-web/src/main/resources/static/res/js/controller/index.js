scloudApp.controller("indexController",["$scope","$rootScope","indexService","verifyCodeService",function(o,e,n,r){$state&&($state=window.$state),$stateParams&&($stateParams=window.$stateParams),o.login={form:{},obj:{},toModal:null,execute:null,clear:null},o.forget={form:{},obj:{},toModal:null,execute:null,clear:null,have:!0},o.isLoginPanel=!0,o.login.execute=function(){n.checkLogin({loginName:o.login.obj.loginName,password:o.login.obj.password},function(e){o.pwdErrorInfo="",location.href=ctx+"/res/main.html#"+e},function(e){o.pwdErrorInfo=e})},o.sendPhoneCode=function(e){r.sendForgetPwdPhoneCode(e,function(e){!1===e.success?o.forget.have=!0:o.forget.have=!1})},o.forget.execute=function(){n.resetpwd({phone:o.forget.obj.phone,password:o.forget.obj.password,code:o.forget.obj.messageCode},function(n){e.notify({type:"success",text:"密码重置成功，请登录"}),o.forget.clear(),o.toLogin()},function(e){o.codeErrorInfo=e.info})},o.forget.clear=function(){o.forget.obj={}},o.toLogin=function(){o.isLoginPanel=!0,o.clearErrorInfo()},o.toForgetpwd=function(){o.isLoginPanel=!1,o.clearErrorInfo()},o.clearErrorInfo=function(){o.pwdErrorInfo="",o.codeErrorInfo=""}}]);