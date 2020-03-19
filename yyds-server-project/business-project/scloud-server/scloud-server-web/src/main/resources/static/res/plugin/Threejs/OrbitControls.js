THREE.OrbitControls=function(e,t){var n,o,a,i,r;this.object=e,this.domElement=void 0!==t?t:document,this.enabled=!0,this.target=new THREE.Vector3,this.maxY=0,this.minDistance=0,this.maxDistance=1/0,this.minZoom=0,this.maxZoom=1/0,this.minPolarAngle=0,this.maxPolarAngle=Math.PI,this.minAzimuthAngle=-1/0,this.maxAzimuthAngle=1/0,this.enableDamping=!1,this.dampingFactor=.25,this.enableZoom=!0,this.zoomSpeed=1,this.enableRotate=!0,this.rotateSpeed=1,this.enablePan=!0,this.keyPanSpeed=7,this.autoRotate=!1,this.autoRotateSpeed=2,this.enableKeys=!0,this.keys={LEFT:37,UP:38,RIGHT:39,BOTTOM:40},this.mouseButtons={ORBIT:THREE.MOUSE.LEFT,ZOOM:THREE.MOUSE.MIDDLE,PAN:THREE.MOUSE.RIGHT},this.target0=this.target.clone(),this.position0=this.object.position.clone(),this.zoom0=this.object.zoom,this.getPolarAngle=function(){return h.phi},this.getAzimuthalAngle=function(){return h.theta},this.reset=function(){s.target.copy(s.target0),s.object.position.copy(s.position0),s.object.zoom=s.zoom0,s.object.updateProjectionMatrix(),s.dispatchEvent(c),s.update(),d=u.NONE},this.update=(n=new THREE.Vector3,o=(new THREE.Quaternion).setFromUnitVectors(e.up,new THREE.Vector3(0,1,0)),a=o.clone().inverse(),i=new THREE.Vector3,r=new THREE.Quaternion,function(){var e=s.object.position;return n.copy(e).sub(s.target),n.applyQuaternion(o),h.setFromVector3(n),s.autoRotate&&d===u.NONE&&C(2*Math.PI/60/60*s.autoRotateSpeed),h.theta+=p.theta,h.phi+=p.phi,h.theta=Math.max(s.minAzimuthAngle,Math.min(s.maxAzimuthAngle,h.theta)),h.phi=Math.max(s.minPolarAngle,Math.min(s.maxPolarAngle,h.phi)),Math.cos(h.phi)*h.radius+s.target.y<1&&(h.phi=Math.acos((1-s.target.y)/h.radius)),h.makeSafe(),h.radius*=b,h.radius=Math.max(s.minDistance,Math.min(s.maxDistance,h.radius)),s.target.add(f),n.setFromSpherical(h),n.applyQuaternion(a),e.copy(s.target).add(n),s.object.lookAt(s.target),!0===s.enableDamping?(p.theta*=1-s.dampingFactor,p.phi*=1-s.dampingFactor):p.set(0,0,0),b=1,f.set(0,0,0),!(!(T||i.distanceToSquared(s.object.position)>E||8*(1-r.dot(s.object.quaternion))>E)||(s.dispatchEvent(c),i.copy(s.object.position),r.copy(s.object.quaternion),T=!1))}),this.dispose=function(){s.domElement.removeEventListener("contextmenu",B,!1),s.domElement.removeEventListener("mousedown",Z,!1),s.domElement.removeEventListener("mousewheel",F,!1),s.domElement.removeEventListener("MozMousePixelScroll",F,!1),s.domElement.removeEventListener("touchstart",X,!1),s.domElement.removeEventListener("touchend",_,!1),s.domElement.removeEventListener("touchmove",K,!1),document.removeEventListener("mousemove",z,!1),document.removeEventListener("mouseup",Y,!1),window.removeEventListener("keydown",I,!1)};var s=this,c={type:"change"},l={type:"start"},m={type:"end"},u={NONE:-1,ROTATE:0,DOLLY:1,PAN:2,TOUCH_ROTATE:3,TOUCH_DOLLY:4,TOUCH_PAN:5},d=u.NONE,E=1e-6,h=new THREE.Spherical,p=new THREE.Spherical,b=1,f=new THREE.Vector3,T=!1,g=new THREE.Vector2,R=new THREE.Vector2,v=new THREE.Vector2,O=new THREE.Vector2,y=new THREE.Vector2,H=new THREE.Vector2,w=new THREE.Vector2,P=new THREE.Vector2,j=new THREE.Vector2;function M(){return Math.pow(.95,s.zoomSpeed)}function C(e){p.theta-=e}function L(e){p.phi-=e}var N,A,x,D=(N=new THREE.Vector3,function(e,t){N.setFromMatrixColumn(t,0),N.multiplyScalar(-e),f.add(N)}),k=(A=new THREE.Vector3,function(e,t){A.setFromMatrixColumn(t,1),A.multiplyScalar(e),f.add(A)}),U=(x=new THREE.Vector3,function(e,t){var n=s.domElement===document?s.domElement.body:s.domElement;if(s.object instanceof THREE.PerspectiveCamera){var o=s.object.position;x.copy(o).sub(s.target);var a=x.length();a*=Math.tan(s.object.fov/2*Math.PI/180),D(2*e*a/n.clientHeight,s.object.matrix),k(2*t*a/n.clientHeight,s.object.matrix)}else s.object instanceof THREE.OrthographicCamera?(D(e*(s.object.right-s.object.left)/s.object.zoom/n.clientWidth,s.object.matrix),k(t*(s.object.top-s.object.bottom)/s.object.zoom/n.clientHeight,s.object.matrix)):(console.warn("WARNING: OrbitControls.js encountered an unknown camera type - pan disabled."),s.enablePan=!1)});function S(e){s.object instanceof THREE.PerspectiveCamera?b/=e:s.object instanceof THREE.OrthographicCamera?(s.object.zoom=Math.max(s.minZoom,Math.min(s.maxZoom,s.object.zoom*e)),s.object.updateProjectionMatrix(),T=!0):(console.warn("WARNING: OrbitControls.js encountered an unknown camera type - dolly/zoom disabled."),s.enableZoom=!1)}function V(e){s.object instanceof THREE.PerspectiveCamera?b*=e:s.object instanceof THREE.OrthographicCamera?(s.object.zoom=Math.max(s.minZoom,Math.min(s.maxZoom,s.object.zoom/e)),s.object.updateProjectionMatrix(),T=!0):(console.warn("WARNING: OrbitControls.js encountered an unknown camera type - dolly/zoom disabled."),s.enableZoom=!1)}function Z(e){if(!1!==s.enabled){if(e.preventDefault(),e.button===s.mouseButtons.ORBIT){if(!1===s.enableRotate)return;!function(e){g.set(e.clientX,e.clientY)}(e),d=u.ROTATE}else if(e.button===s.mouseButtons.ZOOM){if(!1===s.enableZoom)return;!function(e){w.set(e.clientX,e.clientY)}(e),d=u.DOLLY}else if(e.button===s.mouseButtons.PAN){if(!1===s.enablePan)return;!function(e){O.set(e.clientX,e.clientY)}(e),d=u.PAN}d!==u.NONE&&(document.addEventListener("mousemove",z,!1),document.addEventListener("mouseup",Y,!1),s.dispatchEvent(l))}}function z(e){if(!1!==s.enabled)if(e.preventDefault(),d===u.ROTATE){if(!1===s.enableRotate)return;!function(e){R.set(e.clientX,e.clientY),v.subVectors(R,g);var t=s.domElement===document?s.domElement.body:s.domElement;C(2*Math.PI*v.x/t.clientWidth*s.rotateSpeed),L(2*Math.PI*v.y/t.clientHeight*s.rotateSpeed),g.copy(R),s.update()}(e)}else if(d===u.DOLLY){if(!1===s.enableZoom)return;!function(e){P.set(e.clientX,e.clientY),j.subVectors(P,w),0<j.y?S(M()):j.y<0&&V(M()),w.copy(P),s.update()}(e)}else if(d===u.PAN){if(!1===s.enablePan)return;!function(e){y.set(e.clientX,e.clientY),H.subVectors(y,O),H.y<0&&s.object.position.y<5||(U(H.x,H.y),O.copy(y),s.update())}(e)}}function Y(e){!1!==s.enabled&&(document.removeEventListener("mousemove",z,!1),document.removeEventListener("mouseup",Y,!1),s.dispatchEvent(m),d=u.NONE)}function F(e){!1===s.enabled||!1===s.enableZoom||d!==u.NONE&&d!==u.ROTATE||(e.preventDefault(),e.stopPropagation(),function(e){var t=0;void 0!==e.wheelDelta?t=e.wheelDelta:void 0!==e.detail&&(t=-e.detail),0<t?V(M()):t<0&&S(M()),s.update()}(e),s.dispatchEvent(l),s.dispatchEvent(m))}function I(e){!1!==s.enabled&&!1!==s.enableKeys&&!1!==s.enablePan&&function(e){switch(e.keyCode){case s.keys.UP:U(0,s.keyPanSpeed),s.update();break;case s.keys.BOTTOM:U(0,-s.keyPanSpeed),s.update();break;case s.keys.LEFT:U(s.keyPanSpeed,0),s.update();break;case s.keys.RIGHT:U(-s.keyPanSpeed,0),s.update()}}(e)}function X(e){if(!1!==s.enabled){switch(e.touches.length){case 1:if(!1===s.enableRotate)return;!function(e){g.set(e.touches[0].pageX,e.touches[0].pageY)}(e),d=u.TOUCH_ROTATE;break;case 2:if(!1===s.enableZoom)return;!function(e){var t=e.touches[0].pageX-e.touches[1].pageX,n=e.touches[0].pageY-e.touches[1].pageY,o=Math.sqrt(t*t+n*n);w.set(0,o)}(e),d=u.TOUCH_DOLLY;break;case 3:if(!1===s.enablePan)return;!function(e){O.set(e.touches[0].pageX,e.touches[0].pageY)}(e),d=u.TOUCH_PAN;break;default:d=u.NONE}d!==u.NONE&&s.dispatchEvent(l)}}function K(e){if(!1!==s.enabled)switch(e.preventDefault(),e.stopPropagation(),e.touches.length){case 1:if(!1===s.enableRotate)return;if(d!==u.TOUCH_ROTATE)return;!function(e){R.set(e.touches[0].pageX,e.touches[0].pageY),v.subVectors(R,g);var t=s.domElement===document?s.domElement.body:s.domElement;C(2*Math.PI*v.x/t.clientWidth*s.rotateSpeed),L(2*Math.PI*v.y/t.clientHeight*s.rotateSpeed),g.copy(R),s.update()}(e);break;case 2:if(!1===s.enableZoom)return;if(d!==u.TOUCH_DOLLY)return;!function(e){var t=e.touches[0].pageX-e.touches[1].pageX,n=e.touches[0].pageY-e.touches[1].pageY,o=Math.sqrt(t*t+n*n);P.set(0,o),j.subVectors(P,w),0<j.y?V(M()):j.y<0&&S(M()),w.copy(P),s.update()}(e);break;case 3:if(!1===s.enablePan)return;if(d!==u.TOUCH_PAN)return;!function(e){y.set(e.touches[0].pageX,e.touches[0].pageY),H.subVectors(y,O),U(H.x,H.y),O.copy(y),s.update()}(e);break;default:d=u.NONE}}function _(e){!1!==s.enabled&&(s.dispatchEvent(m),d=u.NONE)}function B(e){e.preventDefault()}this.dollyIn=S,this.dollyOut=V,s.domElement.addEventListener("contextmenu",B,!1),s.domElement.addEventListener("mousedown",Z,!1),s.domElement.addEventListener("mousewheel",F,!1),s.domElement.addEventListener("MozMousePixelScroll",F,!1),s.domElement.addEventListener("touchstart",X,!1),s.domElement.addEventListener("touchend",_,!1),s.domElement.addEventListener("touchmove",K,!1),window.addEventListener("keydown",I,!1),this.update()},THREE.OrbitControls.prototype=Object.create(THREE.EventDispatcher.prototype),THREE.OrbitControls.prototype.constructor=THREE.OrbitControls,Object.defineProperties(THREE.OrbitControls.prototype,{center:{get:function(){return console.warn("THREE.OrbitControls: .center has been renamed to .target"),this.target}},noZoom:{get:function(){return console.warn("THREE.OrbitControls: .noZoom has been deprecated. Use .enableZoom instead."),!this.enableZoom},set:function(e){console.warn("THREE.OrbitControls: .noZoom has been deprecated. Use .enableZoom instead."),this.enableZoom=!e}},noRotate:{get:function(){return console.warn("THREE.OrbitControls: .noRotate has been deprecated. Use .enableRotate instead."),!this.enableRotate},set:function(e){console.warn("THREE.OrbitControls: .noRotate has been deprecated. Use .enableRotate instead."),this.enableRotate=!e}},noPan:{get:function(){return console.warn("THREE.OrbitControls: .noPan has been deprecated. Use .enablePan instead."),!this.enablePan},set:function(e){console.warn("THREE.OrbitControls: .noPan has been deprecated. Use .enablePan instead."),this.enablePan=!e}},noKeys:{get:function(){return console.warn("THREE.OrbitControls: .noKeys has been deprecated. Use .enableKeys instead."),!this.enableKeys},set:function(e){console.warn("THREE.OrbitControls: .noKeys has been deprecated. Use .enableKeys instead."),this.enableKeys=!e}},staticMoving:{get:function(){return console.warn("THREE.OrbitControls: .staticMoving has been deprecated. Use .enableDamping instead."),!this.enableDamping},set:function(e){console.warn("THREE.OrbitControls: .staticMoving has been deprecated. Use .enableDamping instead."),this.enableDamping=!e}},dynamicDampingFactor:{get:function(){return console.warn("THREE.OrbitControls: .dynamicDampingFactor has been renamed. Use .dampingFactor instead."),this.dampingFactor},set:function(e){console.warn("THREE.OrbitControls: .dynamicDampingFactor has been renamed. Use .dampingFactor instead."),this.dampingFactor=e}}});