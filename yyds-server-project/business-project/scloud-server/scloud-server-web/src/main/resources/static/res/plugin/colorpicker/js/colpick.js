!function(s){var t,a,c,o,l,r,n,d,p,h,u,f,e=(t={showEvent:"click",onShow:function(){},onBeforeShow:function(){},onHide:function(){},onChange:function(){},onSubmit:function(){},colorScheme:"light",color:"3289c7",livePreview:!0,flat:!1,layout:"full",submit:1,submitText:"OK",height:156,hsl:!1},a=function(t){return t.data.field.val(Math.max(0,Math.min(t.data.max,parseInt(t.data.val-t.pageY+t.data.y,10)))),t.data.preview&&y.apply(t.data.field.get(0),[!0]),!1},c=function(t){return y.apply(t.data.field.get(0),[!0]),t.data.el.removeClass("colpick_slider").find("input").focus(),s(document).off("mouseup",c),s(document).off("mousemove",a),!1},o=function(t){var e="touchmove"==t.type?t.originalEvent.changedTouches[0].pageY:t.pageY;return y.apply(t.data.cal.data("colpick").fields.eq(4).val(parseInt(360*(t.data.cal.data("colpick").height-Math.max(0,Math.min(t.data.cal.data("colpick").height,e-t.data.y)))/t.data.cal.data("colpick").height,10)).get(0),[t.data.preview]),!1},l=function(t){return s(document).off("mouseup touchend",l),s(document).off("mousemove touchmove",o),!1},r=function(t){var e;return e="touchmove"==t.type?(pageX=t.originalEvent.changedTouches[0].pageX,t.originalEvent.changedTouches[0].pageY):(pageX=t.pageX,t.pageY),y.apply(t.data.cal.data("colpick").fields.eq(6).val(parseInt(100*(t.data.cal.data("colpick").height-Math.max(0,Math.min(t.data.cal.data("colpick").height,e-t.data.pos.top)))/t.data.cal.data("colpick").height,10)).end().eq(5).val(parseInt(100*Math.max(0,Math.min(t.data.cal.data("colpick").height,pageX-t.data.pos.left))/t.data.cal.data("colpick").height,10)).get(0),[t.data.preview]),!1},n=function(t){return s(document).off("mouseup touchend",n),s(document).off("mousemove touchmove",r),!1},d=function(t){0!=t.data.cal.data("colpick").onHide.apply(this,[t.data.cal.get(0)])&&t.data.cal.hide(),s("html").off("mousedown",d)},p=function(){var t="CSS1Compat"==document.compatMode;return{l:window.pageXOffset||(t?document.documentElement.scrollLeft:document.body.scrollLeft),w:window.innerWidth||(t?document.documentElement.clientWidth:document.body.clientWidth)}},h=function(t){return{h:Math.min(360,Math.max(0,t.h)),s:Math.min(100,Math.max(0,t.s)),x:Math.min(100,Math.max(0,t.x))}},u=function(t){return{r:Math.min(255,Math.max(0,t.r)),g:Math.min(255,Math.max(0,t.g)),b:Math.min(255,Math.max(0,t.b))}},f=function(t){var e=6-t.length;if(0<e){for(var i=[],a=0;a<e;a++)i.push("0");i.push(t),t=i.join("")}return t},{init:function(d){if("string"==typeof(d=s.extend({},t,d||{})).color)d.color=d.hsl?Y(d.color):P(d.color);else if(null!=d.color.r&&null!=d.color.g&&null!=d.color.b)d.color=d.hsl?z(d.color):N(d.color);else{if(null==d.color.h||null==d.color.s||null==d.color.b)return this;d.color=d.hsl?fixHsl(d.color):fixHsb(d.color)}return this.each(function(){if(!s(this).data("colpickId")){var t=s.extend({},d);t.origColor=d.color;var e="collorpicker_"+parseInt(1e3*Math.random());s(this).data("colpickId",e);var i=s('<div class="colpick"><div class="colpick_color"><div class="colpick_color_overlay1"><div class="colpick_color_overlay2"><div class="colpick_selector_outer"><div class="colpick_selector_inner"></div></div></div></div></div><div class="colpick_hue"><div class="colpick_hue_arrs"><div class="colpick_hue_larr"></div><div class="colpick_hue_rarr"></div></div></div><div class="colpick_new_color"></div><div class="colpick_current_color"></div><div class="colpick_hex_field"><div class="colpick_field_letter">#</div><input type="text" maxlength="6" size="6" /></div><div class="colpick_rgb_r colpick_field"><div class="colpick_field_letter">R</div><input type="text" maxlength="3" size="3" /><div class="colpick_field_arrs"><div class="colpick_field_uarr"></div><div class="colpick_field_darr"></div></div></div><div class="colpick_rgb_g colpick_field"><div class="colpick_field_letter">G</div><input type="text" maxlength="3" size="3" /><div class="colpick_field_arrs"><div class="colpick_field_uarr"></div><div class="colpick_field_darr"></div></div></div><div class="colpick_rgb_b colpick_field"><div class="colpick_field_letter">B</div><input type="text" maxlength="3" size="3" /><div class="colpick_field_arrs"><div class="colpick_field_uarr"></div><div class="colpick_field_darr"></div></div></div><div class="colpick_hsx_h colpick_field"><div class="colpick_field_letter">H</div><input type="text" maxlength="3" size="3" /><div class="colpick_field_arrs"><div class="colpick_field_uarr"></div><div class="colpick_field_darr"></div></div></div><div class="colpick_hsx_s colpick_field"><div class="colpick_field_letter">S</div><input type="text" maxlength="3" size="3" /><div class="colpick_field_arrs"><div class="colpick_field_uarr"></div><div class="colpick_field_darr"></div></div></div><div class="colpick_hsx_x colpick_field"><div class="colpick_field_letter">B</div><input type="text" maxlength="3" size="3" /><div class="colpick_field_arrs"><div class="colpick_field_uarr"></div><div class="colpick_field_darr"></div></div></div><div class="colpick_submit"></div></div>').attr("id",e);i.addClass("colpick_"+t.layout+(t.submit?"":" colpick_"+t.layout+"_ns")),"light"!=t.colorScheme&&i.addClass("colpick_"+t.colorScheme),t.hsl&&i.addClass("colpick_hsl"),i.find("div.colpick_submit").html(t.submitText).click(q),t.fields=i.find("input").change(y).blur(w).focus(M),i.find("div.colpick_field_arrs").mousedown(C).end().find("div.colpick_current_color").click(S),t.selector=i.find("div.colpick_color").on("mousedown touchstart",T),t.selectorIndic=t.selector.find("div.colpick_selector_outer"),t.el=this,t.hue=i.find("div.colpick_hue_arrs"),huebar=t.hue.parent();var a,c,o=navigator.userAgent.toLowerCase(),l="Microsoft Internet Explorer"===navigator.appName,r=l?parseFloat(o.match(/msie ([0-9]{1,}[\.0-9]{0,})/)[1]):0,n=["#ff0000","#ff0080","#ff00ff","#8000ff","#0000ff","#0080ff","#00ffff","#00ff80","#00ff00","#80ff00","#ffff00","#ff8000","#ff0000"];if(l&&r<10)for(a=0;a<=11;a++)c=s("<div></div>").attr("style","height:8.333333%; filter:progid:DXImageTransform.Microsoft.gradient(GradientType=0,startColorstr="+n[a]+", endColorstr="+n[a+1]+'); -ms-filter: "progid:DXImageTransform.Microsoft.gradient(GradientType=0,startColorstr='+n[a]+", endColorstr="+n[a+1]+')";'),huebar.append(c);else stopList=n.join(","),huebar.attr("style","background:-webkit-linear-gradient(top center,"+stopList+"); background:-moz-linear-gradient(top center,"+stopList+"); background:linear-gradient(to bottom,"+stopList+"); ");i.find("div.colpick_hue").on("mousedown touchstart",I),t.newColor=i.find("div.colpick_new_color"),t.currentColor=i.find("div.colpick_current_color"),i.data("colpick",t),v(t.color,i.get(0)),g(t.color,i.get(0)),k(t.color,i.get(0)),m(t.color,i.get(0)),_(t.color,i.get(0)),x(t.color,i.get(0)),b(t.color,i.get(0)),t.flat?(i.appendTo(this).show(),i.css({position:"relative",display:"block"})):(i.appendTo(document.body),s(this).on(t.showEvent,H),i.css({position:"absolute"}))}})},showPicker:function(){return this.each(function(){s(this).data("colpickId")&&H.apply(this)})},hidePicker:function(){return this.each(function(){s(this).data("colpickId")&&s("#"+s(this).data("colpickId")).hide()})},setColor:function(e,i){if(i=void 0===i?1:i,"string"==typeof e)e=P(e);else if(null!=e.r&&null!=e.g&&null!=e.b)e=N(e);else{if(null==e.h||null==e.s||null==e.b)return this;e=fixHsb(e)}return this.each(function(){if(s(this).data("colpickId")){var t=s("#"+s(this).data("colpickId"));t.data("colpick").color=e,t.data("colpick").origColor=e,v(e,t.get(0)),g(e,t.get(0)),k(e,t.get(0)),m(e,t.get(0)),_(e,t.get(0)),b(e,t.get(0)),t.data("colpick").onChange.apply(t.parent(),[e,t.data("colpick").hsl?B(e):j(e),t.data("colpick").hsl?O(e):L(e),t.data("colpick").el,1]),i&&x(e,t.get(0))}})}});function v(t,e){var i=s(e).data("colpick").hsl?O(t):L(t);s(e).data("colpick").fields.eq(1).val(i.r).end().eq(2).val(i.g).end().eq(3).val(i.b).end()}function g(t,e){s(e).data("colpick").fields.eq(4).val(Math.round(t.h)).end().eq(5).val(Math.round(t.s)).end().eq(6).val(Math.round(t.x)).end()}function k(t,e){s(e).data("colpick").fields.eq(0).val(s(e).data("colpick").hsl?B(t):j(t))}function _(t,e){s(e).data("colpick").selector.css("backgroundColor","#"+(s(e).data("colpick").hsl?B({h:t.h,s:100,x:50}):j({h:t.h,s:100,x:100}))),s(e).data("colpick").selectorIndic.css({left:parseInt(s(e).data("colpick").height*t.s/100,10),top:parseInt(s(e).data("colpick").height*(100-t.x)/100,10)})}function m(t,e){s(e).data("colpick").hue.css("top",parseInt(s(e).data("colpick").height-s(e).data("colpick").height*t.h/360,10))}function x(t,e){s(e).data("colpick").currentColor.css("backgroundColor","#"+(s(e).data("colpick").hsl?B(t):j(t)))}function b(t,e){s(e).data("colpick").newColor.css("backgroundColor","#"+(s(e).data("colpick").hsl?B(t):j(t)))}function y(t){var e,i=s(this).parent().parent();if(0<this.parentNode.className.indexOf("_hex"))i.data("colpick").color=e=i.data("colpick").hsl?Y(f(this.value)):P(f(this.value)),v(e,i.get(0)),g(e,i.get(0));else if(0<this.parentNode.className.indexOf("_hsx"))i.data("colpick").color=e=h({h:parseInt(i.data("colpick").fields.eq(4).val(),10),s:parseInt(i.data("colpick").fields.eq(5).val(),10),x:parseInt(i.data("colpick").fields.eq(6).val(),10)}),v(e,i.get(0)),k(e,i.get(0));else{var a={r:parseInt(i.data("colpick").fields.eq(1).val(),10),g:parseInt(i.data("colpick").fields.eq(2).val(),10),b:parseInt(i.data("colpick").fields.eq(3).val(),10)};i.data("colpick").color=e=i.data("colpick").hsl?z(u(a)):N(u(a)),k(e,i.get(0)),g(e,i.get(0))}_(e,i.get(0)),m(e,i.get(0)),b(e,i.get(0)),i.data("colpick").onChange.apply(i.parent(),[e,i.data("colpick").hsl?B(e):j(e),i.data("colpick").hsl?O(e):L(e),i.data("colpick").el,0])}function w(t){s(this).parent().removeClass("colpick_focus")}function M(){s(this).parent().parent().data("colpick").fields.parent().removeClass("colpick_focus"),s(this).parent().addClass("colpick_focus")}function C(t){t.preventDefault?t.preventDefault():t.returnValue=!1;var e=s(this).parent().find("input").focus(),i={el:s(this).parent().addClass("colpick_slider"),max:0<this.parentNode.className.indexOf("_hsx_h")?360:0<this.parentNode.className.indexOf("_hsx")?100:255,y:t.pageY,field:e,val:parseInt(e.val(),10),preview:s(this).parent().parent().data("colpick").livePreview};s(document).mouseup(i,c),s(document).mousemove(i,a)}function I(t){t.preventDefault?t.preventDefault():t.returnValue=!1;var e={cal:s(this).parent(),y:s(this).offset().top};s(document).on("mouseup touchend",e,l),s(document).on("mousemove touchmove",e,o);var i="touchstart"==t.type?t.originalEvent.changedTouches[0].pageY:t.pageY;return y.apply(e.cal.data("colpick").fields.eq(4).val(parseInt(360*(e.cal.data("colpick").height-(i-e.y))/e.cal.data("colpick").height,10)).get(0),[e.cal.data("colpick").livePreview]),!1}function T(t){t.preventDefault?t.preventDefault():t.returnValue=!1;var e,i={cal:s(this).parent(),pos:s(this).offset()};return i.preview=i.cal.data("colpick").livePreview,s(document).on("mouseup touchend",i,n),s(document).on("mousemove touchmove",i,r),e="touchstart"==t.type?(pageX=t.originalEvent.changedTouches[0].pageX,t.originalEvent.changedTouches[0].pageY):(pageX=t.pageX,t.pageY),y.apply(i.cal.data("colpick").fields.eq(6).val(parseInt(100*(i.cal.data("colpick").height-(e-i.pos.top))/i.cal.data("colpick").height,10)).end().eq(5).val(parseInt(100*(pageX-i.pos.left)/i.cal.data("colpick").height,10)).get(0),[i.preview]),!1}function q(t){var e=s(this).parent(),i=e.data("colpick").color;e.data("colpick").origColor=i,x(i,e.get(0)),e.data("colpick").onSubmit(i,e.data("colpick").hsl?B(i):j(i),e.data("colpick").hsl?O(i):L(i),e.data("colpick").el)}function H(t){t.stopPropagation();var e=s("#"+s(this).data("colpickId"));e.data("colpick").onBeforeShow.apply(this,[e.get(0)]);var i=s(this).offset(),a=i.top+this.offsetHeight,c=i.left,o=p(),l=e.width();c+l>o.l+o.w&&(c-=l),e.css({left:c+"px",top:a+"px"}),0!=e.data("colpick").onShow.apply(this,[e.get(0)])&&e.show(),s("html").mousedown({cal:e},d),e.mousedown(function(t){t.stopPropagation()})}function S(){var t=s(this).parent(),e=t.data("colpick").origColor;t.data("colpick").color=e,v(e,t.get(0)),k(e,t.get(0)),g(e,t.get(0)),_(e,t.get(0)),m(e,t.get(0)),b(e,t.get(0))}function i(t){return{r:(t=parseInt(-1<t.indexOf("#")?t.substring(1):t,16))>>16,g:(65280&t)>>8,b:255&t}}function X(t){var e={h:0,s:0,x:0};return e.h=t.h,e.x=(200*t.x+t.s*(100-Math.abs(2*t.x-100)))/200,e.s=200*(e.x-t.x)/e.x,e}function E(t){var i=[t.r.toString(16),t.g.toString(16),t.b.toString(16)];return s.each(i,function(t,e){1==e.length&&(i[t]="0"+e)}),i.join("")}var P=function(t){return N(i(t))},Y=function(t){return z(i(t))},N=function(t){var e={h:0,s:0,x:0},i=Math.min(t.r,t.g,t.b),a=Math.max(t.r,t.g,t.b),c=a-i;return e.x=a,e.s=0!=a?255*c/a:0,0!=e.s?t.r==a?e.h=(t.g-t.b)/c:t.g==a?e.h=2+(t.b-t.r)/c:e.h=4+(t.r-t.g)/c:e.h=-1,e.h*=60,e.h<0&&(e.h+=360),e.s*=100/255,e.x*=100/255,e},z=function(t){return D(N(t))},D=function(t){var e={h:0,s:0,x:0};return e.h=t.h,e.x=t.x*(200-t.s)/200,e.s=t.x*t.s/(100-Math.abs(2*e.x-100)),e},L=function(t){var e={},i=t.h,a=255*t.s/100,c=255*t.x/100;if(0==a)e.r=e.g=e.b=c;else{var o=c,l=(255-a)*c/255,r=i%60*(o-l)/60;360==i&&(i=0),i<60?(e.r=o,e.b=l,e.g=l+r):i<120?(e.g=o,e.b=l,e.r=o-r):i<180?(e.g=o,e.r=l,e.b=l+r):i<240?(e.b=o,e.r=l,e.g=o-r):i<300?(e.b=o,e.g=l,e.r=l+r):i<360?(e.r=o,e.g=l,e.b=o-r):(e.r=0,e.g=0,e.b=0)}return{r:Math.round(e.r),g:Math.round(e.g),b:Math.round(e.b)}},O=function(t){return L(X(t))},j=function(t){return E(L(t))},B=function(t){return j(X(t))};s.fn.extend({colpick:e.init,colpickHide:e.hidePicker,colpickShow:e.showPicker,colpickSetColor:e.setColor}),s.extend({colpick:{rgbToHex:E,rgbToHsb:N,rgbToHsl:z,hsbToHex:j,hsbToRgb:L,hsbToHsl:D,hexToHsb:P,hexToHsl:Y,hexToRgb:i,hslToHsb:X,hslToRgb:O,hslToHex:B}})}(jQuery);