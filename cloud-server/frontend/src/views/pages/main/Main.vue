<template>
  <Layout>
    <div class="row">
      <div class="col">
        <BCard title="나의 작업 정보" style="height: 47vh">
          <MDKTable
            :hide-filter-row="true"
            :fields="dispatcherFields"
            :items="dispatcherItems"
            style="text-align: center"
          >
            <template #cell(robots)="props">
              {{props.value.length}} 개
            </template>
          </MDKTable>
          <MDKTable
              :hide-filter-row="true"
              :fields="recentFields"
              :items="recentItems"
          >
          </MDKTable>
        </BCard>
      </div>
      <div class="col">
        <BCard title="서비스 사용 안내" style="height: 47vh;">
          <BCard bg-variant="info">
            <div class="font-size-15"><b>▪ 신규 등록방법 </b></div>
            <br/>
            <ol>
              <li>Organization Dispatcher 정보를 등록 한다.</li>
              <li>환경파일 등록 시 해당 OD를 선택 하여 등록 한다.</li>
              <li>환경파일 업데이트시 신규로 등록 한다.</li>
              <li>로봇 식별 정보는 해당 OD를 지정 하여 입력 한다.</li>
            </ol>
          </BCard>
          <BCard bg-variant="info" class="mb-0">
            <div class="font-size-15"><b>▪ 모니터링 방법 </b></div>
            <br/>
            <ol>
              <li>RoOTS 파일 등록 후 OD 조회 여부는 최근 다운로드 시간을 확인 한다.</li>
              <li>로봇상태 수신은 상태변경시 또는 3분 주기로 현재상태가 전달 된다.</li>
              <li>로봇 상태의 이력정보는 로봇별로 상세보기 버튼을 클릭하여 확인 한다.</li>
            </ol>
          </BCard>
          <div class="mt-2" style="float: right">
            * OD : Organization Dispatcher
          </div>
        </BCard>
      </div>
    </div>
    <div class="row">
      <div class="col">
        <BCard title="연동 API 정보" style="height: 40vh">
          <br/>
          <MDKForm
            :fields="fields"
            :labelType="'table'"
            :readOnly="true"
            :entity="items"
          >
            <template v-slot:inputs="props">
              <template v-if="props.column.key == 'description'">
                <textarea readonly class="form-control" style="resize: none; border: none" v-model="props.entity.description">
                </textarea>
              </template>
            </template>

          </MDKForm>
          <br/>
          <br/>
          <MDKForm
              :fields="fields2"
              :labelType="'table'"
              :readOnly="true"
              :entity="items2"
          >

          </MDKForm>
        </BCard>
      </div>
    </div>
<!--    <BCard></BCard>-->
  </Layout>

</template>

<script>
import Layout from "../../layouts/main";
import MDKTable from "@/mdk/MDKTable2";
export default {
  name: "Main",
  components: {MDKTable, Layout},
  data() {
    const _dApi = this.$_app.service.dispatcher;
    return {
      dApi : _dApi,
      dispatcherFields: [
          _dApi.schema.dispatcherId,
          _dApi.schema.dispatcherName,
          _dApi.schema.robots,
      ],
      dispatcherItems: [],

      recentFields: [
        {key: 'task', label: '작업 구분'},
        {key: 'description', label: '등록 정보'},
        {key: 'regDate', label: '최근 등록 일시'},
      ],
      recentItems: [],

      fields: [
          [{key: 'apiName', label: 'API 명'}, {key: 'requestAddress', label: '요청 주소'}],
          [{key: 'type', label: '연동 유형'}, {key: 'description', label: '설명'}],
          [{key: 'methodType', label: 'Method Type'}, {key: 'message', label: '전송 메시지'}],
      ],
      items: {
          apiName: 'RoOTS 최신파일 정보',
          requestAddress: 'http://cloud-robot.i-on.net/rest/download/url/{parameter_ID}\n',
          type: '조회',
          description: `로봇 설정정보 파일을 등록하고 Dispatcher 별로 최신 파일 정보를 제공 함.\n(parameter_ID 는 Dispatcher ID)`,
          methodType: 'GET',
          message: '최신 파일정보 조회 API URL \n'
      },
      fields2: [
        [{key: 'apiName', label: 'API 명'}, {key: 'requestAddress', label: '요청 주소'}],
        [{key: 'type', label: '연동 유형'}, {key: 'description', label: '설명'}],
        [{key: 'methodType', label: 'Method Type'}, {key: 'message', label: '전송 메시지'}],
      ],
      items2:{
          apiName: '로봇 상태 정보',
          requestAddress: 'http://cloud-robot.i-on.net/rest/robot/{parameter_ID}\n',
          type: '수신',
          description: '로봇 상태정보를 수신하여 저장 함\n(parameter_ID 는 Robot ID)',
          methodType: 'POST',
          message: '로봇ID, 상태값 (Start, Progress, Stop, Complete)\n\n'
      }
    }
  },
  methods: {
    getDispatcherListByUser() {
      const vm = this;

      vm.dApi.searchByExample({robots: {}}, null)
      .then(res => {
        console.log(res)
        vm.dispatcherItems = res;
      })
    },

    getTopFile() {
      const vm = this;

      vm.$_app.service.file.findTopByJql({regUser: vm.$_app.user.email}, ['-regDate'])
      .then(res => {
        console.log(res)
        if(res.fileId) {
          vm.recentItems[1] = {};
          vm.recentItems[1].task = '환경파일 (RoOTS)'
          vm.recentItems[1].description = res.name
          vm.recentItems[1].regDate = res.regDate;
        }
      })
    },

    getTopDispatcher() {
      const vm = this;

      vm.$_app.service.dispatcher.findTopByJql({regUser: vm.$_app.user.email}, ['-regDate'])
          .then(res => {
            console.log(res)
            if(res.dispatcherId) {
              vm.recentItems[0] = {};
              vm.recentItems[0].task = '디스패처 (Organization Dispatcher)'
              vm.recentItems[0].description = res.dispatcherName;
              vm.recentItems[0].regDate = res.regDate;
            }
          })
    },

    getTopRobot() {
      const vm = this;

      vm.$_app.service.robot.findTopByJql({regUser: vm.$_app.user.email}, ['-regDate'])
          .then(res => {
            console.log(res)
            if(res.robotId) {
              vm.recentItems[2] = {};
              vm.recentItems[2].task = '로봇 식별정보'
              vm.recentItems[2].description = res.robotName
              vm.recentItems[2].regDate = res.regDate;
            }
          })
    },

  },
  mounted() {
    const vm = this;

    vm.getDispatcherListByUser();
    vm.getTopFile();
    vm.getTopDispatcher();
    vm.getTopRobot();
  }
}
</script>

<style scoped lang="scss">
::v-deep {
  .card.bg-info, {
    background-color: #fafafc !important
  }
  .mdk-form .mdk-input-label {
    background-color: #d0edff !important;
  }
  tr>td:nth-child(2), tr>td:nth-child(3) {
    text-align: center;
  }

  .table-responsive {
    overflow: auto;
    height: 185px;
  }

  thead > tr > th {
    background-color: #fafafc;
    position: sticky;
    top: 0;
  }

  label {
    margin-bottom: 0;
  }

}
</style>