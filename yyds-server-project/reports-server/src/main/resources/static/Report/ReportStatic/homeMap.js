function createHomeMap(params) {
    var alarmMapId = params.alarmMapId;
    var overlays = [];
    var bdary = new BMap.Boundary();
    var geoc = new BMap.Geocoder();
    var chinaPlain = [[], [], []];
    var lastTier = [];
    var labels = [];
    var mapLocation;
    var isResized = false;
    var map = new BMap.Map(alarmMapId, {
      minZoom: 5
    }); // 创建Map实例
    // 某些区域的边境没有了, 必须替换成别的名字
  
    var DistrictMapper = {
      '浙江省杭州市临安区': '临安区'
    };
    map.centerAndZoom(new BMap.Point(106.7732, 37.313085), 5); // 初始化地图,设置中心点坐标和地图级别*     
  
    map.setMapStyle({
      styleJson: [{
        "featureType": "water",
        "elementType": "all",
        "stylers": {
          "color": "#001427ff"
        }
      }, {
        "featureType": "arterial",
        "elementType": "geometry.fill",
        "stylers": {
          "color": "#00325cff"
        }
      }, {
        "featureType": "arterial",
        "elementType": "geometry.stroke",
        "stylers": {
          "color": "#00325cff"
        }
      }, {
        "featureType": "local",
        "elementType": "geometry",
        "stylers": {
          "color": "#00325cff"
        }
      }, {
        "featureType": "land",
        "elementType": "all",
        "stylers": {
          "color": "#003473ff"
        }
      }, {
        "featureType": "road",
        "elementType": "geometry.fill",
        "stylers": {
          "color": "#00325cff"
        }
      }, {
        "featureType": "building",
        "elementType": "geometry.fill",
        "stylers": {
          "color": "#000000ff"
        }
      }, {
        "featureType": "all",
        "elementType": "labels.text.fill",
        "stylers": {
          "color": "#857f7fff"
        }
      }, {
        "featureType": "all",
        "elementType": "labels.text.stroke",
        "stylers": {
          "color": "#003473ff"
        }
      }, {
        "featureType": "building",
        "elementType": "geometry",
        "stylers": {
          "color": "#022338ff"
        }
      }, {
        "featureType": "green",
        "elementType": "geometry",
        "stylers": {
          "color": "#00335fff"
        }
      }, {
        "featureType": "manmade",
        "elementType": "geometry",
        "stylers": {
          "color": "#022338"
        }
      }, {
        "featureType": "poi",
        "elementType": "all",
        "stylers": {
          "visibility": "off"
        }
      }, {
        "featureType": "all",
        "elementType": "labels.icon",
        "stylers": {
          "visibility": "off"
        }
      }, {
        "featureType": "all",
        "elementType": "labels.text.fill",
        "stylers": {
          "color": "#38fffdff",
          "visibility": "on"
        }
      }, {
        "featureType": "boundary",
        "elementType": "geometry",
        "stylers": {
          "color": "#1387fbff"
        }
      }, {
        "featureType": "highway",
        "elementType": "all",
        "stylers": {
          "color": "#003473ff"
        }
      }, {
        "featureType": "railway",
        "elementType": "all",
        "stylers": {
          "color": "#003473ff"
        }
      }, {
        "featureType": "subway",
        "elementType": "all",
        "stylers": {
          "color": "#003473ff"
        }
      }]
    }); // map.setCurrentCity("中国");          // 设置地图显示的城市 此项是必须设置的
  
    map.enableScrollWheelZoom(true); // 开启鼠标滚轮缩放
  
    addCanvasOverlay(); // 将中国的省市区三级单独分成三个数组
  
    china.nextTier.forEach(function (v) {
      chinaPlain[0].push(v);
      v.nextTier.forEach(function (u) {
        chinaPlain[1].push(u);
        u.nextTier.forEach(function (w) {
          chinaPlain[2].push(w);
        });
      });
    });
    /**
     * 对某些地图错误的修复
     * @Author   chenht
     * @DateTime 2018-05-16
     */
  
    function errorFilter(rs, v) {
      if (v.name === '天津市') {
        rs.boundaries = rs.boundaries.filter(function (v) {
          return +v.substr(0, 3) > 116;
        });
      }
    }
  
    function getBound(boundary) {
      var path = boundary.getPath();
      return {
        top: Math.max.apply(null, path.map(function (v) {
          return v.lat;
        })),
        right: Math.max.apply(null, path.map(function (v) {
          return v.lng;
        })),
        bottom: Math.min.apply(null, path.map(function (v) {
          return v.lat;
        })),
        left: Math.min.apply(null, path.map(function (v) {
          return v.lng;
        }))
      };
    }
    /**
     * 初始化一个地区的边界
     * 同时计算这个边界的外接矩形
     * @Author   chenht
     * @DateTime 2018-05-14
     */
  
  
    function initBoundaries(v, callback) {
      return function (rs) {
        errorFilter(rs, v); // 一个地区会有多个边界(不相邻)
  
        v.boundaries = rs.boundaries.map(function (u) {
          var poly = new BMap.Polygon(u);
          poly.bound = getBound(poly);
          return poly;
        }); // 计算每条边界的上下左右的范围,计算合起来之后的最大值。
  
        v.rect = ['top', 'right', 'bottom', 'left'].map(function (u) {
          var list = [];
          v.boundaries.forEach(function (w) {
            list.push(w.bound[u]);
          });
  
          if (u === 'top' || u === 'right') {
            return Math.max.apply(0, list);
          } else {
            return Math.min.apply(0, list);
          }
        });
        callback && callback(v);
      };
    }
    /**
     * 渲染单条边界
     * 并绑定点击事件.
     * @Author   chenht
     * @DateTime 2018-05-14
     * @param    {[type]}   v     区域对象
     */
  
  
    function render(v, color) {
      color = color || "#38fffd";
  
      for (var i = 0; i < v.boundaries.length; i++) {
        var ply = new BMap.Polygon(v.boundaries[i].getPath(), {
          strokeWeight: 2,
          strokeColor: color,
          fillColor: 'rgba(0,0,0,0.2)'
        }); //建立多边形覆盖物
  
        ply.addEventListener('click', function () {
          if (map.clickTimeout) {
            clearTimeout(map.clickTimeout);
            delete map.clickTimeout;
            return;
          }
  
          map.clickTimeout = setTimeout(function () {
            var scaleList = new Array(19).join('.').split('.').map(function (v, i) {
              return 111321 / Math.pow(2, 18 - i);
            });
            map.cancleDragEvent = true;
            map.setCenter(new BMap.Point((v.rect[1] + v.rect[3]) / 2, (v.rect[0] + v.rect[2]) / 2));
            var scale = Math.min(document.getElementById(alarmMapId).offsetHeight / (v.rect[0] - v.rect[2]), document.getElementById(alarmMapId).offsetWidth / (v.rect[1] - v.rect[3]));
            var res = 0;
  
            for (var j = 0; j < scaleList.length; j++) {
              if (scale < scaleList[j] * 1.1) {
                break;
              }
  
              res = j;
            }
  
            map.setZoom(res);
            setTimeout(function () {
              if (v.tier.length === 1 && v.tier[0].nextTier[0].name === v.tier[0].name) {
                locate([v.tier[0], v.tier[0].nextTier[0]]);
              } else {
                locate(v.tier);
              }
            }, 300);
            delete map.clickTimeout;
          }, 300);
        });
        map.addOverlay(ply); //添加覆盖物
  
        overlays.push(ply);
      }
    }
    /**
     * 一次性渲染所有需要渲染的边界.
     * @Author   chenht
     * @DateTime 2018-05-14
     */
  
  
    function renderAll(filtered) {
      var min = map.pixelToPoint(new BMap.Pixel(0, document.getElementById(alarmMapId).offsetHeight));
      var max = map.pixelToPoint(new BMap.Pixel(document.getElementById(alarmMapId).offsetWidth), 0);
      var view = [max.lat, max.lng, min.lat, min.lng]; // 如果locate方法以及计算出了当前看到的区域, 且该区域低于等于区级（市或者区）.
  
      if (mapLocation && mapLocation[1]) {
        // 计算出省和市的区域对象.
        var province = china.nextTier.filter(function (v) {
          return v.name === mapLocation[0];
        })[0];
        var city = province.nextTier.filter(function (v) {
          return v.name === mapLocation[1];
        })[0];
  
        if (city && city.childrenState === 'ready') {
          // 渲染该市下的所有的区。
          city.nextTier.forEach(function (v) {
            if (!v.rect || v.rect[2] > view[0] || v.rect[0] < view[2] || v.rect[1] < view[3] || v.rect[3] > view[1]) {
              return;
            }
  
            render(v);
          });
        }
      } else if (mapLocation && mapLocation[0]) {
        // 如果只到省，那么渲染该省下的所有市。
        var province = china.nextTier.filter(function (v) {
          return v.name === mapLocation[0];
        })[0];
  
        if (province && province.childrenState === 'ready') {
          province.nextTier.forEach(function (v) {
            if (!v.rect || v.rect[2] > view[0] || v.rect[0] < view[2] || v.rect[1] < view[3] || v.rect[3] > view[1]) {
              return;
            }
  
            render(v);
          });
        }
      } // 渲染鼠标选中的区域。
  
  
      if (filtered) {
        render(filtered, '#00ff00');
      }
    }
    /**
     * 判断点是不是在边界中.
     * @Author   chenht
     * @DateTime 2018-05-14
     */
  
  
    function isPointInBoundary(point, area) {
      return area.rect && point.lat > area.rect[2] && point.lat < area.rect[0] && point.lng > area.rect[3] && point.lng < area.rect[1] && area.boundaries.some(function (u) {
        return BMapLib.GeoUtils.isPointInPolygon(point, u);
      });
    }
    /**
     * 极坐标转换成直角坐标。
     * @Author   chenht
     * @DateTime 2018-05-14
     * @param    {[type]}   rhoLevel 表示圆心开始的第几个同心圆。（或者几个单位圆）
     */
  
  
    function polarToCartesian(rhoLevel, theta) {
      var scale = 0.5;
      var maxRhoLevel = 2;
      var height = document.getElementById(alarmMapId).offsetHeight / (isResized ? 1.3 : 1);
      var width = document.getElementById(alarmMapId).offsetWidth / (isResized ? 1.3 : 1);
      var radiusMax = Math.min(height, width) / 2;
      var offsetX = rhoLevel / maxRhoLevel * radiusMax * Math.cos(theta) * scale;
      var offsetY = rhoLevel / maxRhoLevel * radiusMax * Math.sin(theta) * scale;
      return [width / 2 + offsetX, height / 2 + offsetY];
    }
    /**
     * 添加label
     * @Author   chenht
     * @DateTime 2018-05-14
     */
  
  
    function addLabel(rhoLevel, theta) {
      var pos = polarToCartesian(rhoLevel, theta);
      var label = {
        top: pos[1],
        left: pos[0]
      };
      labels.push(label);
    }
  
    function tiernameToCode(tiernames) {
      var parent = china;
      var res = '';
      tiernames.forEach(function (v) {
        var filtered = parent.nextTier.filter(function (u) {
          return u.name === v;
        })[0];
  
        if (filtered) {
          res += filtered.code;
          parent = filtered;
        }
      });
      return res;
    }
    /**
     * 计算用户当前看到的区域是什么。
     * @Author   chenht
     * @DateTime 2018-05-14
     */
  
  
    function locate(tier) {
      var i = 0; // 当每一个点的信息都获取到了之后执行judge方法
  
      function cb() {
        i++;
  
        if (i >= labels.length) {
          judge();
        }
      }
  
      ; // 根据locate方法去调用加载子集地图的方法.
  
      function autoDraw() {
        setTimeout(function () {
          var x = document.getElementById(alarmMapId).offsetWidth / 2;
          var y = document.getElementById(alarmMapId).offsetHeight / 2;
          onMapMouseMove({
            point: map.pixelToPoint(new BMap.Pixel(x, y))
          });
        }, 0);
      }
  
      function judge() {
        // 圆心的点为要判断的点。（基准点）
        var location = labels[0].data;
        var level = 2; // 判断其余的24个点有多少和圆心的相似度。
        // 依次判断2区、1市、0省是否一致。
  
        for (var i = level; i >= 0; i--) {
          // 判断24个点中有几个在2区、1市、0省上和圆心一致。
          var sameCount = labels.filter(function (v) {
            var l = v.data;
  
            for (var j = 0; j <= i; j++) {
              if (l[j] !== location[j] && l[j] !== '') {
                return false;
              }
            }
  
            return true;
          }).length; // 如果一致的超过了一半。我们可以认为他当前就是看到了这个区域。
  
          if (sameCount / labels.length > 0.49) {
            break;
          }
        } // 国外不显示
  
  
        if (labels[0].data.filter(function (x) {
          return /[a-zA-z]/.test(x) || !x;
        }).length > 0) {
          document.getElementById('location').innerText = '';
        } else if (i < 0) {
          document.getElementById('location').innerText = '中国';
          params.locateCallback();
          mapLocation = null;
        } else {
          document.getElementById('location').innerText = '中国 ' + location.slice(0, i + 1).join(' ');
          var lastLocation = mapLocation;
          mapLocation = location.slice(0, i + 1);
          params.locateCallback(tiernameToCode(mapLocation)); // 如果与前一次不同则加载地图.
  
          if ([0, 1, 2].some(function (v) {
            return !lastLocation || mapLocation[v] !== lastLocation[v];
          })) {
            autoDraw();
          }
        }
      }
  
      ; // 如果参数中传来了tier（即鼠标点击选中了某个区域）
      // 则不计算，直接将区域设置为该值。
  
      if (tier && Array.isArray(tier)) {
        document.getElementById('location').innerText = '中国 ' + tier.map(function (v) {
          return v.name;
        }).join(' ');
        mapLocation = tier.map(function (v) {
          return v.name;
        });
        params.locateCallback(tiernameToCode(mapLocation));
        autoDraw();
        return;
      } // 遍历25个label，采集对应点的坐标信息。
  
  
      labels.forEach(function (v) {
        var point = new BMap.Pixel(v.left, v.top);
        point = map.pixelToPoint(point); // 该方法返回某个坐标点的详细地理层级信息。
  
        geoc.getLocation(point, function (res) {
          var addComp = res.addressComponents;
          v.data = [addComp.province, addComp.city, addComp.district];
          cb();
        });
      });
    }
  
    function locateByTierCode(tierCode) {
      if (!tierCode) {
        return;
      }
  
      map.cancleDragEvent = true;
      var tiers = [tierCode.substr(0, 2), tierCode.substr(2, 2), tierCode.substr(4, 2)];
      var list = china.nextTier;
      var fullName = '';
      var tierList = [];
      tiers = tiers.map(function (v) {
        if (v) {
          var res = list.filter(function (u) {
            return u.code === v;
          })[0];
          list = res.nextTier;
          res = Object.assign({}, res);
          fullName += res.name;
          tierList.push(res);
          res.fullName = fullName;
          res.tier = Object.assign([], tierList);
        }
  
        return res;
      });
      locate(tiers.filter(function (v) {
        return v;
      }));
    } // 对省级地区初始化。
  
  
    china.nextTier.forEach(function (v, i) {
      bdary.get(v.name, initBoundaries(v));
    });
  
    function onMapMouseMove(e) {
      if (isResized) {
        var origin = map.pixelToPoint({
          x: 0,
          y: 0
        });
        var multiX = 1.3;
        var multiY = 1.35;
        e.point.lat = origin.lat + (e.point.lat - origin.lat) / multiY;
        e.point.lng = origin.lng + (e.point.lng - origin.lng) / multiX;
        e.offsetX /= multiX;
        e.offsetY /= multiY;
      } // console.log(e.point, e.offsetX);
  
  
      var tier = []; // 先判断鼠标是不是落在某个省之中。
  
      var filtered = china.nextTier.filter(function (v) {
        return isPointInBoundary(e.point, v);
      })[0];
  
      if (filtered) {
        filtered.fullName = filtered.name;
        filtered.tier = [filtered];
        tier.push(filtered);
      }
      /* 接下来判断鼠标是不是落在某个市中） */
  
  
      if (tier[0] && mapLocation && mapLocation[0] === filtered.name) {
        // 如果这个省的市区域都加载完成了
        if (filtered.childrenState === 'ready') {
          // 判断鼠标落在哪个市。
          var cityFiltered = filtered.nextTier.filter(function (v) {
            return isPointInBoundary(e.point, v);
          })[0];
  
          if (cityFiltered) {
            cityFiltered.fullName = tier[0].name + cityFiltered.name;
            cityFiltered.tier = [tier[0], cityFiltered];
            filtered = cityFiltered;
            tier.push(cityFiltered);
          }
        } else if (filtered.childrenState !== 'loading') {
          // 如果没有加载过，就开始加载
          filtered.childrenState = 'loading';
          var readyCount = 0; // 依次初始化该省下的市。
  
          filtered.nextTier.forEach(function (v) {
            bdary.get(filtered.name + v.name, initBoundaries(v, function () {
              if (++readyCount >= filtered.nextTier.length) {
                filtered.childrenState = 'ready';
                renderAll(filtered);
              }
            }));
          });
        }
      }
      /* 区级操作， 基本和市的操作一样。 */
  
  
      if (tier[1] && mapLocation && mapLocation[1] === filtered.name) {
        if (filtered.childrenState === 'ready') {
          var districtFiltered = filtered.nextTier.filter(function (v) {
            return isPointInBoundary(e.point, v);
          })[0];
  
          if (districtFiltered) {
            districtFiltered.fullName = tier[0].name + tier[1].name + districtFiltered.name;
            districtFiltered.tier = [tier[0], tier[1], districtFiltered];
            filtered = districtFiltered;
            tier.push(districtFiltered);
          }
        } else if (filtered.childrenState !== 'loading') {
          filtered.childrenState = 'loading';
          var readyCount = 0;
          filtered.nextTier.forEach(function (v) {
            /* 过滤市辖区， 因为市辖区没有地理位置。 */
            if (v.name === '市辖区') {
              readyCount++;
              return;
            }
  
            var name = tier[0].name + tier[1].name + v.name;
            bdary.get(DistrictMapper[name] || name, initBoundaries(v, function () {
              if (++readyCount >= filtered.nextTier.length) {
                filtered.childrenState = 'ready';
                renderAll(filtered);
              }
            }));
          });
        }
      }
  
      var isSameToLast = true;
  
      for (var k = 0; k < 3; k++) {
        if (tier[k] !== lastTier[k]) {
          isSameToLast = false;
        }
      }
  
      lastTier = tier; // 如果结果和上一次一样则不重新渲染。
  
      if (!isSameToLast) {
        // map.clearOverlays()
        overlays.forEach(function (v) {
          v.remove();
        });
        overlays = [];
        renderAll(filtered);
      }
    }
  
    map.addEventListener('mousemove', onMapMouseMove);
    map.addEventListener('dragend', locate);
    /**
     * 定义鼠标缩放之后重新执行locate方法。
     * @Author   chenht
     * @DateTime 2018-05-14
     */
    // var wheelTimeout;
    // document.getElementById(alarmMapId).addEventListener('mousewheel', function () {
    //     if (wheelTimeout) {
    //         clearTimeout(wheelTimeout)
    //     }
    //     wheelTimeout = setTimeout(function() {
    //         locate()
    //     }, 1000);
    // })
  
    map.addEventListener('zoomend', function () {
      if (map.cancleDragEvent) {
        map.cancleDragEvent = false;
        return;
      }
  
      locate();
    }); // 依次添加label用来采集地图上的坐标对应的位置信息
    // 分布是圆心、一级同心圆（平均八份）、二级同心圆（平均十六份）
  
    addLabel(0, 0);
  
    for (var i = 0; i < 8; i++) {
      addLabel(1, i / 8 * Math.PI * 2);
    }
  
    for (i = 0; i < 16; i++) {
      addLabel(2, i / 16 * Math.PI * 2);
    }
  
    map.locateByTierCode = locateByTierCode;
  
    function addMask() {
      /*画遮蔽层的相关方法
      *思路: 首先在中国地图最外画一圈，圈住理论上所有的中国领土，然后再将每个闭合区域合并进来，并全部连到西北角。
      *      这样就做出了一个经过多次西北角的闭合多边形*/
      //定义中国东南西北端点，作为第一层
      //向数组中添加一次闭合多边形，并将西北角再加一次作为之后画闭合区域的起点
      var pStart = new BMap.Point(180, 90);
      var pEnd = new BMap.Point(0, -90);
      var pArray = [new BMap.Point(pStart.lng, pStart.lat), new BMap.Point(pEnd.lng, pStart.lat), new BMap.Point(pEnd.lng, pEnd.lat), new BMap.Point(pStart.lng, pEnd.lat)];
      [[135.077218, 48.544352], [134.92218, 48.584352], [134.827218, 48.534352], [134.727669, 48.495377], [134.304531, 48.394091], [133.513447, 48.177476], [132.832747, 48.054205], [132.519993, 47.789172], [131.765704, 47.813962], [131.103402, 47.776772], [130.919429, 48.331824], [130.77225, 48.868729], [129.907577, 49.351849], [128.73015, 49.699156], [127.791888, 49.85404], [127.791888, 50.492084], [126.927215, 51.616759], [126.467283, 52.579818], [125.952158, 53.059077], [124.701142, 53.313247], [123.56051, 53.664362], [121.555204, 53.46722], [120.340983, 53.125528], [119.95464, 52.579818], [120.616942, 52.523746], [120.506559, 52.095236], [119.862653, 51.616759], [119.365926, 50.959196], [119.089967, 50.362806], [119.108364, 50.05583], [118.133307, 49.925357], [117.471005, 49.794528], [116.808702, 49.889712], [116.385564, 49.758785], [115.962426, 48.953617], [115.520891, 48.147476], [115.796851, 47.677465], [116.27518, 47.652609], [117.103059, 47.652609], [118.004526, 47.801568], [118.887596, 47.577968], [119.402721, 47.127871], [119.402721, 46.800397], [118.464459, 46.825659], [117.103059, 46.648575], [115.980824, 46.088213], [115.226534, 45.702829], [114.159491, 45.275796], [112.761297, 45.171782], [111.639061, 45.132727], [111.436691, 44.55683], [111.51028, 44.001703], [110.682402, 43.387647], [108.897864, 42.658724], [106.892559, 42.522781], [103.82021, 42.140555], [102.422016, 42.536389], [101.336575, 42.82146], [99.478448, 42.929712], [97.601924, 42.997272], [96.019756, 43.815487], [92.72664, 45.288784], [91.144473, 45.599605], [91.457227, 46.483616], [90.794924, 47.553064], [89.562305, 48.221295], [88.2377, 48.953617], [87.722576, 49.279683], [87.097067, 49.255604], [86.60034, 49.122957], [86.177203, 48.710696], [85.533297, 48.344091], [85.404516, 47.875888], [85.349324, 47.390897], [84.926186, 47.215692], [83.233635, 47.315881], [82.865689, 47.328391], [82.258578, 45.844449], [82.368962, 45.366651], [82.093003, 45.30177], [80.989165, 45.275796], [79.903724, 45.015402], [80.326862, 44.332772], [80.510835, 43.642047], [80.621219, 43.186043], [80.27167, 43.010775], [79.885327, 42.304653], [79.259819, 41.838593], [78.487133, 41.576647], [77.916816, 41.341363], [77.272911, 41.16086], [76.739389, 41.02167], [76.26106, 40.546202], [75.672346, 40.75639], [74.881262, 40.630357], [74.255754, 40.293095], [73.777425, 39.939968], [73.74063, 39.556517], [73.53826, 39.34256], [73.685438, 38.725549], [74.034987, 38.407771], [74.458125, 38.335352], [74.734084, 38.074036], [74.844468, 37.577865], [74.678892, 37.21089], [74.6237, 36.975076], [75.414784, 36.501232], [75.801127, 35.934721], [76.518622, 35.379154], [77.309706, 35.137703], [77.972008, 34.758986], [78.376749, 34.241106], [78.523927, 33.473647], [78.7079, 32.978834], [78.450338, 32.745921], [78.30316, 32.340745], [78.431941, 32.04349], [78.671106, 31.572152], [78.855079, 31.145879], [79.425395, 30.797108], [80.087697, 30.447053], [81.301919, 29.855455], [81.90903, 30.0157], [82.7921, 29.485907], [84.539843, 28.661613], [85.71727, 28.124721], [86.821108, 27.732537], [87.998535, 27.69979], [88.568851, 27.716165], [88.863208, 27.108656], [89.580703, 27.190949], [89.654292, 27.765274], [90.923705, 27.650651], [91.751584, 27.223849], [92.04594, 26.778874], [92.965805, 26.646689], [93.830478, 26.960375], [94.860727, 27.453873], [96.185332, 27.798001], [97.123594, 27.503101], [97.620321, 27.896122], [97.675513, 28.059457], [98.080254, 27.306056], [98.595378, 27.009824], [98.393008, 26.066566], [97.804294, 25.483523], [97.528335, 24.847254], [97.417951, 24.10637], [97.804294, 23.717348], [98.595378, 23.886634], [98.834543, 23.123105], [99.239283, 22.697005], [99.165694, 22.303805], [99.386462, 21.857966], [100.251135, 21.445169], [100.839848, 21.290063], [101.704521, 21.031186], [102.05407, 21.152053], [101.998878, 21.582901], [101.962083, 22.132497], [102.587591, 22.355156], [103.599443, 22.338041], [104.482513, 22.560368], [105.383981, 22.799392], [106.083078, 22.59454], [106.469421, 22.286683], [106.874162, 21.754879], [107.315697, 21.514051], [107.812424, 21.410715], [107.775629, 21.134792], [106.929353, 20.269201], [106.175064, 19.17158], [106.377435, 18.470789], [107.297299, 17.23746], [109.008248, 15.675143], [109.688948, 13.705222], [109.652153, 11.664031], [108.750686, 9.571001], [108.198767, 6.876803], [108.493124, 5.090099], [109.817729, 3.612656], [111.10554, 3.298351], [114.71141, 5.514272], [116.256783, 7.556636], [118.758815, 10.883133], [119.531502, 13.669242], [119.494707, 16.617614], [120.414572, 18.961654], [121.51841, 20.633358], [122.751029, 22.303805], [123.247756, 23.378111], [124.811526, 25.68375], [126.577667, 25.900278], [127.479134, 26.67975], [128.454191, 28.189945], [128.766945, 29.93561], [128.73015, 31.650877], [127.957464, 32.153119], [127.221572, 32.745921], [127.019202, 33.596907], [125.988953, 33.827543], [125.731391, 34.546135], [125.878569, 35.454458], [125.731391, 36.634799], [125.80498, 37.51927], [124.425183, 37.972159], [124.498772, 38.58128], [125.013896, 39.242487], [124.590758, 39.471014], [124.296402, 39.840762], [124.388388, 40.081441], [124.940307, 40.335346], [125.731391, 40.630357], [126.448885, 40.96591], [126.798434, 41.493704], [127.111188, 41.410654], [127.883875, 41.271998], [128.490985, 41.452192], [128.307012, 41.879854], [128.950918, 41.921089], [129.484439, 42.12686], [129.999564, 42.549994], [130.073153, 42.807915], [130.404304, 42.495557], [130.77225, 42.359256], [130.698661, 42.726583], [131.195388, 42.848541], [131.360964, 43.494895], [131.342566, 44.491021], [131.820896, 45.002351], [132.998323, 44.976239], [133.623831, 45.599605], [134.102161, 46.394582], [134.37812, 47.228226], [134.874847, 47.851127], [134.985231, 48.233588], [135.13241, 48.454352], [135.077218, 48.474352]].forEach(function (v) {
        pArray.push(new BMap.Point(v[0], v[1]));
      }); // 循环添加各闭合区域
      //添加遮蔽层
  
      var plyall = new BMap.Polygon(pArray, {
        strokeOpacity: 1,
        strokeColor: "transparent",
        strokeWeight: 0,
        fillColor: "#001427ff",
        fillOpacity: 0.8
      }); // 建立多边形覆盖物
  
      map.addOverlay(plyall);
      pStart = new BMap.Point(180, 90);
      pEnd = new BMap.Point(0, -90);
      pArray = [new BMap.Point(135.077218, 48.454352), new BMap.Point(pStart.lng, pStart.lat), new BMap.Point(pStart.lng, pEnd.lat), new BMap.Point(135.077218, 48.454352)];
      var sanjiaoxing = new BMap.Polygon(pArray, {
        strokeOpacity: 1,
        strokeColor: "transparent",
        strokeWeight: 0,
        fillColor: "#001427ff",
        fillOpacity: 0.8
      }); // 建立多边形覆盖物
  
      map.addOverlay(sanjiaoxing);
    }
  
    function addCanvasOverlay() {
      function CanvasOverlay(data) {
        this._data = data;
      }
  
      CanvasOverlay.prototype = new BMap.Overlay();
  
      CanvasOverlay.prototype.initialize = function (map) {
        this._map = map;
        var canvas = document.createElement('canvas');
        var ctx = canvas.getContext('2d');
        canvas.width = 1920;
        canvas.height = 1080;
        canvas.style = "position: absolute; top: -100px; left: -100px;pointer-events: none;";
        map.getPanes().labelPane.appendChild(canvas);
        this.canvas = canvas;
        this.ctx = ctx;
        return canvas;
      };
  
      CanvasOverlay.prototype.draw = function () {
        var map = this._map;
        var ne = map.getBounds().getNorthEast();
        var sw = map.getBounds().getSouthWest();
        var pos = map.pointToOverlayPixel(new BMap.Point(sw.lng, ne.lat));
        pos.x -= 100;
        pos.y -= 100;
        this.canvas.pos = pos;
        this.canvas.style = "position: absolute; left: " + pos.x + "px; top: " + pos.y + "px;pointer-events: none;";
        this.ctx.clearRect(0, 0, 1920, 1080);
        this.ctx.fillStyle = "rgba(255,0,0,1)";
        this.ctx.beginPath();
        chinaGeoJson.forEach(function (v) {
          v.forEach(function (u, i) {
            var pos = map.pointToOverlayPixel(new BMap.Point(u[0], u[1]));
            this.ctx[i === 0 ? 'moveTo' : 'lineTo'](pos.x - this.canvas.pos.x, pos.y - this.canvas.pos.y);
          }.bind(this));
        }.bind(this)); // this.ctx.endPath();
  
        this.ctx.fill();
        this.ctx.globalCompositeOperation = 'source-out';
        this.ctx.fillStyle = "rgba(0,20,39,0.8)";
        this.ctx.strokeStyle = "#1387fb";
        this.ctx.lineWidth = 3;
        this.ctx.fillRect(0, 0, 1920, 1080);
        this.ctx.globalCompositeOperation = 'source-over'; // this.ctx.stroke();
      };
  
      var canvasOverlay = new CanvasOverlay();
      map.addOverlay(canvasOverlay);
      setTimeout(function () {
        canvasOverlay.draw();
        canvasOverlay.canvas.parentNode.parentNode.append(canvasOverlay.canvas);
      }, 10);
      map.canvasOverlay = canvasOverlay;
    } // addMask();
  
    /**
     * 测试boundary的bound.
     */
    // setTimeout(function() {
    //     china.nextTier.forEach((v, i) => {
    //         setTimeout(function() {
    //             map.clearOverlays()
    //             var boundary = [
    //                 new BMap.Point(v.rect[3], v.rect[0]),
    //                 new BMap.Point(v.rect[1], v.rect[0]), 
    //                 new BMap.Point(v.rect[1], v.rect[2]),
    //                 new BMap.Point(v.rect[3], v.rect[2])
    //             ];
    //             var ply = new BMap.Polygon(boundary, {strokeWeight: 2, strokeColor: 'red', fillColor: 'rgba(0,0,0,0.2)'}); //建立多边形覆盖物
    //             map.addOverlay(ply)
    //         }, i * 1000);
    //     })
    // }, 5000);
  
  
    return map;
  }