<template>
  <div class="toggle-panel" :class = 'className' :style = "directionStyle">
    <div class="wrapper">
      <div class="content" :style = "directionStyleContent">
        <slot></slot>
      </div>
    </div>
    <div class="toggle-button cursor-point" @click='toggled = !toggled'>
      <i :class="'ic ic-menu-' + (toggled ? 'left' : 'right')"></i>
    </div>

  </div>
</template>

<script>
export default {
  name: 'togglePanel',
  data () {
    return {
      toggled: false
    }
  },
  props: {
    direction: {
      required: true
    },
    toEdge: Boolean,
    margin: Boolean,
    size: {
      type: Number,
      default: 200
    }
  },
  methods: {
    computeStyle (isContent) {
      let ds = {}
      let size = (!isContent && this.toggled ? 0 : this.size) + (isContent && this.toEdge ? 24 : 0) + 'px'
      switch (this.direction) {
        case 'left':
        case 'right': {
          ds.width = size
          break
        }
        case 'up':
        case 'down': {
          ds.height = size
          break
        }
      }
      return ds
    }
  },
  computed: {
    className () {
      let cn = [this.direction]
      this.toggled && cn.push('toggled')
      this.toEdge && cn.push('toEdge')
      this.margin && cn.push('margin')
      return cn
    },
    directionStyle () {
      let ds = this.computeStyle()
      return ds
    },
    directionStyleContent () {
      let ds = this.computeStyle(true)
      return ds
    }
  }
}
</script>

<style scoped lang = 'stylus'>
gap = 24px
btnEdgeLong = 42px
btnEdgeShort = 21px

.toggle-panel
  position relative
  display flex

  &.left
    height 100%
    flex-direction column
    transition width 0.5s, margin 0.5s
    .wrapper .content
      height 100%
    .toggle-button
      height btnEdgeLong
      top 50%
      right -(btnEdgeShort / 2)
      margin-top - (btnEdgeLong / 2)
      i
        transform rotate(-180deg)
        line-height btnEdgeLong
        margin-left 2px
    &.margin
      margin-right gap
    &.margin.toggled
      margin-right 0
    &.toggled .content
      transform translateX(-100%)
    &.toEdge
      &.toggled .wrapper
        transform translateX(-(gap))
      &.toggled .toggle-button
        margin-right gap - (btnEdgeShort / 2)
      .wrapper
        margin -(gap)
        margin-right 0

  &.right
    height 100%
    flex-direction column
    transition width 0.5s
    .wrapper .content
      height 100%
    .toggle-button
      height btnEdgeLong
      top 50%
      left -(btnEdgeShort / 2)
      margin-top - (btnEdgeLong / 2)
      margin-left - (btnEdgeLong / 2)
      i
        line-height btnEdgeLong
        margin-left 2px
    &.margin
      margin-left gap
    &.margin.toggled
      margin-left 0
    &.toEdge
      &.toggled .wrapper
        margin-right 0
        transform translateX(gap)
      &.toggled .toggle-button
        margin-left gap - (btnEdgeShort / 2)
      .wrapper
        transition margin-right 0.5s
        margin -(gap)
        margin-left 0

  &.up
    width 100%
    flex-direction row
    transition height 0.5s, margin 0.5s
    .wrapper .content
      width 100%
    .toggle-button
      width btnEdgeLong
      bottom -(btnEdgeShort / 2)
      margin-left - (btnEdgeLong / 2)
      right 50%
      i
        transform rotate(-90deg)
        line-height btnEdgeShort
        margin-left 2px
    &.margin
      margin-bottom gap
    &.margin.toggled
      margin-bottom 0
    &.toggled .content
      transform translateY(-100%)
    &.toEdge
      &.toggled .wrapper
        transform translateY(-(gap))
      &.toggled .toggle-button
        margin-bottom gap - (btnEdgeShort / 2)
      .wrapper
        margin -(gap)
        margin-bottom 0

  &.down
    width 100%
    flex-direction row
    transition height 0.5s, margin 0.5s
    .wrapper .content
      width 100%
    .toggle-button
      width btnEdgeLong
      top -(btnEdgeShort / 2)
      right 50%
      i
        transform rotate(90deg)
        line-height btnEdgeShort
        margin-right 3px
    &.margin
      margin-top gap
    &.margin.toggled
      margin-top 0
    &.toEdge
      &.toggled .wrapper
        transform translateY(gap)
      &.toggled .toggle-button
        margin-top gap - (btnEdgeShort / 2)
      .wrapper
        margin -(gap)
        margin-top 0

  .wrapper
    margin 0
    overflow hidden
    position relative
    transition transform 0.5s
    background-color #152545
    flex 1
    .content
      transition transform 0.5s
      position absolute
      font-size 20px
      color #fff
  .toggle-button
    background-color #051530
    position absolute
    border-radius 7px
    height btnEdgeShort
    width btnEdgeShort
    transition all 0.5s
    margin 0
    font-weight 400
    text-align center
    color #fff
  &.toggled .toggle-button
    box-shadow 0 0 10px 0 #3AB0FF
</style>
