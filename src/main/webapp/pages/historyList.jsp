<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path;
%>
<jsp:include page="diamodsdetail.jsp"></jsp:include>
<jsp:include page="result.jsp"></jsp:include>
<jsp:include page="diamondshistory.jsp"></jsp:include>
	<section class="content table-content">
		<form class="form-inline" >
		<!-- 工具栏 -->
		<div id="toolbar">
		</div>
		</form>
		<div class="row" style="margin: 10px;">
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
    	//console.log("get diamond list");
        var oTableInit = new Object();
        //初始化Table
        oTableInit.Init = function() {
            $('#tableListForContacts').bootstrapTable({
                url: "<%=basePath %>/history/getDiamondList",
                pagination: true, //分页
                search: true, //显示搜索框
                //sortable: false,    //是否启用排序
                //sortName: "basketNo",
                //sortOrder: "asc",     //排序方式 
                sidePagination: "client", //服务端处理分页server
                pageNumber: 1,                       //初始化加载第一页，默认第一页
                pageSize: 10,                       //每页的记录行数（*）
                pageList: [5, 10, 25, 50],          //每页的记录行数（*）
                //contentType : "application/x-www-form-urlencoded",
                queryParams: queryParams, //传递参数（*）
                toolbar:"#toolbar",//工具栏
                showColumns: true,                  //是否显示所有的列
                showRefresh: false,                  //是否显示刷新按钮
                responseHandler: function(data){
                    return data.rows;
                },
                columns: [
                	{
                        title: 'number',//标题  可不加
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
                    },
                    {
                        title: 'GIA Number',
                        field: 'giano',
                        align: 'center',
                        valign: 'middle',
                        events: operateEvents,
                        formatter : operateFormat,
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
                    },
                    {
                        title: 'history',
                        field: 'status',
                        align: 'center',
                        valign: 'middle',
                    }
                ]
            });
        };
        return oTableInit;
    };
 
    function queryParams(params) {
    	return {
    		limit : this.limit, // 页面大小
	        offset : this.offset, // 页码
	        pageNumber : this.pageNumber,
	        pageSize : this.pageSize,
    	};
    };
    
    function search() {
    	$('#tableListForContacts').bootstrapTable('refresh');
    }
 function operateFormat(value, row, index) {
	 console.log(value);
	 if(null != value && value!='')
	 {
	 	return '<button type="button" class="btn btn-default" data-dismiss="modal" id="addBtn">'+value+'</button>';
	 };
	 return '-';
	
 }
 
 var giaNo = "";
 window.operateEvents = {
 			'click #addBtn': function(e, value, row, index) {
			giaNo = row.giano;
			var oTable = DiamondsHistoryInit();
		    oTable.Init();
			$("#addModal").modal('show');
			
 		}
 	};
</script>
