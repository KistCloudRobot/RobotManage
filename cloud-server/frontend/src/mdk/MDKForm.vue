<template>
  <table v-if="labelType=='table'" class="mdk-form" :disabled="readOnly">
    <colgroup class="col24">
      <col><col><col><col><col><col><col><col><col><col><col><col>
      <col><col><col><col><col><col><col><col><col><col><col><col>
    </colgroup>
    <template v-for="(row, row_index) in fields" :key="row_index">
      <tr>
        <template v-if="typeof row === 'string'">
          <td colSpan="24" class="form-sub-title">
            {{row}}
          </td>
        </template>
        <template v-else v-for="(column, col_index) in toArray(row)">
          <td v-if="column!=null" :key="'label-' + col_index"
              class="mdk-input-label">
            <label>{{column.label}}</label>
          </td>
          <td v-if="column!=null" :key="'input-' + col_index"
              class="mdk-input-area"
              :colSpan="calcColumnSpan(col_index, row)" >
            <slot name="inputs" :column="column" :entity="valueSet">
              <input class="form-control" :disabled="readOnly" style="border: none" v-model="getInputSet(column.key)[column.key]"/>
<!--              <MDKInput :field="column" :ref="column.key"-->
<!--                        v-model="getInputSet(column.key)[column.key]"-->
<!--                        :disabled="readOnly"-->
<!--                        @focus="onFocused(column)"-->
<!--                        @input="onFieldChanged(column, $event)"/>-->
<!--              <div v-show="showError" class="input-error-message">-->
<!--                {{errorMessages[column.key]}}-->
<!--              </div>-->
            </slot>
          </td>
        </template>
      </tr>
    </template>
  </table>
  <div v-else :class="'mdk-form ' + labelType" >
    <template v-for="(row, row_index) in fields" :key="row_index">
      <template v-if="typeof row === 'string'">
        <slot name="form-sub-title" :subtitle="row" :index="row_index">
          <div :class="'form-sub-title ' + getColumnClass(0, row)"
               v-html="row">
          </div>
        </slot>
      </template>
      <template v-else v-for="(column, col_index) in toArray(row)">
        <div v-if="column!=null" :key="row_index + '-' + col_index"
             :class="getColumnClass(col_index, row)" >
          <label v-if="hasLeftLabel()" class='mdk-input-label'>
            <slot name="input-label" :column="column">
              {{column.label}}
            </slot>
          </label>
          <div>
            <slot name="inputs" :column="column" :entity="valueSet">
              <MDKInput :field="column" :ref="column.key"
                        v-model="getInputSet(column.key)[column.key]"
                        :value="getInputSet(column.key)[column.key]"
                        :disabled="readOnly"
                        :placeholder="placeholder"
                        @focus="onFocused(column)"
                        @input="onFieldChanged"/>
              <div v-show="showError" class="input-error-message">
                {{errorMessages[column.key]}}
              </div>
            </slot>
          </div>
          <label v-if="hasRightLabel()" class="mdk-input-label">
            <slot name="input-label" :column="column">
              {{column.label}}
            </slot>
          </label>
        </div>
      </template>
    </template>
  </div>
</template>

<script>
/**
 * @TODO hasInputError() 함수 추가
 * @TODO hasInputError() true 인 경우, 폼 UI 저장(Submit) 버튼 disable 하기.
 * @TODO
 */
import {JqlSchema} from "@/mdk/jql-schema.js";

