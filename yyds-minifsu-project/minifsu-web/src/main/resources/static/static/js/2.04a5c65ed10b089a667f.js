webpackJsonp([2],{SxiZ:function(e,t){},xGy5:function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var l={name:"s-points",props:{},data:function(){return{searcher:{name:"",timeRange:[new Date((new Date).getFullYear(),(new Date).getMonth(),(new Date).getDate(),0,0,0),new Date((new Date).getFullYear(),(new Date).getMonth(),(new Date).getDate()+1,0,0,0)]},pickerOptions:{shortcuts:[{text:"最近一周",onClick:function(e){var t=new Date,a=new Date;a.setTime(a.getTime()-6048e5),e.$emit("pick",[a,t])}},{text:"最近一个月",onClick:function(e){var t=new Date,a=new Date;a.setTime(a.getTime()-2592e6),e.$emit("pick",[a,t])}},{text:"最近三个月",onClick:function(e){var t=new Date,a=new Date;a.setTime(a.getTime()-7776e6),e.$emit("pick",[a,t])}}]},multipleSelection:[],alarmList:[],userGroups:[],columns:[{label:"告警名称",value:"name",filter:"nullFilter",className:""},{label:"告警点 ID",value:"alarmId",filter:"nullFilter",className:""},{label:"告警点值",value:"value",filter:"nullFilter",className:""},{label:"门限",value:"threshold",filter:"nullFilter",className:""},{label:"上报时间",value:"tReport",filter:"dataFormatFilter",className:""},{label:"等级",value:"level",filter:"nullFilter",className:""}],modifyCont:{username:"",password:"",group_name:"",comment:""},pagination:{total:0,currentPage:1,pageSize:15,onChange:this.onPaginationChanged},loading:!1}},computed:{optionModify:function(){return{name:"门限设置",form:{"门限值":[{type:"input",name:"threshold",defaultValue:this.modifyCont.threshold,rule:{required:!0,requiredError:"输入内容不可为空。"}}]},clearText:this.$t("OPERATION.CLEAR"),clear:this.clear,executeText:this.$t("OPERATION.CONFIRM"),execute:this.executeModify,style:{}}}},methods:{addMockData:function(){for(var e=0;e<10;e++)this.alarmList.push({alarmId:"1001",beginDelayFT:0,delay:0,devName:"开关电源",dev_colId:"1-1_1001",h:0,link:19,name:"电池01熔丝故障告警",num:17,recoverDelay:0,recoverDelayFT:0,signalName:" 电池01熔丝故障告警",tReport:1556094413224,threshold:1,value:1});this.pagination.total=this.alarmList.length},handleSelectionChange:function(){},intoNextPage:function(){},getAlarmList:function(){var e=this,t={dev:this.$route.query.dev};this.$api.getAlarmList(t,this.$route.query.sN).then(function(t){e.alarmList=t.data})}},mounted:function(){this.getAlarmList()}},n={render:function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",{staticClass:"alarmList flex-column"},[a("sc-breadcrumb",{attrs:{urls:[{name:"SN列表"},{name:"设备列表",path:"/devices",query:{sN:e.$route.query.sN}},{name:"设备下实时告警"}]}}),e._v(" "),a("table-box",{staticClass:"flex-1",attrs:{"row-class-name":"cursor-point","row-click":e.intoNextPage,loading:e.loading,stripe:!0,border:!0,data:e.alarmList},on:{"selection-change":e.handleSelectionChange}},[a("el-table-column",{attrs:{type:"selection"}}),e._v(" "),e._l(e.columns,function(t){return a("el-table-column",{key:t.label,attrs:{formatter:e.$tableFilter(t.filter),prop:t.value,label:t.label,"min-width":t.width,fixed:t.fixed,className:t.className,resizable:!1}})}),e._v(" "),a("el-table-column",{attrs:{label:"告警描述",fixed:"right"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("span",[e._v(e._s(t.row.name)+"值为"+e._s(t.row.value))])]}}])})],2),e._v(" "),a("modal",{ref:"threshold",attrs:{option:e.optionModify}})],1)},staticRenderFns:[]};var i=a("C7Lr")(l,n,!1,function(e){a("SxiZ")},null,null);t.default=i.exports}});