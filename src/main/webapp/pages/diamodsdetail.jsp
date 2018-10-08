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
									<input type="text" name="basketno" class="col-sm-3 form-control" id="basketno1" readonly="readonly"/>
								</div>
							</div>
							<div class="form-group" >
								<label for="productcode" class="col-sm-5 control-label">Product Code:</label>
								<div class="col-sm-3">
									<input type="text" name="productcode" class="col-sm-3 form-control" id="productcode1" readonly="readonly"/>
								</div>
							</div>
							<div class="form-group">
								<label for="diamondsNumber" class="col-sm-5 control-label">Number of Diamonds:</label>
								<div class="col-sm-3">
									<input type="text" name="diamondsNumber" class="col-sm-3 form-control" id="diamondsNumber" readonly="readonly"/>
								</div>
							</div>
							<div class="form-group">
								<label for="totalweight" class="col-sm-5 control-label">Total Weight:</label>
								<div class="col-sm-3">
									<input type="text" name="totalweight" class="col-sm-3 form-control" id="totalweight" readonly="readonly"/>
								</div>
							</div>
							<div class="form-group">
								<label for="mimweight" class="col-sm-5 control-label">Minumum Weight Diamond:</label>
								<div class="col-sm-3">
									<input type="text" name="mimweight" class="col-sm-3 form-control" id="mimweight" readonly="readonly"/>
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
            $('#tableListForDetail').bootstrapTable({
                url: "<%=basePath %>/history/getDiamondDetails",
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
                	console.log(basketInfo);
                	$("#productcode1").val(basketInfo.productcode);
                	$("#diamondsNumber").val(basketInfo.diamondsNumber);
                	$("#totalweight").val(basketInfo.totalweight);
                	$("#mimweight").val(basketInfo.mimweight);
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
</script>
