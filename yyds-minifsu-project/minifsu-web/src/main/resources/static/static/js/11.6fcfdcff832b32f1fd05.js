webpackJsonp([11],{A74C:function(t,e){},VG4s:function(t,e,a){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var i=a("aA9S"),n=a.n(i),s={name:"s-sites",props:{},data:function(){return{searcher:{sn:""},multipleSelection:[],fsuList:[],columns:[{label:"SN 编码",value:"sN",filter:"nullFilter",className:""},{label:"绑定状态",value:"bindMark",filter:"bindMarkFilter",className:""},{label:"在线状态",value:"status",filter:"statusFilter",className:""},{label:"已绑定FSU ID",value:"fsuId",filter:"nullFilter",className:""},{label:"SN 别名",value:"name",filter:"nullFilter",className:""},{label:"适配层版本号",value:"adapterVer",filter:"nullFilter",className:""},{label:"引擎版本号",value:"engineVer",filter:"nullFilter",className:""}],modifyCont:{},typeList:[{label:"引擎",value:1},{label:"适配层",value:2}],pagination:{total:0,currentPage:1,pageSize:15,onChange:this.onPaginationChanged},upgradeParam:{type:null,documentObj:""},loading:!1,allDocument:[],documentList:[],disabled:!0,upgradeDialogVisible:!1,compilerInfo:{engine:"",program:""}}},computed:{optionBind:function(){return{name:"绑定",form:{"SN ID":[{type:"span",text:this.modifyCont.sN,rule:{}}],"FSU ID":[{type:"input",name:"fsuId",defaultValue:this.modifyCont.fsuId||"",disabled:!0,rule:{}}],"FSU 设备列表":[{type:"textarea",name:"devIds",rows:8,rule:{}}]},clearText:this.$t("OPERATION.CLEAR"),clear:this.clear,executeText:this.$t("OPERATION.CONFIRM"),execute:this.executeModify,style:{}}}},methods:{addMockData:function(){for(var t=0;t<10;t++)this.fsuList.push({username:"某某某",password:"123456",group_name:"城管大队一组",comment:"**********",sN:"123123123123123123"});this.pagination.total=this.fsuList.length},goSearch:function(){this.pagination.currentPage=1,this.getFsuList()},getFsuList:function(){var t=this,e={sn:this.searcher.sn,page:this.pagination.currentPage,count:this.pagination.pageSize};this.$api.getFsuList(e).then(function(e){e.data&&(t.pagination.total=e.data.totalSize,t.fsuList=e.data.list,t.loading=!1)},function(e){e&&(t.loading=!1)})},getDocumentList:function(){var t=this;this.$api.getDocumentList().then(function(e){t.allDocument=e.data}.bind(this))},handleChange:function(t){var e=this;this.upgradeParam.documentObj={},this.documentList=this.allDocument.filter(function(e){return e.name===(1===t?"engine":"program")})[0].list,0!==this.documentList.length&&this.documentList.forEach(function(t){e.$set(t,"label",t.name),e.$set(t,"value",t)})},compiler:function(){var t=this,e={type:this.upgradeParam.type};this.$api.compiler(e,this.modifyCont.sN).then(function(e){1===e.data.result&&(1===t.upgradeParam.type?(t.compilerInfo.engine="编译成功,文件名为"+e.data.fileName+",文件大小为"+e.data.totalLen,t.delOption(1),t.paramForUpgradeEngine={file:{fileName:e.data.fileName,totalLen:e.data.totalLen,type:t.upgradeParam.type,md5:"12342jlagjkljl24gajklgj"}}):2===t.upgradeParam.type&&(t.compilerInfo.program="编译成功,文件名为"+e.data.fileName+",文件大小为"+e.data.totalLen,t.delOption(2),t.paramForUpgradeProgram={file:{fileName:e.data.fileName,totalLen:e.data.totalLen,type:t.upgradeParam.type,md5:"12342jlagjkljl24gajklgj"}})),t.$refs.upgradeDialog.resetFields()}.bind(this))},delOption:function(t){this.typeList=this.typeList.filter(function(e){return e.value!=t})},upgrade:function(){var t=this;this.compilerInfo.engine&&this.$api.upgrade(this.paramForUpgradeEngine,this.modifyCont.sN).then(function(e){t.upgradeDialogVisible=!1}),this.$nextTick(function(){t.compilerInfo.program&&t.$api.upgrade(t.paramForUpgradeProgram,t.modifyCont.sN).then(function(e){t.upgradeDialogVisible=!1})})},handleCancel:function(){this.upgradeDialogVisible=!1,this.$refs.upgradeDialog.resetFields(),this.compilerInfo={engine:"",program:""},this.typeList=[{label:"引擎",value:1},{label:"适配层",value:2}]},onPaginationChanged:function(t){this.pagination.pageSize=t.pageSize,this.pagination.currentPage=t.currentPage,this.getFsuList()},handleSelectionChange:function(t){this.multipleSelection=this.comFunc.map(t,"username")},intoNextPage:function(){},executeAdd:function(t){},executeModify:function(t){this.modifyCont=n()({},this.modifyCont,t),t.devIds&&(this.modifyCont.devIds=t.devIds.split("\n")),this.setFsu(this.modifyCont)},executeUpgrade:function(t){this.upgradeParam=n()({},t),this.upgrade()},clear:function(t){},showDialog:function(t,e){e&&(this.modifyCont=e),this.$refs[t]&&this.$refs[t].toModal()},showUpgradeDialog:function(t){t&&(this.modifyCont=t),this.upgradeDialogVisible=!0},setFsu:function(t){var e=this,a={fsuId:t.fsuId,devCodeList:t.devIds||[],sn:t.sN,name:t.name,address:t.address,setUpTime:(new Date).getTime(),vpnName:"全国7",fsuClass:t.fsuClass,imsi:t.imsi,operators:t.operaters,heartCycle:t.heartCycle,businessRhythm:t.businessRhythm,runStatusRhythm:t.runStatusRhythm,alarmRhythm:t.alarmRhythm,coordinate:t.coordinate};this.$api.setFsu(a).then(function(t){e.getFsuList()})},unbind:function(t){var e=this,a={fsuId:t.fsuId};this.$confirm("是否解绑该设备？","提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then(function(){e.$api.unbind(a,t.sN).then(function(t){e.getFsuList()})}.bind(this))}},mounted:function(){this.getFsuList(!0),this.getDocumentList()}},o={render:function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{staticClass:"fsuList flex-column"},[a("operation-bar-layout",{staticStyle:{}},[a("div",{attrs:{slot:"query"},slot:"query"},[a("el-form",{ref:"searcher",attrs:{model:t.searcher,"label-position":"right",inline:!0}},[a("el-form-item",{attrs:{label:"SN",prop:"sn","label-width":"35px"}},[a("el-input",{attrs:{placeholder:"请输入SN"},model:{value:t.searcher.sn,callback:function(e){t.$set(t.searcher,"sn","string"==typeof e?e.trim():e)},expression:"searcher.sn"}})],1)],1)],1),t._v(" "),a("div",{attrs:{slot:"operate"},slot:"operate"},[a("el-button",{attrs:{type:"primary"},on:{click:t.goSearch}},[t._v("查询")])],1)]),t._v(" "),a("table-box",{staticClass:"flex-1",attrs:{"row-class-name":"cursor-point","row-click":t.intoNextPage,loading:t.loading,stripe:!0,border:!0,pagination:t.pagination,data:t.fsuList},on:{"selection-change":t.handleSelectionChange}},[a("el-table-column",{attrs:{type:"selection"}}),t._v(" "),t._l(t.columns,function(e){return a("el-table-column",{key:e.label,attrs:{formatter:t.$tableFilter(e.filter),prop:e.value,label:e.label,"min-width":e.width,fixed:e.fixed,className:e.className,resizable:!1}})}),t._v(" "),a("el-table-column",{attrs:{label:t.$t("SECURITY.USER.OPERATION"),width:"550"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("div",[a("i",{staticStyle:{color:"#20A0FF",cursor:"pointer"},on:{click:function(a){return t.showDialog("bindDialog",e.row)}}},[t._v("/ 绑定")]),t._v(" "),e.row.bindMark?a("i",{staticStyle:{color:"#20A0FF",cursor:"pointer"},on:{click:function(a){return t.unbind(e.row)}}},[t._v("/ 解绑")]):t._e(),t._v(" "),2===e.row.status?a("i",{staticStyle:{color:"#20A0FF",cursor:"pointer"},on:{click:function(a){return t.showUpgradeDialog(e.row)}}},[t._v("/ 升级")]):t._e(),t._v(" "),a("router-link",{staticStyle:{color:"#20A0FF",cursor:"pointer"},attrs:{to:{path:t._ctx+"/devices",query:{sN:e.row.sN}},tag:"span"}},[t._v("\n            / 查看设备\n          ")]),t._v(" "),2===e.row.status?a("router-link",{staticStyle:{color:"#20A0FF",cursor:"pointer"},attrs:{to:{path:t._ctx+"/alarmsForSN",query:{sN:e.row.sN}},tag:"span"}},[t._v("\n            / 实时告警\n          ")]):t._e(),t._v(" "),a("router-link",{staticStyle:{color:"#20A0FF",cursor:"pointer"},attrs:{to:{path:t._ctx+"/fsuInfoList",query:{sN:e.row.sN}},tag:"span"}},[t._v("\n            / SN 运行状态\n          ")]),t._v(" "),a("router-link",{staticStyle:{color:"#20A0FF",cursor:"pointer"},attrs:{to:{path:t._ctx+"/fsuPKT",query:{sN:e.row.sN}},tag:"span"}},[t._v("\n            / SN 终端日志\n          ")])],1)]}}])})],2),t._v(" "),a("el-dialog",{attrs:{title:"升级",visible:t.upgradeDialogVisible,"close-on-click-modal":!1},on:{"update:visible":function(e){t.upgradeDialogVisible=e},close:t.handleCancel}},[a("el-form",{ref:"upgradeDialog",attrs:{model:t.upgradeParam,"label-width":"125px"}},[a("el-form-item",{attrs:{label:"SN ID"}},[a("span",[t._v(t._s(t.modifyCont.sN))])]),t._v(" "),a("el-form-item",{directives:[{name:"show",rawName:"v-show",value:0!=t.typeList.length,expression:"typeList.length != 0"}],attrs:{label:"选择升级类型",prop:"type"}},[a("el-select",{staticStyle:{width:"200px"},attrs:{"auto-complete":"off"},on:{change:t.handleChange},model:{value:t.upgradeParam.type,callback:function(e){t.$set(t.upgradeParam,"type",e)},expression:"upgradeParam.type"}},t._l(t.typeList,function(t,e){return a("el-option",{key:e,attrs:{label:t.label,value:t.value}})}),1)],1),t._v(" "),a("el-form-item",{directives:[{name:"show",rawName:"v-show",value:t.upgradeParam.type&&0!=t.typeList.length,expression:"upgradeParam.type && typeList.length != 0"}],attrs:{label:"后端编译"}},[a("el-button",{attrs:{type:"primary"},on:{click:t.compiler}},[t._v(" 编译 ")])],1),t._v(" "),t.compilerInfo.engine?a("el-form-item",{attrs:{label:"引擎编译结果"}},[a("span",[t._v(t._s(t.compilerInfo.engine))])]):t._e(),t._v(" "),t.compilerInfo.program?a("el-form-item",{attrs:{label:"适配层编译结果"}},[a("span",{},[t._v(t._s(t.compilerInfo.program))])]):t._e()],1),t._v(" "),a("div",{attrs:{slot:"footer"},slot:"footer"},[a("el-button",{nativeOn:{click:function(e){return t.handleCancel(e)}}},[t._v("取消")]),t._v(" "),a("el-button",{attrs:{type:"primary",disabled:!(t.compilerInfo.engine||t.compilerInfo.program)},nativeOn:{click:function(e){return t.upgrade(e)}}},[t._v("确定")])],1)],1),t._v(" "),a("modal",{ref:"bindDialog",attrs:{option:t.optionBind}})],1)},staticRenderFns:[]};var r=a("C7Lr")(s,o,!1,function(t){a("A74C")},null,null);e.default=r.exports}});