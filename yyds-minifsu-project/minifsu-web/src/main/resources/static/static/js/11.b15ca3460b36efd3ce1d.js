webpackJsonp([11],{GNlV:function(e,t,n){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var s={name:"s-bind-sn",props:{},data:function(){return{form:{name:"",region:"",date1:"",date2:"",delivery:!1,type:[],resource:"",desc:""},url:"/fsu/terminal/import"}},computed:{},methods:{beforeUpload:function(e){console.log(e)},handleOnSuccess:function(e){this.$message({message:"操作成功",type:"success",showClose:!0})},handleOnError:function(e){this.$message({message:"操作失败",type:"error",showClose:!0})},onChange:function(e){console.log(e)}},mounted:function(){}},o={render:function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{staticClass:"binf-sn flex-column"},[n("div",{staticStyle:{background:"#cccccc",width:"100%",height:"2px"}}),e._v(" "),n("el-form",{staticStyle:{margin:"40px 0 0 40px",width:"400px"}},[n("el-upload",{attrs:{drag:"",action:e.url,"before-upload":e.beforeUpload,"on-success":e.handleOnSuccess,"on-change":e.onChange,"on-error":e.handleOnError,multiple:""}},[n("i",{staticClass:"el-icon-upload"}),e._v(" "),n("div",{staticClass:"el-upload__text"},[e._v("将文件拖到此处，或"),n("em",[e._v("点击上传")])]),e._v(" "),n("div",{staticClass:"el-upload__tip",attrs:{slot:"tip"},slot:"tip"},[e._v("只能上传excel文件，且不超过500kb")])])],1)],1)},staticRenderFns:[]};var a=n("C7Lr")(s,o,!1,function(e){n("PzMq")},null,null);t.default=a.exports},PzMq:function(e,t){}});