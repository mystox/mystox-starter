<template>
  <div class="cell" :class = "cell.type" >
    <!-- span 普通的显示一句话 -->
    <span v-if = "cell.type === 'span'" >{{cell.text | modalFilter(cell.filter)}}</span>

    <!-- input 输入框 属性有： name, [placeholder], [rule], [defaultValue] -->
    <el-input
      v-else-if = "cell.type === 'input'"
      type="text"
      @input = "modelFn"
      :value="bind"
      :placeholder="cell.placeholder || '请输入'">
    </el-input>

    <!-- textarea 输入框 属性有： name, [placeholder], [rule], [defaultValue] -->
    <el-input
      v-else-if = "cell.type === 'textarea'"
      type="textarea"
      @input = "modelFn"
      :value="bind"
      :disabled = "cell.disabled"
      :rows = "cell.rows"
      :placeholder="cell.placeholder || '请输入'">
    </el-input>

    <!--switch 选择 属性有：name, [rule], -->
    <el-switch
      v-else-if="cell.type === 'switch'"
      :value="bind == undefined ? cell.inactiveValue : bind"
      @input = "modelFn"
      :active-color="cell.activeColor"
      :inactive-color="cell.inactiveColor"
      :active-value="cell.activeValue"
      :inactive-value="cell.inactiveValue">
    </el-switch>

    <!-- select 下拉栏 属性有：name, options:{value, label}, [placeholder], [rule], [defaultValue] -->
    <el-select
      v-else-if = "cell.type === 'select'"
      @input = "modelFn"
      :value = "bind"
      @change = "cell.handleChange"
      :disabled = "cell.disabled"
      :placeholder="cell.placeholder">
      <el-option
        v-for="(item, index) in cell.options"
        :key="index" :label="(item.label || item) | modalFilter(cell.filter)"
        @input = "modelFn"
        :value="(item.value != undefined ? item.value : item)">
      </el-option>
    </el-select>

    <!-- button 按钮 属性有：[name](需要disabled时必填), html, click, [cooling], [rule] -->
    <el-button
      v-else-if = "cell.type === 'button'"
      v-html = "nonFormElementRule.cooling[cell.name] || cell.html"
      type = "cell.styleType || primary"
      :disabled="cell.disabled"
      @click="cell.handleClick(cell)">
    </el-button>

    <!-- dateTimePicker 时间选择器 name, style, [start], [end], [placeholder], [format], [rule], [defaultValue] -->
    <date-time-picker
      v-else-if = "cell.type === 'dateTimePicker'"
      :value = "bind"
      :type = "cell.style"
      :start="cell.start"
      :end="cell.end"
      @input = "modelFn"
      :placeholder="cell.placeholder"
      :format="cell.format">
    </date-time-picker>

    <!-- editableSelect 可编辑下拉框 属性有：name, options:{value, label}, [placeholder], [rule], [defaultValue] -->
    <el-select
      v-else-if = "cell.type === 'editableSelect'"
      :value = "bind"
      :placeholder="cell.placeholder || '直接输入以添加新条目'"
      filterable
      allow-create
      default-first-option>
      <el-option
        v-for="item in cell.options"
        :key="item.value"
        :label="item.label"
        @input = "modelFn"
        :value="item.value">
      </el-option>
    </el-select>

    <!-- radio 单选组 属性有：name, options:{value, label, [disabled]}, [border], [rule], [defaultValue] -->
    <div v-else-if = "cell.type === 'radio'"
         class="radio-group">
      <el-radio
        v-for = "item in cell.options"
        :key="item.value"
        @input = "modelFn"
        :value="bind"
        :border="cell.border"
        :disabled = "item.disabled"
        :label="item.value">{{item.label}}
      </el-radio>
    </div>

    <!-- checkbox 多选组 属性有：name, options:{value, label, [disabled]}, [min], [max], [border], [rule], [defaultValue] -->
    <el-checkbox-group
      v-else-if = "cell.type === 'checkbox'"
      @input = "modelFn"
      :value="bind"
      :min="cell.min"
      :max="cell.max">
      <el-checkbox
        v-for="item in cell.options"
        :label="item.value" :key="item.value">
        {{item.label}}
      </el-checkbox>
    </el-checkbox-group>

    <el-upload v-else-if = "cell.type === 'upload' && cell.isImage"
      :class="'image-uploader' + (uploadFiles[cell.name] && uploadFiles[cell.name].type === 'png' ? ' transparent' : '') + (errors[cell.name] ? ' error' : '')"
      action=""
      :show-file-list="false"
      :auto-upload="false"
      :on-change="validateUpdateFile(cell.rule.validFile, 'image', cell.name)">
      <div slot="tip" class="el-upload__tip">{{cell.hint}}</div>
      <img v-if="uploadFiles[cell.name]" :src="uploadFiles[cell.name].url" class="image">
      <i v-else class="ic ic-plus image-uploader-icon"></i>
    </el-upload>

    <div class="help-inline" v-if = "cell.rule && cell.rule.required">*</div>
  </div>
</template>

<script>
export default {
  name: 'formCell',
  props: ['cell', 'value', 'uploadFiles', 'nonFormElementValid', 'nonFormElementRule', 'errors', 'res', 'btnClickHandle'],
  data () {
    return {
      bind: null
    }
  },
  methods: {
    modelFn (n) {
      this.$set(this.res, this.cell.name, n)
    }
  },
  watch: {
    value (n, o) {
      this.bind = this.value
    }
  },
  mounted () {
    if (this.value !== undefined && this.value !== null) {
      this.bind = this.value
    }
  }
}
</script>

<style></style>
