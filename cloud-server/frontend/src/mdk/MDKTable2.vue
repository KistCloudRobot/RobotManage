<template>
  <div>
      <VueBTable
          :sticky-header="stickyHeader"
          :responsive="true"
          id="mdk-table"
          @sort-changed="onSortChanged"
          v-on="$attrs"
          :no-local-sorting="!localSearchEnabled"
          :line-numbers="true"
          :fields="fields"
          :items="filteredRows"
          :per-page="perPages"
          :no-sort-reset="true"
          :selectable="selectable"
          select-mode="single"
          @row-selected="onRowSelected"
          @row-dblclicked="onRowDBLClick"
          show-empty
      >

        <template #head()="props">
          <div :id="'mdk-field-'+props.field.key">{{props.label}}</div>
        </template>

        <template v-if="!hideFilterRow" v-slot:top-row slot-scope="{ fields }">
          <td v-for="field in fields" :key="field.key">
<!--            @TODO yeony - clearSearchParams 시 input 내 내용 사라지지 않음.-->
<!--            {{searchParams[field.key] ? '있어' : 'ㄴㄴ'}}-->
            <MDKInput :field="field"
                      :value="searchParams[field.key]"
                      :placeholder="this.$MDKOptions.rowFilterPlaceholder"
                      @input="onFilterChanged"
            />
          </td>
        </template>

        <!-- 모든 Slot 을 자동으로 Bypass 하기 -->
        <template v-for="(_, slot) in $slots" v-slot:[slot]="props">
          <slot :name="slot" v-bind="props" />
        </template>

      </VueBTable>
    <br/>
      <VueBPagination v-if="pagination"
                    v-model="currentPage"
                    :total-rows="totalCount"
                    :per-page="perPages"
                    size="sm" align="center"
                    @update:model-value="onPageChanged"></VueBPagination>
  </div>
</template>

<script>
import {JqlSchema} from "@/mdk/jql-schema.js";
import VueBTable from "@/mdk/BootstrapVue3/components/BTable/BTable.vue";
import VueBPagination from "@/mdk/BootstrapVue3/components/BPagination/BPagination.vue";

