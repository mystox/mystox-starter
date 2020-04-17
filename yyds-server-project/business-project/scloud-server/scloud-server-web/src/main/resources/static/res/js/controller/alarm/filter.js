scloudApp.controller("alarmFilterController",["$scope","$rootScope","$filter","$timeout","treeService","deviceService","alarmFilterService","fileUploadService","alarmService",function(e,t,a,i,r,l,n,o,d){function u(){e.isLoading=!0,n.getRuleList(e.paginationConf,function(t){e.isLoading=!1,e.ruleList=t.list,e.paginationConf.totalItems=t.count},function(){e.isLoading=!1})}function s(){for(var t=[],a=e.updateRule.obj.selectedAreaList,i=0;i<a.length;i++)t[i]=a[i].key;return t}function c(){for(var t=[],a=e.updateRule.obj.alarmLevelList,i=0;i<a.length;i++)t.push(a[i].id);return t}function m(){for(var t=[],a=e.updateRule.obj.deviceTypes,i=0;i<a.length;i++)t.push(a[i].id);return t}function p(e,t){for(var a=0;a<t.length;a++)if(e===t[a].id)return t[a]}$state&&($state=window.$state),$stateParams&&($stateParams=window.$stateParams),e.UPDATE_MODE="ADD",e.alarmLevelListForSelect2={data:[{id:1,text:"一级告警"},{id:2,text:"二级告警"},{id:3,text:"三级告警"},{id:4,text:"四级告警"}]},e.updateRule={form:{},obj:{baseSite:!0,selectedAreaList:[],areaCodes:[],alarmLevelList:[],deviceTypes:[]},clearArea:function(){this.obj.selectedAreaList=[],this.obj.areaCodes=[]},toModal:null,execute:null,clear:null},e.modifyRule={form:{},obj:{},toModal:null,execute:null,clear:null},e.deleteRule={form:{},obj:{},toModal:null,execute:null,clear:null},e.importAlarmFilter={form:{},obj:{},toModal:null,execute:null,select:null,clear:null,loading:!1},e.isLoading=!1,e.paginationConf={currentPage:1,itemsPerPage:15,pagesLength:9,onChange:function(){}},e.$watch("paginationConf.currentPage + paginationConf.itemsPerPage",u),e.openDetailTab=function(t){e.openDetailIndex===t?e.openDetailIndex=null:e.openDetailIndex=t},e.updateRule.toModal=function(t,i){i.stopPropagation(),e.UPDATE_MODE="MODIFY",e.updateRule.obj={id:t.id,name:t.name,baseSite:t.baseSite,selectedAreaList:[],areaCodes:[],startBeginTime:t.startBeginTime&&a("date")(new Date(t.startBeginTime),"yyyy/MM/dd HH:mm:ss"),startEndTime:t.startEndTime&&a("date")(new Date(t.startEndTime),"yyyy/MM/dd HH:mm:ss"),clearBeginTime:t.clearBeginTime&&a("date")(new Date(t.clearBeginTime),"yyyy/MM/dd HH:mm:ss"),clearEndTime:t.clearEndTime&&a("date")(new Date(t.clearEndTime),"yyyy/MM/dd HH:mm:ss"),alarmLevelList:[],deviceTypes:[],alarmName:t.alarmName,remarks:t.remarks};var l=$.fn.zTree.getZTreeObj("areaTree").getNodesByParam("level",4),n=$.fn.zTree.getZTreeObj("areaTree").getNodesByParam("level",3),o=t.baseSite?t.siteCodeList:t.tierCodeList;o&&o.forEach(function(a){var i=void 0;(i=t.baseSite?l.find(function(e){return e.code===a}):n.find(function(e){return e.fullCode===a}))&&e.updateRule.obj.selectedAreaList.push({key:a,value:t.baseSite?i.name:r.getParentName(i,[])})});var d=t.alarmLevelList;d&&d.forEach(function(t){e.updateRule.obj.alarmLevelList.push(p(t,e.alarmLevelListForSelect2.data))});var u=t.deviceTypeList;u&&u.forEach(function(t){e.updateRule.obj.deviceTypes.push(p(t,e.deviceTypeList.data))})},e.updateRule.execute=function(){var t=e.updateRule.obj.__get("id","name","alarmName","startBeginTime","startEndTime","clearBeginTime","clearEndTime","remarks");t.startBeginTime&&(t.startBeginTime=new Date(t.startBeginTime).getTime()),t.startEndTime&&(t.startEndTime=new Date(t.startEndTime).getTime()),t.clearBeginTime&&(t.clearBeginTime=new Date(t.clearBeginTime).getTime()),t.clearEndTime&&(t.clearEndTime=new Date(t.clearEndTime).getTime()),t.enterpriseCode=e.user.enterpriseCode,t.serverCode=e.user.serverCode,t.baseSite=e.updateRule.obj.baseSite,t.baseSite?t.siteCodeList=s():t.tierCodeList=s(),t.alarmLevelList=c(),t.deviceTypeList=m(),"ADD"===e.UPDATE_MODE?n.addRule(t,function(){e.updateRule.clear(),u()}):"MODIFY"===e.UPDATE_MODE&&n.modifyRule(t,function(){e.updateRule.clear(),u()})},e.updateRule.clear=function(){e.updateRule.form.$setPristine(),e.updateRule.obj={baseSite:!0,selectedAreaList:[],areaCodes:[],alarmLevelList:[],deviceTypes:[]},e.UPDATE_MODE="ADD"},e.baseSite="0",e.changeSiteBased=function(t){e.updateRule.obj.baseSite="0"===t,e.updateRule.obj.selectedAreaList=[]},e.getNodesByParamFuzzy=function(t){e.updateRule.obj.baseSite?e.zTree.getNodesByParamFuzzy(t,"areaTree","radioSiteTree",function(){e.getCheckedNodeInSelect("areaTree")}):e.zTree2.getNodesByParamFuzzy(t,"areaTree2","radioTierTree",function(){e.getCheckedNodeInSelect2("areaTree2")})},e.getCheckedNodeInSelect=function(t){var a=r.getCheckedNode(t);a&&(e.multiNodeSelect.add({key:a.code,value:a.name},e.updateRule.obj.selectedAreaList),e.$apply())},e.getCheckedNodeInSelect2=function(t){var a=r.getCheckedNode(t);a&&(e.multiNodeSelect.add({key:r.wrapZero(r.getParentCode(a),6),value:r.getParentName(a,[])},e.updateRule.obj.selectedAreaList),e.$apply())},e.multiNodeSelect={add:function(e,t){!1===t.containsObj(e)&&(t[t.length]=e)},remove:function(e,t){!e||e.length<1||t.removeObj(e[0])}},e.deleteRule.toModal=function(t,a){a.stopPropagation(),e.deleteRule.obj=t,$("#deleteRuleModal").modal("show")},e.deleteRule.execute=function(){$("#deleteRuleModal").modal("hide"),n.deleteRule({id:e.deleteRule.obj.id},function(){u(),e.deleteRule.clear()})},e.deleteRule.clear=function(){e.deleteRule.obj={},$("#deleteRuleModal").modal("hide")},e.importAlarmFilter.toModal=function(){$("#importAlarmFilterModal").modal("show"),e.fileUpload=o.getFileUpload()},e.importAlarmFilter.select=function(){e.fileUpload.selectFile("fileUploadBtn")},e.importAlarmFilter.execute=function(){e.importAlarmFilter.loading=!0,e.fileUpload.upload("/alarmFilterFunc/importAlarmFilter",function(a){if(e.importAlarmFilter.loading=!1,e.importAlarmFilter.clear(),!1===a.success)return void t.notify({type:"error",text:"导入失败<br/>"+a.info});t.notify({type:"success",text:"导入成功"}),u()},function(){e.importAlarmFilter.loading=!1,e.importAlarmFilter.clear(),console.log("请求失败")})},e.importAlarmFilter.clear=function(){$("#importAlarmFilterModal").modal("hide"),e.fileUpload.files=null,e.fileUpload.info={id:null,name:null,url:null,size:null,progress:null}},e.useFilter=function(e,t){t.stopPropagation(),n.useFilter({id:e,state:!0},function(){u()})},e.unuseFilter=function(e,t){t.stopPropagation(),n.unuseFilter({id:e,state:!1},function(){u()})},e.exportAlarmFilter=function(){n.exportAlarmFilter()},function(){e.treeLoading=!0,e.treeLoading2=!0,r.getSiteMapTree({serverCode:e.user.serverCode},function(t){e.treeLoading=!1,e.zTree=r.createZTree(t),r.initTree(e.zTree.zNodes,"areaTree","radioSiteTree",function(){e.getCheckedNodeInSelect("areaTree")}),e.treeLoading2=!1,e.zTree2=r.createZTree(r.getTierTreeBySiteTree(t)),r.initTree(e.zTree2.zNodes,"areaTree2","radioTierTree",function(){e.getCheckedNodeInSelect2("areaTree2")})})}(),function(){l.getDeviceTypeList(null,function(t){for(var a=[],i=[],r=0;r<t.length;r++)a.push({id:t[r].code,text:t[r].typeName}),i[t[r].code]=t[r].typeName;e.deviceTypeList={data:a},e.deviceTypeMap=i})}(),function(){d.getAlarmLevelList({pageSize:9999,curPage:1,enterpriseCode:e.user.enterpriseCode,serverCode:e.user.serverCode},function(t){var a=t.list.find(function(e){return"启用"===e.state});e.alarmLevelList=a?{data:a.levelNames.map(function(e,t){return{id:a.levels[t],text:e+"("+a.levels[t]+")"}})}:[]})}()}]);