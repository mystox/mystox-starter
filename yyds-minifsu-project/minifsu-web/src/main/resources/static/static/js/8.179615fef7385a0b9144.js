webpackJsonp([8],{"6+lM":function(e,t){},IVV0:function(e,t,n){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var o={render:function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{staticClass:"alarmList flex-column"},[n("sc-breadcrumb",{attrs:{urls:[{name:"SN列表"},{name:"设备列表",path:"/devices",query:{sN:e.$route.query.sn}},{name:"告警门限设置"}]}}),e._v(" "),n("operation-bar-layout",{staticStyle:{}},[n("div",{attrs:{slot:"query"},slot:"query"},[n("el-form",{ref:"searcher",attrs:{model:e.searcher,"label-position":"right",inline:!0}},[n("el-form-item",{attrs:{label:"告警名称",prop:"name","label-width":"70px"}},[n("el-input",{attrs:{placeholder:"请输入告警名称"},model:{value:e.searcher.sn,callback:function(t){e.$set(e.searcher,"sn",t)},expression:"searcher.sn"}})],1)],1)],1),e._v(" "),n("div",{attrs:{slot:"operate"},slot:"operate"},[n("el-button",{attrs:{type:"primary"},on:{click:function(t){return e.goSearch(!1)}}},[e._v("查询")])],1)]),e._v(" "),n("table-box",{staticClass:"flex-1",attrs:{"row-class-name":"cursor-point","row-click":e.intoNextPage,loading:e.loading,stripe:!0,border:!0,pagination:e.pagination,data:e.alarmList},on:{"selection-change":e.handleSelectionChange}},[n("el-table-column",{attrs:{type:"selection"}}),e._v(" "),e._l(e.columns,function(t){return n("el-table-column",{key:t.label,attrs:{formatter:e.$tableFilter(t.filter),prop:t.value,label:t.label,"min-width":t.width,fixed:t.fixed,className:t.className,resizable:!1}})}),e._v(" "),n("el-table-column",{attrs:{label:e.$t("SECURITY.USER.OPERATION"),fixed:"right"},scopedSlots:e._u([{key:"default",fn:function(t){return[n("i",{staticStyle:{color:"#20A0FF",cursor:"pointer"},on:{click:function(n){return e.showDialog("threshold",t.row)}}},[e._v("门限设置")])]}}])})],2),e._v(" "),n("modal",{ref:"threshold",attrs:{option:e.optionModify}})],1)},staticRenderFns:[]};var a=n("C7Lr")({name:"s-points",props:{},data:function(){return{searcher:{name:""},multipleSelection:[],alarmList:[],userGroups:[],columns:[{label:"告警点 ID",value:"alarmId",filter:"nullFilter",className:""},{label:"门限值",value:"threshold",filter:"nullFilter",className:""}],modifyCont:{username:"",password:"",group_name:"",comment:""},pagination:{total:0,currentPage:1,pageSize:15,onChange:this.onPaginationChanged},loading:!1}},computed:{optionModify:function(){return{name:"门限设置",form:{"门限值":[{type:"input",name:"threshold",defaultValue:this.modifyCont.threshold,rule:{required:!0,requiredError:"输入内容不可为空。"}}]},clearText:this.$t("OPERATION.CLEAR"),clear:this.clear,executeText:this.$t("OPERATION.CONFIRM"),execute:this.executeModify,style:{}}}},methods:{addMockData:function(){for(var e=0;e<10;e++)this.alarmList.push({username:"pointName",password:"123456",group_name:"城管大队三组",comment:"**********"});this.pagination.total=this.alarmList.length},getThreshold:function(){var e=this;this.$api.getThreshold({port:this.$route.query.port,resNo:this.$route.query.resNo-0,type:this.$route.query.type-0},this.$route.query.sn).then(function(t){e.pagination.total=t.data.length,e.alarmList=t.data,e.loading=!1},function(t){t&&(e.loading=!1)})},onPaginationChanged:function(e){this.pagination.pageSize=e.pageSize,this.pagination.currentPage=e.currentPage,this.getUsers()},handleSelectionChange:function(e){this.multipleSelection=this.comFunc.map(e,"username")},executeModify:function(e){this.updateContent(e)},clear:function(e){},showDialog:function(e,t){t&&(this.modifyCont=t),this.$refs[e].toModal()},intoNextPage:function(){},updateContent:function(e){var t=this,n={deviceId:this.modifyCont.deviceId,configId:this.modifyCont.id,coId:this.modifyCont.coId,threshold:e.threshold-0};this.$api.setThreshold(n,this.$route.query.sn).then(function(){t.getThreshold()})},deleteContents:function(e){var t=this,n={users:this.multipleSelection};this.appHttp.postDeleteUsers(n).then(function(){e&&e.call(t)})},deleteContentsNeedValidated:function(){var e=this;0===this.multipleSelection.length?this.$message({message:"请选择删除项",type:"warning",showClose:!0}):this.$confirm("此操作将永久删除所选项, 是否继续?","提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then(function(){e.deleteContents(function(){this.getUsers()})}).catch(function(){e.$message({type:"info",message:"已取消删除",showClose:!0})})}},mounted:function(){this.getThreshold()}},o,!1,function(e){n("6+lM")},null,null);t.default=a.exports}});