export default {

  props: {
    fields : Array,
    entity: Object,
    readOnly: Boolean,
    labelType: String,
    placeholder: String,
    showError: {
      type: Boolean,
      default: true
    }
  },

  data() {
    return {
      errorMessages: {},
      valueSet: {},
    }
  },

  watch: {
    // 22.03.18 @yeony - Setting.vue modal 처리 위해 수정
    entity: {
      deep: true,
      immediate: true,
      handler() {
        this.valueSet = JqlSchema.toPlainValueSet(this.entity);
        console.log(this.valueSet, this.valueSet.id)
      }
    }
  },

  methods: {

    getInputSet(key) {
      if (this.valueSet[key] === undefined) {
        this.valueSet[key] = null;
      }
      return this.valueSet;
    },

    getUpdatedProperties() {
      const updated_entity = {}
      for (const p in this.valueSet) {
        const edt_v = this.valueSet[p] || '';
        const org_v = JqlSchema.getValue(this.entity, p, false) || ''
        if (edt_v != org_v) {
          JqlSchema.setValue(updated_entity, p, edt_v);
        }
      }
      return updated_entity;
    },

    setInputValuesEmpty() {
      this.valueSet = {};
    },

    toArray(row) {
      if (Array.isArray(row)) {
        return row;
      }
      else {
        return [row];
      }
    },

    hasLeftLabel() {
      return this.labelType != 'none' && this.labelType != 'float';
    },

    hasRightLabel() {
      return this.labelType == 'float';
    },

    calcColumnWidth(index, columns, max_width) {
      if (!Array.isArray(columns)) {
        return max_width;
      }

      const column = columns[index];
      if (column == null) return;

      let col_length = columns.length;
      let col_span = max_width / col_length;
      if (col_span < 1) {
        col_span = 1;
      }

      let col_width = col_span;
      for (let c = index + 1; c < col_length; c++) {
        if (columns[c] != null) {
          return parseInt(col_width + 0.1);
        }
        col_width += col_span;
      }
      col_width = max_width - index * col_span;
      return parseInt(col_width + 0.1);
    },

    calcColumnSpan(col_index, columns) {
      if (!Array.isArray(columns)) {
        return 24-1;
      }
      let max_width = 24;
      for (const c of columns) {
        if (c != null) max_width --;
      }
      let colSpan = this.calcColumnWidth(col_index, columns, max_width)
      return colSpan;
    },

    getColumnClass(col_index, row) {
      let cls = 'mdk-input-area ';
      if (this.labelType=='float') {
        cls += 'form-floating '
      }
      cls += 'col-sm-' + this.calcColumnWidth(col_index, row, 12)
      return cls;
    },

    onFieldChanged(field, value) {
      const vm = this;
      const errMsg = field.validate? field.validate(value, this.valueSet) : null
      if (errMsg != null) {
        this.errorMessages = {...this.errorMessages, [field.key]: errMsg};
      }
      else {
        delete this.errorMessages[field.key];
        this.errorMessages = {...this.errorMessages}
      }
      vm.valueSet[field.key] = value;
      vm.$emit('change', this.valueSet, this.errorMessages, field);
    },

    getInputField(fieldName) {
      return this.$refs[fieldName][0];
    },

    onFocused(newField) {
      const vm = this;
      console.log('field-focus', newField)
      vm.$emit('field-focus', newField);
    }
  },
}
</script>

<style lang="scss">
//@import "@/scss/vue.scss";
div.mdk-form {
  display: flex;
  flex-wrap: wrap;
}

table.mdk-form {
  border-width: 1px;
  border: none;
  width: 100%;
  colgroup.col24 col {
    width: 1000px;
  }

  .mdk-input-control[disabled] {
    border: none;
    background: transparent;
    //color: $input-color;
  }

   td {
    border: 1px solid #bebcd2;
     //color: $input-color;
     &.mdk-input-label {
       //background-color: $tertiary;
       //color: $input-color;
       width: 10%;
     }
  }
}

.mdk-form {
  div.input-error-message {
    font-size: 10px;
    margin-left: 0.2rem;
    color: orangered;
    height: 1em;
  }

  .mdk-input-area > .mdk-input-label {
    margin-top: 0.5rem;
  }

  .mdk-input-label {
    text-align: center;
    color: black;
  }
  &.left {
    .mdk-input-control {
      margin-right: 20ex;
    }
    .mdk-input-area {
      display: grid;
      grid-template-columns: 9em 1fr;
      margin: 10px 0;
    }
  }

  .form-sub-title {
    border: none;
    padding: 0.5ex;
    padding-top: 2ex;
    font-weight: bold;
    font-size: larger;
  }
}

[disabled] .mdk-input {
  display: grid;
  grid-template-columns: 7em 1fr;
  margin: 10px 0;
}


</style>