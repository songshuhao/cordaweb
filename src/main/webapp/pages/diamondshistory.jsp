<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	/*
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path;*/
	String basePath = request.getContextPath();
%>
<!-- 新增的模态框，在修改中将获取一行的值放入input中，改变一些参数继续使用 -->
<div class="modal fade" id="addModal" tabindex="-1" role="dialog">
	<div class="modal-dialog modal-lg" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<h3>Diamonds History</h3>
			</div>
			<div class="modal-body">
				<table id="tableListForHistory"></table>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
			</div>
		</div>				
	</div>
</div>
<script type="text/javascript">
function DiamondsHistoryInit() {
    var oTableInit = new Object();
    //初始化Table
    var url = "<%=basePath %>/history/getDiamondsHistoryList";
    oTableInit.Init = function() {
    	$("#tableListForHistory").bootstrapTable('destroy'); 
        $('#tableListForHistory').bootstrapTable({
            url: url,
            cache:false,
            pagination: false, //分页
            search: false, //显示搜索框
            sidePagination: "client", //服务端处理分页server
            pageNumber: 1,                       //初始化加载第一页，默认第一页
            pageSize: 10,                       //每页的记录行数（*）
            pageList: [5, 10, 25, 50],          //每页的记录行数（*）
            queryParams: queryParamsHistory, //传递参数（*）
            showColumns: false,                  //是否显示所有的列
            showRefresh: false,                  //是否显示刷新按钮
            responseHandler: function(data){
                return data.rows;
            },
            columns: [
            	{
                    title: 'Number',//标题  可不加
                    width:'64px',
                    align: 'center',
                    valign: 'middle',
                    formatter: function (value, row, index) {
                        return index+1;
                    }
                },
            	{
                    title: 'tradeid',
                    field: 'tradeid',
                    align: 'center',
                    valign: 'middle',
                    visible: false,
                },
                
                {
                    title: 'Package Code',
                    field: 'basketno',
                    align: 'center',
                    valign: 'middle',
                },
                {
                    title: 'Product Code',
                    field: 'productcode',
                    align: 'center',
                    valign: 'middle',
                },
                {
                    title: 'GIA Number',
                    field: 'giano',
                    align: 'center',
                    valign: 'middle',
                },
                {
                    title: 'Shape',
                    align: 'center',
                    field: 'shape',
                    valign: 'middle',
                },
                {
                    title: 'Size',
                    field: 'size',
                    align: 'center',
                    valign: 'middle',
                },
                {
                    title: 'Clarity',
                    field: 'clarity',
                    align: 'center',
                    valign: 'middle',
                },
                {
                    title: 'Polish',
                    field: 'polish',
                    align: 'center',
                    valign: 'middle',
                },
                {
                    title: 'Symmetry',
                    field: 'symmetry',
                    align: 'center',
                    valign: 'middle',
                },
                {
                    title: 'Status',
                    field: 'statusDesc',
                    align: 'center',
                    valign: 'middle',
                }
            ]
        });
    };
    return oTableInit;
};
    
function queryParamsHistory(params) {
	return {
		limit : this.limit, // 页面大小
        offset : this.offset, // 页码
        pageNumber : this.pageNumber,
        pageSize : this.pageSize,
        giano : giaNo,
        basketno : basketNo

	};
};
</script>
