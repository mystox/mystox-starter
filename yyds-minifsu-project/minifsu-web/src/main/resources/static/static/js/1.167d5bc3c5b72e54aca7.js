webpackJsonp([1],{"1o4i":function(e,t,n){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var o={name:"s-devices",props:{},data:function(){return{searcher:{name:""},multipleSelection:[],deviceList:[],userGroups:[],columns:[{label:"设备名称",value:"name",filter:"nullFilter",className:""},{label:"设备 ID",value:"id",filter:"nullFilter",className:""},{label:"所属SN",value:"sN",filter:"nullFilter",className:""}],modifyCont:{username:"",password:"",group_name:"",comment:""},pagination:{total:0,currentPage:1,pageSize:15,onChange:this.onPaginationChanged},loading:!1}},computed:{optionAdd:function(){return{name:"SECURITY.USER.DIALOG_NEW.TITLE",form:{"SECURITY.USER.USER_NAME":[{type:"input",name:"username",defaultValue:"",rule:{required:!0,requiredError:"输入内容不可为空"}}],"SECURITY.USER.USER_PASSWORD":[{type:"input",name:"password",defaultValue:"",rule:{required:!0,requiredError:"输入内容不可为空。"}}],"SECURITY.USER.GROUP_NAME":[{type:"select",name:"group_name",options:this.userGroups.map(function(e){return{value:e.group_name,label:e.group_name}})}],"SECURITY.USER.COMMENT":[{type:"input",name:"comment",defaultValue:"",rule:{}}]},clearText:this.$t("OPERATION.CLEAR"),clear:this.clear,executeText:this.$t("OPERATION.CONFIRM"),execute:this.executeAdd,style:{}}},optionModify:function(){return{name:"SECURITY.USER.DIALOG_MODIFY.TITLE",form:{"SECURITY.USER.USER_NAME":[{type:"span",text:this.modifyCont.username,rule:{}}],"SECURITY.USER.USER_PASSWORD":[{type:"input",name:"password",defaultValue:this.modifyCont.password,rule:{required:!0,requiredError:"输入内容不可为空。"}}],"SECURITY.USER.GROUP_NAME":[{type:"select",name:"group_name",defaultValue:this.modifyCont.group_name,options:this.userGroups.map(function(e){return{value:e.group_name,label:e.group_name}})}],"SECURITY.USER.COMMENT":[{type:"input",name:"comment",defaultValue:this.modifyCont.comment,rule:{}}]},clearText:this.$t("OPERATION.CLEAR"),clear:this.clear,executeText:this.$t("OPERATION.CONFIRM"),execute:this.executeModify,style:{}}}},methods:{addMockData:function(){for(var e=0;e<10;e++)this.deviceList.push({username:"deviceName",password:"123456",group_name:"城管大队二组",comment:"**********"});this.pagination.total=this.deviceList.length},getDeviceList:function(){var e=this;console.log(this.$router);var t={sn:this.$route.query.sN};this.$api.getDeviceList(t,t.sn).then(function(t){e.pagination.total=t.data.length,e.deviceList=t.data,e.loading=!1},function(t){t&&(e.loading=!1)})},onPaginationChanged:function(e){this.pagination.pageSize=e.pageSize,this.pagination.currentPage=e.currentPage,this.getUsers()},handleSelectionChange:function(e){this.multipleSelection=this.comFunc.map(e,"username")},intoNextPage:function(){},executeAdd:function(e){this.addContent(e)},executeModify:function(e){this.updateContent(e)},clear:function(e){},addContent:function(e){var t=this,n={username:e.username,password:e.password,group_name:e.group_name,comment:e.comment};this.appHttp.postAddUser(n).then(function(){t.getUsers()})},showDialog:function(e,t){t&&(this.modifyCont=t),this.$refs[e].toModal()},updateContent:function(e){var t=this,n={username:e.username,password:e.password,group_name:e.password,comment:e.comment};this.appHttp.postUpdateUser(n).then(function(){t.getUsers()})},deleteContents:function(e){var t=this,n={users:this.multipleSelection};this.appHttp.postDeleteUsers(n).then(function(){e&&e.call(t)})},deleteContentsNeedValidated:function(){var e=this;0===this.multipleSelection.length?this.$message({message:"请选择删除项",type:"warning",showClose:!0}):this.$confirm("此操作将永久删除所选项, 是否继续?","提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then(function(){e.deleteContents(function(){this.getUsers()})}).catch(function(){e.$message({type:"info",message:"已取消删除",showClose:!0})})}},mounted:function(){this.getDeviceList()}},a={render:function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{staticClass:"deviceList flex-column"},[n("sc-breadcrumb",{attrs:{urls:[{name:"SN列表",path:e._ctx+"/fsus"},{name:"设备列表"}]}}),e._v(" "),n("table-box",{staticClass:"flex-1",attrs:{"row-class-name":"cursor-point","row-click":e.intoNextPage,loading:e.loading,stripe:!0,border:!0,data:e.deviceList},on:{"selection-change":e.handleSelectionChange}},[n("el-table-column",{attrs:{type:"selection"}}),e._v(" "),e._l(e.columns,function(t){return n("el-table-column",{key:t.label,attrs:{formatter:e.$tableFilter(t.filter),prop:t.value,label:t.label,"min-width":t.width,fixed:t.fixed,className:t.className,resizable:!1}})}),e._v(" "),n("el-table-column",{attrs:{label:e.$t("SECURITY.USER.OPERATION"),fixed:"right",width:"250"},scopedSlots:e._u([{key:"default",fn:function(t){return[n("div",{staticStyle:{"font-size":"14px",color:"#20A0FF",cursor:"pointer"}},[n("router-link",{attrs:{to:{path:e._ctx+"/points",query:{deviceId:t.row.id,resNo:t.row.resNo,type:t.row.type,sN:t.row.sN,dev:t.row.type+"-"+t.row.resNo}},tag:"span"}},[e._v("\n            实时数据\n          ")]),e._v(" "),n("router-link",{attrs:{to:{path:e._ctx+"/alarmsForDevice",query:{deviceId:t.row.id,resNo:t.row.resNo,type:t.row.type,sN:t.row.sN,port:t.row.port,dev:t.row.type+"-"+t.row.resNo}},tag:"span"}},[e._v("\n           / 实时告警\n          ")]),e._v(" "),n("router-link",{attrs:{to:{path:e._ctx+"/setAlarms",query:{deviceId:t.row.id,resNo:t.row.resNo,type:t.row.type,sN:t.row.sN,port:t.row.port}},tag:"span"}},[e._v("\n           / 告警门限设置\n          ")])],1)]}}])})],2),e._v(" "),n("modal",{ref:"addUserInfoDialog",attrs:{option:e.optionAdd}}),e._v(" "),n("modal",{ref:"modifyUserInfoDialog",attrs:{option:e.optionModify}})],1)},staticRenderFns:[]};var r=n("C7Lr")(o,a,!1,function(e){n("VeXy")},null,null);t.default=r.exports},VeXy:function(e,t){}});