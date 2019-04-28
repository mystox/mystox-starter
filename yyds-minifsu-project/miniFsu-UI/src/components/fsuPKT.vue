<template>
  <div class="fsuList flex-column">
    <sc-breadcrumb
      :urls="[
        {name: 'SN列表', path: '/fsus'},
        {name: 'SN 日志记录'},
      ]"
    ></sc-breadcrumb>
    <operation-bar-layout style="{}">
       <div slot="query">
        <el-form class=""
                 :model="searcher"
                 ref="searcher"
                 label-position="right"
                 :inline="true">
          <!--使用重置功能需要给item元素传入prop属性-->
          <!-- <el-form-item label="告警名称" prop="name" label-width="80px">
            <el-input v-model="searcher.sn" placeholder="请输入告警名称">
            </el-input>
          </el-form-item> -->
          <el-form-item label="时间" prop="name" label-width="45px">
            <el-date-picker
              v-model="searcher.timeRange"
              type="datetimerange"
              :picker-options="pickerOptions"
              placeholder="选择时间范围"
              :clearable = "false"
              align="right">
            </el-date-picker>
          </el-form-item>
        </el-form>
      </div>
      <div slot="operate">
        <el-button type="primary" @click="getTerminalLog()">查询</el-button>
      </div>
    </operation-bar-layout>
    <table-box
      class="flex-1"
      row-class-name="cursor-point"
      :row-click = 'intoNextPage'
      :loading = "loading"
      :stripe = "true"
      :border = "true"
      :pagination = "pagination"
      :data="fsuList"
      @selection-change="handleSelectionChange">
      <el-table-column type="selection">
      </el-table-column>
      <el-table-column
        v-for="item in columns" :key="item.label"
        :formatter="$tableFilter(item.filter)"
        :prop="item.value"
        :label="item.label"
        :min-width="item.width"
        :fixed = 'item.fixed'
        :className = 'item.className'
        :resizable='false'>
      </el-table-column>
      <el-table-column label="报文">
        <template slot-scope="scope">
          <span>{{JSON.stringify(scope.row.payload.payload)}}</span>
        </template>
      </el-table-column>
    </table-box>
    <modal :option="optionBind" ref="bindDialog"></modal>
  </div>
</template>

<script>
  export default {
    name: 's-sites',
    props: {},
    data () {
      return {
        searcher: {
          name: '',
          timeRange: [
            new Date() - 1 * 24 * 60 * 60 * 1000, 
            new Date().getTime()
          ],
        },
        pickerOptions: {
          shortcuts: [{
            text: '最近一周',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
              picker.$emit('pick', [start, end]);
            }
          }, {
            text: '最近一个月',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
              picker.$emit('pick', [start, end]);
            }
          }, {
            text: '最近三个月',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
              picker.$emit('pick', [start, end]);
            }
          }]
        },
        multipleSelection: [],
        fsuList: [],
        userGroups: [],
        columns: [
          {
            label: '流量大小',
            value: 'payloadSize',
            // width: 270,
            filter: 'nullFilter',
            className: '',
          },
          {
            label: '报文类型',
            value: 'pktTypeName',
            // width: 270,
            filter: 'nullFilter',
            className: '',
          },
          {
            label: '时间',
            value: 'recordTime',
            // width: 270,
            filter: 'dataFormatFilter',
            className: '',
          },
        ],
        modifyCont: {
          
        },
        pagination: {
          total: 0,
          currentPage: 1,
          pageSize: 15,
          onChange: this.onPaginationChanged
        },
        loading: false,
      }
    },
    computed: {
      optionBind() {
        return {
          name: '绑定',
          form: {
            'SN ID': [
              {
                type: 'span',
                text: this.modifyCont.sN,
                rule: {}
              }
            ],
            'SN 设备列表': [
              {
                type: 'textarea',
                name: 'snDeviceList',
                defaultValue: '123123123\n123123123\n123123123\n123123123\n123123123\n',
                disabled: true,
                rows: 8,
                rule: {}
              }
            ],
            'FSU ID': [
              {
                type: 'input',
                name: 'fsuId',
                rule: {}
              }
            ],
            'FSU 设备列表': [
              {
                type: 'textarea',
                name: 'devIds',
                rows: 8,
                rule: {}
              }
            ],
          },
          clearText: this.$t('OPERATION.CLEAR'),
          clear: this.clear,
          executeText: this.$t('OPERATION.CONFIRM'),
          execute: this.executeModify,
          style: {
            // width: '50%',
          }
        }
      },
    },
    methods: {
      addMockData() {
        for (let i = 0; i < 10; i++) {
          this.fsuList.push({
            "recordTime":1556103423840,
            "payloadSize":132,  // 流量大小
            "payload":{
                "payload":{
                    "memUse":"14%",
                    "csq":31,
                    "pktType":11,
                    "sysTime":1556103420,
                    "SN":"MINI201904180005",
                    "cpuUse":"99%"
                },
                "msgId":"01556103420"
            },
            "pktType": 11,  // 报文类型
            "id":"5cc040ff2ebfe763b82a3fa6",
            "sn":"MINI201904180005"
          });
        }
        this.pagination.total = this.fsuList.length;
      },

      getTerminalLog() {
        let param = {
          sn: this.$route.query.sN,
          startTime: new Date(this.searcher.timeRange[0]).getTime(),
          endTime: new Date(this.searcher.timeRange[1]).getTime(),
          page: this.pagination.currentPage ,
          count: this.pagination.pageSize ,
        };
        this.$api.getTerminalLog(param, this.$route.query.sN).then((res) => {
          this.pagination.total = res.data.totalSize;
          this.fsuList = res.data.list;
          this.loading = false;
        }, (err)=> {
          if (err) this.loading = false;
        })
      },

      onPaginationChanged (pagination) {
        this.pagination.pageSize = pagination.pageSize;
        this.pagination.currentPage = pagination.currentPage;
        this.getTerminalLog()
      },

      handleSelectionChange(val) {
        this.multipleSelection = this.comFunc.map(val, "username");
      },

      intoNextPage() {
      },

      executeAdd(res) {
        this.addContent(res)
      },
      executeModify(res) {
        this.modifyCont = Object.assign({}, this.modifyCont, res);
        this.modifyCont.devIds = res.devIds.split('\n');
        // this.updateContent(res);
      },
      clear(res) {
      //  console.log(res)
      },

    },
    mounted() {
      // this.addMockData();
      this.getTerminalLog();
    }
  }
</script>

<style lang="stylus" rel="stylesheet/stylus">
  .fsuList
    .table-wrapper
      margin-top: 24px;
      .dialog-wrapper
        .el-dialog
          .dialog-footer
            .el-button
              width: 160px;
              height: 30px;
              padding: 0 15px;
              &:nth-of-type(1)
                margin-right: 25px;
</style>


