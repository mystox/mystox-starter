export default {
  MENU: {
    HOME: {
      NAME: '首页'
    },
    SYS: {
      NAME: '系统管理',
      BASIC_INFO: '基本信息',
      TIME_SETTING: '时间维护',
      MAINTAIN: '系统维护',
      CONFIG_MANAGE: '配置管理',
      RESUME_SETTING: '恢复出厂',
    },
    DATA: {
      NAME: '数据监控',
      CMCC: {
        REAL_TIME_DATA: '实时数据',
        HISTORY_DATA: '历史数据',
        DATA_STORAGE_ROLE: '数据存储规则',
      },
      CRCC: {
        REALTIME_PERFORMANCE: '实时性能',
        ACTIVE_ALARM: '活动告警',
        HISTORY_ALARM: '历史告警',
        ALARM_PARAM: '告警参数',
        ALARM_ASSOCIATION: '告警关联'
      }
    },
    ALARM: {
      NAME: '告警管理',
      ACTIVE_ALARM: '活动告警',
      CNTOWER_ACTIVE_ALARM: '当前告警',
      HISTORY_ALARM: '历史告警',
      ALARM_PARAM: '告警参数',
      ALARM_ASSOCIATION: '告警关联'
    },
    VIDEO: {
      NAME: '视频监控',
      PARAMS_SETTING: '参数设置',
      CAMERA: '摄像头'
    },
    SCENE: {
      NAME: '站点配置',
      CRCC: {
        SITE_CONFIG: '机房配置',
        SERVER_CONFIG: '服务器配置',
      },
      CMCC: {
        SITE_CONFIG: '机房配置',
        DEVICE_CONFIG: '设备配置',
        SERVER_CONFIG: '服务器配置',
      },
      CNTower: {
        SITE_CONFIG: '机房配置',
        DEVICE_CONFIG: '设备配置',
        REGISTER_CONFIG: '注册机配置',
        VPN_SERVER_CARRIERS_CONFIG: 'VPN服务运营商',
        VPN_SERVER_CONFIG: 'VPN服务器',
        OMC_CONFIG: 'OMC配置',
        SERVER_GROUPS_CONFIG: '服务器组配置',
      },
      COLLECTOR_MANAGE: '采集器管理',
      SCENE_INTERFACE_MANAGE: '场景接口管理'
    },
    NETWORK: {
      NAME: '网络',
      TCP_IP: 'TCP/IP',
      LINK: '连接',
      E1: 'E1'
    },
    SECURITY: {
      NAME: '安全',
      USER: '用户',
      USER_GROUP: '用户组'
    }
  },

  OPERATION: {
    SEARCH: '查询',
    RESET: '重置',
    ADD: '增加',
    APPLY: '应用',
    REFRESH: '刷新',
    DELETE: '删除',
    CLEAR: '取消',
    CONFIRM: '确定',
    GET: '获取',
    SET: '设置'
  },

  NAV: {
    NAV_INSTRUCTION: '说明',
    NAV_LOGOUT: '退出'
  },

  // 数据监控
  DATA: {
    // 实时数据设备列表
    REAL_TIME_DATA: {
      DEVICE_ID: '设备ID',
      DEVICE_BRAND: '设备厂家',
      VERSION: '版本描述',
      BEGIN_RUN_TIME: '投入运营时间',
      SERIAL_NO: '序列号',
      GUARANTEE_PERIOD: '保质期(年)',

      // 单个设备信息列表
      DETAIL: {
        DIALOG_MODIFY: {
          TITLE: '信息显示',
          TITLE2: '参数修改'
        },
        SIGNAL_NAME: '监控点名称',
        SIGNAL_ID: '监控点ID',
        SIGNAL_TYPE: '监控点类型',
        VALUE: '监控点数值',
        TIME: '采集时间',
        OPERATION: '操作',
        SIGNAL_NO: '监控点顺序号',
        STORAGE_INTERVAL: '存储时间间隔',
        STATUS: '数据状态',
        ABS_VAL: '绝对阈值',
        REL_VAL: '百分比阈值',
        G2_ENABLE: '精简模式启用',
      }
    },
    // 历史数据设备列表
    HIS_DEVICE_LIST: {
      DEVICE_ID: '设备ID',
      DEVICE_BRAND: '设备厂家',
      VERSION: '版本描述',
      BEGIN_RUN_TIME: '投入运营时间',
      // 单个设备信息列表
      DETAIL: {
        DIALOG_MODIFY: {
          TITLE: '修改监控点参数'
        },
        SIGNAL_NAME: '监控点名称',
        SIGNAL_ID: '监控点ID',
        SIGNAL_TYPE: '监控点类型',
        VALUE: '监控点数值',
        TIME: '采集时间',
        OPERATION: '操作',
        SIGNAL_NO: '监控点顺序号',
        STORAGE_INTERVAL: '存储时间间隔',
        ABS_VAL: '绝对阈值',
        REL_VAL: '百分比阈值'
      }
    },
  },

  // 告警管理
  ALARM_MANAGE: {
    // 实时告警
    ACTIVE_ALARM: {
      SERIAL_NO: '序列号',
      NM_ALARM_ID: '网管告警编号',
      DEVICE_NAME: '设备名称',
      SIGNAL_NAME: '监控点名称',
      ALARM_NAME: '告警名称',
      ALARM_LEVEL: '告警等级',
      ALARM_TIME: '采集时间',
      EVENT_VALUE: '触发值',
      ALARM_DESC: '告警描述',
      START_TIME: '开始时间',

      DEVICE_ID: '设备ID',
      SIGNAL_ID: '监控点ID',
      ALARM_FLAG: '告警标识',
      REPORTED: '上报状态',
      LAST_REPORT_TIME: '最后上报时间'
    },
    // 历史告警，同实时告警

    // 告警参数
    ALARM_PARAMS: {
      DEVICE_NAME: '设备名称',
      DEVICE_ID: '设备ID',
      BRAND: '设备厂家',
      VERSION: '版本描述',
      BEGIN_RUN_TIME: '投入运营时间',

      // 告警参数详情
      DETAIL: {
        DIALOG_MODIFY: {
          TITLE: '修改告警点参数'
        },
        SIGNAL_NAME: '告警点名称',
        SIGNAL_ID: '告警点ID',
        SIGNAL_NO: '告警点顺序号',
        DELAY: '告警产生延时',
        RECOVER_DELAY: '告警恢复延时',
        FLIP_TIME: '告警翻转时间',
        THRESHOLD: '告警阈值',
        ALARM_ENABLE: '告警屏蔽',

        CO_TYPE: '信号类型',
        HYSTERSIS: '告警恢复回差',
        LEVEL: '告警等级',
        CO_ID: '关联告警点ID',
        G2_ENABLE: '精简模式启用',
        ALARM_DESC: '告警描述',
        NORMAL_DESC: '正常描述',
        OPERATION: '操作'
      }
    },
    // 告警关联
    ALARM_CORRELATE: {
      DIALOG_NEW: {
        TITLE: '添加告警关联规则'
      },
      DIALOG_MODIFY: {
        TITLE: '修改告警规则'
      },
      PT_NAME: '告警点名称',
      FUNC_NAME: '关联动作',
    }

  },
  // 视频监控
  VIDEO: {
    DIALOG_NEW: {
      TITLE: '新增摄像头'
    },
    DIALOG_MODIFY: {
      TITLE: '修改摄像头配置'
    },
    CAMERA_NAME: '摄像头名称',
    CAMERA_MANUFACTOR: '摄像头厂家',
    CAMERA_PORT: ' 摄像头端口号',
    CAMERA_MODEL: '摄像头序列号',
    CAMERA_IP: '摄像头IP',
    OPERATION: '操作',
    RESOLUTION: '分辨率',
    BIT_RATE: '码流',
    PRERECORD_DURATION: '预录时间',
    CAPTURE_INTERVAL: '抓拍间隔',
    CAPTURE_NUM: '抓拍张数',
    ONLINE: '是否在线',
    SD_STATUS: 'sd的状态',
    INIT_TIME: '初始化时间',
    DEFAULT_CONFIG: '是否是默认配置'
  },
  // 站点配置
  SITE_CONFIG: {
    // 机房配置
    STATION_CONFIG: {
      NAME: '机房配置',
      FSU_ID: 'FSU ID',
      USERNAME: '用户名',
      PASSWORD: '密码',
      SITE_NAME: '站点名称',
      SITE_ID: '站点编号',
      ROOM_NAME: '机房名称',
      ROOM_ID: '机房编号',
      TYPE: '站点类型',
      DATA_CENTER: '数据中心',
      COMMUNICATION_BUILDING: '通信机楼',
      TRANSMISSION_NODE: '传输节点',
      COMMUNICATION_STATION: '通信基站',
      DESC: '描述',
      FTP_USERNAME: 'FTP账号',
      FTP_PASSWORD: 'FTP密码',
      STATION_STATE: '基站状态',
      TESTING: '测试',
      COME_ACROSS: '交维',
      ENGINEERING: '工程',
      ADDRESS: '地址'
    },
    // 设备配置
    DEVICE_CONFIG: {
      DIALOG_NEW: {
        TITLE: '新增设备'
      },
      DIALOG_MODIFY: {
        TITLE: '修改设备'
      },
      DEVICE_NAME: '设备名称',
      DEVICE_ID: '设备ID',
      DEVICE_TYPE: '设备类型',
      DEVICE_SUB_TYPE: '设备子类型',
      RESERVE_NUM: '预留',
      SYS_NO: '系统序列号',
      DEVICE_NO: '设备序列号',
      DEVICE_SERIAL_NO: '资源编号',
      MODEL: '设备型号',
      BRAND: '设备厂家',
      RATED_CAPACITY: '额定容量',
      DEVICE_DESC: '设备描述',
      group: '组名',
      DEVICE_START_TIME: '设备投入使用时间',
      VERSION: '版本描述'
    },
    // 服务器配置
    SERVER_CONFIG: {
      DIALOG_NEW: {
        TITLE: '新增服务器'
      },
      DIALOG_MODIFY: {
        TITLE: '修改服务器'
      },
      SC_NAME: '服务器名称',
      SC_IP: '服务器IP',
      SC_PORT: '服务器端口',
      HB_PERIOD: '心跳周期（秒）',
      HB_TIMEOUT_LIMIT: '心跳超过次数上限',
      LOGIN_INTERVAL: '注册时间间隔（秒）',
      LOGIN_LIMIT: '注册上限',
      ALARM_REPORT_LIMIT: '单条告警上报次数',
      ALARM_REPORT_INTERVAL: '告警上报时间间隔（秒）',
      PRIORITY: '优先级',
      OPERATION: '操作',
    },
    // 注册机配置
    REGISTER_CONFIG: {
      DIALOG_NEW: {
        TITLE: '新增服务器'
      },
      DIALOG_MODIFY: {
        TITLE: '修改服务器'
      },
      SC_NAME: '服务器名称',
      SC_IP: '服务器IP',
      SC_PORT: '服务器端口',
      HB_PERIOD: '心跳周期（秒）',
      HB_TIMEOUT_LIMIT: '心跳超过次数上限',
      LOGIN_INTERVAL: '注册时间间隔（秒）',
      LOGIN_LIMIT: '重启前失败轮数上限',
      LOGIN_FAIL_LIMIT: '每轮注册失败次数上限',
      ALARM_REPORT_LIMIT: '单条告警上报次数',
      ALARM_REPORT_INTERVAL: '告警上报时间间隔（秒）',
      PRIORITY: '优先级',
      SRV_GROUP: '服务器组',
      OPERATION: '操作',
    },
    // OMC配置
    OMC_CONFIG: {
      DIALOG_NEW: {
        TITLE: '新增服务器'
      },
      DIALOG_MODIFY: {
        TITLE: '修改服务器'
      },
      ID: 'OMC ID',
      IP: 'OMC IP',
      PORT: 'OMC 端口',
      HB_PERIOD: '心跳周期（秒）',
      HB_TIMEOUT_LIMIT: '心跳超过次数上限',
      LOGIN_INTERVAL: '注册时间间隔（秒）',
      LOGIN_LIMIT: '重启前失败轮数上限',
      LOGIN_FAIL_LIMIT: '每轮注册失败次数上限',
      ALARM_REPORT_LIMIT: '单条告警上报次数',
      ALARM_REPORT_INTERVAL: '告警上报时间间隔（秒）',
      PRIORITY: '优先级',
      SRV_GROUP: '服务器组',
      OPERATION: '操作',
    },
    // vpn服务器运营商管理
    VPN_SERVER_CARRIERS_CONFIG: {
      NO: '编号',
      NAME: '姓名',
      ENABLE: '使用状态',
      PRIORITY: '优先级',
      OPERATION: '操作',
    },
    // vpn服务器运营商管理
    VPN_SERVER_CONFIG: {
      NO: '编号',
      CM: '移动vpn服务器IP',
      CU: '联通vpn服务器IP',
      CT: '电信vpn服务器IP',
      RSVD: '备用vpn服务器IP',
      ENABLE: '使用状态',
      PRIORITY: '优先级',
      SRV_GROUP: '服务器组',
      OPERATION: '操作',
    },
    // 服务器组管理
    SERVER_GROUP_CONFIG: {
      DIALOG_MODIFY: {
        TITLE: '设置服务器组'
      },
      NAME: '服务器名称',
      GROUP: '服务器组编号',
      ENABLE: '使用状态',
    },
    // 场景接口配置
    SCENE_MANAGE: {
      DIALOG_NEW: {
        TITLE: '新增场景接口'
      },
      DIALOG_MODIFY: {
        TITLE: '新增场景接口'
      },
      SCENE_NAME: '应用场景名称',
      INTERFACE_STATE: '接口状态',
      SCENE_ID: '场景ID',
      SCENE_ALARM_NO: '告警序列号',
      SCENE_ENABLE: '是否启用',
    },
    // 采集器配置
    COLLECTOR_MANAGE: {
      DIALOG_NEW: {
        TITLE: '新增采集器'
      },
      DIALOG_MODIFY: {
        TITLE: '修改采集器配置'
      },
      COLLECTOR_NAME: '采集器名称',
      COLLECTOR_IP: '采集器IP',
      COLLECTOR_PORT: '采集器端口',
      COLLECTOR_STATUS: '状态',
      OPERATION: '操作',
    },
  },
  // 安全
  SECURITY: {
    // 用户
    USER: {
      DIALOG_NEW: {
        TITLE: '新增用户',
      },
      DIALOG_MODIFY: {
        TITLE: '修改用户'
      },
      USER_NAME: '用户',
      USER_PASSWORD: '密码',
      GROUP_NAME: '组名',
      COMMENT: '备注',
      OPERATION: '操作',
    },
    // 用户组
    USER_GROUP: {
      DIALOG_NEW: {
        TITLE: '新增用户组'
      },
      DIALOG_MODIFY: {
        TITLE: '修改用户组'
      },
      GROUP_NAME: '组名',
      AUTHORIZATION: '权限',
      COMMENT: '备注',
      OPERATION: '操作',
    },
  },


  DEMO: {
    MODAL_NAME: '这个是DEMO_TITLE表头',
    MODAL_FORM_ITEM_NAME: '姓名'
  }
}
