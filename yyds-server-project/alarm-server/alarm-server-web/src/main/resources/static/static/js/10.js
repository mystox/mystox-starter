webpackJsonp([10],{ESLf:function(e,t){},WGBG:function(e,t,r){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var i=r("woOf"),l=r.n(i),a={name:"alarmCycle",data:function(){var e=this;return{curUser:{strId:"123123",name:"chenht"},enterPriseList:[],query:{},cycleList:[],pagination:{page:1,pageSize:15,total:0,handleCurrentChange:function(t){e.pagination.page=t,e.getCycleList()},pageSizeChange:function(t){e.pagination.pageSize=t,e.getCycleList()}},setCycleVisible:!1,setCycleForm:{}}},computed:{setCycleRules:function(){return{aaa:{message:"sdfse",trigger:"blur",validator:function(e,t,r){t?r():r(new Error(""))}}}}},methods:{getUniqueServiceList:function(){var e=this;this.$api.enterpriseLevelControllerGetUniqueServiceList().then(function(t){e.enterPriseList=t})},getServerList:function(e){return(this.enterPriseList.find(function(t){return t.enterpriseCode===e})||{serverCodes:[]}).serverCodes},getUsedAlarmLevel:function(){var e=this;this.$api.enterpriseLevelControllerGetLastUse({enterpriseCode:this.setCycleForm.enterpriseCode,serverCode:this.setCycleForm.serverCode}).then(function(t){e.$set(e.setCycleForm,"curAlarmLevel",t[0])})},getCycleList:function(){var e=this;this.$api.alarmCycleControllerList(l()({currentPage:this.pagination.page,pageSize:this.pagination.pageSize},this.query)).then(function(t){e.cycleList=t.list,e.pagination.total=t.count})},addCycleToModal:function(e,t){var r=this;this.$nextTick(function(){e?(r.setCycleForm=l()({},e),r.getUsedAlarmLevel(),r.setCycleForm.__type="modify"):(r.setCycleForm={alarms:[]},r.setCycleForm.__type="add"),t&&(r.setCycleForm.__type="detail")}),this.setCycleVisible=!0},addCycleSubmit:function(){var e=this,t={enterpriseCode:this.setCycleForm.enterpriseCode,serverCode:this.setCycleForm.serverCode,diffTime:this.setCycleForm.diffTime,operator:this.curUser,name:this.setCycleForm.curAlarmLevel.name,id:this.setCycleForm.id},r=this.enterPriseList.find(function(e){return e.enterpriseCode===t.enterpriseCode}),i=r.serverCodes.find(function(e){return e.code===t.serverCode});t.enterpriseName=r.title,t.serverName=i.title;var l="add"===this.setCycleForm.__type?"alarmCycleControllerAdd":"alarmCycleControllerUpdate";this.$api[l](t).then(function(t){console.log(t),e.getCycleList(),e.setCycleVisible=!1})},deleteCycleToModal:function(e){var t=this;this.$confirm("确定要删除该服务告警等级？<br/>注意：将要删除的服务告警等级是“XXXXX”， 确定删除吗？（包含该告警等级所有数据）","是否删除",{type:"warning",dangerouslyUseHTMLString:!0}).then(function(r){t.$api.alarmCycleControllerDelete({id:e.id}).then(function(e){t.getCycleList()})}).catch(function(){})},updateStatus:function(e){var t=this;this.$api.alarmCycleControllerUpdateState({id:e.id,state:"启用"===e.state?"禁用":"启用",enterpriseCode:e.enterpriseCode,serverCode:e.serverCode}).then(function(e){t.getCycleList()})}},mounted:function(){this.getUniqueServiceList(),this.getCycleList()}},s={render:function(){var e=this,t=e.$createElement,r=e._self._c||t;return r("section",{staticClass:"df",attrs:{id:"alarm-cycle-page"}},[r("div",{staticClass:"wrapper df dfv"},[r("div",{staticClass:"query-form df"},[r("el-form",{attrs:{inline:!0,"label-width":"100px"}},[r("el-form-item",{attrs:{label:"企业名称",prop:"enterpriseName"}},[r("el-input",{attrs:{size:"mini"},model:{value:e.query.enterpriseName,callback:function(t){e.$set(e.query,"enterpriseName",t)},expression:"query.enterpriseName"}})],1),e._v(" "),r("el-form-item",{attrs:{label:"服务名称",prop:"serverName"}},[r("el-input",{attrs:{size:"mini"},model:{value:e.query.serverName,callback:function(t){e.$set(e.query,"serverName",t)},expression:"query.serverName"}})],1),e._v(" "),r("el-form-item",{attrs:{label:"规则名称",prop:"name"}},[r("el-input",{attrs:{size:"mini"},model:{value:e.query.name,callback:function(t){e.$set(e.query,"name",t)},expression:"query.name"}})],1),e._v(" "),r("el-form-item",{attrs:{label:"操作用户",prop:"operatorName"}},[r("el-input",{attrs:{size:"mini"},model:{value:e.query.operatorName,callback:function(t){e.$set(e.query,"operatorName",t)},expression:"query.operatorName"}})],1),e._v(" "),r("el-button",{staticStyle:{"margin-left":"33px"},attrs:{type:"primary",size:"mini"},on:{click:function(t){return e.getCycleList()}}},[e._v("确认")]),e._v(" "),r("el-button",{attrs:{type:"primary",size:"mini"},on:{click:function(t){e.query={}}}},[e._v("清除")])],1)],1),e._v(" "),r("div",{staticClass:"list df dfv"},[r("div",{staticClass:"list-header"},[r("h3",[e._v("告警周期列表")]),e._v(" "),r("div",{staticClass:"btn-group"},[r("el-button",{attrs:{type:"primary",size:"mini"},on:{click:function(t){return e.addCycleToModal()}}},[e._v("添加")])],1)]),e._v(" "),r("div",{staticClass:"table-box df dfv"},[r("el-table",{attrs:{height:"100%",data:e.cycleList}},[r("el-table-column",{attrs:{label:"序号",type:"index"}}),e._v(" "),r("el-table-column",{attrs:{label:"企业名称",prop:"enterpriseName"}}),e._v(" "),r("el-table-column",{attrs:{label:"服务名称",prop:"serverName"}}),e._v(" "),r("el-table-column",{attrs:{label:"规则名称",prop:"name"}}),e._v(" "),r("el-table-column",{attrs:{label:"实时告警时效周期",prop:"diffTime"}}),e._v(" "),r("el-table-column",{attrs:{label:"操作用户",prop:"operator.name"}}),e._v(" "),r("el-table-column",{attrs:{label:"状态",prop:"state"}}),e._v(" "),r("el-table-column",{attrs:{label:"创建时间",prop:"updateTime",formatter:e.$tableFilter("dateFormatFilter")}}),e._v(" "),r("el-table-column",{attrs:{label:"操作"},scopedSlots:e._u([{key:"default",fn:function(t){return[r("a",{on:{click:function(r){return e.addCycleToModal(t.row,"detail")}}},[e._v("查看")]),e._v(" "),r("a",{on:{click:function(r){return e.updateStatus(t.row)}}},[e._v(e._s("启用"===t.row.state?"禁用":"启用"))]),e._v(" "),r("a",{on:{click:function(r){return e.addCycleToModal(t.row)}}},[e._v("修改")]),e._v(" "),r("a",{on:{click:function(r){return e.deleteCycleToModal(t.row)}}},[e._v("删除")])]}}])})],1)],1),e._v(" "),r("el-col",{staticClass:"toolbar",attrs:{span:24}},[r("el-pagination",{staticStyle:{float:"right"},attrs:{layout:"total, prev, pager, next,sizes, jumper","page-sizes":[10,15,50,100],"page-size":e.pagination.pageSize,total:e.pagination.total},on:{"current-change":e.pagination.handleCurrentChange,"size-change":e.pagination.pageSizeChange}})],1)],1)]),e._v(" "),r("el-dialog",{staticClass:"m400",attrs:{title:"detail"===e.setCycleForm.__type?"周期详情":"告警周期设置","close-on-click-modal":!1,visible:e.setCycleVisible},on:{close:function(t){e.setCycleVisible=!1}}},[r("el-form",{ref:"setCycle",attrs:{model:e.setCycleForm,rules:e.setCycleRules,"label-width":"100px"}},[r("el-form-item",{attrs:{label:"企业名称",prop:"enterpriseCode"}},[r("el-select",{staticStyle:{width:"220px"},attrs:{disabled:"detail"===e.setCycleForm.__type,size:"mini"},model:{value:e.setCycleForm.enterpriseCode,callback:function(t){e.$set(e.setCycleForm,"enterpriseCode",t)},expression:"setCycleForm.enterpriseCode"}},e._l(e.enterPriseList,function(t,i){return r("el-option",{key:i,attrs:{label:t.title,value:t.enterpriseCode}},[e._v(e._s(t.title))])}),1)],1),e._v(" "),r("el-form-item",{attrs:{label:"服务名称",prop:"serverCode"}},[r("el-select",{staticStyle:{width:"220px"},attrs:{disabled:"detail"===e.setCycleForm.__type,size:"mini"},on:{input:function(t){return e.getUsedAlarmLevel()}},model:{value:e.setCycleForm.serverCode,callback:function(t){e.$set(e.setCycleForm,"serverCode",t)},expression:"setCycleForm.serverCode"}},e._l(e.getServerList(e.setCycleForm.enterpriseCode),function(t,i){return r("el-option",{key:i,attrs:{label:t.title,value:t.code}},[e._v(e._s(t.title))])}),1)],1),e._v(" "),r("el-form-item",{attrs:{label:"规则名称"}},[r("span",[e._v(e._s(e._f("nullFilter")((e.setCycleForm.curAlarmLevel||{}).name)))])]),e._v(" "),r("el-form-item",{attrs:{label:"操作用户"}},[r("span",[e._v(e._s(e._f("nullFilter")((e.setCycleForm.curAlarmLevel||{operator:{}}).operator.name)))])]),e._v(" "),r("el-form-item",{attrs:{label:"时效周期",prop:"diffTime"}},[r("el-input",{staticStyle:{width:"220px"},attrs:{disabled:"detail"===e.setCycleForm.__type,size:"mini"},model:{value:e.setCycleForm.diffTime,callback:function(t){e.$set(e.setCycleForm,"diffTime",t)},expression:"setCycleForm.diffTime"}}),e._v(" 小时\n      ")],1)],1),e._v(" "),r("span",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[r("el-button",{attrs:{size:"mini",type:"default"},on:{click:function(t){e.setCycleVisible=!1}}},[e._v("取 消")]),e._v(" "),"detail"!==e.setCycleForm.__type?r("el-button",{attrs:{size:"mini",type:"primary"},on:{click:function(t){return e.addCycleSubmit()}}},[e._v("确 定")]):e._e()],1)],1)],1)},staticRenderFns:[]};var n=r("VU/8")(a,s,!1,function(e){r("ESLf")},"data-v-1ab8a0ca",null);t.default=n.exports}});