export default {
  name: "MDKTable",
  components: {VueBTable, VueBPagination},

  props: {
    noLocalSearch : Boolean,
    hideFilterRow : Boolean,
    fields : Array,
    items: Array,
    pagination: Object,
    search: Function,
    selectable: {
      default: true,
      type: Boolean
    },
    stickyHeader: {
      default: false,
      type: Boolean
    }
  },

  data() {
    return {
      filteredRows: this.items,
      currentPage: this.pagination?.currentPage,
      perPages: this.pagination?.perPage,
      searchParams: {},
      sortOptions: [],
      localSearchEnabled: false,
      beforeSorted: '',
    }
  },

  watch: {
    items: {
      deep: true,
      immediate: false,
      handler() {
        const vm = this;
        vm.$nextTick(() => {
          vm.filteredRows = vm.items;
          vm.initializePagination();
        });
      }
    },
  },

  computed: {
    // @yeony - bootstrapVue 공식문서에서 totalCount를 computed를 통해 사용함
    totalCount() {
      return this.pagination.totalCount || this.filteredRows.length;
    },
  },

  methods: {

    initializePagination() {
      const vm = this;
      if (vm.noLocalSearch) {
        this.localSearchEnabled = false;
        return;
      }

      for (const col of vm.fields) {
        const v = vm.searchParams[{...col}.key];
        if (v) {
          vm.localSearchEnabled = false;
          console.log('not empty searchParams', col.key, vm.searchParams)
          return;
        }
      }

      // vm.localSearchEnabled = !vm.pagination?.totalCount
      //     || this.items.length >= this.pagination.totalCount;
      vm.localSearchEnabled = false
    },

    onSortChanged(data) {
      const vm = this;

      if(vm.beforeSorted.key == data.sortBy) {
        console.log('1', data)
        data.sortDesc = vm.beforeSorted.sort == '+'? true : false;
        console.log('2',data)
      } else if(!vm.beforeSorted.key) {
        console.log('3',data)
      }

      console.log(vm.sortOptions, vm.sortOptions.length)

      if (vm.sortOptions.length > 0 && vm.sortOptions[0].substring(1) == data.sortBy) {
        vm.sortOptions.splice(0, 1);
      }

      const sortBy = (data.sortDesc ? '-' : '+') + data.sortBy;
      vm.sortOptions.splice(0, 0, sortBy);
      if(vm.sortOptions.length>3) {
        vm.sortOptions.pop();
      }


      // aria-sort 바꾸기
      // @TODO - yeony global에 unique id 생성 함수 만들기 (id 중복 방지)
      // sortOptions는 2개까지 적용
      const firstSort = vm.sortOptions[0]
      const secondSort = vm.sortOptions[1]

      // sortOption이 적용된 fields
      const sortedField1 = document.getElementById('mdk-field-'+data.sortBy)
      const sortedField2 = document.getElementById('mdk-field-'+secondSort?.slice(1))

      console.log(firstSort, sortedField1)

      if(firstSort.charAt(0) == '+') {
        sortedField1.setAttribute("aria-sort", 'ascending')
      } else if(firstSort.charAt(0) == '-') {
        sortedField1.setAttribute("aria-sort", 'descending')
      }

      // secondSort가 있을 때
      if(secondSort) {
        setTimeout(() => {
          vm.changeSecondColumns(secondSort, sortedField2)
        }, 100)

      }


      vm.beforeSorted = {key: data.sortBy, sort: firstSort.charAt(0)}
      vm.reloadItems(false);
    },

    // @yeony 22.07.18
    // EV 서비스 관리 (Manage_fee) 작업 중, local sorting이 제대로 되지 않아 forEach 구문 각각 추가
    // 이슈 : sorting이 3개 이상 표시됨

    changeSecondColumns(secondSort, sortedField2) {
      const vm = this;
      if(secondSort.charAt(0) == '+') {
        sortedField2.setAttribute("aria-sort", 'ascending2')
        vm.sortOptions.forEach((s, i) => {
          if(i >1 && !vm.sortOptions[0].includes(s.slice(1))) {
            let sortedNone = document.getElementById('mdk-field-'+s?.slice(1))
            sortedNone.setAttribute("aria-sort", 'none')
          }
        })
      } else if(secondSort.charAt(0) == '-') {
        sortedField2.setAttribute("aria-sort", 'descending2')
        vm.sortOptions.forEach((s, i) => {
          if(i >1 && !vm.sortOptions[0].includes(s.slice(1))) {
            console.log(s)
            let sortedNone = document.getElementById('mdk-field-'+s?.slice(1))
            sortedNone.setAttribute("aria-sort", 'none')
          }
        })
      }
    },

    onFilterChanged(field, value) {
      const vm = this;
      this.searchParams[field.key] = value;
      delete this.searchParams['undefined']
      console.log('onFilterChanged', {...this.searchParams})
      vm.reloadItems(true);
    },

    clearSearchParams() {
      const vm = this;
      vm.searchParams = {};
      vm.reloadItems(true);
    },

    reloadItems(filterChanged) {
      const vm = this;
      if (filterChanged) {
        vm.currentPage = 1;
      }

      if (!vm.localSearchEnabled) {
        console.log('remote-search!!!', vm.searchParams, vm.sortOptions)
        vm.$emit("filter-change", JqlSchema.encodePlainValueSet({...vm.searchParams}), vm.sortOptions)
      }
      else if (filterChanged) {
        console.log('local-search!!!', vm.searchParams, vm.sortOptions)
        vm.filteredRows = JqlSchema.filterEntitiesByExample(vm.items, vm.fields, vm.searchParams)
        // @yeony 22.02.07 - local search 시 totalCount가 화면에 반영되지 않아 $parent로 접근함.
        console.log(vm.$parent)
        vm.$parent.paginationOptions.totalCount = vm.filteredRows.length;
      }
    },

    onPageChanged(currentPage) {
      console.log('페이지!!!!',currentPage)
      this.currentPage = currentPage;
      this.reloadItems(false)
    },

    onRowSelected(row) {
      this.$emit("row-selected", row)
    },

    onRowDBLClick(row) {
      console.log('bst row', row)
      this.$emit("row-dblclicked", row)
    }
  },

  mounted() {
    const vm = this;
    for (const col of vm.fields) {
      if (col == null) {
        console.log("Error! wrong column fields. see belows");
        let count = 0;
        for (const c of vm.fields) {
          console.log("col " + count, c?.key, c)
          count ++;
        }
        break;
      }
    }
    vm.initializePagination();
    console.log('$appName', vm.$MDKOptions);
  }
}
</script>

