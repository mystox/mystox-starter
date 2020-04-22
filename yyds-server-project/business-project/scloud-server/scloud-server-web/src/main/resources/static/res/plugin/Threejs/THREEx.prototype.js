  ;(function () {   
    /**
     * 添加mesh的prototype
     * @Author   chenht
     * @DateTime 2017-04-11
     */
    var initMeshPrototype = function() {
        var __leaveMeshFn = { // 离开状态的方法
            normal: function function_name() {
            }, 
            moving: function function_name() {
                this.setOpacity(1);
            }, 
            selected: function function_name() {            
                if (this.beforeCancelSelected) { // hook 取消移动之后调用
                    this.beforeCancelSelected();
                }

                this.hideBorderLine();
                if (this.hideDeviceInfo) {
                    this.hideDeviceInfo();  
                }  
            }
        };
        var __enterMeshFn = { // 进入状态的方法
            normal: function function_name() {
            }, 
            moving: function function_name() {
                this.setOpacity(0.5);
            }, 
            selected: function function_name() {
                if (scene3D.setting.showDeviceInfoFlag && this.showDeviceInfo) {
                    this.showDeviceInfo();  
                }  

                if (this.afterSelected) { // hook 确认选择之后调用
                    this.afterSelected(this.intersectedPos);
                }
                this.showBorderLine();
            }
        };

        // 对自身设置高亮
        THREE.Mesh.prototype.highlight = function (color) {
            this.originColor = this.material.color.clone();
            if (color) {
                this.material.color.set(color);
            } else {
                this.material.color.set(this.highlightColor);
            }        
        };
        // 对自身取消高亮
        THREE.Mesh.prototype.unHighlight = function () {
            this.material.color.set(this.originColor);
        };
        /**
         * 显示设备的边界线
         * @Author   chenht
         * @DateTime 2017-08-31
         */
        THREE.Mesh.prototype.showBorderLine = function () {
            var mesh = this;

            // 容器中不显示
            if (mesh.parent.name && mesh.parent.name.type) { return; }

            var boundingBox = $.extend(true, {}, mesh.spec.boundingBox);
            // // 根据旋转量来转换长和宽
            // if (scene3D.similarWith((this.rotation.y + 2 * Math.PI) % (Math.PI), Math.PI / 2, 0.01)) {
            //     t = boundingBox.max.x; boundingBox.max.x = boundingBox.max.z; boundingBox.max.z = t;
            //     t = boundingBox.min.x; boundingBox.min.x = boundingBox.min.z; boundingBox.min.z = t;
            // }
            // 为边界线添加padding值，y轴底部不添加
            for (var i = 0; i < 3; i++) {
                var a = ['x', 'y', 'z'][i];
                var padding = (boundingBox.max[a] - boundingBox.min[a]) / 100;
                boundingBox.max[a] += padding;
                if (i === 1) { continue; }
                boundingBox.min[['x', 'y', 'z'][i]] -= padding;
            }
            // 为24条线定义位置。
            // 8个顶点
            [[1, 1, 1], [1, 1, 0], [1, 0, 1], [1, 0, 0], [0, 1, 1], [0, 1, 0], [0, 0, 1], [0, 0, 0]].forEach(function (v, itr) {
                // 每个顶点3条线
                for (var j = 0; j < 3; j++) {
                    // 根据物体中心计算出来的偏移值
                    var offset = new THREE.Vector3();
                    v.forEach(function (u, i) {
                        var m = u ? 'max' : 'min';
                        var a = ['x', 'y', 'z'][i];
                        offset[a] = boundingBox[m][a];
                        var p0 = 0, p1;
                        if (i === j) {
                            // 根据顶点的位置、是否翻转来计算线段第二个点的位置。
                            p1 = p0 - (u * 2 - 1) * Math.abs(boundingBox.max[a] - boundingBox.min[a]) / 4 * (mesh.scale[a] < 0 ? -1 : 1);
                        } else {
                            p1 = p0;
                        }
                        // 重新计算顶点的坐标。
                        scene3D.borderLine[itr * 3 + j].geometry.vertices[0][a] = p0;
                        scene3D.borderLine[itr * 3 + j].geometry.vertices[1][a] = p1;
                    });
                    scene3D.borderLine[itr * 3 + j].geometry.verticesNeedUpdate = true;
                    var pos;
                    // 是否是容器内的设备
                    if (mesh.parent.type === 'Scene') {
                        pos = mesh.position.clone();
                    } else {
                        pos = mesh.parent.position.clone().add(mesh.position);
                    }
                    scene3D.borderLine[itr * 3 + j].position.copy(pos.add(offset));
                    scene3D.scene.add(scene3D.borderLine[itr * 3 + j]);

                    var quaternion = new THREE.Quaternion();
                    quaternion.setFromAxisAngle( new THREE.Vector3( 0, 1, 0 ), mesh.rotation.y );

                    var relativePos;       


                    // 是否是容器内的设备
                    if (mesh.parent.type === 'Scene') {
                        relativePos = scene3D.borderLine[itr * 3 + j].position.clone().sub(mesh.position); 
                        relativePos.applyQuaternion( quaternion );  
                        scene3D.borderLine[itr * 3 + j].position.copy(relativePos.add(mesh.position));
                    } else {   
                        relativePos = scene3D.borderLine[itr * 3 + j].position.clone().sub(mesh.parent.position).sub(mesh.position);     
                        relativePos.applyQuaternion( quaternion );
                        quaternion.setFromAxisAngle( new THREE.Vector3( 0, 1, 0 ), mesh.parent.rotation.y );
                        relativePos.add(mesh.position);
                        relativePos.applyQuaternion( quaternion );
                        scene3D.borderLine[itr * 3 + j].position.copy(relativePos.add(mesh.parent.position));
                    }
                     
                    scene3D.borderLine[itr * 3 + j].rotation.y = mesh.rotation.y + mesh.parent.rotation.y;
                }                
            });


        };
        THREE.Mesh.prototype.hideBorderLine = function () {
            scene3D.borderLine.forEach(function (v) {
                scene3D.scene.remove(v);
            });
        };
        // 对自身设置透明度
        THREE.Sprite.prototype.setOpacity = THREE.Mesh.prototype.setOpacity = function (o) {
            /**
             * 如果o大于等于1时，则会将透明度设置为默认值，（没有默认值则设置为1） 
             * @Author   chenht
             * @DateTime 2017-06-19
             */
            if (o >= 1) {                
                this.material.opacity = this.device && scene3D.model[this.device.type].material.opacity || 1;
                this.material.transparent = this.device && scene3D.model[this.device.type].material.transparent || false;
            } else {
                this.material.transparent = true;
                this.material.opacity = o;
            }
            /**
             * 递归遍历自己的儿子（如果有opacityPropagation属性）
             * @Author   chenht
             * @DateTime 2017-06-19
             */
            this.children.filter(function (v) {
                return v.name && v.name.opacityPropagation;
            }).forEach(function (v) {
                if (v.setOpacity) {
                    v.setOpacity(o);
                }
            });
        };
        /**
         * 更新名牌
         * @Author   chenht
         * @DateTime 2017-06-19
         */
        THREE.Mesh.prototype.updateNameTag = function () {
            var canvas = scene3D._createNameTagCanvas(this.device.name, scene3D.user.level === 'guest' && this.device.alarmCount, scene3D.user.level === 'guest' && scene3D.setting.display);

            //  如果名牌已经存在则只要更新贴图
            if (this.nameTag && this.nameTag.parent === this) {
                this.nameTag.material.map.dispose();
                this.nameTag.material.map = new THREE.Texture(canvas);
                this.nameTag.material.map.needsUpdate = true;
            } else { // 名牌不存在则新建名牌
                var spriteMap = new THREE.Texture(canvas);
                spriteMap.needsUpdate = true;
                var spriteMaterial = new THREE.SpriteMaterial( { 
                    map: spriteMap, 
                    depthTest: false
                } );

                var sprite = new THREE.Sprite( spriteMaterial );
                sprite.name = {
                    type:"nameTag"
                };

                this.add(sprite);
                this.nameTag = sprite;
                scene3D.nameTagList.push(sprite);
            }

            // 将设备上的showName属性复制到名牌上
            if (this.showName === undefined) {
                this.showName = this.device.model.showName;
            }

            // 设置名牌是否显示
            if (this.showName) {
                this.nameTag.show();
            } else {
                this.nameTag.hide();
            }
            // 缩放名牌的尺寸
            this.nameTag.scale.set(canvas.width / 32 * 0.77 * scene3D.setting.nameTagSize, canvas.height / 32 * 0.77 * scene3D.setting.nameTagSize, 1);
            this.nameTag.originScale = this.nameTag.scale.clone();
            // 根据规格的缩放来重新绘制名牌大小。
            this.spec.updateNameTag();
            if (canvas.height > 1024) {
                this.nameTag.position.y = this.device.model.height / 2 + 3 - 2.9;
            } else {
                this.nameTag.position.y = this.device.model.height / 2 + 3 - 2.9;
            }
        };        
        /**
         * 切换mesh的材质的正反面 （因为doubleSide不反光）
         * @Author   chenht
         * @DateTime 2017-08-21
         */
        THREE.Mesh.prototype.reverse = function (side) {
            if (!this.skipReverse) {
                this.material.side++;
                this.material.side %= 2;
                if (side !== undefined) {
                    this.material.side = side;
                }
            }
            this.children.forEach(function (v) {
                if (v.type === 'Mesh') {
                    v.reverse(side);
                }
            });
        };
        // 隐藏自身
        THREE.Line.prototype.hide = THREE.Sprite.prototype.hide = THREE.Sprite.prototype.hide = THREE.Mesh.prototype.hide = function (force) {
            if (force || this.type !== 'Sprite') {
                this.children.forEach(function (c) { // 执行所有子元素的方法。
                    c.hide && c.hide(true);
                });  
            }      
            this.material.visible = false;    
        };     
        // 显示自身
        THREE.Mesh.prototype.show = function () {
            var _this = this;
            this.children.forEach(function (c) { // 执行所有子元素的方法。
                c.show && c.show();
                // 对于名牌需要特殊判断，根据showName属性显示。
                if (c.type === 'Sprite') {
                    if (_this.showName) {
                        c.show();
                    } else {
                        c.hide();
                    }
                }
            });        
            this.material.visible = true;
        };
        // 名牌的show方法则直接显示。
        THREE.Line.prototype.show = THREE.Sprite.prototype.show = function () {        
            this.material.visible = true;
        };
        // 是否隐藏
        THREE.Line.prototype.isShow = THREE.Sprite.prototype.isShow = THREE.Mesh.prototype.isShow = function () {
            return this.material.visible;
        };

        THREE.Vector3.prototype.createVector = function () {
            var camera = scene3D.camera;

            var p = new THREE.Vector3(this.x, this.y, this.z);
            var vector = p.project(camera);

            vector.x = (vector.x + 1) / 2 * scene3D.canvas.offsetWidth;
            vector.y = -(vector.y - 1) / 2 * scene3D.canvas.offsetHeight;
            vector.z = 0;

            return vector;
        };

        /**
         * 名牌点击事件的重写
         * @Author   chenht
         * @DateTime 2017-06-19
         */
        var originSpriteRaycast = THREE.Sprite.prototype.raycast;
        THREE.Sprite.prototype.raycast = function (raycaster, intersects) {
            // 判断该sprite是否为名牌
            if (this.parent.nameTag === this) {

                // 摄像机的方向向量
                var vectorCT = scene3D.camera.position.clone().sub(scene3D.controller.target).normalize();
                // 摄像机与y轴的叉乘向量
                var v = vectorCT.clone().cross(new THREE.Vector3(0,1,0));
                // 该向量旋转之后得到的与名牌一致的向量
                v = v.applyAxisAngle(vectorCT, - Math.PI / 2);

                // 生成一个一半尺寸的平面，将其按照上面的向量移动，是的与名牌视觉上重合
                var planeGeom = new THREE.PlaneGeometry(this.scale.x, this.scale.y / 2);
                var a = new THREE.Mesh(planeGeom, new THREE.MeshBasicMaterial({color: Math.random() * 0xFFFFFF}));
                a.quaternion.copy(scene3D.camera.quaternion);
                a.position.copy(this.getWorldPosition());
                a.position.add(v.normalize().multiplyScalar(this.scale.y / 4));
                scene3D.scene.add(a);

                // 让该平面去intersect，如果结果数组改变则有碰撞，将该结果给device
                var length = intersects.length;     
                a.updateMatrixWorld();
                a.raycast(raycaster, intersects);
                scene3D.scene.remove(a);
                if (length < intersects.length) {
                    intersects[intersects.length - 1].object = this.parent;
                }


            } else { // 否则调用原生的方法
                originSpriteRaycast.call(this, raycaster, intersects);
            }

        };

        /**
         * 对于mesh的raycast方法的重写，（目前用于机柜门打开）
         * @Author   chenht
         * @DateTime 2017-06-20
         */
        var originMeshRaycast = THREE.Mesh.prototype.raycast;
        THREE.Mesh.prototype.raycast = function (raycaster, intersects) {
            // 首先调用原生的方法
            originMeshRaycast.call(this, raycaster, intersects);
            // 然后判断是否有privateRaycast，有则调用
            if (this.privateRaycast) {
                var length = intersects.length; 
                this.privateRaycast(raycaster, intersects);
            }               
        };
        Object.defineProperties(THREE.Mesh.prototype, {
            showName: {
                set: function (v) {
                    if (v === this._showName || v !== true && v !== false) {
                        return;
                    }
                    if (v) {
                        this.nameTag.show();
                    } else {
                        this.nameTag.hide();
                    }
                    this._showName = v;
                },
                get: function () {
                    return this._showName;
                }
            },
            _showName: {
                value: undefined,
                writable: true
            }
        });
        // 设置物体状态
        Object.defineProperties(THREE.Mesh.prototype, {
            state: {
                set: function (v) {
                    var validList = ['normal', 'selected'];
                    if (scene3D.user.level === 'administrator') {
                        validList.push('moving');
                    }
                    if (v === this._state || validList.indexOf(v) === -1) { // 与当前状态一致或非法，返回
                        return;
                    }
                    __leaveMeshFn[this._state].call(this); // 执行离开方法
                    __enterMeshFn[v].call(this); // 执行进入方法
                    // this.children.forEach(function (c) { // 修改所有子物体的状态。
                    //     c.state = v;
                    // });
                    this._state = v;
                },
                get: function () {
                    return this._state;
                }
            },
            _state: {
                value: 'normal',
                writable: true
            },
            /**
             * Mesh的规格
             * 通过boundingBox计算其尺寸，然后通过scale来调整其大小，使其满足需要的尺寸。
             * @type {Object}
             */
            spec: {
                set: function (v) {
                    console.warn('please use Mesh.spec.set(x, y, z) to modify this variable!');
                    return undefined;
                },
                get: function () {
                    if (!this._spec) {
                        // 计算 boundingBox
                        this.geometry.computeBoundingBox();
                        var b = this.geometry.boundingBox;
                        var _this = this;

                        // 獲取兒子們的spec
                        // 計算整個mesh的BoundingBox
                        var calculateMultiBoundingBox = function (box) {
                            var maxmin = [[[box.max.x], [box.max.y], [box.max.z]], [[box.min.x], [box.min.y], [box.min.z]]];
                            _this.children.forEach(function (v) {
                                if (v.spec) {
                                    var points = [
                                        new THREE.Vector3(v.geometry.boundingBox.max.x, v.geometry.boundingBox.max.y, v.geometry.boundingBox.max.z), 
                                        new THREE.Vector3(v.geometry.boundingBox.min.x, v.geometry.boundingBox.min.y, v.geometry.boundingBox.min.z)
                                    ];
                                    // 三個軸的四元組
                                    var qx = new THREE.Quaternion().setFromAxisAngle(new THREE.Vector3(1,0,0), v.rotation.x);
                                    var qy = new THREE.Quaternion().setFromAxisAngle(new THREE.Vector3(0,1,0), v.rotation.y);
                                    var qz = new THREE.Quaternion().setFromAxisAngle(new THREE.Vector3(0,0,1), v.rotation.z);
                                    // 將兩個頂點按照四元組進行旋轉
                                    points[0].applyQuaternion(qx).applyQuaternion(qy).applyQuaternion(qz);
                                    points[1].applyQuaternion(qx).applyQuaternion(qy).applyQuaternion(qz);
                                    // 壓入數組
                                    maxmin[0][0].push(Math.max(points[0].x, points[1].x) + v.position.x);
                                    maxmin[0][1].push(Math.max(points[0].y, points[1].y) + v.position.y);
                                    maxmin[0][2].push(Math.max(points[0].z, points[1].z) + v.position.z);
                                    maxmin[1][0].push(Math.min(points[0].x, points[1].x) + v.position.x);
                                    maxmin[1][1].push(Math.min(points[0].y, points[1].y) + v.position.y);
                                    maxmin[1][2].push(Math.min(points[0].z, points[1].z) + v.position.z);
                                }
                            });

                            return{
                                // 計算最大最小值
                                max: {
                                    x: Math.max.apply(null, maxmin[0][0]) * _this.scale.x,
                                    y: Math.max.apply(null, maxmin[0][1]) * _this.scale.y,
                                    z: Math.max.apply(null, maxmin[0][2]) * _this.scale.z,                                
                                },
                                min: {
                                    x: Math.min.apply(null, maxmin[1][0]) * _this.scale.x,
                                    y: Math.min.apply(null, maxmin[1][1]) * _this.scale.y,
                                    z: Math.min.apply(null, maxmin[1][2]) * _this.scale.z,       
                                }
                            };
                        };
                        
                        this._spec = new THREE.Vector3((b.max.x - b.min.x) * this.scale.x, (b.max.y - b.min.y) * this.scale.y, (b.max.z - b.min.z) * this.scale.z);

                        b = calculateMultiBoundingBox(b);
                        this._spec.boundingBox = b;
                        // 保存原有的set方法
                        var originSetMethod = THREE.Vector3.prototype.set;
                        this._spec.set = function (x, y, z) {
                            // 计算原始的尺寸。
                            old = {
                                x: _this._spec.x / _this.scale.x,
                                y: _this._spec.y / _this.scale.y,
                                z: _this._spec.z / _this.scale.z,
                            };
                            // 通过原始尺寸与当前尺寸的比值来计算scale
                            _this.scale.set(x / old.x, y / old.y, z / old.z);
                            // 重新計算整個mesh的boudingBox
                            _this._spec.boundingBox = calculateMultiBoundingBox(_this.geometry.boundingBox);
                            // 重新计算borderLine的尺寸；
                            _this.showBorderLine();
                            if (_this.nameTag) {
                                _this.updateNameTag();
                            }
                            originSetMethod.call(_this._spec, x, y, z);
                        };
                        if (this.device) {
                            this._spec.updateNameTag = function () {
                                    _this.nameTag.originScale.set(_this.nameTag.originScale.x / _this.scale.x, _this.nameTag.originScale.y / _this.scale.y, 1);
                                    _this.nameTag.scale = _this.nameTag.originScale.clone();
                            };
                        }
                        this.originSpec = this._spec.clone();
                    }
                    return this._spec;                    
                }
            },
            _spec: {
                value: undefined,
                writable: true
            }
        });

        THREE.OrbitControls.prototype.zoomIn = function () {
            this.dollyOut( Math.pow(0.8, this.zoomSpeed) );
        };
        THREE.OrbitControls.prototype.zoomOut = function () {
            this.dollyIn( Math.pow(0.8, this.zoomSpeed) );
        };

        /**
         * 位Box型的几何体创建6个面的UV。
         * @Author   chenht
         * @DateTime 2017-10-20
         */
        THREE.BoxGeometry.prototype.setUV = function (array) {
            var geo = this;

            var face_left = [
                new THREE.Vector2(array[0][0], array[0][1]),
                new THREE.Vector2(array[1][0], array[1][1]),
                new THREE.Vector2(array[2][0], array[2][1]),
                new THREE.Vector2(array[3][0], array[3][1]),
            ];
            var face_back = [
                new THREE.Vector2(array[4][0], array[4][1]),
                new THREE.Vector2(array[5][0], array[5][1]),
                new THREE.Vector2(array[6][0], array[6][1]),
                new THREE.Vector2(array[7][0], array[7][1]),
            ];
            var face_front = [
                new THREE.Vector2(array[8][0], array[8][1]),
                new THREE.Vector2(array[9][0], array[9][1]),
                new THREE.Vector2(array[10][0], array[10][1]),
                new THREE.Vector2(array[11][0], array[11][1]),
            ];
            var face_right = [
                new THREE.Vector2(array[12][0], array[12][1]),
                new THREE.Vector2(array[13][0], array[13][1]),
                new THREE.Vector2(array[14][0], array[14][1]),
                new THREE.Vector2(array[15][0], array[15][1]),
            ];
            var face_top = [
                new THREE.Vector2(array[16][0], array[16][1]),
                new THREE.Vector2(array[17][0], array[17][1]),
                new THREE.Vector2(array[18][0], array[18][1]),
                new THREE.Vector2(array[19][0], array[19][1]),
            ];
            var face_bottom = [
                new THREE.Vector2(array[20][0], array[20][1]),
                new THREE.Vector2(array[21][0], array[21][1]),
                new THREE.Vector2(array[22][0], array[22][1]),
                new THREE.Vector2(array[23][0], array[23][1]),
            ];

            geo.faceVertexUvs[0][0] = [ face_front[0], face_front[1], face_front[3] ];
            geo.faceVertexUvs[0][1] = [ face_front[1], face_front[2], face_front[3] ];
            geo.faceVertexUvs[0][2] = [ face_back[0], face_back[1], face_back[3] ];
            geo.faceVertexUvs[0][3] = [ face_back[1], face_back[2], face_back[3] ];
            geo.faceVertexUvs[0][4] = [ face_top[3], face_top[0], face_top[2] ];
            geo.faceVertexUvs[0][5] = [ face_top[0], face_top[1], face_top[2] ];
            geo.faceVertexUvs[0][6] = [ face_bottom[0], face_bottom[1], face_bottom[3] ];
            geo.faceVertexUvs[0][7] = [ face_bottom[1], face_bottom[2], face_bottom[3] ];
            geo.faceVertexUvs[0][8] = [ face_left[0], face_left[1], face_left[3] ];
            geo.faceVertexUvs[0][9] = [ face_left[1], face_left[2], face_left[3] ];
            geo.faceVertexUvs[0][10] = [ face_right[0], face_right[1], face_right[3] ];
            geo.faceVertexUvs[0][11] = [ face_right[1], face_right[2], face_right[3] ];
        };


        THREE.CustomCanvasTexture = function ( parentTexture ) {

            this._canvas = document.createElement( "canvas" );
            this._canvas.width = this._canvas.height = 2048;
            this._context2D = this._canvas.getContext( "2d" );
            this.paths = [];

            if ( parentTexture ) {

                this._parentTexture.push( parentTexture );
                parentTexture.image = this._canvas;

            }


        };

        THREE.CustomCanvasTexture.prototype = {

            constructor: THREE.CustomCanvasTexture,

            _canvas: null,
            _context2D: null,

            _parentTexture: [],

            addParent: function ( parentTexture ) {

                if ( this._parentTexture.indexOf( parentTexture ) === - 1 ) {

                    this._parentTexture.push( parentTexture );
                    parentTexture.image = this._canvas;

                }

            },

            pushPath: function (path) {
                this.paths.push(path);
                this._draw();
            },

            setSize: function (width, height) {
                var _c = document.createElement("canvas");
                var _ctx = _c.getContext("2d");
                _c.width = this._canvas.width;
                _c.height = this._canvas.height;

                var id = this._context2D.getImageData(0, 0, this._canvas.width, this._canvas.height);

                _ctx.putImageData(id, 0,0);

                this._canvas.width = width;
                this._canvas.height = height;

                this._context2D.drawImage(_c, 0,0, _c.width,_c.height, 0,0, this._canvas.width, this._canvas.height);
            },

            _clear: function () {                
                if ( ! this._context2D ) return;

                this.paths.length = 0;

                this._context2D.fillStyle = 'white';
                this._context2D.fillRect(0, 0, this._canvas.width, this._canvas.height);
            },

            _draw: function () {

                if ( ! this._context2D ) return;

                this.paths.forEach(function (v) {
                    this._context2D.moveTo(v[0][0], v[0][1]);
                    for (var i = 1; i < v.length; i++) {
                        this._context2D.lineTo(v[i][0], v[i][1]);
                    }
                }.bind(this));
            },

            apply: function () {

                this._context2D.fillStyle = 'rgba(50, 0, 0, 0.6)';
                if (navigator.userAgent.indexOf('Mac') > 0) {
                    this._context2D.fillStyle = 'rgba(255, 0, 0, 0.5)';
                }
                this._context2D.shadowOffsetX = 0; // 阴影Y轴偏移
                this._context2D.shadowOffsetY = 0; // 阴影X轴偏移
                this._context2D.shadowBlur = 30; // 模糊尺寸
                this._context2D.shadowColor = 'rgba(0, 0, 0, 0.5)'; // 颜色

                this._context2D.fill();

                this._context2D.beginPath();
                for ( var i = 0; i < this._parentTexture.length; i ++ ) {

                    this._parentTexture[ i ].needsUpdate = true;

                }
            }


        };

        
    };  

    setTimeout(function () {
        initMeshPrototype();   
    }, 500);
})();