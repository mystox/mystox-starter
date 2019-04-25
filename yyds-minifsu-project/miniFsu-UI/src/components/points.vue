<template>
  <div class="pointList flex-column">
    <sc-breadcrumb
      :urls="[
        {name: 'SN列表'},
        {name: '设备列表', path: '/devices', query: {sN: $route.query.sn}},
        {name: '实时数据'},
      ]"
    ></sc-breadcrumb>
    <!-- <operation-bar-layout style="{}">
      <div slot="query">
        <el-form class=""
                 :model="searcher"
                 ref="searchForm"
                 label-position="right"
                 :inline="true">
          <el-form-item label="信号名称" prop="name" label-width="70px">
            <el-input v-model="searcher.name" placeholder="请输入信号点名称">
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
      :pagination = "pagination"
      :data="pointList"
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
          <i style="color: #20A0FF; cursor: pointer;" @click="showDialog('setSignalDialog', scope.row)"
          v-if="scope.row.dataType === 'AO' || scope.row.dataType === 'DO'">
            设置信号点值
            </i>          
        </template>
      </el-table-column>
    </table-box>
    <modal :option="optionModify" ref="setSignalDialog"></modal>
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
        pointList: [],
        userGroups: [],
        columns: [
          {
            label: '信号点名称',
            value: 'name',
            // width: 270,
            filter: 'nullFilter',
            className: '',
          },
          {
            label: '信号点 ID',
            value: 'coId',
            // width: 270,
            filter: 'nullFilter',
            className: '',
          },
          {
            label: '信号点类型',
            value: 'dataType',
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
        options: [
          {label: '关', value: 0 },
          {label: '开', value: 1 }
        ],
      }
    },
    computed: {
      optionModify() {
        let cell;
        if (this.modifyCont.dataType === 'DO') {
          cell =  [
              {
                type: 'select',
                name: 'value',
                options: [
                  {label: '关', value: 0},
                  {label: '开', value: 1 }
                ],
                defaultValue: this.modifyCont.value,
                rule: {
                  required: true,
                  requiredError: '输入内容不可为空。'
                }
              }
            ]
        } else if (this.modifyCont.dataType === 'AO') {
          cell = [
              {
                type: 'input',
                name: 'value',
                defaultValue: this.modifyCont.value,
                rule: {
                  required: true,
                  requiredError: '输入内容不可为空。'
                }
              }
            ]
        }

        return {
          name: '设置信号点值',
          form: {
            '信号点 ID': [
              {
                type: 'span',
                text: this.modifyCont.coId,
                rule: {
                }
              }
            ],
            '信号点值': cell
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
          this.pointList.push({
            username: "pointName",
            password: '123456',
            group_name: '城管大队三组',
            comment: "**********"
          });
        }
        this.pagination.total = this.pointList.length;
      },

    
      getSignalList() {
        let params = {
          // cur_page: this.pagination.currentPage ,
          // page_size: this.pagination.pageSize ,
        };
        this.$api.getSignalList({
          deviceId: this.$route.query.deviceId,
          resNo: this.$route.query.resNo - 0,
          type: this.$route.query.type - 0,
        }, this.$route.query.sn).then((data) => {
          this.pagination.total = data.data.info.length;
          this.pointList = data.data.info;
          this.dev = data.data.dev;
          this.loading = false;
        }, (err)=> {
          if (err) this.loading = false;
        })
      },

      onPaginationChanged (pagination) {
        this.pagination.pageSize = pagination.pageSize;
        this.pagination.currentPage = pagination.currentPage;
        this.getUsers()
      },

      handleSelectionChange(val) {
        this.multipleSelection = this.comFunc.map(val, "username");
      },

      executeModify(res) {
        this.updateContent(res)
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

      addContent(res) {
        let params = {
          username: res.username,
          password: res.password,
          group_name: res.group_name,
          comment: res.comment
        };
        this.appHttp.postAddUser(params).then(() => {
          this.getUsers();
        })
      },

      // 设置信号点值
      updateContent(res) {
        let params = {
          // dev: this.dev,
          // stePoint: this.modifyCont.coId,
          // steData: res.value,
          "dev":"3-1",
          "stePoint":201001,
          "steData":1
        };
        this.$api.setSignal(params, this.$route.query.sn).then(() => {
          this.getSignalList();
        })
      },

      deleteContents(callback) {
        let params = {
          users: this.multipleSelection
        };
        this.appHttp.postDeleteUsers(params).then(() => {
          callback && callback.call(this);
        })
      },

      deleteContentsNeedValidated() {
        if (this.multipleSelection.length === 0) {
          this.$message({message: "请选择删除项", type: 'warning', showClose: true})
        } else {
          this.$confirm('此操作将永久删除所选项, 是否继续?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            this.deleteContents(function() {
              this.getUsers();
            })
          }).catch(() => {
            this.$message({
              type: 'info',
              message: '已取消删除',
              showClose: true
            });
          });
        }
      },

    },
    mounted() {
      // this.addMockData();
      this.getSignalList();
    }
  }
</script>

<style lang="stylus" rel="stylesheet/stylus">
  .pointList
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


