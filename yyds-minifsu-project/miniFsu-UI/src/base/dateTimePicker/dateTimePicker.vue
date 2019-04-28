<template>
  <el-time-picker v-if = "['hour', 'minute', 'second'].indexOf(type) >= 0" :value-format = "{hour: 'HH:00', minute: 'HH:mm', second: 'HH:mm:ss' }[type]" :format = "{hour: 'HH:00', minute: 'HH:mm', second: 'HH:mm:ss' }[type]" :picker-options = "{selectableRange: start && end && (start + ' - ' + end)}" :value="value" @input="$emit('input', arguments[0])" :placeholder="placeholder || '选择时间'"></el-time-picker>
  <el-date-picker v-else :format = "format" :type = "type || 'datetime'" :value="value" @input="$emit('input', arguments[0])" :placeholder="placeholder || '选择日期时间'"></el-date-picker>
</template>

<script>
export default {
  name: 'dateTimePicker',
  data () {
    return {}
  },
  props: {
    type: {
      required: true
    },
    value: {
      required: true
    },
    format: String,
    start: String,
    end: String,
    placeholder: String
  },
  mounted () {
  }
}
</script>

<style scoped lang = "stylus">

</style>
