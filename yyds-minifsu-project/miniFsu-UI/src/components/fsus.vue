<template>
  <div class="fsuList flex-column">
    <operation-bar-layout style="{}">
      <div slot="query">
        <el-form class=""
                 :model="searcher"
                 ref="searcher"
                 label-position="right"
                 :inline="true">
          <!--使用重置功能需要给item元素传入prop属性-->
          <el-form-item label="SN" prop="sn" label-width="50px">
            <el-input v-model="searcher.sn" placeholder="请输入SN">
            </el-input>
          </el-form-item>
        </el-form>
      </div>
      <div slot="operate">
        <!-- <el-button type="primary" @click="showDialog('addUserInfoDialog')">新增</el-button> -->
        <!--<el-button type="primary">应用</el-button>-->
        <el-button type="primary" @click="getFsuList">查询</el-button>
        <!-- <el-button type="primary" @click="deleteContentsNeedValidated(false)">删除</el-button> -->
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
      <el-table-column
        :label="$t('SECURITY.USER.OPERATION')" width="300">
        <template slot-scope="scope">
          <div>
            <!-- <i title='修改'
                 style="color: #20A0FF; cursor: pointer;"
                  @click="showDialog('modifyUserInfoDialog', scope.row)"
                 class="icon iconfont icon-edit icon-lg">
              </i> -->
            <i style="color: #20A0FF; cursor: pointer;" @click="showDialog('bindDialog', scope.row)">绑定</i>
            <router-link :to="{
                  path: '/devices',
                  query: {
                      sN: scope.row.sN
                  }
                }" tag="span" style="color: #20A0FF; cursor: pointer;">
              / 设备
            </router-link>
            <router-link :to="{
                  path: '/fsuInfoList',
                  query: {
                      sN: scope.row.sN
                  }
                }" tag="span" style="color: #20A0FF; cursor: pointer;">
              / SN 运行状态
            </router-link>
            <router-link :to="{
                  path: '/fsuPKT',
                  query: {
                      sN: scope.row.sN
                  }
                }" tag="span" style="color: #20A0FF; cursor: pointer;">
              / SN 终端日志
            </router-link>
          </div>
        </template>
      </el-table-column>
    </table-box>
    <!-- <modal :option="optionAdd" ref="addUserInfoDialog"></modal> -->
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
          sn: '',
        },
        multipleSelection: [],
        fsuList: [],
        userGroups: [],
        columns: [
          {
            label: 'SN 名称',
            value: 'name',
            // width: 270,
            filter: 'nullFilter',
            className: '',
          },
           {
            label: 'SN 编码',
            value: 'sN',
            // width: 270,
            filter: 'nullFilter',
            className: '',
          },
          {
            label: '绑定状态',
            value: 'bindStatus',
            // width: 270,
            filter: 'nullFilter',
            className: '',
          },
          {
            label: '在线状态',
            value: 'status',
            // width: 270,
            filter: 'statusFilter',
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
            // 'SN 设备列表': [
            //   {
            //     type: 'textarea',
            //     name: 'snDeviceList',
            //     defaultValue: '123123123\n123123123\n123123123\n123123123\n123123123\n',
            //     disabled: true,
            //     rows: 8,
            //     rule: {}
            //   }
            // ],
            'FSU ID': [
              {
                type: 'input',
                name: 'fsuId',
                defaultValue: "43048243800189",
                disabled: true,
                rule: {}
              }
            ],
            'FSU 设备列表': [
              {
                type: 'textarea',
                name: 'devIds',
                defaultValue: "43048243800189\n43048240700215\n43048240600130\n43048241820217\n43048243700210\n43048241800169\n43048241830216\n43048241840216\n43048241810169\n43048241500109\n43048241900261\n43048249900010\n43048244500016\n43048241860244",
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
      // 模拟数据
      addMockData() {
        for (let i = 0; i < 10; i++) {
          this.fsuList.push({
            username: "某某某",
            password: '123456',
            group_name: '城管大队一组',
            comment: "**********",
            sN: '123123123123123123'
          });
        }
        this.pagination.total = this.fsuList.length;
      },

      // 获取FSU（sn）列表
      getFsuList() {
        let param = {
          // sn: this.searcher.sn,
          page: this.pagination.currentPage ,
          count: this.pagination.pageSize ,
        };
        this.$api.getFsuList(param).then((res) => {
          if (!res.data) return;
          this.pagination.total = res.data.totalSize;
          this.fsuList = res.data.list;
          this.loading = false;
        }, (err)=> {
          if (err) this.loading = false;
        })
      },

      // 分页变化回调
      onPaginationChanged (pagination) {
        this.pagination.pageSize = pagination.pageSize;
        this.pagination.currentPage = pagination.currentPage;
        this.getFsuList()
      },

      // 列表复选框回调
      handleSelectionChange(val) {
        this.multipleSelection = this.comFunc.map(val, "username");
      },

      // 进入下一页
      intoNextPage() {
      },

      executeAdd(res) {
        // this.addContent(res)
      },

      // 点击模态框确定按钮。
      executeModify(res) {
        this.modifyCont = Object.assign({}, this.modifyCont, res);
        this.modifyCont.devIds = res.devIds.split('\n');
        this.setFsu(this.modifyCont);
      },

      // 关闭模态框
      clear(res) {
      //  console.log(res)
      },

      showDialog(dialogName, item) {
        item && (this.modifyCont = item);
        this.$refs[dialogName].toModal();
      },

      setFsu(res) {
        let params = {
          "fsuId" : res.fsuId,
          "devCodeList" : res.devIds,
          "sn": res.sN,
          "name" : "test_fsu",
          "address" : "address",
          "setUpTime" : new Date().getTime(), 
          "vpnName": "全国3",

          "fsuClass":"",
          "imsi" : "imsi",
          "operators" : "yytd",
          "heartCycle" : 10,
          "businessRhythm" : 100,
          "runStatusRhythm" : 50,
          "alarmRhythm" : 1000,
          "coordinate" : "120.261175,30.317344"
        };
        this.$api.setFsu(params).then(() => {
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
      this.getFsuList(true);
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


