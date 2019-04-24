<template>
  <div class="inline">
    <div @click="dialogFormVisible = true" class="inline">
      <slot></slot>
    </div>

    <el-dialog class = 'modal-wrapper' :append-to-body = "true" :close-on-click-modal='false' :close-on-press-escape='false' :title="$t(option.name)" :visible.sync="dialogFormVisible" :width="option.style.width">
      <!-- 滚动条层 -->
      <el-scrollbar
        tag="div"
        wrap-class="el-form__wrap"
        view-class="el-form__list"
        ref="scrollbar">
        <slot name="other-form"></slot>
        <el-form v-if = "option.form" v-for="(optionForm, formNumber) in (Array.isArray(option.form) ? option.form : [option.form])" :key="formNumber" :ref = '"form" + formNumber' class = "modal-form" :rules = 'rule' v-show = "formNumber === curFormNumber" :model="form[formNumber]">

          <slot name="other-form-cell"></slot>

          <div class="control-group-wrapper">
            <!-- 表单每一行 -->
            <div class="control-group" v-if="item" v-for="(item, key) in optionForm" :key="key">
              <el-form-item v-if="item[0]" class="control-content" :label = "$t(key)" label-width="180px" :prop = 'ruleIndex[key]'>
                <!-- 表单右侧每个单元， flex布局 -->
                <form-cell class="cell"
                           v-for = '(cell, index) in item' :key="index"
                           :cell = 'cell'
                           :uploadFiles = "uploadFiles"
                           :nonFormElementValid = "nonFormElementValid"
                           :nonFormElementRule = "nonFormElementRule"
                           :res = "res"
                           :btnClickHandle = "btnClickHandle"
                           :errors = "errors"
                           v-model = 'res[cell.name]'></form-cell>
              </el-form-item>
            </div>
          </div>

        </el-form>

        <!-- 模态框表格 -->
        <el-table v-if = "option.table" :data="option.table.data" style="width: 100%">
          <el-table-column v-for = "column in option.table.header" :key = "column.name" :prop="column.name" :label="column.label" :width="column.width"></el-table-column>
        </el-table>

      </el-scrollbar>

      <div slot="footer" class="dialog-footer model-footer">
        <el-button v-if="option.clear && curFormNumber === 0" @click="dialogFormVisible = false">{{option.clearText}}</el-button>
        <el-button v-if="Array.isArray(option.form) && curFormNumber > 0" @click="prev()">上一步</el-button>
        <el-button v-if="Array.isArray(option.form) && curFormNumber < option.form.length - 1" @click="next()">下一步</el-button>
        <el-button v-if="option.execute && !(Array.isArray(option.form) && curFormNumber < option.form.length - 1)" type="primary" @click="execute()"
        v-loading.fullscreen.lock="option.fullscreenLoading" element-loading-text="请稍等..." >{{option.executeText}}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>

  /***************  modal组件配置参数  ******************
   * this.props
   *    options [object]  modal配置
   *        name [string]   modal标题
   *        from [object]   modal表单
   *            'DEMO.MODIFY_DILOG_ITEM_NAME' [array] 表单项items,可以包含多个表单元素cell
   *                  (cell  [object]  表单元素)
   *                      type [string]  表单元素type
   *                      name [string]  表单元素name
   *                      defaultValue [string] 默认值
   *                      rule [object]  表单校验规则
   *                          required      [boolean] 是否必填
   *                          requiredError [string]  错误提示信息
   *                          equal         [string]  是否相等，主要用于密码重复验证
   *                          equalError    [String]  错误提示信息
   *                          regex         [string]  正则匹配规则
   *                          regexError    [string]  错误提示信息
   *                          ...                     其他组件自行陈扩展
   *
   *        clear [string] 取消按钮
   *        execute [string]  确定按钮
   */

