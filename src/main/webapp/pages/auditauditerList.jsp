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
<!-- 					<input type="button" value="Import" id="importBtn" data-toggle="modal" class="btn btn-primary" onclick="openImport()"></input> -->
			<input type="button" value="Submit" id="submitBtn" data-toggle="modal" data-target="#submitModal" class="btn btn-primary" onclick="submit()"></input>
	</div>
	<!-- bootstrapTable -->
	</form>
	<div class="row">
		 <div class="col-md-12">
		   <table id="tableListForContacts"></table>
		 </div>
	</div>
</section>

<!-- 新增篮子的模态框，在修改用户中将获取一行的值放入input中，改变一些参数继续使用 -->
		<div class="modal fade" id="addModal" tabindex="-1" role="dialog">
			<div class="modal-dialog" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<h3>Audit Diamonds</h3>
					</div>
					<div class="modal-body">
						<form id="addForm" action="" method="post" class="form-horizontal required-validate">
							<input type="hidden" id="userid" name="userid" value="${userInfo.userId}"/>
							<input type="hidden" id="seqNo" name="seqNo"/>
							<div class="form-group">
								<label for="basketno" class="col-sm-2 control-label">Package Code:</label>
								<div class="col-sm-7">
									<input type="text" name="basketno" class="form-control" id="basketno" placeholder="Basket Code" readonly="readonly"/>
								</div>
							</div>
							<div class="form-group">
								<label for="result" class="col-sm-2 control-label">Result:</label>
								<div class="col-sm-7">
									<select id="result" class="form-control" name="result">
								      <option value="verified">audit-verified</option>
								      <option value="failure">audit-failure</option>
								      </select>
								</div>
							</div>
							<div class="form-group">
								<label for="auditdate" class="col-sm-2 control-label">Audit Date:</label>
								<div class="col-sm-7">
									<input type="text" name="auditdate" class="form-control" id="auditdate" placeholder="Audit Date" data-bv-notempty/>
								</div>
							</div>
						</form>
					</div>
					<div class="modal-footer">
						<button type="button" id="conf" class="btn btn-primary" onclick="add()" >Add</button>
						<button type="button" class="btn btn-default" data-dismiss="modal" onclick="resetAddModal()">Cancel</button>
					</div>
				</div>				
			</div>
		</div>
			
<script type="text/javascript">
    $(function(){
       var oTable = TableInit();
       oTable.Init();
       
       $("form.required-validate").each(function() {
           var $form = $(this);
           $form.bootstrapValidator();

           $form.on('success.form.bv', function(e) {
               // Prevent form submission
               e.preventDefault();
           });


       });
    });
    
    laydate.render({
  	   elem: '#auditdate',
       });
 
    function TableInit() {
        var oTableInit = new Object();
        //初始化Table
        oTableInit.Init = function() {
            $('#tableListForContacts').bootstrapTable({
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
                        sortable: true,    //是否启用排序
                        sortOrder: "desc",     //排序方式 
                        align: 'center',
                        valign: 'middle',
                        events: operateEventspackageno,
                        formatter : operateFormatpackageno,
                    },
                    {
                        title: 'vault',
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
                        title: 'Audit Date',
                        field: 'auditdate',
                        align: 'center',
                        valign: 'middle',
                    },
                    {
                        title: 'Status',
                        field: 'statusDesc',
                        align: 'center',
                        valign: 'middle',
                        visible: true,
                    },
                    {
                        title: 'Operation',
                        field: 'status',
                        align: 'center',
                        valign: 'middle',
                        events: operateEvents,
                        formatter : operateFormat,
                    }
                ]
            });
        };
        return oTableInit;
    };
 
    
    //binding event shuhao.song
    window.operateEvents = {
		'click #addBtn': function(e, value, row, index) {
			resetAddModal();
			//console.log(row);
			$("#basketno").val(row.basketno);
			$("#auditdate").val(row.auditdate);
			$("#result").attr("value",row.result);
			$("#seqNo").val(row.seqNo);
			$("#conf").text("Add");
   		},	
   		'click #modifyBtn': function(e, value, row, index) {
			resetAddModal();
			//console.log(row);
			$$("#basketno").val(row.basketno);
			$("#auditdate").val(row.auditdate);
			$("#result").attr("value",row.result);
			
			$("#seqNo").val(row.seqNo);
			$("#conf").text("Modify");
   		}
   	};
    
    function operateFormat(value, row, index) {
	   	 var status = value;
	   	 var result = row.result;
	   	 ////console.log(status);
	   	 if(status=='13'){
	   		 return '<input type="button" value="Modify" id="modifyBtn" data-toggle="modal" data-target="#addModal" class="btn btn-primary"></input>';
	   	 }else if(status=='12'){
	   		 return '<input type="button" value="Audit" id="addBtn" data-toggle="modal" data-target="#addModal" class="btn btn-primary"></input>';
   		}
    }
    
    var step = "auta";//audit to aoc
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
    	$('#tableListForContacts').bootstrapTable('refresh');
    }
    
    function submit()
    {
    	var selectLsit= $("#tableListForContacts").bootstrapTable('getSelections');  
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
    
  //点击取消后清空表单中已写信息
	function resetAddModal(){
		document.getElementById("addForm").reset();
	}
	
	//Modal验证销毁重构
    $('#addModal').on('hidden.bs.modal', function() {
    	$("#addForm").data('bootstrapValidator').destroy();
    	$('#addForm').data('bootstrapValidator',null);
    	$('#addForm').bootstrapValidator();
    });
	
	//新增用户
	function add(){
		
		var $form = $("#addForm");

        var data = $form.data('bootstrapValidator');
        if (data) {
        // 修复记忆的组件不验证
            data.validate();
            if (!data.isValid()) {
                return false;
            }
        }
		
        var index = layer.load();
		var param = $("#addForm").serializeArray();
		param.push({"name":"step","value":step});
		$.ajax({
			url:"<%=basePath %>/audit/updateBasketInfo",
			method:"post",
			data:param,
			dataType:"json",
			success:function(data){
				layer.close(index);
				if(data.state=="success"){
					messageShow(data,"#addModal",false)
				}
				if(data.state=="fail"){
					messageShow(data,"#addModal",false)
				}
			},
			error:function(){
				layer.close(index);
				messageShow(null,"#addModal",false)
			}
		});
	}

</script>
