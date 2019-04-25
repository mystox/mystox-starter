<template>
  <div class="deviceList flex-column">
    <sc-breadcrumb
      :urls="[
        {name: 'SN列表', path: '/fsus'},
        {name: '设备列表'},
      ]"
    ></sc-breadcrumb>
    <!-- <operation-bar-layout style="{}">
     <div slot="query">
        <el-form class=""
                 :model="searcher"
                 ref="searchForm"
                 label-position="right"
                 :inline="true">
          <el-form-item label="设备名称" prop="sn" label-width="70px">
            <el-input v-model="searcher.sn" placeholder="请输入设备名称">
            </el-input>
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
      :data="deviceList"
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
        :label="$t('SECURITY.USER.OPERATION')" fixed="right" width="250">
        <template slot-scope="scope">
          <div style="font-size: 14px; color: #20A0FF; cursor: pointer;">
            <router-link :to="{
                  path: '/points',
                  query: {
                      deviceId: scope.row.id,
                      resNo: scope.row.resNo,
                      type: scope.row.type,
                      sn: scope.row.sN,
                      dev: scope.row.type + '-' + scope.row.resNo
                  }
                }" tag="span">
              实时数据
            </router-link>
            <router-link :to="{
                  path: '/alarms',
                  query: {
                      deviceId: scope.row.id,
                      resNo: scope.row.resNo,
                      type: scope.row.type,
                      sn: scope.row.sN,
                      port: scope.row.port,
                      dev: scope.row.type + '-' + scope.row.resNo
                  }
                }" tag="span">
             / 实时告警
            </router-link>
            <router-link :to="{
                  path: '/setAlarms',
                  query: {
                      deviceId: scope.row.id,
                      resNo: scope.row.resNo,
                      type: scope.row.type,
                      sn: scope.row.sN,
                      port: scope.row.port,
                  }
                }" tag="span">
             / 告警门限设置
            </router-link>
          </div>
        </template>
      </el-table-column>
    </table-box>
    <modal :option="optionAdd" ref="addUserInfoDialog"></modal>
    <modal :option="optionModify" ref="modifyUserInfoDialog"></modal>
  </div>
</template>

<script>
  export default {
    name: 's-devices',
    props: {},
    data () {
      return {
        searcher: {
          name: '',
        },
        multipleSelection: [],
        deviceList: [],
        userGroups: [],
        columns: [
          {
            label: '设备名称',
            value: 'name',
            // width: 270,
            filter: 'nullFilter',
            className: '',
          },
          {
            label: '设备 ID',
            value: 'id',
            // width: 270,
            filter: 'nullFilter',
            className: '',
          },
          {
            label: '所属SN',
            value: 'sN',
            // width: 270,
            filter: 'nullFilter',
            className: '',
          }
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
      optionAdd() {
        return {
          name: 'SECURITY.USER.DIALOG_NEW.TITLE',
          form: {
            'SECURITY.USER.USER_NAME': [
              {
                type: 'input',
                name: 'username',
                defaultValue: '',
                rule: {
                  required: true,
                  requiredError: '输入内容不可为空',
                }
              }
            ],
            'SECURITY.USER.USER_PASSWORD': [
              {
                type: 'input',
                name: 'password',
                defaultValue: '',
                rule: {
                  required: true,
                  requiredError: '输入内容不可为空。'
                }
              }
            ],
            'SECURITY.USER.GROUP_NAME': [
              {
                type: 'select',
                name: 'group_name',
                options: this.userGroups.map((v)=> {
                  return {
                    value: v.group_name,
                    label: v.group_name
                  }
                })
              }
            ],
            'SECURITY.USER.COMMENT': [
              {
                type: 'input',
                name: 'comment',
                defaultValue: '',
                rule: {
                }
              }
            ],
          },
          clearText: this.$t('OPERATION.CLEAR'),
          clear: this.clear,
          executeText: this.$t('OPERATION.CONFIRM'),
          execute: this.executeAdd,
          style: {
//            width: '50%',
          }
        }
      },
      optionModify() {
        return {
          name: 'SECURITY.USER.DIALOG_MODIFY.TITLE',
          form: {
            'SECURITY.USER.USER_NAME': [
              {
                type: 'span',
                text: this.modifyCont.username,
                rule: {
                }
              }
            ],
            'SECURITY.USER.USER_PASSWORD': [
              {
                type: 'input',
                name: 'password',
                defaultValue: this.modifyCont.password,
                rule: {
                  required: true,
                  requiredError: '输入内容不可为空。'
                }
              }
            ],
            'SECURITY.USER.GROUP_NAME': [
              {
                type: 'select',
                name: 'group_name',
                defaultValue: this.modifyCont.group_name,
                options: this.userGroups.map((v)=> {
                  return {
                    value: v.group_name,
                    label: v.group_name
                  }
                })
              }
            ],
            'SECURITY.USER.COMMENT': [
              {
                type: 'input',
                name: 'comment',
                defaultValue: this.modifyCont.comment,
                rule: {}
              }
            ],
          },
          clearText: this.$t('OPERATION.CLEAR'),
          clear: this.clear,
          executeText: this.$t('OPERATION.CONFIRM'),
          execute: this.executeModify,
          style: {
//            width: '50%',
          }
        }
      },
    },
    methods: {
      addMockData() {
        for (let i = 0; i < 10; i++) {
          this.deviceList.push({
            username: "deviceName",
            password: '123456',
            group_name: '城管大队二组',
            comment: "**********"
          });
        }
        this.pagination.total = this.deviceList.length;

      },

      getDeviceList() {
        console.log(this.$router);
        let param = {
          sn: this.$route.query.sN,
          // cur_page: this.pagination.currentPage ,
          // page_size: this.pagination.pageSize ,
        };
        this.$api.getDeviceList(param, param.sn).then((res) => {
          this.pagination.total = res.data.length;
          this.deviceList = res.data;
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

      intoNextPage() {
      },

      executeAdd(res) {
        this.addContent(res)
      },
      executeModify(res) {
        this.updateContent(res)
      },
      clear(res) {
//        console.log(res)
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

      showDialog(dialogName, item) {
        item && (this.modifyCont = item);
        this.$refs[dialogName].toModal();
      },

      updateContent(res) {
        let params = {
          username: res.username,
          password: res.password,
          group_name: res.password,
          comment: res.comment
        };
        this.appHttp.postUpdateUser(params).then(() => {
          this.getUsers();
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
      this.addMockData();
      this.getDeviceList();
    }
  }
</script>

<style lang="stylus" rel="stylesheet/stylus">
  .deviceList
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