import formCell from './formCell.vue'
// 表单元素列表
const formElementType = [
  'input',
  'select',
  'dateTimePicker',
  'radio',
  'checkbox',
  'upload'
]
export default {
  name: 'modal',
  components: {formCell},
  data () {
    return {
      dialogFormVisible: false,
      formLabelWidth: '120px',
      form: new Array(this.option.form[0] ? this.option.form.length : 1).join('.').split('.').map(v => ({})),
      curFormNumber: 0,
      res: {},
      rule: {},
      errors: {},
      ruleIndex: {},
      uploadFiles: {},
      ruleAsyncCallbacks: {},
      nonFormElementRule: {
        validOther: {},
        cooling: {}
      },
      nonFormElementValid: {}
    }
  },
  props: {
    option: {
      required: true
    },
  },
  watch: {
    dialogFormVisible (n, o) {
      if (n) {
        this.updateDefaultValue()
      } else { // 有可能点击x关闭
        this.clear()
      }
    }
  },
  methods: {
    toModal () {
      this.dialogFormVisible = true
    },
    /**
     * 提交表单（点击确认）
     * @Author   chenht
     * @DateTime 2018-02-26
     */
    execute () {
      // 验证所有元素是否合法
      this.$refs['form' + this.curFormNumber][0].validate((valid) => {
        if (valid) {
          // 有提交回调则调用
          this.option.execute && this.option.execute(this.res)
          !this.option.hold && this.clear()
        } else {
          return false
        }
      })
    },
    /**
     * 关闭模态框
     * @Author   chenht
     * @DateTime 2018-02-26
     */
    clear () {
      this.res = {}
      // 重置表单
      let i = 0
      while (this.$refs['form' + i]) {
        this.$refs['form' + i++][0].resetFields()
      }
      this.option.clear && this.option.clear()
      this.dialogFormVisible = false
      this.uploadFiles = {}
      this.curFormNumber = 0
    },
    next () {
      this.$refs['form' + this.curFormNumber][0].validate((valid) => {
        if (valid) {
          this.curFormNumber++
        } else {
          return false
        }
      })
    },
    prev () {
      this.curFormNumber--
    },
    /**
     * 处理button元素点击事件
     * @Author   chenht
     * @DateTime 2018-02-26
     * @param    {[type]}   button [description]
     * @return   {[type]}          [description]
     */
    btnClickHandle (button) {
      if (button.cooling) {
        // 冷却时间向上取整
        button.cooling = Math.ceil(button.cooling / 1000) * 1000
        let c = button.cooling / 1000

        // 将要显示的冷却时间保存在nonFormElementRule.cooling[button.name]中
        this.$set(this.nonFormElementRule.cooling, button.name, c + 's')
        let timeLine = new Array(c).join('.').split('.').map((u, i) => (c - i - 1)) // => [n .... 5,4,3,2,1,0]
        let fn = () => {
          let time = timeLine.shift()
          if (time) {
            this.$set(this.nonFormElementRule.cooling, button.name, time + 's')
            setTimeout(fn, 1000)
          } else {
            this.$set(this.nonFormElementRule.cooling, button.name, false)
          }
        }
        // 定时
        setTimeout(fn, 1000)
      }

      // 触发button原来的回调函数
      button.click && button.click(this.res)
    },
    uploadSuccessHandle (r) {
      console.log(r)
    },
    validateUpdateFile (validFn, type, name) {
      return file => {
        let cb = (result) => {
          if (result && result.name === 'Error') {
            this.$message.warning(result.message)
            this.$set(this.errors, name, true)
          } else {
            if (type === 'image') {
              this.$set(this.res, name, file)
              this.$set(this.uploadFiles, name, {file: file, url: file.url, type: file.name.split('.').last()})
            } else {

            }
            delete this.errors[name]
          }
        }
        let result = validFn(file, cb)
        result && cb(result)
      }
    },
    /**
     * 为表单元素设置默认值
     * @Author   chenht
     * @DateTime 2018-02-26
     */
    updateDefaultValue () {
      let _this = this

      let forms = Array.isArray(this.option.form) ? this.option.form : [this.option.form]
      forms.forEach(form => {
        for (let key in form) {
          form[key] && form[key].forEach(u => {
            if (u.type === 'checkbox') {
              _this.$set(_this.res, u.name, [])
            }
            if (u.defaultValue !== undefined) {
              // _this.res[u.name] = u.defaultValue>
              _this.$set(_this.res, u.name, u.defaultValue)
            }
          })
        }
      })
    },
    /**
     * 注册form元素的校验规则
     * @Author   chenht
     * @DateTime 2018-02-27
     * @return   {[type]}   [description]
     */
    registeRule () {
      let _this = this

      let forms = []
      if (!Array.isArray(this.option.form)) {
        forms = [this.option.form]
      } else {
        forms = this.option.form
      }
      forms.forEach(form => {
        // 对form对象中的所有属性遍历
        for (let key in form) {

          // 拿出单独每个cell，对其rule属性进行判断
          form[key] && form[key].forEach(u => {
            if (u.rule) {

              // 对于非表单元素
              if (formElementType.indexOf(u.type) < 0) {
                if (u.rule.validOther) {
                  let vo = u.rule.validOther
                  if (typeof u.rule.validOther === 'string') {
                    vo = [vo]
                  }
                  this.$set(this.nonFormElementRule.validOther, u.name, {})
                  this.$set(this.nonFormElementValid, u.name, false)
                  let watchFn = (n, o) => {
                    this.nonFormElementValid[u.name] = Object.values(this.nonFormElementRule.validOther[u.name]).every(e => e)
                  }
                  vo.forEach((w) => {
                    if (!this.nonFormElementRule.validOther[w]) {
                      this.$set(this.nonFormElementRule.validOther, w, [])
                    }
                    this.nonFormElementRule.validOther[w].push(u.name)
                    this.$set(this.nonFormElementRule.validOther[u.name], w, false)
                    this.$watch('nonFormElementRule.validOther.' + u.name + '.' + w, watchFn)
                  })
                }
                return
              }

              if (u.rule.asyncCallBack) {
                this.ruleAsyncCallbacks[u.name] = (() => {
                  let ifDo = true
                  let result
                  return (res) => {
                    if (ifDo) {
                      u.rule.asyncCallBack(res, (v) => {
                        result = v
                        let forms = _this.option.form[0] ? _this.option.form : [_this.option.form]
                        let hasField = Object.values(forms[_this.curFormNumber].__map(x => {
                          return x.find(y => y.name === u.name)
                        })).some(x => x)
                        if (hasField) {
                          _this.$refs['form' + _this.curFormNumber][0].validateField(u.name)
                        } else {
                          ifDo = true
                        }
                      })
                      ifDo = false
                    } else {
                      ifDo = true
                    }
                    return result
                  }
                })()
              }

              // 对于
              this.rule[u.name] = {
                validator (rule, value, callback) {
                  let nonForm = _this.nonFormElementRule.validOther[rule.field]
                  if (nonForm) {
                    nonForm.forEach(itr => {
                      _this.$set(_this.nonFormElementRule.validOther[nonForm], rule.field, true)
                    })
                  }
                  _this.$set(_this.errors, rule.field, false)

                  function validate (arg) {
                    if (nonForm) {
                      nonForm.forEach(itr => {
                        _this.$set(_this.nonFormElementRule.validOther[nonForm], rule.field, false)
                      })
                    }
                    if (arg) {
                      _this.$set(_this.errors, rule.field, true)
                    }
                    return callback(arg)
                  }

                  let curV = _this.res[rule.field]
                  if (u.rule.regex && curV && !u.rule.regex.test(curV)) {
                    return validate(new Error(u.rule.regexError || ' '))
                  }
                  if (u.rule.required && (!curV && curV !== 0)) {
                    return validate(new Error(u.rule.requiredError || ' '))
                  }

                  if (u.rule.equal &&
                    (_this.res.hasOwnProperty(u.rule.equal)
                      ? (curV !== _this.res[u.rule.equal]) : (curV !== u.rule.equal))
                  ) {
                    return validate(new Error(u.rule.equalError || ' '))
                  }

                  if (u.rule.lessThan) {
                    let cmp = 0
                    if (_this.res.hasOwnProperty(u.rule.lessThan)) {
                      cmp = _this.res[u.rule.lessThan]
                    } else {
                      cmp = u.rule.lessThan
                    }
                    if (
                      (u.type === 'input' && !isNaN(Number(curV)) && Number(curV) >= Number(cmp)) ||
                      (u.type === 'dateTimePicker' && new Date(curV).getTime() >= new Date(cmp).getTime())
                    ) {
                      return validate(new Error(u.rule.lessThanError || ' '))
                    }
                  }

                  if (u.rule.greatThan) {
                    let cmp = 0
                    if (_this.res.hasOwnProperty(u.rule.greatThan)) {
                      cmp = _this.res[u.rule.greatThan]
                    } else {
                      cmp = u.rule.greatThan
                    }
                    if (
                      (u.type === 'input' && !isNaN(Number(curV)) && Number(curV) <= Number(cmp)) ||
                      (u.type === 'dateTimePicker' && new Date(curV).getTime() <= new Date(cmp).getTime())
                    ) {
                      return validate(new Error(u.rule.lessThanError || ' '))
                    }
                  }

                  if (u.rule.callback) {

                  }

                  if (u.rule.asyncCallBack) {
                    if (!_this.ruleAsyncCallbacks[u.name](_this.res)) {
                      return validate(new Error(u.rule.asyncCallBackError || ' '))
                    }
                  }

                  callback()
                },
                trigger: 'change'
              }
              this.ruleIndex[key] = u.name
            }
          })
        }
      })
    }
  },
  mounted () {
    this.registeRule()
  }
}
</script>

