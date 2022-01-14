(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-2d219fc7"],{ba1c:function(t,e,i){"use strict";i.r(e);var n=function(){var t=this,e=t.$createElement,i=t._self._c||e;return i("div",{staticClass:"app-container"},[i("div",{staticClass:"filter-container",staticStyle:{"margin-bottom":"10px"}},[i("el-input",{staticClass:"filter-item",staticStyle:{width:"200px"},attrs:{placeholder:"请输入小区编码"},model:{value:t.listQuery.communityId,callback:function(e){t.$set(t.listQuery,"communityId",e)},expression:"listQuery.communityId"}}),t._v(" "),i("el-input",{staticClass:"filter-item",staticStyle:{width:"200px"},attrs:{placeholder:"请输入小区名称"},model:{value:t.listQuery.name,callback:function(e){t.$set(t.listQuery,"name",e)},expression:"listQuery.name"}}),t._v(" "),i("el-button",{directives:[{name:"waves",rawName:"v-waves"}],staticClass:"filter-item",attrs:{type:"primary",icon:"el-icon-search"},on:{click:t.queryCommunity}},[t._v("查询小区")]),t._v(" "),0==t.list.length?i("el-button",{staticClass:"filter-item",staticStyle:{"margin-left":"10px"},attrs:{type:"primary",icon:"el-icon-edit"},on:{click:t.addCommunity}},[t._v("添加小区")]):t._e()],1),t._v(" "),i("el-table",{directives:[{name:"loading",rawName:"v-loading",value:t.listLoading,expression:"listLoading"}],attrs:{data:t.list,"element-loading-text":"Loading",border:"",fit:"","highlight-current-row":""}},[i("el-table-column",{attrs:{align:"center",label:"编号",width:"90"},scopedSlots:t._u([{key:"default",fn:function(e){return[t._v(t._s(e.$index+1))]}}])}),t._v(" "),i("el-table-column",{attrs:{align:"center",label:"省份"},scopedSlots:t._u([{key:"default",fn:function(e){return[t._v(t._s(e.row.provName))]}}])}),t._v(" "),i("el-table-column",{attrs:{align:"center",label:"市、州"},scopedSlots:t._u([{key:"default",fn:function(e){return[t._v(t._s(e.row.cityName))]}}])}),t._v(" "),i("el-table-column",{attrs:{align:"center",label:"区、县"},scopedSlots:t._u([{key:"default",fn:function(e){return[t._v(t._s(e.row.areaName))]}}])}),t._v(" "),i("el-table-column",{attrs:{align:"center",label:"小区编码"},scopedSlots:t._u([{key:"default",fn:function(e){return[t._v(t._s(e.row.communityId))]}}])}),t._v(" "),i("el-table-column",{attrs:{align:"center",label:"小区名称"},scopedSlots:t._u([{key:"default",fn:function(e){return[t._v(t._s(e.row.name))]}}])}),t._v(" "),i("el-table-column",{attrs:{label:"小区地址",align:"center"},scopedSlots:t._u([{key:"default",fn:function(e){return[i("span",[t._v(t._s(e.row.address))])]}}])}),t._v(" "),i("el-table-column",{attrs:{label:"创建时间",align:"center"},scopedSlots:t._u([{key:"default",fn:function(e){return[i("span",[t._v(t._s(e.row.createTime))])]}}])}),t._v(" "),i("el-table-column",{attrs:{"class-name":"status-col",label:"操作",align:"right"},scopedSlots:t._u([{key:"default",fn:function(e){var n=e.row,l=e.$index;return[i("el-button",{attrs:{size:"mini",type:"danger"},on:{click:function(e){return t.deleteCommunity(n,l)}}},[t._v("删除")])]}}])})],1),t._v(" "),i("el-dialog",{attrs:{title:"小区",visible:t.dialogFormVisible},on:{"update:visible":function(e){t.dialogFormVisible=e}}},[i("el-form",{ref:"dataForm",staticStyle:{width:"400px","margin-left":"50px"},attrs:{rules:t.rules,model:t.temp,"label-position":"left","label-width":"70px"}},[i("el-form-item",{attrs:{label:"小区编码",prop:"type"}},[i("el-input",{attrs:{placeholder:"请输入小区编码"},model:{value:t.temp.communityId,callback:function(e){t.$set(t.temp,"communityId",e)},expression:"temp.communityId"}})],1),t._v(" "),i("el-form-item",{attrs:{label:"小区名称",prop:"type"}},[i("el-input",{attrs:{placeholder:"请输入小区名称"},model:{value:t.temp.name,callback:function(e){t.$set(t.temp,"name",e)},expression:"temp.name"}})],1)],1),t._v(" "),i("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[i("el-button",{on:{click:function(e){t.dialogFormVisible=!1}}},[t._v("取消")]),t._v(" "),i("el-button",{attrs:{type:"primary"},on:{click:function(e){return t.saveCommunityInfo()}}},[t._v("提交")])],1)],1),t._v(" "),i("el-dialog",{attrs:{title:"温馨提示",visible:t.deleteCommunityDailogVisible,width:"30%","before-close":t.handleClose},on:{"update:visible":function(e){t.deleteCommunityDailogVisible=e}}},[i("span",[t._v("您确定删除当前小区吗？")]),t._v(" "),i("span",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[i("el-button",{on:{click:function(e){t.deleteCommunityDailogVisible=!1}}},[t._v("取 消")]),t._v(" "),i("el-button",{attrs:{type:"primary"},on:{click:t.doDeleteCommunity}},[t._v("确 定")])],1)])],1)},l=[],a=i("b775");function o(t){return Object(a["a"])({url:"/api/community/getCommunitys",method:"get",params:{page:1,row:10,communityId:""}})}function s(t){return Object(a["a"])({url:"/api/community/getCommunitys",method:"get",params:t})}function r(t){return Object(a["a"])({url:"/api/community/deleteCommunity",method:"post",data:t})}function u(t){return Object(a["a"])({url:"/api/community/saveCommunity",method:"post",data:t})}i("ed08");var m={filters:{statusFilter:function(t){var e={published:"success",draft:"gray",deleted:"danger"};return e[t]}},components:{},data:function(){return{listQuery:{page:1,row:10,communityId:"",name:""},list:null,listLoading:!0,deleteCommunityDailogVisible:!1,dialogFormVisible:!1,curCommunity:{},temp:{communityId:"",name:""},rules:{}}},watch:{dialogFormVisible:function(t){0==t&&(this.temp={communityId:"",name:""})}},created:function(){this.fetchData()},methods:{fetchData:function(){var t=this;this.listLoading=!0,o().then((function(e){t.list=e.data,t.listLoading=!1}))},queryCommunity:function(){var t=this;this.listLoading=!0,s(this.listQuery).then((function(e){t.list=e.data,t.listLoading=!1}))},addCommunity:function(){this.dialogFormVisible=!0},deleteCommunity:function(t){this.deleteCommunityDailogVisible=!0,this.curCommunity=t},doDeleteCommunity:function(){var t=this;this.listLoading=!0,r(this.curCommunity).then((function(e){t.listLoading=!1,t.$message({type:"info",message:e.msg}),t.deleteCommunityDailogVisible=!1,t.queryCommunity()}))},saveCommunityInfo:function(){var t=this;this.listLoading=!0,u(this.temp).then((function(e){t.listLoading=!1,t.dialogFormVisible=!1,t.queryCommunity()}))},restartCommunity:function(t){}}},c=m,d=i("2877"),f=Object(d["a"])(c,n,l,!1,null,null,null);e["default"]=f.exports}}]);