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
<jsp:include page="diamodsdetail.jsp"></jsp:include>
<jsp:include page="result.jsp"></jsp:include>
	<section class="content table-content">
		<form class="form-inline" >
		<!-- 工具栏 -->
		<div id="toolbar">
		</div>
		</form>
		<div class="row">
			 <div class="col-md-12">
			   <table id="tableListForContacts"></table>
			 </div>
		</div>
	</section>
<script type="text/javascript">

    $(function(){
       var oTable = TableInit();
       oTable.Init();
    });
 
    function TableInit() {
    	////console.log("get diamond list");
        var oTableInit = new Object();
        //初始化Table
        oTableInit.Init = function() {
            $('#tableListForContacts').bootstrapTable({
                url: "<%=basePath %>/history/getDiamondList",
                cache:false,
                pagination: true, //分页
                search: true, //显示搜索框
                sortable: true,    //是否启用排序
                sortName: "basketno",
                sortOrder: "desc",     //排序方式 
                sidePagination: "server", //服务端处理分页server,client
                pageNumber: 1,                       //初始化加载第一页，默认第一页
                pageSize: 10,                       //每页的记录行数（*）
                pageList: [5, 10, 25, 50],          //每页的记录行数（*）
                //contentType : "application/x-www-form-urlencoded",
                queryParams: queryParams, //传递参数（*）
                toolbar:"#toolbar",//工具栏
                showColumns: false,                  //是否显示所有的列
                showRefresh: true,                  //是否显示刷新按钮
                /* responseHandler: function(data){
                	//console.log(data);
                    return data.rows;
                }, */
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
                        title: 'Package Code',
                        field: 'basketno',
                        align: 'center',
                        valign: 'middle',
                        sortable: true,
                        sortOrder: "desc",
                        events: operateEventspackageno,
                        formatter : operateFormatpackageno,
                    },
                    {
                        title: 'Product Code',
                        field: 'productcode',
                        align: 'center',
                        valign: 'middle',
                    },
                    {
                        title: 'Supplier Code',
                        field: 'suppliercode',
                        align: 'center',
                        valign: 'middle',
                    },
                    {
                        title: 'Supplier Name',
                        field: 'suppliername',
                        align: 'center',
                        valign: 'middle',
                        visible:false,
                    },
                    {
                        title: 'Diamonds Number',
                        field: 'diamondsnumber',
                        align: 'center',
                        valign: 'middle',
                    },
                    /* {
                        title: 'GIA Number',
                        field: 'giano',
                        align: 'center',
                        valign: 'middle',
                        visible:false,
                        //events: operateEvents,
                        //formatter : operateFormat,
                    },
                    {
                        title: 'Shape',
                        align: 'center',
                        field: 'shape',
                        valign: 'middle',
                        visible:false,
                    },
                    {
                        title: 'Size',
                        field: 'size',
                        align: 'center',
                        valign: 'middle',
                        visible:false,
                       
                    },
                    {
                        title: 'Clarity',
                        field: 'clarity',
                        align: 'center',
                        valign: 'middle',
                        visible:false,
                    },
                    {
                        title: 'Polish',
                        field: 'polish',
                        align: 'center',
                        valign: 'middle',
                        visible:false,
                    },
                    {
                        title: 'Symmetry',
                        field: 'symmetry',
                        align: 'center',
                        valign: 'middle',
                        visible:false,
                    }, */
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
 
    function queryParams(params) {
    	//console.log(params);
    	return {
    		limit : params.limit, // 每页记录数5,10.15....
	        offset : params.offset, // 从第几条记录开始,第0页，第1页
	        pageNumber : this.pageNumber,
	        pageSize : this.pageSize,
	        order: params.order,//排序
	        sort: params.sort,//排序列名
	        search:params.search,//查询条件
    	};
    };
    
    function search() {
    	$('#tableListForContacts').bootstrapTable('refresh');
    }

</script>