<style lang = "stylus">
.modal-wrapper
  textarea,
  input[type='text'],
  input[type='password'],
  input[type='datetime'],
  input[type='datetime-local'],
  input[type='date'],
  input[type='month'],
  input[type='time'],
  input[type='week'],
  input[type='number'],
  input[type='email'],
  input[type='url'],
  input[type='search'],
  input[type='tel'],
  input[type='color'],
  select
    color #606266
.inline
  display inline
.el-dialog
  border-radius 4px
  width 560px
  max-height 600px
  .el-dialog__body
    padding-bottom 18px
.modal-wrapper
  text-align left
  color #DDD
  .el-form__wrap
    max-height 400px
.control-group
  text-align left
  display flex
  .el-form-item
    margin-bottom 20px
  .control-label
    width 180px
    height 40px
    line-height 40px
    text-align center
  .control-content
    width 410px
    .el-form-item__label
      text-align center
    .el-form-item__content
      display flex
    .cell
      margin-right 5px
      &.button
        flex 1.5
        button
          padding 12px 0px
          width 100%
      &.input
        flex 3
      &.select
        flex 3
        .el-select
          width 100%
      &.span
        flex 3
        height 40px
        line-height 40px
      &.dateTimePicker
        flex 3
        .el-date-editor
          width 100%
      &.editableSelect
        flex 3
        .el-select
          width 100%
        input
          cursor auto
      &.radio
        flex 3
        .radio-group
          margin-right -30px
          .el-radio
            margin-right 30px
          .el-radio+.el-radio
            margin-left 0
      &.checkbox
        flex 3
        .el-checkbox-group
          margin-right -30px
          .el-checkbox
            margin-right 30px
          .el-checkbox+.el-checkbox
            margin-left 0
      &.upload
        flex 3
        .image-uploader
          &.transparent .el-upload
            background url('transparent.jpg')
          &.error .el-upload
            border-color #f56c6c
          .el-upload
            border 1px dashed #d9d9d9
            border-radius 6px
            cursor pointer
            position relative
            overflow hidden
          .el-upload:hover
            border-color #409EFF
        .image-uploader-icon
          font-size 28px
          color #8c939d
          width 178px
          height 161px
          line-height 178px
          text-align center
        .image
          max-width 178px
          max-height 178px
          display block
        .el-upload__tip
          line-height 12px
          white-space nowrap
      .help-inline
        color #f00
        position absolute
        right -10px
        top 3px
</style>
