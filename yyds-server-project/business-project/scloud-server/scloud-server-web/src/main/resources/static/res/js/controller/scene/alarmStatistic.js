scloudApp.controller("alarmStatisticController",["$scope","$rootScope","$stateParams","$filter","treeService","deviceService","configurationService",function(e,t,a,i,r,n,s){function o(){s.getRoomHistoryAlarmList({siteId:a.siteId||params.siteId,alarmLevel:e.query.alarmLevel,startTime:new Date(e.query.tStart).getTime(),endTime:new Date(e.query.tEnd).getTime(),deviceTypeCode:e.query.deviceTypeCode,currentPage:e.paginationConf.currentPage,pageSize:e.paginationConf.itemsPerPage},function(t){e.roomHistoryAlarmList=t.list,e.paginationConf.totalItems=t.count})}if($state&&($state=window.$state),a&&(a=window.$stateParams),params=t.util.getParamsFromUrl(),e.params=params,e.paginationConf={currentPage:1,itemsPerPage:15,pagesLength:5,onChange:function(){}},a.siteId&&a.alarmStatisticCycle){var m=(new Date).getTime(),d=(new Date).getTime()-24*a.alarmStatisticCycle*3600*1e3;params.siteId=a.siteId,params.tEnd=m,params.tStart=d,location.hash.indexOf("siteId")<0&&(location.hash="#/scene/alarmStatistic?siteId="+a.siteId+"&tEnd="+m+"&tStart="+d)}e.query={siteId:a.siteId,alarmLevel:null,deviceTypeCode:null,tEnd:new Date(parseInt(params.tEnd)).Format("yyyy/MM/dd hh:mm:ss"),tStart:new Date(parseInt(params.tStart)).Format("yyyy/MM/dd hh:mm:ss")},e.alarmLevelList=[{name:"一级告警",value:"1"},{name:"二级告警",value:"2"},{name:"三级告警",value:"3"},{name:"四级告警",value:"4"}],e.isLoading=!1,e.$watch("paginationConf.currentPage + paginationConf.itemsPerPage",o),e.toQuery=function(){o()},function(){n.getDeviceTypeList(null,function(t){e.deviceTypeList=t})}(),e.exportRoomHistoryAlarmList=function(){s.exportRoomHistoryAlarmList({siteId:a.siteId||params.siteId,alarmLevel:e.query.alarmLevel,startTime:new Date(e.query.tStart).getTime(),endTime:new Date(e.query.tEnd).getTime(),deviceTypeCode:e.query.deviceTypeCode},function(){})},e.clear=function(){e.query={siteId:a.siteId,alarmLevel:null,deviceTypeCode:null,tEnd:new Date(parseInt(params.tEnd)).Format("yyyy/MM/dd hh:mm:ss"),tStart:new Date(parseInt(params.tStart)).Format("yyyy/MM/dd hh:mm:ss")}}}]);