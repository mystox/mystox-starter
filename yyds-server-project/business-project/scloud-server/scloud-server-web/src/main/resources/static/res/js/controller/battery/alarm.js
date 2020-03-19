scloudApp.controller("batteryAlarmController",["$scope","$rootScope","$stateParams","$filter","treeService","batteryService","alarmService",function(e,t,a,r,n,i,o){function l(a){var r=n.getCheckedNode(a);if(!r)return void t.notify({type:"warn",text:"请选择一个站点"});e.currentSite=r,c()}function c(){if(e.isSelectAll=!1,!e.currentSite)return void(e.isLoading=!1);if(!e.alarmQuery.tStart||!e.alarmQuery.tEnd){var t=new Date;t.setDate(t.getDate()+1),e.alarmQuery.tEnd=r("date")(t,"yyyy/MM/dd 00:00:00"),t.setDate(t.getDate()-30),e.alarmQuery.tStart=r("date")(t,"yyyy/MM/dd 00:00:00")}e.isLoading=!0,i.getAlarmList(e.currentSite.id,e.paginationConf,e.alarmQuery,function(t){e.isLoading=!1,e.paginationConf.totalItems=t.count,e.currentAlarmList=t.list})}$state&&($state=window.$state),a&&(a=window.$stateParams),e.CURRENT_ALARM=t.CONST.alarmStateList[0].key,e.HISTORY_ALARM=t.CONST.alarmStateList[1].key,e.alarmQuery={alarmLevel:a.alarmLevel,alarmState:e.CURRENT_ALARM},e.isLoading=!1,e.paginationConf={currentPage:1,itemsPerPage:15,pagesLength:9},e.$watch("paginationConf.currentPage + paginationConf.itemsPerPage",c),e.searchKey={online:!0,offline:!0,keyword:""},e.getNodesByParamFuzzy=function(){setTimeout(function(){e.zTree.getNodesByParamFuzzy(e.searchKey,"areaTree","radioSiteTree")},10)},e.getNodesByParamFuzzy=function(t,a,r){e.zTree.getNodesByParamFuzzy(t,a,r,function(){l("areaTree")})},e.query=function(){c()},e.reset=function(){e.alarmQuery={alarmState:e.CURRENT_ALARM},e.query()},e.checkAlarm=function(e,t){t.stopPropagation(),i.checkAlarm(e.id,function(){c()})},e.eliminateAlarm={form:{},obj:{},toModal:null,execute:null,clear:null},e.eliminateAlarm.toModal=function(t,a){e.currentEliminateAlarm=t,$("#eliminateAlarmModal").modal("show"),a.stopPropagation()},e.eliminateAlarm.execute=function(){e.eliminateAlarm.clear(),o.eliminateAlarm(e.currentEliminateAlarm.alarm.id,function(){c()})},e.eliminateAlarm.clear=function(){$("#eliminateAlarmModal").modal("hide")},e.selectAll=function(t){e.isSelectAll=t;for(var a=0;a<e.currentAlarmList.length;a++)e.currentAlarmList[a].isSelected=t},e.batchOperation={form:{},obj:{},type:"",title:"",toModal:null,execute:null,clear:null},e.batchOperation.toModal=function(a){for(var r=[],n=0;n<e.currentAlarmList.length;n++)e.currentAlarmList[n].isSelected&&r.push(e.currentAlarmList[n].alarm.id);if(r.length<1)return void t.notify({type:"warn",text:"请先勾选告警"});e.batchOperation.obj.alarmIdList=r,"check"===a?e.batchOperation.title="确认":"clear"===a&&(e.batchOperation.title="消除"),e.batchOperation.type=a,$("#batchOperationModal").modal("show")},e.batchOperation.execute=function(){i.updateAlarmInBatch(e.batchOperation.obj.alarmIdList,e.batchOperation.type,function(){c()}),e.batchOperation.clear()},e.batchOperation.clear=function(){$("#batchOperationModal").modal("hide")},function(){e.target=t.util.getToggleParam("navTreeTarget",[".nav-tree"]),e.other=t.util.getToggleParam("navTreeOther",[".top",".bottom"])}(),function(){i.getBatteryQueryList(function(t){e.powerList=t.powerList})}(),function(){e.treeLoading=!0,n.getSiteMapTree({},function(t){e.treeLoading=!1,e.zTree=n.createZTree(t),n.initTree(e.zTree.zNodes,"areaTree","radioSiteTree",function(){l("areaTree")}),n.checkNodeBySiteId("areaTree",null)})}()}]);