"use strict";(self["webpackChunkfrontend"]=self["webpackChunkfrontend"]||[]).push([[110],{3505:function(e,t,o){o.r(t),o.d(t,{default:function(){return p}});var n=o(3396);const a={class:"d-flex justify-content-end"},l=(0,n.Uk)("조회 초기화"),s=(0,n.Uk)("신규 등록"),i=(0,n._)("br",null,null,-1);function r(e,t,o,r,c,d){const m=(0,n.up)("b-button"),u=(0,n.up)("MDKTable"),f=(0,n.up)("BCard"),p=(0,n.up)("MDKForm"),h=(0,n.up)("b-modal"),g=(0,n.up)("Layout");return(0,n.wg)(),(0,n.j4)(g,null,{default:(0,n.w5)((()=>[(0,n.Wm)(f,null,{default:(0,n.w5)((()=>[(0,n._)("div",a,[(0,n.Wm)(m,{size:"sm",class:"me-3",variant:"secondary",onClick:d.onClearFilter},{default:(0,n.w5)((()=>[l])),_:1},8,["onClick"]),(0,n.Wm)(m,{size:"sm",variant:"primary",onClick:t[0]||(t[0]=e=>c.modalShow=!0)},{default:(0,n.w5)((()=>[s])),_:1})]),i,(0,n.Wm)(u,{fields:c.fields,items:c.items,pagination:c.paginationOptions,ref:"MDKTable",onRowSelected:d.onRowSelected,onFilterChange:d.getRobotInfoList},null,8,["fields","items","pagination","onRowSelected","onFilterChange"])])),_:1}),(0,n.Wm)(h,{modelValue:c.modalShow,"onUpdate:modelValue":t[1]||(t[1]=e=>c.modalShow=e),"ok-title":"저장","cancel-title":"취소",onOk:d.onSave},{default:(0,n.w5)((()=>[(0,n.Wm)(p,{fields:c.formFields,entity:{},onChange:d.onFieldChanged},null,8,["fields","onChange"])])),_:1},8,["modelValue","onOk"])])),_:1})}var c=o(1227),d=o(3467),m={name:"Manage_identification",components:{MDKTable:d.Z,Layout:c.Z},data(){const e=this.$_app.service.robot;return{api:e,fields:e.columnInfos,formFields:[e.schema.type,e.schema.name,e.schema.rid,e.schema.location],items:[],paginationOptions:{currentPage:1,totalCount:0,perPage:2},selectedItem:{},formObj:{},modalShow:!1}},methods:{getRobotInfoList(e,t){const o=this;o.api.searchByExample(e,t,o.paginationOptions.perPage,o.$refs.MDKTable.currentPage).then((e=>{o.items=e.content,o.paginationOptions.totalCount=e.totalElements,console.log(e)}))},onRowSelected(e){this.selectedItem=e;const t=[...document.getElementsByClassName("b-tr")];t.forEach((t=>{if(t.ariaSelected==e.trIndex){const e=t;e.setAttribute("data-selected",!0)}else t.ariaSelected!==e.trIndex&&t.setAttribute("data-selected",!1)}))},onFieldChanged(e,t,o){this.formObj=Object.assign(this.formObj,e)},onSave(){const e=this;e.formObj.createdby=e.$_app.user.email,console.log(e.formObj),e.api.add(e.formObj).then((e=>{console.log(e)})).catch((e=>{console.log(e)}))},onClearFilter(){const e=this;e.$refs.MDKTable.clearSearchParams()}},mounted(){const e=this;e.getRobotInfoList()}},u=o(89);const f=(0,u.Z)(m,[["render",r]]);var p=f}}]);
//# sourceMappingURL=110.64d8d32b.js.map