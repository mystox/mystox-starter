var BMapLib=window.BMapLib=BMapLib||{};!function(){var d,t,u=d=u||{version:"1.3.8"};u.guid="$BAIDU$",window[u.guid]=window[u.guid]||{},u.dom=u.dom||{},u.dom.g=function(t){return"string"==typeof t||t instanceof String?document.getElementById(t):t&&t.nodeName&&(1==t.nodeType||9==t.nodeType)?t:null},u.g=u.G=u.dom.g,u.dom.getDocument=function(t){return 9==(t=u.dom.g(t)).nodeType?t:t.ownerDocument||t.document},u.lang=u.lang||{},u.lang.isString=function(t){return"[object String]"==Object.prototype.toString.call(t)},u.isString=u.lang.isString,u.dom._g=function(t){return u.lang.isString(t)?document.getElementById(t):t},u._g=u.dom._g,u.browser=u.browser||{},/msie (\d+\.\d)/i.test(navigator.userAgent)&&(u.browser.ie=u.ie=document.documentMode||+RegExp.$1),u.dom.getComputedStyle=function(t,e){t=u.dom._g(t);var i,n=u.dom.getDocument(t);return n.defaultView&&n.defaultView.getComputedStyle&&(i=n.defaultView.getComputedStyle(t,null))?i[e]||i.getPropertyValue(e):""},u.dom._styleFixer=u.dom._styleFixer||{},u.dom._styleFilter=u.dom._styleFilter||[],u.dom._styleFilter.filter=function(t,e,i){for(var n,o=0,s=u.dom._styleFilter;n=s[o];o++)(n=n[i])&&(e=n(t,e));return e},u.string=u.string||{},u.string.toCamelCase=function(t){return t.indexOf("-")<0&&t.indexOf("_")<0?t:t.replace(/[-_][^-_]/g,function(t){return t.charAt(1).toUpperCase()})},u.dom.getStyle=function(t,e){var i=u.dom;t=i.g(t),e=u.string.toCamelCase(e);var n=t.style[e]||(t.currentStyle?t.currentStyle[e]:"")||i.getComputedStyle(t,e);if(!n){var o=i._styleFixer[e];o&&(n=o.get?o.get(t):u.dom.getStyle(t,o))}return(o=i._styleFilter)&&(n=o.filter(e,n,"get")),n},u.getStyle=u.dom.getStyle,/opera\/(\d+\.\d)/i.test(navigator.userAgent)&&(u.browser.opera=+RegExp.$1),u.browser.isWebkit=/webkit/i.test(navigator.userAgent),u.browser.isGecko=/gecko/i.test(navigator.userAgent)&&!/like gecko/i.test(navigator.userAgent),u.browser.isStrict="CSS1Compat"==document.compatMode,u.dom.getPosition=function(t){t=u.dom.g(t);var e,i,n=u.dom.getDocument(t),o=u.browser,s=u.dom.getStyle,r=(0<o.isGecko&&n.getBoxObjectFor&&"absolute"==s(t,"position")&&(""===t.style.top||t.style.left),{left:0,top:0});if(t==(o.ie&&!o.isStrict?n.body:n.documentElement))return r;if(t.getBoundingClientRect){i=t.getBoundingClientRect(),r.left=Math.floor(i.left)+Math.max(n.documentElement.scrollLeft,n.body.scrollLeft),r.top=Math.floor(i.top)+Math.max(n.documentElement.scrollTop,n.body.scrollTop),r.left-=n.documentElement.clientLeft,r.top-=n.documentElement.clientTop;var l=n.body,a=parseInt(s(l,"borderLeftWidth")),p=parseInt(s(l,"borderTopWidth"));o.ie&&!o.isStrict&&(r.left-=isNaN(a)?2:a,r.top-=isNaN(p)?2:p)}else{e=t;do{if(r.left+=e.offsetLeft,r.top+=e.offsetTop,0<o.isWebkit&&"fixed"==s(e,"position")){r.left+=n.body.scrollLeft,r.top+=n.body.scrollTop;break}e=e.offsetParent}while(e&&e!=t);for((0<o.opera||0<o.isWebkit&&"absolute"==s(t,"position"))&&(r.top-=n.body.offsetTop),e=t.offsetParent;e&&e!=n.body;)r.left-=e.scrollLeft,o.opera&&"TR"==e.tagName||(r.top-=e.scrollTop),e=e.offsetParent}return r},u.event=u.event||{},u.event._listeners=u.event._listeners||[],u.event.on=function(e,t,i){t=t.replace(/^on/i,""),e=u.dom._g(e);var n,o=function(t){i.call(e,t)},s=u.event._listeners,r=u.event._eventFilter,l=t;return t=t.toLowerCase(),r&&r[t]&&(l=(n=r[t](e,t,o)).type,o=n.listener),e.addEventListener?e.addEventListener(l,o,!1):e.attachEvent&&e.attachEvent("on"+l,o),s[s.length]=[e,t,i,o,l],e},u.on=u.event.on,t=window[u.guid],u.lang.guid=function(){return"TANGRAM__"+(t._counter++).toString(36)},t._counter=t._counter||1,window[u.guid]._instances=window[u.guid]._instances||{},u.lang.isFunction=function(t){return"[object Function]"==Object.prototype.toString.call(t)},u.lang.Class=function(t){this.guid=t||u.lang.guid(),window[u.guid]._instances[this.guid]=this},window[u.guid]._instances=window[u.guid]._instances||{},u.lang.Class.prototype.dispose=function(){for(var t in delete window[u.guid]._instances[this.guid],this)u.lang.isFunction(this[t])||delete this[t];this.disposed=!0},u.lang.Class.prototype.toString=function(){return"[object "+(this._className||"Object")+"]"},u.lang.Event=function(t,e){this.type=t,this.returnValue=!0,this.target=e||null,this.currentTarget=null},u.lang.Class.prototype.addEventListener=function(t,e,i){if(u.lang.isFunction(e)){this.__listeners||(this.__listeners={});var n,o=this.__listeners;if("string"==typeof i&&i){if(/[^\w\-]/.test(i))throw"nonstandard key:"+i;n=e.hashCode=i}0!=t.indexOf("on")&&(t="on"+t),"object"!=typeof o[t]&&(o[t]={}),n=n||u.lang.guid(),e.hashCode=n,o[t][n]=e}},u.lang.Class.prototype.removeEventListener=function(t,e){if(void 0===e||(!u.lang.isFunction(e)||(e=e.hashCode))&&u.lang.isString(e)){this.__listeners||(this.__listeners={}),0!=t.indexOf("on")&&(t="on"+t);var i=this.__listeners;if(i[t])if(void 0!==e)i[t][e]&&delete i[t][e];else for(var n in i[t])delete i[t][n]}},u.lang.Class.prototype.dispatchEvent=function(t,e){for(var i in u.lang.isString(t)&&(t=new u.lang.Event(t)),this.__listeners||(this.__listeners={}),e=e||{})t[i]=e[i];var n=this.__listeners,o=t.type;if(t.target=t.target||this,t.currentTarget=this,0!=o.indexOf("on")&&(o="on"+o),u.lang.isFunction(this[o])&&this[o].apply(this,arguments),"object"==typeof n[o])for(i in n[o])n[o][i].apply(this,arguments);return t.returnValue},u.lang.inherits=function(t,e,i){var n,o,s=t.prototype,r=new Function;for(n in r.prototype=e.prototype,o=t.prototype=new r,s)o[n]=s[n];(t.prototype.constructor=t).superClass=e.prototype,"string"==typeof i&&(o._className=i)},u.inherits=u.lang.inherits;var e=BMapLib.TextIconOverlay=function(t,e,i){this._position=t,this._text=e,this._options=i||{},this._styles=this._options.styles||[],this._styles.length||this._setupDefaultStyles()};d.lang.inherits(e,BMap.Overlay,"TextIconOverlay"),e.prototype._setupDefaultStyles=function(){for(var t,e=[53,56,66,78,90],i=0;t=e[i];i++)this._styles.push({url:"http://api.map.baidu.com/library/TextIconOverlay/1.2/src/images/m"+i+".png",size:new BMap.Size(t,t)})},e.prototype.initialize=function(t){return this._map=t,this._domElement=document.createElement("div"),this._updateCss(),this._updateText(),this._updatePosition(),this._bind(),this._map.getPanes().markerMouseTarget.appendChild(this._domElement),this._domElement},e.prototype.draw=function(){this._map&&this._updatePosition()},e.prototype.getText=function(){return this._text},e.prototype.setText=function(t,e){!t||this._text&&this._text.toString()==t.toString()||(this._text=t,this._level=e,this._updateText(),this._updateCss(),this._updatePosition())},e.prototype.getPosition=function(){return this._position},e.prototype.setPosition=function(t){!t||this._position&&this._position.equals(t)||(this._position=t,this._updatePosition())},e.prototype.getStyleByText=function(t,e,i){var n=parseInt(t),o=parseInt(n/10);return o=Math.max(0,o),e[(o=Math.min(o,e.length/4-1))+5*i]||{}},e.prototype._updateCss=function(){var t=0|this._level,e=this.getStyleByText(this._text,this._styles,t);this._domElement.style.cssText=this._buildCssText(e,0<this._text),this._domElement.className="alarm-icon level"+t},e.prototype._updateText=function(){this._domElement&&(this._domElement.innerHTML=this._text)},e.prototype._updatePosition=function(){if(this._domElement&&this._position){var t=this._domElement.style,e=this._map.pointToOverlayPixel(this._position);e.x-=Math.ceil(parseInt(t.width)/2),e.y-=Math.ceil(parseInt(t.height)/2),t.left=e.x+"px",t.top=e.y+"px"}},e.prototype._buildCssText=function(t,e){var i=t.url,n=t.size,o=t.anchor,s=t.offset,r=t.textColor||"black",l=t.textSize||10,a=[];if(d.browser.ie<7)a.push('filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod=scale,src="'+i+'");');else{a.push("background-image:url("+i+");");var p="0 0";s instanceof BMap.Size&&(p=s.width+"px "+s.height+"px"),a.push("background-position:"+p+";")}return n instanceof BMap.Size&&(o instanceof BMap.Size?(0<o.height&&o.height<n.height&&a.push("height:"+(n.height-o.height)+"px; padding-top:"+o.height+"px;"),0<o.width&&o.width<n.width&&a.push("width:"+(n.width-o.width)+"px; padding-left:"+o.width+"px;")):(a.push("height:"+n.height+"px; line-height:"+n.height+"px;"),a.push("width:"+n.width+"px; text-align:center;"))),a.push("cursor:pointer; color:"+r+"; position:absolute; font-size:"+l+"px; font-family:Arial,sans-serif; font-weight:bold;"),e&&a.push("pointer-events:none;"),a.join("")},e.prototype._bind=function(){if(this._domElement){var e=this,r=this._map,i=d.lang.Event;d.event.on(this._domElement,"mouseover",function(t){e.dispatchEvent(n(t,new i("onmouseover")))}),d.event.on(this._domElement,"mouseout",function(t){e.dispatchEvent(n(t,new i("onmouseout")))}),d.event.on(this._domElement,"click",function(t){e.dispatchEvent(n(t,new i("onclick")))})}function n(t,e){var i=t.srcElement||t.target,n=t.clientX||t.pageX,o=t.clientY||t.pageY;if(t&&e&&n&&o&&i){var s=d.dom.getPosition(r.getContainer());e.pixel=new BMap.Pixel(n-s.left,o-s.top),e.point=r.pixelToPoint(e.pixel)}return e}}}();