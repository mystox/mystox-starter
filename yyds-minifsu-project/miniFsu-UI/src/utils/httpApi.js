import axios from 'axios';
import {Message, Notification, Loading} from 'element-ui';
const base = process.env.API_HOST;

// use custom message component
const message = (msg, type, showClose)=> {
    let msgs = {
        success: msg || '操作成功',
        error: msg || '操作失败'
    };
    Message({
        message: msgs[type],
        type: type,
        showClose: showClose,
    })
}

// get
const get = (url, param, successMsg, failMsg)=> {
    return axios.get(url, {params: param}).then(
        (response)=> {
            let res = response.data;
            if (res.success) {
                successMsg && message(successMsg || res.info, 'success', true);
                return Promise.resolve(res);
            } else {
                message(failMsg || res.info, 'error', true);
            }
        }, 
        (httpErr)=> {
            message('连接服务器失败', 'error', true);
            return Promise.reject(httpErr);
        }
    ).catch(err=> {
        return Promise.reject(err);
    })
}

// post
const post = (url, param, successMsg, failMsg)=> {
    return axios({
            method: 'post',
            url:url,
            data: param
        }).then(
            (response)=> {
                let res = response.data;
                if (res.success) {
                    successMsg && message(successMsg || res.info, 'success', true);
                    return Promise.resolve(res);
                } else {
                    message(failMsg || res.info, 'error', true);
                }
            },
            (httpErr)=> {
                message('连接服务器失败', 'error', true);
                return Promise.reject(httpErr);
            }
    ).catch(err=> {
        return Promise.reject(err);
    })
}

export default {
    install(Vue, opt) {
        Vue.prototype.$api = {
            // 导入终端列表
            importTerminalList(param) {
                return post(`${base}fsu/terminal/import`, param);
            },

            // 绑定终端列表
            setFsu(param) {
                return post(`${base}fsu/setFsu`, param);
            },

            // 获取终端列表
            getFsuList(param) {
                return post(`${base}fsu/list`, param);
            },

            // 获取设备列表
            getDeviceList(param, sn) {
                return post(`${base}fsu/getDeviceList?sn=${sn}` , param)
            },

            // 获取设备型号点实时数据
            getSignalList(param, sn) {
                return post(`${base}dataMnt/getSignalList?sn=${sn}`, param)
            },

            // 设置信号点值
            setSignal(param, sn) {
                return post(`${base}dataMnt/setSignal?sn=${sn}`, param)
            },

            // 获取告警门限列表
            getThreshold(param, sn) {
                return post(`${base}dataMnt/getThreshold?sn=${sn}`, param)
            },

            // 设置告警门限值
            setThreshold(param, sn) {
                return post(`${base}dataMnt/setThreshold?sn=${sn}`, param)
            },

            // 获取实时告警列表
            getAlarmList(param, sn) {
                return post(`${base}dataMnt/getAlarmList?sn=${sn}`, param)
            },

            // 告警列表导入
            alarmModelImport(param, sn) {
                return post(`${base}fsu/alarmModel/import?sn=${sn}`, param)
            },

            // 信号点列表导入
            signalModelImport(param, sn) {
                return post(`${base}fsu/signalModel/import?sn=${sn}`, param)
            },

            // 获取终端日志报文信息
            getTerminalLog(param, sn) {
                return post(`${base}fsu/getTerminalLog?sn=${sn}`, param)
            },

            // 获取SN状态信息
            getRunState(param, sn) {
                return post(`${base}fsu/getRunState?sn=${sn}`, param)
            },
        }
    }
}


