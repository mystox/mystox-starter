webpackJsonp([3],{FnMu:function(t,e){},"N5D/":function(t,e){},idHs:function(t,e,a){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var l=a("bOdI"),i=a.n(l),s=a("dYWt"),n={components:{tree:a.n(s).a},data:function(){var t=this;return{formInline:{type:void 0,status:void 0},tableData:[],detailData:[],formVisible:!1,page:5,pageSize:10,total2:0,pagination:{page:1,pageSize:15,total:0,handleCurrentChange:function(e){t.pagination.page=e,t.getData()},pageSizeChange:function(e){t.pagination.pageSize=e,t.getData()}},search:"",searchData:"",nodes:[{id:1,label:"企业服务根节点",children:[{id:2,label:"新研联",children:[{id:5,label:"招不出"},{id:6,label:"白活不出"}]},{id:3,label:"国动",children:[{id:5,label:"阿里云"},{id:6,label:"腾讯云"}]},{id:4,label:"义益钛迪",children:[{id:5,label:"义益云"},{id:6,label:"智慧用电"}]}]}],setting:{check:{enable:!0},data:{simpleData:{enable:!1,pIdKey:"pid"},key:{children:"children",name:"label"}},view:{showIcon:!1}}}},methods:{getData:function(){var t=this;this.$api.getReportTaskList({serverCode:"YYTD_MQTT_DEMO_1.0.0",enterepriseCode:"YYTD",taskStatus:0,page:this.pagination.page,count:this.pagination.pageSize}).then(function(e){t.tableData=e.list,t.pagination.total=e.total;for(var a=0;a<t.tableData.length;a++){0==t.tableData[a].taskStatus?t.tableData[a].taskStatus="无效":1==t.tableData[a].taskStatus?t.tableData[a].taskStatus="有效":2==t.tableData[a].taskStatus?t.tableData[a].taskStatus="执行中":3==t.tableData[a].taskStatus&&(t.tableData[a].taskStatus="超时");var l,i,s=new Date,n=new Date;s.setTime(t.tableData[a].startTime),n.setTime(t.tableData[a].endTime),l=s.getFullYear()+"-"+s.getMonth()+"-"+s.getDay(),i=n.getFullYear()+"-"+n.getMonth()+"-"+n.getDay(),e.list[a].startTime=l,e.list[a].endTime=i}})},handleSearch:function(){var t,e=this;this.$api.getReportTaskList((t={serverCode:"YYTD_MQTT_DEMO_1.0.0",enterepriseCode:"YYTD",taskStatus:0,currentPage:this.pagination.page,pageSize:this.pagination.pageSize,taskType:this.formInline.type},i()(t,"taskStatus",this.formInline.status),i()(t,"page",1),i()(t,"count",2),t)).then(function(t){e.tableData=t.list,e.tableData.forEach(function(t){t.__operaValidity=t.operaValidity>0?e.$util.dateFormat(new Date(t.operaValidity)):"永久"});for(var a=0;a<t.list.length;a++)0==t.list[a].taskStatus?t.list[a].taskStatus="无效":1==t.list[a].taskStatus?t.list[a].taskStatus="有效":2==t.list[a].taskStatus?t.list[a].taskStatus="执行中":3==t.list[a].taskStatus&&(t.list[a].taskStatus="超时")})},handleClear:function(){this.$refs.formInline.resetFields()},pageSizeChange:function(t){this.pageSize=t,this.getDetail()},handleCurrentChange:function(t){this.page=t,this.getDetail()},getDetail:function(){var t=this;this.$http.post("/reports/getReportTaskResults",{taskId:this.tableData[this.curIndex].id,page:this.page,count:this.pageSize}).then(function(e){console.log(e),t.detailData=e.data.data.list,t.total2=e.data.data.total})},handleView:function(t,e){this.curIndex=t,this.formVisible=!0,this.page=1,this.getDetail()},handleDetail:function(t,e){this.$msgbox(e.result.dataResult,"详情")},handleCancel:function(){this.formVisible=!1}},mounted:function(){this.handleSearch()}},o={render:function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("section",{attrs:{id:"service"}},[a("div",{attrs:{id:"main"}},[a("div",{staticClass:"aside"},[a("div",{staticStyle:{left:"10px"}},[a("el-form",[a("el-input",{staticStyle:{"mix-width":"320",left:"0px"},attrs:{placeholder:"请输入搜索内容"},model:{value:t.search,callback:function(e){t.search=e},expression:"search"}})],1),t._v(" "),a("hr")],1),t._v(" "),a("div",[a("tree",{ref:"sb",attrs:{nodes:t.nodes,setting:t.setting}})],1)]),t._v(" "),a("div",{staticClass:"container"},[a("div",{staticClass:"query-form"},[a("el-form",{ref:"formInline",staticClass:"demo-form-inline",attrs:{inline:!0,model:t.formInline,size:"small"}},[a("el-form-item",{attrs:{label:"任务类型",prop:"type"}},[a("el-select",{attrs:{placeholder:"请选择"},model:{value:t.formInline.type,callback:function(e){t.$set(t.formInline,"type",e)},expression:"formInline.type"}},[a("el-option",{attrs:{label:"全部",value:void 0}}),t._v(" "),a("el-option",{attrs:{label:"定时任务",value:"schecduled"}}),t._v(" "),a("el-option",{attrs:{label:"单次任务",value:"singleTask"}})],1)],1),t._v(" "),a("el-form-item",{attrs:{label:"任务状态",prop:"status"}},[a("el-select",{attrs:{placeholder:"请选择"},model:{value:t.formInline.status,callback:function(e){t.$set(t.formInline,"status",e)},expression:"formInline.status"}},[a("el-option",{attrs:{label:"全部",value:void 0}}),t._v(" "),a("el-option",{attrs:{label:"无效",value:0}}),t._v(" "),a("el-option",{attrs:{label:"有效",value:1}}),t._v(" "),a("el-option",{attrs:{label:"执行中",value:2}}),t._v(" "),a("el-option",{attrs:{label:"超时",value:3}})],1)],1),t._v(" "),a("el-form-item",[a("el-button",{attrs:{type:"primary"},on:{click:t.handleSearch}},[t._v("查询")]),t._v(" "),a("el-button",{attrs:{type:"primary"},on:{click:t.handleClear}},[t._v("清除")])],1)],1)],1),t._v(" "),a("div",[a("div",{staticStyle:{float:"left","padding-left":"12px","padding-top":"10px","background-color":"#193166",width:"100%",height:"30px"}},[t._v("报表任务列表")]),t._v(" "),a("div",{staticClass:"table-box"},[[a("el-table",{attrs:{data:t.tableData,border:""}},[a("el-table-column",{attrs:{prop:"reportName",label:"报表名称","min-width":"120"}}),t._v(" "),a("el-table-column",{attrs:{prop:"taskStatus",label:"任务状态","min-width":"120"}}),t._v(" "),a("el-table-column",{attrs:{label:"有效期",prop:"__operaValidity"}}),t._v(" "),a("el-table-column",{attrs:{prop:"startTime",label:"任务开始时间","min-width":"120",formatter:t.$tableFilter("dateFormatFilter")}}),t._v(" "),a("el-table-column",{attrs:{prop:"endTime",label:"任务结束时间","min-width":"120",formatter:t.$tableFilter("dateFormatFilter")}}),t._v(" "),a("el-table-column",{attrs:{prop:"reportServerCode",label:"报表服务编码","min-width":"120"}}),t._v(" "),a("el-table-column",{attrs:{prop:"taskType",label:"报表类型","min-width":"120",formatter:t.$tableFilter("taskTypeFilter")}}),t._v(" "),a("el-table-column",{attrs:{label:"操作","min-width":"120"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("el-button",{staticClass:"icomoon pic-look",attrs:{type:"text"},on:{click:function(a){return t.handleView(e.$index,e.row)}}})]}}])})],1)]],2)]),t._v(" "),a("div",{staticClass:"footer"},[a("el-col",{staticClass:"toolbar",attrs:{span:24}},[a("el-pagination",{staticStyle:{float:"right"},attrs:{layout:"total, prev, pager, next,sizes, jumper","page-sizes":[10,15,50,100],"page-size":t.pagination.pageSize,total:t.pagination.total},on:{"current-change":t.pagination.handleCurrentChange,"size-change":t.pagination.pageSizeChange}})],1)],1)])]),t._v(" "),a("el-dialog",{staticClass:"m800",attrs:{title:"查看详情",visible:t.formVisible,"close-on-click-modal":!1},on:{close:t.handleCancel}},[a("div",{staticClass:"table-box",staticStyle:{height:"500px"}},[a("el-table",{attrs:{data:t.detailData,border:"",height:"100%"}},[a("el-table-column",{attrs:{type:"index","min-width":"80"}}),t._v(" "),a("el-table-column",{attrs:{prop:"runId",label:"运行编号","min-width":"160"}}),t._v(" "),a("el-table-column",{attrs:{prop:"startTime",label:"任务开始时间","min-width":"160",formatter:t.$tableFilter("dateFormatFilter")}}),t._v(" "),a("el-table-column",{attrs:{prop:"recordTime",label:"任务结束时间","min-width":"160",formatter:t.$tableFilter("dateFormatFilter")}}),t._v(" "),a("el-table-column",{attrs:{label:"操作","min-width":"70"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("el-button",{staticClass:"icomoon pic-look",attrs:{type:"text",size:"small"},on:{click:function(a){return t.handleDetail(e.$index,e.row)}}})]}}])})],1),t._v(" "),a("el-col",{staticClass:"toolbar",attrs:{span:24}},[a("el-pagination",{staticStyle:{float:"right"},attrs:{layout:"total, prev, pager, next","page-sizes":[10,15,50,100],"page-size":t.pageSize,total:t.total2},on:{"current-change":t.handleCurrentChange,"size-change":t.pageSizeChange}})],1)],1)])],1)},staticRenderFns:[]};var r=a("VU/8")(n,o,!1,function(t){a("FnMu"),a("N5D/")},"data-v-84eafbde",null);e.default=r.exports}});