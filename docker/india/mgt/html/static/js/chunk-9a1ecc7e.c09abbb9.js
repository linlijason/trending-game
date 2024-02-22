(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-9a1ecc7e"],{"22b7":function(e,t,r){"use strict";r.d(t,"a",(function(){return a})),r.d(t,"b",(function(){return n}));var a=function(){var e=this,t=e.$createElement,r=e._self._c||t;return r("div",{staticClass:"app-container"},[r("h4",{staticClass:"form-header h4"},[e._v("基本信息")]),r("el-form",{ref:"form",attrs:{model:e.form,"label-width":"80px"}},[r("el-row",[r("el-col",{attrs:{span:8,offset:2}},[r("el-form-item",{attrs:{label:"用户昵称",prop:"nickName"}},[r("el-input",{attrs:{disabled:""},model:{value:e.form.nickName,callback:function(t){e.$set(e.form,"nickName",t)},expression:"form.nickName"}})],1)],1),r("el-col",{attrs:{span:8,offset:2}},[r("el-form-item",{attrs:{label:"登录账号",prop:"phonenumber"}},[r("el-input",{attrs:{disabled:""},model:{value:e.form.userName,callback:function(t){e.$set(e.form,"userName",t)},expression:"form.userName"}})],1)],1)],1)],1),r("h4",{staticClass:"form-header h4"},[e._v("角色信息")]),r("el-table",{directives:[{name:"loading",rawName:"v-loading",value:e.loading,expression:"loading"}],ref:"table",attrs:{"row-key":e.getRowKey,data:e.roles.slice((e.pageNum-1)*e.pageSize,e.pageNum*e.pageSize)},on:{"row-click":e.clickRow,"selection-change":e.handleSelectionChange}},[r("el-table-column",{attrs:{label:"序号",type:"index",align:"center"},scopedSlots:e._u([{key:"default",fn:function(t){return[r("span",[e._v(e._s((e.pageNum-1)*e.pageSize+t.$index+1))])]}}])}),r("el-table-column",{attrs:{type:"selection","reserve-selection":!0,width:"55"}}),r("el-table-column",{attrs:{label:"角色编号",align:"center",prop:"roleId"}}),r("el-table-column",{attrs:{label:"角色名称",align:"center",prop:"roleName"}}),r("el-table-column",{attrs:{label:"权限字符",align:"center",prop:"roleKey"}}),r("el-table-column",{attrs:{label:"创建时间",align:"center",prop:"createTime",width:"180"},scopedSlots:e._u([{key:"default",fn:function(t){return[r("span",[e._v(e._s(e.parseTime(t.row.createTime)))])]}}])})],1),r("pagination",{directives:[{name:"show",rawName:"v-show",value:e.total>0,expression:"total>0"}],attrs:{total:e.total,page:e.pageNum,limit:e.pageSize},on:{"update:page":function(t){e.pageNum=t},"update:limit":function(t){e.pageSize=t}}}),r("el-form",{attrs:{"label-width":"100px"}},[r("el-form-item",{staticStyle:{"text-align":"center","margin-left":"-120px","margin-top":"30px"}},[r("el-button",{attrs:{type:"primary"},on:{click:function(t){return e.submitForm()}}},[e._v("提交")]),r("el-button",{on:{click:function(t){return e.close()}}},[e._v("返回")])],1)],1)],1)},n=[]},"68bf":function(e,t,r){"use strict";Object.defineProperty(t,"__esModule",{value:!0}),t.default=void 0,r("d3b7"),r("159b"),r("d81d"),r("a15b");var a=r("c0c7"),n={name:"AuthRole",data:function(){return{loading:!0,total:0,pageNum:1,pageSize:10,roleIds:[],roles:[],form:{}}},created:function(){var e=this,t=this.$route.params&&this.$route.params.userId;t&&(this.loading=!0,(0,a.getAuthRole)(t).then((function(t){e.form=t.user,e.roles=t.roles,e.total=e.roles.length,e.$nextTick((function(){e.roles.forEach((function(t){t.flag&&e.$refs.table.toggleRowSelection(t)}))})),e.loading=!1})))},methods:{clickRow:function(e){this.$refs.table.toggleRowSelection(e)},handleSelectionChange:function(e){this.roleIds=e.map((function(e){return e.roleId}))},getRowKey:function(e){return e.roleId},submitForm:function(){var e=this,t=this.form.userId,r=this.roleIds.join(",");(0,a.updateAuthRole)({userId:t,roleIds:r}).then((function(t){e.$modal.msgSuccess("授权成功"),e.close()}))},close:function(){this.$store.dispatch("tagsView/delView",this.$route),this.$router.push({path:"/system/user"})}}};t.default=n},"6a33":function(e,t,r){"use strict";r.r(t);var a=r("22b7"),n=r("761b");for(var o in n)["default"].indexOf(o)<0&&function(e){r.d(t,e,(function(){return n[e]}))}(o);var u=r("2877"),l=Object(u["a"])(n["default"],a["a"],a["b"],!1,null,null,null);t["default"]=l.exports},"761b":function(e,t,r){"use strict";r.r(t);var a=r("68bf"),n=r.n(a);for(var o in a)["default"].indexOf(o)<0&&function(e){r.d(t,e,(function(){return a[e]}))}(o);t["default"]=n.a},c0c7:function(e,t,r){"use strict";var a=r("4ea4").default;Object.defineProperty(t,"__esModule",{value:!0}),t.addUser=s,t.changeUserStatus=m,t.delUser=d,t.exportUser=c,t.getAuthRole=w,t.getUser=l,t.getUserProfile=p,t.importTemplate=v,t.listUser=u,t.resetUserPwd=f,t.updateAuthRole=y,t.updateUser=i,t.updateUserProfile=h,t.updateUserPwd=g,t.uploadAvatar=b;var n=a(r("b775")),o=r("c38a");function u(e){return(0,n.default)({url:"/system/user/list",method:"get",params:e})}function l(e){return(0,n.default)({url:"/system/user/"+(0,o.praseStrEmpty)(e),method:"get"})}function s(e){return(0,n.default)({url:"/system/user",method:"post",data:e})}function i(e){return(0,n.default)({url:"/system/user",method:"put",data:e})}function d(e){return(0,n.default)({url:"/system/user/"+e,method:"delete"})}function c(e){return(0,n.default)({url:"/system/user/export",method:"get",params:e})}function f(e,t){var r={userId:e,password:t};return(0,n.default)({url:"/system/user/resetPwd",method:"put",data:r})}function m(e,t){var r={userId:e,status:t};return(0,n.default)({url:"/system/user/changeStatus",method:"put",data:r})}function p(){return(0,n.default)({url:"/system/user/profile",method:"get"})}function h(e){return(0,n.default)({url:"/system/user/profile",method:"put",data:e})}function g(e,t){var r={oldPassword:e,newPassword:t};return(0,n.default)({url:"/system/user/profile/updatePwd",method:"put",params:r})}function b(e){return(0,n.default)({url:"/system/user/profile/avatar",method:"post",data:e})}function v(){return(0,n.default)({url:"/system/user/importTemplate",method:"get"})}function w(e){return(0,n.default)({url:"/system/user/authRole/"+e,method:"get"})}function y(e){return(0,n.default)({url:"/system/user/authRole",method:"put",params:e})}}}]);