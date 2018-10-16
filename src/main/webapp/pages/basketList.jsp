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
<jsp:include page="result.jsp"></jsp:include>
<section class="content table-content">
	<form class="form-inline" >
	<!-- 工具栏 -->
	<div id="toolbar">
			<input type="button" value="Add" id="addBtn" data-toggle="modal" data-target="#addModal" class="btn btn-primary"></input>
			<input type="button" value="Import" id="importBtn" data-toggle="modal" class="btn btn-primary" onclick="openImport()"></input>
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
						<h3>Add PackageInfo</h3>
					</div>
					<div class="modal-body">
						<form id="addForm" action="<%=basePath %>/basket/addBasketInfo" method="post" class="form-horizontal required-validate">
							<input type="hidden" id="userid" name="userid" value="${userInfo.userId}"/>
							<input type="hidden" id="seqNo" name="seqNo"/>
							<div class="form-group">
								<label for="inputBasketNo" class="col-sm-5 control-label">Package Code:</label>
								<div class="col-sm-5">
									<input type="text" name="basketno" class="form-control" id="basketno" placeholder="Package Code" data-bv-notempty/>
								</div>
							</div>
							<div class="form-group" >
								<label for="inputProduct" class="col-sm-5 control-label">Product Code:</label>
								<div class="col-sm-5">
									<select id="productcode" class="form-control" name="productcode">
								      <c:forEach items="${productList }" var="product">
											<option value="${product.productcode }">
												${product.productcode }
											</option>
										</c:forEach>
								    </select>
								</div>
							</div>
							<div class="form-group">
								<label for="inputName" class="col-sm-5 control-label">Supplier:</label>
								<div class="col-sm-5">
									<select id="suppliercode" class="form-control" name="suppliercode">
									  <c:forEach items="${supplierMap }" var="supplier">
											<option value="${supplier.key }">
												${supplier.key }
											</option>
										</c:forEach>
								      </select>
								</div>
							</div>
							<div class="form-group">
								<label for="diamondsnumber" class="col-sm-5 control-label">Number of Diamonds:</label>
								<div class="col-sm-5">
									<input type="text" name="diamondsnumber" class="col-sm-4 form-control" id="diamondsnumber" placeholder="Number of Diamonds:" data-bv-notempty/>
								</div>
							</div>
							<div class="form-group">
								<label for="totalWeight" class="col-sm-5 control-label">Total Weight:</label>
								<div class="col-sm-5">
									<input type="text" name="totalweight" class="col-sm-4 form-control" id="totalweight" placeholder="Total Weight" data-bv-notempty/>
								</div>
							</div>
							<div class="form-group">
								<label for="inputmimWeight" class="col-sm-5 control-label">Minumum Weight Diamond:</label>
								<div class="col-sm-5">
									<input type="text" name="mimweight" class="col-sm-4 form-control" id="mimweight" placeholder="Minumum Weight Diamond" data-bv-notempty/>
								</div>
							</div>
						</form>
					</div>
					<div class="modal-footer">
						<button type="button" id="conf" class="btn btn-primary" onclick="add()"><!-- <span class="glyphicon glyphicon-floppy-disk"></span> -->Add</button>
						<button type="button" class="btn btn-default" data-dismiss="modal"><!-- <span class="glyphicon glyphicon-remove" aria-hidden="true"></span> -->Cancel</button>
					</div>
				</div>				
			</div>
		</div>
		
		<!-- 上传篮子的模态框 -->
		<div class="modal fade" id="importModal" tabindex="-1" role="dialog">
			<div class="modal-dialog" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<h3>Import PackageInfo</h3>
					</div>
					<div class="modal-body">
						<form id="importForm" action="<%=basePath %>/basket/importBasketInfo" class="form-horizontal required-validate" method="post" enctype="multipart/form-data">
							<div class="form-group">
								<label for="upfile" class="col-sm-4 control-label">Package File:</label>
								<div class="col-sm-6">
									<input type="file" name="files" class="form-control" id="upfile" data-bv-notempty/>
								</div>
							</div>
						</form>
					</div>
					<div class="modal-footer">
						<input type="button" id="importCsv" onclick="importCsv()" class="btn btn-primary" value="Add" />  
						<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
					</div>
				</div>				
			</div>
		</div>
		
		
