<template>
  <div class="fsuList flex-column">
    <operation-bar-layout style="{}">
      <div slot="query">
        <el-form class=""
                 :model="searcher"
                 ref="searcher"
                 label-position="right"
                 :inline="true">
          <el-form-item label="SN" prop="sn" label-width="35px">
            <el-input v-model.trim="searcher.sn" placeholder="请输入SN">
            </el-input>
          </el-form-item>
        </el-form>
      </div>
      <div slot="operate">
        <el-button type="primary" @click="getFsuList">查询</el-button>
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
        :label="$t('SECURITY.USER.OPERATION')" width="550">
        <template slot-scope="scope">
          <div>
            <i v-if="!scope.row.bindMark" style="color: #20A0FF; cursor: pointer;" @click="showDialog('bindDialog', scope.row)">/ 绑定</i>
            <i v-if="scope.row.bindMark" style="color: #20A0FF; cursor: pointer;" @click="unbind(scope.row)">/ 解绑</i>
            <i v-if="scope.row.status === 2" style="color: #20A0FF; cursor: pointer;" @click="showUpgradeDialog(scope.row)">/ 升级</i>
            <!-- <i style="color: #20A0FF; cursor: pointer;" @click="showUpgradeDialog(scope.row)">/ 升级</i> -->
            <router-link :to="{
                  path: '/devices',
                  query: {
                      sN: scope.row.sN
                  }
                }" tag="span" style="color: #20A0FF; cursor: pointer;">
              / 查看设备
            </router-link>
            <router-link v-if="scope.row.status === 2"
            :to="{
                  path: '/alarmsForSN',
                  query: {
                      sN: scope.row.sN,
                  }
                }" tag="span" style="color: #20A0FF; cursor: pointer;">
              / 实时告警
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

    <el-dialog :title="'升级'" :visible.sync="upgradeDialogVisible" :close-on-click-modal="false" @close="handleCancel">
      <el-form :model="upgradeParam" label-width="125px" ref="upgradeDialog">
        <el-form-item label="SN ID">
          <span>{{modifyCont.sN}}</span>
        </el-form-item>
        <el-form-item label="选择升级类型" prop="type" v-show="typeList.length != 0">
          <el-select v-model="upgradeParam.type" auto-complete="off" style="width:200px;" @change="handleChange">
            <el-option v-for="(item, index) in typeList" :label="item.label" :value="item.value" :key="index">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="选择升级文件" prop="documentObj" v-show="typeList.length != 0">
          <el-select v-model="upgradeParam.documentObj" auto-complete="off" style="width:200px;" :disabled="!upgradeParam.type">
            <el-option v-for="(item, index) in documentList" :label="item.label" :value="item" :key="index">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="后端编译" v-show="upgradeParam.type && (upgradeParam.documentObj && upgradeParam.documentObj.name) && typeList.length != 0">
          <el-button @click="compiler" type="primary"> 编译 </el-button>
        </el-form-item>
        <el-form-item label="引擎编译结果" v-if="compilerInfo.engine">
          <span>{{compilerInfo.engine}}</span>
        </el-form-item>
        <el-form-item label="适配层编译结果" v-if="compilerInfo.program">
          <span style>{{compilerInfo.program}}</span>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click.native="handleCancel">取消</el-button>
        <el-button type="primary" @click.native="upgrade" :disabled="!(compilerInfo.engine || compilerInfo.program)">确定</el-button>
      </div>
    </el-dialog>

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
        columns: [
          {
            label: 'SN 编码',
            value: 'sN',
            // width: 270,
            filter: 'nullFilter',
            className: '',
          },
          {
            label: '绑定状态',
            value: 'bindMark',
            // width: 270,
            filter: 'bindMarkFilter',
            className: '',
          },
          {
            label: '在线状态',
            value: 'status',
            // width: 270,
            filter: 'statusFilter',
            className: '',
          },
          {
            label: '已绑定FSU ID',
            value: 'fsuId',
            // width: 270,
            filter: 'nullFilter',
            className: '',
          },
          {
            label: '适配层版本号',
            value: 'adapterVer',
            // width: 270,
            filter: 'nullFilter',
            className: '',
          },
          {
            label: '引擎版本号',
            value: 'engineVer',
            // width: 270,
            filter: 'nullFilter',
            className: '',
          },
        ],
        modifyCont: {
        },
        typeList: [
          {label: '引擎', value: 1},
          {label: '适配层', value: 2}
        ],
        pagination: {
          total: 0,
          currentPage: 1,
          pageSize: 15,
          onChange: this.onPaginationChanged
        },
        upgradeParam: {
          type: null,
          documentObj: '',
        },
        loading: false,
        allDocument: [],
        documentList: [],
        disabled: true,
        upgradeDialogVisible: false,
        compilerInfo: {
          engine: '',
          program: '',
        },
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
            'FSU ID': [
              {
                type: 'input',
                name: 'fsuId',
                // defaultValue: "43048243800189",
                disabled: true,
                rule: {}
              }
            ],
            'FSU 设备列表': [
              {
                type: 'textarea',
                name: 'devIds',
                // defaultValue: "43048243800189\n43048240700215\n43048240600130\n43048241820217\n43048243700210\n43048241800169\n43048241830216\n43048241840216\n43048241810169\n43048241500109\n43048241900261\n43048249900010\n43048244500016\n43048241860244",
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
          sn: this.searcher.sn,
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

      // 获取升级文件信息列表
      getDocumentList() {
        this.$api.getDocumentList().then((res=> {
          // 获取文件目录
          this.allDocument = res.data;
        }).bind(this))
      },

      // 处理选择升级类型
      handleChange(val) {
        this.upgradeParam.documentObj = {};
        this.documentList = this.allDocument.filter(v=> {
          return v.name === (val === 1 ? 'engine' : 'program');
        })[0].list;

        if (this.documentList.length === 0) return;
        
        // 改成formCell 需要的数据结构
        this.documentList.forEach(v=> {
          this.$set(v, 'label', v.name);
          this.$set(v, 'value', v);
        })
      },

      // 点击编译
      compiler() {
        let name = this.upgradeParam.documentObj.name;
        let url = this.upgradeParam.documentObj.url;
        let host = 'http://172.16.6.39:8081/'
        let param = {
          "url": host + url,
          "name": name,
          "type": this.upgradeParam.type,
          "md5": 'sdfwefwefwef',
        }
        this.$api.compiler(param, this.modifyCont.sN).then((res=> {
          if (res.data.result === 1) {
            if (this.upgradeParam.type === 1) {
              this.compilerInfo['engine'] = `编译成功,文件名为${res.data.fileName},文件大小为${res.data.totalLen}`;
              this.delOption(1);
              this.paramForUpgradeEngine = {
                "fileName": res.data.fileName,
                "totalLen": res.data.totalLen,
                "type": this.upgradeParam.type,
                "md5": "12342jlagjkljl24gajklgj"
              }
            } else if (this.upgradeParam.type === 2) {
              this.compilerInfo['program'] = `编译成功,文件名为${res.data.fileName},文件大小为${res.data.totalLen}`;
              this.delOption(2);
              this.paramForUpgradeProgram = {
                "fileName": res.data.fileName,
                "totalLen": res.data.totalLen,
                "type": this.upgradeParam.type,
                "md5": "12342jlagjkljl24gajklgj"
              }
            }
          } 
          this.$refs['upgradeDialog'].resetFields();
        }).bind(this))
      },
      
      // 删除已经选择过的option
      delOption(type) {
        // this.documentList = this.documentList.filter(v=> {
        //   return v.name != fileName;
        // })
        this.typeList = this.typeList.filter(v=> {
          return v.value != type;
        })
      },


      // 点击升级
      upgrade() {
        if (this.compilerInfo.engine) {
          this.$api.upgrade(this.paramForUpgradeEngine, this.modifyCont.sN).then(res=> {
            // console.log(res);
          });
        } 
        this.$nextTick(()=> {
          if (this.compilerInfo.program) {
            this.$api.upgrade(this.paramForUpgradeProgram, this.modifyCont.sN).then(res=> {
              // console.log(res);
            });
          }
        })

      },

      // 点击取消
      handleCancel() {
        this.upgradeDialogVisible = false;
        this.$refs['upgradeDialog'].resetFields();
        this.compilerInfo = {
          engine: '',
          program: '',
        };
        this.typeList = [
          {label: '引擎', value: 1},
          {label: '适配层', value: 2}
        ];
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
        if (res.devIds) {
          this.modifyCont.devIds = res.devIds.split('\n');
        }
        this.setFsu(this.modifyCont);
      },

      // 升级
      executeUpgrade(res) {
        this.upgradeParam = Object.assign({}, res);
        this.upgrade()
      },

      // 关闭模态框
      clear(res) {
      //  console.log(res)
      },

      showDialog(dialogName, item) {
        item && (this.modifyCont = item);
        this.$refs[dialogName] &&this.$refs[dialogName].toModal();
      },
      
      showUpgradeDialog(item) {
        item && (this.modifyCont = item);
        this.upgradeDialogVisible = true;
      },

      // 绑定FSU
      setFsu(res) {
        let params = {
          "fsuId" : res.fsuId,
          "devCodeList" : res.devIds,
          "sn": res.sN,
          "name" : res.name,
          "address" : res.address,
          "setUpTime" : new Date().getTime(), 
          "vpnName": '全国7',

          "fsuClass": res.fsuClass,
          "imsi" : res.imsi,
          "operators" : res.operaters,
          "heartCycle" : res.heartCycle,
          "businessRhythm" :res.businessRhythm,
          "runStatusRhythm" : res.runStatusRhythm,
          "alarmRhythm" : res.alarmRhythm,
          "coordinate" : res.coordinate
        };
        this.$api.setFsu(params).then((res) => {
          this.getFsuList();
        })
      },

      // 解绑FSU
      unbind(item) {
        let param = {
          fsuId: item.fsuId
        }
        this.$api.unbind(param, item.sN).then((res)=> {
          this.getFsuList();
        })
      },
    },
    mounted() {
      // this.addMockData();
      this.getFsuList(true);
      this.getDocumentList();
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


