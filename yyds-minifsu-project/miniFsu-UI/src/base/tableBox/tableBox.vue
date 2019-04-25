<template>
  <div class="table-box">
    <el-table
      ref = 'table'
      @row-click = "rowClickFn"
      v-loading="loading"
      element-loading-spinner="ic ic-spinner ic-spin ic-5x"
      element-loading-background="transparent"
      :data='data'
      :size='size'
      :width='width'
      :height='"100%"'
      :maxHeight='"100%"'
      :fit='true'
      :stripe='stripe'
      :border='border'
      :rowKey='rowKey'
      :context='context'
      :showHeader='showHeader'
      :showSummary='showSummary'
      :sumText='sumText'
      :summaryMethod='summaryMethod'
      :rowClassName='"normal-row " + (rowClassName || "")'
      :rowStyle='rowStyle'
      cellClassName='normal-cell'
      :cellStyle='cellStyle'
      headerRowClassName='normal-header'
      :headerRowStyle='headerRowStyle'
      :headerCellClassName='headerCellClassName'
      :headerCellStyle='headerCellStyle'
      :highlightCurrentRow='highlightCurrentRow'
      :currentRowKey='currentRowKey'
      :emptyText='emptyText'
      :expandRowKeys='expandRowKeys'
      :defaultExpandAll='defaultExpandAll'
      :defaultSort='defaultSort'
      :tooltipEffect='tooltipEffect'
      :spanMethod='spanMethod'
      @sort-change = 'sortChange'
      @selection-change="handleSelectionChange"
      @expand-change="handleExpandChange"
    >
      <slot></slot>
      <div slot="empty" class="empty">
        <div class="no-data" v-if = '!loading'>
          <div class="img img-2x"></div>
        </div>
      </div>
    </el-table>
    <pagination :value="pagination"></pagination>
  </div>

</template>

<script>
// import ElTable1 from './table/src/table.vue'
import Vue from 'vue'
Vue.prototype.$tableFilter = filters => {
  let filter = Vue.filter('modalFilter')
  if (filters) {
    return (row, column, cellValue) => {
      return filter(cellValue, filters)
    }
  }
}
export default {
  name: 'tableBox',
  // components: {ElTable1},
  props: [
    'data',
    'size',
    'width',
    'height',
    'maxHeight',
    'fit',
    'stripe',
    'border',
    'rowKey',
    'context',
    'showHeader',
    'showSummary',
    'sumText',
    'summaryMethod',
    'rowClassName',
    'rowStyle',
    'cellClassName',
    'cellStyle',
    'headerRowClassName',
    'headerRowStyle',
    'headerCellClassName',
    'headerCellStyle',
    'highlightCurrentRow',
    'currentRowKey',
    'emptyText',
    'expandRowKeys',
    'defaultExpandAll',
    'defaultSort',
    'tooltipEffect',
    'spanMethod',
    'loading',
    'rowClick',
    'pagination'
  ],
  data () {
    return {
      selectedItem: []
    }
  },
  methods: {
    rowClickFn (args) {
      this.rowClick && this.rowClick(args)
    },
    changeExpand (row, event = {stopPropagation () {}}) {
      console.log(event)
      let bodys = this.$refs.table.$children.filter(v => (v.$options._componentTag === 'table-body'))
      bodys[0].handleExpandClick(row, event)
    },
    sortChange (value) {
      this.$emit('sort-change', value)
    },
    handleSelectionChange (value) {
      this.selectedItem = value;
      this.$emit('selection-change', value);
    },
    handleExpandChange (value) {
//      event = document.getElementsByTagName('body');
//      window.dispatchEvent(event);
//      this.$emit('expand-change', value);
    },
    print () {
      let body = this.$refs.table.$refs.fixedBodyWrapper.childNodes[0].innerHTML.match(/<tbody[\s\S]*/g)
      let header = this.$refs.table.$refs.fixedHeaderWrapper.childNodes[0].innerHTML.match(/<thead[\s\S]*/g)
      return `<table style = "width: 100%">${header}${body}</table>`
    },
    doLayout () {
      this.$nextTick(() => {
        this.$refs.table.doLayout()
        this.$nextTick(() => {
          this.$refs.table.doLayout()
        })
      })
    }

  }
}
</script>

<style lang='stylus'>
color-darker = #091734
color-lighter = #152545
color-border = #3F4C66
realTimeDeviceSignalList
.table-box
  overflow hidden
  display flex
  flex-direction column
  width 100%
  .el-table
    th
      height: auto
      padding: 6px 0
    td
      height auto
      padding 4px 0
      &.el-table__expanded-cell
        .form-item-expand
          text-align left
          margin-left 50px
          .el-form-item
            margin-right 30px
            /*background: #eeeeee;*/
            border-radius 5px
            padding 0 5px
            margin-bottom 0
/*
.el-table
flex 1
color #fff
.ic
  padding 0 3px
.el-table__body-wrapper
  background-color color-lighter
&:before,.el-table__fixed:before,.el-table__fixed-right:before,.el-table__fixed-right-patch
  background-color color-lighter
tr.normal-row,tr.normal-header,tr.normal-header th, td.el-table__expanded-cell
  background-color color-lighter
  color #fff
  border none
td.el-table__expanded-cell
  border 1px inset color-border
  border-left 1px hidden transparent
  border-right 1px hidden transparent
  border-top inset color-border !important
  border-color color-border !important
  background-color color-lighter !important
  form
    display flex
    flex-wrap wrap
    .el-form-item
      width 32%
      margin-bottom 5px
      .el-form-item__label
        color #fff
tr.normal-row.hover-row
  background-color color-darker
.el-table__body tr.hover-row>td.normal-cell, &.el-table--enable-row-hover .el-table__body tr:hover>td, td.el-table__expanded-cell:hover
  background-color color-darker
  border 1px solid transparent
  border-left 1px hidden transparent
  border-right 1px hidden transparent
table
  border-collapse collapse
td.normal-cell
  border-bottom 1px inset color-border
  border 1px inset color-border
  border-left 1px hidden transparent
  border-right 1px hidden transparent
  border-collapse collapse
.el-table__empty-block
  background-color color-lighter
  .el-table__empty-text
    display block
    width 100%
    height 100%
.empty
  position relative
  height 100%


.table-box .el-table__expand-icon, .print-screen .el-table__expand-icon
display none
*/
</style>
