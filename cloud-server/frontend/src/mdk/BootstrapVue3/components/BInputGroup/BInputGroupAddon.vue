<template>
  <component :is="tag" :id="id" class="d-flex" :class="computedClasses">
    <b-input-group-text v-if="isTextBoolean">
      <slot />
    </b-input-group-text>
    <slot v-else />
  </component>
</template>

<script setup lang="ts">
// import type {BInputGroupAddonProps} from '../../types/components'
import type {Booleanish} from '../../types'
import {useBooleanish} from '../../composables'
import {computed, toRef} from 'vue'
import BInputGroupText from './BInputGroupText.vue'

interface BInputGroupAddonProps {
  append?: Booleanish
  id?: string
  isText?: Booleanish
  tag?: string
}

const props = withDefaults(defineProps<BInputGroupAddonProps>(), {
  append: false,
  tag: 'div',
  isText: false,
})

const appendBoolean = useBooleanish(toRef(props, 'append'))
const isTextBoolean = useBooleanish(toRef(props, 'isText'))

const computedClasses = computed(() => ({
  'input-group-append': appendBoolean.value,
  'input-group-prepend': !appendBoolean.value,
}))
</script>
