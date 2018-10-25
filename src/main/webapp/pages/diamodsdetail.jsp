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
<jsp:include page="diamondshistory.jsp"></jsp:include>
<div class="modal fade" id="detailModal" tabindex="-1" role="dialog">
	<div class="modal-dialog modal-lg" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<h3>Diamonds Detail</h3>
			</div>
			<div class="modal-body">
				<div class="form-group">
								<label for="inputBasketNo" class="col-sm-5 control-label">Package Code:</label>
								<div class="col-sm-3">
									<input type="text" name="basketno1" class="col-sm-3 form-control" id="basketno1" readonly="readonly"/>
								</div>
							</div>
							<div class="form-group" >
								<label for="productcode1" class="col-sm-5 control-label">Product Code:</label>
								<div class="col-sm-3">
									<input type="text" name="productcode1" class="col-sm-3 form-control" id="productcode1" readonly="readonly"/>
								</div>
							</div>
							<div class="form-group">
								<label for="diamondsnumber1" class="col-sm-5 control-label">Number of Diamonds:</label>
								<div class="col-sm-3">
									<input type="text" name="diamondsnumber1" class="col-sm-3 form-control" id="diamondsnumber1" readonly="readonly"/>
								</div>
							</div>
							<div class="form-group">
								<label for="totalweight1" class="col-sm-5 control-label">Total Weight:</label>
								<div class="col-sm-3">
									<input type="text" name="totalweight1" class="col-sm-3 form-control" id="totalweight1" readonly="readonly"/>
								</div>
							</div>
							<div class="form-group">
								<label for="mimweight1" class="col-sm-5 control-label">Minumum Weight Diamond:</label>
								<div class="col-sm-3">
									<input type="text" name="mimweight1" class="col-sm-3 form-control" id="mimweight1" readonly="readonly"/>
								</div>
							</div>
				<table id="tableListForDetail"></table>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
			</div>
		</div>				
	</div>
</div>
<script type="text/javascript">
    function DiamondsDetailInit() {
        var oTableInit = new Object();
        //初始化Table
        oTableInit.Init = function() {
        	$("#tableListForDetail").bootstrapTable('destroy'); 
            $('#tableListForDetail').bootstrapTable({
                url: "<%=basePath %>/history/getDiamondDetails",
                cache:false,
                //pagination: true, //分页
                //search: true, //显示搜索框
                //sortable: false,    //是否启用排序
                //sortName: "basketNo",
                //sortOrder: "asc",     //排序方式 
                sidePagination: "client", //服务端处理分页server
                pageNumber: 1,                       //初始化加载第一页，默认第一页
                pageSize: 10,                       //每页的记录行数（*）
                pageList: [5, 10, 25, 50],          //每页的记录行数（*）
                //contentType : "application/x-www-form-urlencoded",
                queryParams: queryParamsDetail, //传递参数（*）
                //toolbar:"#toolbar",//工具栏
                //showColumns: true,                  //是否显示所有的列
                //showRefresh: false,                  //是否显示刷新按钮
                responseHandler: function(data){
                	var basketInfo = JSON.parse(data.basketInfo);
                	$("#productcode1").val(basketInfo.productcode);
                	$("#diamondsnumber1").val(basketInfo.diamondsnumber);
                	$("#totalweight1").val(basketInfo.totalweight);
                	$("#mimweight1").val(basketInfo.mimweight);
                    return JSON.parse(data.rows);
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
                        title: 'Package Code',
                        field: 'basketno',
                        align: 'center',
                        valign: 'middle',
                        visible: false,
                    },
                    {
                        title: 'Product Code',
                        field: 'productcode',
                        align: 'center',
                        valign: 'middle',
                        visible: false,
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
                        visible: false,
                    },
                    {
                        title: 'GIA Number',
                        field: 'giano',
                        align: 'center',
                        valign: 'middle',
                        events: operateEventsGiaNo,
                        formatter : operateFormatGiaNo,
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
    
    function queryParamsDetail(params) {
    	return {
    		limit : this.limit, // 页面大小
	        offset : this.offset, // 页码
	        pageNumber : this.pageNumber,
	        pageSize : this.pageSize,
	        basketno:$("#basketno1").val(),
    	};
    };
    
    function operateFormatpackageno(value, row, index) {
    	return '<button type="button" class="btn btn-default" data-dismiss="modal" id="detail">'+value+'</button>';
    }
    
    
    //binding event shuhao.song
    window.operateEventspackageno = {
    			'click #detail': function(e, value, row, index) {
   			$("#basketno1").val(row.basketno);
   			$("#detailModal").modal('show');
   		 	var oTableInit = DiamondsDetailInit();
   		 	oTableInit.Init();
   			
    		}
    	};
    
    function operateFormatGiaNo(value, row, index) {
   	 //console.log(value);
   	 if(null != value && value!='')
   	 {
   	 	return '<button type="button" class="btn btn-default" data-dismiss="modal" id="addBtn">'+value+'</button>';
   	 };
   	 return;
   	
    }
    
    var giaNo = "";
    var basketNo = "";
    
    window.operateEventsGiaNo = {
    			'click #addBtn': function(e, value, row, index) {
   			giaNo = row.giano;
   			basketNo = row.basketno;
   			var oTable = DiamondsHistoryInit();
   		    oTable.Init();
   			$("#addModal").modal('show');
   			
    		}
    	};
</script>
