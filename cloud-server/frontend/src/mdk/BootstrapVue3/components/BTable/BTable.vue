<template>
  <div v-if="responsive" :class="responsiveClasses">
    <table :class="classes">
      <thead>
        <slot v-if="$slots['thead-top']" name="thead-top" />
        <tr>
          <th
            v-for="field in computedFields"
            :key="field.key"
            scope="col"
            :class="[field.class, field.thClass, field.variant ? `table-${field.variant}` : '']"
            :title="field.headerTitle"
            :abbr="field.headerAbbr"
            :style="field.thStyle"
            v-bind="field.thAttr"
            :id="'mdk-field-'+field.key"
            @click="sortedField(field)"
            :aria-sort="'none'"
          >
            <slot
              v-if="$slots['head(' + field.key + ')']"
              :name="'head(' + field.key + ')'"
              :label="field.label"
            />
            <template v-else>
              <div>{{ field.label }}</div>
              <template v-if="field.sortable">
                <span class="sr-only"> (Click to sort Ascending) </span>
              </template>
            </template>
          </th>
        </tr>
        <tr v-if="$slots['thead-sub']">
          <td
            v-for="field in computedFields"
            :key="field.key"
            scope="col"
            :class="[field.class, field.thClass, field.variant ? `table-${field.variant}` : '']"
          >
            <slot
              v-if="$slots['thead-sub']"
              name="thead-sub"
              :items="computedFields"
              v-bind="field"
            />
            <template v-else>{{ field.label }}</template>
          </td>
        </tr>
      </thead>
      <tbody>
        <tr v-if="$slots['top-row']">
          <slot
              name="top-row"
            />
        </tr>

        <template v-if="items">
          <!-- eslint-disable-next-line vue/require-v-for-key -->
          <tr v-for="(tr, trIndex) in items" :class="[tr._rowVariant ? `table-${tr._rowVariant}` : 'b-tr']"
            @click="selectedRow(tr, trIndex)"
            :aria-selected="trIndex"
          >
            <td
                v-for="(field, index) in computedFields"
                :key="field.key"
                v-bind="field.tdAttr"
                :class="[
              field.class,
              field.tdClass,
              field.variant ? `table-${field.variant}` : '',
              tr?._cellVariants && tr?._cellVariants[field.key]
                ? `table-${tr?._cellVariants[field.key]}`
                : '',
            ]"
            >
<!--              {{tr[field.key]}}-->
              <slot
                  v-if="$slots['cell(' + field.key + ')']"
                  :name="'cell(' + field.key + ')'"
                  :value="tr[field.key]"
                  :index="index"
                  :item="tr"
                  :items="items"
              />
              <template v-else>{{ tr[field.key] }}</template>
            </td>
          </tr>
        </template>
      </tbody>
      <tfoot v-if="footCloneBoolean">
        <tr>
          <th
            v-for="field in computedFields"
            :key="field.key"
            v-bind="field.thAttr"
            scope="col"
            :class="[field.class, field.thClass, field.variant ? `table-${field.variant}` : '']"
            :title="field.headerTitle"
            :abbr="field.headerAbbr"
            :style="field.thStyle"
          >
            {{ field.label }}
          </th>
        </tr>
      </tfoot>
      <caption v-if="$slots['table-caption']">
        <slot name="table-caption" />
      </caption>
      <caption v-else-if="caption">
        {{
          caption
        }}
      </caption>
    </table>
  </div>
  <table v-else :class="classes">
    <thead>
      <slot v-if="$slots['thead-top']" name="thead-top" />
      <tr>
        <th
          v-for="field in computedFields"
          :key="field.key"
          scope="col"
          :class="[field.class, field.thClass, field.variant ? `table-${field.variant}` : '']"
          :title="field.headerTitle"
          :abbr="field.headerAbbr"
          :style="field.thStyle"
          v-bind="field.thAttr"
        >
          <slot
            v-if="$slots['head(' + field.key + ')']"
            :name="'head(' + field.key + ')'"
            :label="field.label"
          />
          <template v-else>{{ field.label }}</template>
        </th>
      </tr>
      <tr v-if="$slots['thead-sub']">
        <td
          v-for="field in computedFields"
          :key="field.key"
          scope="col"
          :class="[field.class, field.thClass, field.variant ? `table-${field.variant}` : '']"
        >
          <slot
            v-if="$slots['thead-sub']"
            name="thead-sub"
            :items="computedFields"
            v-bind="field"
          />
          <template v-else>{{ field.label }}</template>
        </td>
      </tr>
    </thead>
    <tbody>
      <!-- eslint-disable-next-line vue/require-v-for-key -->
      <tr v-for="tr in items" :class="[tr._rowVariant ? `table-${tr._rowVariant}` : null]"
      >
        <td
          v-for="(field, index) in computedFields"
          :key="field.key"
          v-bind="field.tdAttr"
          :class="[
            field.class,
            field.tdClass,
            field.variant ? `table-${field.variant}` : '',
            tr?._cellVariants && tr?._cellVariants[field.key]
              ? `table-${tr?._cellVariants[field.key]}`
              : '',
          ]"
        >
          <slot
            v-if="$slots['cell(' + field.key + ')']"
            :name="'cell(' + field.key + ')'"
            :value="tr[field.key]"
            :index="index"
            :item="tr"
            :items="items"
          />
          <template v-else>{{ tr[field.key] }}</template>
        </td>
      </tr>
    </tbody>
    <tfoot v-if="footCloneBoolean">
      <tr>
        <th
          v-for="field in computedFields"
          :key="field.key"
          v-bind="field.thAttr"
          scope="col"
          :class="[field.class, field.thClass, field.variant ? `table-${field.variant}` : '']"
          :title="field.headerTitle"
          :abbr="field.headerAbbr"
          :style="field.thStyle"
        >
          {{ field.label }}
        </th>
      </tr>
    </tfoot>
    <caption v-if="$slots['table-caption']">
      <slot name="table-caption" />
    </caption>
    <caption v-else-if="caption">
      {{
        caption
      }}
    </caption>
  </table>
