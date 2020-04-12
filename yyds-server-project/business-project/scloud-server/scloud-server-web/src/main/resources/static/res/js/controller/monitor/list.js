scloudApp.controller("monitorListController",["$scope","$rootScope","treeService","deviceService","cameraService","$filter","$stateParams",function(e,i,t,a,r,n,o){function d(a){var r=t.getCheckedNode(a);if(!r)return i.notify({type:"warn",text:"请选择一个站点"}),void(e.currentSite=null);e.currentSite=r,c()}function c(){e.currentSite&&(e.deviceLoading=!0,e.currentCameraId="",r.getCameraList(e.currentSite.id,function(t){e.deviceLoading=!1,e.deviceList=t,e.deviceManager=i.util.tabPage.getInstance(e.deviceList);var a=e.parentCameraIdx?e.parentCameraIdx:0;e.deviceManager.currentIndex=a,s(t[a]),e.deviceManager.toSelect=function(i,t){e.deviceManager.showPage(t),s(i)}},function(){e.deviceLoading=!1}))}function s(i){if(e.currentCameraId=i&&i.id,e.currentCameraId)switch(e.tabPage.currentIndex){case 0:e.startVideoSource(),e.windowManager.setWindowSrc(i);break;case 1:l();break;case 2:f();break;case 3:v();break;case 4:m(),p(),y(),h()}}function u(e){var i={xtype:0,ytype:0,zoomType:0};switch(e){case 1:i.xtype=1,i.ytype=-1;break;case 2:i.ytype=-1;break;case 3:i.xtype=-1,i.ytype=-1;break;case 4:i.xtype=1;break;case 6:i.xtype=-1;break;case 7:i.xtype=1,i.ytype=1;break;case 8:i.ytype=1;break;case 9:i.xtype=-1,i.ytype=1;break;case"+":i.zoomType=1;break;case"-":i.zoomType=-1}return i}function l(){r.getVideoList(e.currentCameraId,e.videoQuery,function(i){e.videoFileList=i.list},function(){console.log("获取录像列表失败")})}function g(e,i){var t=e.toDataURL(i);t=t.replace(function(e){return e=e.toLocaleLowerCase().replace(/jpg/i,"jpeg"),"image/"+e.match(/png|jpeg|bmp|gif/)[0]}(i),"image/octet-stream");!function(e,i){var t=document.createElement("a");t.href=e,t.download=i;var a=document.createEvent("MouseEvents");a.initMouseEvent("click",!0,!1,window,0,0,0,0,0,!1,!1,!1,!1,0,null),t.dispatchEvent(a)}(t,"录像截图.jpg")}function f(){e.currentCameraId&&(e.isLoading1=!0,r.getCaptureList(e.currentCameraId,e.captureQuery,e.paginationConf1,function(i){e.isLoading1=!1,e.paginationConf1.totalItems=i.count,e.captureList=i.list},function(){e.isLoading1=!1}))}function v(){e.currentCameraId&&(e.isLoading2=!0,r.getLogList(e.currentCameraId,e.LogQuery,e.paginationConf2,function(i){console.log(i),e.isLoading2=!1,e.paginationConf2.totalItems=i.count,e.LogList=i.list},function(){e.isLoading2=!1}))}function m(){r.getDeviceInfomation(e.currentCameraId,function(i){i&&(e.currentCameraInfo.hostName=i.hostName,e.currentCameraInfo.hardwareId=i.hardwareId,e.currentCameraInfo.model=i.model,e.currentCameraInfo.firmwareVersion=i.firmwareVersion)})}function p(){r.getUserList(e.currentCameraId,function(i){e.cameraUserList=i})}function h(){r.getStoreSize(e.currentCameraId,function(i){e.localStoreConfig=i,i&&($("#imgCapacity").css({width:i.imageSizeNow+"%"}),$("#videoCapacity").css({width:i.videoSizeNow+"%"}))})}function y(){r.getImageConfig(e.currentCameraId,function(i){i.brightness+="",i.contrast+="",i.colorSaturation+="",i.sharpness+="",e.currentImageConfig=i,w()})}function w(){for(var e=0;e<$("input[type=range]").length;e++)$("input[type=range]").eq(e).trigger("input")}function L(e){}$state&&($state=window.$state),o&&(o=window.$stateParams),e.parentCameraIdx=o.deviceIdx,e.deviceLoading=!1,e.getNodesByParamFuzzy=function(e,i,t){},e.searchKey={online:!0,offline:!0,keyword:""},e.getNodesByParamFuzzy=function(){setTimeout(function(){e.zTree.getNodesByParamFuzzy(e.searchKey,"areaTree","radioSiteTree",function(){d("areaTree")})},10)},function(){e.treeLoading=!0,t.getSiteMapTree({},function(i){e.treeLoading=!1,e.zTree=t.createZTree(i),t.initTree(e.zTree.zNodes,"areaTree","radioSiteTree",function(){d("areaTree")}),t.checkNodeBySiteId("areaTree",null)})}(),e.tabPage=i.util.tabPage.getInstance(["实时视频","录像回放","图像回放","日志记录","配置管理"]),e.changeTopTab=function(t){if(e.tabPage.currentIndex!==t){if(0===t&&!i.util.checkPrivilege(5103)||1===t&&!i.util.checkPrivilege(5203)||2===t&&!i.util.checkPrivilege(5303)||3===t&&!i.util.checkPrivilege(5403)||4===t&&!i.util.checkPrivilege(5503))return void i.notify({type:"warn",text:"暂无权限"});if(0===t)switch(videoManager.disposePlayer("backplayer"),e.window_col){case"video-col1":e.videoList=[{id:"video1",hide:!1},{id:"video2",hide:!0},{id:"video3",hide:!0},{id:"video4",hide:!0},{id:"video5",hide:!0},{id:"video6",hide:!0},{id:"video7",hide:!0},{id:"video8",hide:!0},{id:"video9",hide:!0}];break;case"video-col2":e.videoList=[{id:"video1",hide:!1},{id:"video2",hide:!1},{id:"video3",hide:!1},{id:"video4",hide:!1},{id:"video5",hide:!0},{id:"video6",hide:!0},{id:"video7",hide:!0},{id:"video8",hide:!0},{id:"video9",hide:!0}];break;case"video-col3":e.videoList=[{id:"video1",hide:!1},{id:"video2",hide:!1},{id:"video3",hide:!1},{id:"video4",hide:!1},{id:"video5",hide:!1},{id:"video6",hide:!1},{id:"video7",hide:!1},{id:"video8",hide:!1},{id:"video9",hide:!1}]}else 1===t?(e.videoList.forEach(function(e){e.hide||-1===videoManager.getPlayer(e.id)||videoManager.disposePlayer(e.id)}),e.backVideoList=[{id:"backplayer",hide:!1}]):videoManager.disposeAll();e.tabPage.showPage(t)}},e.startVideoSource=function(){e.windowManager.initWindowSrc()},e.videoList=[{id:"video1",hide:!1},{id:"video2",hide:!0},{id:"video3",hide:!0},{id:"video4",hide:!0},{id:"video5",hide:!0},{id:"video6",hide:!0},{id:"video7",hide:!0},{id:"video8",hide:!0},{id:"video9",hide:!0}],e.playerList=[{id:"video1",hide:!1,src:""},{id:"video2",hide:!0,src:""},{id:"video3",hide:!0,src:""},{id:"video4",hide:!0,src:""},{id:"video5",hide:!0,src:""},{id:"video6",hide:!0,src:""},{id:"video7",hide:!0,src:""},{id:"video8",hide:!0,src:""},{id:"video9",hide:!0,src:""}],e.window_col="video-col1",e.changeWindow=function(i){switch(i){case 1:e.videoList.forEach(function(e,i){videoManager.disposePlayer(e.id)}),e.videoList=[],e.playerList.forEach(function(i,t){i.hide=0!==t,e.videoList.push({id:i.id,hide:i.hide,src:i.src})});break;case 2:e.videoList.forEach(function(e,i){videoManager.disposePlayer(e.id)}),e.videoList=[],e.playerList.forEach(function(i,t){i.hide=!(t<4),e.videoList.push({id:i.id,hide:i.hide,src:i.src})});break;case 3:e.videoList.forEach(function(e,i){videoManager.disposePlayer(e.id)}),e.videoList=[],e.playerList.forEach(function(i,t){i.hide=!1,e.videoList.push({id:i.id,hide:i.hide,src:i.src})})}e.window_col="video-col"+i},e.$on("ngRepeatFinished",function(i){0===e.tabPage.currentIndex?(C(e.videoList),e.currentPlayer=videoManager.players[0],e.windowManager.initWindowSrc()):1===e.tabPage.currentIndex&&(C(e.backVideoList),e.currentPlayer=videoManager.getPlayer("backplayer"))}),e.windowManager=i.util.tabPage.getInstance(e.videoList),e.windowManager.selectWindow=function(i){e.currentPlayer=videoManager.getPlayer(e.videoList[i].id),e.windowManager.showPage(i),e.videoPauseing=e.currentPlayer.paused()},e.windowManager.setWindowSrc=function(i){if(r.startVideoSource(i.id,function(e){}),e.currentPlayer){var t="rtmp://"+i.redIp+"/oflaDemo/"+i.id;videoManager.setPlayerSrc(e.currentPlayer.id_,t,function(i){e.playerList.forEach(function(i){i.id===e.currentPlayer.id_&&(i.src=t)})})}},e.windowManager.firstAccess=!0,e.windowManager.initWindowSrc=function(){if(!e.windowManager.firstAccess)return void e.playerList.forEach(function(e,i){""!==e.src&&videoManager.setPlayerSrc(e.id,e.src)});if(e.deviceList&&0!==e.deviceList.length&&e.windowManager.firstAccess){switch(e.window_col){case"video-col1":e.deviceList.forEach(function(i,t){if(t<1){var a="rtmp://"+i.redIp+"/oflaDemo/"+i.id;videoManager.setPlayerSrc("video"+(t+1),a,function(i){e.playerList[t].src=a})}});break;case"video-col2":e.deviceList.forEach(function(i,t){if(t<3){var a="rtmp://"+i.redIp+"/oflaDemo/"+i.id;videoManager.setPlayerSrc("video"+(t+1),a,function(i){e.playerList[t].src=a})}});break;case"video-col3":e.deviceList.forEach(function(i,t){if(t<9){var a="rtmp://"+i.redIp+"/oflaDemo/"+i.id;videoManager.setPlayerSrc("video"+(t+1),a,function(i){e.playerList[t].src=a})}})}e.windowManager.firstAccess=!1}},e.windowManager.closeWindow=function(i){var t=videoManager.getPlayer("video"+(i+1));t.reset(),t.hasStarted(!1),t.inUsing=!1,e.playerList.forEach(function(e,i){e.id===t.id_&&(e.src="")})};var C=function(e){e.forEach(function(e){e.hide||videoManager.players.some(function(i){return i.id_==e.id})||("backplayer"===e.id?videoManager.initPlayer(e.id,{techOrder:["html5","flash"],controlBar:{playToggle:!0,fullscreenToggle:!0,progressControl:!0,remainingTimeDisplay:!0,volumeMenuButton:!1}}):videoManager.initPlayer(e.id,{controlBar:!1}))})};e.startFocus=function(i){r.startFocus(e.currentCameraId,i,function(e){console.log(e)})},e.stopFocus=function(){r.stopFocus(e.currentCameraId,function(e){console.log(e)})},e.cameraCtrlStart=function(i){var t=u(i);r.cameraCtrlStart(e.currentCameraId,t,function(e){console.log("start",e)})},e.cameraCtrlStop=function(){r.cameraCtrlStop(e.currentCameraId,function(e){console.log("stop",e)})},e.resolutionList=["4:3","16:9"],$("#resolutionList").css("top",32*-e.resolutionList.length-18+"px"),e.updateAspect=function(i){e.currentCameraId&&r.updateAspect(e.currentCameraId,i,function(i){videoManager.setPlayerSrc(e.currentPlayer.id_,i,function(t){e.playerList.forEach(function(t){t.id===e.currentPlayer.id_&&(t.src=i)})})})},e.videoPauseing=!1,e.videoPause=function(){videoManager.playToggle(e.currentPlayer,function(i){e.videoPauseing=i})},e.recording=!1,e.startVideoRecord=function(){if(e.currentCameraId){for(var i=e.currentSite.name,t="",a=0;a<e.deviceList.length;a++)console.log(e.deviceList),e.deviceList[a].id===e.currentCameraId&&(t=e.deviceList[a].name);r.startVideoRecord(e.currentCameraId,n("date")(new Date,"yyyyMMddHHmmss")+"_"+t+"_"+i,function(){e.recording=!0;var i=new window.SpeechSynthesisUtterance;i.text="再次点击即可停止录制",i.volume=1,i.rate=1,i.pitch=1,window.speechSynthesis.speak(i)})}},e.stopVideoRecord=function(){r.stopVideoRecord(e.currentCameraId,function(){e.recording=!1})},e.videoCapture=function(){if(e.currentCameraId){for(var i=e.currentSite.name,t="",a=0;a<e.deviceList.length;a++)console.log(e.deviceList),e.deviceList[a].id===e.currentCameraId&&(t=e.deviceList[a].name);r.videoCapture(e.currentCameraId,n("date")(new Date,"yyyyMMddHHmmss")+"_"+t+"_"+i)}},e.fullScreen=function(){e.currentPlayer.customBtn.FullscreenToggle.targetFuc()},e.backVideoList=[{id:"backplayer",hide:!1}],e.videoQuery={model:{tStart:null,tEnd:null},confirm:function(){l()}},e.videoFileList=[],e.currentFile=-1,e.setBackVideoUrl=function(i,t){videoManager.setPlayerSrc("backplayer","http://"+i.downPath,function(){e.currentFile=t,e.backVideoPauseing=!1})},e.currentSpeed=1,e.speedUp=function(){e.currentSpeed<16&&e.currentPlayer&&(e.currentSpeed*=2,videoManager.setPlaybackRate("backplayer",e.currentSpeed))},e.speedDown=function(){e.currentSpeed>.125&&e.currentPlayer&&(e.currentSpeed/=2,videoManager.setPlaybackRate("backplayer",e.currentSpeed))},e.backVideoPauseing=!1,e.backVideoPause=function(){"backplayer"===e.currentPlayer.id_&&videoManager.playToggle(e.currentPlayer,function(i){e.backVideoPauseing=i})},e.nextBackVideo=function(){-1!==e.currentFile&&0!==e.videoFileList.length&&(e.currentFile===e.videoFileList.length-1?e.setBackVideoUrl(e.videoFileList[0],0):e.setBackVideoUrl(e.videoFileList[e.currentFile+1],e.currentFile+1))},e.backVideoCapture=function(){var e=$("#backplayer_html5_api").get(0),i=document.createElement("canvas");i.width=400,i.height=300,i.getContext("2d").drawImage(e,0,0,i.width,i.height),g(i,"image/jpeg")},e.downBackVideo=function(){-1!==e.currentFile&&0!==e.videoFileList.length&&(console.log(e.videoFileList[e.currentFile].id),window.location.href="/camera/downMp4/"+e.videoFileList[e.currentFile].id)},e.captureQuery={model:{type:"",tStart:n("date")((new Date).getTime()-864e5,"yyyy-MM-dd HH:mm:ss"),tEnd:n("date")((new Date).getTime()+864e5,"yyyy-MM-dd HH:mm:ss")},options:{type:["定时抓拍","手动抓拍","移动侦测"]},confirm:function(){f()},cancel:function(){this.model={type:"",tStart:"",tEnd:""}}},e.captureList=[],e.isLoading1=!1,e.paginationConf1={currentPage:1,itemsPerPage:15,pagesLength:9},e.$watch("paginationConf1.currentPage + paginationConf1.itemsPerPage",f),e.previewImg={url:""},e.previewImg.toModal=function(i){e.previewImg.url="/camera/lookGion/"+i.id,$("#previewImg").modal("show")},e.previewImg.clear=function(){e.previewImg.url="",$("#previewImg").modal("hide")},e.downGion=function(e){window.location.href="/camera/downGion/"+e.id},e.captureDelete=function(e){r.captureDelete(e,function(){f()})},e.LogQuery={model:{type:"",tStart:"",tEnd:""},options:{type:["操作日志","告警日志"]},confirm:function(){v()},cancel:function(){this.model={type:"",tStart:"",tEnd:""}}},e.LogList=[],e.isLoading2=!1,e.paginationConf2={currentPage:1,itemsPerPage:15,pagesLength:9},e.$watch("paginationConf2.currentPage + paginationConf2.itemsPerPage",v),e.tabPage2=i.util.tabPage.getInstance(["系统","网络","视频","图像","存储"]),e.currentCameraInfo={},e.setDeviceInfomation=function(){e.currentCameraId&&e.currentCameraInfo.hostName&&r.setDeviceInfomation(e.currentCameraId,e.currentCameraInfo.hostName,function(e){m()})},e.cameraUserList=[],e.currentTimeConfig="",e.getCurrentTime=function(){e.currentCameraId&&(e.currentTimeConfig=n("date")((new Date).getTime(),"yyyy-MM-dd HH:mm:ss"),r.setSystemTime(e.currentCameraId,e.currentTimeConfig,function(e){console.log("修改系统时间成功")}))},e.addUserMotal={userTypeList:[{key:"USER",value:"普通用户"},{key:"OPERATOR",value:"操作员"},{key:"ADMINISTRATOR",value:"管理员"}],form:{},obj:{}},e.addUserMotal.toModal=function(){e.currentCameraId&&$("#addUserModal").modal("show")},e.addUserMotal.execute=function(){$("#addUserModal").modal("hide"),r.addUser(e.currentCameraId,e.addUserMotal.obj,function(){e.addUserMotal.form.$setPristine(),p()})},e.addUserMotal.clear=function(){e.addUserMotal.form.$setPristine(),$("#addUserModal").modal("hide")},e.updateUserModal={userTypeList:[{key:"USER",value:"普通用户"},{key:"OPERATOR",value:"操作员"},{key:"ADMINISTRATOR",value:"管理员"}],form:{},obj:{}},e.updateUserModal.toModal=function(i){$("#updateUserModal").modal("show"),e.updateUserModal.obj.name=i.name,e.updateUserModal.obj.password=i.password,e.updateUserModal.obj.userType=e.updateUserModal.userTypeList[["普通用户","操作员","管理员"].indexOf(i.userLevel)].key},e.updateUserModal.execute=function(){$("#updateUserModal").modal("hide"),r.updateUser(e.currentCameraId,e.updateUserModal.obj,function(){e.updateUserModal.form.$setPristine(),p()})},e.updateUserModal.clear=function(){e.updateUserModal.form.$setPristine(),$("#updateUserModal").modal("hide")},e.deleteUser=function(i){r.deleteUser(e.currentCameraId,i,function(){p()})},e.localStoreConfig={imageSize:0,imageSizeNow:0,videoSize:0,videoSizeNow:0},e.storeConfig={},e.setStoreSize=function(){r.setStoreSize(e.storeConfig,function(e){h()})},e.currentImageConfig={brightness:"0",contrast:"0",colorSaturation:"0",sharpness:"0"},e.setImageConfig=function(){0!==e.currentImageConfig.brightness&&e.currentCameraId&&r.setImageConfig(e.currentCameraId,e.currentImageConfig)},$.fn.RangeSlider=function(e){this.sliderCfg={min:e&&!isNaN(parseFloat(e.min))?Number(e.min):null,max:e&&!isNaN(parseFloat(e.max))?Number(e.max):null,step:e&&Number(e.step)?e.step:1,callback:e&&e.callback?e.callback:null};var i=$(this),t=this.sliderCfg.min,a=this.sliderCfg.max,r=this.sliderCfg.step,n=this.sliderCfg.callback;i.attr("min",t).attr("max",a).attr("step",r),i.bind("input",function(e){var t=this;setTimeout(function(){i.attr("value",t.value),i.css("background","linear-gradient(to right, #059CFA "+t.value+"%, #000 "+t.value+"%)"),$.isFunction(n)&&n(t)},0)})};for(var I=0;I<$("input[type=range]").length;I++)$($("input[type=range]")[I]).RangeSlider({min:0,max:100,step:.1,callback:L});var P=window.AudioContext||window.webkitAudioContext;if(!window.AudioContext)return void console.log("当前浏览器不支持Web Audio API");for(var M=document.getElementsByClassName("sound"),b=new P,S=[196,220,246.94,261.63,293.66,329.63,349.23,392,440,493.88,523.25,587.33,659.25,698.46,783.99,880,987.77,1046.5],k=0,T=1,I=0;I<M.length;I++)M[I].addEventListener("mouseenter",function(){var e=S[k];e||(T*=-1,k+=2*T,e=S[k]),k+=T;var i=b.createOscillator(),t=b.createGain();i.connect(t),t.connect(b.destination),i.type="square",i.frequency.value=8e3,t.gain.setValueAtTime(0,b.currentTime),t.gain.linearRampToValueAtTime(.1,b.currentTime+.001),i.start(b.currentTime),t.gain.exponentialRampToValueAtTime(.01,b.currentTime+.001),i.stop(b.currentTime+.01)})}]);