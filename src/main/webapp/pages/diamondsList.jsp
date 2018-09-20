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
	<section class="content table-content">
		<form class="form-inline" >
		<!-- 工具栏 -->
		<div id="toolbar">
				<input type="button" value="Import" id="importBtn" data-toggle="modal" class="btn btn-primary" onclick="openImport()"></input>
				<input type="button" value="Submit" id="submitBtn" data-toggle="modal" data-target="#submitModal" class="btn btn-primary" onclick="submit()"></input>
		</div>
		<!-- bootstrapTable -->
		</form>
		<div class="row" style="margin: 10px;">
			 <div class="col-md-12">
			   <table id="tableListForContacts"></table>
			 </div>
		</div>
	</section>

<!-- 新增的模态框，在修改中将获取一行的值放入input中，改变一些参数继续使用 -->
		<div class="modal fade" id="addModal" tabindex="-1" role="dialog">
			<div class="modal-dialog" role="document" style="width:50%">
				<div class="modal-content">
					<div class="modal-header">
						<h3>Add DiamondsInfo</h3>
					</div>
					<div class="modal-body">
						<form id="addForm" action="<%=basePath %>/diamonds/addDiamondsInfo" method="post" class="form-horizontal required-validate">
								<input type="hidden" id="supcode" name="supcode" />
								<input type="hidden" name="tradeid" id="tradeid" />
							<table class="table">
								<tr>
									<td>Package Code:</td>
									<td>
									<select id="basketno" class="form-control col-sm-3" name="basketno">
								     <c:forEach items="${basketMap }" var="basket">
											<option value="${basket.key }">
												${basket.key }
											</option>
										</c:forEach>
								    </select>
								    </td>
									<td>origin:</td>
									<td><div class="form-group"><input  type="text" id="origin" name="origin" /></div></td>
								</tr>
								<tr>
									<td>Product Code:</td>
									<td><div class="form-group"><input type="text" name="productcode" id="productcode" class="form-control" style="width: 180px;" readonly="readonly"></div></td>
									<td>Mining Date:</td>
									<td><div class="form-group"><input  type="text" id="minedate" name="minedate" /></div></td>
								</tr>
								<tr>
									<td>GIA Number:</td>
									<td><div class="form-group"><input type="text" id="giano" name="giano" data-bv-notempty></div></td>
									<td>Cutter:</td>
									<td><div class="form-group"><input  type="text" id="cutter" name="cutter"></div></td>
								</tr>
								<tr>
									<td>Size:</td>
									<td><div class="form-group"><input type="text" id="size" name="size" data-bv-notempty></div></td>
									<td>Craftman:</td>
									<td><input type="text" id="craftsmanname" name="craftsmanname"></td>
								</tr>
								<tr>
									<td>Shape:</td>
									<td>
									<select id="shape" class="form-control" name="shape">
								      <option value="BR" selected = "selected">BR</option>
								     </select>
								    </td>
									<td>Craftman Date:</td>
									<td><input type="text" id="craftsmandate" name="craftsmandate"></td>
								</tr>
								
								<tr>
									<td>Color:</td>
									<td>
									<select id="color" class="form-control" name="color">
								      <option value="D" selected = "selected">D</option>
								     </select>
								    </td>
									<td>Dealer:</td>
									<td><input type="text" id="dealername" name="dealername"></td>
								</tr>
								
								<tr>
									<td>Clarity:</td>
									<td>
										<input type="text" id="clarity" name="clarity" value="IF">
								    </td>
									<td>Deal Date:</td>
									<td><input type="text" id="dealerdate" name="dealerdate"></td>
								</tr>
								
								<tr>
									<td>Cut:</td>
									<td>
										<input type="text" id="cut" name="cut" value="EX">
								    </td>
									<td>Re-verification:</td>
									<td><input type="text" id="reverification" name="reverification"></td>
								</tr>
								
								<tr>
									<td>Polish:</td>
									<td>
										<input type="text" id="polish" name="polish" value="EX">
								    </td>
									<td>Remark1：</td>
									<td><input type="text" id="remark1" name="remark1"></td>
								</tr>
								
								<tr>
									<td>Symmetry:</td>
									<td>
										<input type="text" id="symmetry" name="Symmetry" value="EX">
								    </td>
									<td>Remark2：</td>
									<td><input type="text" id="remark2" name="remark2"></td>
								</tr>
							</table>
						</form>
					</div>
					<div class="modal-footer">
						<button type="button" id="conf" class="btn btn-default" onclick="add()">add</button>
						<button type="button" class="btn btn-default" data-dismiss="modal" onclick="resetAddModal()">cancel</button>
					</div>
				</div>				
			</div>
		</div>
		
		<!-- 上传篮子的模态框 -->
		<div class="modal fade" id="importModal" tabindex="-1" role="dialog">
			<div class="modal-dialog" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<h3>Import DiamondsInfo</h3>
					</div>
					<div class="modal-body">
						<form id="importForm" action="<%=basePath %>/diamond/importDiamondsInfo" class="form-horizontal required-validate" method="post" enctype="multipart/form-data">
							<div class="form-group">
								<label for="upfile" class="col-sm-2 control-label">Diamond File:</label>
								<div class="col-sm-7">
									<input type="file" name="files" class="form-control" id="upfile" data-bv-notempty/>
								</div>
								<label id="errorupfile" for="upfile" class="col-sm-3 control-label"></label>
							</div>
							<!-- <input type="submit" value="test" /> -->
						</form>
					</div>
					<div class="modal-footer">
