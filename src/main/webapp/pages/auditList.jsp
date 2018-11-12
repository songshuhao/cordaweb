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
<jsp:include page="common.jsp"></jsp:include>
<section class="content table-content">
	<form class="form-inline" >
	<!-- 工具栏 -->
	<div id="toolbar">
		<!-- <input type="button" value="Import" id="importBtn" data-toggle="modal" class="btn btn-primary" onclick="openImport()"></input> -->
		<input type="button" value="Submit" id="submitBtn" data-toggle="modal" data-target="#submitModal" class="btn btn-primary" onclick="submit()"></input>
	</div>
	<!-- bootstrapTable -->
	</form>
	<div class="row">
		 <div class="col-md-12">
		   <table id="tableListForData"></table>
		 </div>
	</div>
</section>
<script type="text/javascript">
    $(function(){
    	importValidate();
    	$("form.required-validate").each(function() {
            var $form = $(this);
            // 修复bootstrap validator重复向服务端提交bug
            $form.on('success.form.bv', function(e) {
                // Prevent form submission
                e.preventDefault();
            });


        });
       var oTable = TableInit();
       oTable.Init();
    });
 
    function TableInit() {
        var oTableInit = new Object();
        //初始化Table
        oTableInit.Init = function() {
            $('#tableListForData').bootstrapTable({
                url: "<%=basePath %>/audit/getBasketList",
                cache:false,
                pagination: true, //分页
                search: true, //显示搜索框
                sortable: true,    //是否启用排序
                sortOrder: "desc",     //排序方式 
                sortName: "basketno",
                sidePagination: "client", //服务端处理分页server
                pageNumber: 1,                       //初始化加载第一页，默认第一页
                pageSize: 10,                       //每页的记录行数（*）
                pageList: [5, 10, 25, 50],          //每页的记录行数（*）
                //contentType : "application/x-www-form-urlencoded",
                queryParams: queryParams, //传递参数（*）
                toolbar:"#toolbar",//工具栏
                showColumns: true,                  //是否显示所有的列
                showRefresh: true,                  //是否显示刷新按钮
                responseHandler: function(data){
                	////console.log(data);
                    return data.rows;
                },
                /**导出*/
                showExport: true,
                exportTypes :[ 'excel'],
                columns: [
                	{
        				checkbox:"true",
        				field : "box"
        			},
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
                        sortable: true,    //是否启用排序
                        sortOrder: "desc",     //排序方式 
                        events: operateEventspackageno,
                        formatter : operateFormatpackageno,
                    },
                    {
                        title: 'Valut',
                        field: 'vault',
                        align: 'center',
                        valign: 'middle',
                    },
                    {
                        title: 'Inventory Box',
                        field: 'invtymgr',
                        align: 'center',
                        valign: 'middle',
                    },
                    {
                        title: 'Sealed Bag No',
                        field: 'sealedbagno',
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
 
    var step = "atau";//aoc to audit
    function queryParams(params) {
    	return {
    		limit : this.limit, // 页面大小
	        offset : this.offset, // 页码
	        pageNumber : this.pageNumber,
	        pageSize : this.pageSize,
	        step:step
    	};
    };
    
    function search() {
    	$('#tableListForData').bootstrapTable('refresh');
    }
    
    function submit()
    {
    	var selectLsit= $("#tableListForData").bootstrapTable('getSelections');  
        if(selectLsit.length<=0){  
        	var data = {"state":"fail","message":"Please select one or more data"};
        	messageShow(data,null,false);
       }else
   	   {
    	   
    	   var index = layer.load();
    	   var param = {"packageInfos":selectLsit,"step":step};
    	   param = JSON.stringify(param);
    	   var url = "<%=basePath %>/audit/submitBasketList";
	       	$.ajax({
	   			url:url,
	   			method:"post",
	   			data:param,
	   			dataType:"json",
	   			contentType: 'application/json;charset=UTF-8',
	   			success:function(data){
					layer.close(index);
					if(data.state=="success"){
						messageShow(data,null,true)
					}
					if(data.state=="fail"){
						messageShow(data,null,true)
					}
				},
				error:function(){
					layer.close(index);
					messageShow(null,null,true)
				}
	   		});
   	   }
    	
    }
</script>
