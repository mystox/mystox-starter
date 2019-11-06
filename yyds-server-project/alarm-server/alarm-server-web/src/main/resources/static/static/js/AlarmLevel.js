webpackJsonp([3],{EUD6:function(e,t){},HAvc:function(e,t,l){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var a=l("woOf"),i=l.n(a),r={name:"alarmLevel",data:function(){var e=this;return{curUser:{strId:"123123",name:"chenht"},curService:JSON.parse(localStorage.curService),curEnterprise:JSON.parse(localStorage.curEnterprise),oneToEight:["一","二","三","四","五","六","七","八"],defaultAlarmColor:["#DB001B","#FF8400","#E6D500","#C001E7","#FF5695","#00B1FF","#78E2FA","#7ED321"],curView:"1",alarmLevelQuery:{},deviceLevelQuery:{},relationQuery:{},alarmLevelList:[],deviceLevelList:[],relationList:[],alarmLevelPagi:{page:1,pageSize:15,total:0,handleCurrentChange:function(t){e.alarmLevelPagi.page=t,e.getAlarmLevelList()},pageSizeChange:function(t){e.alarmLevelPagi.pageSize=t,e.getAlarmLevelList()}},deviceLevelPagi:{page:1,pageSize:15,total:0,handleCurrentChange:function(t){e.deviceLevelPagi.page=t,e.getDeviceLevelList()},pageSizeChange:function(t){e.deviceLevelPagi.pageSize=t,e.getDeviceLevelList()}},relationPagi:{page:1,pageSize:15,total:0,handleCurrentChange:function(t){e.relationPagi.page=t,e.getRelationList()},pageSizeChange:function(t){e.relationPagi.pageSize=t,e.getRelationList()}},addDeviceLevelForm:{},addDeviceLevelVisible:!1,addAlarmLevelForm:{},addAlarmLevelVisible:!1,setRelationForm:{},setRelationVisible:!1}},computed:{addDeviceLevelRules:function(){return{deviceType:{required:!0,message:"请选择设备类型",trigger:"change"},deviceModel:{required:!0,message:"请选择设备型号",trigger:"change"},levels:[{validator:function(e,t,l){t&&t.length>0?l():l(new Error("请选择告警级别设置"))},trigger:"blur"}]}},addAlarmLevelRules:function(){var e=this,t={name:{required:!0,trigger:"blur",message:"请输入规则名称"}};return new Array(8).fill("").map(function(l,a){t["addAlarmLevelForm"+a]={validator:function(t,l,i){return a>e.addAlarmLevelForm.alarmLength?i():e.addAlarmLevelForm.levelNames[a]?e.addAlarmLevelForm.colors[a]?void i():i("请选择颜色"):i("请输入等级名称")},trigger:"blur"}}),t},setRelationRules:function(){return{aaa:{message:"sdfse",trigger:"blur",validator:function(e,t,l){t?l():l(new Error(""))}}}}},methods:{getAlarmLevelList:function(){var e=this;this.$api.enterpriseLevelControllerList(i()({pageSize:this.alarmLevelPagi.pageSize,curPage:this.alarmLevelPagi.page,enterpriseCode:this.curEnterprise.code,serverCode:this.curService.code},this.alarmLevelQuery)).then(function(t){t.list.forEach(function(t,l){t.showOps=!0,t.level=t.levels.length+"个等级",t.children=[{id:l+"-title",level:"等级名称",name:"自定义名称",operator:{name:" "},enterpriseName:"告警颜色"}],t.levels.forEach(function(a,i){t.children.push({id:l+"-"+i,level:e.oneToEight[a-1]+"级告警",name:t.levelNames[i],color:t.colors[i],operator:{name:" "},isColor:!0})}),t.children.length%2==0&&t.children.push({id:l+"-invisible",invisible:!0})}),e.alarmLevelList=t.list,e.alarmLevelPagi.total=t.count})},getDeviceLevelList:function(){var e=this;this.$api.deviceTypeLevelControllerList(i()({pageSize:this.deviceLevelPagi.pageSize,curPage:this.deviceLevelPagi.page,enterpriseCode:this.curEnterprise.code,serverCode:this.curService.code},this.deviceLevelQuery)).then(function(t){t.list.forEach(function(e){e.alarm=[],e.levels.forEach(function(t){e.alarm[t-1]=!0})}),e.deviceLevelList=t.list,e.deviceLevelPagi.total=t.count})},getRelationList:function(){var e=this;this.$api.alarmLevelControllerList(i()({currentPage:this.relationPagi.page,pageSize:this.relationPagi.pageSize,serverCode:this.curService.code,enterpriseCode:this.curEnterprise.code},this.relationQuery)).then(function(t){t.list.forEach(function(e,t){e.showOps=!0,e.level=e.sourceLevelList.length+"个等级",e.children=[{id:t+"-title",enterpriseName:"源告警等级",serverName:"目标告警等级",deviceType:"自定义名称",deviceModel:"告警颜色",operator:{name:" "}}],e.sourceLevelList.forEach(function(l,a){e.children.push({id:t+"-"+a,enterpriseName:l+"级告警",serverName:e.targetLevelList[a]+"级告警",color:e.colorList[a],deviceType:e.targetLevelNameList[a],operator:{name:" "},isColor:!0})}),e.children.length%2==0&&e.children.push({id:t+"-invisible",invisible:!0})}),e.relationList=t.list,e.relationPagi.total=t.count})},getDeviceList4AddDeviceLevel:function(){var e=this;this.$api.deviceTypeLevelControllerGetDeviceTypeList({enterpriseCode:this.addDeviceLevelForm.enterpriseCode,serverCode:this.addDeviceLevelForm.serverCode}).then(function(t){var l={},a=[];for(var i in(t||[]).forEach(function(e){l[e.type]||(l[e.type]={type:e.type,models:[]}),l[e.type].models.push(e.model)}),l)a.push(l[i]);e.$set(e.addDeviceLevelForm,"deviceTypeList",a)})},addDeviceLevelToModal:function(e){var t=this;this.addDeviceLevelVisible=!0,this.$nextTick(function(){e?(t.addDeviceLevelForm=i()({},e),t.addDeviceLevelForm.__type="modify"):(t.addDeviceLevelForm={serverCode:t.curService.code,enterpriseCode:t.curEnterprise.code,levels:[]},t.addDeviceLevelForm.__type="add"),t.getDeviceList4AddDeviceLevel()})},addDeviceLevelSubmit:function(){var e=this;this.$refs.addDeviceLevel.validate(function(t){if(t){var l={serverCode:e.addDeviceLevelForm.serverCode,enterpriseCode:e.addDeviceLevelForm.enterpriseCode,levels:e.addDeviceLevelForm.levels,deviceType:e.addDeviceLevelForm.deviceType,deviceModel:e.addDeviceLevelForm.deviceModel,id:e.addDeviceLevelForm.id,operator:e.curUser,enterpriseName:e.curEnterprise.name,serverName:e.curService.name},a="add"===e.addDeviceLevelForm.__type?"deviceTypeLevelControllerAdd":"deviceTypeLevelControllerUpdate";e.$api[a](l).then(function(t){console.log(t),e.addDeviceLevelVisible=!1,e.getDeviceLevelList(),e.$refs.addDeviceLevel.resetFields()})}})},deleteDeviceToModal:function(e){var t=this,l=void 0;if(!e&&(l=this.$refs.deviceLevelList.selection).length<1)this.$message({type:"warning",message:"请至少选择一个设备"});else{this.deleteDeviceList=l||[e];var a=1===this.deleteDeviceList.length?"您将要删除的设备等级为<br/>设备类型:"+e.deviceType+", 设备型号:"+e.deviceModel:"您一共选择了"+this.deleteDeviceList.length+"个设备等级";this.$confirm(a,"是否删除",{type:"warning",dangerouslyUseHTMLString:!0}).then(function(e){t.$api.deviceTypeLevelControllerDelete({id:t.deleteDeviceList.map(function(e){return e.id})[0]}).then(function(e){t.getDeviceLevelList()})}).catch(function(){})}},addAlarmLevelToModal:function(e){var t=this;this.addAlarmLevelVisible=!0,this.$nextTick(function(){e?(t.addAlarmLevelForm=i()({},e),t.addAlarmLevelForm.alarmLength=e.levels.length-1,t.addAlarmLevelForm.__type="modify"):(t.addAlarmLevelForm={serverCode:t.curService.code,enterpriseCode:t.curEnterprise.code,alarmLength:0,levels:[],levelNames:[],colors:[]},t.resetAllAlarmSettings(0),t.addAlarmLevelForm.__type="add")})},addAlarmLevelSubmit:function(){var e=this;this.$refs.addAlarmLevel.validate(function(t){if(t){var l={name:e.addAlarmLevelForm.name,serverCode:e.addAlarmLevelForm.serverCode,enterpriseCode:e.addAlarmLevelForm.enterpriseCode,levels:[],levelNames:e.addAlarmLevelForm.levelNames,colors:e.addAlarmLevelForm.colors,id:e.addAlarmLevelForm.id,code:e.addAlarmLevelForm.code,state:e.addAlarmLevelForm.state,enterpriseName:e.curEnterprise.name,serverName:e.curService.name};l.operator=e.curUser;for(var a=0;a<e.addAlarmLevelForm.alarmLength+1;a++)l.levels.push(a+1);var i="add"===e.addAlarmLevelForm.__type?"enterpriseLevelControllerAdd":"enterpriseLevelControllerUpdate";e.$api[i](l).then(function(t){e.getAlarmLevelList(),e.addAlarmLevelVisible=!1,e.$refs.addAlarmLevel.resetFields()})}})},changeAlarmLevelState:function(e){var t=this;this.$api.enterpriseLevelControllerUpdateState({id:e.id,code:e.code,enterpriseCode:e.enterpriseCode,serverCode:e.serverCode,state:"启用"===e.state?"禁用":"启用"}).then(function(e){t.getAlarmLevelList()})},resetAllAlarmSettings:function(e){var t=this;this.$set(this.addAlarmLevelForm,"colors",new Array(e+1).fill("").map(function(e,l){return t.defaultAlarmColor[l]}))},deleteAlarmLevelToModal:function(e){var t=this;this.$confirm("确定要删除该服务告警等级？<br/>注意：将要删除的服务告警规则是“"+e.name+"”， 确定删除吗？（包含该告警等级所有数据）","是否删除",{type:"warning",dangerouslyUseHTMLString:!0}).then(function(l){t.$api.enterpriseLevelControllerDelete({id:e.id}).then(function(e){t.getAlarmLevelList()})}).catch(function(){})},setRelationToModal:function(e){var t=this;this.setRelationVisible=!0,this.$nextTick(function(){t.setRelationForm=i()({},e),t.setRelationForm.targetLevelList=e.targetLevelList.map(function(e){return e}),t.setRelationForm.targetLevelNameList=e.targetLevelNameList.map(function(e){return e}),t.$api.enterpriseLevelControllerGetLastUse({enterpriseCode:e.enterpriseCode,serverCode:e.serverCode}).then(function(e){t.$set(t.setRelationForm,"targetLevelOptions",e)})})},setRelationSubmit:function(){var e=this,t=this.setRelationForm;t.targetLevelNameList=this.setRelationForm.targetLevelList.map(function(t){return e.setRelationForm.targetLevelOptions.find(function(e){return e.level===t}).levelName}),t.colorList=this.setRelationForm.targetLevelList.map(function(t){return e.setRelationForm.targetLevelOptions.find(function(e){return e.level===t}).color}),this.$api.alarmLevelControllerUpdate(t).then(function(t){e.getRelationList(),e.setRelationVisible=!1})}},mounted:function(){this.getAlarmLevelList(),this.getDeviceLevelList(),this.getRelationList()}},o={render:function(){var e=this,t=e.$createElement,l=e._self._c||t;return l("section",{staticClass:"df",attrs:{id:"alarm-level-page"}},[l("el-tabs",{staticClass:"tabs df",model:{value:e.curView,callback:function(t){e.curView=t},expression:"curView"}},[l("el-tab-pane",{attrs:{label:"告警等级",name:"1"}},[l("div",{staticClass:"tab-content df dfv"},[l("div",{staticClass:"query-form df"},[l("el-form",{attrs:{inline:!0,"label-width":"100px"}},[l("el-form-item",{attrs:{label:"规则名称",prop:"name"}},[l("el-input",{attrs:{size:"mini"},nativeOn:{keyup:function(t){return!t.type.indexOf("key")&&e._k(t.keyCode,"enter",13,t.key,"Enter")?null:e.getAlarmLevelList(t)}},model:{value:e.alarmLevelQuery.name,callback:function(t){e.$set(e.alarmLevelQuery,"name",t)},expression:"alarmLevelQuery.name"}})],1),e._v(" "),l("el-form-item",{attrs:{label:"操作用户",prop:"operatorName"}},[l("el-input",{attrs:{size:"mini"},nativeOn:{keyup:function(t){return!t.type.indexOf("key")&&e._k(t.keyCode,"enter",13,t.key,"Enter")?null:e.getAlarmLevelList(t)}},model:{value:e.alarmLevelQuery.operatorName,callback:function(t){e.$set(e.alarmLevelQuery,"operatorName",t)},expression:"alarmLevelQuery.operatorName"}})],1),e._v(" "),l("el-button",{staticStyle:{"margin-left":"33px"},attrs:{type:"primary",size:"mini"},on:{click:function(t){return e.getAlarmLevelList()}}},[e._v("确认")]),e._v(" "),l("el-button",{attrs:{type:"primary",size:"mini"},on:{click:function(t){e.alarmLevelQuery={}}}},[e._v("清除")])],1)],1),e._v(" "),l("div",{directives:[{name:"loading",rawName:"v-loading",value:e.$apiLoading.enterpriseLevelControllerList,expression:"$apiLoading.enterpriseLevelControllerList"}],staticClass:"list df dfv"},[l("div",{staticClass:"list-header"},[l("h3",[e._v("告警等级列表")]),e._v(" "),l("div",{staticClass:"btn-group"},[l("el-button",{attrs:{type:"primary",size:"mini"},on:{click:function(t){return e.addAlarmLevelToModal()}}},[e._v("添加")])],1)]),e._v(" "),l("div",{staticClass:"table-box df dfv"},[l("el-table",{attrs:{height:"100%",data:e.alarmLevelList,"tree-props":{children:"children"},"row-key":"id","row-class-name":function(e){return e.row.invisible?"invisible":""}}},[l("el-table-column",{attrs:{label:"告警等级",prop:"level"}}),e._v(" "),l("el-table-column",{attrs:{label:"规则名称",prop:"name"}}),e._v(" "),l("el-table-column",{attrs:{label:"企业名称"},scopedSlots:e._u([{key:"default",fn:function(t){return[t.row.isColor?l("div",{staticClass:"alarm-color",style:"background: "+t.row.color}):l("span",[e._v(e._s(e._f("nullFilter")(t.row.enterpriseName)))])]}}])}),e._v(" "),l("el-table-column",{attrs:{label:"服务名称"},scopedSlots:e._u([{key:"default",fn:function(t){return[t.row.showOps?l("span",[e._v(e._s(e._f("nullFilter")(t.row.serverName)))]):e._e()]}}])}),e._v(" "),l("el-table-column",{attrs:{label:"操作用户",prop:"operator.name",formatter:e.$tableFilter("nullFilter")}}),e._v(" "),l("el-table-column",{attrs:{label:"状态",prop:"state"}}),e._v(" "),l("el-table-column",{attrs:{label:"创建时间",prop:"updateTime"},scopedSlots:e._u([{key:"default",fn:function(t){return[t.row.showOps?l("span",[e._v(e._s(e._f("dateFormatFilter")(t.row.updateTime)))]):e._e()]}}])}),e._v(" "),l("el-table-column",{attrs:{label:"操作"},scopedSlots:e._u([{key:"default",fn:function(t){return[l("label",{directives:[{name:"show",rawName:"v-show",value:t.row.showOps&&"系统"!==t.row.levelType&&"启用"!==t.row.state,expression:"scope.row.showOps && scope.row.levelType !== '系统' && scope.row.state !== '启用'"}]},[l("a",{staticClass:"icomoon pic-edit",attrs:{title:"修改"},on:{click:function(l){return e.addAlarmLevelToModal(t.row)}}})]),e._v(" "),l("label",{directives:[{name:"show",rawName:"v-show",value:t.row.showOps&&"系统"!==t.row.levelType&&"启用"!==t.row.state,expression:"scope.row.showOps && scope.row.levelType !== '系统' && scope.row.state !== '启用'"}]},[l("a",{staticClass:"icomoon pic-delete",attrs:{title:"删除"},on:{click:function(l){return e.deleteAlarmLevelToModal(t.row)}}})]),e._v(" "),l("label",{directives:[{name:"show",rawName:"v-show",value:t.row.showOps,expression:"scope.row.showOps"}]},[l("a",{staticClass:"icomoon",class:"启用"===t.row.state?"pic-invalid":"pic-check",attrs:{title:"启用"===t.row.state?"禁用":"启用"},on:{click:function(l){return e.changeAlarmLevelState(t.row)}}})])]}}])})],1)],1),e._v(" "),l("el-col",{staticClass:"toolbar",attrs:{span:24}},[l("el-pagination",{staticStyle:{float:"right"},attrs:{layout:"total, prev, pager, next,sizes, jumper","page-sizes":[10,15,50,100],"page-size":e.alarmLevelPagi.pageSize,total:e.alarmLevelPagi.total},on:{"current-change":e.alarmLevelPagi.handleCurrentChange,"size-change":e.alarmLevelPagi.pageSizeChange}})],1)],1)])]),e._v(" "),l("el-tab-pane",{attrs:{label:"设备等级",name:"2"}},[l("div",{staticClass:"tab-content df dfv"},[l("div",{staticClass:"query-form df"},[l("el-form",{attrs:{inline:!0,"label-width":"100px"}},[l("el-form-item",{attrs:{label:"设备类型",prop:"deviceType"}},[l("el-input",{attrs:{size:"mini"},nativeOn:{keyup:function(t){return!t.type.indexOf("key")&&e._k(t.keyCode,"enter",13,t.key,"Enter")?null:e.getDeviceLevelList()}},model:{value:e.deviceLevelQuery.deviceType,callback:function(t){e.$set(e.deviceLevelQuery,"deviceType",t)},expression:"deviceLevelQuery.deviceType"}})],1),e._v(" "),l("el-form-item",{attrs:{label:"设备型号",prop:"deviceModel"}},[l("el-input",{attrs:{size:"mini"},nativeOn:{keyup:function(t){return!t.type.indexOf("key")&&e._k(t.keyCode,"enter",13,t.key,"Enter")?null:e.getDeviceLevelList()}},model:{value:e.deviceLevelQuery.deviceModel,callback:function(t){e.$set(e.deviceLevelQuery,"deviceModel",t)},expression:"deviceLevelQuery.deviceModel"}})],1),e._v(" "),l("el-form-item",{attrs:{label:"操作用户",prop:"operatorName"}},[l("el-input",{attrs:{size:"mini"},nativeOn:{keyup:function(t){return!t.type.indexOf("key")&&e._k(t.keyCode,"enter",13,t.key,"Enter")?null:e.getDeviceLevelList()}},model:{value:e.deviceLevelQuery.operatorName,callback:function(t){e.$set(e.deviceLevelQuery,"operatorName",t)},expression:"deviceLevelQuery.operatorName"}})],1),e._v(" "),l("el-button",{staticStyle:{"margin-left":"33px"},attrs:{type:"primary",size:"mini"},on:{click:function(t){return e.getDeviceLevelList()}}},[e._v("确认")]),e._v(" "),l("el-button",{attrs:{type:"primary",size:"mini"},on:{click:function(t){e.deviceLevelQuery={}}}},[e._v("清除")])],1)],1),e._v(" "),l("div",{directives:[{name:"loading",rawName:"v-loading",value:e.$apiLoading.deviceTypeLevelControllerList,expression:"$apiLoading.deviceTypeLevelControllerList"}],staticClass:"list df dfv"},[l("div",{staticClass:"list-header"},[l("h3",[e._v("设备等级列表")]),e._v(" "),l("div",{staticClass:"btn-group"},[l("el-button",{attrs:{type:"primary",size:"mini"},on:{click:function(t){return e.addDeviceLevelToModal()}}},[e._v("添加")]),e._v(" "),l("el-button",{attrs:{type:"primary",size:"mini"},on:{click:function(t){return e.deleteDeviceToModal()}}},[e._v("批量删除")])],1)]),e._v(" "),l("div",{staticClass:"table-box df dfv"},[l("el-table",{ref:"deviceLevelList",attrs:{height:"100%",data:e.deviceLevelList}},[l("el-table-column",{attrs:{type:"selection"}}),e._v(" "),l("el-table-column",{attrs:{label:"企业名称",prop:"enterpriseName"}}),e._v(" "),l("el-table-column",{attrs:{label:"服务名称",prop:"serverName"}}),e._v(" "),l("el-table-column",{attrs:{label:"设备类型",prop:"deviceType"}}),e._v(" "),l("el-table-column",{attrs:{label:"设备型号",prop:"deviceModel"}}),e._v(" "),l("el-table-column",{attrs:{label:"设备告警级别",width:"110",prop:"level5"}}),e._v(" "),l("el-table-column",{attrs:{label:"1",width:"29"},scopedSlots:e._u([{key:"default",fn:function(e){return[l("i",{staticClass:"level-select",class:(e.row.alarm||[])[0]&&"selected"})]}}])}),e._v(" "),l("el-table-column",{attrs:{label:"2",width:"29"},scopedSlots:e._u([{key:"default",fn:function(e){return[l("i",{staticClass:"level-select",class:(e.row.alarm||[])[1]&&"selected"})]}}])}),e._v(" "),l("el-table-column",{attrs:{label:"3",width:"29"},scopedSlots:e._u([{key:"default",fn:function(e){return[l("i",{staticClass:"level-select",class:(e.row.alarm||[])[2]&&"selected"})]}}])}),e._v(" "),l("el-table-column",{attrs:{label:"4",width:"29"},scopedSlots:e._u([{key:"default",fn:function(e){return[l("i",{staticClass:"level-select",class:(e.row.alarm||[])[3]&&"selected"})]}}])}),e._v(" "),l("el-table-column",{attrs:{label:"5",width:"29"},scopedSlots:e._u([{key:"default",fn:function(e){return[l("i",{staticClass:"level-select",class:(e.row.alarm||[])[4]&&"selected"})]}}])}),e._v(" "),l("el-table-column",{attrs:{label:"6",width:"29"},scopedSlots:e._u([{key:"default",fn:function(e){return[l("i",{staticClass:"level-select",class:(e.row.alarm||[])[5]&&"selected"})]}}])}),e._v(" "),l("el-table-column",{attrs:{label:"7",width:"29"},scopedSlots:e._u([{key:"default",fn:function(e){return[l("i",{staticClass:"level-select",class:(e.row.alarm||[])[6]&&"selected"})]}}])}),e._v(" "),l("el-table-column",{attrs:{label:"8",width:"49"},scopedSlots:e._u([{key:"default",fn:function(e){return[l("i",{staticClass:"level-select",class:(e.row.alarm||[])[7]&&"selected"})]}}])}),e._v(" "),l("el-table-column",{attrs:{label:"操作用户",prop:"operator.name"}}),e._v(" "),l("el-table-column",{attrs:{label:"操作时间",prop:"updateTime",formatter:e.$tableFilter("dateFormatFilter")}}),e._v(" "),l("el-table-column",{attrs:{label:"操作"},scopedSlots:e._u([{key:"default",fn:function(t){return[l("label",[l("a",{staticClass:"icomoon pic-edit",attrs:{title:"修改"},on:{click:function(l){return e.addDeviceLevelToModal(t.row)}}})]),e._v(" "),l("label",[l("a",{staticClass:"icomoon pic-delete",attrs:{title:"删除"},on:{click:function(l){return e.deleteDeviceToModal(t.row)}}})])]}}])})],1)],1),e._v(" "),l("el-col",{staticClass:"toolbar",attrs:{span:24}},[l("el-pagination",{staticStyle:{float:"right"},attrs:{layout:"total, prev, pager, next,sizes, jumper","page-sizes":[10,15,50,100],"page-size":e.deviceLevelPagi.pageSize,total:e.deviceLevelPagi.total},on:{"current-change":e.deviceLevelPagi.handleCurrentChange,"size-change":e.deviceLevelPagi.pageSizeChange}})],1)],1)])]),e._v(" "),l("el-tab-pane",{attrs:{label:"等级对应",name:"3"}},[l("div",{staticClass:"tab-content df dfv"},[l("div",{staticClass:"query-form df"},[l("el-form",{staticStyle:{"max-width":"1050px"},attrs:{inline:!0,"label-width":"100px"}},[l("el-form-item",{attrs:{label:"操作用户",prop:"operatorName"}},[l("el-input",{attrs:{size:"mini"},nativeOn:{keyup:function(t){return!t.type.indexOf("key")&&e._k(t.keyCode,"enter",13,t.key,"Enter")?null:e.getRelationList()}},model:{value:e.relationQuery.operatorName,callback:function(t){e.$set(e.relationQuery,"operatorName",t)},expression:"relationQuery.operatorName"}})],1),e._v(" "),l("el-form-item",{attrs:{label:"设备类型",prop:"deviceType"}},[l("el-input",{attrs:{size:"mini"},nativeOn:{keyup:function(t){return!t.type.indexOf("key")&&e._k(t.keyCode,"enter",13,t.key,"Enter")?null:e.getRelationList()}},model:{value:e.relationQuery.deviceType,callback:function(t){e.$set(e.relationQuery,"deviceType",t)},expression:"relationQuery.deviceType"}})],1),e._v(" "),l("el-form-item",{attrs:{label:"设备型号",prop:"deviceModel"}},[l("el-input",{attrs:{size:"mini"},nativeOn:{keyup:function(t){return!t.type.indexOf("key")&&e._k(t.keyCode,"enter",13,t.key,"Enter")?null:e.getRelationList()}},model:{value:e.relationQuery.deviceModel,callback:function(t){e.$set(e.relationQuery,"deviceModel",t)},expression:"relationQuery.deviceModel"}})],1),e._v(" "),l("el-form-item",{attrs:{label:"等级变更",prop:"generate"}},[l("el-select",{attrs:{size:"mini"},on:{input:function(t){return e.getRelationList()}},model:{value:e.relationQuery.generate,callback:function(t){e.$set(e.relationQuery,"generate",t)},expression:"relationQuery.generate"}},[l("el-option",{attrs:{label:"全部",value:void 0}},[e._v("全部")]),e._v(" "),l("el-option",{attrs:{label:"系统",value:"系统"}},[e._v("系统")]),e._v(" "),l("el-option",{attrs:{label:"手动",value:"手动"}},[e._v("手动")])],1)],1),e._v(" "),l("el-button",{staticStyle:{"margin-left":"33px"},attrs:{type:"primary",size:"mini"},on:{click:e.getRelationList}},[e._v("确认")]),e._v(" "),l("el-button",{attrs:{type:"primary",size:"mini"},on:{click:function(t){e.relationQuery={}}}},[e._v("清除")])],1)],1),e._v(" "),l("div",{directives:[{name:"loading",rawName:"v-loading",value:e.$apiLoading.alarmLevelControllerList,expression:"$apiLoading.alarmLevelControllerList"}],staticClass:"list df dfv"},[l("div",{staticClass:"list-header"},[l("h3",[e._v("等级对应")]),e._v(" "),l("div",{staticClass:"btn-group"})]),e._v(" "),l("div",{staticClass:"table-box df dfv"},[l("el-table",{attrs:{height:"100%",data:e.relationList,"tree-props":{children:"children"},"row-key":"id","row-class-name":function(e){return e.row.invisible?"invisible":""}}},[l("el-table-column",{attrs:{label:"企业名称",prop:"enterpriseName"}}),e._v(" "),l("el-table-column",{attrs:{label:"服务名称",prop:"serverName"}}),e._v(" "),l("el-table-column",{attrs:{label:"设备类型",prop:"deviceType"}}),e._v(" "),l("el-table-column",{attrs:{label:"设备型号"},scopedSlots:e._u([{key:"default",fn:function(t){return[t.row.isColor?l("div",{staticClass:"alarm-color",style:"background: "+t.row.color}):l("span",[e._v(e._s(t.row.deviceModel))])]}}])}),e._v(" "),l("el-table-column",{attrs:{label:"等级变更",prop:"generate"}}),e._v(" "),l("el-table-column",{attrs:{label:"操作用户",prop:"operator.name",formatter:e.$tableFilter("nullFilter")}}),e._v(" "),l("el-table-column",{attrs:{label:"创建时间",prop:"updateTime"},scopedSlots:e._u([{key:"default",fn:function(t){return[t.row.showOps?l("span",[e._v(e._s(e._f("dateFormatFilter")(t.row.updateTime)))]):e._e()]}}])}),e._v(" "),l("el-table-column",{attrs:{label:"操作"},scopedSlots:e._u([{key:"default",fn:function(t){return[l("label",{directives:[{name:"show",rawName:"v-show",value:t.row.showOps,expression:"scope.row.showOps"}]},[l("a",{staticClass:"icomoon pic-edit",attrs:{title:"修改"},on:{click:function(l){return e.setRelationToModal(t.row)}}})])]}}])})],1)],1),e._v(" "),l("el-col",{staticClass:"toolbar",attrs:{span:24}},[l("el-pagination",{staticStyle:{float:"right"},attrs:{layout:"total, prev, pager, next,sizes, jumper","page-sizes":[10,15,50,100],"page-size":e.alarmLevelPagi.pageSize,total:e.alarmLevelPagi.total},on:{"current-change":e.alarmLevelPagi.handleCurrentChange,"size-change":e.alarmLevelPagi.pageSizeChange}})],1)],1)])])],1),e._v(" "),l("el-dialog",{staticClass:"m400",attrs:{title:"add"===e.addDeviceLevelForm.__type?"添加告警级别设置":"设备告警级别设置","close-on-click-modal":!1,visible:e.addDeviceLevelVisible},on:{close:function(t){e.addDeviceLevelVisible=!1,e.$refs.addDeviceLevel.resetFields()}}},[l("el-form",{ref:"addDeviceLevel",attrs:{model:e.addDeviceLevelForm,rules:e.addDeviceLevelRules,"label-width":"110px"}},[l("el-form-item",{attrs:{label:"企业名称",prop:"enterpriseCode"}},[l("span",[e._v(e._s(e.curEnterprise.name))])]),e._v(" "),l("el-form-item",{attrs:{label:"服务名称",prop:"serverCode"}},[l("span",[e._v(e._s(e.curService.name))])]),e._v(" "),l("el-form-item",{attrs:{label:"设备类型",prop:"deviceType"}},[l("el-select",{staticStyle:{width:"240px"},attrs:{disabled:"add"!==e.addDeviceLevelForm.__type,size:"mini"},on:{input:function(t){return e.$set(e.addDeviceLevelForm,"deviceModel",null)}},model:{value:e.addDeviceLevelForm.deviceType,callback:function(t){e.$set(e.addDeviceLevelForm,"deviceType",t)},expression:"addDeviceLevelForm.deviceType"}},e._l(e.addDeviceLevelForm.deviceTypeList,function(t,a){return l("el-option",{key:a,attrs:{label:t.type,value:t.type}},[e._v(e._s(t.type))])}),1)],1),e._v(" "),l("el-form-item",{attrs:{label:"设备型号",prop:"deviceModel"}},[l("el-select",{staticStyle:{width:"240px"},attrs:{disabled:"add"!==e.addDeviceLevelForm.__type,size:"mini"},model:{value:e.addDeviceLevelForm.deviceModel,callback:function(t){e.$set(e.addDeviceLevelForm,"deviceModel",t)},expression:"addDeviceLevelForm.deviceModel"}},e._l(((e.addDeviceLevelForm.deviceTypeList||[]).find(function(t){return t.type===e.addDeviceLevelForm.deviceType})||{models:[]}).models,function(t,a){return l("el-option",{key:a,attrs:{label:t,value:t}},[e._v(e._s(t))])}),1)],1),e._v(" "),l("el-form-item",{attrs:{label:"告警级别设置",prop:"levels"}},[l("el-select",{staticClass:"multiple",staticStyle:{width:"240px"},attrs:{size:"mini",multiple:""},model:{value:e.addDeviceLevelForm.levels,callback:function(t){e.$set(e.addDeviceLevelForm,"levels",t)},expression:"addDeviceLevelForm.levels"}},e._l([0,1,2,3,4,5,6,7],function(t,a){return l("el-option",{key:a,attrs:{label:e.oneToEight[t]+"级告警",value:t+1}},[e._v(e._s(e.oneToEight[t]+"级告警"))])}),1)],1)],1),e._v(" "),l("span",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[l("el-button",{attrs:{size:"mini",type:"default"},on:{click:function(t){e.addDeviceLevelVisible=!1,e.$refs.addDeviceLevel.resetFields()}}},[e._v("取 消")]),e._v(" "),l("el-button",{attrs:{size:"mini",type:"primary"},on:{click:function(t){return e.addDeviceLevelSubmit()}}},[e._v("确 定")])],1)],1),e._v(" "),l("el-dialog",{staticClass:"m400",attrs:{title:"add"===e.addAlarmLevelForm.__type?"添加":"修改","close-on-click-modal":!1,visible:e.addAlarmLevelVisible},on:{close:function(t){e.addAlarmLevelVisible=!1,e.$refs.addAlarmLevel.resetFields()}}},[l("el-form",{ref:"addAlarmLevel",staticClass:"add-alarm-level-form",attrs:{model:e.addAlarmLevelForm,rules:e.addAlarmLevelRules,"label-width":"95px","label-position":"left"}},[l("el-form-item",{attrs:{label:" 规则名称",prop:"name"}},[l("el-input",{staticStyle:{width:"190px"},attrs:{maxlength:20,size:"mini"},model:{value:e.addAlarmLevelForm.name,callback:function(t){e.$set(e.addAlarmLevelForm,"name",t)},expression:"addAlarmLevelForm.name"}})],1),e._v(" "),l("el-form-item",{attrs:{label:"　操作用户",prop:"machineType"}},[l("span",[e._v(e._s(e.curUser.name))])]),e._v(" "),l("el-form-item",{attrs:{label:"　企业名称",prop:"enterpriseCode"}},[l("span",[e._v(e._s(e.curEnterprise.name))])]),e._v(" "),l("el-form-item",{attrs:{label:"　服务名称",prop:"serverCode"}},[l("span",[e._v(e._s(e.curService.name))])]),e._v(" "),l("el-form-item",{attrs:{label:"　告警等级"}},[l("el-select",{staticStyle:{width:"190px"},attrs:{size:"mini"},on:{input:e.resetAllAlarmSettings},model:{value:e.addAlarmLevelForm.alarmLength,callback:function(t){e.$set(e.addAlarmLevelForm,"alarmLength",t)},expression:"addAlarmLevelForm.alarmLength"}},e._l(e.oneToEight,function(t,a){return l("el-option",{key:a,attrs:{label:t+"级告警",value:a}},[e._v(e._s(t+"级告警"))])}),1)],1),e._v(" "),l("el-form-item",{attrs:{label:"　等级名称及颜色设定","label-width":"180px"}}),e._v(" "),e._l(e.oneToEight.slice(0,e.addAlarmLevelForm.alarmLength+1||0),function(t,a){return l("el-form-item",{key:a,staticClass:"is-required",attrs:{label:" "+t+"警等级",prop:"addAlarmLevelForm"+a}},[l("el-input",{staticStyle:{width:"190px"},attrs:{size:"mini"},model:{value:e.addAlarmLevelForm.levelNames[a],callback:function(t){e.$set(e.addAlarmLevelForm.levelNames,a,t)},expression:"addAlarmLevelForm.levelNames[index]"}}),e._v(" "),l("el-color-picker",{staticStyle:{"vertical-align":"-10px","margin-left":"10px"},attrs:{size:"mini"},model:{value:e.addAlarmLevelForm.colors[a],callback:function(t){e.$set(e.addAlarmLevelForm.colors,a,t)},expression:"addAlarmLevelForm.colors[index]"}})],1)})],2),e._v(" "),l("span",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[l("el-button",{attrs:{size:"mini",type:"default"},on:{click:function(t){e.addAlarmLevelVisible=!1,e.$refs.addAlarmLevel.resetFields()}}},[e._v("取 消")]),e._v(" "),l("el-button",{attrs:{size:"mini",type:"primary"},on:{click:function(t){return e.addAlarmLevelSubmit()}}},[e._v("确 定")])],1)],1),e._v(" "),l("el-dialog",{staticClass:"m400",attrs:{title:"设备告警级对应修改","close-on-click-modal":!1,visible:e.setRelationVisible},on:{close:function(t){e.setRelationVisible=!1}}},[l("el-form",{ref:"setRelation",attrs:{model:e.setRelationForm,rules:e.setRelationRules,"label-width":"100px","label-position":"left"}},[l("el-form-item",{attrs:{label:"企业名称",prop:"enterpriseCode"}},[l("span",[e._v(e._s(e.curEnterprise.name))])]),e._v(" "),l("el-form-item",{attrs:{label:"服务名称",prop:"serverCode"}},[l("span",[e._v(e._s(e.curService.name))])]),e._v(" "),l("el-form-item",{attrs:{label:"设备类型",prop:"machineType"}},[l("span",[e._v(e._s(e.setRelationForm.deviceType))])]),e._v(" "),l("el-form-item",{attrs:{label:"设备型号",prop:"aaa"}},[l("span",[e._v(e._s(e.setRelationForm.deviceModel))])]),e._v(" "),e._l(e.setRelationForm.sourceLevelList,function(t,a){return l("el-form-item",{key:a,attrs:{label:e.setRelationForm.sourceLevelList[a]+"级告警",prop:"relation"+a}},[l("el-select",{staticStyle:{width:"190px"},attrs:{size:"mini"},model:{value:e.setRelationForm.targetLevelList[a],callback:function(t){e.$set(e.setRelationForm.targetLevelList,a,t)},expression:"setRelationForm.targetLevelList[index]"}},e._l(e.setRelationForm.targetLevelOptions||[],function(t,a){return l("el-option",{key:a,attrs:{label:t.levelName,value:t.level}},[e._v(e._s(t.levelName))])}),1)],1)})],2),e._v(" "),l("span",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[l("el-button",{attrs:{size:"mini",type:"default"},on:{click:function(t){e.setRelationVisible=!1}}},[e._v("取 消")]),e._v(" "),l("el-button",{attrs:{size:"mini",type:"primary"},on:{click:function(t){return e.setRelationSubmit()}}},[e._v("确 定")])],1)],1)],1)},staticRenderFns:[]};var s=l("VU/8")(r,o,!1,function(e){l("EUD6")},"data-v-0b794ede",null);t.default=s.exports}});