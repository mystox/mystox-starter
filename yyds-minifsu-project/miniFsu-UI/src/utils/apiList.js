const base = process.env.API_HOST;
export default (openFullScreen = () => {}, closeFullScreen = () => {}) => ({
  install (Vue, opt) {
    // use custom message component
    const message = (msg, type, showClose) => {
      let msgs = {
        success: msg || "操作成功",
        error: msg || "操作失败"
      };
      Vue.prototype.$message({
        message: msgs[type],
        type: type,
        showClose: showClose
      });
    };

    // get
    const get = (url, param, successMsg, failMsg) => {
      openFullScreen();
      return Vue.prototype.$http
        .get(url, { params: param })
        .then(
          response => {
            closeFullScreen();
            let res = response.data;
            if (res.success) {
              successMsg && message(successMsg || res.info, "success", true);
              return Promise.resolve(res);
            } else {
              message(failMsg || res.info, "error", true);
            }
          },
          httpErr => {
            message("连接服务器失败", "error", true);
            return Promise.reject(httpErr);
          }
        )
        .catch(err => {
          closeFullScreen();
          return Promise.reject(err);
        });
    };

    // post
    const post = (url, param, successMsg, failMsg) => {
      openFullScreen();
      return Vue.prototype.$http({
        method: "post",
        url: url,
        data: param
      })
        .then(
          response => {
            closeFullScreen();
            let res = response.data;
            if (res.success) {
              successMsg && message(successMsg || res.info, "success", true);
              return Promise.resolve(res);
            } else {
              message(failMsg || res.info, "error", true);
              return Promise.reject(res.info);
            }
          },
          httpErr => {
            message("连接服务器失败", "error", true);
            return Promise.reject(httpErr);
          }
        )
        .catch(err => {
          closeFullScreen();
          return Promise.reject(err);
        });
    };
    const successMsg = "操作成功";
    const failMsg = "操作失败";

    const $api = {
      // 登录
      authLogin (param) {
        // return post(`${base}auth/login`, param);
        return Vue.prototype.$http.post('/api/proxy_ap/system/login_login.action', `username=${param.username}&password=${param.password}`, { 
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded' 
          } 
        }).then((res)=> {
          return post(`/api/proxy_ap/commonFunc/saveCurrentService`, res.data.data[0])
        }).then((res)=> {
          return Vue.prototype.$http.post('/api/proxy_ap/system/switchEnterprise', `GuoDong`, { 
            headers: {
              'Content-Type': 'application/json' 
            } 
          })
        }).then((res)=>{
          // console.log(res)
          return post(`/api/proxy_ap/commonFunc/saveCurrentService`, res.data.data[2])
        })
      },
      
      // 导入终端列表
      importTerminalList (param) {
        return post(`${base}fsu/terminal/import`, param, successMsg, failMsg);
      },

      // 绑定终端列表
      setFsu (param) {
        return post(`${base}fsu/setFsu`, param, successMsg, failMsg);
      },

      // 解绑
      unbind (param, sn) {
        return post(`${base}fsu/unbind?sn=${sn}`, param, successMsg, failMsg);
      },

      // 获取升级文件列表信息
      getDocumentList (param, sn) {
        return post(`${base}resource/getDocumentList`, param);
      },

      // 获取终端列表
      getFsuList (param) {
        return post(`${base}fsu/list`, param);
      },

      // 获取设备列表
      getDeviceList (param, sn) {
        return post(`${base}fsu/getDeviceList?sn=${sn}`, param);
      },

      // 获取设备型号点实时数据
      getSignalList (param, sn) {
        return post(`${base}dataMnt/getSignalList?sn=${sn}`, param);
      },

      // 设置信号点值
      setSignal (param, sn) {
        return post(`${base}dataMnt/setSignal?sn=${sn}`, param, successMsg, failMsg);
      },

      // 获取告警门限列表
      getThreshold (param, sn) {
        return post(`${base}dataMnt/getThreshold?sn=${sn}`, param);
      },

      // 设置告警门限值
      setThreshold (param, sn) {
        return post(`${base}dataMnt/setThreshold?sn=${sn}`, param, successMsg, failMsg);
      },

      // 获取实时告警列表
      getAlarmList (param, sn) {
        return post(`${base}dataMnt/getAlarmList?sn=${sn}`, param);
      },

      // 告警列表导入
      alarmModelImport (param, sn) {
        return post(`${base}fsu/alarmModel/import?sn=${sn}`, param, successMsg, failMsg);
      },

      // 信号点列表导入
      signalModelImport (param, sn) {
        return post(`${base}fsu/signalModel/import?sn=${sn}`, param, successMsg, failMsg );
      },

      // 获取终端日志报文信息
      getTerminalLog (param, sn) {
        return post(`${base}fsu/getTerminalLog?sn=${sn}`, param);
      },

      // 获取SN状态信息
      getRunState (param, sn) {
        return post(`${base}fsu/getRunState?sn=${sn}`, param);
      },

      // 编译
      compiler (param, sn) {
        return post(`${base}fsu/compiler?sn=${sn}`, param);
      },

      // 升级
      upgrade (param, sn) {
        return post(`${base}fsu/upgrade?sn=${sn}`, param, successMsg, failMsg);
      }
    };

    Vue.prototype.$api || (Vue.prototype.$api = {});
    for (let i in $api) {
      // 云管在离开页面的时候会删除toDelete的api
      Vue.prototype.$api[i] || (Vue.prototype.$api[i] = $api[i]);
      $api[i].toDelete = true;
    }
  }
});
