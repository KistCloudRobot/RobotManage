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
          @filter-change="getRobotInfoList"
      >
        <template #[`cell(orgDispatcher.dispatcherId)`]="props">
<!--          {{props}}-->
          {{props.item.orgDispatcher? props.item.orgDispatcher.dispatcherId : '-'}}
        </template>
        <template #cell(log)="props">
          <div class="w-100 d-flex justify-content-center">
            <b-button size="sm" style="margin: auto 0" @click="openLogModal(props.item)" >상세보기</b-button>
          </div>
        </template>
      </MDKTable>
      <b-button variant="warning" class="float-end" :disabled="selectedItem.robotId ? false : true"
                @click="openUpdateModal">수정</b-button>
    </BCard>
    <b-modal v-model="modalShow" hide-header-close ok-title="저장" cancel-title="취소" @ok="onSave">

      <MDKForm
          :fields="formFields"
          :entity="{}"
          @change="onFieldChanged"
      >

      </MDKForm>
    </b-modal>
    <b-modal v-model="updateModalShow" hide-header-close ok-title="수정" cancel-title="취소" @ok="onUpdate">
      <MDKForm
          :fields="updateFormFields"
          :entity="{...selectedItem}"
          @change="onUpdateFieldChanged"
      >

      </MDKForm>
    </b-modal>
    <b-modal v-model="logModalShow" ok-title="취소" hide-header-close ok-only ok-variant="secondary" :title="'로봇Id: '+selectedItem.robotId+' (로봇 명: '+selectedItem.robotName+')'">
      <div style="max-height: 60vh; overflow-y: auto">
        <MDKTable
            :hide-filter-row="true"
            :sticky-header="true"
            :fields="logFields"
            :items="logItems"
            ref="MDKTable2"
            @filter-change="getRobotStatus"
            :pagination="paginationOptions2"
        >

        </MDKTable>
      </div>
    </b-modal>
  </Layout>
</template>

<script>
import Layout from "../../layouts/main";
import MDKTable from "@/mdk/MDKTable2";
export default {
  name: "Manage_identification",
  components: {MDKTable, Layout},
  data() {
    const _api = this.$_app.service.robot;
    const _sApi = this.$_app.service.robotStatus;
    return {
      api: _api,
      sApi: _sApi,
      fields: [
        _api.schema.robotId,
        _api.schema.robotName,
        _api.schema.robotType,
        _api.schema.robotPlace,
        _api.schema.regUser,
        _api.schema.regDate,
        _api.schema['orgDispatcher.dispatcherId'],
        _api.schema.log,
      ],
      formFields: [
          _api.schema.robotId,
          _api.schema.robotName,
          _api.schema.robotType,
          _api.schema.robotPlace,
          _api.schema.orgDispatcher.dispatcherId,
      ],
      updateFormFields: [
        _api.schema.robotName,
        _api.schema.robotType,
        _api.schema.robotPlace,
        _api.schema.orgDispatcher.dispatcherId,
      ],
      items: [],
      paginationOptions: {
        currentPage: 1,
        totalCount : 0,
        perPage: 10,
      },
      paginationOptions2: {
        currentPage: 1,
        totalCount : 0,
        perPage: 5,
      },
      selectedItem: {},
      formObj: {},
      modalShow: false,
      logFields: _sApi.columnInfos,
      logItems: [],
      logModalShow: false,
      updateModalShow: false,
      updateFormObj: {},
      robotId: '',
    }
  },
  methods: {
    getRobotInfoList(filterOptions = {orgDispatcher: {}}, sortOptions = ['+robotId']) {
      const vm = this;

      vm.api.searchByExample(filterOptions, sortOptions, vm.paginationOptions.perPage, vm.$refs.MDKTable.currentPage)
      .then(res => {
        vm.items = res.content;
        vm.paginationOptions.totalCount = res.totalElements;
        console.log(res)
      })
    },

    openLogModal(robotItem) {
      const vm = this;

      vm.logModalShow = true;
      vm.robotId = robotItem.robotId;

      vm.getRobotStatus()
    },

    getRobotStatus(jql = {}, sort = ['-updateTime']) {
      const vm = this;

      jql = {'robotId' : vm.robotId}
      sort = [...sort, '-updateTime']

      vm.sApi.searchByExample(jql, sort, vm.paginationOptions2.perPage, vm.$refs.MDKTable2.currentPage)
          .then(res => {
            vm.logItems = res.content;
            vm.paginationOptions2.totalCount = res.totalElements;
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

    openUpdateModal() {
      const vm = this;

      vm.updateModalShow = true;
    },

    onFieldChanged(valueSet, err, field) {
      this.formObj = Object.assign(this.formObj, valueSet);
    },

    onUpdateFieldChanged(valueSet, err, field) {
      console.log(valueSet)
      this.updateFormObj = Object.assign(this.formObj, valueSet);
    },

    onUpdate() {
      const vm = this;

      console.log(vm.updateFormObj)
      vm.updateFormObj = {...vm.updateFormObj, orgDispatcher: { dispatcherId: vm.updateFormObj['orgDispatcher.dispatcherId']}}
      vm.api.updateByIdList(vm.updateFormObj.robotId, vm.updateFormObj)
      .then(res => {
        alert('성공적으로 수정되었습니다.')
        vm.getRobotInfoList();
        console.log(res)
      })
    },

    onSave() {
      const vm = this;

      vm.formObj.regUser = vm.$_app.user.email;

      // vm.formObj.orgDispatcher = {dispatcherId: vm.formObj['orgDispatcher.dispatcherId']};

      vm.formObj = {...vm.formObj, orgDispatcher: { dispatcherId: vm.formObj['orgDispatcher.dispatcherId']}}

      console.log(vm.formObj)

      vm.api.add(vm.formObj)
      .then(res => {
        console.log(res)
        vm.getRobotInfoList();
        // vm.$swal('등록 완료', '성공적으로 등록되었습니다.', 'success')
      })
      .catch(err => {
        console.log(err)
      })

    },

    onClearFilter() {
      const vm = this;
      vm.$refs.MDKTable.clearSearchParams();
    },
  },
  mounted() {
    const vm = this;
    this.$_app.service.dispatcher.searchByExample(null, null)
        .then(res => {
          console.log(res)
          vm.fields[6].dropdownOptions = {};
          vm.formFields[4].dropdownOptions = {};
          vm.updateFormFields[3].dropdownOptions = {};
          res.forEach(d => {
            vm.fields[6].dropdownOptions[d.dispatcherId] = d.dispatcherId;
            vm.formFields[4].dropdownOptions[d.dispatcherId] = d.dispatcherId;
            vm.updateFormFields[3].dropdownOptions[d.dispatcherId] = d.dispatcherId
          })
        })

    vm.getRobotInfoList();

  }
}
</script>

<style scoped>

</style>