scloudApp.controller("alarmCountController",["$scope","$rootScope","treeService","alarmService","reportService","$stateParams","$state",function(e,t,r,a,i,n,u){u&&(u=window.$state),n&&(n=window.$stateParams),e.tabPage=t.util.tabPage.getInstance(["实时告警","告警分布","告警频发站"]),e.tabPage.currentIndex=0,e.$stateParams=n,e.realtimeQuery={},e.tierDistQuery={},e.frequentQuery={},e.realtimePagiConf={currentPage:1,itemsPerPage:15,pagesLength:9},e.isRealtimeLoading=!1,e.resetRealtimeQuery=function(){e.realtimeQuery={tEnd:(new Date).Format("yyyy/MM/dd"),tStart:new Date((new Date).getTime()-Date.oneWeek).Format("yyyy/MM/dd")}},e.resetRealtimeQuery(),e.realtimeQueryChange=function(t){var r=new Date(e.realtimeQuery.tEnd),a=new Date(e.realtimeQuery.tStart);t&&r.getTime()?e.realtimeQuery.tStart=new Date(r.getTime()-Date.oneWeek).Format("yyyy/MM/dd"):!t&&a.getTime()&&(e.realtimeQuery.tEnd=new Date(a.getTime()+Date.oneWeek).Format("yyyy/MM/dd"))},e.getRealtimeAlarmChartData=function(){var t={startBeginTime:e.realtimeQuery.tStart?new Date(e.realtimeQuery.tStart).getTime():null,startEndTime:e.realtimeQuery.tEnd?new Date(e.realtimeQuery.tEnd).getTime():null,tier:n.tier,enterpriseCode:e.user.enterpriseCode,serverCode:e.user.serverCode};e.realtimeQueryLevel=n.realtimeParams?n.realtimeParams+"":null,n.realtimeParams=null,i.getAlarmCountByTimeRange(t,function(t){var r={};t.forEach(function(e){r[e.name]?(r[e.name].intPro.push(e.intPro),r[e.name].count+=e.count):(r[e.name]=e,e.intPro=[e.intPro])}),t=[];for(var a in r)t.push(r[a]);e.totalRealtimeAlarmCount=t.reduce(function(e,t){return e+t.count},0);try{chart.alarmCountPie({id:"realTimePie",name:"",title:" ",data:t.map(function(e){return{value:e.count,name:e.name}})}).on("click",function(r){var a=t.find(function(e){return e.name===r.name});e.realtimeQueryLevel=a.intPro,e.realtimeQueryLevelName=a.name,e.getRealtimeALarmList(e.realtimeQueryLevel)})}catch(e){}e.realtimeQueryLevel=t.reduce(function(e,t){return e.concat(t.intPro)},[]),n.alarmLevel?(e.realtimeQueryLevel=n.alarmLevel,e.realtimeQueryLevelName=(t.find(function(e){return e.intPro.sort(function(e,t){return e-t}).join("")===n.alarmLevel.sort(function(e,t){return e-t}).join("")})||{name:"全部"}).name,n.alarmLevel=null):e.realtimeQueryLevelName="全部",e.getRealtimeALarmList()})},e._firstGetRealtimeALarmList=!0,e.$watch("realtimePagiConf.currentPage + realtimePagiConf.itemsPerPage",function(){e.getRealtimeALarmList(e.realtimeQueryLevel)}),e.getRealtimeALarmList=function(t){if(e._firstGetRealtimeALarmList)return void(e._firstGetRealtimeALarmList=!1);e.isRealtimeLoading=!0;var r={tierCode:n.tier,enterpriseCode:e.user.enterpriseCode,serverCode:e.user.serverCode,startBeginTime:e.realtimeQuery.tStart?new Date(e.realtimeQuery.tStart).getTime():null,startEndTime:e.realtimeQuery.tEnd?new Date(e.realtimeQuery.tEnd).getTime()+Date.oneDay-1:null,type:"实时告警",levelList:e.realtimeQueryLevel};a.getCountRealtimeAlarmList(r,function(t){$(".page1 .table-box").scrollTop(0),e.realtimeAlarmList=t.list,e.realtimePagiConf.totalItems=t.count,e.isRealtimeLoading=!1})},e.exportRealtimeAlarm=function(){e.exportRealtimeAlarm.have=!1,a.exportAlarmCountTab1({tierCode:n.tier,enterpriseCode:e.user.enterpriseCode,serverCode:e.user.serverCode,startBeginTime:e.realtimeQuery.tStart?new Date(e.realtimeQuery.tStart).getTime():null,startEndTime:e.realtimeQuery.tEnd?new Date(e.realtimeQuery.tEnd).getTime()+Date.oneDay-1:null,type:"实时告警",levelList:e.realtimeQueryLevel},function(){})},e.exportRealtimeAlarm.have=!0,e.resetTierDistQuery=function(){e.tierDistQuery={}},e.getTierDistAll=function(){e.tierDistQuery.tEnd||e.tierDistQuery.tStart||(e.tierDistQuery={tEnd:(new Date).Format("yyyy/MM/dd hh:mm:ss"),tStart:new Date((new Date).getTime()-Date.oneWeek).Format("yyyy/MM/dd hh:mm:ss")}),e.getTierDist(1)},e.getTierDist=function(t,r,i){function o(r){var a=new Array(8).join(".").split(".").map(function(e,t){return r[t]?r[t].name:"-"}),o=new Array(8).join(".").split(".").map(function(e,t){return r[t]?r[t].count:"-"});try{e["distributeChart"+t]=chart.alarmTierTop5({id:"distribute-chart"+t,xAxis:{data:a},series:{data:o,pinData:o},title:1===t?"全国":i,callback:function(r){if(e.tierChartClickable){var a=e["distributeChart"+t].data.find(function(e){return e.name===r}),i=e["distributeChart"+t].data.findIndex(function(e){return e===a}),n=e["distributeChart"+t].getOption();n.series[1].data=n.series[0].data=n.series[0].data.map(function(e){return e.value||e}),n.series[1].data[i]=n.series[0].data[i]={value:n.series[0].data[i],itemStyle:s},e["distributeChart"+t].setOption(n),t>2||(e.getTierDist(t+1,a.code,r),e.tierChartClickable=!1)}},callback2:function(t){e.leaved||u.go("alarm/list",{commonParam:{tierName:i?i+"-"+t:t,queryTEnd:e.tierDistQuery.tEnd,queryTStart:e.tierDistQuery.tStart}}),e.leaved=!0}}),e["distributeChart"+t].data=r,n.distributeCode&&(e["distributeChart"+t].setOption(n.distributeCode[t-1].option),e["distributeChart"+t].data=n.distributeCode[t-1].data),t<3?e.getTierDist(t+1,r[0].code,r[0].name):(e.tierChartClickable=!0,delete n.distributeCode)}catch(e){}}var m={enterpriseCode:e.user.enterpriseCode,serverCode:e.user.serverCode,tierMark:(n.tier||"").substr(0,2*t),tierCodePrefix:r,startBeginTime:e.tierDistQuery.tStart?new Date(e.tierDistQuery.tStart).getTime():null,startEndTime:e.tierDistQuery.tEnd?new Date(e.tierDistQuery.tEnd).getTime():null};m.tierMark.length<2*t&&delete m.tierMark;var s={normal:{color:{type:"linear",x:0,y:0,x2:1,y2:0,colorStops:[{offset:0,color:"#FF6D6D"},{offset:.49,color:"#FF6D6D"},{offset:.5,color:"#FF4A69"},{offset:1,color:"#FF4A69"}],globalCoord:!1},opacity:.8},emphasis:{opacity:1}};t>1&&!r?o({tierList:[]}):a.getAlarmTierList(m,o)},e.resetFrequentQuery=function(){e.frequentQuery={tEnd:(new Date).Format("yyyy/MM/dd"),tStart:new Date((new Date).getTime()-Date.oneWeek).Format("yyyy/MM/dd")}},e.resetFrequentQuery(),e.frequentQueryChange=function(t){var r=new Date(e.frequentQuery.tEnd),a=new Date(e.frequentQuery.tStart);t?r.getTime()&&r.getTime()>a.getTime()?r.getTime()-a.getTime()>Date.oneMonth&&(e.frequentQuery.tStart=new Date(r.getTime()-Date.oneMonth).Format("yyyy/MM/dd")):e.frequentQuery.tEnd=new Date(a.getTime()+Date.oneMonth).Format("yyyy/MM/dd"):a.getTime()&&r.getTime()>a.getTime()?r.getTime()-a.getTime()>Date.oneMonth&&(e.frequentQuery.tEnd=new Date(a.getTime()+Date.oneMonth).Format("yyyy/MM/dd")):e.frequentQuery.tStart=new Date(r.getTime()-Date.oneMonth).Format("yyyy/MM/dd")},e.getFrequentList=function(){var t={enterpriseCode:e.user.enterpriseCode,serverCode:e.user.serverCode,startBeginTime:e.frequentQuery.tStart?new Date(e.frequentQuery.tStart).getTime():null,startEndTime:e.frequentQuery.tEnd?new Date(e.frequentQuery.tEnd).getTime()+Date.oneDay-1:null,tierCode:n.tier,siteName:(n.query||{}).siteName};e.frequentAlarmList=[];var r=Math.round((t.startEndTime-t.startBeginTime)/Date.oneDay);e.frequentDateList=new Array(r).join(".").split(".").map(function(e,r){return new Date(new Date(t.startEndTime).getTime()-r*Date.oneDay).Format("yyyy/MM/dd")}).reverse(),i.alarmSiteHistory(t,function(t){t.sort(function(e,t){return t.count-e.count}),e.frequentSites=t,e.frequentAlarmList=t.map(function(e){return{name:e.values[0],total:e.count,count:e.values.slice(2,e.values.length)}}),$(".scroll").scrollTop(0)})},e.exportFrequentList=function(){e.exportFrequentList.have=!1,a.exportAlarmCountTab2({enterpriseCode:e.user.enterpriseCode,serverCode:e.user.serverCode,startBeginTime:e.frequentQuery.tStart?new Date(e.frequentQuery.tStart).getTime():null,startEndTime:e.frequentQuery.tEnd?new Date(e.frequentQuery.tEnd).getTime()+Date.oneDay-1:null,tierCode:n.tier},function(){})},e.exportFrequentList.have=!0,e.tabPage.currentIndex=n.tabPage?n.tabPage.currentIndex:0,e.$applyParam=function(t){e.tierDistQuery={tStart:t.queryTStart?new Date(t.queryTStart).Format("yyyy/MM/dd hh:mm:ss"):new Date((new Date).getTime()-Date.oneWeek).Format("yyyy/MM/dd hh:mm:ss"),tEnd:t.queryTEnd?new Date(t.queryTEnd).Format("yyyy/MM/dd hh:mm:ss"):new Date((new Date).getTime()).Format("yyyy/MM/dd hh:mm:ss")},e.realtimeQuery=t.realtimeQuery||e.realtimeQuery,e.tierDistQuery=t.tierDistQuery||e.tierDistQuery,e.frequentQuery=t.frequentQuery||e.frequentQuery,e.realtimePagiConf=t.realtimePagiConf||e.realtimePagiConf,n=t},e.$getParam=function(){return{tabPage:e.tabPage,distributeCode:e.distributeChart1&&e.distributeChart2&&e.distributeChart3&&[{option:e.distributeChart1.getOption(),data:e.distributeChart1.data},{option:e.distributeChart2.getOption(),data:e.distributeChart2.data},{option:e.distributeChart3.getOption(),data:e.distributeChart3.data}],realtimeParams:e.realtimeQueryLevel,realtimeQuery:e.realtimeQuery,tierDistQuery:e.tierDistQuery,frequentQuery:e.frequentQuery,realtimePagiConf:e.realtimePagiConf}},e.getRealtimeAlarmChartData(),e.getTierDistAll(),e.getFrequentList()}]);