scloudApp.controller("operationMapController",["$scope","$rootScope","siteService","treeService","$compile","$state","$stateParams",function(e,t,i,n,a,o,r){function s(t){function i(){e.treeLoading=!1,e.zTree=n.createZTree(a),n.initTree(e.zTree.zNodes,"siteTree","checkboxSiteMapTree",p,l),r.siteIdList?(n.applyTreeParam("siteTree",r)||n.checkedAllNodes("siteTree",!0),delete r.siteIdList):e.filterTree()}e.treeLoading=!0;var a;t.company||(t.company=null),t.name||(t.name=null),t.shareInfo&&!t.shareInfo.length&&(t.shareInfo=null),n.getSiteMapTree({query:t,keys:["shareInfo","fsuState","approvalStatus","coordinate","code","stationType","online","alarmNum","address","towerHeight","towerType","company","platform","recordTime"]},function(e){a=e,i()})}function l(t,i,n){if(!n.nextTier){var a=new BMap.Point(n.coordinate.split(",")[0],n.coordinate.split(",")[1]);e.map.setZoom(12);v.filter(function(e){return e.site.id===n.id})[0];e.map.panTo(a),setTimeout(function(){e.curPin=e.curSite=null,n.isPin?e.curPin=n:(e.curSite=n,e.locateDetail()),e.$apply()},50)}}function p(t){e.curSite=null,e.curPin=null,Array.isArray(t)||(t=n.getCheckedNodes("siteTree")),t=t.filter(function(e){return e.coordinate}),1===t.length&&t[0].coordinate?t[0].isPin?(e.curPin=t[0],e.map.setCenter(new BMap.Point(e.curPin.coordinate.split(",")[0],e.curPin.coordinate.split(",")[1]))):(e.curSite=t[0],e.map.setCenter(new BMap.Point(e.curSite.coordinate.split(",")[0],e.curSite.coordinate.split(",")[1])),setTimeout(function(){e.locateDetail()},100)):e.$$phase||e.$apply(),e.query(e.zTree.isEmpty?[]:t)}function c(t){$(t.target).parents(".filter").length||$(t.target).is(".filter")||$(t.target).is("body")||(t.stopPropagation(),e.showFilter=!1,e.$apply(),document.removeEventListener("click",c))}function d(e,t){var i=new BMap.Marker(e);for(var n in i)this[n]=i[n];this._point=e,delete this.initialize,this.site=t}function u(t){if(this.site){$(this._div).removeClass("no-animation");var n=this.site.__get("name","company","shareInfo","id");n.coordinate=t.point.lng+","+t.point.lat,this.disableDragging(),i.updatePin(n,function(){this.site=n,this.enableDragging()}.bind(this))}else e.pinPoint=this.getPosition(),e.pinCoord=e.map.pointToOverlayPixel(e.pinPoint),e.$apply()}o&&(o=window.$state),r&&(r=window.$stateParams),e.siteQuery={},e.treeQuery={online:!0,offline:!0,onbuilding:!0,finishBuilding:!0,stock:!0,discard:!0,pin:!0},e.shareInfoList={name:"全部",children:[{name:"暂无"},{name:"移动"},{name:"电信"},{name:"联通"},{name:"铁塔"},{name:"移动电信"},{name:"电信联通"},{name:"移动联通"},{name:"移动铁塔"},{name:"联通铁塔"},{name:"电信铁塔"},{name:"移动电信铁塔"},{name:"移动电信联通"},{name:"电信联通铁塔"},{name:"移动联通铁塔"},{name:"移动电信联通铁塔"}]},e.curView="approval",e.isAddingPin=!1;var f=new Image;f.src="./img/common/meitainuo-sprite.png";var y={online:!0,offline:!0,onbuilding:!0,finishBuilding:!0,stock:!0,discard:!0,pin:!0};e.isFilterSite=!0;var v=[];e.filterSiteName=function(){var t;if(e.searchKey){n.checkedAllNodes("siteTree",!1);var i=$.fn.zTree.getZTreeObj("siteTree");t=i.getNodesByFilter(function(t){return(!t.nextTier||0===t.nextTier.length)&&t.name.indexOf(e.searchKey)>-1},!1),setTimeout(function(){t.forEach(function(e){i.checkNode(e,!0,!0,!0)})},300)}else n.checkedAllNodes("siteTree",!0),t=e.zTree.isEmpty?[]:n.getCheckedNodes("siteTree");p(t)},e.filterTree=function(){angular.equals(e.treeQuery,y)&&!e.isFilterSite||(e.isFilterSite=!1,y=angular.copy(e.treeQuery),n.filterTree(function(t){return!!t.coordinate&&(!(!(e.treeQuery.onbuilding&&"在建"===t.approvalStatus||e.treeQuery.finishBuilding&&"竣工"===t.approvalStatus||e.treeQuery.stock&&"存量"===t.approvalStatus||e.treeQuery.discard&&"拆除"===t.approvalStatus||e.treeQuery.pin&&"临时"===t.approvalStatus||e.treeQuery.pin&&t.isPin)&&(e.treeQuery.onbuilding||e.treeQuery.finishBuilding||e.treeQuery.stock||e.treeQuery.discard||e.treeQuery.pin)||!(t.online&&e.treeQuery.online||!t.online&&e.treeQuery.offline||!e.treeQuery.online&&!e.treeQuery.offline))&&(!e.siteQuery.platform||!t.isPin))},e.zTree,"siteTree","checkboxSiteMapTree",p,l)),e.map.canvasOverlay&&e.map.canvasOverlay.draw(),e.filterSiteName()},e.showFilterDialog=function(){e.showFilter=!e.showFilter,e.showFilter&&document.addEventListener("click",c)},e.addPin={obj:{},execute:function(){var t=$.fn.zTree.getZTreeObj("siteTree");t.getNodeByParam("code","-2")||t.addNodes(t.getNodeByParam("code","-1"),{code:"-2",name:"临时标记"}),i.addPin({name:this.obj.name,coordinate:this.overlay.getPosition().lng+","+this.overlay.getPosition().lat,company:this.obj.company,shareInfo:this.obj.shareInfo},function(){delete e.pinCoord,delete e.pinPoint,e.isAddingPin=!0,e.addPin.obj={},e.addPin.overlay.enableDragging(),e.isAddingPin=!1,t.getNodesByParam("isPin",!0).forEach(function(e){t.removeNode(e)}),i.getPinList({},function(i){var n=t.getNodesByParam("code","-2")[0];i.forEach(function(e){e.isPin=!0}),t.addNodes(n,i),e.filterSiteName()})})},clear:function(){delete e.pinCoord,delete e.pinPoint,e.isAddingPin=!0,this.obj={},this.overlay.remove()}},e.deletePin={toModal:function(){$("#deletePinModal").modal("show")},execute:function(){var t=$.fn.zTree.getZTreeObj("siteTree");i.deletePin(e.curPin.id,function(){t.removeNode(e.curPin),e.curPin=null,e.filterSiteName();var i=t.getNodeByParam("code","-2");i.nextTier.length||t.removeNode(i),$("#deletePinModal").modal("hide")})},clear:function(){$("#deletePinModal").modal("hide")}},d.prototype=$.extend({},BMap.Marker.prototype,{initialize:function(t){if(this._div=BMap.Marker.prototype.initialize.call(this,t),this._div){var i={"暂无":"zw","移动":"y","电信":"d","联通":"l","铁塔":"t","移动电信":"yd","电信联通":"dl","移动联通":"yl","移动铁塔":"yt","联通铁塔":"lt","电信铁塔":"dt","移动电信铁塔":"ydt","移动电信联通":"ydl","电信联通铁塔":"dlt","移动联通铁塔":"ylt","移动电信联通铁塔":"ydlt"}[this.site&&this.site.shareInfo]||"";this._div.className+=" map-location-pin "+i,this._div.addEventListener("mousedown",function(){this.mousedownTime=(new Date).getTime()}.bind(this)),this._div.addEventListener("mouseup",function(){(new Date).getTime()-this.mousedownTime<300&&this.site&&(e.curPin=this.site,e.curSite=null,e.curPin.coordinate=this.point.lng+","+this.point.lat,e.$apply()),delete this.mousedownTime}.bind(this))}return this._div}}),e.getPlatformInfo=function(){return e.curSite&&e.curSite.platform&&e.curSite.platform.filter(function(e){return e}).join(",")},e.toSceneSite=function(e){o.go("scene/site",{commonParam:{siteIdList:[e]}})},e.locateDetail=function(){if(e.pinPoint&&(e.pinCoord=e.map.pointToOverlayPixel(e.pinPoint)),e.curSite){var t=e.map.pointToOverlayPixel(e.curSite.point);e.curSite.left=t.x-50,e.curSite.top=t.y+20}(e.pinPoint||e.curSite||e.curPin)&&e.$apply()},e.query=function(t){function i(t,i,n){n=n||{};var a=t.sizeMap["approval"===e.curView?i.approvalStatus:i.shareInfo||"移动"],o=r.pointToOverlayPixel(i.point),s=n.pos&&[n.pos.x,n.pos.y]||[o.x-t.canvas.pos.x,o.y-t.canvas.pos.y],l=n.ctx||t.ctx;l.fillStyle="rgba(0,255,0,0.9)",l.fillStyle="rgba(255,0,0,0.5)",l.drawImage(f,a[0],a[2],a[1],a[3],s[0]-a[1]/2+a[4],s[1]-a[3],a[1],a[3])}function n(e,t){var n=r.pointToOverlayPixel(e.point);n.x-=t.canvas.pos.x,n.y-=t.canvas.pos.y;var a=t._toRender.filter(function(e){var i=r.pointToOverlayPixel(e.point);return i.x-=t.canvas.pos.x,i.y-=t.canvas.pos.y,Math.abs(n.x-i.x)<68&&Math.abs(n.y-i.y-50)<120});c.clearRect(0,0,1920,1080),n=r.pointToOverlayPixel(e.point),n.x-=t.canvas.pos.x,n.y-=t.canvas.pos.y,t.ctx.clearRect(n.x-34,n.y-100,68,100),a.forEach(function(e){i(t,e,{ctx:c})}),t.ctx.drawImage(p,n.x-34,n.y-100,68,100,n.x-34,n.y-100,68,100)}function a(e,t){var n=r.pointToOverlayPixel(e.point);n.x-=t.canvas.pos.x,n.y-=t.canvas.pos.y;var a=t._toRender.filter(function(i){var a=r.pointToOverlayPixel(i.point);return a.x-=t.canvas.pos.x,a.y-=t.canvas.pos.y,Math.abs(n.x-a.x)<68&&Math.abs(n.y-a.y-50)<120&&i!==e}),o=0;return setInterval(function(){c.clearRect(0,0,1920,1080),n=r.pointToOverlayPixel(e.point),n.x-=t.canvas.pos.x,n.y-=t.canvas.pos.y,t.ctx.clearRect(n.x-34,n.y-100,68,100),a.forEach(function(e){i(t,e,{ctx:c})}),i(t,e,{pos:{x:n.x,y:n.y-20*Math.abs(Math.sin(o))},ctx:c}),t.ctx.drawImage(p,n.x-34,n.y-100,68,100,n.x-34,n.y-100,68,100),o+=.4},50)}function o(e){this._data=e,e.forEach(function(e){var t=e.coordinate||"0,0";e.point=new BMap.Point(t.split(",")[0],t.split(",")[1])}),this.sizeMap={"暂无":[270,30,8,33,0],"移动":[70,30,58,33,0],"电信":[70,30,8,33,0],"联通":[70,30,108,33,0],"移动电信":[70,30,208,33,0],"电信联通":[70,30,258,33,0],"移动联通":[70,30,158,33,0],"移动电信联通":[70,30,308,33,0],"铁塔":[270,30,8,33,0],"移动铁塔":[270,30,108,33,0],"联通铁塔":[270,30,158,33,0],"电信铁塔":[270,30,208,33,0],"移动联通铁塔":[270,30,258,33,0],"电信联通铁塔":[270,30,308,33,0],"移动电信铁塔":[270,30,358,33,0],"移动电信联通铁塔":[270,30,408,33,0],"在建":[70,30,402,42,0],"存量":[70,30,451,40,0],"竣工":[70,32,511,30,16],"拆除":[70,30,352,42,0],"临时":[77,14,558,34,0]}}e.map.clearOverlays();var r=e.map,s=[],l=[];t.forEach(function(e){e.isPin?l.push(e):s.push(e)}),l.forEach(function(e){var t=new BMap.Point(e.coordinate.split(",")[0],e.coordinate.split(",")[1]),i=new d(t,e);i.enableDragging(),i.addEventListener("dragstart",function(e){$(e.currentTarget._div).addClass("no-animation")}),i.addEventListener("dragend",u.bind(i)),r.addOverlay(i)});var p=document.createElement("canvas"),c=p.getContext("2d");p.width=1920,p.height=1080,o.prototype=new BMap.Overlay,o.prototype.initialize=function(t){this._map=t;var i=document.createElement("canvas"),o=i.getContext("2d");return i.width=1920,i.height=1080,i.style="position: absolute; top: 0; left: 0;",i.addEventListener("click",function(t){this.hoverNode&&(e.curSite=this.hoverNode,e.curPin=null,e.locateDetail(),e.$apply(),t.stopPropagation())}.bind(this)),i.addEventListener("mouseout",function(e){this.hoverNode&&(clearInterval(this.lastAnimate),n(this.hoverNode,this))}.bind(this)),i.addEventListener("mousemove",function(i){var o=this._toRender.filter(function(n){var a=this.sizeMap["approval"===e.curView?n.approvalStatus:n.shareInfo||"移动"],o=t.pointToOverlayPixel(n.point);return o.x-=this.canvas.pos.x,o.y-=this.canvas.pos.y,i.offsetX-o.x<a[1]/2+a[4]&&i.offsetX-o.x>-a[1]/2+a[4]&&o.y-i.offsetY<a[3]&&o.y-i.offsetY>0}.bind(this)),r=this.hoverNode;this.hoverNode=o[o.length-1],r!==this.hoverNode&&(clearInterval(this.lastAnimate),r&&n(r,this),this.hoverNode&&(this.lastAnimate=a(this.hoverNode,this))),this.hoverNode?this.canvas.style.cursor="pointer":this.canvas.style.cursor="default"}.bind(this)),t.getPanes().labelPane.appendChild(i),this.canvas=i,this.ctx=o,i},o.prototype.draw=function(){var e=this._map,t=e.getBounds().getNorthEast(),n=e.getBounds().getSouthWest(),a=e.pointToOverlayPixel(new BMap.Point(n.lng,t.lat));this.canvas.pos=a,this.canvas.style="position: absolute; left: "+a.x+"px; top: "+a.y+"px;",this.ctx.clearRect(0,0,1920,1080),this.ctx.fillStyle="rgba(255,0,0,0.1)",this._toRender=this._data.filter(function(e){return e.point.lng>n.lng&&e.point.lng<t.lng&&e.point.lat<t.lat&&e.point.lat>n.lat}),this._toRender.forEach(function(e){i(this,e)}.bind(this))};var y=new o(s);r.addOverlay(y),r.canvasOverlay=y},e.filterSite=function(){e.treeLoading=!0,e.isFilterSite=!0;var t=this.siteQuery.__get("code","company","platform");this.siteQuery.stationType&&(t.stationType=[this.siteQuery.stationType]),t.shareInfo=e.shareInfoList.children.filter(function(e){return e.value}).map(function(e){return e.name}),setTimeout(function(){s(t)},300)},e.checkCoordValid=function(){var t=parseFloat(e.siteQuery.lat),i=parseFloat(e.siteQuery.lng);return!isNaN(i)&&!isNaN(t)&&t<=180&&t>=0&&i<=180&&i>=0},e.getAddr=function(){var i=new BMap.Geocoder,n=new BMap.Point(e.siteQuery.lng,e.siteQuery.lat);i.getLocation(n,function(i){var n=i.addressComponents;i.address||t.notify({type:"warning",text:"找不到该坐标的地址"}),e.siteQuery.address=[n.province,n.city,n.district,n.street,n.streetNumber].filter(function(e){return!!e}).join(", "),e.$apply()})},e.clearSiteQuery=function(){e.siteQuery={},e.searchKey="",e.shareInfoList.children.forEach(function(e){e.value=!1}),e.shareInfoList.update=!0},e.showImage=function(){e.showSIteimage=!0,$("#imageShowModal").modal("show")},e.hidemage=function(){e.showSIteimage=!1,$("#imageShowModal").modal("hide")},e.startDistance=function(){e.distanceToolObject.open()},e.driveRoute=function(){e.clearRoute();var t=new BMap.Point(e.curSite.coordinate.split(",")[0],e.curSite.coordinate.split(",")[1]);!function(t,i,n){new BMap.DrivingRoute(e.map,{renderOptions:{map:e.map,autoViewport:!0},policy:n}).search(t,i)}(e.curPoint,t,BMAP_DRIVING_POLICY_LEAST_TIME),e.curSite=void 0,e.curPin=void 0},e.clearRoute=function(){e.map.getOverlays().forEach(function(e){e.site||e.remove()})},setTimeout(function(){"share"===r.curView?e.filterSite():s({})},300),function(){function t(){if("#/operation/map"!==location.hash)return void document.removeEventListener("keydown",t);e.isAddingPin&&(e.isAddingPin=!1,e.$apply())}var i=new BMap.Map("siteMap");e.distanceToolObject=new BMapLib.DistanceTool(i,{lineStroke:2}),i.centerAndZoom(new BMap.Point(106.7732,37.313085),5),i.setMapStyle({styleJson:[{featureType:"background",elementType:"geometry.fill",stylers:{color:"#f2f8ffff"}},{featureType:"districtlabel",elementType:"labels.text.fill",stylers:{color:"#66c2ffff"}},{featureType:"district",elementType:"labels.text.fill",stylers:{color:"#66c2ffff"}},{featureType:"district",elementType:"labels.text.stroke",stylers:{color:"#66c2ff00"}},{featureType:"water",elementType:"geometry",stylers:{color:"#66c2ffff"}},{featureType:"arterial",elementType:"geometry",stylers:{color:"#e0f1fdff",hue:"#bce3fd"}},{featureType:"local",elementType:"geometry",stylers:{color:"#e0f1fdff",hue:"#bce3fd"}},{featureType:"highway",elementType:"geometry",stylers:{color:"#e0f1fdff",hue:"#bce3fd"}},{featureType:"subway",elementType:"geometry",stylers:{color:"#f2f8ffff",visibility:"off",weight:2.9}},{featureType:"subway",elementType:"labels",stylers:{color:"#f2f8ffff",visibility:"on"}},{featureType:"railway",elementType:"geometry",stylers:{color:"#f2f8ffff"}},{featureType:"railway",elementType:"labels",stylers:{color:"#f2f8ffff"}},{featureType:"airportlabel",elementType:"labels",stylers:{color:"#f2f8ffff"}},{featureType:"scenicspotslabel",elementType:"labels",stylers:{color:"#f2f8ffff"}},{featureType:"educationlabel",elementType:"labels",stylers:{color:"#f2f8ffff"}},{featureType:"medicallabel",elementType:"labels",stylers:{color:"#f2f8ffff"}},{featureType:"poilabel",elementType:"labels",stylers:{visibility:"on"}},{featureType:"poilabel",elementType:"labels.icon",stylers:{visibility:"off"}},{featureType:"subwaystation",elementType:"geometry",stylers:{visibility:"off"}},{featureType:"education",elementType:"geometry",stylers:{visibility:"off"}},{featureType:"medical",elementType:"geometry",stylers:{visibility:"off"}},{featureType:"transportation",elementType:"geometry",stylers:{visibility:"off"}},{featureType:"shopping",elementType:"geometry",stylers:{visibility:"on"}},{featureType:"poilabel",elementType:"labels.text.fill",stylers:{color:"#66c2ffff"}},{featureType:"poilabel",elementType:"labels.text.stroke",stylers:{color:"#ffffff0"}}]}),i.enableScrollWheelZoom(!0),i.addControl(new BMap.MapTypeControl({mapTypes:[BMAP_NORMAL_MAP,BMAP_SATELLITE_MAP,BMAP_HYBRID_MAP]})),i.addEventListener("mouseout",function(){e.inMap=!1,e.isAddingPin&&e.$apply()}),i.addEventListener("mousemove",function(t){e.inMap?e.isAddingPin&&$("#addPinHint").css({top:t.clientY+"px",left:t.clientX+"px"}):(e.inMap=!0,e.isAddingPin&&e.$apply())}),i.addEventListener("mousedown",function(t){e.isAddingPin&&2===t.domEvent.button&&(e.isAddingPin=!1,e.$apply())}),document.addEventListener("keydown",t),i.addEventListener("click",function(t){if(e.isAddingPin){e.pinCoord=e.map.pointToOverlayPixel(t.point),e.pinPoint=t.point,e.isAddingPin=!1;var n=new d(t.point);n.enableDragging(),n.addEventListener("dragend",u),n.addEventListener("dragging",function(t){var i=e.map.pointToOverlayPixel(t.point);$("#addPinModal").css({left:i.x-165+"px",top:i.y+20+"px"})}),i.addOverlay(n),e.addPin.overlay=n,e.map.panTo(t.point),e.$apply(),setTimeout(function(){n._div.className+=" no-animation"},100)}}),e.map=i,(new BMap.Geolocation).getCurrentPosition(function(t){if(this.getStatus()===BMAP_STATUS_SUCCESS){var n=new BMap.Marker(t.point);i.addOverlay(n),n.disableMassClear(),n.remove=function(){},e.curPoint=t.point}},{enableHighAccuracy:!0}),i.addEventListener("zoomend",function(){e.locateDetail(),setTimeout(function(){e.locateDetail()},50)}),setTimeout(function(){var t=a('<div ng-show="curSite" ng-mousedown="$event.stopPropagation()" ng-style="{top: curSite.top + \'px\', left: curSite.left + \'px\'}" class="site-detail"><div class="close" ng-click="curSite = null">&times;</div><div class="row-item"><span>站点编码</span><span ng-bind="curSite.code | nullFilter"></span></div><div class="row-item"><span>站点名称</span><span><a ng-click="toSceneSite(curSite.id)" ng-bind="curSite.name | nullFilter"></a></span></div><div class="row-item"><span>站点类型</span><span ng-bind="curSite.stationType | stationTypeFilter"></span></div><div class="row-item"><span>站点状态</span><span ng-class="curSite.online?\'font-success\':\'font-danger\'" ng-bind="curSite.online?\'FSU 在线\':\'FSU 离线\'"></span></div><div class="row-item"><span>告警数量</span><span ng-bind="curSite.alarmNum || 0"></span></div><div class="row-item"><span>站点地址</span><span>{{curSite.address | nullFilter}} <a class="ic ic-map-location" ng-click="driveRoute()"></a></span></div><div class="row-item"><span>站点经纬度</span><span ng-bind="curSite.coordinate | nullFilter"></span></div><div class="row-item"><span>铁塔高度</span><span ng-bind="curSite.towerHeight | nullFilter: \'米\'"></span></div><div class="row-item"><span>铁塔类型</span><span ng-bind="curSite.towerType | nullFilter"></span></div><div class="row-item"><span>归属公司</span><span ng-bind="curSite.company | nullFilter"></span></div><div class="row-item"><span>审批状态</span><span ng-bind="curSite.approvalStatus | nullFilter"></span></div><div class="row-item"><span>共享信息</span><span ng-bind="curSite.shareInfo | nullFilter"></span></div><div class="row-item"><span>平台信息</span><span class="platform" title="{{getPlatformInfo() | nullFilter}}" ng-bind="getPlatformInfo() | nullFilter"></span></div><div class="row-item"><span>站点图片</span><a ng-click="showImage()">查看</a></div><div class="arrow"></div></div>')(e);$("#siteMap >div :eq(0) > div:not(.BMap_mask):eq(0)").append(t);var i=a('<div id="addPinModal" ng-show="pinCoord" ng-mousedown="$event.stopPropagation()" ng-style="{left: pinCoord.x - 165 + \'px\', top: pinCoord.y + 20 + \'px\'}">    <div class="header">        <h3>添加临时标记</h3>        <div class="close" ng-click="addPin.clear(); $event.stopPropagation();">&times;</div>     </div>    <div class = "item"><label>站点名称</label><input type="text" ng-model = "addPin.obj.name" /></div>    <div class = "item"><label>归属公司</label><input type="text" ng-model = "addPin.obj.company" /></div>    <div class = "item"><label>共享信息</label><select ng-options = "item.name as item.name for item in shareInfoList.children" ng-model = "addPin.obj.shareInfo"></select></div>    <div class="footer">        <button class="btn btn-primary" ng-click="addPin.execute(); $event.stopPropagation();" ng-disabled="!addPin.obj.name || !addPin.obj.company || !addPin.obj.shareInfo">保存</button>        <button class="btn btn-default" ng-click="addPin.clear(); $event.stopPropagation();">取消</button>    </div></div>')(e);$("#siteMap >div :eq(0) > div:not(.BMap_mask):eq(0)").append(i);var n=a('<div id="pinDetailModal" ng-show="curPin" ng-mousedown="$event.stopPropagation()"     ng-style="{        top: map.pointToOverlayPixel({lng: curPin.coordinate.split(\',\')[0], lat: curPin.coordinate.split(\',\')[1]}).y + 20 + \'px\',        left: map.pointToOverlayPixel({lng: curPin.coordinate.split(\',\')[0], lat: curPin.coordinate.split(\',\')[1]}).x - 55 + \'px\'     }">    <div class="close" ng-click="curPin = null">&times;</div>    <div class="row-item"><span>站点名称</span><span ng-bind="curPin.name | nullFilter"></span></div>    <div class="row-item"><span>经度</span><span ng-bind="curPin.coordinate.split(\',\')[0] | nullFilter"></span></div>    <div class="row-item"><span>纬度</span><span ng-bind="curPin.coordinate.split(\',\')[1] | nullFilter"></span></div>    <div class="row-item"><span>归属公司</span><span ng-bind="curPin.company | nullFilter"></span></div>    <div class="row-item"><span>共享信息</span><span ng-bind="curPin.shareInfo | nullFilter"></span></div>    <div class="row-item"><button class="btn btn-primary pull-right" ng-click="deletePin.toModal();">删除</button></div>    <div class="arrow"></div></div>')(e);$("#siteMap >div :eq(0) > div:not(.BMap_mask):eq(0)").append(n)},300)}()}]);