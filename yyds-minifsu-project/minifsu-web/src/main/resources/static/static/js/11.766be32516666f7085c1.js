webpackJsonp([11],{RO7F:function(t,e){},VG4s:function(t,e,n){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var s=n("aA9S"),i=n.n(s),a={name:"s-sites",props:{},data:function(){return{searcher:{sn:""},multipleSelection:[],fsuList:[],userGroups:[],columns:[{label:"SN 编码",value:"sN",filter:"nullFilter",className:""},{label:"绑定状态",value:"bindStatus",filter:"nullFilter",className:""},{label:"在线状态",value:"status",filter:"statusFilter",className:""}],modifyCont:{},pagination:{total:0,currentPage:1,pageSize:15,onChange:this.onPaginationChanged},loading:!1}},computed:{optionBind:function(){return{name:"绑定",form:{"SN ID":[{type:"span",text:this.modifyCont.sN,rule:{}}],"FSU ID":[{type:"input",name:"fsuId",defaultValue:"43048243800189",disabled:!0,rule:{}}],"FSU 设备列表":[{type:"textarea",name:"devIds",defaultValue:"43048243800189\n43048240700215\n43048240600130\n43048241820217\n43048243700210\n43048241800169\n43048241830216\n43048241840216\n43048241810169\n43048241500109\n43048241900261\n43048249900010\n43048244500016\n43048241860244",rows:8,rule:{}}]},clearText:this.$t("OPERATION.CLEAR"),clear:this.clear,executeText:this.$t("OPERATION.CONFIRM"),execute:this.executeModify,style:{}}}},methods:{addMockData:function(){for(var t=0;t<10;t++)this.fsuList.push({username:"某某某",password:"123456",group_name:"城管大队一组",comment:"**********",sN:"123123123123123123"});this.pagination.total=this.fsuList.length},getFsuList:function(){var t=this,e={sn:this.searcher.sn,page:this.pagination.currentPage,count:this.pagination.pageSize};this.$api.getFsuList(e).then(function(e){e.data&&(t.pagination.total=e.data.totalSize,t.fsuList=e.data.list,t.loading=!1)},function(e){e&&(t.loading=!1)})},onPaginationChanged:function(t){this.pagination.pageSize=t.pageSize,this.pagination.currentPage=t.currentPage,this.getFsuList()},handleSelectionChange:function(t){this.multipleSelection=this.comFunc.map(t,"username")},intoNextPage:function(){},executeAdd:function(t){},executeModify:function(t){this.modifyCont=i()({},this.modifyCont,t),this.modifyCont.devIds=t.devIds.split("\n"),this.setFsu(this.modifyCont)},clear:function(t){},showDialog:function(t,e){e&&(this.modifyCont=e),this.$refs[t].toModal()},setFsu:function(t){var e={fsuId:t.fsuId,devCodeList:t.devIds,sn:t.sN,name:"test_fsu",address:"address",setUpTime:(new Date).getTime(),vpnName:"全国3",fsuClass:"",imsi:"imsi",operators:"yytd",heartCycle:10,businessRhythm:100,runStatusRhythm:50,alarmRhythm:1e3,coordinate:"120.261175,30.317344"};this.$api.setFsu(e).then(function(){})},deleteContents:function(t){var e=this,n={users:this.multipleSelection};this.appHttp.postDeleteUsers(n).then(function(){t&&t.call(e)})},deleteContentsNeedValidated:function(){var t=this;0===this.multipleSelection.length?this.$message({message:"请选择删除项",type:"warning",showClose:!0}):this.$confirm("此操作将永久删除所选项, 是否继续?","提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then(function(){t.deleteContents(function(){this.getUsers()})}).catch(function(){t.$message({type:"info",message:"已取消删除",showClose:!0})})}},mounted:function(){this.getFsuList(!0)}},o={render:function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("div",{staticClass:"fsuList flex-column"},[n("operation-bar-layout",{staticStyle:{}},[n("div",{attrs:{slot:"query"},slot:"query"},[n("el-form",{ref:"searcher",attrs:{model:t.searcher,"label-position":"right",inline:!0}},[n("el-form-item",{attrs:{label:"SN",prop:"sn","label-width":"35px"}},[n("el-input",{attrs:{placeholder:"请输入SN"},model:{value:t.searcher.sn,callback:function(e){t.$set(t.searcher,"sn",e)},expression:"searcher.sn"}})],1)],1)],1),t._v(" "),n("div",{attrs:{slot:"operate"},slot:"operate"},[n("el-button",{attrs:{type:"primary"},on:{click:t.getFsuList}},[t._v("查询")])],1)]),t._v(" "),n("table-box",{staticClass:"flex-1",attrs:{"row-class-name":"cursor-point","row-click":t.intoNextPage,loading:t.loading,stripe:!0,border:!0,pagination:t.pagination,data:t.fsuList},on:{"selection-change":t.handleSelectionChange}},[n("el-table-column",{attrs:{type:"selection"}}),t._v(" "),t._l(t.columns,function(e){return n("el-table-column",{key:e.label,attrs:{formatter:t.$tableFilter(e.filter),prop:e.value,label:e.label,"min-width":e.width,fixed:e.fixed,className:e.className,resizable:!1}})}),t._v(" "),n("el-table-column",{attrs:{label:t.$t("SECURITY.USER.OPERATION"),width:"400"},scopedSlots:t._u([{key:"default",fn:function(e){return[n("div",[n("i",{staticStyle:{color:"#20A0FF",cursor:"pointer"},on:{click:function(n){return t.showDialog("bindDialog",e.row)}}},[t._v("绑定")]),t._v(" "),n("router-link",{staticStyle:{color:"#20A0FF",cursor:"pointer"},attrs:{to:{path:"/devices",query:{sN:e.row.sN}},tag:"span"}},[t._v("\n            / 查看设备\n          ")]),t._v(" "),2===e.row.status?n("router-link",{staticStyle:{color:"#20A0FF",cursor:"pointer"},attrs:{to:{path:"/alarmsForSN",query:{sN:e.row.sN}},tag:"span"}},[t._v("\n            / 实时告警\n          ")]):t._e(),t._v(" "),n("router-link",{staticStyle:{color:"#20A0FF",cursor:"pointer"},attrs:{to:{path:"/fsuInfoList",query:{sN:e.row.sN}},tag:"span"}},[t._v("\n            / SN 运行状态\n          ")]),t._v(" "),n("router-link",{staticStyle:{color:"#20A0FF",cursor:"pointer"},attrs:{to:{path:"/fsuPKT",query:{sN:e.row.sN}},tag:"span"}},[t._v("\n            / SN 终端日志\n          ")])],1)]}}])})],2),t._v(" "),n("modal",{ref:"bindDialog",attrs:{option:t.optionBind}})],1)},staticRenderFns:[]};var r=n("C7Lr")(a,o,!1,function(t){n("RO7F")},null,null);e.default=r.exports}});