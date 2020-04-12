scloudApp.controller("entranceListController",["$scope","$rootScope","treeService","entranceService","signalService",function(e,n,t,i,a){function r(i){var a=t.getCheckedNode(i);if(!a)return n.notify({type:"warn",text:"请选择一个站点"}),void(e.entranceQuery.siteId=null);e.entranceQuery.siteId=a.id,o()}function o(){e.entranceQuery.siteId&&(e.isLoading=!0,i.getEntranceList(e.paginationConf,e.entranceQuery,function(n){e.isLoading=!1,e.entranceList=n.list,e.paginationConf.totalItems=n.count,e.entranceList.length>0&&e.getSignalList(e.entranceList[0].entrance.id)},function(){e.isLoading=!1}))}function c(n){i.checkAuthorize(n,function(n){e.hasAuthorize=!!n.success})}$state&&($state=window.$state),$stateParams&&($stateParams=window.$stateParams),e.entranceQuery={},e.isLoading=!1,e.paginationConf={currentPage:1,itemsPerPage:15,pagesLength:9,onChange:function(){}},e.$watch("paginationConf.currentPage + paginationConf.itemsPerPage",o),e.isLoading2=!1,e.searchKey={online:!0,offline:!0,keyword:""},e.getNodesByParamFuzzy=function(){setTimeout(function(){e.zTree.getNodesByParamFuzzy(e.searchKey,"areaTree","radioSiteTree")},10)},e.getSignalList=function(n){e.currentSelectedEntranceId=n,e.isLoading2=!0,a.getSignalList(n,{},function(n){e.isLoading2=!1,e.signalList=n.tableList,e.signalList.length>0&&e.changeCurrentSelectedSignal(e.signalList[0].signal)},function(){e.isLoading2=!1}),c(n)},e.changeCurrentSelectedSignal=function(n){e.currentSelectedSignal=n},e.modifyTeleControl=function(e){a.modifyAdjustValue(e)},function(){e.target=n.util.getToggleParam("navTreeTarget",[".nav-tree"]),e.other=n.util.getToggleParam("navTreeOther",[".top",".left"])}(),function(){e.treeLoading=!0,t.getSiteMapTree({},function(n){e.treeLoading=!1,e.zTree=t.createZTree(n),t.initTree(e.zTree.zNodes,"areaTree","radioSiteTree",function(){r("areaTree")}),t.checkNodeBySiteId("areaTree",null)})}()}]);