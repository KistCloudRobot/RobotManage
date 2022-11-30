<template>
  <Layout>
    <BCard>
      <div class="d-flex justify-content-between">
        <div>
          <b-button size="sm" class="text-black" variant="outline-dark" disabled>검색 결과: {{ paginationOptions.totalCount }}</b-button>
        </div>
        <div>
          <b-button size="sm" class="me-3" variant="secondary" @click="onClearFilter">조회 초기화</b-button>
          <b-button size="sm" variant="warning" :disabled="!selectedItem.email" @click="openModal">권한 수정</b-button>
        </div>
      </div>
      <br/>
      <MDKTable
        :fields="fields"
        :items="items"
        :pagination="paginationOptions"
        @filter-change="getUserList"
        @row-selected="onRowSelected"
        ref="MDKTable"
      >

      </MDKTable>
    </BCard>
    <b-modal v-model="modalShow" ok-title="저장" cancel-title="취소" @ok="onSave">

      <MDKForm
          :fields="fields"
          :entity="{...selectedItem}"
          :readOnly="true"
      >
        <template v-slot:inputs="props">
          <template v-if="props.column.key === 'roles'">
<!--            {{props.entity.roles}}-->
            <div class="d-flex">
<!--              {{isUser}} {{isAdmin}}-->
              <div class="form-check form-check-info mb-3 me-4">
                <input
                    class="form-check-input"
                    type="checkbox"
                    id="role-user"
                    v-model="isUser"
                />
<!--                :checked="getUserRoles(props.entity.roles, 'USER')"-->
                <label class="form-check-label" for="role-user">
                  USER
                </label>
              </div>

              <div class="form-check form-check-danger">
                <input
                    class="form-check-input"
                    type="checkbox"
                    id="role-admin"
                    v-model="isAdmin"
                />
<!--                :checked="getUserRoles(props.entity.roles, 'ADMIN')"-->
                <label class="form-check-label" for="role-admin">
                  ADMIN
                </label>
              </div>
            </div>
          </template>
        </template>
      </MDKForm>
    </b-modal>
  </Layout>

</template>

<script>
import Layout from "../../layouts/main"

export default {
  name: "User",
  components: {Layout},
  data() {
    const _api = this.$_app.service.user;
    return {
      api : _api,
      fields: _api.columnInfos,
      items: [],
      paginationOptions: {
        currentPage: 1,
        totalCount : 0,
        perPage: 10,
      },
      selectedItem: {},
      modalShow: false,
      updatedUserRole: [],
      isUser: false,
      isAdmin: false,
    }
  },
  methods: {
    getUserList(jql, sort) {
      const vm = this;

      vm.api.searchByExample(jql, sort, vm.paginationOptions.perPage, vm.$refs.MDKTable.currentPage)
      .then(res => {
        vm.items = res.content;
        vm.paginationOptions.totalCount = res.totalElements;
      })
    },

    openModal() {
      const vm = this;

      vm.modalShow = true;

      vm.isUser = vm.selectedItem.roles.includes('USER')
      vm.isAdmin = vm.selectedItem.roles.includes('ADMIN')
    },

    onSave() {
      const vm = this;

      let role = [];

      console.log(vm.selectedItem, vm.isUser, vm.isAdmin)
      if(vm.isUser) {
        role.push('USER')
      }
      if(vm.isAdmin) {
        role.push('ADMIN')
      }

      console.log(role.join(','))

      vm.api.updateByIdList(vm.selectedItem.id, {roles: role.join(',')})
      .then(res => {
        alert('성공적으로 수정되었습니다.')
        console.log(res)
        vm.getUserList()
      })
    },

    onClearFilter() {
      const vm = this;
      vm.$refs.MDKTable.clearSearchParams();
    },

    onRowSelected(row) {
      this.selectedItem = row;
      this.updatedUserRole = [];

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
  },
  mounted() {
    const vm = this;

    vm.getUserList();
  }
}
</script>

<style scoped>

</style>