import {AriaInvalid, ButtonVariant, InputSize} from '../types'
import {computed, ComputedRef} from 'vue'
import {resolveAriaInvalid} from '../utils'

/**
 * @param items must be a reactive object ex: reactive({ plain: toRef(plainBoolean, 'value')})
 * @returns
 */
const _getComputedAriaInvalid = (items: {
  ariaInvalid?: AriaInvalid
  state?: boolean
}): ComputedRef => computed(() => resolveAriaInvalid(items.ariaInvalid, items.state))

/**
 * @param items must be a reactive object ex: reactive({ plain: toRef(plainBoolean, 'value')})
 * @returns
 */
const getClasses = (items: {
  plain?: boolean
  button?: boolean
  inline?: boolean
  switch?: boolean
  size?: InputSize
}): ComputedRef =>
  computed(() => ({
    'form-check': !items.plain && !items.button,
    'form-check-inline': items.inline,
    'form-switch': items.switch,
    [`form-control-${items.size}`]: items.size && items.size !== 'md',
  }))

/**
 * @param items must be a reactive object ex: reactive({ plain: toRef(plainBoolean, 'value')})
 * @returns
 */
const getInputClasses = (items: {
  plain?: boolean
  button?: boolean
  state?: boolean
}): ComputedRef =>
  computed(() => ({
    'form-check-input': !items.plain && !items.button,
    'is-valid': items.state === true,
    'is-invalid': items.state === false,
    'btn-check': items.button,
  }))

/**
 * @param items must be a reactive object ex: reactive({ plain: toRef(plainBoolean, 'value')})
 * @returns
 */
const getLabelClasses = (items: {
  plain?: boolean
  button?: boolean
  buttonVariant?: ButtonVariant
  size?: InputSize
}): ComputedRef =>
  computed(() => ({
    'form-check-label': !items.plain && !items.button,
    'btn': items.button,
    [`btn-${items.buttonVariant}`]: items.button,
    [`btn-${items.size}`]: items.button && items.size && items.size !== 'md',
  }))

/**
 * @param items must be a reactive object ex: reactive({ plain: toRef(plainBoolean, 'value')})
 * @returns
 */
const getGroupAttr = (items: {
  required?: boolean
  ariaInvalid?: AriaInvalid
  state?: boolean
}): ComputedRef =>
  computed(() => ({
    'aria-invalid': _getComputedAriaInvalid(items).value,
    'aria-required': items.required?.toString() === 'true' ? 'true' : null,
  }))

/**
 * @param items must be a reactive object ex: reactive({ plain: toRef(plainBoolean, 'value')})
 * @returns
 */
const getGroupClasses = (items: {
  validated?: boolean
  buttons?: boolean
  stacked?: boolean
  size?: InputSize
}): ComputedRef =>
  computed(() => ({
    'was-validated': items.validated,
    'btn-group': items.buttons && !items.stacked,
    'btn-group-vertical': items.stacked,
    [`btn-group-${items.size}`]: items.size,
  }))

/**
 * @param slots
 * @param nodeType
 * @param disabled
 * @returns
 */
const slotsToElements = (slots: Array<any>, nodeType: string, disabled: boolean) =>
  slots
    .filter((e: any) => e.type.name === nodeType)
    .map((e: any) => {
      const txtChild = (e.children.default ? e.children.default() : []).find(
        (e: any) => e.type.toString() === 'Symbol(Text)'
      )

      return {
        props: {
          disabled,
          ...e.props,
        },
        text: txtChild ? txtChild.children : '',
      }
    })

/**
 * @param option
 * @param props
 * @returns
 */
const optionToElement = (option: any, props: any): any => {
  if (typeof option === 'string') {
    return {
      props: {
        value: option,
        disabled: props.disabled,
      },
      text: option,
    }
  }

  return {
    props: {
      value: option[props.valueField],
      disabled: props.disabled || option[props.disabledField],
      ...option.props,
    },
    text: option[props.textField],
    html: option[props.htmlField],
  }
}

/**
 * @param el
 * @param idx
 * @param props
 * @param computedName
 * @param computedId
 * @returns
 */
const bindGroupProps = (
  el: any,
  idx: number,
  props: any,
  computedName: ComputedRef,
  computedId: ComputedRef
): any => ({
  ...el,
  props: {
    'button-variant': props.buttonVariant,
    'form': props.form,
    'name': computedName.value,
    'id': `${computedId.value}_option_${idx}`,
    'button': props.buttons,
    'state': props.state,
    'plain': props.plain,
    'size': props.size,
    'inline': !props.stacked,
    'required': props.required,
    ...el.props,
  },
})

export {
  getClasses,
  getInputClasses,
  getLabelClasses,
  getGroupAttr,
  getGroupClasses,
  slotsToElements,
  optionToElement,
  bindGroupProps,
}
