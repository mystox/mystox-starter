scloudApp.controller("configFsuController",["$scope","$rootScope","$stateParams","treeService","fsuService","deviceService",function(e,o,t,d,i,n){function s(){null!==e.fsuQuery.siteIds&&0!==e.fsuQuery.siteIds.length&&(e.isLoading=!0,i.getFsuList(e.paginationConf,e.fsuQuery,function(o){e.isLoading=!1,e.paginationConf.totalItems=o.count,e.fsuList=o.list,e.fsuList&&e.fsuList.length>0?e.currentFsuId=e.fsuList[0].id:e.currentFsuId=null,u()},function(){e.isLoading=!1}))}function u(){if(!e.currentFsuId)return e.deviceList=null,void(e.paginationConf2.totalItems=0);e.deviceList=null,e.isLoading2=!0,n.getDeviceListOnFsu(e.paginationConf2,e.currentFsuId,function(o){e.isLoading2=!1,e.deviceList=o.list,e.paginationConf2.totalItems=o.count},function(){e.isLoading2=!1})}function a(t){var i=d.getCheckedNode(t);if(!i)return void o.notify({type:"warn",text:"请选择一个站点"});e.addFsu.obj.siteId=i.id,e.addFsu.obj.siteName=i.name,e.createFsuCode(e.addFsu.obj.siteId),e.toggleNavTree()}function c(){n.getSystemNameList(null,function(o){e.systemList=o})}function r(e){for(e=e.substring(e.length-4);e&&"0"===e[0];)e=e.substring(1);return"1"===e&&(e=""),e}$state&&($state=window.$state),t&&(t=window.$stateParams),e.fsuQuery={siteIds:null,state:t.state},"liheng"===e.enterprise.uniqueCode?e.fsuModelList=["ePLC-FS"]:e.fsuModelList=["ePLC-FS","ePLC-FH"],e.currentFsuId=null,e.currentModifyFsuObj={},e.url={add:null,modify:null},e.addFsu={form:{},obj:{},toModal:null,execute:null,clear:null},e.modifyFsu={form:{},obj:{},toModal:null,execute:null,clear:null},e.addDeviceOnFsu={form:{},obj:{},toModal:null,execute:null,clear:null,style:!1},e.deleteFsu={form:{},obj:{},toModal:null,execute:null,clear:null},e.modifyDevice={form:{},obj:{},toModal:null,execute:null,clear:null,style:!1},e.deleteDevice={form:{},obj:{},toModal:null,execute:null,clear:null},e.isLoading=!1,e.isLoading2=!1,e.paginationConf={currentPage:1,itemsPerPage:15,pagesLength:9,onChange:function(){}},e.$watch("paginationConf.currentPage + paginationConf.itemsPerPage",s),e.paginationConf2={currentPage:1,itemsPerPage:15,pagesLength:9,onChange:function(){}},e.$watch("paginationConf2.currentPage + paginationConf2.itemsPerPage",u),e.searchKey={online:!0,offline:!0,keyword:""},e.getNodesByParamFuzzy=function(){setTimeout(function(){e.zTree.getNodesByParamFuzzy(e.searchKey,"areaTree","checkboxSiteTree")},10)},e.getNodesByParamFuzzy2=function(o,t,d){e.zTree2.getNodesByParamFuzzy(o,t,d,function(){a("areaTree2")})},e.query=function(){for(var o=d.getCheckedNodes("areaTree"),t=[],i=0;i<o.length;i++)o[i].id&&t.push(o[i].id);e.fsuQuery.siteIds=t,e.deviceList=null,s()},e.reset=function(){e.fsuQuery={siteIds:e.fsuQuery.siteIds}},e.getDevicesInFsu=function(o){e.currentFsuId=o,u()},e.addFsu.toModal=function(){e.addFsu.obj.model=o.CONST.fsuModelList[0],e.addFsu.obj.manufactory=o.CONST.fsuManufactoryList[0],$("#addFsuModal").modal("show")},e.addFsu.execute=function(){$("#addFsuModal").modal("hide"),i.addFsu(e.addFsu.obj,function(){e.addFsu.clear(),s(),c()})},e.addFsu.clear=function(){e.addFsu.form.$setPristine(),e.addFsu.obj={},$("#addFsuModal").modal("hide")},e.modifyFsu.toModal=function(o,t){$("#modifyFsuModal").modal("show"),t.stopPropagation(),e.currentModifyFsuObj=angular.copy(o),e.modifyFsu.obj.siteName=o.siteName,e.modifyFsu.obj.name=o.name,e.modifyFsu.obj.code=o.code,e.modifyFsu.obj.shortCode=o.shortCode,e.modifyFsu.obj.systemName=o.systemName,e.modifyFsu.obj.model=o.model,e.modifyFsu.obj.respName=o.respName,e.modifyFsu.obj.manufactory=o.manufactory},e.modifyFsu.execute=function(){var t=e.currentModifyFsuObj;t.name=e.modifyFsu.obj.name,t.respName=e.modifyFsu.obj.respName,t.systemName=e.modifyFsu.obj.systemName,t.manufactory=o.util.strNull(e.modifyFsu.obj.manufactory),$("#modifyFsuModal").modal("hide"),i.modifyFsu(t,function(){s(),c()})},e.modifyFsu.clear=function(){e.modifyFsu.form.$setPristine(),$("#modifyFsuModal").modal("hide")},e.addDeviceOnFsu.toModal=function(o,t){e.currentFsuId=o.id,e.addDeviceOnFsu.obj.fsuId=o.id,e.addDeviceOnFsu.obj.siteId=o.siteId,e.addDeviceOnFsu.obj.fsuName=o.name,$("#addDeviceOnFsuModal").modal("show")},e.addDeviceOnFsu.execute=function(){$("#addDeviceOnFsuModal").modal("hide"),n.addDeviceOnFsu(e.url.add,{fsuId:e.addDeviceOnFsu.obj.fsuId,siteId:e.addDeviceOnFsu.obj.siteId,name:e.addDeviceOnFsu.obj.name,type:e.addDeviceOnFsu.obj.type.typeName,typeCode:e.addDeviceOnFsu.obj.type.code,code:e.addDeviceOnFsu.obj.code.code,shortCode:e.addDeviceOnFsu.obj.code.shortCode,systemName:e.addDeviceOnFsu.obj.systemName,manufactory:e.addDeviceOnFsu.obj.manufactory,model:e.addDeviceOnFsu.obj.model,ip:e.addDeviceOnFsu.obj.ip,port:e.addDeviceOnFsu.obj.port,rtspPort:e.addDeviceOnFsu.obj.rtspPort,username:e.addDeviceOnFsu.obj.username,password:e.addDeviceOnFsu.obj.password},function(){c(),u(),e.addDeviceOnFsu.clear()})},e.addDeviceOnFsu.clear=function(){e.addDeviceOnFsu.form.$setPristine(),e.addDeviceOnFsu.obj={},$("#addDeviceOnFsuModal").modal("hide")},e.deleteFsu.toModal=function(o,t){t.stopPropagation(),$("#deleteFsuModal").modal("show"),e.deleteFsu.obj=o},e.deleteFsu.execute=function(){$("#deleteFsuModal").modal("hide"),i.deleteFsu(e.deleteFsu.obj.id,function(){s(),e.deleteFsu.clear()})},e.deleteFsu.clear=function(){e.deleteFsu.form.$setPristine(),$("#deleteFsuModal").modal("hide")},e.createFsuCode=function(o){o&&i.createFsuCode(o,function(o){e.addFsu.obj.code=o,e.addFsu.obj.name=e.addFsu.obj.siteName+"FSU"+r(o.code)})},e.createDeviceCode=function(){var o=e.addDeviceOnFsu.obj;n.createDeviceCode(o,function(t){o.code=t,e.getAddingUrl(o.type.code),e.addDeviceOnFsu.obj.name=o.type.typeName+r(t.code)})},e.modifyDevice.toModal=function(o){n.getDeviceInfo(o.id,function(t){$("#modifyDeviceModal").modal("show"),e.modifyDevice.obj={id:o.id,name:o.name,type:{code:o.typeCode,typeName:o.type},code:o.code,shortCode:o.shortCode,systemName:o.systemName,model:o.model,manufactory:o.manufactory,ip:t.ip,port:t.port,rtspPort:t.rtspPort,username:t.username,password:t.password},e.getModifyingUrl(o.typeCode)})},e.modifyDevice.execute=function(){$("#modifyDeviceModal").modal("hide"),n.modifyDevice(e.url.modify,e.modifyDevice.obj,function(){e.modifyDevice.clear(),u()})},e.modifyDevice.clear=function(){e.modifyDevice.form.$setPristine(),e.modifyDevice.obj={},$("#modifyDeviceModal").modal("hide")},e.deleteDevice.toModal=function(o){$("#deleteDeviceModal").modal("show"),e.deleteDevice.obj=o},e.deleteDevice.execute=function(){$("#deleteDeviceModal").modal("hide"),n.deleteDevice(e.deleteDevice.obj.id,function(){u(),e.deleteDevice.clear()})},e.deleteDevice.clear=function(){e.deleteDevice.form.$setPristine(),$("#deleteDeviceModal").modal("hide")},e.getAddingUrl=function(o){n.getAddingUrl(o,function(o){e.url.add=o})},e.getModifyingUrl=function(o){n.getModifyingUrl(o,function(o){e.url.modify=o})},e.exportFsuList=function(){for(var o=d.getCheckedNodes("areaTree"),t=[],n=0;n<o.length;n++)o[n].id&&t.push(o[n].id);e.fsuQuery.siteIds=t,e.exportFsuList.have=!1,i.exportFsuList(e.fsuQuery)},e.exportFsuList.have=!0,e.exportFsuListPdf=function(){for(var o=d.getCheckedNodes("areaTree"),t=[],n=0;n<o.length;n++)o[n].id&&t.push(o[n].id);e.fsuQuery.siteIds=t,e.exportFsuListPdf.have=!1,i.exportFsuListPdf(e.fsuQuery)},e.exportFsuListPdf.have=!0,e.exportDeviceList=function(){e.exportDeviceList.have=!1,n.exportDeviceList(e.currentFsuId)},e.exportDeviceList.have=!0,e.exportDeviceListPdf=function(){e.exportDeviceListPdf.have=!1,n.exportDeviceListPdf(e.currentFsuId)},e.exportDeviceListPdf.have=!0,function(){e.target2=o.util.getToggleParam(null,[".nav-tree-sm"],{left:"0"},{left:"-232px"})}(),function(){n.getSpecialDeviceTypeCode(function(o){e.specialDevice=o})}(),function(){n.getDeviceTypeList(null,function(o){for(var t=[],d=0;d<o.length;d++)t.push({code:o[d].code,typeName:o[d].typeName});e.deviceTypeList=t;var i=e.deviceTypeList.map(function(e){return e.code}).indexOf("038");i>-1&&e.deviceTypeList.splice(i,1)})}(),c(),function(){e.treeLoading=!0,d.getSiteMapTree({},function(o){e.treeLoading=!1,e.zTree=d.createZTree(o),e.zTree2=d.createZTree(angular.copy(o)),d.initTree(e.zTree.zNodes,"areaTree","checkboxSiteTree"),d.initTree(e.zTree2.zNodes,"areaTree2","radioSiteTree",function(){a("areaTree2")}),d.checkedAllNodes("areaTree",!0),e.query()})}()}]);