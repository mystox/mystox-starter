scloudApp.controller("entranceLogController",["$scope","$rootScope","$filter","logService",function(t,e,o,n){function a(){if(!t.logQuery.tStart||!t.logQuery.tEnd){var e=new Date;e.setDate(e.getDate()+1),t.logQuery.tEnd=o("date")(e,"yyyy/MM/dd 00:00:00"),e.setDate(e.getDate()-30),t.logQuery.tStart=o("date")(e,"yyyy/MM/dd 00:00:00")}t.isLoading=!0,n.getLoggerList(t.paginationConf,t.logQuery,function(e){t.isLoading=!1,t.logList=e.list,t.paginationConf.totalItems=e.count},function(){t.isLoading=!1})}$state&&($state=window.$state),$stateParams&&($stateParams=window.$stateParams),t.logQuery={logType:e.CONST.logType[2]},t.isLoading=!1,t.paginationConf={currentPage:1,itemsPerPage:15,pagesLength:9,onChange:function(){}},t.$watch("paginationConf.currentPage + paginationConf.itemsPerPage",a),t.query=function(){a()},t.reset=function(){t.logQuery={logType:e.CONST.logType[2]}}}]);