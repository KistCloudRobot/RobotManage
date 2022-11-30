<template>
  <div class="widget-chart">
    <slot name="headerLabel" />
    <div class="flex-1">
      <iframe :src="`${url}&panelId=${panelId}&from=${getFromDate}&to=${getToDate}&theme=${theme}`" width="100%" height="100%" />
    </div>

    <slot name="footerLabel" />
  </div>
</template>

<script>
export default {
  props: {
    url: String,
    panelId: Number,
    from : {type: String, default: 'now/d'},
    to: {type: String, default: 'now/d'},
    theme: { type: String, default: 'light'}
  },
  data() {
    return {

    }
  },
  computed: {
    getFromDate() {
      const vm = this;
      return vm.$moment(vm.from).valueOf() || vm.from;
    },
    getToDate() {
      const vm = this;
      return vm.$moment(vm.$moment(vm.to).add(1, 'days')).endOf() -1  || vm.to;
    }
  },
}
</script>

<style lang="scss" scoped>
.widget-chart {
  margin-bottom: 0;
  height: 100%;
}
</style>
