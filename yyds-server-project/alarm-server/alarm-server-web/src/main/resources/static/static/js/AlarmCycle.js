webpackJsonp([3],{PplA:function(e,t){},WGBG:function(e,t,r){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var i=r("woOf"),l=r.n(i),s={name:"alarmCycle",data:function(){var e=this;return{curUser:{strId:"123123",name:"chenht"},curService:JSON.parse(localStorage.curService),curEnterprise:JSON.parse(localStorage.curEnterprise),enterPriseList:[],query:{},cycleList:[],pagination:{page:1,pageSize:15,total:0,handleCurrentChange:function(t){e.pagination.page=t,e.getCycleList()},pageSizeChange:function(t){e.pagination.pageSize=t,e.getCycleList()}},setCycleVisible:!1,setCycleForm:{}}},computed:{setCycleRules:function(){var e=this;return{diffTime:[{message:"请输入时效周期",trigger:"blur",required:!0},{validator:function(t,r,i){e.$util.RegExp.positiveInteger.test(r||"")?i():i("非法的时间")},trigger:"blur"}]}}},methods:{getUniqueServiceList:function(){var e=this;this.$api.enterpriseLevelControllerGetUniqueServiceList().then(function(t){e.enterPriseList=t})},getServerList:function(e){return(this.enterPriseList.find(function(t){return t.enterpriseCode===e})||{serverCodes:[]}).serverCodes},getUsedAlarmLevel:function(){var e=this;this.$api.enterpriseLevelControllerGetLastUse({enterpriseCode:this.setCycleForm.enterpriseCode,serverCode:this.setCycleForm.serverCode}).then(function(t){e.$set(e.setCycleForm,"curAlarmLevel",t[0])})},getCycleList:function(){var e=this;this.$api.alarmCycleControllerList(l()({currentPage:this.pagination.page,pageSize:this.pagination.pageSize},this.query)).then(function(t){e.cycleList=t.list,e.pagination.total=t.count})},addCycleToModal:function(e,t){var r=this;this.$nextTick(function(){e?(r.setCycleForm=l()({},e),r.setCycleForm.__type="modify"):(r.setCycleForm={alarms:[],enterpriseCode:r.curEnterprise.code,serverCode:r.curService.code},r.setCycleForm.__type="add"),r.getUsedAlarmLevel(),t&&(r.setCycleForm.__type="detail")}),this.setCycleVisible=!0},addCycleSubmit:function(){var e=this;this.$refs.setCycle.validate(function(t){if(t){var r={enterpriseCode:e.setCycleForm.enterpriseCode,serverCode:e.setCycleForm.serverCode,diffTime:e.setCycleForm.diffTime,operator:e.curUser,name:e.setCycleForm.curAlarmLevel.name,id:e.setCycleForm.id},i=e.enterPriseList.find(function(e){return e.enterpriseCode===r.enterpriseCode}),l=i.serverCodes.find(function(e){return e.code===r.serverCode});r.enterpriseName=i.title,r.serverName=l.title;var s="add"===e.setCycleForm.__type?"alarmCycleControllerAdd":"alarmCycleControllerUpdate";e.$api[s](r).then(function(t){console.log(t),e.getCycleList(),e.setCycleVisible=!1,e.$refs.setCycle.resetFields()})}})},deleteCycleToModal:function(e){var t=this;this.$confirm("确定要删除该服务告警等级？","是否删除",{type:"warning",dangerouslyUseHTMLString:!0}).then(function(r){t.$api.alarmCycleControllerDelete({id:e.id}).then(function(e){t.getCycleList()})}).catch(function(){})},updateStatus:function(e){var t=this;this.$api.alarmCycleControllerUpdateState({id:e.id,state:"启用"===e.state?"禁用":"启用",enterpriseCode:e.enterpriseCode,serverCode:e.serverCode}).then(function(e){t.getCycleList()})}},mounted:function(){this.getUniqueServiceList(),this.getCycleList()}},a={render:function(){var e=this,t=e.$createElement,r=e._self._c||t;return r("section",{staticClass:"df",attrs:{id:"alarm-cycle-page"}},[r("div",{staticClass:"wrapper df dfv"},[r("div",{staticClass:"query-form df"},[r("el-form",{attrs:{inline:!0,"label-width":"100px"}},[r("el-form-item",{attrs:{label:"企业名称",prop:"enterpriseName"}},[r("el-input",{attrs:{size:"mini"},nativeOn:{keyup:function(t){return!t.type.indexOf("key")&&e._k(t.keyCode,"enter",13,t.key,"Enter")?null:e.getCycleList()}},model:{value:e.query.enterpriseName,callback:function(t){e.$set(e.query,"enterpriseName",t)},expression:"query.enterpriseName"}})],1),e._v(" "),r("el-form-item",{attrs:{label:"服务名称",prop:"serverName"}},[r("el-input",{attrs:{size:"mini"},nativeOn:{keyup:function(t){return!t.type.indexOf("key")&&e._k(t.keyCode,"enter",13,t.key,"Enter")?null:e.getCycleList()}},model:{value:e.query.serverName,callback:function(t){e.$set(e.query,"serverName",t)},expression:"query.serverName"}})],1),e._v(" "),r("el-form-item",{attrs:{label:"规则名称",prop:"name"}},[r("el-input",{attrs:{size:"mini"},nativeOn:{keyup:function(t){return!t.type.indexOf("key")&&e._k(t.keyCode,"enter",13,t.key,"Enter")?null:e.getCycleList()}},model:{value:e.query.name,callback:function(t){e.$set(e.query,"name",t)},expression:"query.name"}})],1),e._v(" "),r("el-form-item",{attrs:{label:"操作用户",prop:"operatorName"}},[r("el-input",{attrs:{size:"mini"},nativeOn:{keyup:function(t){return!t.type.indexOf("key")&&e._k(t.keyCode,"enter",13,t.key,"Enter")?null:e.getCycleList()}},model:{value:e.query.operatorName,callback:function(t){e.$set(e.query,"operatorName",t)},expression:"query.operatorName"}})],1),e._v(" "),r("el-button",{staticStyle:{"margin-left":"33px"},attrs:{type:"primary",size:"mini"},on:{click:function(t){return e.getCycleList()}}},[e._v("确认")]),e._v(" "),r("el-button",{attrs:{type:"primary",size:"mini"},on:{click:function(t){e.query={}}}},[e._v("清除")])],1)],1),e._v(" "),r("div",{staticClass:"list df dfv"},[r("div",{staticClass:"list-header"},[r("h3",[e._v("告警周期列表")]),e._v(" "),r("div",{staticClass:"btn-group"},[r("el-button",{attrs:{type:"primary",size:"mini"},on:{click:function(t){return e.addCycleToModal()}}},[e._v("添加")])],1)]),e._v(" "),r("div",{staticClass:"table-box df dfv"},[r("el-table",{attrs:{height:"100%",data:e.cycleList}},[r("el-table-column",{attrs:{label:"序号",type:"index"}}),e._v(" "),r("el-table-column",{attrs:{label:"企业名称",prop:"enterpriseName",formatter:e.$tableFilter("nullFilter")}}),e._v(" "),r("el-table-column",{attrs:{label:"服务名称",prop:"serverName",formatter:e.$tableFilter("nullFilter")}}),e._v(" "),r("el-table-column",{attrs:{label:"规则名称",prop:"name"}}),e._v(" "),r("el-table-column",{attrs:{label:"实时告警时效周期",prop:"diffTime"}}),e._v(" "),r("el-table-column",{attrs:{label:"操作用户",prop:"operator.name"}}),e._v(" "),r("el-table-column",{attrs:{label:"状态",prop:"state"}}),e._v(" "),r("el-table-column",{attrs:{label:"创建时间",prop:"updateTime",formatter:e.$tableFilter("dateFormatFilter")}}),e._v(" "),r("el-table-column",{attrs:{label:"操作"},scopedSlots:e._u([{key:"default",fn:function(t){return[r("label",[r("a",{staticClass:"icomoon pic-look",attrs:{title:"详情"},on:{click:function(r){return e.addCycleToModal(t.row,"detail")}}})]),e._v(" "),"系统"!==t.row.cycleType?r("label",[r("a",{staticClass:"icomoon pic-edit",attrs:{title:"修改"},on:{click:function(r){return e.addCycleToModal(t.row)}}})]):e._e(),e._v(" "),"系统"!==t.row.cycleType?r("label",[r("a",{staticClass:"icomoon pic-delete",attrs:{title:"删除"},on:{click:function(r){return e.deleteCycleToModal(t.row)}}})]):e._e(),e._v(" "),r("label",[r("a",{staticClass:"icomoon",class:"启用"===t.row.state?"pic-invalid":"pic-check",attrs:{title:"启用"===t.row.state?"禁用":"启用"},on:{click:function(r){return e.updateStatus(t.row)}}})])]}}])})],1)],1),e._v(" "),r("el-col",{staticClass:"toolbar",attrs:{span:24}},[r("el-pagination",{staticStyle:{float:"right"},attrs:{layout:"total, prev, pager, next,sizes, jumper","page-sizes":[10,15,50,100],"page-size":e.pagination.pageSize,total:e.pagination.total},on:{"current-change":e.pagination.handleCurrentChange,"size-change":e.pagination.pageSizeChange}})],1)],1)]),e._v(" "),r("el-dialog",{staticClass:"m400",attrs:{title:"detail"===e.setCycleForm.__type?"周期详情":"告警周期设置","close-on-click-modal":!1,visible:e.setCycleVisible},on:{close:function(t){e.setCycleVisible=!1,e.$refs.setCycle.resetFields()}}},[r("el-form",{ref:"setCycle",attrs:{model:e.setCycleForm,rules:e.setCycleRules,"label-width":"100px"}},[r("el-form-item",{attrs:{label:"企业名称",prop:"enterpriseCode"}},[e.setCycleForm.enterpriseCode?r("el-select",{staticStyle:{width:"220px"},attrs:{disabled:"",size:"mini"},model:{value:e.setCycleForm.enterpriseCode,callback:function(t){e.$set(e.setCycleForm,"enterpriseCode",t)},expression:"setCycleForm.enterpriseCode"}},e._l(e.enterPriseList,function(t,i){return r("el-option",{key:i,attrs:{label:t.title,value:t.enterpriseCode}},[e._v(e._s(t.title))])}),1):r("span",[e._v("-")])],1),e._v(" "),r("el-form-item",{attrs:{label:"服务名称",prop:"serverCode"}},[e.setCycleForm.serverCode?r("el-select",{staticStyle:{width:"220px"},attrs:{disabled:"",size:"mini"},on:{input:function(t){return e.getUsedAlarmLevel()}},model:{value:e.setCycleForm.serverCode,callback:function(t){e.$set(e.setCycleForm,"serverCode",t)},expression:"setCycleForm.serverCode"}},e._l(e.getServerList(e.setCycleForm.enterpriseCode),function(t,i){return r("el-option",{key:i,attrs:{label:t.title,value:t.code}},[e._v(e._s(t.title))])}),1):r("span",[e._v("-")])],1),e._v(" "),r("el-form-item",{attrs:{label:"规则名称"}},[r("span",[e._v(e._s(e._f("nullFilter")(e.$util.getValue(e.setCycleForm,"curAlarmLevel.name"))))])]),e._v(" "),r("el-form-item",{attrs:{label:"操作用户"}},[r("span",[e._v(e._s(e._f("nullFilter")(e.$util.getValue(e.setCycleForm,"curAlarmLevel.operator.name"))))])]),e._v(" "),r("el-form-item",{attrs:{label:"时效周期",prop:"diffTime"}},[r("el-input",{staticStyle:{width:"220px"},attrs:{disabled:"detail"===e.setCycleForm.__type,size:"mini"},model:{value:e.setCycleForm.diffTime,callback:function(t){e.$set(e.setCycleForm,"diffTime",t)},expression:"setCycleForm.diffTime"}}),e._v(" 小时\n      ")],1)],1),e._v(" "),r("span",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[r("el-button",{attrs:{size:"mini",type:"default"},on:{click:function(t){e.setCycleVisible=!1,e.$refs.setCycle.resetFields()}}},[e._v("取 消")]),e._v(" "),"detail"!==e.setCycleForm.__type?r("el-button",{attrs:{size:"mini",type:"primary"},on:{click:function(t){return e.addCycleSubmit()}}},[e._v("确 定")]):e._e()],1)],1)],1)},staticRenderFns:[]};var n=r("VU/8")(s,a,!1,function(e){r("PplA")},"data-v-38bd0225",null);t.default=n.exports}});