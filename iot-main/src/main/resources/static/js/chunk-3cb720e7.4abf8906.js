(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-3cb720e7"],{"1c64":function(t,e,a){},"1cc6":function(t,e,a){"use strict";var n=a("1c64"),i=a.n(n);i.a},"333d":function(t,e,a){"use strict";var n=function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{staticClass:"pagination-container",class:{hidden:t.hidden}},[a("el-pagination",t._b({attrs:{background:t.background,"current-page":t.currentPage,"page-size":t.pageSize,layout:t.layout,"page-sizes":t.pageSizes,total:t.total},on:{"update:currentPage":function(e){t.currentPage=e},"update:current-page":function(e){t.currentPage=e},"update:pageSize":function(e){t.pageSize=e},"update:page-size":function(e){t.pageSize=e},"size-change":t.handleSizeChange,"current-change":t.handleCurrentChange}},"el-pagination",t.$attrs,!1))],1)},i=[];a("c5f6");Math.easeInOutQuad=function(t,e,a,n){return t/=n/2,t<1?a/2*t*t+e:(t--,-a/2*(t*(t-2)-1)+e)};var s=function(){return window.requestAnimationFrame||window.webkitRequestAnimationFrame||window.mozRequestAnimationFrame||function(t){window.setTimeout(t,1e3/60)}}();function l(t){document.documentElement.scrollTop=t,document.body.parentNode.scrollTop=t,document.body.scrollTop=t}function o(){return document.documentElement.scrollTop||document.body.parentNode.scrollTop||document.body.scrollTop}function r(t,e,a){var n=o(),i=t-n,r=20,u=0;e="undefined"===typeof e?500:e;var c=function t(){u+=r;var o=Math.easeInOutQuad(u,n,i,e);l(o),u<e?s(t):a&&"function"===typeof a&&a()};c()}var u={name:"Pagination",props:{total:{required:!0,type:Number},page:{type:Number,default:1},limit:{type:Number,default:20},pageSizes:{type:Array,default:function(){return[10,20,30,50]}},layout:{type:String,default:"total, sizes, prev, pager, next, jumper"},background:{type:Boolean,default:!0},autoScroll:{type:Boolean,default:!0},hidden:{type:Boolean,default:!1}},computed:{currentPage:{get:function(){return this.page},set:function(t){this.$emit("update:page",t)}},pageSize:{get:function(){return this.limit},set:function(t){this.$emit("update:limit",t)}}},methods:{handleSizeChange:function(t){this.$emit("pagination",{page:this.currentPage,limit:t}),this.autoScroll&&r(0,800)},handleCurrentChange:function(t){this.$emit("pagination",{page:t,limit:this.pageSize}),this.autoScroll&&r(0,800)}}},c=u,p=(a("1cc6"),a("2877")),d=Object(p["a"])(c,n,i,!1,null,"f3b72548",null);e["a"]=d.exports},9709:function(t,e,a){"use strict";a.r(e);var n=function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{staticClass:"app-container"},[a("div",{staticClass:"filter-container",staticStyle:{"margin-bottom":"10px"}},[a("el-input",{staticClass:"filter-item",staticStyle:{width:"200px"},attrs:{placeholder:"请输入任务ID"},model:{value:t.listQuery.taskId,callback:function(e){t.$set(t.listQuery,"taskId",e)},expression:"listQuery.taskId"}}),t._v(" "),a("el-input",{staticClass:"filter-item",staticStyle:{width:"200px"},attrs:{placeholder:"请输入任务名称"},model:{value:t.listQuery.taskName,callback:function(e){t.$set(t.listQuery,"taskName",e)},expression:"listQuery.taskName"}}),t._v(" "),a("el-button",{directives:[{name:"waves",rawName:"v-waves"}],staticClass:"filter-item",attrs:{type:"primary",icon:"el-icon-search"},on:{click:t.queryTask}},[t._v("查询任务")]),t._v(" "),a("el-button",{staticClass:"filter-item",staticStyle:{"margin-left":"10px"},attrs:{type:"primary",icon:"el-icon-edit"},on:{click:t.addTask}},[t._v("添加任务")])],1),t._v(" "),a("el-table",{directives:[{name:"loading",rawName:"v-loading",value:t.listLoading,expression:"listLoading"}],attrs:{data:t.list,"element-loading-text":"Loading",border:"",fit:"","highlight-current-row":""}},[a("el-table-column",{attrs:{align:"center",label:"编号",width:"60"},scopedSlots:t._u([{key:"default",fn:function(e){return[t._v(t._s(e.$index+1))]}}])}),t._v(" "),a("el-table-column",{attrs:{align:"center",label:"任务名称"},scopedSlots:t._u([{key:"default",fn:function(e){return[t._v(t._s(e.row.taskName))]}}])}),t._v(" "),a("el-table-column",{attrs:{align:"center",label:"任务ID"},scopedSlots:t._u([{key:"default",fn:function(e){return[t._v(t._s(e.row.taskId))]}}])}),t._v(" "),a("el-table-column",{attrs:{label:"模板名称",align:"center"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("span",[t._v(t._s(e.row.templateName))])]}}])}),t._v(" "),a("el-table-column",{attrs:{label:"定时时间",align:"center"},scopedSlots:t._u([{key:"default",fn:function(e){return[t._v(t._s(e.row.taskCron))]}}])}),t._v(" "),a("el-table-column",{attrs:{"class-name":"status-col",label:"状态",align:"center"},scopedSlots:t._u([{key:"default",fn:function(e){return[t._v(t._s("002"==e.row.state?"运行":"停止"))]}}])}),t._v(" "),a("el-table-column",{attrs:{"class-name":"status-col",label:"操作",width:"300",align:"center"},scopedSlots:t._u([{key:"default",fn:function(e){var n=e.row,i=e.$index;return[a("el-row",["001"==n.state?a("el-button",{attrs:{size:"mini",type:"primary"},on:{click:function(e){return t.startTask(n,i)}}},[t._v("启动")]):t._e(),t._v(" "),"002"==n.state?a("el-button",{attrs:{size:"mini",type:"primary"},on:{click:function(e){return t.stopTask(n,i)}}},[t._v("停止")]):t._e(),t._v(" "),a("el-button",{attrs:{size:"mini",type:"warning"},on:{click:function(e){return t.updateTask(n,i)}}},[t._v("修改")]),t._v(" "),"001"==n.state?a("el-button",{attrs:{size:"mini",type:"danger"},on:{click:function(e){return t.deleteTask(n,i)}}},[t._v("删除")]):t._e()],1)]}}])})],1),t._v(" "),a("pagination",{directives:[{name:"show",rawName:"v-show",value:t.total>0,expression:"total>0"}],attrs:{total:t.total,page:t.listQuery.page,limit:t.listQuery.row},on:{"update:page":function(e){return t.$set(t.listQuery,"page",e)},"update:limit":function(e){return t.$set(t.listQuery,"row",e)},pagination:t.queryTask}}),t._v(" "),a("el-dialog",{attrs:{title:"定时任务",visible:t.dialogFormVisible},on:{"update:visible":function(e){t.dialogFormVisible=e}}},[a("el-form",{ref:"dataForm",staticStyle:{width:"400px","margin-left":"50px"},attrs:{rules:t.rules,model:t.temp,"label-position":"left","label-width":"70px"}},[a("el-form-item",{attrs:{label:"名称",prop:"type"}},[a("el-input",{attrs:{placeholder:"请输入定时任务名称"},model:{value:t.temp.taskName,callback:function(e){t.$set(t.temp,"taskName",e)},expression:"temp.taskName"}})],1),t._v(" "),a("el-form-item",{attrs:{label:"模板",prop:"type"}},[a("el-select",{staticClass:"filter-item",attrs:{placeholder:"请选择模板"},on:{change:t._changeTemplate},model:{value:t.temp.templateId,callback:function(e){t.$set(t.temp,"templateId",e)},expression:"temp.templateId"}},t._l(t.taskTemplates,(function(t){return a("el-option",{key:t.templateId,attrs:{label:t.templateName,value:t.templateId}})})),1)],1),t._v(" "),a("el-form-item",{attrs:{label:"时间",prop:"type"}},[a("el-input",{attrs:{placeholder:"请输入定时时间"},model:{value:t.temp.taskCron,callback:function(e){t.$set(t.temp,"taskCron",e)},expression:"temp.taskCron"}})],1),t._v(" "),t._l(t.templateSpecs,(function(e){return a("el-form-item",{key:e.specId,attrs:{label:e.specName}},[a("el-input",{attrs:{placeholder:e.specDesc},model:{value:e.value,callback:function(a){t.$set(e,"value",a)},expression:"item.value"}})],1)}))],2),t._v(" "),a("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{on:{click:function(e){t.dialogFormVisible=!1}}},[t._v("取消")]),t._v(" "),a("el-button",{attrs:{type:"primary"},on:{click:function(e){return t.saveTaskInfo()}}},[t._v("提交")])],1)],1),t._v(" "),a("el-dialog",{attrs:{title:"温馨提示",visible:t.deleteTaskDailogVisible,width:"30%","before-close":t.handleClose},on:{"update:visible":function(e){t.deleteTaskDailogVisible=e}}},[a("span",[t._v("您确定删除当前门禁吗？")]),t._v(" "),a("span",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{on:{click:function(e){t.deleteTaskDailogVisible=!1}}},[t._v("取 消")]),t._v(" "),a("el-button",{attrs:{type:"primary"},on:{click:t.doDeleteTask}},[t._v("确 定")])],1)])],1)},i=[],s=(a("ac6a"),a("b775"));function l(t){return Object(s["a"])({url:"/api/task/getTasks",method:"get",params:{page:1,row:10}})}function o(t){return Object(s["a"])({url:"/api/task/getTasks",method:"get",params:t})}function r(t){return Object(s["a"])({url:"/api/task/getTaskTemplates",method:"get",params:t})}function u(t){return Object(s["a"])({url:"/api/task/updateTask",method:"post",data:t})}function c(t){return Object(s["a"])({url:"/api/task/deleteTask",method:"post",data:t})}function p(t){return Object(s["a"])({url:"/api/task/saveTask",method:"post",data:t})}function d(t){return Object(s["a"])({url:"/api/task/startTask",method:"post",data:t})}function m(t){return Object(s["a"])({url:"/api/task/stopTask",method:"post",data:t})}var f=a("333d"),g=(a("ed08"),{filters:{statusFilter:function(t){var e={published:"success",draft:"gray",deleted:"danger"};return e[t]}},components:{Pagination:f["a"]},data:function(){return{listQuery:{page:1,row:10,taskName:"",taskId:""},list:null,listLoading:!0,deleteTaskDailogVisible:!1,dialogFormVisible:!1,curTask:{},taskTemplates:[],templateSpecs:[],total:0,temp:{taskId:"",taskName:"",taskCron:"",templateId:""},rules:{}}},created:function(){this.fetchData()},watch:{dialogFormVisible:function(t){0==t&&(this.temp={taskId:"",taskName:"",taskCron:"",templateId:""})}},methods:{fetchData:function(){var t=this;this.listLoading=!0,l().then((function(e){t.list=e.data,t.total=e.total,t.listLoading=!1})),r({}).then((function(e){t.taskTemplates=e.data,t.total=e.total,t.listLoading=!1}))},queryTask:function(){var t=this;this.listLoading=!0,o(this.listQuery).then((function(e){t.list=e.data,t.total=e.total,t.listLoading=!1}))},addTask:function(){this.dialogFormVisible=!0},updateTask:function(t){var e=this;this.temp.taskId=t.taskId,this.temp.taskName=t.taskName,this.temp.templateId=t.templateId,this.temp.taskCron=t.taskCron,this.taskTemplates.forEach((function(a){a.templateId==t.templateId&&(e.templateSpecs=a.taskTemplateSpecDtos)})),this.templateSpecs.forEach((function(e){t.taskAttrDtos.forEach((function(t){e.specCd==t.specCd&&(e.value=t.value,e.attrId=t.attrId)}))})),this.dialogFormVisible=!0},deleteTask:function(t){this.deleteTaskDailogVisible=!0,this.curTask=t},doDeleteTask:function(){var t=this;this.listLoading=!0,c(this.curTask).then((function(e){t.listLoading=!1,t.$message({type:"info",message:e.msg}),t.deleteTaskDailogVisible=!1,t.queryTask()}))},saveTaskInfo:function(){var t=this;this.listLoading=!0,this.temp.taskAttrDtos=this.templateSpecs,"-1"!=this.temp.taskId&&""!=this.temp.taskId?u(this.temp).then((function(e){t.listLoading=!1,t.dialogFormVisible=!1,t.queryTask()})):p(this.temp).then((function(e){t.listLoading=!1,t.dialogFormVisible=!1,t.queryTask()}))},startTask:function(t,e){var a=this;this.listLoading=!0,d(t).then((function(t){a.listLoading=!1,a.$message({type:"info",message:t.msg}),a.queryTask()}))},stopTask:function(t,e){var a=this;this.listLoading=!0,m(t).then((function(t){a.listLoading=!1,a.$message({type:"info",message:t.msg}),a.queryTask()}))},_changeTemplate:function(t){var e=this;this.taskTemplates.forEach((function(a){a.templateId==t&&(e.templateSpecs=a.taskTemplateSpecDtos)}))},handleCommand:function(t){t()}}}),h=g,k=a("2877"),v=Object(k["a"])(h,n,i,!1,null,null,null);e["default"]=v.exports},aa77:function(t,e,a){var n=a("5ca1"),i=a("be13"),s=a("79e5"),l=a("fdef"),o="["+l+"]",r="​",u=RegExp("^"+o+o+"*"),c=RegExp(o+o+"*$"),p=function(t,e,a){var i={},o=s((function(){return!!l[t]()||r[t]()!=r})),u=i[t]=o?e(d):l[t];a&&(i[a]=u),n(n.P+n.F*o,"String",i)},d=p.trim=function(t,e){return t=String(i(t)),1&e&&(t=t.replace(u,"")),2&e&&(t=t.replace(c,"")),t};t.exports=p},c5f6:function(t,e,a){"use strict";var n=a("7726"),i=a("69a8"),s=a("2d95"),l=a("5dbc"),o=a("6a99"),r=a("79e5"),u=a("9093").f,c=a("11e9").f,p=a("86cc").f,d=a("aa77").trim,m="Number",f=n[m],g=f,h=f.prototype,k=s(a("2aeb")(h))==m,v="trim"in String.prototype,b=function(t){var e=o(t,!1);if("string"==typeof e&&e.length>2){e=v?e.trim():d(e,3);var a,n,i,s=e.charCodeAt(0);if(43===s||45===s){if(a=e.charCodeAt(2),88===a||120===a)return NaN}else if(48===s){switch(e.charCodeAt(1)){case 66:case 98:n=2,i=49;break;case 79:case 111:n=8,i=55;break;default:return+e}for(var l,r=e.slice(2),u=0,c=r.length;u<c;u++)if(l=r.charCodeAt(u),l<48||l>i)return NaN;return parseInt(r,n)}}return+e};if(!f(" 0o1")||!f("0b1")||f("+0x1")){f=function(t){var e=arguments.length<1?0:t,a=this;return a instanceof f&&(k?r((function(){h.valueOf.call(a)})):s(a)!=m)?l(new g(b(e)),a,f):b(e)};for(var y,_=a("9e1e")?u(g):"MAX_VALUE,MIN_VALUE,NaN,NEGATIVE_INFINITY,POSITIVE_INFINITY,EPSILON,isFinite,isInteger,isNaN,isSafeInteger,MAX_SAFE_INTEGER,MIN_SAFE_INTEGER,parseFloat,parseInt,isInteger".split(","),T=0;_.length>T;T++)i(g,y=_[T])&&!i(f,y)&&p(f,y,c(g,y));f.prototype=h,h.constructor=f,a("2aba")(n,m,f)}},fdef:function(t,e){t.exports="\t\n\v\f\r   ᠎             　\u2028\u2029\ufeff"}}]);