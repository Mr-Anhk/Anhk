$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/user/list',
        datatype: "json",
        colModel: [			
			{ label: '用户ID', name: 'userId', hidden:true, index: "user_id", width: 45, key: true },
			{ label: '用户名', name: 'userName', width: 75 },
            { label: '所属部门', name: 'deptName', sortable: false, width: 75 },
			{ label: '邮箱', name: 'email', width: 90 },
			{ label: '手机号', name: 'mobile', width: 100 },
			{ label: '状态', name: 'statuName', width: 60, formatter: function(value, options, row){
				return value === '禁用' ?
					'<span class="label label-danger">禁用</span>' : 
					'<span class="label label-success">正常</span>';
			}},
			{ label: '创建时间', name: 'createTime', index: "create_time", width: 85}
        ],
		viewrecords: true,
        height: 385,
        rowNum: 10,
		rowList : [10,30,50],
        rownumbers: true, 
        rownumWidth: 25, 
        autowidth:true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader : {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        },
        prmNames : {
            page:"page", 
            rows:"limit", 
            order: "order"
        },
        gridComplete:function(){
        	//隐藏grid底部滚动条
        	$("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" }); 
        }
    });
});
var setting = {
    data: {
        simpleData: {
            enable: true,
            idKey: "deptId",
            pIdKey: "parentId",
            rootPId: -1
        },
        key: {
            url:"nourl"
        }
    }
};
var ztree;

var vm = new Vue({
    el:'#rrapp',
    data:{
        q:{
            userName: null
        },
        showList: true,
        showPassWord: false,
        title:null,
        roleList:{},
        user:{
            status:1,
            deptId:null,
            deptName:null,
            roleIdList:[]
        }
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function(){
            vm.showPassWord = true;
            vm.showList = false;
            vm.title = "新增";
            vm.roleList = {};
            vm.user = {deptName:null, deptId:null, status:1, roleIdList:[]};

            //获取角色信息
            this.getRoleList();

            vm.getDept();
        },
        getDept: function(){
            //加载部门树
            $.get(baseURL + "sys/dept/list", function(r){
                ztree = $.fn.zTree.init($("#deptTree"), setting, r);
                var node = ztree.getNodeByParam("deptId", vm.user.deptId);
                if(node != null){
                    ztree.selectNode(node);

                    vm.user.deptName = node.name;
                }
            })
        },
        update: function () {
            vm.showPassWord = false;
            var userId = getSelectedRow();
            if(userId == null){
                return ;
            }

            vm.showList = false;
            vm.title = "修改";

            vm.getUser(userId);
            //获取角色信息
            this.getRoleList();
        },
        permissions: function () {
            var userId = getSelectedRow();
            if(userId == null){
                return ;
            }

            window.location.href=baseURL+"sys/permissions/index/"+userId;
        },
        del: function () {
            var userIds = getSelectedRows();
            if(userIds == null){
                return ;
            }

            confirm('确定要删除选中的记录？', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/user/delete",
                    contentType: "application/json",
                    data: JSON.stringify(userIds),
                    success: function(r){
                        if(r.status == 0){
                            alert('操作成功', function(){
                                vm.reload();
                            });
                        }else{
                            alert(r.message);
                        }
                    }
                });
            });
        },
        saveOrUpdate: function () {
            var url = vm.user.userId == null ? "sys/user/save" : "sys/user/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.user),
                success: function(r){
                    if(r.status === 0){
                        alert('操作成功', function(){
                            vm.reload();
                        });
                    }else{
                        alert(r.message);
                    }
                }
            });
        },
        getUser: function(userId){
            $.get(baseURL + "sys/user/info/"+userId, function(r){
                vm.user = r.user;
                vm.user.password = null;

                vm.getDept();
            });
        },
        getRoleList: function(){
            $.get(baseURL + "sys/role/select", function(r){
                vm.roleList = r.list;
            });
        },
        //导出到Excel
        exportToExcel: function () {
            //获取表格列名
            var colNames=$("#jqGrid").jqGrid('getGridParam','colNames');
            //获取表格列字段
            var colModel=$("#jqGrid").jqGrid('getGridParam','colModel');
            var headerNames = "";
            var headerFields = "";
            //因为表格前面有个序号、复选框、以及用户ID等三列，因此从第四列（索引为3）开始循环
            for (let i = 3; i < colNames.length; i++) {
                headerNames += colNames[i] + ",";
            }
            for (let i = 3; i < colModel.length; i++) {
                headerFields += colModel[i].name + ",";
            }
            //构建一个 form表单
            // 创建一个 form
            var form1 = document.createElement("form");
            form1.id = "form1";
            form1.name = "form1";
            // 添加到 body 中
            document.body.appendChild(form1);
            // 创建输入框
            var input1 = document.createElement("input");
            input1.type = "text";
            input1.name = "headerNames";
            input1.value = headerNames;
            form1.appendChild(input1);
            var input2 = document.createElement("input");
            input2.type = "text";
            input2.name = "headerFields";
            input2.value = headerFields;
            form1.appendChild(input2);
            var input3 = document.createElement("input");
            input3.type = "text";
            input3.name = "userName";
            input3.value = vm.q.userName;
            // 将该输入框插入到 form 中
            form1.appendChild(input3);
            // form 的提交方式
            form1.method = "POST";
            // form 提交路径
            form1.action = baseURL + "sys/user/export";
            // 对该 form 执行提交
            form1.submit();
            // 删除该 form
            document.body.removeChild(form1);
        },
        //转换状态
        convertStatu: function () {
            var userId = getSelectedRow();
            if(userId == null){
                return ;
            }
            //获取指定用户ID的行数据
            var row = $("#jqGrid").jqGrid('getRowData', userId);
            //判断，并改变状态码值
            var status = row.statuName.indexOf("正常") === -1 ? 1 : 0;
            //封装数据源
            var data = {"userId":userId, "status": status}
            $.ajax({
                type: "POST",
                url: baseURL + "sys/user/convertStatu",
                contentType: "application/json",
                data: JSON.stringify(data),
                success: function(r){
                    if(r.status === 0){
                        alert('操作成功', function(){
                            vm.reload();
                        });
                    }else{
                        alert(r.message);
                    }
                }
            });
        },
        deptTree: function(){
            layer.open({
                type: 1,
                offset: '50px',
                skin: 'layui-layer-molv',
                title: "选择部门",
                area: ['300px', '450px'],
                shade: 0,
                shadeClose: false,
                content: jQuery("#deptLayer"),
                btn: ['确定', '取消'],
                btn1: function (index) {
                    var node = ztree.getSelectedNodes();
                    //选择上级部门
                    vm.user.deptId = node[0].deptId;
                    vm.user.deptName = node[0].name;

                    layer.close(index);
                }
            });
        },
        reload: function () {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam','page');
            $("#jqGrid").jqGrid('setGridParam',{
                postData:{'userName': vm.q.userName},
                page:page
            }).trigger("reloadGrid");
        }
    }
});