export default {
  install (Vue, opt) {
    let filter = {
      // 内容为空
      'nullFilter': (value, suffix = '') => {
        if (value === null || value === undefined || value === "" || (Array.isArray(value) || value.length === 0)) return '-'
        return value + suffix
      },
      // 时间戳的转化
      'timeFilter': (value, level = 'second') => {
        if (!value) return '-'
        if (value <= 0) {
          return '-'
        }
        let str = ''
        if (level === 'hour' && (value / 3600).toFixed(0) > 0) {
          str += (value / 3600).toFixed(0) + '小时'
          value = value % 3600
        }
        if (level !== 'second' && (value / 60).toFixed(0) > 0) {
          str += (value / 60).toFixed(0) + '分钟'
          value = value % 60
        }
        if (value > 0) {
          str += Math.round(value) + '秒'
        }
        return str
      },
      //转化为指定的时间格式
      'dataFormatFilter': (date) => {
        var fmt = 'yyyy-MM-dd hh:mm:ss';
        var date = new Date(date);
        if (/(y+)/.test(fmt)) {
          fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length));
        }
        let o = {
          'M+': date.getMonth() + 1,
          'd+': date.getDate(),
          'h+': date.getHours(),
          'm+': date.getMinutes(),
          's+': date.getSeconds()
        };

        function padLeftZero (str) {
          return ('00' + str).substr(str.length);
        }

        for (let k in o) {
          if (new RegExp(`(${k})`).test(fmt)) {
            let str = o[k] + '';
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? str : padLeftZero(str));
          }
        }
        return fmt;
      },
      // 告警等级
      'alarmLevelFilter': (value) => {
        switch (value) {
          case 1:
          case '1':
            return '一级告警'
          case 2:
          case '2':
            return '二级告警'
          case 3:
          case '3':
            return '三级告警'
          case 4:
          case '4':
            return '四级告警'
          default:
            return '-'
        }
      },
      'number': (value) => {
        value = Number(value)
        var result = ''
        var counter = 0
        value = (value || 0).toString()
        for (var i = value.length - 1; i >= 0; i--) {
          counter++
          result = value.charAt(i) + result
          if (!(counter % 3) && i !== 0) {
            result = ',' + result
          }
        }
        return result
      },
      // 保留两位小数精度
      'toFixed2Filter': (value)=> {
        return parseFloat(value).toFixed(2);
      },
      // 信号类型
      'signalTypeFilter': (value)=> {
        switch (value) {
          case 1:
          case'1':
            return 'DO(遥控)';
          case 2:
          case "2":
            return 'AO(遥调)';
          case 3:
          case "3":
            return 'AI(遥测)';
          case 4:
          case "4":
            return 'DI(遥信)';
        }
      },
      // 信号类型
      'signalTypeFilterForCnTower': (value)=> {
        switch (value) {
          case 4:
          case'4':
            return 'DO(遥控)';
          case 5:
          case "5":
            return 'AO(遥调)';
          case 3:
          case "3":
            return 'AI(遥测)';
          case 2:
          case "2":
            return 'DI(遥信)';
        }
      },
      // 告警使能
      'alarmEnableFilter': (val) => {
        switch(val){
          case 0:
          case '0':
            return '开启';
          case 1:
          case '1':
            return '未开启';
        }
      },
      // 使能
      'enableFilter': (val) => {
        switch(val){
          case 0:
          case '0':
            return '关闭';
          case 1:
          case '1':
            return '使用';
        }
      },
      // 状态
      'statusFilter': (val) => {
        switch(val){
          case 0:
          case '0':
            return '离线';
          case 1:
          case '1':
            return '注册';
          case 2:
          case '2':
            return '在线';
          case 3:
          case '3':
            return '升级中';
        }
      },
      // 是否
      'yesOrNoFilter': (val) => {
        switch(val){
          case 0:
          case '0':
            return '否';
          case 1:
          case '1':
            return '是';
        }
      },
      // 开始结束
      'startOrEndFilter': (val) => {
        switch(val){
          case 0:
          case '0':
            return '结束';
          case 1:
          case '1':
            return '开始';
        }
      },
      // SD卡状态
      'sdStatusFilter': (val) => {
        switch(val){
          case 1:
          case '1':
            return '正常';
          case 0:
          case '0':
            return '无效';
        }
      },
      // 运营商
      'carrierFilter': (val) => {
        switch(val) {
          case 0:
          case "0":
            return '未知';
          case 1:
          case "1":
            return '移动';
          case 2:
          case "2":
            return '联通';
          case 3:
          case "3":
            return '电信';
          case "":
           return "";
        }
      },
      // 网络类型
      'networkTypeFilter': (val) => {
        switch(val) {
          case 0:
          case "0":
            return '无服务';
          case 2:
          case "2":
            return '2G';
          case 3:
          case "3":
            return '3G';
          case 4:
          case "4":
            return '4G';
          case 5:
          case "5":
            return '未知';
          case "":
           return "";
        }

      },
       // 告警上报状态
      'reportedStateFilter': (val) => {
        switch(val) {
          case 0:
          case "0":
            return '未上报';
          case 1:
          case "1":
            return '上报成功';
          case 2:
          case "2":
            return '正在上报';
          case 3:
          case "3":
            return '关联过滤';
          case "":
           return "";
        }

      },
      /****** cmcc ******/
      'cmccDeviceTypeFilter': (val) => {
        switch (val) {
          case 1:
          case '1':
            return `${val}(高压配电)`;
          case 2:
          case '2':
            return `${val}(低压交流配电)`;
          case 3:
          case '3':
            return `${val}(变压器)`;
          case 4:
          case '4':
            return `${val}(低压直流配电)`;
          case 5:
          case '5':
            return `${val}(发电机组)`;
          case 6:
          case '6':
            return `${val}(开关电源)`;
          case 7:
          case '7':
            return `${val}(铅酸电池)`;
          case 8:
          case '8':
            return `${val}(UPS设备)`;
          case 9:
          case '9':
            return `${val}(UPS配电)`;
          case 11:
          case '11':
            return `${val}(机房专用空调)`;
          case 12:
          case '12':
            return `${val}(中央空调末端)`;
          case 13:
          case '13':
            return `${val}(中央空调主机)`;
          case 14:
          case '14':
            return `${val}(变换设备)`;
          case 15:
          case '15':
            return `${val}(普通空调)`;
          case 16:
          case '16':
            return `${val}(极早期烟感)`;
          case 17:
          case '17':
            return `${val}(机房环境)`;
          case 18:
          case '18':
            return `${val}(电池恒温箱)`;
          case 68:
          case '68':
            return `${val}(锂电池)`;
          case 76:
          case '76':
            return `${val}(动环监控)`;
          case 77:
          case '77':
            return `${val}(智能通风换热)`;
          case 78:
          case '78':
            return `${val}(风光设备)`;
          case 87:
          case '87':
            return `${val}(高压直流电源)`;
          case 88:
          case '88':
            return `${val}(高压直流电源配电)`;
          case 92:
          case '92':
            return `${val}(智能电表)`;
          case 93:
          case '93':
            return `${val}(智能门禁)`;
        }
      }
    };

    for (let key in filter) {
      Vue.filter(key, filter[key])
    }
    Vue.filter('modalFilter', (value, args) => {
      if (!args) {
        return value
      }
      if (!Array.isArray(args)) {
        args = [args]
      }
      args.forEach(v => {
        let _filter = v.replace(/\(.*\)/, '')
        let _params = v.match(/\(.*\)/) && v.match(/\(.*\)/)[0]
        let _apply = [value]
        if (_params) {
          _params = _params.substr(1, _params.length - 2)
          _params = '[' + _params + ']'
          _params = JSON.parse(_params)
          _apply = _apply.concat(_params)
        }

        value = filter[_filter].apply(null, _apply)
      })
      return value
    })
  }
}
