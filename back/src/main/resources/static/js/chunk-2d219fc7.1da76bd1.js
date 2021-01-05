(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-2d219fc7"],{ba1c:function(t,e,a){"use strict";a.r(e);var i=function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{staticClass:"app-container"},[a("div",{staticClass:"filter-container",staticStyle:{"margin-bottom":"10px"}},[a("el-input",{staticClass:"filter-item",staticStyle:{width:"200px"},attrs:{placeholder:"请输入小区编码"},model:{value:t.listQuery.communityId,callback:function(e){t.$set(t.listQuery,"communityId",e)},expression:"listQuery.communityId"}}),t._v(" "),a("el-input",{staticClass:"filter-item",staticStyle:{width:"200px"},attrs:{placeholder:"请输入小区名称"},model:{value:t.listQuery.name,callback:function(e){t.$set(t.listQuery,"name",e)},expression:"listQuery.name"}}),t._v(" "),a("el-button",{directives:[{name:"waves",rawName:"v-waves"}],staticClass:"filter-item",attrs:{type:"primary",icon:"el-icon-search"},on:{click:t.queryCommunity}},[t._v("查询小区")]),t._v(" "),a("el-button",{staticClass:"filter-item",staticStyle:{"margin-left":"10px"},attrs:{type:"primary",icon:"el-icon-edit"},on:{click:t.addCommunity}},[t._v("添加小区")])],1),t._v(" "),a("el-table",{directives:[{name:"loading",rawName:"v-loading",value:t.listLoading,expression:"listLoading"}],attrs:{data:t.list,"element-loading-text":"Loading",border:"",fit:"","highlight-current-row":""}},[a("el-table-column",{attrs:{align:"center",label:"编号",width:"90"},scopedSlots:t._u([{key:"default",fn:function(e){return[t._v(t._s(e.$index+1))]}}])}),t._v(" "),a("el-table-column",{attrs:{align:"center",label:"省份"},scopedSlots:t._u([{key:"default",fn:function(e){return[t._v(t._s(e.row.provName))]}}])}),t._v(" "),a("el-table-column",{attrs:{align:"center",label:"市、州"},scopedSlots:t._u([{key:"default",fn:function(e){return[t._v(t._s(e.row.cityName))]}}])}),t._v(" "),a("el-table-column",{attrs:{align:"center",label:"区、县"},scopedSlots:t._u([{key:"default",fn:function(e){return[t._v(t._s(e.row.areaName))]}}])}),t._v(" "),a("el-table-column",{attrs:{align:"center",label:"小区编码"},scopedSlots:t._u([{key:"default",fn:function(e){return[t._v(t._s(e.row.communityId))]}}])}),t._v(" "),a("el-table-column",{attrs:{align:"center",label:"小区名称"},scopedSlots:t._u([{key:"default",fn:function(e){return[t._v(t._s(e.row.name))]}}])}),t._v(" "),a("el-table-column",{attrs:{label:"小区地址",align:"center"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("span",[t._v(t._s(e.row.address))])]}}])}),t._v(" "),a("el-table-column",{attrs:{label:"创建时间",align:"center"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("span",[t._v(t._s(e.row.createTime))])]}}])}),t._v(" "),a("el-table-column",{attrs:{"class-name":"status-col",label:"操作",align:"right"},scopedSlots:t._u([{key:"default",fn:function(e){var i=e.row,l=e.$index;return[t.curCommunity.communityId!=i.communityId?a("el-button",{attrs:{size:"mini",type:"success"},on:{click:function(e){return t.changeCommunity(i,l)}}},[t._v("切换小区")]):t._e(),t._v(" "),a("el-button",{attrs:{size:"mini",type:"danger"},on:{click:function(e){return t.deleteCommunity(i,l)}}},[t._v("删除")])]}}])})],1),t._v(" "),a("el-dialog",{attrs:{title:"小区",visible:t.dialogFormVisible},on:{"update:visible":function(e){t.dialogFormVisible=e}}},[a("el-form",{ref:"dataForm",staticStyle:{width:"60%","margin-left":"50px"},attrs:{rules:t.rules,model:t.temp,"label-position":"left","label-width":"70px"}},[a("el-form-item",{attrs:{label:"小区名称",prop:"type"}},[a("el-input",{attrs:{placeholder:"请输入小区名称"},model:{value:t.temp.name,callback:function(e){t.$set(t.temp,"name",e)},expression:"temp.name"}})],1),t._v(" "),a("el-form-item",{attrs:{label:"小区地区",prop:"type"}},[a("el-row",[a("el-col",{attrs:{span:8}},[a("el-select",{attrs:{placeholder:"请选择省份"},on:{change:t.changeProv},model:{value:t.provCode,callback:function(e){t.provCode=e},expression:"provCode"}},t._l(t.provs,(function(t){return a("el-option",{key:t.areaCode,attrs:{label:t.areaName,value:t.areaCode}})})),1)],1),t._v(" "),a("el-col",{attrs:{span:8}},[a("el-select",{staticStyle:{"margin-left":"20px"},attrs:{"collapse-tags":"",placeholder:"请选择城市"},on:{change:t.changeCity},model:{value:t.cityCode,callback:function(e){t.cityCode=e},expression:"cityCode"}},t._l(t.citys,(function(t){return a("el-option",{key:t.areaCode,attrs:{label:t.areaName,value:t.areaCode}})})),1)],1),t._v(" "),a("el-col",{attrs:{span:8}},[a("el-select",{staticStyle:{"margin-left":"20px"},attrs:{"collapse-tags":"",placeholder:"请选择区县"},model:{value:t.temp.cityCode,callback:function(e){t.$set(t.temp,"cityCode",e)},expression:"temp.cityCode"}},t._l(t.areas,(function(t){return a("el-option",{key:t.areaCode,attrs:{label:t.areaName,value:t.areaCode}})})),1)],1)],1)],1),t._v(" "),a("el-form-item",{attrs:{label:"小区地址",prop:"type"}},[a("el-input",{attrs:{placeholder:"请输入小区地址"},model:{value:t.temp.address,callback:function(e){t.$set(t.temp,"address",e)},expression:"temp.address"}})],1),t._v(" "),a("el-form-item",{attrs:{label:"外部编码",prop:"type"}},[a("el-input",{attrs:{placeholder:"请输入外部小区编码"},model:{value:t.temp.extCommunityId,callback:function(e){t.$set(t.temp,"extCommunityId",e)},expression:"temp.extCommunityId"}})],1)],1),t._v(" "),a("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{on:{click:function(e){t.dialogFormVisible=!1}}},[t._v("取消")]),t._v(" "),a("el-button",{attrs:{type:"primary"},on:{click:function(e){return t.saveCommunityInfo()}}},[t._v("提交")])],1)],1),t._v(" "),a("el-dialog",{attrs:{title:"温馨提示",visible:t.deleteCommunityDailogVisible,width:"30%","before-close":t.handleClose},on:{"update:visible":function(e){t.deleteCommunityDailogVisible=e}}},[a("span",[t._v("您确定删除当前小区吗？")]),t._v(" "),a("span",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{on:{click:function(e){t.deleteCommunityDailogVisible=!1}}},[t._v("取 消")]),t._v(" "),a("el-button",{attrs:{type:"primary"},on:{click:t.doDeleteCommunity}},[t._v("确 定")])],1)])],1)},l=[],n=a("7998"),o=(a("ed08"),{filters:{statusFilter:function(t){var e={published:"success",draft:"gray",deleted:"danger"};return e[t]}},components:{},data:function(){return{listQuery:{page:1,row:10,communityId:"",name:""},list:null,listLoading:!0,deleteCommunityDailogVisible:!1,dialogFormVisible:!1,curCommunity:{},temp:{communityId:"",name:"",extCommunityId:"",cityCode:"",address:""},rules:{},provs:[],citys:[],areas:[],provCode:"",cityCode:""}},watch:{dialogFormVisible:function(t){0==t&&(this.temp={communityId:"",name:"",extCommunityId:"",cityCode:"",address:""})}},created:function(){this.fetchData()},methods:{fetchData:function(){var t=this;this.listLoading=!0,Object(n["c"])().then((function(e){t.list=e.data,t.listLoading=!1})),Object(n["b"])({areaLevel:"101"}).then((function(e){t.provs=e.data}));var e=JSON.parse(window.localStorage.getItem("curCommunity"));null!=e&&void 0!=e&&(this.curCommunity=e)},changeProv:function(){var t=this;Object(n["b"])({areaLevel:"202",parentAreaCode:this.provCode}).then((function(e){t.citys=e.data}))},changeCity:function(){var t=this;Object(n["b"])({areaLevel:"303",parentAreaCode:this.cityCode}).then((function(e){t.areas=e.data}))},queryCommunity:function(){var t=this;this.listLoading=!0,Object(n["d"])(this.listQuery).then((function(e){t.list=e.data,t.listLoading=!1}))},addCommunity:function(){this.dialogFormVisible=!0},deleteCommunity:function(t){this.deleteCommunityDailogVisible=!0,this.curCommunity=t},doDeleteCommunity:function(){var t=this;this.listLoading=!0,Object(n["a"])(this.curCommunity).then((function(e){t.listLoading=!1,t.$message({type:"info",message:e.msg}),t.deleteCommunityDailogVisible=!1,t.queryCommunity()}))},saveCommunityInfo:function(){var t=this;this.listLoading=!0,Object(n["e"])(this.temp).then((function(e){t.listLoading=!1,t.dialogFormVisible=!1,t.queryCommunity()}))},changeCommunity:function(t){window.localStorage.setItem("curCommunity",JSON.stringify(t)),location.reload()}}}),s=o,r=a("2877"),c=Object(r["a"])(s,i,l,!1,null,null,null);e["default"]=c.exports}}]);