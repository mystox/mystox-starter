webpackJsonp([3],{"94Bc":function(e,t){},IVV0:function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var r={render:function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",{staticClass:"alarmList flex-column"},[a("sc-breadcrumb",{attrs:{urls:[{name:"SN列表"},{name:"设备列表",path:"/devices",query:{sN:e.$route.query.sN}},{name:"告警门限设置"}]}}),e._v(" "),a("operation-bar-layout",{staticStyle:{}},[a("div",{attrs:{slot:"query"},slot:"query"},[a("el-form",{ref:"searcher",attrs:{model:e.searcher,"label-position":"right",inline:!0}},[a("el-form-item",{attrs:{label:"告警名称",prop:"alarmDesc","label-width":"70px"}},[a("el-input",{attrs:{placeholder:"请输入告警名称"},model:{value:e.searcher.alarmDesc,callback:function(t){e.$set(e.searcher,"alarmDesc","string"==typeof t?t.trim():t)},expression:"searcher.alarmDesc"}})],1)],1)],1),e._v(" "),a("div",{attrs:{slot:"operate"},slot:"operate"},[a("el-button",{attrs:{type:"primary"},on:{click:e.getThreshold}},[e._v("查询")])],1)]),e._v(" "),a("table-box",{staticClass:"flex-1",attrs:{"row-class-name":"cursor-point","row-click":e.intoNextPage,loading:e.loading,stripe:!0,border:!0,data:e.alarmList},on:{"selection-change":e.handleSelectionChange}},[a("el-table-column",{attrs:{type:"selection"}}),e._v(" "),e._l(e.columns,function(t){return a("el-table-column",{key:t.label,attrs:{formatter:e.$tableFilter(t.filter),prop:t.value,label:t.label,"min-width":t.width,fixed:t.fixed,className:t.className,resizable:!1}})}),e._v(" "),a("el-table-column",{attrs:{label:e.$t("SECURITY.USER.OPERATION"),fixed:"right"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("i",{staticStyle:{color:"#20A0FF",cursor:"pointer"},on:{click:function(a){return e.showDialog("threshold",t.row)}}},[e._v("门限设置")])]}}])})],2),e._v(" "),a("modal",{ref:"threshold",attrs:{option:e.optionModify}})],1)},staticRenderFns:[]};var o=a("C7Lr")({name:"s-points",props:{},data:function(){return{searcher:{alarmDesc:""},multipleSelection:[],alarmList:[],userGroups:[],columns:[{label:"告警点 ID",value:"alarmId",filter:"nullFilter",className:""},{label:"告警名称",value:"alarmDesc",filter:"nullFilter",className:""},{label:"门限值",value:"threshold",filter:"nullFilter",className:""}],modifyCont:{username:"",password:"",group_name:"",comment:""},pagination:{total:0,currentPage:1,pageSize:15,onChange:this.onPaginationChanged},loading:!1}},computed:{optionModify:function(){return{name:"门限设置",form:{"门限值":[{type:"input",name:"threshold",defaultValue:this.modifyCont.threshold,rule:{required:!0,requiredError:"输入内容不可为空。"}}]},clearText:this.$t("OPERATION.CLEAR"),clear:this.clear,executeText:this.$t("OPERATION.CONFIRM"),execute:this.executeModify,style:{}}}},methods:{addMockData:function(){for(var e=0;e<10;e++)this.alarmList.push({username:"pointName",password:"123456",group_name:"城管大队三组",comment:"**********"});this.pagination.total=this.alarmList.length},getThreshold:function(){var e=this,t={alarmDesc:this.searcher.alarmDesc,port:this.$route.query.port,resNo:this.$route.query.resNo-0,type:this.$route.query.type-0};this.$api.getThreshold(t,this.$route.query.sN).then(function(t){e.pagination.total=t.data.length,e.alarmList=t.data,e.loading=!1},function(t){t&&(e.loading=!1)})},onPaginationChanged:function(e){this.pagination.pageSize=e.pageSize,this.pagination.currentPage=e.currentPage,this.getThreshold()},handleSelectionChange:function(e){this.multipleSelection=this.comFunc.map(e,"username")},executeModify:function(e){this.setThreshold(e)},clear:function(e){},showDialog:function(e,t){t&&(this.modifyCont=t),this.$refs[e].toModal()},intoNextPage:function(){},setThreshold:function(e){var t=this,a={deviceId:this.modifyCont.deviceId,configId:this.modifyCont.id,coId:this.modifyCont.coId,threshold:e.threshold-0};this.$api.setThreshold(a,this.$route.query.sN).then(function(){t.getThreshold()})}},mounted:function(){this.getThreshold()}},r,!1,function(e){a("94Bc")},null,null);t.default=o.exports}});