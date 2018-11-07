<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
		<input type="button" value="Import" id="importBtn" data-toggle="modal" class="btn btn-primary" onclick="openImport()"></input>
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

<!-- 新增篮子的模态框，在修改用户中将获取一行的值放入input中，改变一些参数继续使用 -->
		<div class="modal fade" id="addModal" tabindex="-1" role="dialog">
			<div class="modal-dialog" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<h3>Move Diamonds</h3>
					</div>
					<div class="modal-body">
						<form id="addForm" action="" method="post" class="form-horizontal required-validate">
							<input type="hidden" id="userid" name="userid" value="${userInfo.userId}"/>
							<input type="hidden" id="seqNo" name="seqNo"/>
							<div class="form-group">
								<label for="basketno" class="col-sm-4 control-label">Package Code:</label>
								<div class="col-sm-6">
									<input type="text" name="basketno" class="form-control" id="basketno" placeholder="Package Code" readonly="readonly"/>
								</div>
							</div>
							<div class="form-group">
								<label for="result" class="col-sm-4 control-label">Re-verification Status:</label>
								<div class="col-sm-6">
									<input type="text" name="result" class="form-control" id="result" placeholder="result" readonly="readonly"/>
								</div>
							</div>
							<div class="form-group">
								<label for="vault" class="col-sm-4 control-label">Vault:</label>
								<div class="col-sm-6">
									<select id="vault" class="form-control" name="vault">
									      <c:forEach items="${vaultMap }" var="vault">
											<option value="${vault.value }">
												${vault.key }
											</option>
										</c:forEach>
								    </select>
								</div>
							</div>
							<div class="form-group">
								<label for="owner" class="col-sm-4 control-label">Owner ID:</label>
								<div class="col-sm-6">
									<input type="text" name="owner" class="form-control" id="owner" placeholder="Owner ID:" data-bv-notempty/>
								</div>
							</div>
							
						</form>
					</div>
					<div class="modal-footer">
						<button type="button" id="conf" class="btn btn-primary" onclick="add()">Add</button>
						<button type="button" class="btn btn-default" data-dismiss="modal" onclick="resetAddModal()">Cancel</button>
					</div>
				</div>				
			</div>
		</div>
			
<script type="text/javascript">
    $(function(){
       var oTable = TableInit();
       oTable.Init();
       formValidate();
       importValidate();
       $("form.required-validate").each(function() {
           var $form = $(this);
           // 修复bootstrap validator重复向服务端提交bug
           $form.on('success.form.bv', function(e) {
               // Prevent form submission
               e.preventDefault();
           });


       });
    });
 
    function TableInit() {
        var oTableInit = new Object();
        //初始化Table
        oTableInit.Init = function() {
            $('#tableListForData').bootstrapTable({
                url: "<%=basePath %>/move/getBasketList",
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
                        title: 'Re-verification Status',
                        field: 'result',
                        align: 'center',
                        valign: 'middle',
                    },
                    {
                        title: 'Vault',
                        field: 'vault',
                        align: 'center',
                        valign: 'middle',
                    },
                    {
                        title: 'Owner ID',
                        field: 'owner',
                        align: 'center',
                        valign: 'middle',
                    },{
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
			$("#basketno").val(row.basketno);
			$("#result").val(row.result);
			$("#seqNo").val(row.seqNo);
			$("#conf").text("Add");
   		},	
   		'click #modifyBtn': function(e, value, row, index) {
			resetAddModal();
			$("#basketno").val(row.basketno);
			$("#vault").attr("value",row.vault);
			$("#result").val(row.result);
			$("#owner").val(row.owner);
			
			$("#seqNo").val(row.seqNo);
			$("#conf").text("Modify");
   		}
   	};
    
    function operateFormat(value, row, index) {
   	 var status = value;
   	 var result = row.result;
   	 ////console.log(status);
   	 if(status=='8'){
   		 return '<input type="button" value="Modify" id="modifyBtn" data-toggle="modal" data-target="#addModal" class="btn btn-primary"></input>';
   	 }else if(status=='7' && result=='verified'){
   		 return '<input type="button" value="Add" id="addBtn" data-toggle="modal" data-target="#addModal" class="btn btn-primary"></input>';
   	}
    }
    
    var step = "atv";//aoc to valut
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
    	var index = layer.load();
    	var url = "<%=basePath %>/move/submitBasketList";
    	var param = {"step":step};
    	$.ajax({
			url:url,
			method:"post",
			data:param,
			dataType:"json",
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
    
  //点击取消后清空表单中已写信息
	function resetAddModal(){
		document.getElementById("addForm").reset();
	}
	
	//Modal验证销毁重构
    $('#addModal').on('hidden.bs.modal', function() {
    	$("#addForm").data('bootstrapValidator').destroy();
    	$('#addForm').data('bootstrapValidator',null);
    	formValidate();
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
		//debugger;
		$("#conf").attr("onclick","add()");
		$.ajax({
			url:"<%=basePath %>/move/updateBasketInfo",
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
	
	function formValidate()
	 {
		 var addForm = $("#addForm");
		 addForm.bootstrapValidator({//根据自己的formid进行更改
           message: 'This value is not valid',//默认提示信息
           feedbackIcons: {//提示图标
               valid: 'glyphicon glyphicon-ok',
               invalid: 'glyphicon glyphicon-remove',
               validating: 'glyphicon glyphicon-refresh'
           },
           fields: {
        	   owner: {//名称校验
                      message: 'This value is not valid',
                      validators: {//验证条件
                          /* notEmpty: {
                              message: '附属品名称不能为空'
                          }, */
                          stringLength: {
                              min: 1,
                              max: 20,
                              message: 'Max length 20!'
                          }
                      }
                  }
           },
       });
	 }

</script>