</template>

<script setup lang="ts">
// import type {Breakpoint} from '../../types'
import {defineComponent, computed, toRef} from 'vue'
import type {Booleanish, ColorVariant, TableField, TableItem, VerticalAlign} from '../../types'
import {useBooleanish} from '../../composables'
import useItemHelper from './itemHelper'

interface BTableProps {
  align?: VerticalAlign
  caption?: string
  captionTop?: Booleanish
  borderless?: Booleanish
  bordered?: Booleanish
  borderVariant?: ColorVariant
  dark?: Booleanish
  fields?: Array<TableField>
  footClone?: Booleanish
  hover?: Booleanish
  items?: Array<TableItem>
  responsive?: boolean | 'sm' | 'md' | 'lg' | 'xl' | 'xxl'
  small?: Booleanish
  striped?: Booleanish
  variant?: ColorVariant
  selectable?: Booleanish
}

const props = withDefaults(defineProps<BTableProps>(), {
  captionTop: false,
  borderless: false,
  bordered: false,
  dark: false,
  fields: () => [],
  footClone: false,
  hover: false,
  items: () => [],
  responsive: false,
  small: false,
  striped: false,
  selectable: false,
})

const captionTopBoolean = useBooleanish(toRef(props, 'captionTop'))
const borderlessBoolean = useBooleanish(toRef(props, 'borderless'))
const borderedBoolean = useBooleanish(toRef(props, 'bordered'))
const darkBoolean = useBooleanish(toRef(props, 'dark'))
const footCloneBoolean = useBooleanish(toRef(props, 'footClone'))
const hoverBoolean = useBooleanish(toRef(props, 'hover'))
const smallBoolean = useBooleanish(toRef(props, 'small'))
const stripedBoolean = useBooleanish(toRef(props, 'striped'))

const classes = computed(() => [
  'table',
  {
    [`align-${props.align}`]: props.align,
    [`table-${props.variant}`]: props.variant,
    'table-striped': stripedBoolean.value,
    'table-hover': hoverBoolean.value,
    'table-dark': darkBoolean.value,
    'table-bordered': borderedBoolean.value,
    [`border-${props.borderVariant}`]: props.borderVariant,
    'table-borderless': borderlessBoolean.value,
    'table-sm': smallBoolean.value,
    'caption-top': captionTopBoolean.value,
  },
])

const emit = defineEmits(['row-selected', 'sort-changed'])


const selectedRow = (tr: any, trIndex: number) => {
  emit('row-selected', {...tr, trIndex: trIndex});
}


const sortedField = (field: any) => {
  const data = {
    currentPage: 1,
    filter: undefined,
    perPage: 0,
    sortBy: field.key,
    sortDesc: false
  }

  emit('sort-changed', {...data});
}




const itemHelper = useItemHelper()
const computedFields = computed(() => itemHelper.normaliseFields(props.fields, props.items))

// const computedItems = computed(() => items.length )

const responsiveClasses = computed(() => [
  {
    'table-responsive': typeof props.responsive === 'boolean' && props.responsive,
    [`table-responsive-${props.responsive}`]: typeof props.responsive === 'string',
  },
])

</script>