<script type="text/javascript">
    $(function(){
       var oTable = TableInit();
       oTable.Init();
       formValidate();
       $("form.required-validate").each(function() {
           // 修复bootstrap validator重复向服务端提交bug
           var $form = $(this);
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
            $('#tableListForContacts').bootstrapTable({
                url: "<%=basePath %>/basket/getBasketList",
                pagination: true, //分页
                search: true, //显示搜索框
                //sortable: false,    //是否启用排序
                //sortName: "basketno",
                //sortOrder: "asc",     //排序方式 
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
                        title: 'Supplier',
                        field: 'suppliercode',
                        align: 'center',
                        valign: 'middle',
                    },
                    {
                        title: 'Num of Diamonds',
                        field: 'diamondsnumber',
                        align: 'center',
                        valign: 'middle',
                    },
                    {
                        title: 'Total Weight',
                        field: 'totalweight',
                        align: 'center',
                        valign: 'middle',
                    },
                    {
                        title: 'Min Diamond',
                        field: 'mimweight',
                        align: 'center',
                        valign: 'middle',
                    },
                    {
                        title: 'Status',
                        field: 'statusDesc',
                        align: 'center',
                        valign: 'middle',
                        visible: true,
                    } ,
                    {
                        title: 'Operation',
                        field: 'status',
                        align: 'center',
                        valign: 'middle',
                        visible: true,
                        events: operateEvents,
                        formatter : operateFormat,
                    }
                ]
            });
        };
        return oTableInit;
    };
 
  //Modal验证销毁重构
    $('#addModal').on('hidden.bs.modal', function() {
    	document.getElementById("addForm").reset();
    	$("#addForm").data('bootstrapValidator').destroy();
    	$('#addForm').data('bootstrapValidator',null);
    	formValidate();
    	/*非自定义情况 $('#addForm').bootstrapValidator(); */
    });
	
  //Modal验证销毁重构
    $('#importModal').on('hidden.bs.modal', function() {
    	document.getElementById("importForm").reset();
    	$("#importForm").data('bootstrapValidator').destroy();
    	$('#importForm').data('bootstrapValidator',null);
    	$('#importForm').bootstrapValidator();
    	
    });
    
    function queryParams(params) {
    	return {
    		limit : this.limit, // 页面大小
	        offset : this.offset, // 页码
	        pageNumber : this.pageNumber,
	        pageSize : this.pageSize

    	};
    };
    
    function search() {
    	var url = "<%=basePath %>/basket/getBasketList";
    	$('#tableListForContacts').bootstrapTable('refresh', {url: url});
    }
    
    function openImport()
    {
    	$("#importModal").modal('show');
    }
    
    function importCsv()
    {
    	var $form = $("#importForm");

        var data = $form.data('bootstrapValidator');
        if (data) {
        // 修复记忆的组件不验证
            data.validate();

            if (!data.isValid()) {
                return false;
            }
        }
        var index = layer.load();
    	var formData = new FormData($("#importForm")[0]);
        $.ajax({
            //接口地址
            url: '<%=basePath %>/basket/importBasketInfo' ,
            type: 'POST',
            dataType:'json',
            data: formData,
            //async: false,
            cache: false,
            contentType: false,
            processData: false,
            success:function(data){
          	  	layer.close(index);
          	  	
 				if(data.state=="success"){
 					messageShow(data,"#importModal",false)
 				}
 				if(data.state=="fail"){
 					messageShow(data,"#importModal",false)
 				}
 			},
 			error:function(){
 				layer.close(index);
 				messageShow(null,"#importModal",false)
 			}
        });
    }
    
    function submit()
    {
    	var url = "<%=basePath %>/basket/submitBasketList";
    	var index = layer.load();
    	$.ajax({
			url:url,
			method:"post",
			//data:param,
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
		//debugger;
		$.ajax({
			url:"<%=basePath %>/basket/addBasketInfo",
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
	
	
	 function operateFormat(value, row, index) {
		 var status = value;
		 //console.log(status);
		  if(status=='0'){
			 return '<input type="button" value="Modify" id="modifyBtn" data-toggle="modal" data-target="#addModal" class="btn btn-primary"></input>';
		 }
	 }
	 
	 
	 //binding event shuhao.song
	 window.operateEvents = {
 			'click #modifyBtn': function(e, value, row, index) {
	 			$("#suppliercode").attr("value",row.suppliercode);
				$("#basketno").val(row.basketno);
				$("#diamondsnumber").val(row.diamondsnumber);
				$("#totalweight").val(row.totalweight);
				$("#mimweight").val(row.mimweight);
				$(".modal-header > h3").text("Modify PackageInfo");
				$("#conf").text("Modify");
				$("#seqNo").val(row.seqNo);				
	 		},
	 		'click #addBtn': function(e, value, row, index) {
	 			$(".modal-header > h3").text("Add PackageInfo");
				$("#conf").text("Add");
	 		}
	 	};
	 
	 function formValidate()
	 {
		 var addForm = $("#addForm");
		 addForm.bootstrapValidator({//根据自己的formid进行更改
			 //live: 'disabled',验证时机，enabled是内容有变化就验证（默认），disabled和submitted是提交再验证
             message: 'This value is not valid',//默认提示信息
             feedbackIcons: {//提示图标
                 valid: 'glyphicon glyphicon-ok',
                 invalid: 'glyphicon glyphicon-remove',
                 validating: 'glyphicon glyphicon-refresh'
             },
             fields: {
           		 basketno: {//名称校验
                        message: 'This value is not valid',
                        validators: {//验证条件
                            /* notEmpty: {
                                message: '附属品名称不能为空'
                            }, */
                            stringLength: {
                                min: 1,
                                max: 12,
                                message: 'Max length 12!'
                            },regexp: {//自定义校验
                                regexp: /^[A-Za-z0-9]+$/,//匹配由数字和26个英文字母组成的字符串
                                message: 'Value should be number and letter!'
                            }
                        }
                    },
          	   diamondsnumber: {//名称校验
                     message: 'This value is not valid',
                     validators: {//验证条件
                         /* notEmpty: {
                             message: '附属品名称不能为空'
                         }, */
                         stringLength: {
                             min: 1,
                             max: 10,
                             message: 'Max length 10!'
                         },regexp: {//自定义校验
                             regexp: /^[+]{0,1}(\d+)$|^[+]{0,1}(\d+\.\d+)$/,//>0的数字
                             message: 'Value should be number and bigger than 0!'
                         },
                         callback: {//自定义，可以在这里与其他输入项联动校验/
                        	 //联动校验，修改此值会联动校验mimnumber
                             callback:function(value, validator){
                            	 var mimweight = $("#mimweight").val();
                            	 if(mimweight !=null && mimweight !='')
                            	 {
                            		 addForm.data('bootstrapValidator')
                                	 .updateStatus('mimweight', 'NOT_VALIDATED',null)
                                	 .validateField('mimweight');
                            	 }
                            	 return true;
                             }
                         }
                     }
                 },
                 totalweight: {
              	   message: 'This value is not valid',
                     validators: {//验证条件
                         /* notEmpty: {
                             message: '附属品名称不能为空'
                         }, */
                         stringLength: {
                             min: 1,
                             max: 10,
                             message: 'Max length 10!'
                         },regexp: {//自定义校验
                             regexp: /^[+]{0,1}(\d+)$|^[+]{0,1}(\d+\.\d+)$/,//
                             message: 'Value should be number and bigger than 0!'
                         },callback: {//自定义，可以在这里与其他输入项联动校验/
                        	 //联动校验，修改此值会联动校验mimnumber
                             callback:function(value, validator){
                            	 var mimweight = $("#mimweight").val();
                            	 if(mimweight !=null && mimweight !='')
                            	 {
                            		 addForm.data('bootstrapValidator')
                                	 .updateStatus('mimweight', 'NOT_VALIDATED',null)
                                	 .validateField('mimweight');
                            	 }
                            	 return true;
                             }
                         }
                     }
                 },
                 mimweight: {
              	   message: 'This value is not valid',
                     validators: {//
                         /* notEmpty: {
                             message: '附属品名称不能为空'
                         }, */
                         stringLength: {
                             min: 1,
                             max: 10,
                             message: 'Max length 10!'
                         },regexp: {//自定义校验
                             regexp: /^[+]{0,1}(\d+)$|^[+]{0,1}(\d+\.\d+)$/,
                             message: 'Value should be number and bigger than 0!'
                         },
                         callback: {//自定义，可以在这里与其他输入项联动校验/
                        	 message: 'total weight>=mimweight*diamondsnumber',
                             callback:function(value, validator){
                            	 var totalweight = $("#totalweight").val();
                            	 var diamondsnumber = $("#diamondsnumber").val();
                            	 console.log("totalweight:" + totalweight);
                            	 console.log("diamondsnumber:" + diamondsnumber);
                            	 if(value > totalweight)
                            	 {
                            		 return false; 
                            	 }else if(value*diamondsnumber > totalweight)
                            	 {
                            		return false;	 
                            	 }else
                            	 {
                            		 return true;
                            	 }
                             }
                         }
                     }
                 },
             },
         });
		 
		 var importForm = $("#importForm");
		 importForm.bootstrapValidator();
	 }
</script>
