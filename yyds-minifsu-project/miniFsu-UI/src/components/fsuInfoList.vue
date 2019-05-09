<template>
  <div class="fsuList flex-column">
    <sc-breadcrumb
      :urls="[
        {name: 'SN列表', path: '/fsus'},
        {name: 'SN信息记录'},
      ]"
    ></sc-breadcrumb>
    <operation-bar-layout style="{}">
      <div slot="query">
        <el-form class=""
                 :model="searcher"
                 ref="searchForm"
                 label-position="right"
                 :inline="true">
          <!--使用重置功能需要给item元素传入prop属性-->
          <el-form-item label="时间" prop="name" label-width="45px">
            <el-date-picker
              v-model="searcher.timeRange"
              type="datetimerange"
              :picker-options="pickerOptions"
              placeholder="选择时间范围"
              :clearable="false"
              align="right">
            </el-date-picker>
          </el-form-item>
        </el-form>
      </div>
      <div slot="operate">
        <el-button type="primary" @click="goSearch">查询</el-button>
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
            new Date() - 7 * 24 * 60 * 60 * 1000, 
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
            label: 'CPU使用(%)',
            value: 'cpuUse',
            width: 270,
            filter: 'nullFilter',
            className: '',
          },
          {
            label: '内存使用(%)',
            value: 'memUse',
            width: 270,
            filter: 'nullFilter',
            className: '',
          },
          {
            label: '时间',
            value: 'createTime',
            width: 270,
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
            "csq": 22,
            "memUse": "41.5%",
            "createTime": 1555996089036,
            "sysTime": 15234875,
            "id": "5cbe9db9ca68837ea4d30dc7",
            "sn": "MINI210121000001",
            "cpuUse": "37.2%"
          });
        }
        this.pagination.total = this.fsuList.length;
      },

      /**
      点击搜索之后，需要初始化分页功能。
       */
      goSearch() {
        this.pagination.currentPage = 1;
        this.pagination.pageSize = 15;
        this.getRunState();
      },

      getRunState() {
        let param = {
          sn: this.$route.query.sN,
          startTime: new Date(this.searcher.timeRange[0]).getTime(),
          endTime: new Date(this.searcher.timeRange[1]).getTime(),
          page: this.pagination.currentPage ,
          count: this.pagination.pageSize ,
        };
        this.$api.getRunState(param, this.$route.query.sN).then((res) => {
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
        this.getRunState()
      },

      handleSelectionChange(val) {
        this.multipleSelection = this.comFunc.map(val, "username");
      },

      intoNextPage() {
      },

      executeAdd(res) {
      },
      executeModify(res) {
        this.modifyCont = Object.assign({}, this.modifyCont, res);
        this.modifyCont.devIds = res.devIds.split('\n');
      },
      clear(res) {
      }

    },
    mounted() {
      // this.addMockData();
      this.getRunState(true);
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


