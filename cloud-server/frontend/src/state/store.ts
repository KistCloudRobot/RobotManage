// store.ts
import { InjectionKey } from 'vue'
import { createStore, Store } from 'vuex'
const modules = require("./modules");
// const layout = require("./modules/layout")

// define your typings for the store state
// export interface State {
//   count: number
// }
//
// export const key: InjectionKey<Store<State>> = Symbol()

console.log(modules)
export const store = createStore({
  modules: modules.default
})

