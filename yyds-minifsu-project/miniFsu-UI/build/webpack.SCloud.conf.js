// 在 build 目录下新建 webpack.sync-components.prod.conf.js 文件

const webpack = require('webpack');
const path = require('path');
const utils = require('./utils');
const OptimizeCSSPlugin = require('optimize-css-assets-webpack-plugin')

const ora = require('ora')
const rm = require('rimraf')
const chalk = require('chalk')
const config = require('../config')

require('./check-versions')()
let prodEnv = require('../config/prod.env');
prodEnv.SCLOUD_CTX = '"/minifsu"'

const spinner = ora('building for production...')
function resolve(dir) {
  return path.join(__dirname, '..', dir)
}



const webpackConfig = {
  // 此处引入要打包的组件
  entry: {
    base: resolve('/src/base/index.js'),
    dispose: resolve('/src/base/dispose.js'),
    lang: resolve('/src/lang/index.js'),
    api: resolve('/src/utils/apiList.js'),
    filter: resolve('/src/utils/filter.js'),
    alarmsForDevice: resolve('/src/components/alarmsForDevice.vue'),
    alarmsForSN: resolve('/src/components/alarmsForSN.vue'),
    bindSN: resolve('/src/components/bindSN.vue'),
    devices: resolve('/src/components/devices.vue'),
    fsuInfoList: resolve('/src/components/fsuInfoList.vue'),
    fsuPKT: resolve('/src/components/fsuPKT.vue'),
    fsus: resolve('/src/components/fsus.vue'),
    importDataForTest: resolve('/src/components/importDataForTest.vue'),
    layout: resolve('/src/components/layout.vue'),
    points: resolve('/src/components/points.vue'),
    setAlarms: resolve('/src/components/setAlarms.vue'),
    baseStyle: resolve('/src/common/baseStyle.styl')
  },
  // 输出到静态目录下
  output: {
    path: resolve('../minifsu-web/src/main/resources/static/SCloud/'),
    filename: '[name].js',
  },
  resolve: {
    extensions: ['.js', '.vue', '.json'],
    alias: {
      'vue$': 'vue/dist/vue.esm.js',
      '@': resolve('src'),
    }
  },
  module: {
    rules: [
      {
        test: /\.vue$/,
        loader: 'vue-loader',
        options: {
          esModule: false, // ****** vue-loader v13 更新 默认值为 true v12及之前版本为 false, 此项配置影响 vue 自身异步组件写法以及 webpack 打包结果
          loaders: utils.cssLoaders({
            sourceMap: true,
            extract: false          // css 不做提取
          }),
          transformToRequire: {
            video: 'src',
            source: 'src',
            img: 'src',
            image: 'xlink:href'
          }
        }
      },
      {
        test: /\.js$/,
        loader: 'babel-loader',
        include: [resolve('src'), resolve('test'), resolve('node_modules/webpack-dev-server/client')],
        // options: {
        //   esModule: false
        // }
      },
      {
        test: /\.(jsx?|babel|es6)$/,
        include: process.cwd(),
        exclude: /node_modules|utils\/popper\.js|utils\/date.\js/,
        loader: 'babel-loader'
      },
      {
        test: /\.(png|jpe?g|gif|svg)(\?.*)?$/,
        loader: 'url-loader',
        options: {
          limit: 10000,
          name: utils.assetsPath('img/[name].[hash:7].[ext]')
        }
      },
      {
        test: /\.(mp4|webm|ogg|mp3|wav|flac|aac)(\?.*)?$/,
        loader: 'url-loader',
        options: {
          limit: 10000,
          name: utils.assetsPath('media/[name].[hash:7].[ext]')
        }
      },
      {
        test: /\.styl$/,
        loader: ['css-loader', 'postcss-loader', 'stylus-loader']
      },
      {
        test: /\.(woff2?|eot|ttf|otf)(\?.*)?$/,
        loader: 'url-loader',
        options: {
          limit: 10000,
          name: utils.assetsPath('fonts/[name].[hash:7].[ext]')
        }
      }
    ]
  },
  plugins: [
    new webpack.DefinePlugin({
      'process.env': prodEnv
    }),
    // 压缩JS
    new webpack.optimize.UglifyJsPlugin({
      compress: false,
      sourceMap: true
    }),
    // 压缩CSS 注意不做提取
    new OptimizeCSSPlugin({
      cssProcessorOptions: {
        safe: true
      }
    })
  ]
};

'use strict'

spinner.start()

rm(path.join(config.build.assetsRoot, config.build.assetsSubDirectory), err => {
  if (err) throw err
  webpack(webpackConfig, (err, stats) => {
    spinner.stop()
    if (err) throw err
    process.stdout.write(stats.toString({
      colors: true,
      modules: false,
      children: false, // If you are using ts-loader, setting this to true will make TypeScript errors show up during build.
      chunks: false,
      chunkModules: false
    }) + '\n\n')

    if (stats.hasErrors()) {
      console.log(chalk.red('  Build failed with errors.\n'))
      process.exit(1)
    }

    console.log(chalk.cyan('  Build complete.\n'))
    console.log(chalk.yellow(
      '  Tip: built files are meant to be served over an HTTP server.\n' +
      '  Opening index.html over file:// won\'t work.\n'
    ))
  })
})