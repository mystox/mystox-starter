<template>
  <div class="binf-sn flex-column">
     <!-- <el-form ref="form" :model="form" label-width="80px">
      <el-form-item label="SN">
        <el-input v-model="form.sn"></el-input>
      </el-form-item>
      <el-form-item label="fsu">
        <el-input v-model="form.fsu"></el-input>
      </el-form-item>
     <el-form-item label="FSU">
        <el-select v-model="form.region" placeholder="请选择活动区域">
          <el-option label="区域一" value="shanghai"></el-option>
          <el-option label="区域二" value="beijing"></el-option>
        </el-select
      </el-form-item>
      <el-form-item label="活动时间">
        <el-col :span="11">
          <el-date-picker type="date" placeholder="选择日期" v-model="form.date1" style="width: 100%;"></el-date-picker>
        </el-col>
        <el-col class="line" :span="2">-</el-col>
        <el-col :span="11">
          <el-time-picker placeholder="选择时间" v-model="form.date2" style="width: 100%;"></el-time-picker>
        </el-col>
      </el-form-item>
      <el-form-item label="即时配送">
        <el-switch v-model="form.delivery"></el-switch>
      </el-form-item>
      <el-form-item label="活动性质">
        <el-checkbox-group v-model="form.type">
          <el-checkbox label="美食/餐厅线上活动" name="type"></el-checkbox>
          <el-checkbox label="地推活动" name="type"></el-checkbox>
          <el-checkbox label="线下主题活动" name="type"></el-checkbox>
          <el-checkbox label="单纯品牌曝光" name="type"></el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item label="特殊资源">
        <el-radio-group v-model="form.resource">
          <el-radio label="线上品牌商赞助"></el-radio>
          <el-radio label="线下场地免费"></el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="设备ID">
        <el-input type="textarea" v-model="form.desc" :rows="5"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">绑定</el-button>
        <el-button>取消</el-button>
      </el-form-item>
    </el-form> -->
    <!-- <div style="background: #cccccc; width: 100%; height: 2px;"></div> -->
    <h3>告警点导入</h3>
    <el-form style="margin: 40px 0 0 40px; width: 400px">
      <el-upload
        drag
        :action='alarmModelUrl'
        :before-upload="beforeUpload"
        :on-success="handleOnSuccess"
        :on-change="onChange"
        :on-error="handleOnError"
        multiple>
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">只能上传excel文件</div>
      </el-upload>
    </el-form>
    <!-- <div style="background: #cccccc; width: 100%; height: 2px;"></div> -->
    <h3 style="margin-top: 50px">信号点导入</h3>
    <el-form style="margin: 40px 0 0 40px; width: 400px">
      <el-upload
        drag
        :action='signalModelUrl'
        :before-upload="beforeUpload"
        :on-success="handleOnSuccess"
        :on-change="onChange"
        :on-error="handleOnError"
        multiple>
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">只能上传excel文件</div>
      </el-upload>
    </el-form>
  </div>
</template>

<script>
  
  export default {
    name: 's-bind-sn',
    props: {},
    data () {
      let base = process.env.API_HOST;
      return {
        form: {
          name: '',
          region: '',
          date1: '',
          date2: '',
          delivery: false,
          type: [],
          resource: '',
          desc: ''
        },
        signalModelUrl: (this._ctx ? this.$store.state.menu.currentMark : base) + `fsu/signalModel/import`,
        alarmModelUrl: (this._ctx ? this.$store.state.menu.currentMark : base) + `fsu/alarmModel/import`
      }
    },
    computed: {
    },
    methods: {
      beforeUpload(val) {
        console.log(val);
        debugger;
        return ['.xlsx', '.xls'].some(v=> {
          return v.indexOf(val.name);
        })
      },

      handleOnSuccess(val) {
        this.$message({
          message: '操作成功',
          type: 'success',
          showClose: true,
        })
      },

      handleOnError(val) {
        this.$message({
          message: '操作失败',
          type: 'error',
          showClose: true,
        })
      },

      onChange(val) {
        console.log(val);
      }
    },
    mounted() {
    }
  }
</script>

<style lang="stylus" rel="stylesheet/stylus">
</style>


