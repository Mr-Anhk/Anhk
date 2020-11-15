$(function () {
    var href = window.location.href;
    //截取路径中=后面的值
    var num = href.indexOf("=");
    vm.q.roleId = href.substring(num + 1, href.length);
    vm.reload();
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/user/userRole?roleId='+vm.q.roleId,
        datatype: "json",
        colModel: [
            {label: '用户名', name: 'userName', width: 75},
            {label: '所属部门', name: 'deptName', sortable: false, width: 75},
            {label: '邮箱', name: 'email', width: 90},
            {label: '手机号', name: 'mobile', width: 100},
            { label: '状态', name: 'statuName', width: 60, formatter: function(value, options, row){
                    return value === '禁用' ?
                        '<span class="label label-danger">禁用</span>' :
                        '<span class="label label-success">正常</span>';
                }},
            {label: '创建时间', name: 'createTime', index: "create_time", width: 85}
        ],
        viewrecords: true,
        height: 385,
        rowNum: 10,
        rowList: [10, 30, 50],
        rownumbers: true,
        rownumWidth: 25,
        autowidth: true,
        pager: "#jqGridPager",
        jsonReader: {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        },
        prmNames: {
            page: "page",
            rows: "limit",
            order: "order"
        },
        gridComplete: function () {
            //隐藏grid底部滚动条
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
        }
    });
});

var vm = new Vue({
    el: '#rrapp',
    data: {
        q: {
            userName: null,
            roleId: null
        }
    },
    methods: {
        query: function () {
            vm.reload();
        },
        back: function (event) {
            history.go(-1);
        },
        reload: function () {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                postData: {'userName': vm.q.userName},
                page: page,
            }).trigger("reloadGrid");
        }
    }
});