// @ts-ignore
import {JqlApi} from "./mdk/jql-api.js";
// @ts-ignore
import {JqlSchema, TextColumn, NumberColumn, DateColumn, Dropdown} from "./mdk/jql-schema.js";
// @ts-ignore
import {http} from "@/mdk/auth-api";
// import confirmDatePlugin from "flatpickr/dist/plugins/confirmDate/confirmDate";

const required = (v: string | null, entity: any) => {
    return (v != null && v != '') ? null : "필수 항목입니다"
}

const haveRoles = (v: string | string[], entity: any) => {
    console.log(v, entity)
    return v.includes(',') ? null : '권한은 쉼표(,)로 구분되어야 합니다.'
}

const onlyNum = (v: string, entity: any) => {
    return (!isNaN(parseFloat(v))) ? null : '숫자만 입력 가능합니다.'
}

const userSchema = new JqlSchema([
    new TextColumn('email', '이메일'),
    new TextColumn('name', '이름'),
    new TextColumn('roles', '권한'),
])

const containerSchema = new JqlSchema([
    new TextColumn('id', '관리 ID'),
    new TextColumn('name', '컨테이너 명'),
    new TextColumn('type', '유형 구분'),
    new TextColumn('version', '버전'),
    new TextColumn('resister', '등록자'),
    new TextColumn('created', '등록일'),
    new TextColumn('status', '등록상태'),
    new TextColumn('description', '설명'),
    new NumberColumn('downloadCnt', '누적 다운로드 횟수'),
    new TextColumn('lastDownload', '최근다운로드'),
    new TextColumn('link', '파일다운로드'),
]);

const fileSchema = new JqlSchema([
    new NumberColumn('fileId', '관리 ID'),
    new TextColumn('name', '환경파일 명'),
    new Dropdown('dispatcherId', 'Dispatcher ID', {}),
    // new TextColumn('type', '파일유형'),
    // new NumberColumn('version', '버전'),
    new TextColumn('regUser', '등록자'),
    new DateColumn('regDate', '등록일'),
    // new TextColumn('status', '등록상태'),
    new TextColumn('description', '설명'),
    new DateColumn('lastDownloaded', '최근다운로드'),
    new TextColumn('link', '파일다운로드'),
    new TextColumn('file', '파일업로드')
]);

const dispatcherSchema = new JqlSchema([
    new TextColumn('dispatcherId', 'Dispatcher ID'),
    new TextColumn('dispatcherName', 'Organization dispatcher 명'),
    new TextColumn('description', '설명'),
    new TextColumn('regUser', '등록자'),
    new DateColumn('regDate', '등록일'),
    new Dropdown('robots', '로봇'),
]);

const robotSchema = new JqlSchema([
    new TextColumn('robotId', '로봇 ID'),
    new TextColumn('robotName', '로봇 명'),
    new Dropdown('robotType', '로봇 유형', {COBOT: 'COBOT', ARM_TOW: 'ARM_TOW',PALLETIZER: 'PALLETIZER', AMR_LIFT: 'AMR_LIFT'}),
    new TextColumn('robotPlace', '설치 장소'),
    new TextColumn('regUser', '등록자'),
    new DateColumn('regDate', '조회 기간'),
    new Dropdown('orgDispatcher.dispatcherId', 'Dispatcher ID', {}),
    new TextColumn('log', '작업상태 이력'),
]);
robotSchema.join('orgDispatcher', dispatcherSchema)

const robotStatusSchema = new JqlSchema([
    new TextColumn('robotId', '로봇ID'),
    new TextColumn('updateTime', '시각'),
    new TextColumn('robotStatus', '상태'),
])


class fileApi extends JqlApi {
    private baseUrl: string;
    private schema: JqlSchema;
    private columnInfos: any;
    constructor(baseUrl: string, schema: JqlSchema) {
        super(baseUrl, schema);
        this.baseUrl = baseUrl;
        this.schema = schema;
        this.columnInfos = schema.columns;
    }

    async addFile(jql: object) {
        const res = await http.post(this.baseUrl+`/upload`, jql, {headers: {'Content-Type': 'multipart/form-data'}});

        return res.data;
    }

    async downloadFile(fId: number) {
        const res = await http.get(this.baseUrl+`/download/${fId}`, {headers: {'Content-Type': 'multipart/form-data'}});

        return res.data;
    }
}


/***
 * Schema 간 Join 설정.
 */



export class RestService {
    private user: JqlApi;
    private container: JqlApi;
    private file: JqlApi;
    private robot: JqlApi;
    private dispatcher: JqlApi;
    private robotStatus: JqlApi;
    constructor() {
        this.user = new JqlApi(`/api/account`, userSchema)
        this.container = new JqlApi(`/api/container`, containerSchema)
        this.file = new fileApi(`/api/file`, fileSchema)
        this.robot = new JqlApi(`/api/robot`, robotSchema)
        this.robotStatus = new JqlApi(`/api/robot-status`, robotStatusSchema)
        this.dispatcher = new JqlApi(`/api/dispatcher`, dispatcherSchema)
    }
}

