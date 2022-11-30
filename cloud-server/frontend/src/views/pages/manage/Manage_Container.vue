<template>
  <Layout>
    <b-card>
      <MDKTable
          :fields="fields"
          :items="items"
          :hide-filter-row="false"
          :pagination="{
            currentPage: 1,
            totalCount : 0,
            perPage: 5,
          }"
          :selectable="true"
          @row-selected="onRowSelected"
      >

      </MDKTable>
      <br/>
      <MDKForm
          :fields="formFields"
          :entity="selectedItem"
          :label-type="'table'"
          :read-only="true"
      >

      </MDKForm>
    </b-card>

  </Layout>

</template>

<script>
import Layout from "../../layouts/main";

export default {
  name: "Manage_File",
  components: {Layout},
  data() {
    const _api = this.$_app.service.container;
    return {
      api : _api,
      fields: [
          _api.schema.id,
          _api.schema.name,
          _api.schema.type,
          _api.schema.version,
          _api.schema.resister,
          _api.schema.created,
      ],
      formFields: [
          [_api.schema.name, {..._api.schema.type, label: '컨테이너 유형'}],
          [_api.schema.status, _api.schema.description],
          [_api.schema.resister, _api.schema.created],
          [_api.schema.downloadCnt, _api.schema.lastDownload],
          [_api.schema.link, _api.schema.version],
      ],
      items : [
        { id: 'CT009', name: '로봇작업-AAA-01:22', type: 'Cloudlet Agent', version: 1, resister: 'User1', created: '2022-03-29 12:20', description: '로봇 시뮬레이터 샘플01', lastDownload: '2022-08-20 12:30',  downloadCnt: 3, link: 'http://aaa.ccdd/123/asd', status: '완료', },
        { id: 'CT008', name: '로봇작업-BBA-01:22', type: 'Cloudlet Agent', version: 1, resister: 'User2', created: '2022-03-28 12:20' },
        { id: 'CT007', name: '로봇작업-CCA-01:22', type: 'Cloud Agent', version: 2, resister: 'User1', created: '2022-03-25 12:20' },
        { id: 'CT006', name: '로봇작업-BBC-01:22', type: 'Cloudlet Agent', version: 1, resister: 'User2', created: '2022-03-12 12:20' },
        { id: 'CT005', name: '로봇작업-ABC-01:22', type: 'Cloud Agent', version: 3, resister: 'User1', created: '2022-03-08 12:20' },
        { id: 'CT004', name: '로봇작업-ACA-01:22', type: 'Robot Agent', version: 4, resister: 'User2', created: '2022-03-07 12:20' },
        { id: 'CT003', name: '로봇작업-ABA-01:22', type: 'Cloudlet Agent', version: 2, resister: 'User2', created: '2022-03-05 12:20' },
        { id: 'CT002', name: '로봇작업-CBA-01:22', type: 'Cloudlet Agent', version: 1, resister: 'User1', created: '2022-03-01 12:20' },
      ],
      selectedItem: {},
    }
  },
  methods: {
    onRowSelected(row) {
      this.selectedItem = row;
      console.log(row)
      console.log(document.getElementsByClassName("b-tr"))
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

    onSortChanged(data) {
      /*
      data : {
        apiUrl: undefined
        currentPage: 1
        filter: undefined
        perPage: 0
        sortBy: "name"
        sortDesc: false
      }
       */


    }
  },
  mounted() {
    console.log(this.$_app)
  }
}
</script>

<style scoped lang="scss">
::v-deep {
  tr[data-selected="true"] {
    background-color: #e2e2e2;
  }
  .mdk-input-label {
    background-color: #efefef;
  }
}
</style>