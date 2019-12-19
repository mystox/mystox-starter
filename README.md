### 前端代码仓库
http://git.kongtrolink.com:3000/luoc/MC_WEB

然后下面的配置都没有用

### 前端环境
1 安装node 下载地址 https://nodejs.org/en/

2 打开**terminal**。输入下面命令，将npm资源改成国内淘宝镜像资源。不用翻墙，你懂得。
```
npm install -g cnpm --registry=https://registry.npm.taobao.org
```
3 **minifsu2_framework/yyds-minifsu-project/miniFsu-UI/** 目录下运行下面命令，下载依赖
```
cnpm install
// 不使用淘宝镜像可以直接运行 npm install
```

#### 前端开发环境运行

1 打开**minifsu2_framework/yyds-minifsu-project/miniFsu-UI/config/index.js** 文件。

2 配置 **teaget**属性为后端服务IP。案例如下。
```
module.exports = {
  dev: {
    // Paths
    assetsSubDirectory: 'static',
    assetsPublicPath: '/',
    proxyTable: {
      '/api':{
        target:'http://172.16.6.39:8081/',  // wumy
        changeOrigin: true,
        pathRewrite:{
         '^/api':''
        }
      }
    },
...
}
```
3 minifsu2_framework/yyds-minifsu-project/miniFsu-UI/目录下运行
```
npm run dev
```

4 程序自动会打开浏览器， 如果不行， 打开浏览器手动输入 http://localhost:8080/


#### 前端生产环境资源打包
1 打开**minifsu2_framework/yyds-minifsu-project/miniFsu-UI/config/index.js** 文件。 index、assetsRoot属性如下配置。
```
build: {
    // Template for index.html
    // index打包后的入口文件，存放目录
    index: path.resolve(__dirname, '../../minifsu-web/src/main/resources/static/index.html'),

    // Paths
    // assetsRoot为打包后的js文件，存放目录
    assetsRoot: path.resolve(__dirname, '../../minifsu-web/src/main/resources/static'),
    assetsSubDirectory: 'static',
    assetsPublicPath: '/',
...
}

```

2 minifsu2_framework/yyds-minifsu-project/miniFsu-UI/目录下运行如下命令。即可打包到执行目录
```
npm run build
```
