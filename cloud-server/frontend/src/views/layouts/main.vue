<script>
import { layoutComputed } from "../../state/helpers";
import Vertical from "./vertical";
import Horizontal from "./horizontal";

/**
 * Main component
 */
export default {
  components: {
    Vertical,
    // Horizontal
  },
  // setup() {
  //   console.log(this)
  // },
  // computed: {
  //   ...layoutComputed
  // },
  methods: {},
};
</script>

<template>
  <div>
    <Vertical :layout="'vertical'">
      <slot></slot>
    </Vertical>

<!--    <Horizontal :layout="'horizontal'">-->
<!--      <slot />-->
<!--    </Horizontal>-->
  </div>
</template>
