scloudApp.controller("alarmDelayController",["$scope","$rootScope","$timeout","treeService","alarmRedefineService","alarmDelayService",function(e,t,i,a,n,l){$state&&($state=window.$state),$stateParams&&($stateParams=window.$stateParams),function(){e.treeLoading=!0,a.getSiteMapTree({},function(t){e.treeLoading=!1,e.zTree=a.createZTree(t),a.initTree(e.zTree.zNodes,"siteTree","radioSiteTree",function(){e.getCheckedNodeInSelect("siteTree")}),e.siteList=function e(t){if(!t.nextTier)return t;var i=t.nextTier.map(function(t){return e(t)});return Array.prototype.concat.apply([],i)}(e.zTree.zNodes)})}(),e.getCheckedNodeInSelect=function(t){var i=a.getCheckedNode(t);!i||e.delay.editItem.model.site.filter(function(e){return e.id===i.id}).length>0||(e.delay.editItem.model.site.push(i),e.$apply())},e.delay={list:[],loading:!1,pageConf:{currentPage:1,itemsPerPage:15,pagesLength:9},delayTimeOption:[{value:"5分钟",key:300},{value:"30分钟",key:1800},{value:"60分钟",key:3600}],getList:function(){this.loading=!0,this.list=[];var e=this;l.getAlarmDelayList(this.pageConf,function(t){e.list=t.list,e.pageConf.totalItems=t.count,e.loading=!1})},removeSite:function(t){var i=e.delay.editItem.model.site;i.splice(i.indexOf(t[0]),1),e.delay.editItem.model.remove=[]},editItem:{model:{ruleName:"",pAlarm:[],time:"",site:[]},add:function(){l.addAlarmDelay({delayName:e.delay.editItem.model.ruleName,delayTime:e.delay.editItem.model.time,delayAlarmList:e.delay.editItem.model.pAlarm.map(function(e){return e.id}),siteIdList:e.delay.editItem.model.site.map(function(e){return e.id}),remarks:e.delay.editItem.model.reason},function(i){t.notify({type:"success",text:"添加规则成功。"}),e.delay.getList(),e.delay.editItem.model={ruleName:"",pAlarm:[],time:"",site:[]},e.delay.editItem.cancel()})},toUpdate:function(t){this.updating=!0,this.model={ruleName:t.delayName,time:t.delayTime,pAlarm:t.delayAlarmList.map(function(t){return e.getSignalName(t)}),site:t.siteIdList.map(function(t){return e.getSiteById(t)}),reason:t.remarks,id:t.id}},update:function(){l.modifyAlarmDelay({delayName:this.model.ruleName,delayTime:this.model.time,delayAlarmList:this.model.pAlarm.map(function(e){return e.id}),siteIdList:this.model.site.map(function(e){return e.id}),remarks:this.model.reason,id:this.model.id},function(){t.notify({type:"success",text:"修改规则成功！"}),e.delay.editItem.cancel(),e.delay.getList()})},cancel:function(){this.model={ruleName:"",pAlarm:[],time:"",site:[]},this.updating=!1,this.form.$setPristine()},toggleEnable:function(t){l.toggleAlarmDelayEnableState(t,function(){e.delay.getList()})}},showItem:{},deleteItemModal:{toModal:function(e){$("#deleteRuleModal").modal(),this.item=e},execute:function(){l.deleteAlarmDelay(this.item.id,function(){e.delay.getList(),t.notify({type:"success",text:"删除规则成功。"})}),this.clear()},clear:function(){this.item=null,$("#deleteRuleModal").modal("hide")}},expo:function(){l.exportAlarmDelay(this.pageConf,function(e){console.log(e)})}},e.signalList={data:[]},e.getSignalName=function(t){return e.signalList.data.filter(function(e){return e.id===t})[0]||{text:"无信号"}},e.getSiteById=function(t){return e.siteList.filter(function(e){return e.id===t})[0]||{}},function(){n.getDISignalTypeList(function(t){e.signalList.data=t.map(function(e){return{id:e.strId,text:e.name}})}),e.$watch("delay.pageConf.currentPage + delay.pageConf.itemsPerPage",e.delay.getList())}()}]);