<template>
  <b-input-group-addon :id="id" :is-text="isTextBoolean" :tag="tag" append>
    <slot />
  </b-input-group-addon>
</template>

<script setup lang="ts">
// import type {BInputGroupAppendProps} from '../../types/components'
import BInputGroupAddon from './BInputGroupAddon.vue'
import {toRef} from 'vue'
import {useBooleanish} from '../../composables'
import type {Booleanish} from '../../types'

interface BInputGroupAppendProps {
  id?: string
  isText?: Booleanish
  tag?: string
}

const props = withDefaults(defineProps<BInputGroupAppendProps>(), {
  tag: 'div',
  isText: false,
})

const isTextBoolean = useBooleanish(toRef(props, 'isText'))
</script>