<style lang="scss" scoped>
::v-deep {
  #mdk-table > table > thead > tr > th {
    div {
      text-align: center;
      color: #000;
    }
  }

  .b-table-sticky-header {
    //overflow-y: auto; // vue-custom-scrollbar 사용하기.
    > table#mdk-table > thead > tr > th {
      position: sticky;
      top: 0;
      z-index: 2;
    }
  }

  #mdk-table > table > thead > tr > [aria-sort]:not(.b-table-sort-icon-left), .table.b-table > tfoot > tr > [aria-sort]:not(.b-table-sort-icon-left) {
    background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' width='101' height='101' view-box='0 0 101 101' preserveAspectRatio='none'%3e%3cpath fill='black' opacity='.3' d='M51 1l25 23 24 22H1l25-22zM51 101l25-23 24-22H1l25 22z'/%3e%3c/svg%3e");
    background-position: right calc(0.75rem / 2) center;
    padding-right: calc(0.75rem + 0.65em);
  }

  #mdk-table > table > thead > tr > [aria-sort], .table.b-table > tfoot > tr > [aria-sort] {
    cursor: pointer;
    background-image: none;
    background-repeat: no-repeat;
    background-size: 0.65em 1em;
  }

  #mdk-table > table > thead > tr > [aria-sort=ascending], .table.b-table > tfoot > tr > [aria-sort=ascending] {
    background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' width='101' height='101' view-box='0 0 101 101' preserveAspectRatio='none'%3e%3cpath fill='black' d='M51 1l25 23 24 22H1l25-22z'/%3e%3cpath fill='black' opacity='.3' d='M51 101l25-23 24-22H1l25 22z'/%3e%3c/svg%3e") !important;
  }

  #mdk-table > table > thead >  tr > [aria-sort=ascending2], .table.b-table > tfoot > tr > [aria-sort=ascending2] {
    background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' width='101' height='101' view-box='0 0 101 101' preserveAspectRatio='none'%3e%3cpath fill='gray' d='M51 1l25 23 24 22H1l25-22z'/%3e%3cpath fill='gray' opacity='.3' d='M51 101l25-23 24-22H1l25 22z'/%3e%3c/svg%3e") !important;
  }

  #mdk-table > table > thead > tr > [aria-sort=descending], .table.b-table > tfoot > tr > [aria-sort=descending] {
    background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' width='101' height='101' view-box='0 0 101 101' preserveAspectRatio='none'%3e%3cpath fill='black' opacity='.3' d='M51 1l25 23 24 22H1l25-22z'/%3e%3cpath fill='black' d='M51 101l25-23 24-22H1l25 22z'/%3e%3c/svg%3e") !important;
  }

  #mdk-table > table > thead > tr > [aria-sort=descending2], .table.b-table > tfoot > tr > [aria-sort=descending2] {
    background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' width='101' height='101' view-box='0 0 101 101' preserveAspectRatio='none'%3e%3cpath fill='gray' opacity='.3' d='M51 1l25 23 24 22H1l25-22z'/%3e%3cpath fill='gray' d='M51 101l25-23 24-22H1l25 22z'/%3e%3c/svg%3e") !important;
  }

  div#mdk-table {
    --bs-table-accent-bg: transparent;
    background-color: #fafafc;
    tbody {
      tr > td {
        border-bottom: 1px solid #eee;
        color: #4f5861;
      }
    }
  }

  //.page-item .page-link {
  //  color: #000000;
  //  background-color: #fff;
  //}

  .table-active.b-table-row-selected {
    --bs-table-accent-bg: #eaeaea;
  }

  div#mdk-table.table-dark {
    tbody {
      tr.b-table-row-selected.bg-active {
        --bs-table-accent-bg: #4f5861
      }

    }
  }



}

</style>