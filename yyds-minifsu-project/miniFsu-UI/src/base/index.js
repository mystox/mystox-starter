import Modal from './modal'
import dateTimePicker from './dateTimePicker'
import NavBarMenu from './navBarMenu'
import TableBox from './tableBox'
import Panel from './panel'
import TogglePanel from './togglePanel'
import Tree from './tree'
import Pagination from './pagination'
import QueryForm from './queryForm'
import QueryFormItem from './queryFormItem'
import Tabs from './tabs'
import OperationBar from './operationBar'
import Breadcrumb from './breadcrumb'

export default {
  install (Vue, opts) {
    Vue.component(Modal.name, Modal)
    Vue.component(dateTimePicker.name, dateTimePicker)
    Vue.component(NavBarMenu.name, NavBarMenu)
    Vue.component(TableBox.name, TableBox)
    Vue.component(Panel.name, Panel)
    Vue.component(TogglePanel.name, TogglePanel)
    Vue.component(Tree.name, Tree)
    Vue.component(Pagination.name, Pagination)
    Vue.component(QueryForm.name, QueryForm)
    Vue.component(QueryFormItem.name, QueryFormItem)
    Vue.component(Tabs.name, Tabs)
    Vue.component(OperationBar.name, OperationBar)
    Vue.component(Breadcrumb.name, Breadcrumb)

    Vue.prototype.$tableFilter = filters => {
      let filter = Vue.filter('modalFilter')
      if (filters) {
        return (row, column, cellValue) => {
          return filter(cellValue, filters)
        }
      }
    }

    const getSingleNodePath = (node) => {
      if (node.key === '-1') {
        return ''
      } else {
        return getSingleNodePath(node.parent) + node.key
      }
    }
    Vue.prototype.$tree = {
      getTierTree (tree, callback) {
        Vue.prototype.$api.getTierTree().then((res) => {
          let firstTier = res.data.firstTier
          firstTier = {
            enterpriseId: null,
            uniqueCode: null,
            name: '全部',
            code: '-1',
            nextTier: firstTier
          }
          tree.data = [firstTier]
          if (tree.ready) {
            callback(tree)
          } else {
            tree.onReady = callback
          }
        })
      },
      getNodesPath (nodes) {
        if (!Array.isArray(nodes)) {
          nodes = [nodes]
        }
        return nodes.map(v => getSingleNodePath(v))
      }
    }
    Vue.prototype._ctx = process.env.SCLOUD_CTX
  }
}
