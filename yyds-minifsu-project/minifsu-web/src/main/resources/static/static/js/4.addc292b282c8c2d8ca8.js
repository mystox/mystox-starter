webpackJsonp([4],{FSJw:function(e,t){},d837:function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var i=a("aA9S"),n=a.n(i),o={name:"s-sites",props:{},data:function(){return{searcher:{name:"",timeRange:[new Date-864e5,(new Date).getTime()]},pickerOptions:{shortcuts:[{text:"最近一周",onClick:function(e){var t=new Date,a=new Date;a.setTime(a.getTime()-6048e5),e.$emit("pick",[a,t])}},{text:"最近一个月",onClick:function(e){var t=new Date,a=new Date;a.setTime(a.getTime()-2592e6),e.$emit("pick",[a,t])}},{text:"最近三个月",onClick:function(e){var t=new Date,a=new Date;a.setTime(a.getTime()-7776e6),e.$emit("pick",[a,t])}}]},multipleSelection:[],fsuList:[],userGroups:[],columns:[{label:"流量大小",value:"payloadSize",filter:"nullFilter",className:""},{label:"报文类型",value:"pktType",filter:"nullFilter",className:""},{label:"时间",value:"recordTime",filter:"dataFormatFilter",className:""}],modifyCont:{},pagination:{total:0,currentPage:1,pageSize:15,onChange:this.onPaginationChanged},loading:!1}},computed:{},methods:{addMockData:function(){for(var e=0;e<10;e++)this.fsuList.push({recordTime:1556103423840,payloadSize:132,payload:{payload:{memUse:"14%",csq:31,pktType:11,sysTime:1556103420,SN:"MINI201904180005",cpuUse:"99%"},msgId:"01556103420"},pktType:11,id:"5cc040ff2ebfe763b82a3fa6",sn:"MINI201904180005"});this.pagination.total=this.fsuList.length},getTerminalLog:function(){var e=this,t={sn:this.$route.query.sN,startTime:new Date(this.searcher.timeRange[0]).getTime(),endTime:new Date(this.searcher.timeRange[1]).getTime(),page:this.pagination.currentPage,count:this.pagination.pageSize};this.$api.getTerminalLog(t,this.$route.query.sN).then(function(t){e.pagination.total=t.data.totalSize,e.fsuList=t.data.list,e.loading=!1},function(t){t&&(e.loading=!1)})},onPaginationChanged:function(e){this.pagination.pageSize=e.pageSize,this.pagination.currentPage=e.currentPage,this.getTerminalLog()},handleSelectionChange:function(e){this.multipleSelection=this.comFunc.map(e,"username")},intoNextPage:function(){},executeAdd:function(e){this.addContent(e)},executeModify:function(e){this.modifyCont=n()({},this.modifyCont,e),this.modifyCont.devIds=e.devIds.split("\n")},clear:function(e){}},mounted:function(){this.getTerminalLog()}},s={render:function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",{staticClass:"fsuList flex-column"},[a("sc-breadcrumb",{attrs:{urls:[{name:"SN列表",path:"/fsus"},{name:"SN PKT记录"}]}}),e._v(" "),a("operation-bar-layout",{staticStyle:{}},[a("div",{attrs:{slot:"query"},slot:"query"},[a("el-form",{ref:"searcher",attrs:{model:e.searcher,"label-position":"right",inline:!0}},[a("el-form-item",{attrs:{label:"时间",prop:"name","label-width":"45px"}},[a("el-date-picker",{attrs:{type:"datetimerange","picker-options":e.pickerOptions,placeholder:"选择时间范围",align:"right"},model:{value:e.searcher.timeRange,callback:function(t){e.$set(e.searcher,"timeRange",t)},expression:"searcher.timeRange"}})],1)],1)],1),e._v(" "),a("div",{attrs:{slot:"operate"},slot:"operate"},[a("el-button",{attrs:{type:"primary"},on:{click:function(t){return e.getTerminalLog()}}},[e._v("查询")])],1)]),e._v(" "),a("table-box",{staticClass:"flex-1",attrs:{"row-class-name":"cursor-point","row-click":e.intoNextPage,loading:e.loading,stripe:!0,border:!0,pagination:e.pagination,data:e.fsuList},on:{"selection-change":e.handleSelectionChange}},[a("el-table-column",{attrs:{type:"selection"}}),e._v(" "),e._l(e.columns,function(t){return a("el-table-column",{key:t.label,attrs:{formatter:e.$tableFilter(t.filter),prop:t.value,label:t.label,"min-width":t.width,fixed:t.fixed,className:t.className,resizable:!1}})}),e._v(" "),a("el-table-column",{attrs:{label:"报文"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("span",[e._v(e._s(JSON.stringify(t.row.payload.payload)))])]}}])})],2),e._v(" "),a("modal",{ref:"bindDialog",attrs:{option:e.optionBind}})],1)},staticRenderFns:[]};var l=a("C7Lr")(o,s,!1,function(e){a("FSJw")},null,null);t.default=l.exports}});