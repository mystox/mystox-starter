scloudApp.controller("entranceAuthorizeController",["$scope","$rootScope","treeService","entranceService","userService",function(e,t,n,r,o){function a(r){var o=n.getCheckedNode(r);if(!o)return t.notify({type:"warn",text:"请选择一个站点"}),void(e.entranceQuery.siteId=null);e.entranceQuery.siteId=o.id,i()}function i(){e.entranceQuery.siteId&&(e.isLoading=!0,r.getEntranceList(e.paginationConf,e.entranceQuery,function(t){e.isLoading=!1,e.entranceList=t.list,e.paginationConf.totalItems=t.count},function(){e.isLoading=!1}))}$state&&($state=window.$state),$stateParams&&($stateParams=window.$stateParams),e.entranceQuery={},e.checkedEntranceList=[],e.addAuthorize={form:{},obj:{},toModal:null,execute:null,clear:null},e.deleteAuthorize={form:{},obj:{},toModal:null,execute:null,clear:null},e.isLoading=!1,e.paginationConf={currentPage:1,itemsPerPage:15,pagesLength:9,onChange:function(){}},e.$watch("paginationConf.currentPage + paginationConf.itemsPerPage",i),e.searchKey={online:!0,offline:!0,keyword:""},e.getNodesByParamFuzzy=function(){setTimeout(function(){e.zTree.getNodesByParamFuzzy(e.searchKey,"areaTree","radioSiteTree")},10)},e.addAuthorize.toModal=function(){var n=document.querySelectorAll("input[name=checkedEntrance]:checked");if(n.length<1)return void t.notify({type:"warn",text:"请先勾选门禁设备"});for(var r=[],o=0;o<n.length;o++)r.push({name:n[o].title,value:n[o].value});e.addAuthorize.obj.entranceList=r,$("#addAuthorizeModal").modal("show")},e.addAuthorize.execute=function(){var t=[],n=[];e.addAuthorize.obj.entranceList.forEach(function(e){n.push(e.value)}),e.addAuthorize.obj.userList.forEach(function(e){t.push(e.id)}),r.addAuthorize(n,t),e.addAuthorize.clear()},e.addAuthorize.clear=function(){e.addAuthorize.form.$setPristine(),e.addAuthorize.obj={},e.userList=angular.copy(e.fullUserList),$("#addAuthorizeModal").modal("hide")},e.deleteAuthorize.toModal=function(t){e.deleteAuthorize.obj.entranceName=t.name,e.deleteAuthorize.obj.entranceId=t.id,r.getAuthorizedUserList(e.deleteAuthorize.obj.entranceId,function(t){var n=[];t.forEach(function(e){n.push({name:e.name,value:e.strId})}),e.deleteAuthorize.obj.userList=n}),$("#deleteAuthorizeModal").modal("show")},e.deleteAuthorize.execute=function(t){r.deleteAuthorize(e.deleteAuthorize.obj.entranceId,{name:t.name,strId:t.value})},e.deleteAuthorize.clear=function(){e.deleteAuthorize.form.$setPristine(),e.deleteAuthorize.obj={},$("#deleteAuthorizeModal").modal("hide")},function(){e.target=t.util.getToggleParam("navTreeTarget",[".nav-tree"]),e.other=t.util.getToggleParam("navTreeOther",[".top",".bottom"])}(),function(){e.treeLoading=!0,n.getSiteMapTree({},function(t){e.treeLoading=!1,e.zTree=n.createZTree(t),n.initTree(e.zTree.zNodes,"areaTree","radioSiteTree",function(){a("areaTree")}),n.checkNodeBySiteId("areaTree",null)})}(),function(){o.getUserList({currentPage:1,itemsPerPage:t.CONST.maxPageSize},{type:null},function(t){e.fullUserList=t.list;var n=angular.copy(e.fullUserList);n.forEach(function(e){e.value=e.name}),e.userList=n})}()}]);