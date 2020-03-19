scloudApp.controller("reportListController",["$scope","$rootScope","reportService","treeService","$filter","$stateParams",function(e,t,r,c,a,i){function o(){e.table&&e.table.tbody&&(e.dataList=e.table.tbody.getPaginationItems(e.paginationConf.itemsPerPage,e.paginationConf.currentPage,!1))}function n(){var r,a,i;if("站点用电负荷统计表"!==e.reportName.value){if(!(r=c.getCheckedTierCodes("areaTree"))||0===r.length)return t.notify({type:"warn",text:"尚未选择区域"}),null}else{var o=c.getCheckedTierCodesAndSiteIds("siteTree",e.zTree.zNodes);o.siteIds.length>0&&(i=o.siteIds),r=o.codes,0===r.length&&(a=i,i=void 0)}return{tierCodes:r,siteIds:a,siteIds2:i}}function s(t){var r=t.keyName,c=t.statList,i=t.tableHead;i.unshift("序号"),i.unshift(e.reportName.value);for(var o=0;o<c.length;o++){var n=c[o];n.tReport&&(n.tReport=a("date")(n.tReport,"yyyy-MM-dd HH:mm:ss")),n.tRecover&&(n.tRecover=a("date")(n.tRecover,"yyyy-MM-dd HH:mm:ss")),n.tStart&&(n.tStart=a("date")(n.tStart,"yyyy-MM-dd HH:mm:ss")),n.tEnd&&(n.tEnd=a("date")(n.tEnd,"yyyy-MM-dd HH:mm:ss")),n.stationType&&(n.stationType=a("stationTypeFilter")(n.stationType)),n.level&&(n.level=a("alarmLevelFilter")(n.level))}for(var o=0,s=0;o<e.reportName.layout.length;o++)for(var u=0;u<e.reportName.layout[o].length;u++)e.reportName.layout[o][u].value=i[s++];return{thead:e.reportName.layout,keyName:r,tbody:c}}$state&&($state=window.$state),i&&(i=window.$stateParams),e.reportQuery={statisticsDepth:1,statisticalCycleType:"",deviceName:"UPS",dateType:1,alarmState:"待处理",top:"10"},e.isShowTimeSelect=!0,e.isLoading=!1,e.isLoading2=!1,e.paginationConf={currentPage:1,itemsPerPage:10,pagesLength:5,onChange:function(){}},e.statisticsDepthList=[{key:1,value:"一级"},{key:2,value:"二级"},{key:3,value:"三级"},{key:4,value:"站点"}],e.statisticalCycles={data:[]},e.topList=["1","3","5","10","20","50","100"],e.$watch("paginationConf.currentPage + paginationConf.itemsPerPage",o),e.$watch("reportQuery.dateType + reportName.value",function(t,r){if(e.reportName)switch(e.reportQuery.dateType){case 1:e.isShowTimeSelect=!("站点用电负荷统计表"===e.reportName.value||e.customQuery("统计周期",e.reportName)),e.statisticalCycles={data:[{id:1,text:"第1季度"},{id:2,text:"第2季度"},{id:3,text:"第3季度"},{id:4,text:"第4季度"}]},e.reportQuery.statisticalCycle=[{id:1,text:"第1季度"}];break;case 2:e.isShowTimeSelect=!("站点用电负荷统计表"===e.reportName.value||e.customQuery("统计周期",e.reportName)),e.statisticalCycles={data:[{id:101,text:"1月"},{id:102,text:"2月"},{id:103,text:"3月"},{id:104,text:"4月"},{id:105,text:"5月"},{id:106,text:"6月"},{id:107,text:"7月"},{id:108,text:"8月"},{id:109,text:"9月"},{id:110,text:"10月"},{id:111,text:"11月"},{id:112,text:"12月"}]},e.reportQuery.statisticalCycle=[{id:101,text:"1月"}];break;case 3:e.isShowTimeSelect=!("站点用电负荷统计表"===e.reportName.value||e.customQuery("统计周期",e.reportName)),e.statisticalCycles={data:new Array(52).join(" ").split(" ").map(function(e,t){return{id:1001+t,text:"第"+(t+1)+"周"}})},e.reportQuery.statisticalCycle=[{id:1001,text:"第1周"}];break;default:e.isShowTimeSelect=!0}}),e.$watch("reportName.value",function(t,r,c){if(e.reportName&&e.reportName.isCustom)e.curCustomRerpot=e.reportName;else switch(delete e.curCustomRerpot,t){case"市电停电断站情况统计表":case"FSU运行统计表":e.statisticsDepthList=[{key:1,value:"一级"},{key:2,value:"二级"},{key:3,value:"三级"}];break;default:e.statisticsDepthList=[{key:1,value:"一级"},{key:2,value:"二级"},{key:3,value:"三级"},{key:4,value:"站点"}]}}),e.searchKey={online:!0,offline:!0,keyword:""},e.getNodesByParamFuzzy=function(){setTimeout(function(){e.zTree.getNodesByParamFuzzy(e.searchKey.keyword,"areaTree","checkboxTierTree"),e.siteTree.getNodesByParamFuzzy(e.searchKey,"siteTree","checkboxTierTree")},10)},e.reportTypeList=[{value:"系统报表",reportNameList:[{key:102,value:"站点停电统计表",layout:[[{c:7,r:1}],[{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1}]],algorithm:"有效的筛选项：局站类型、开始时间",execute:function(t,c,a){r.getSystemSiteOff(t,c,function(e){a(e)},function(){e.isLoading=!1})},export:function(t,c,a,i){r.exportSystemSiteOff(t,c,this.value,a,function(){i()},function(){e.isLoading2=!1})}},{key:103,value:"站点停电明细表",layout:[[{c:9,r:1}],[{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1}]],algorithm:"有效的筛选项：局站类型、开始时间",execute:function(t,c,a){r.getSiteBlackoutTable(t,c,function(e){a(e)},function(){e.isLoading=!1})},export:function(t,c,a,i){r.exportSiteBlackoutTable(t,c,this.value,a,function(){i()},function(){e.isLoading2=!1})}},{key:104,value:"站点用电负荷统计表",layout:[[{c:18,r:1}],[{c:1,r:2},{c:1,r:2},{c:1,r:2},{c:1,r:2},{c:1,r:2},{c:1,r:2},{c:1,r:2},{c:3,r:1},{c:1,r:2},{c:1,r:2},{c:1,r:2},{c:1,r:2},{c:1,r:2},{c:1,r:2},{c:1,r:2},{c:1,r:2}],[{c:1,r:1},{c:1,r:1},{c:1,r:1}]],algorithm:"有效的筛选项：局站类型、开始时间",execute:function(t,c,a){r.getSiteElecLoadStatisticsTable(t,c,function(e){a(e)},function(){e.isLoading=!1})},export:function(t,c,a,i){r.exportSiteElecLoadStatisticsTable(t,c,this.value,a,function(){i()},function(){e.isLoading2=!1})}},{key:106,value:"市电停电断站情况统计表",layout:[[{c:6,r:1}],[{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1}]],algorithm:"日平均断站时长 = Σ站址的一级低压脱离告警时长 (查询时间范围内)/交维态站址总量(按照首次告警时间统计)；\n月平均断站时长 = Σ站址的一级低压脱离告警时长 (月首日到查询时间截止日期)/交维态站址总量(按照首次告警时间统计)。\n有效的筛选项：开始时间",execute:function(t,c,a){r.getSystemSiteBreakAverage(t,c,function(e){a(e)},function(){e.isLoading=!1})},export:function(t,c,a,i){r.exportSystemSiteBreakAverage(t,c,this.value,a,function(){i()},function(){e.isLoading2=!1})}},{key:107,value:"用电量统计报表",layout:[[{c:13,r:1}],[{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1}]],algorithm:"平台报表能统计平台“分路计量”上报的用电量信息。选定开始时间、结束时间，获取数据库中所有企业站点分路计量设备开始时间的\n“移动租户电量”、“联通租户电量”和“电信租户电量”以及结束时间的“移动租户电量”、“联通租户电量”和“电信租户电量”共6个信号量。\n",execute:function(t,c,a){r.getElecConsumptionNumericalTable(t,c,function(e){a(e)},function(){e.isLoading=!1})},export:function(t,c,a,i){r.exportElecConsumptionNumericalTable(t,c,this.value,a,function(){i()},function(){e.isLoading2=!1})}}]},{value:"告警报表",reportNameList:[{key:201,value:"FSU离线统计表",layout:[[{c:7,r:1}],[{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1}]],algorithm:"",execute:function(t,c,a){r.getFsuOfflineStatisticsTable(t,c,function(e){a(e)},function(){e.isLoading=!1})},export:function(t,c,a,i){r.exportFsuOfflineStatisticsTable(t,c,this.value,a,function(){i()},function(){e.isLoading2=!1})}},{key:202,value:"告警分类统计表",layout:[[{c:14,r:1}],[{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1}]],algorithm:"",execute:function(t,c,a){r.getAlarmClassificationStaticsTable(t,c,function(e){a(e)},function(){e.isLoading=!1})},export:function(t,c,a,i){r.exportAlarmClassificationStaticsTable(t,c,this.value,a,function(){i()},function(){e.isLoading2=!1})}},{key:203,value:"FSU运行统计表",layout:[[{c:7,r:1}],[{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1}]],algorithm:"FSU入网总量 = 交维态站址对应的FSU数量；\n入网FSU在线量 = 在线状态的交维态站址对应的FSU数量；入网FSU离线量 = 离线状态的交维态站址对应的FSU数量；\n入网FSU在线率 = 入网FSU在线量/(入网FSU在线量+入网FSU离线量)*100%；入网FSU离线率 = 入网FSU离线量/(入网FSU在线量+入网FSU离线量)*100%。\n有效的筛选项：FSU厂家",execute:function(t,c,a){r.getAlarmFsuOperation(t,c,function(e){a(e)},function(){e.isLoading=!1})},export:function(t,c,a,i){r.exportAlarmFsuOperation(t,c,this.value,a,function(){i()},function(){e.isLoading2=!1})}},{key:205,value:"FSU离线明细表",layout:[[{c:9,r:1}],[{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1}]],algorithm:"有效的筛选项：局站类型、开始时间",execute:function(t,c,a){r.getFsuOfflineDetailTable(t,c,function(e){a(e)},function(){e.isLoading=!1})},export:function(t,c,a,i){r.exportFsuOfflineDetailTable(t,c,this.value,a,function(){i()},function(){e.isLoading2=!1})}}]}],e.reportType="system"===i.reportType?e.reportTypeList[0]:e.reportTypeList[1],e.query=function(){var t;if(t=n()){var r=t.tierCodes;e.table={thead:[],tbody:[],keyName:[]},e.dataList=[],e.isLoading=!0,e.reportName.execute(e.reportName&&"站点用电负荷统计表"===e.reportName.value?t:r,e.reportQuery,function(t){e.paginationConf.totalItems=t.statList.length,e.paginationConf.currentPage=1,e.paginationConf.itemsPerPage=10,e.paginationConf.totalItems&&(e.table=s(t),o()),e.isLoading=!1})}},e.chooseDefTableOnChangeOption=function(){var t=arguments;setTimeout(function(){t=e[t[0]][t[1]],4===t?("站点停电统计表"===e.reportName.value&&(e.reportTypeList[0].reportNameList[0].layout=[[{c:10,r:1}],[{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1}]]),"告警分类统计表"===e.reportName.value&&(e.reportTypeList[1].reportNameList[1].layout=[[{c:18,r:1}],[{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1}]]),"FSU离线统计表"===e.reportName.value&&(e.reportTypeList[1].reportNameList[0].layout=[[{c:10,r:1}],[{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1}]])):("站点停电统计表"===e.reportName.value&&(e.reportTypeList[0].reportNameList[0].layout=[[{c:7,r:1}],[{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1}]]),"告警分类统计表"===e.reportName.value&&(e.reportTypeList[1].reportNameList[1].layout=[[{c:14,r:1}],[{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1}]]),"FSU离线统计表"===e.reportName.value&&(e.reportTypeList[1].reportNameList[0].layout=[[{c:7,r:1}],[{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1}]])),e.reportTypeList[0].reportNameList[1].layout="UPS"===t?[[{c:21,r:1}],[{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1},{c:1,r:1}]]:[[{c:18,r:1}],[{c:1,r:2},{c:1,r:2},{c:1,r:2},{c:1,r:2},{c:1,r:2},{c:1,r:2},{c:1,r:2},{c:3,r:1},{c:1,r:2},{c:1,r:2},{c:1,r:2},{c:1,r:2},{c:1,r:2},{c:1,r:2},{c:1,r:2},{c:1,r:2}],[{c:1,r:1},{c:1,r:1},{c:1,r:1}]]},10)},e.export=function(t){var r;if(r=n()){var c=r.tierCodes;e.isLoading2=!0,e.reportName.export(e.reportName&&"站点用电负荷统计表"===e.reportName.value?r:c,e.reportQuery,t,function(){e.isLoading2=!1})}},function(){e.target=t.util.getToggleParam("navTreeTarget",[".nav-tree"]),e.other=t.util.getToggleParam("navTreeOther",[".top",".middle",".bottom"])}(),e.customQuery=function(t,r){return(e.curCustomRerpot||r&&r.isCustom)&&(r||e.curCustomRerpot).filters.indexOf(t)>-1},function(){e.treeLoading=!0,c.getTierTree(null,function(t){e.treeLoading=!1,e.zTree=c.createZTree(t),c.initTree(e.zTree.zNodes,"areaTree","checkboxTierTree"),c.checkedAllNodes("areaTree",!0)}),c.getSiteMapTree({},function(t){e.siteTree=c.createZTree(t),c.initTree(e.siteTree.zNodes,"siteTree","checkboxSiteTree"),c.checkedAllNodes("siteTree",!0)})}(),function(){var t=new Date;t.setHours(0,0,0),e.reportQuery.tEnd=a("date")(t,"yyyy/MM/dd HH:mm:ss"),t.setDate(1),e.reportQuery.tStart=a("date")(t,"yyyy/MM/dd HH:mm:ss")}(),function(){r.getCustomReportList({currentPage:1,itemsPerPage:99999},function(t){function c(e,t){var r=e.filters,c=e.tableHeads,a={"站点类型":"stationType","FSU厂家":"fsuManufactory","FSU状态":"fsuState","统计深度":"statisticsDepth","告警状态":"alarmState","统计周期":"dateType","TOP数量":"top"};r=r.map(function(e){return a[e]}),t.tableName=e.value,t.startTime=t.tStart,t.endTime=t.tEnd;var i=t.__get.apply(t,r.concat(["startTime","endTime"]));switch(i.dateType){case 1:i.quarters=t.statisticalCycle.map(function(e){return e.id%100}),delete i.startTime,delete i.endTime;break;case 2:i.months=t.statisticalCycle.map(function(e){return e.id%100}),delete i.startTime,delete i.endTime;break;case 3:i.weeks=t.statisticalCycle.map(function(e){return e.id%100}),delete i.startTime,delete i.endTime}return i.tableHeads=c,4===t.statisticsDepth&&(i.tableHeads=[i.tableHeads[0],"站点名称"].concat(i.tableHeads.slice(1))),4!==t.statisticsDepth||e.columnAppend?4!==t.statisticsDepth&&e.columnAppend&&(e.layout[0][0].c--,e.layout[1].pop(),e.columnAppend=!1):(e.layout[1].push({r:1,c:1}),e.layout[0][0].c++,e.columnAppend=!0),i.tableName=e.value,i}t.list.forEach(function(e){e.isCustom=!0,e.value=e.name}),e.reportTypeList[1].reportNameList=e.reportTypeList[1].reportNameList.concat(t.list),t.list.forEach(function(t){switch(t.statisticsContent){case"实时告警统计":t.execute=function(a,i,o){i=c(t,i),r.getCustomRealtime(a,i,function(e){o(e)},function(){e.isLoading2=!1})},t.export=function(a,i,o,n){i=c(t,i),r.exportCustomRealtime(a,i,t.value,o,function(){n()},function(){e.isLoading2=!1})};break;case"历史告警统计":t.execute=function(a,i,o){i=c(t,i),r.getCustomHistory(a,i,function(e){o(e)},function(){e.isLoading2=!1})},t.export=function(a,i,o,n){i=c(t,i),r.exportCustomHistory(a,i,t.value,o,function(){n()},function(){e.isLoading2=!1})};break;case"TOP告警统计":t.execute=function(a,i,o){i=c(t,i),r.getCustomTopAlarm(a,i,function(e){o(e)},function(){e.isLoading2=!1})},t.export=function(a,i,o,n){i=c(t,i),r.exportCustomTopAlarm(a,i,t.value,o,function(){n()},function(){e.isLoading2=!1})}}t.layout=[[{c:t.tableHeads.length+1,r:1}]].concat([new Array(t.tableHeads.length+1).join(".").split(".").map(function(){return{c:1,r:1}})])})})}()}]);