<!-- 						<button type="button" id="importCsv" class="btn btn-default" onclick="importCsv()">add</button> -->
						<input type="button" id="importCsv" onclick="importCsv()" class="btn btn-default" value="add" />  
						<button type="button" class="btn btn-default" data-dismiss="modal" onclick="resetimportModal()">cancel</button>
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

           // 修复bootstrap validator重复向服务端提交bug
           $form.on('success.form.bv', function(e) {
               // Prevent form submission
               e.preventDefault();
           });


       });
       
     //执行一个laydate实例
       laydate.render({
         elem: '#minedate', //指定元素
       });
       laydate.render({
    	   elem: '#craftsmandate',
         });
       laydate.render({
    	   elem: '#dealerdate',
         });
     
    });
 
    function TableInit() {
    	//console.log("get diamond list");
        var oTableInit = new Object();
        //初始化Table
        oTableInit.Init = function() {
            $('#tableListForContacts').bootstrapTable({
                url: "<%=basePath %>/diamond/getDiamondList",
                pagination: true, //分页
                search: true, //显示搜索框
                //sortable: false,    //是否启用排序
                //sortName: "basketNo",
                //sortOrder: "asc",     //排序方式 
                sidePagination: "client", //服务端处理分页server
                pageNumber: 1,                       //初始化加载第一页，默认第一页
                pageSize: 5,                       //每页的记录行数（*）
                pageList: [5,10, 25],//每页的记录行数（*）
                //contentType : "application/x-www-form-urlencoded",
                queryParams: queryParams, //传递参数（*）
                toolbar:"#toolbar",//工具栏
                showColumns: true,                  //是否显示所有的列
                showRefresh: true,                  //是否显示刷新按钮
                responseHandler: function(data){
                	//console.log(data.rows);
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
                        visible: true,
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
                        title: 'operation',
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
 
    
    function queryParams(params) {
    	//var askid = $("#query-micro-company").val().toString();
    	//var currency = $("#query-currency").val().toString();
    	return {
    		limit : this.limit, // 页面大小
	        offset : this.offset, // 页码
	        pageNumber : this.pageNumber,
	        pageSize : this.pageSize

    	};
    };
    
    function search() {
    	console.log("333");
    	$('#tableListForContacts').bootstrapTable('refresh');
    	console.log("444");
    }
    
  //点击取消后清空表单中已写信息
	function resetAddModal(){
		document.getElementById("addForm").reset();
	}
    
 function operateFormat(value, row, index) {
	 var status = value;
	 //console.log(status);
	 if(status=='2'){
		 return '';
	 }else if(status=='1'){
		 return '<input type="button" value="Add" id="addBtn" data-toggle="modal" data-target="#addModal" class="btn btn-primary"></input>';
	}
 }
 
 
 //binding event shuhao.song
 window.operateEvents = {
 			'click #addBtn': function(e, value, row, index) {
			$("#tradeid").val(row.tradeid);
			$("#supcode").val(row.supcode);
			$("#basketno").val(row.basketno);
			$("#productcode").val(row.productcode);
			//钻石信息赋值
			var productcode = $("#productcode").val();
			var productMap = '${productMapJson}';
			productMap = JSON.parse(productMap);
			var product = '';
			for(var productKey in productMap)
			{
				/* $("#shape").val("BR");
				$("#color").val("D");
				$("#clarity").val("VS1");
				$("#cut").val("EX";
				$("#polish").val("EX");
				$("#symmetry").val("EX"); */
				if(productKey == productcode)
				{
					product = productMap[productKey];
					//console.log(product);
					$("#shape").val(product.shape);
					$("#color").val(product.color);
					$("#clarity").val(product.clarity);
					$("#cut").val(product.cut);
					$("#polish").val(product.polish);
					$("#symmetry").val(product.symmetry);
					break;
				}
			}
			
 		}
 	};
 
    function submit()
    {
    	var index = layer.load(1);
    	var url = "<%=basePath %>/diamond/submitDiamondList";
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
	//新增
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
        var index = layer.load(1);
		var param = $("#addForm").serializeArray();
		//debugger;
		$.ajax({
			url:"<%=basePath %>/diamond/addDiamondInfo",
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
        var index = layer.load(1);
    	var formData = new FormData($("#importForm")[0]);
    	//console.table(formData);
        $.ajax({
            //接口地址
            url: '/diamond/importDiamondsInfo' ,
            type: 'POST',
            dataType:'json',
            data: formData,
            async: false,
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
    
  //Modal验证销毁重构
    $('#addModal').on('hidden.bs.modal', function() {
        $("#addForm").data('bootstrapValidator').destroy();
    	$('#addForm').data('bootstrapValidator',null);
    	$('#addForm').bootstrapValidator();
    });
	
  //Modal验证销毁重构
    $('#importModal').on('hidden.bs.modal', function() {
    	$("#importForm").data('bootstrapValidator').destroy();
    	$('#importForm').data('bootstrapValidator',null);
    	$('#importForm').bootstrapValidator();
    });

</script>