<template>
  <div class="tree">
    <div class="input-box">
      <i class="ic ic-map-marker"></i>
      <input
        placeholder="输入关键字进行过滤"
        v-model="filterText" />
      <i class="ic ic-search"></i>
    </div>
    <el-treee
      :props="treeProps"
      :data="data"
      ref = "tree"
      node-key="code"
      :filter-node-method="filterNode"
      :expand-on-click-node="false"
      :default-expand-all="true"
      :default-checked-keys = "defaultCheckAll ? ['-1']: []"
      show-checkbox
      :on-ready="onReadyCallback"
      @check-change="handleCheckChangeAsync"
    ></el-treee>

  </div>
</template>

<script>
import ElTreee from './el-tree.vue'

export default {
  name: 'tree',
  components: {
    ElTreee
  },
  data () {
    return {
      filterText: '',
      treeProps: {
        label: 'name',
        children: 'nextTier'
      },
      treeData: [],
      changeAsyncObject: {
        list: [],
        lastTimeout: null
      },
      data: []
    }
  },
  props: {
    handleCheckChange: Function,
    defaultCheckAll: Boolean
  },
  watch: {
    filterText (val, o) {
      this.$refs.tree.filter(val)
    }
  },
  methods: {
    filterNode (value, data) {
      if (!value) return true
      return data.name.indexOf(value) !== -1
    },
    closeAllNodes () {
      this.$refs.tree.closeAllNodes()
    },
    expandllNodes () {
      this.$refs.tree.expandllNodes()
    },
    setAllNodeChecked (checked) {
      this.$refs.tree.setAllNodeChecked(checked)
    },
    getCheckedNodes (leafOnly) {
      return this.$refs.tree.getCheckedNodes(leafOnly)
    },
    handleCheckChangeAsyncTimeoutFn () {
      this.handleCheckChange && this.handleCheckChange(this.changeAsyncObject.list)
      this.changeAsyncObject.list = []
      this.changeAsyncObject.lastTimeout = []
    },
    handleCheckChangeAsync (value) {
      this.changeAsyncObject.list.push(value)
      window.clearTimeout(this.changeAsyncObject.lastTimeout)
      this.changeAsyncObject.lastTimeout = window.setTimeout(() => {
        this.handleCheckChangeAsyncTimeoutFn()
      }, 100)
    },
    onReadyCallback () {
      this.ready = true
      this.onReady && this.onReady(this)
    }
  },
  created () {}
}
</script>

<style lang = 'stylus'>
.tree
  .input-box
    height: 35px;
    display: flex;
    border-bottom: 1px solid rgba(233,233,233,0.2);
    background-color: #152545;
    .ic-map-marker, .ic-search
      width: 23px;
      line-height: 36px;
    .ic-map-marker
      font-size: 16px;
      text-align: right;
    .ic-search
      font-size: 14px;
    input
      flex 1
      background-color: transparent;
      border: none
      padding 0 15px
      color: #fff;
  .el-tree
    background-color: transparent
    .el-tree-node__content
      height: 31px;
      line-height: 31px;
    .el-tree-node__content:hover
      background-color: transparent
    .el-tree-node:focus>.el-tree-node__content // focus状态需要force state才能在属性中观测到。
      background-color: transparent
    .el-tree-node__label
      color: #fff;
      font-size: 12px;
      margin-top: 1px;
    .el-checkbox__inner
      background-color: transparent
    .el-checkbox__input.is-checked .el-checkbox__inner
      background-color: #409EFF
    & > .el-tree-node, & > .el-tree-node:focus
      & > .el-tree-node__content
        background-color: #2E4169;
      & > .el-tree-node__children > .el-tree-node
        & > .el-tree-node__content
          background-color: #273759;
</style>
