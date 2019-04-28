<template>
  <div class="pagination">
    <el-pagination
      v-if = "pagiObj.total > 0"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="pagiObj.currentPage"
      :page-sizes="pagiObj.sizes"
      :page-size="pagiObj.pageSize"
      layout="prev, pager, next, sizes, total, jumper"
      :total="pagiObj.total">
    </el-pagination>
  </div>
</template>

<script>
export default {
  name: 'pagination',
  data () {
    return {
      pagiObj: {}
    }
  },
  methods: {
    handleSizeChange (size) {
      this.pagiObj.pageSize = size
      this.pagiObj.onChange(this.pagiObj)
    },
    handleCurrentChange (currentPage) {
      this.pagiObj.currentPage = currentPage
      this.pagiObj.onChange(this.pagiObj)
    },
    updatePagination (params) {
      if (!params) {
        params = {}
      } else if (typeof params === 'function') {
        params = {
          onChange: params
        }
      }
      this.pagiObj = {
        sizes: (params && params.sizes) || [5, 10, 15, 20, 30, 50, 100],
        pageSize: (params && params.pageSize) || 15,
        total: (params && params.total) || 0,
        currentPage: (params && params.currentPage) || 1,
        onChange: params && params.onChange
      }
    }
  },
  props: [
    'value'
  ],
  watch: {
    pagiObj (val, o) {
      if ((val && o &&
        val.sizes === o.sizes &&
        val.pageSize === o.pageSize &&
        val.total === o.total &&
        val.currentPage === o.currentPage &&
        val.onChange === o.onChange) || (!val && !o)
      ) {
        console.log('1')
        return
      }
      this.$emit('input', val)
    }
  },
  mounted () {
    this.updatePagination(this.value)
    this.$watch('value', (val, o) => {
      this.updatePagination(val)
    }, {deep: true})
  }
}
</script>

<style lang='stylus'>
.pagination
  text-align right
  padding 10px
  .el-select .el-input input
    /*color #fff*/
  .el-pager li,.el-pagination .btn-prev,.el-pagination .btn-next
    background-color transparent
    margin 0 3px
    min-width 30px
    text-align center
    /*border 1px solid #DDD*/
    padding 4px 8px
    font-size 14px
    height 30px
    line-height 20px
    border-radius 5px
    /*color #FFF*/
  .el-pager li.active+li
    /*border 1px solid #DDD*/
  .el-pager li.active,.el-pager li:hover,.el-pagination .btn-prev:hover,.el-pagination .btn-next:hover
    background-color #3AB0FF
    border-color #3AB0FF !important
  .el-pagination .btn-prev.disabled:hover,.el-pagination .btn-next.disabled:hover
    background-color transparent
  .el-pagination .el-select .el-input .el-input__inner
    height 30px
  .el-pagination__total,.el-pagination__jump
    /*color #fff*/
</style>
