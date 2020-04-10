var g_tmsum,BMapLib=window.BMapLib=BMapLib||{};!function(){function o(e,t,r){t=h(t);var s=e.pointToPixel(t.getNorthEast()),i=e.pointToPixel(t.getSouthWest());s.x+=r,s.y-=r,i.x-=r,i.y+=r;var a=e.pixelToPoint(s),n=e.pixelToPoint(i);return new BMap.Bounds(n,a)}function a(e){return"[object Array]"===Object.prototype.toString.call(e)}function r(e,t){var r=-1;if(a(t))if(t.indexOf)r=t.indexOf(e);else for(var s,i=0;s=t[i];i++)if(s===e){r=i;break}return r}var h=function(e){var t=n(e.getNorthEast().lng,-180,180),r=n(e.getSouthWest().lng,-180,180),s=n(e.getNorthEast().lat,-74,74),i=n(e.getSouthWest().lat,-74,74);return new BMap.Bounds(new BMap.Point(r,i),new BMap.Point(t,s))},n=function(e,t,r){return t&&(e=Math.max(e,t)),r&&(e=Math.min(e,r)),e},e=BMapLib.MarkerClusterer=function(e,t){if(e){this._map=e,this._markers=[],this._clusters=[];var r=t||{};this._gridSize=r.gridSize||60,this._maxZoom=r.maxZoom||18,this._minClusterSize=r.minClusterSize||2,this._isAverageCenter=!1,null!=r.isAverageCenter&&(this._isAverageCenter=r.isAverageCenter),this._styles=r.styles||[];var s=this;this._mapMoveCallback=function(){s._redraw()},this._map.addEventListener("zoomend",this._mapMoveCallback),this._map.addEventListener("moveend",this._mapMoveCallback);var i=r.markers;a(i)&&this.addMarkers(i)}};function m(e){this._markerClusterer=e,this._map=e.getMap(),this._minClusterSize=e.getMinClusterSize(),this._isAverageCenter=e.isAverageCenter(),this._center=null,this._markers=[],this._gridBounds=null,this._isReal=!1,this._clusterMarker=new BMapLib.TextIconOverlay(this._center,this._markers.length,{styles:this._markerClusterer.getStyles()})}e.prototype.dispose=function(){try{this._map.removeEventListener("zoomend",this._mapMoveCallback),this._map.removeEventListener("moveend",this._mapMoveCallback),this._markers.forEach(function(e){e.remove()}),this._clusters.forEach(function(e){e.remove()})}catch(e){return}},e.prototype.addMarkers=function(e){for(var t=0,r=e.length;t<r;t++)this._pushMarkerTo(e[t]);this._createClusters()},e.prototype._pushMarkerTo=function(e){-1===r(e,this._markers)&&(e.isInCluster=!1,this._markers.push(e))},e.prototype.addMarker=function(e){this._pushMarkerTo(e),this._createClusters()},e.prototype._createClusters=function(){var e,t=this._map.getBounds(),r=o(this._map,t,this._gridSize);(new Date).getTime();g_tmsum=0;var s,i,a=this._markers.length,n=this._markers;for(s=0;s<a;s++)!(i=n[s]).isInCluster&&r.containsPoint(i.getPosition())&&(e=(new Date).getTime(),this._addToClosestCluster(i),(new Date).getTime()-e);for(a=(n=this._clusters).length,s=0;s<a;s++)1==(i=n[s])._clusterMarker.bDelayDraw?this._map.addOverlay(i._clusterMarker):2==i._clusterMarker.bDelayDraw&&this._map.removeOverlay(i._clusterMarker),i.updateClusterMarker();(new Date).getTime()},e.prototype._addToClosestCluster=function(e){var t,r,s,i,a,n=16e12,o=null,h=e.getPosition(),u=this._clusters,l=u.length;for(i=0;i<l;i++){var _=(a=u[i]).getCenter();_&&(s=(t=_.lng-h.lng)*t+(r=_.lat-h.lat)*r)<n&&(n=s,o=a)}o&&o.isMarkerInClusterBounds(e)?o.addMarker(e,1):((a=new m(this)).addMarker(e,1),this._clusters.push(a))},e.prototype._clearLastClusters=function(){for(var e,t=0;e=this._clusters[t];t++)e.remove();this._clusters=[],this._removeMarkersFromCluster()},e.prototype._removeMarkersFromCluster=function(){for(var e,t=0;e=this._markers[t];t++)e.isInCluster=!1},e.prototype._removeMarkersFromMap=function(){for(var e,t=0;e=this._markers[t];t++)e.isInCluster=!1,this._map.removeOverlay(e)},e.prototype._removeMarker=function(e){var t=r(e,this._markers);return-1!==t&&(this._map.removeOverlay(e),this._markers.splice(t,1),!0)},e.prototype.removeMarker=function(e){var t=this._removeMarker(e);return t&&(this._clearLastClusters(),this._createClusters()),t},e.prototype.removeMarkers=function(e){for(var t=!1,r=0;r<e.length;r++){var s=this._removeMarker(e[r]);t=t||s}return t&&(this._clearLastClusters(),this._createClusters()),t},e.prototype.clearMarkers=function(){this._clearLastClusters(),this._removeMarkersFromMap(),this._markers=[]},e.prototype._redraw=function(){(new Date).getTime();this._clearLastClusters();(new Date).getTime();this._createClusters();(new Date).getTime()},e.prototype.getGridSize=function(){return this._gridSize},e.prototype.setGridSize=function(e){this._gridSize=e,this._redraw()},e.prototype.getMaxZoom=function(){return this._maxZoom},e.prototype.setMaxZoom=function(e){this._maxZoom=e,this._redraw()},e.prototype.getStyles=function(){return this._styles},e.prototype.setStyles=function(e){this._styles=e,this._redraw()},e.prototype.getMinClusterSize=function(){return this._minClusterSize},e.prototype.setMinClusterSize=function(e){this._minClusterSize=e,this._redraw()},e.prototype.isAverageCenter=function(){return this._isAverageCenter},e.prototype.getMap=function(){return this._map},e.prototype.getMarkers=function(){return this._markers},e.prototype.getClustersCount=function(){for(var e,t=0,r=0;e=this._clusters[r];r++)e.isReal()&&t++;return t},m.prototype.addMarker=function(e,t){if(this.isMarkerInCluster(e))return!1;if(this._center){if(this._isAverageCenter){var r=this._markers.length+1,s=(this._center.lat*(r-1)+e.getPosition().lat)/r,i=(this._center.lng*(r-1)+e.getPosition().lng)/r;this._center=new BMap.Point(i,s),this._center.bbb=1,this.updateGridBounds()}}else this._center=e.getPosition(),this.updateGridBounds();e.isInCluster=!0,this._markers.push(e);var a=(new Date).getTime(),n=(new Date).getTime();g_tmsum+=n-a;var o=this._markers.length;if(o<this._minClusterSize)return t?e.bDelayDraw=1:(this._map.addOverlay(e),e.bDelayDraw=0),!0;if(o===this._minClusterSize)for(var h=0;h<o;h++)this._markers[h].getMap()&&(t?this._markers[h].bDelayDraw=2:(this._map.removeOverlay(this._markers[h]),this._markers[h].bDelayDraw=0));return t?this._clusterMarker.bDelayDraw=1:(this._map.addOverlay(this._clusterMarker),this._clusterMarker=0),this._isReal=!0,t||this.updateClusterMarker(),!0},m.prototype.isMarkerInCluster=function(e){if(this._markers.indexOf)return-1!=this._markers.indexOf(e);for(var t,r=0;t=this._markers[r];r++)if(t===e)return!0;return!1},m.prototype.isMarkerInClusterBounds=function(e){return this._gridBounds.containsPoint(e.getPosition())},m.prototype.isReal=function(e){return this._isReal},m.prototype.updateGridBounds=function(){var e=new BMap.Bounds(this._center,this._center);this._gridBounds=o(this._map,e,this._markerClusterer.getGridSize())},m.prototype.updateClusterMarker=function(e){if(this._level=this._markers.reduce(function(e,t){return Math.min(e,0===t.topAlarm?1/0:t.topAlarm)},1/0),4<this._level&&(this._level=0),this._map.getZoom()>this._markerClusterer.getMaxZoom()){this._clusterMarker&&(e?this._clusterMarker.bDelayDraw=2:(this._map.removeOverlay(this._clusterMarker),this._clusterMarker.bDelayDraw=0));for(var t=0;r=this._markers[t];t++)e?r.bDelayDraw=1:(this._map.addOverlay(r),r.bDelayDraw=0)}else if(this._markers.length<this._minClusterSize){this._clusterMarker.hide();var r;for(t=0;r=this._markers[t];t++)e?r.bDelayDraw=1:(this._map.addOverlay(r),r.bDelayDraw=0)}else{this._clusterMarker.setPosition(this._center),this._clusterMarker.setText(this._markers.length,this._level);this._map,this.getBounds()}},m.prototype.remove=function(){for(var e=0;this._markers[e];e++)this._markers[e].getMap()&&this._map.removeOverlay(this._markers[e]);this._map.removeOverlay(this._clusterMarker),this._markers.length=0,delete this._markers},m.prototype.getBounds=function(){for(var e,t=new BMap.Bounds(this._center,this._center),r=0;e=this._markers[r];r++)t.extend(e.getPosition());return t},m.prototype.getCenter=function(){return this._center}}();