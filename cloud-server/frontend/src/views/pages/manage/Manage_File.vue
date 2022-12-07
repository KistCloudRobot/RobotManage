<template>
  <Layout>
    <b-card>
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
          ref="MDKTable"
          :fields="fields"
          :items="items"
          :hide-filter-row="false"
          :pagination="paginationOptions"
          :selectable="true"
          @row-selected="onRowSelected"
          @filter-change="getFileList"
      >
        <template v-slot:cell(link)="props">
          <div class="d-flex justify-content-center">
            <b-button variant="info" size="sm" @click="onDownloadFile(props.item.fileId, props.item.name)">다운로드</b-button>
          </div>
        </template>

      </MDKTable>
      <br/>
    </b-card>
    <b-modal v-model="modalShow" hide-header-close ok-title="저장" cancel-title="취소" @ok="onSave">
<!--      {{newFileInfo}}-->
     <MDKForm
      :fields="inserFormFileds"
      :entity="{}"
      @change="onFieldChanged"
     >
       <template v-slot:inputs="props">
         <template v-if="props.column.key === 'file'">
           <input type="file" class="form-control" @change="previewFiles" accept=".txt"/>
         </template>
       </template>

     </MDKForm>
    </b-modal>

  </Layout>

</template>

<script>
import Layout from "../../layouts/main";
import BModal from "../../../mdk/BootstrapVue3/components/BModal.vue";

export default {
  name: "Manage_File",
  components: {Layout, BModal},
  data() {
    const _api = this.$_app.service.file;
    const _dApi = this.$_app.service.dispatcher;
    return {
      api : _api,
      dApi: _dApi,
      fields: [
        _api.schema.fileId,
        _api.schema.name,
        _api.schema.dispatcherId,
        _api.schema.description,
        _api.schema.regUser,
        _api.schema.regDate,
        _api.schema.lastDownloaded,
        _api.schema.link,
      ],
      formFields: [
          [_api.schema.name, _api.schema.description],
          [_api.schema.regDate, _api.schema.regUser],
          [_api.schema.link, _api.schema.lastDownloaded],
      ],
      inserFormFileds: [
          _api.schema.file,
          _api.schema.dispatcherId,
          _api.schema.description,
      ],
      items : [],
      modalShow: false,
      paginationOptions: {
        currentPage: 1,
        totalCount : 0,
        perPage: 10,
      },
      selectedItem: {},
      newFileInfo: {},
      file: '',
      searchParams: {},
    }
  },
  methods: {
    onSave() {
      const vm = this;
      console.log(vm.newFileInfo, vm.file)

      let formData = new FormData();

      for(let key in vm.newFileInfo) {
        formData.append(key, vm.newFileInfo[key]);
      }

      formData.append('regUser', vm.$_app.user.email);
      formData.append('file', vm.file);

      /**
       * 하나의 객체로 넘기기?
       * for @may 22.09.01
       * 문제) JQLController 단에서는 RequestBody로 데이터를 받아 content-type 'multipart/form-data'로 넘길 수 없음.
       * 방안1) jql (json)으로 만들어 넘기지 않고 FormData 객체로 만들어 넘겨야 함. -- cf.아래 formData 형식 있어용
       **/


      /**
       * type : work
       * version : 1
       * description : test
       * isTrusted : true // 무시해도 됨 (사용자 액션(클릭, 타이핑 등..)에 따라 Event가 넘어오는 바람에 생기는 isTrusted)
       * file: File {name: 'test.txt', lastModified: 1662001826519, lastModifiedDate: Thu Sep 01 2022 12:10:26 GMT+0900 (한국 표준시), webkitRelativePath: '', size: 24,…}
       **/


      /**
       * Question!
       * 1. file의 name을 따로 빼서 name: '파일명' 으로 넣을까요? (ex--> name: test.txt)
       * 2. file의 타입은 Blob말고 File로 하면 될까요?
       * 3. FileController에서 formData를 받는 api를 추가해 확장할 수 있는지?
       **/


      // FormData의 key 확인
      for (let key of formData.keys()) {
        console.log(key);
      }

      // FormData의 value 확인
      for (let value of formData.values()) {
        console.log(value);
      }


      console.log(formData)
      vm.api.addFile(formData)
      .then(res => {
        console.log(res)

        vm.getFileList()
      })

    },

    onDownloadFile(fileId, title) {
      const vm = this;

      vm.api.downloadFile(fileId)
      .then(res => {
        console.log(res)
        const url = window.URL.createObjectURL(new Blob([res]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', title);
        document.body.appendChild(link);
        link.click();

        vm.getFileList();
      })
    },

    previewFiles(event) {
      this.file = event.target.files[0];
      console.log(event.target.files);
    },

    onFieldChanged(valueSet, err, field) {
      this.newFileInfo = Object.assign(this.newFileInfo, valueSet);
      delete this.newFileInfo['undefined'];
    },

    onClearFilter() {
      const vm = this;
      vm.$refs.MDKTable.clearSearchParams();
      vm.searchParams = {};
      // vm.getFileList(null);
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

    onSortChanged(data) {
      //

    },

    getFileList(filterOptions = {}, sortOptions = ['-fileId']) {
      const vm = this;
      vm.searchParams = {...filterOptions}
      sortOptions = [...sortOptions, '-fileId']
      vm.api.searchByExample(vm.searchParams, sortOptions, vm.paginationOptions.perPage, vm.$refs.MDKTable.currentPage)
      .then(res => {
        vm.items = res.content;
        vm.paginationOptions.totalCount = res.totalElements;
      })
    },

    getDispatcherList() {
      const vm = this;

      vm.dApi.searchByExample(null, null)
      .then(res => {
        vm.fields[2].dropdownOptions = {};
        res.forEach(d => {
          vm.fields[2].dropdownOptions[d.dispatcherId] = d.dispatcherId
        })
      })
    }
  },
  mounted() {
    const vm = this;
    vm.getFileList();
    vm.getDispatcherList();

    console.log(vm.fields)
  }
}
</script>

<style scoped lang="scss">
::v-deep {
  //.mdk-input-label {
  //  background-color: #efefef;
  //}

}
</style>