<template>
  <div
    class="el-tree-node"
    @click.stop="handleClick"
    v-show="node.visible"
    :class="{
      'is-expanded': expanded,
      'is-current': tree.store.currentNode === node,
      'is-hidden': !node.visible,
      'is-focusable': !node.disabled,
      'is-checked': !node.disabled && node.checked
    }"
    role="treeitem"
    tabindex="-1"
    :aria-expanded="expanded"
    :aria-disabled="node.disabled"
    :aria-checked="node.checked"
  >
    <div class="el-tree-node__content"
      :style="{ 'padding-left': (node.level - 1) * tree.indent + 'px' }">
      <span
        class="el-tree-node__expand-icon el-icon-caret-right"
        @click.stop="handleExpandIconClick"
        :class="{ 'is-leaf': node.isLeaf, expanded: !node.isLeaf && expanded }">
      </span>
      <el-checkbox
        v-if="showCheckbox"
        v-model="node.checked"
        :indeterminate="node.indeterminate"
        :disabled="!!node.disabled"
        @click.native.stop
        @change="handleCheckChange"
      >
      </el-checkbox>
      <el-checkbox
        class="radio-like-checkbox"
        v-if="showRadio && node.isLeaf"
        v-model="node.checked"
        :indeterminate="node.indeterminate"
        :disabled="!!node.disabled"
        @click.native.stop
        @change="handleCheckChange"
      >
      </el-checkbox>
      <span
        v-if="node.loading"
        class="el-tree-node__loading-icon el-icon-loading">
      </span>
      <node-content :node="node"></node-content>
    </div>
    <el-collapse-transition>
      <div
        class="el-tree-node__children"
        v-if="!renderAfterExpand || childNodeRendered"
        v-show="expanded"
        role="group"
        :aria-expanded="expanded"
      >
        <el-tree-nodee
          :render-content="renderContent"
          v-for="child in node.childNodes"
          :render-after-expand="renderAfterExpand"
          :key="getNodeKey(child)"
          :node="child"
          @node-expand="handleChildNodeExpand">
        </el-tree-nodee>
      </div>
    </el-collapse-transition>
  </div>
</template>

<script type="text/jsx">
import ElCollapseTransition from 'element-ui/src/transitions/collapse-transition'
import ElCheckbox from 'element-ui/packages/checkbox'
import ElRadio from 'element-ui/packages/radio'
import emitter from 'element-ui/src/mixins/emitter'

export default {
  name: 'ElTreeNodee',

  componentName: 'ElTreeNodee',

  mixins: [emitter],

  props: {
    node: {
      default () {
        return {}
      }
    },
    props: {},
    renderContent: Function,
    renderAfterExpand: {
      type: Boolean,
      default: true
    }
  },

  components: {
    ElCollapseTransition,
    ElCheckbox,
    ElRadio,
    NodeContent: {
      props: {
        node: {
          required: true
        }
      },
      render (h) {
        const parent = this.$parent
        const node = this.node
        const data = node.data
        const store = node.store
        return (
          parent.renderContent
            ? parent.renderContent.call(parent._renderProxy, h, { _self: parent.tree.$vnode.context, node, data, store })
            : <span class="el-tree-node__label">{ this.node.label }</span>
        )
      }
    }
  },

  data () {
    return {
      tree: null,
      expanded: false,
      childNodeRendered: false,
      showCheckbox: false,
      showRadio: false,
      oldChecked: null,
      oldIndeterminate: null
    }
  },

  watch: {
    'node.indeterminate' (val) {
      this.handleSelectChange(this.node.checked, val)
    },

    'node.checked' (val) {
      this.handleSelectChange(val, this.node.indeterminate)
    },

    'node.expanded' (val) {
      this.$nextTick(() => {
        this.expanded = val
      })
      if (val) {
        this.childNodeRendered = true
      }
    }
  },

  methods: {
    getNodeKey (node, index) {
      const nodeKey = this.tree.nodeKey
      if (nodeKey && node) {
        return node.data[nodeKey]
      }
      return index
    },

    handleSelectChange (checked, indeterminate) {
      if (this.oldChecked !== checked && this.oldIndeterminate !== indeterminate) {
        this.tree.$emit('check-change', this.node.data, checked, indeterminate)
      }
      this.oldChecked = checked
      this.indeterminate = indeterminate
    },

    handleClick () {
      const store = this.tree.store
      store.setCurrentNode(this.node)
      this.tree.$emit('current-change', store.currentNode ? store.currentNode.data : null, store.currentNode)
      this.tree.currentNode = this
      if (this.tree.expandOnClickNode) {
        this.handleExpandIconClick()
      }
      this.node.checked = !this.node.checked
      let ev = {target: {checked: this.node.checked}}
      this.handleCheckChange(this.node.checked, ev)

      this.tree.$emit('node-click', this.node.data, this.node, this)
    },

    handleExpandIconClick () {
      if (this.node.isLeaf) return
      if (this.expanded) {
        this.tree.$emit('node-collapse', this.node.data, this.node, this)
        this.node.collapse()
      } else {
        this.node.expand()
        this.$emit('node-expand', this.node.data, this.node, this)
      }
    },

    handleCheckChange (value, ev) {
      if (this.showCheckbox) {
        this.node.setChecked(ev.target.checked, !this.tree.checkStrictly)
      }
      if (this.showRadio) {
        this.node.setCheckedRadio(ev.target.checked, !this.tree.checkStrictly)
      }
    },

    handleChildNodeExpand (nodeData, node, instance) {
      this.broadcast('ElTreeNode', 'tree-node-expand', node)
      this.tree.$emit('node-expand', nodeData, node, instance)
    }
  },

  created () {
    const parent = this.$parent

    if (parent.isTree) {
      this.tree = parent
    } else {
      this.tree = parent.tree
    }

    const tree = this.tree
    if (!tree) {
      console.warn('Can not find node\'s tree.')
    }

    const props = tree.props || {}
    const childrenKey = props['children'] || 'children'

    this.$watch(`node.data.${childrenKey}`, () => {
      this.node.updateChildren()
    })

    this.showCheckbox = tree.showCheckbox
    this.showRadio = tree.showRadio

    if (this.node.expanded) {
      this.expanded = true
      this.childNodeRendered = true
    }

    if (this.tree.accordion) {
      this.$on('tree-node-expand', node => {
        if (this.node !== node) {
          this.node.collapse()
        }
      })
    }
  }
}
</script>


<style lang="stylus">
  .radio-like-checkbox
    .el-checkbox__inner
      border 1px solid #dcdfe6
      border-radius 100%
      width 14px
      height 14px
      backgroundcolor: #fff
      position relative
      cursor pointer
      display inline-block
      box-sizing border-box
    .el-checkbox__inner::after
      content ""
      width 4px
      height 4px
      border-radius 100%
      background-color #fff
      position absolute
      left 50%
      top 50%
      transform translate(-50%,-50%) scale(1) rotate(0) !important
      transition transform .15s cubic-bezier(.71,-.46,.88,.6)
    .is-checked .el-checkbox__inner
      border-color #409eff
      background #409eff
    .el-radio__input
      white-space nowrap
      cursor pointer
      outline none
      display inline-block
      line-height 1
      position relative
      vertical-align middle

</style>
