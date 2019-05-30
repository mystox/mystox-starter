<template>
  <div class="alarmList flex-column">
    <sc-breadcrumb 
      :urls="[
        {name: 'SN列表', path: _ctx + '/fsus'},
        {name: '实时告警'},
      ]"
    ></sc-breadcrumb>
    <!-- <operation-bar-layout style="{}">
      <div slot="query">
        <el-form class=""
                 :model="searcher"
                 ref="searcher"
                 label-position="right"
                 :inline="true">
          <el-form-item label="告警名称" prop="name" label-width="80px">
            <el-input v-model="searcher.sn" placeholder="请输入告警名称">
            </el-input>
          </el-form-item>
          <el-form-item label="时间" prop="name" label-width="80px">
            <el-date-picker
              v-model="searcher.timeRange"
              type="datetimerange"
              :picker-options="pickerOptions"
              placeholder="选择时间范围"
              align="right">
            </el-date-picker>
          </el-form-item>
        </el-form>
      </div>
      <div slot="operate">
        <el-button type="primary" @click="goSearch(false)">查询</el-button>
      </div>
    </operation-bar-layout> -->
    <table-box
      class="flex-1"
      row-class-name="cursor-point"
      :row-click = 'intoNextPage'
      :loading = "loading"
      :stripe = "true"
      :border = "true"
      :data="alarmList"
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
      <el-table-column :label="'告警描述'" fixed="right">
        <template slot-scope="scope">
          <span>{{scope.row.name}}值为{{scope.row.value}}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column
        :label="$t('SECURITY.USER.OPERATION')" fixed="right">
        <template slot-scope="scope">
          <i style="color: #20A0FF; cursor: pointer;" @click="showDialog('threshold', scope.row)">门限设置</i>
        </template>
      </el-table-column> -->
    </table-box>
    <modal :option="optionModify" ref="threshold"></modal>
  </div>
</template>

<script>
  export default {
    name: 's-points',
    props: {},
    data () {
      return {
        searcher: {
          name: '',
          timeRange: [
            new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate(), 0, 0, 0), 
            new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate() + 1, 0, 0, 0)
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
        alarmList: [],
        userGroups: [],
        columns: [
           {
            label: '告警名称',
            value: 'name',
            // width: 270,
            filter: 'nullFilter',
            className: '',
          },
          {
            label: '告警点 ID',
            value: 'alarmId',
            // width: 270,
            filter: 'nullFilter',
            className: '',
          },
          {
            label: '告警点值',
            value: 'value',
            // width: 270,
            filter: 'nullFilter',
            className: '',
          },
          {
            label: '门限',
            value: 'threshold',
            // width: 270,
            filter: 'nullFilter',
            className: '',
          },
          {
            label: '上报时间',
            value: 'tReport',
            // width: 270,
            filter: 'dataFormatFilter',
            className: '',
          },
          {
            label: '等级',
            value: 'level',
            // width: 270,
            filter: 'nullFilter',
            className: '',
          },
          // {
          //   label: '描述',
          //   value: 'desc',
          //   // width: 270,
          //   filter: 'nullFilter',
          //   className: '',
          // },
        ],
        modifyCont: {
          username: '',
          password: '',
          group_name: '',
          comment: '',
        },
        pagination: {
          total: 0,
          currentPage: 1,
          pageSize: 15,
          onChange: this.onPaginationChanged
        },
        loading: false,
      };
    },
    computed: {
      optionModify() {
        return {
          name: '门限设置',
          form: {
            '门限值': [
              {
                type: 'input',
                name: 'threshold',
                defaultValue: this.modifyCont.threshold,
                rule: {
                  required: true,
                  requiredError: '输入内容不可为空。'
                }
              }
            ],
          },
          clearText: this.$t('OPERATION.CLEAR'),
          clear: this.clear,
          executeText: this.$t('OPERATION.CONFIRM'),
          execute: this.executeModify,
          style: {
          //  width: '50%',
          }
        }
      },
    },
    methods: {
      addMockData() {
        for (let i = 0; i < 10; i++) {
          this.alarmList.push({
            alarmId: "1001",
            beginDelayFT: 0,
            delay: 0,
            devName: "开关电源",
            dev_colId: "1-1_1001",
            h: 0,
            link: 19,
            name: "电池01熔丝故障告警",
            num: 17,
            recoverDelay: 0,
            recoverDelayFT: 0,
            signalName: " 电池01熔丝故障告警",
            tReport: 1556094413224,
            threshold: 1,
            value: 1,
          });
        }
        this.pagination.total = this.alarmList.length;
      },

      handleSelectionChange() {
        
      },

      intoNextPage() {

      },

      // 获取实时告警列表
      getAlarmList() {
        let param = {
          // sn: this.$route.query.sN
        }
        this.$api.getAlarmList(param, this.$route.query.sN).then((res)=> {
          this.alarmList = res.data;
        })
      }

    },
    mounted() {
      // this.addMockData();
      this.getAlarmList();
    }
  }
</script>

<style lang="stylus" rel="stylesheet/stylus">
  .alarmList
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


