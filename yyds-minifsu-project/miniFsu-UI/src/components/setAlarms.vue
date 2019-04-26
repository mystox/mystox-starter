<template>
  <div class="alarmList flex-column">
    <sc-breadcrumb
      :urls="[
        {name: 'SN列表'},
        {name: '设备列表', path: `/devices`, query: {sN: $route.query.sN}},
        {name: '告警门限设置'},
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
          <el-form-item label="告警描述" prop="alarmDesc" label-width="70px">
            <el-input v-model.trim="searcher.alarmDesc" placeholder="请输入告警名称">
            </el-input>
          </el-form-item>
        </el-form>
      </div>
      <div slot="operate">
        <el-button type="primary" @click="getThreshold">查询</el-button>
      </div>
    </operation-bar-layout>
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
      <!-- <el-table-column :label="'SN'" fixed="left">
        <template slot-scope="scope">
          <div style="font-size: 14px; color: #20A0FF; cursor: pointer;">
            <router-link :to="{
                  path: '/points',
                  query: {
                      deviceId: scope.row.deviceId
                  }
                }" tag="span">
              {{scope.row.username}}
            </router-link>
          </div>
        </template>
      </el-table-column> -->
      <el-table-column
        :label="$t('SECURITY.USER.OPERATION')" fixed="right">
        <template slot-scope="scope">
          <i style="color: #20A0FF; cursor: pointer;" @click="showDialog('threshold', scope.row)">门限设置</i>
        </template>
      </el-table-column>
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
          alarmDesc: '',
        },
        multipleSelection: [],
        alarmList: [],
        userGroups: [],
        columns: [
          {
            label: '告警点 ID',
            value: 'alarmId',
            // width: 270,
            filter: 'nullFilter',
            className: '',
          },
          {
            label: '告警描述',
            value: 'alarmDesc',
            // width: 270,
            filter: 'nullFilter',
            className: '',
          },
          {
            label: '门限值',
            value: 'threshold',
            // width: 270,
            filter: 'nullFilter',
            className: '',
          },
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
      }
    },
    computed: {
      optionModify() {
        return {
          name: '门限设置',
          form: {
            // '告警点 ID': [
            //   {
            //     type: 'alarmId',
            //     text: this.modifyCont.alarmId,
            //     rule: {
            //     }
            //   }
            // ],
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
            username: "pointName",
            password: '123456',
            group_name: '城管大队三组',
            comment: "**********"
          });
        }
        this.pagination.total = this.alarmList.length;
      },

      /**
       * 获取告警列表
       */
      getThreshold() {
        let param = {
          alarmDesc: this.searcher.alarmDesc,
          port: this.$route.query.port,
          resNo: this.$route.query.resNo - 0,
          type: this.$route.query.type - 0,
        }
        this.$api.getThreshold(param, this.$route.query.sN).then((data) => {
          this.pagination.total = data.data.length;
          this.alarmList = data.data;
          this.loading = false
        }, (err)=> {
          if (err) this.loading = false;
        })
      },

      onPaginationChanged (pagination) {
        this.pagination.pageSize = pagination.pageSize;
        this.pagination.currentPage = pagination.currentPage;
        this.getThreshold()
      },

      handleSelectionChange(val) {
        this.multipleSelection = this.comFunc.map(val, "username");
      },

      executeModify(res) {
        this.setThreshold(res)
      },
      clear(res) {
//        console.log(res)
      },

      showDialog(dialogName, item) {
        item && (this.modifyCont = item);
        this.$refs[dialogName].toModal();
      },

      intoNextPage() {

      },

      // 设置门限值。
      setThreshold(res) {
        let params = {
          "deviceId": this.modifyCont.deviceId,
          "configId": this.modifyCont.id,
          "coId":  this.modifyCont.coId,
          "threshold": res.threshold - 0
        };
        this.$api.setThreshold(params, this.$route.query.sN).then(() => {
          this.getThreshold();
        })
      },

    },
    mounted() {
      // this.addMockData();
      this.getThreshold();
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


