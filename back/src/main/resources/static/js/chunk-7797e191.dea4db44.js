(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-7797e191"],{"1c64":function(t,e,n){},"1cc6":function(t,e,n){"use strict";var i=n("1c64"),a=n.n(i);a.a},"333d":function(t,e,n){"use strict";var i=function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("div",{staticClass:"pagination-container",class:{hidden:t.hidden}},[n("el-pagination",t._b({attrs:{background:t.background,"current-page":t.currentPage,"page-size":t.pageSize,layout:t.layout,"page-sizes":t.pageSizes,total:t.total},on:{"update:currentPage":function(e){t.currentPage=e},"update:current-page":function(e){t.currentPage=e},"update:pageSize":function(e){t.pageSize=e},"update:page-size":function(e){t.pageSize=e},"size-change":t.handleSizeChange,"current-change":t.handleCurrentChange}},"el-pagination",t.$attrs,!1))],1)},a=[];n("c5f6");Math.easeInOutQuad=function(t,e,n,i){return t/=i/2,t<1?n/2*t*t+e:(t--,-n/2*(t*(t-2)-1)+e)};var o=function(){return window.requestAnimationFrame||window.webkitRequestAnimationFrame||window.mozRequestAnimationFrame||function(t){window.setTimeout(t,1e3/60)}}();function l(t){document.documentElement.scrollTop=t,document.body.parentNode.scrollTop=t,document.body.scrollTop=t}function r(){return document.documentElement.scrollTop||document.body.parentNode.scrollTop||document.body.scrollTop}function s(t,e,n){var i=r(),a=t-i,s=20,u=0;e="undefined"===typeof e?500:e;var c=function t(){u+=s;var r=Math.easeInOutQuad(u,i,a,e);l(r),u<e?o(t):n&&"function"===typeof n&&n()};c()}var u={name:"Pagination",props:{total:{required:!0,type:Number},page:{type:Number,default:1},limit:{type:Number,default:20},pageSizes:{type:Array,default:function(){return[10,20,30,50]}},layout:{type:String,default:"total, sizes, prev, pager, next, jumper"},background:{type:Boolean,default:!0},autoScroll:{type:Boolean,default:!0},hidden:{type:Boolean,default:!1}},computed:{currentPage:{get:function(){return this.page},set:function(t){this.$emit("update:page",t)}},pageSize:{get:function(){return this.limit},set:function(t){this.$emit("update:limit",t)}}},methods:{handleSizeChange:function(t){this.$emit("pagination",{page:this.currentPage,limit:t}),this.autoScroll&&s(0,800)},handleCurrentChange:function(t){this.$emit("pagination",{page:t,limit:this.pageSize}),this.autoScroll&&s(0,800)}}},c=u,m=(n("1cc6"),n("2877")),d=Object(m["a"])(c,i,a,!1,null,"f3b72548",null);e["a"]=d.exports},aa77:function(t,e,n){var i=n("5ca1"),a=n("be13"),o=n("79e5"),l=n("fdef"),r="["+l+"]",s="​",u=RegExp("^"+r+r+"*"),c=RegExp(r+r+"*$"),m=function(t,e,n){var a={},r=o((function(){return!!l[t]()||s[t]()!=s})),u=a[t]=r?e(d):l[t];n&&(a[n]=u),i(i.P+i.F*r,"String",a)},d=m.trim=function(t,e){return t=String(a(t)),1&e&&(t=t.replace(u,"")),2&e&&(t=t.replace(c,"")),t};t.exports=m},ba1c:function(t,e,n){"use strict";n.r(e);var i=function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("div",{staticClass:"app-container"},[n("div",{staticClass:"filter-container",staticStyle:{"margin-bottom":"10px"}},[n("el-input",{staticClass:"filter-item",staticStyle:{width:"200px"},attrs:{placeholder:"请输入小区编码"},model:{value:t.listQuery.communityId,callback:function(e){t.$set(t.listQuery,"communityId",e)},expression:"listQuery.communityId"}}),t._v(" "),n("el-input",{staticClass:"filter-item",staticStyle:{width:"200px"},attrs:{placeholder:"请输入小区名称"},model:{value:t.listQuery.name,callback:function(e){t.$set(t.listQuery,"name",e)},expression:"listQuery.name"}}),t._v(" "),n("el-button",{staticClass:"filter-item",attrs:{type:"primary",icon:"el-icon-search"},on:{click:t.queryCommunity}},[t._v("查询小区")]),t._v(" "),n("el-button",{staticClass:"filter-item",staticStyle:{"margin-left":"10px"},attrs:{type:"primary",icon:"el-icon-edit"},on:{click:t.addCommunity}},[t._v("添加小区")])],1),t._v(" "),n("el-table",{directives:[{name:"loading",rawName:"v-loading",value:t.listLoading,expression:"listLoading"}],attrs:{data:t.list,"element-loading-text":"Loading",border:"",fit:"","highlight-current-row":""}},[n("el-table-column",{attrs:{align:"center",label:"编号",width:"90"},scopedSlots:t._u([{key:"default",fn:function(e){return[t._v(t._s(e.$index+1))]}}])}),t._v(" "),n("el-table-column",{attrs:{align:"center",label:"小区名称"},scopedSlots:t._u([{key:"default",fn:function(e){return[t._v(t._s(e.row.name))]}}])}),t._v(" "),n("el-table-column",{attrs:{align:"center",label:"城市"},scopedSlots:t._u([{key:"default",fn:function(e){return[t._v(t._s(e.row.provName+e.row.cityName+e.row.areaName))]}}])}),t._v(" "),n("el-table-column",{attrs:{align:"center",label:"外部ID"},scopedSlots:t._u([{key:"default",fn:function(e){return[t._v(t._s(e.row.extCommunityId))]}}])}),t._v(" "),n("el-table-column",{attrs:{align:"center",label:"小区编码"},scopedSlots:t._u([{key:"default",fn:function(e){return[t._v(t._s(e.row.communityId))]}}])}),t._v(" "),n("el-table-column",{attrs:{label:"小区地址",align:"center"},scopedSlots:t._u([{key:"default",fn:function(e){return[n("span",[t._v(t._s(e.row.address))])]}}])}),t._v(" "),n("el-table-column",{attrs:{label:"创建时间",align:"center"},scopedSlots:t._u([{key:"default",fn:function(e){return[n("span",[t._v(t._s(e.row.createTime))])]}}])}),t._v(" "),n("el-table-column",{attrs:{"class-name":"status-col",label:"操作",align:"right",width:"250"},scopedSlots:t._u([{key:"default",fn:function(e){var i=e.row,a=e.$index;return[t.curCommunity.communityId!=i.communityId?n("el-button",{attrs:{size:"mini",type:"success"},on:{click:function(e){return t.changeCommunity(i,a)}}},[t._v("切换小区")]):t._e(),t._v(" "),n("el-button",{attrs:{size:"mini",type:"primary"},on:{click:function(e){return t.editCommunity(i,a)}}},[t._v("修改")]),t._v(" "),n("el-button",{attrs:{size:"mini",type:"danger"},on:{click:function(e){return t.deleteCommunity(i,a)}}},[t._v("删除")])]}}])})],1),t._v(" "),n("pagination",{directives:[{name:"show",rawName:"v-show",value:t.total>0,expression:"total > 0"}],attrs:{total:t.total,page:t.listQuery.page,limit:t.listQuery.row},on:{"update:page":function(e){return t.$set(t.listQuery,"page",e)},"update:limit":function(e){return t.$set(t.listQuery,"row",e)},pagination:t.fetchData}}),t._v(" "),n("el-dialog",{attrs:{title:"小区",visible:t.dialogFormVisible},on:{"update:visible":function(e){t.dialogFormVisible=e}}},[n("el-form",{ref:"dataForm",staticStyle:{width:"60%","margin-left":"50px"},attrs:{rules:t.rules,model:t.temp,"label-position":"left","label-width":"70px"}},[n("el-form-item",{attrs:{label:"小区名称",prop:"type"}},[n("el-input",{attrs:{placeholder:"请输入小区名称"},model:{value:t.temp.name,callback:function(e){t.$set(t.temp,"name",e)},expression:"temp.name"}})],1),t._v(" "),n("el-form-item",{directives:[{name:"show",rawName:"v-show",value:""==t.temp.communityId,expression:"temp.communityId ==''"}],attrs:{label:"小区地区",prop:"type"}},[n("el-row",[n("el-col",{attrs:{span:8}},[n("el-select",{attrs:{placeholder:"请选择省份"},on:{change:t.changeProv},model:{value:t.provCode,callback:function(e){t.provCode=e},expression:"provCode"}},t._l(t.provs,(function(t){return n("el-option",{key:t.areaCode,attrs:{label:t.areaName,value:t.areaCode}})})),1)],1),t._v(" "),n("el-col",{attrs:{span:8}},[n("el-select",{staticStyle:{"margin-left":"20px"},attrs:{"collapse-tags":"",placeholder:"请选择城市"},on:{change:t.changeCity},model:{value:t.cityCode,callback:function(e){t.cityCode=e},expression:"cityCode"}},t._l(t.citys,(function(t){return n("el-option",{key:t.areaCode,attrs:{label:t.areaName,value:t.areaCode}})})),1)],1),t._v(" "),n("el-col",{attrs:{span:8}},[n("el-select",{staticStyle:{"margin-left":"20px"},attrs:{"collapse-tags":"",placeholder:"请选择区县"},model:{value:t.temp.cityCode,callback:function(e){t.$set(t.temp,"cityCode",e)},expression:"temp.cityCode"}},t._l(t.areas,(function(t){return n("el-option",{key:t.areaCode,attrs:{label:t.areaName,value:t.areaCode}})})),1)],1)],1)],1),t._v(" "),n("el-form-item",{attrs:{label:"小区地址",prop:"type"}},[n("el-input",{attrs:{placeholder:"请输入小区地址"},model:{value:t.temp.address,callback:function(e){t.$set(t.temp,"address",e)},expression:"temp.address"}})],1),t._v(" "),n("el-form-item",{attrs:{label:"外部编码",prop:"type"}},[n("el-input",{attrs:{placeholder:"请输入外部小区编码"},model:{value:t.temp.extCommunityId,callback:function(e){t.$set(t.temp,"extCommunityId",e)},expression:"temp.extCommunityId"}})],1)],1),t._v(" "),n("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[n("el-button",{on:{click:function(e){t.dialogFormVisible=!1}}},[t._v("取消")]),t._v(" "),n("el-button",{attrs:{type:"primary"},on:{click:function(e){return t.saveCommunityInfo()}}},[t._v("提交")])],1)],1),t._v(" "),n("el-dialog",{attrs:{title:"温馨提示",visible:t.deleteCommunityDailogVisible,width:"30%","before-close":t.handleClose},on:{"update:visible":function(e){t.deleteCommunityDailogVisible=e}}},[n("span",[t._v("您确定删除当前小区吗？")]),t._v(" "),n("span",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[n("el-button",{on:{click:function(e){t.deleteCommunityDailogVisible=!1}}},[t._v("取 消")]),t._v(" "),n("el-button",{attrs:{type:"primary"},on:{click:t.doDeleteCommunity}},[t._v("确 定")])],1)])],1)},a=[],o=n("7998"),l=n("333d"),r=(n("ed08"),{filters:{statusFilter:function(t){var e={published:"success",draft:"gray",deleted:"danger"};return e[t]}},components:{Pagination:l["a"]},data:function(){return{listQuery:{page:1,row:10,communityId:"",name:""},list:null,listLoading:!0,deleteCommunityDailogVisible:!1,dialogFormVisible:!1,curCommunity:{},total:0,temp:{communityId:"",name:"",extCommunityId:"",cityCode:"",address:""},rules:{},provs:[],citys:[],areas:[],provCode:"",cityCode:""}},watch:{dialogFormVisible:function(t){0==t&&(this.temp={communityId:"",name:"",extCommunityId:"",cityCode:"",address:""})}},created:function(){this.fetchData()},methods:{handleClose:function(){handleCloses()},fetchData:function(){var t=this;this.listLoading=!0,Object(o["c"])().then((function(e){t.list=e.data,t.total=e.total,t.listLoading=!1})),Object(o["b"])({areaLevel:"101"}).then((function(e){t.provs=e.data}));var e=JSON.parse(window.localStorage.getItem("curCommunity"));null!=e&&void 0!=e&&(this.curCommunity=e)},changeProv:function(){var t=this;Object(o["b"])({areaLevel:"202",parentAreaCode:this.provCode}).then((function(e){t.citys=e.data}))},changeCity:function(){var t=this;Object(o["b"])({areaLevel:"303",parentAreaCode:this.cityCode}).then((function(e){t.areas=e.data}))},queryCommunity:function(){var t=this;this.listLoading=!0,Object(o["d"])(this.listQuery).then((function(e){t.list=e.data,t.listLoading=!1}))},editCommunity:function(t,e){this.dialogFormVisible=!0,this.temp=t},addCommunity:function(){this.dialogFormVisible=!0},deleteCommunity:function(t){this.deleteCommunityDailogVisible=!0,this.curCommunity=t},doDeleteCommunity:function(){var t=this;this.listLoading=!0,Object(o["a"])(this.curCommunity).then((function(e){t.listLoading=!1,t.$message({type:"info",message:e.msg}),t.deleteCommunityDailogVisible=!1,t.queryCommunity()}))},saveCommunityInfo:function(){var t=this;if(this.listLoading=!0,""!=this.temp.communityId)return this.temp.cityCode=null,this.temp.appId=null,void Object(o["f"])(this.temp).then((function(e){t.listLoading=!1,t.dialogFormVisible=!1,t.queryCommunity()}));Object(o["e"])(this.temp).then((function(e){t.listLoading=!1,t.dialogFormVisible=!1,t.queryCommunity()}))},changeCommunity:function(t){window.localStorage.setItem("curCommunity",JSON.stringify(t)),location.reload()}}}),s=r,u=n("2877"),c=Object(u["a"])(s,i,a,!1,null,null,null);e["default"]=c.exports},c5f6:function(t,e,n){"use strict";var i=n("7726"),a=n("69a8"),o=n("2d95"),l=n("5dbc"),r=n("6a99"),s=n("79e5"),u=n("9093").f,c=n("11e9").f,m=n("86cc").f,d=n("aa77").trim,p="Number",f=i[p],y=f,g=f.prototype,v=o(n("2aeb")(g))==p,h="trim"in String.prototype,b=function(t){var e=r(t,!1);if("string"==typeof e&&e.length>2){e=h?e.trim():d(e,3);var n,i,a,o=e.charCodeAt(0);if(43===o||45===o){if(n=e.charCodeAt(2),88===n||120===n)return NaN}else if(48===o){switch(e.charCodeAt(1)){case 66:case 98:i=2,a=49;break;case 79:case 111:i=8,a=55;break;default:return+e}for(var l,s=e.slice(2),u=0,c=s.length;u<c;u++)if(l=s.charCodeAt(u),l<48||l>a)return NaN;return parseInt(s,i)}}return+e};if(!f(" 0o1")||!f("0b1")||f("+0x1")){f=function(t){var e=arguments.length<1?0:t,n=this;return n instanceof f&&(v?s((function(){g.valueOf.call(n)})):o(n)!=p)?l(new y(b(e)),n,f):b(e)};for(var C,_=n("9e1e")?u(y):"MAX_VALUE,MIN_VALUE,NaN,NEGATIVE_INFINITY,POSITIVE_INFINITY,EPSILON,isFinite,isInteger,isNaN,isSafeInteger,MAX_SAFE_INTEGER,MIN_SAFE_INTEGER,parseFloat,parseInt,isInteger".split(","),w=0;_.length>w;w++)a(y,C=_[w])&&!a(f,C)&&m(f,C,c(y,C));f.prototype=g,g.constructor=f,n("2aba")(i,p,f)}},fdef:function(t,e){t.exports="\t\n\v\f\r   ᠎             　\u2028\u2029\ufeff"}}]);