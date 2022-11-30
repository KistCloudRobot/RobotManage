<template>
  <div>
    <flat-pickr v-if="!disabled && field.type == 'datetime' && !isDropdown(field)"
                :name="field.key"
                class="form-control mdk-input-control"
                v-model="inputValue"
                :config="field.datePickerConfig"
                :disabled="!enabled"
                :placeholder="getPlaceholder(field)"
                @focus="onFocused"
                @on-close="updateValueImmediately(field)"
                @on-change="updateValueImmediately(field)"
    />

    <!--  :value="value" @input="$emit('input', $event.target.value)"-->
    <b-form-checkbox v-else-if="field.type == 'checkbox'"
                     :name="field.key"
                     class="form-control mdk-input-control"
                     v-model="inputValue"
                     :disabled="!enabled"
                     :type="field.type"
                     :placeholder="getPlaceholder(field)"
                     @focus="onFocused"
                     @input="updateValueImmediately(field)"
    />

    <b-form-input v-else-if="!isDropdown(field)"
                  autocomplete="off"
                  class="form-control mdk-input-control"
                  :name="field.key"
                  v-model="inputValue"
                  :disabled="!enabled"
                  :type="field.type"
                  :placeholder="getPlaceholder(field)"
                  @focus="onFocused"
                  @blur="updateValueImmediately(field)"
                  @input="updateValueOnKeyup(field)"
                  @keydown.enter="updateValueImmediately(field)"
    />

    <!-- options are a list of primitives -->
    <b-form-select v-else-if="field.type == 'dropdown-array'"
                   :name="field.key"
                   class="form-control form-select mdk-input-control"
                   v-model="inputValue"
                   :disabled="!enabled"
                   @focus="onFocused"
                   @input="updateValueImmediately(field)">
      <b-form-select-option value="" key="-1">
        {{getPlaceholder(field)}}
      </b-form-select-option>
      <b-form-select-option
          v-for="(option, i) in field.dropdownOptions"
          :key="i"
          :value="i">
        {{ option }}
      </b-form-select-option>
    </b-form-select>

    <!-- options are a list of objects with text and value -->
    <b-form-select v-else
                   :name="field.key"
                   class="form-control form-select mdk-input-control"
                   v-model="inputValue"
                   :disabled="!enabled"
                   :options="field.dropdownOptions"
                   @focus="onFocused"
                   @input="updateValueImmediately(field)">
      <template #first>
        <b-form-select-option value="">{{getPlaceholder(field)}}</b-form-select-option>
      </template>
    </b-form-select>
  </div>
</template>

<script>

export default {
  props: {
    field: Object,
    value: String,
    placeholder: String,
    disabled: Boolean,
  },

  // 22.02.18 @yeony - checkbox v-model이 Boolean type이라 error 발생.
  // props의 type 지정하지 않는 방식으로 변경
  // props : ['field', 'value', 'placeholder', 'disabled', 'show-error'],

  data() {
    return {
      inputValue : this.value || '',
      enabled: !this.disabled,
    }
  },

  watch: {
    value: {
      immediate: true,
      handler() {
        this.inputValue = this.value || ''
        console.log('inputValue--------------------------------------', this.inputValue)
      }
    },

    disabled() {
      this.enabled = !this.disabled
    }
  },

  methods: {

    getInitialValue(field, value) {
      if (value == null) {
        value = this.isDropdown(field) ? -1 : "";
      }
      return value;
    },

    setDisabled(disabled) {
      this.enabled = !disabled;
    },

    isDropdown(field) {
      return field.dropdownOptions != null
    },

    getPlaceholder(field) {
      if (!this.enabled) return this.$MDKOptions.readonlyInputPlaceholder;
      if (this.placeholder) return this.placeholder;
      const placeholder = (field.placeholder) || field.label;
      return placeholder;
    },

    updateValueOnEnter(field) {
      let value = this.inputValue;
      console.log('updateValueOnEnter', value);
      if (this.timer) clearTimeout(this.timer);
      this.updateValueImmediately(field, value);
    },

    updateValueOnKeyup(field) {
      if (this.timer) clearTimeout(this.timer);
      this.timer = setTimeout(() => {
        this.updateValueImmediately(field);
      }, 800);
    },

    updateValueImmediately(field) {
      if (this.timer) clearTimeout(this.timer);
      let value = this.inputValue;
      console.log('Field changed', field.key, value);
      this.$emit('input', field, value);
    },

    onFocused() {
      this.$emit("focus");
    }

  },
}

</script>

