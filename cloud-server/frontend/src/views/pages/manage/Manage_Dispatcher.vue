<template>
  <Layout>
    <BCard>
      <div class="d-flex justify-content-between">
        <div>
          <b-button size="sm" class="text-black" variant="outline-dark" disabled>검색 결과: {{ paginationOptions.totalCount }}</b-button>
        </div>
        <div>
          <b-button size="sm" class="me-3" variant="secondary" @click="onClearFilter">조회 초기화</b-button>
          <b-button size="sm" variant="primary" @click="modalShow = true">신규 등록</b-button>
        </div>
      </div>
      <br/>
      <MDKTable
          :fields="fields"
          :items="items"
          :pagination="paginationOptions"
          ref="MDKTable"
          @row-selected="onRowSelected"
          @filter-change="getDispatherList"
      >
      </MDKTable>
      <b-button variant="warning" class="float-end" :disabled="selectedItem.dispatcherId ? false : true"
                @click="openUpdateModal">수정</b-button>
    </BCard>
    <b-modal v-model="modalShow" hide-header-close ok-title="저장" cancel-title="취소" @ok="onSave">
      <!--      @change="onFieldChanged"-->
      <MDKForm
          ref="MDKForm1"
          :fields="formFields"
          :entity="{}"
          @change="onFieldChanged"
      >

      </MDKForm>
    </b-modal>
    <b-modal v-model="updateModalShow" hide-header-close ok-title="수정" cancel-title="취소" @ok="onUpdate">
      <MDKForm
          ref="MDKForm2"
          :fields="[formFields[1], formFields[2]]"
          :entity="selectedItem"
      >
        <template v-slot:inputs="props">
<!--          {{props.column}}-->
          <MDKInput
              v-if="props.column.key == 'dispatcherName'"
              :field="props.column"
              :value="dispatcherName"
              @input="onUpdateFieldChanged"
          />
          <MDKInput
              v-if="props.column.key == 'description'"
              :field="props.column"
              :value="description"
              @input="onUpdateDescriptionChanged"
          />
        </template>

      </MDKForm>
    </b-modal>
  </Layout>
</template>

<script>
import Layout from "../../layouts/main";
export default {
  name: "Manage_Dispatcher",
  components: {Layout},
  data() {
    const _api = this.$_app.service.dispatcher;
    return {
      api: _api,
      fields: [
        _api.schema.dispatcherId,
        _api.schema.dispatcherName,
        _api.schema.description,
        _api.schema.regUser,
        _api.schema.regDate,
      ],
      items: [],
      paginationOptions: {
        currentPage: 1,
        totalCount : 0,
        perPage: 10,
      },
      selectedItem: {},
      formFields: [
        _api.schema.dispatcherId,
        _api.schema.dispatcherName,
        _api.schema.description,
      ],
      modalShow: false,
      updateModalShow: false,
      dispatcherName: '',
      description: '',
      updatedDescription: '',
      updatedName: '',
      formObj: {},
    }
  },

  methods: {
    getDispatherList(filter, sortOptions = ['+dispatcherId']) {
      const vm = this;

      vm.api.searchByExample(filter, sortOptions, vm.paginationOptions.perPage, vm.$refs.MDKTable.currentPage)
      .then(res => {
        console.log(res)
        vm.items = res.content;
        vm.paginationOptions.totalCount = res.totalElements;

      })
    },
    onRowSelected(row) {
      this.selectedItem = row;

      const trArr = [...document.getElementsByClassName("b-tr")]
      trArr.forEach(tr => {
        if(tr.ariaSelected == row.trIndex) {
          const selected = tr;
          selected.setAttribute('data-selected', true)
        } else if(tr.ariaSelected !== row.trIndex) {
          tr.setAttribute('data-selected', false)
        }
      })
    },

    onFieldChanged(valueSet, err, field) {
      this.formObj = Object.assign(this.formObj, valueSet);
    },
    onUpdateFieldChanged(field, value) {
      console.log(field, value)
      this.updatedName = value;
    },
    onUpdateDescriptionChanged(field, value) {
      console.log(field, value)
      this.updatedDescription = value;
    },

    onSave() {
      const vm = this;

      vm.formObj.regUser = vm.$_app.user.email;
      console.log(vm.formObj)

      vm.api.add(vm.formObj)
      .then(res => {
        console.log(res)
        vm.getDispatherList();
      })
    },

    openUpdateModal() {
      const vm = this

      this.updateModalShow = true;
      vm.dispatcherName = vm.selectedItem.dispatcherName;
      vm.description = vm.selectedItem.description;
    },

    onUpdate() {
      const vm = this;

      vm.api.updateByIdList(vm.selectedItem.dispatcherId, {
        dispatcherName: vm.updatedName ?? vm.dispatcherName,
        description: vm.updatedDescription ?? vm.description
      })
      .then(res => {
        alert('성공적으로 수정되었습니다.')
        console.log('수정됨!', res)
        vm.getDispatherList();
      })


    },

    onClearFilter() {
      const vm = this;
      vm.$refs.MDKTable.clearSearchParams();
    },
  },
  mounted() {
    this.getDispatherList();
  }
}
</script>

<style scoped>

</style>