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

<!-- 新增的模态框，在修改中将获取一行的值放入input中，改变一些参数继续使用 -->
		<div class="modal fade" id="addModal" tabindex="-1" role="dialog">
			<div class="modal-dialog" role="document" style="width:50%">
				<div class="modal-content">
					<div class="modal-header">
						<h3>Add DiamondsInfo</h3>
					</div>
					<div class="modal-body">
						<form id="addForm" action="<%=basePath %>/diamonds/addDiamondsInfo" method="post" class="form-horizontal required-validate">
								<input type="hidden" id="userid" name="userid" value="${userInfo.userId}"/>
								<input type="hidden" id="suppliercode" name="suppliercode" />
								<input type="hidden" id="tradeid" name="tradeid" />
								<input type="hidden" id="status" name="status" />
								<div class="form-group"> 
									<label for="basketno" class="col-sm-2 control-label">Package Code:</label> 
									<div class="col-md-4 rowGroup"> 
										<%-- <select id="basketno" class="form-control" name="basketno">
									     	<c:forEach items="${basketMap }" var="basket">
												<option value="${basket.key }">
													${basket.key }
												</option>
											</c:forEach>
									    </select> --%>
									    <input  type="text" class="form-control" id="basketno" name="basketno" readonly="readonly"/>
									</div> 
									<label for="origin" class="col-sm-2 control-label">Origin:</label> 
									<div class="col-md-4 rowGroup"> 
										<input  type="text" class="form-control" id="origin" name="origin" />
									</div>
								</div>
								
								<div class="form-group"> 
									<label for="productcode" class="col-sm-2 control-label">Product Code:</label> 
									<div class="col-md-4 rowGroup" >
										<input  type="text" class="form-control" id="productcode" name="productcode" readonly="readonly"/>
									</div>  
									<label for="minedate" class="col-sm-2 control-label">Mining Date:</label> 
									<div class="col-md-4 rowGroup"> 
										<input  type="text" class="form-control" id="minedate" name="minedate" />
									</div> 
								</div>
								
								<div class="form-group"> 
									<label for="giano" class="col-sm-2 control-label">GIA Number:</label> 
									<div class="col-md-4 rowGroup" >
										<input  type="text" class="form-control" id="giano" name="giano" data-bv-notempty/>
									</div>  
									<label for="cutter" class="col-sm-2 control-label">Cutter:</label> 
									<div class="col-md-4 rowGroup"> 
										<input  type="text" class="form-control" id="cutter" name="cutter" />
									</div> 
								</div>
								
								<div class="form-group"> 
									<label for="size" class="col-sm-2 control-label">Size:</label> 
									<div class="col-md-4 rowGroup" >
										<input  type="text" class="form-control" id="size" name="size" data-bv-notempty/>
									</div>  
									<label for="craftsmanname" class="col-sm-2 control-label">Craftman:</label> 
									<div class="col-md-4 rowGroup"> 
										<input  type="text" class="form-control" id="craftsmanname" name="craftsmanname" />
									</div> 
								</div>
								<div class="form-group"> 
									<label for="shape" class="col-sm-2 control-label">Shape:</label> 
									<div class="col-md-4 rowGroup" >
										<input  type="text" class="form-control" id="shape" name="shape" data-bv-notempty/>
									</div>  
									<label for="craftsmandate" class="col-sm-2 control-label">Craftman Date:</label> 
									<div class="col-md-4 rowGroup"> 
										<input  type="text" class="form-control" id="craftsmandate" name="craftsmandate" />
									</div> 
								</div>
								<div class="form-group"> 
									<label for="color" class="col-sm-2 control-label">Color:</label> 
									<div class="col-md-4 rowGroup" >
										<input  type="text" class="form-control" id="color" name="color" data-bv-notempty/>
									</div>  
									<label for="dealername" class="col-sm-2 control-label">Dealer:</label> 
									<div class="col-md-4 rowGroup"> 
										<input  type="text" class="form-control" id="dealername" name="dealername" />
									</div> 
								</div>
								<div class="form-group"> 
									<label for="clarity" class="col-sm-2 control-label">Clarity:</label> 
									<div class="col-md-4 rowGroup" >
										<input  type="text" class="form-control" id="clarity" name="clarity" data-bv-notempty/>
									</div>  
									<label for="dealerdate" class="col-sm-2 control-label">Deal Date:</label> 
									<div class="col-md-4 rowGroup"> 
										<input  type="text" class="form-control" id="dealerdate" name="dealerdate"/>
									</div> 
								</div>
								<div class="form-group"> 
									<label for="cut" class="col-sm-2 control-label">Cut:</label> 
									<div class="col-md-4 rowGroup" >
										<input  type="text" class="form-control" id="cut" name="cut" data-bv-notempty/>
									</div>  
									<label for="reverification" class="col-sm-2 control-label">Re-verification:</label> 
									<div class="col-md-4 rowGroup"> 
										<input  type="text" class="form-control" id="reverification" name="reverification"/>
									</div> 
								</div>
								<div class="form-group"> 
									<label for="polish" class="col-sm-2 control-label">Polish:</label> 
									<div class="col-md-4 rowGroup" >
										<input  type="text" class="form-control" id="polish" name="polish" data-bv-notempty/>
									</div>  
									<label for="remark1" class="col-sm-2 control-label">Remark1:</label> 
									<div class="col-md-4 rowGroup"> 
										<input  type="text" class="form-control" id="remark1" name="remark1" />
									</div> 
								</div>
								
								<div class="form-group"> 
									<label for="symmetry" class="col-sm-2 control-label">Symmetry:</label> 
									<div class="col-md-4 rowGroup" >
										<input  type="text" class="form-control" id="symmetry" name="symmetry" data-bv-notempty/>
									</div>  
									<label for="remark2" class="col-sm-2 control-label">Remark2:</label> 
									<div class="col-md-4 rowGroup"> 
										<input  type="text" class="form-control" id="remark2" name="remark2"/>
									</div> 
								</div>
						</form>
					</div>
					<div class="modal-footer">
						<button type="button" id="conf" class="btn btn-primary" onclick="add()" >Add</button>
						<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
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
						<input type="button" id="importCsv" onclick="importCsv()" class="btn btn-primary" value="Add" />  
						<button type="button" class="btn btn-default" data-dismiss="modal" onclick="resetimportModal()">Cancel</button>
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
    	////console.log("get diamond list");
        var oTableInit = new Object();
        //初始化Table
        oTableInit.Init = function() {
            $('#tableListForContacts').bootstrapTable({
                url: "<%=basePath %>/diamond/getDiamondList",
                cache:false,
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
                showRefresh: true,                  //是否显示刷新按钮
                responseHandler: function(data){
                	////console.log(data.rows);
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
                        visible: false,
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
                        visible: false,
                    },{
                        title: 'Status',
                        field: 'statusDesc',
                        align: 'center',
                        valign: 'middle',
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
 
    
    function queryParams(params) {
    	return {
    		limit : this.limit, // 页面大小
	        offset : this.offset, // 页码
	        pageNumber : this.pageNumber,
	        pageSize : this.pageSize

    	};
    };
    
    function search() {
    	//console.log(111111111111111);
    	$('#tableListForContacts').bootstrapTable('refresh');
    }
    
  //点击取消后清空表单中已写信息
	function resetAddModal(){
		document.getElementById("addForm").reset();
	}
    
 function operateFormat(value, row, index) {
	 var status = value;
	 //console.log(row.giano);
	 if(status=='2' || (row.giano != null && row.giano!='')){
		 return '<div class="form-inline"><input type="button" value="Modify" id="modifyBtn" data-toggle="modal" data-target="#addModal" class="btn btn-primary" style="margin-right: 3px; margin-bottom:1px;"></input>'
		 +'<input type="button" value="Delete" id="deleteBtn" data-toggle="modal" class="btn btn-primary"></input></div>';
	 }else if(status=='1' && row.giano == null){
		 return '<input type="button" value="Add" id="addBtn" data-toggle="modal" data-target="#addModal" class="btn btn-primary"></input>';
	}
 }
 
 
 //binding event shuhao.song
 window.operateEvents = {
 			'click #addBtn': function(e, value, row, index) {
 			$("#tradeid").val(row.tradeid);
			$("#suppliercode").val(row.suppliercode);
			$("#basketno").val(row.basketno);
			$("#productcode").val(row.productcode);
			$("#status").val(row.status);
			//钻石信息赋值
			var productcode = $("#productcode").val();
			var productMap = '${productMapJson}';
			productMap = JSON.parse(productMap);
			var product = '';
			for(var productKey in productMap)
			{
				if(productKey == productcode)
				{
					product = productMap[productKey];
					////console.log(product);
					$("#shape").val(product.shape);
					$("#color").val(product.color);
					$("#clarity").val(product.clarity);
					$("#cut").val(product.cut);
					$("#polish").val(product.polish);
					$("#symmetry").val(product.symmetry);
					break;
				}
			}
			
			$(".modal-header > h3").text("Add DiamondsInfo");
			$("#conf").text("Add");
			
 		},
 		'click #modifyBtn': function(e, value, row, index) {
 			////console.log(row);
 			$("#tradeid").val(row.tradeid);
			$("#suppliercode").val(row.suppliercode);
			$("#basketno").val(row.basketno);
			$("#status").val(row.status);
			$("#productcode").val(row.productcode);
			$("#giano").val(row.giano);
			$("#size").val(row.size);
			$("#shape").val(row.shape);
			$("#color").val(row.color);
			$("#clarity").val(row.clarity);
			$("#cut").val(row.cut);
			$("#polish").val(row.polish);
			$("#symmetry").val(row.symmetry);
			//-----------
			$("#origin").val(row.origin);
			$("#minedate").val(row.minedate);
			$("#cutter").val(row.cutter);
			$("#craftsmanname").val(row.craftsmanname);
			$("#craftsmandate").val(row.craftsmandate);
			$("#dealername").val(row.dealername);
			$("#reverification").val(row.reverification);
			$("#dealerdate").val(row.dealerdate);
			$("#remark1").val(row.remark1);
			$("#remark2").val(row.remark2);
			
			$(".modal-header > h3").text("Modify DiamondsInfo");
			$("#conf").text("Modify");
 		},
 		'click #deleteBtn': function(e, value, row, index) {
 			deleteDiamond(row);
 		}
 	};
 
    function submit()
    {
    	var index = layer.load();
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
		var tradeId=$("#tradeid").val();
		var url="";
		
		if(tradeId !='' && null != tradeId)
		{
			url = "<%=basePath %>/diamond/eidtDiamondInfo";
			$('#addForm').bootstrapValidator('enableFieldValidators', 'giano', false);
		}else
		{
			url = "<%=basePath %>/diamond/addDiamondInfo";
		}
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
		
		$.ajax({
			url:url,
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
	
	
	//删除
	function deleteDiamond(param){
		
		var index = layer.load();
		//console.log(param);
		//alert(111);
		//debugger;
		$.ajax({
			url:"<%=basePath %>/diamond/deleteDiamondInfo",
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
        var index = layer.load();
    	var formData = new FormData($("#importForm")[0]);
    	//console.table(formData);
        $.ajax({
            //接口地址
            url: '<%=basePath %>/diamond/importDiamondsInfo' ,
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
    
  //Modal验证销毁重构
    $('#addModal').on('hidden.bs.modal', function() {
    	resetAddModal();
        $("#addForm").data('bootstrapValidator').destroy();
    	$('#addForm').data('bootstrapValidator',null);
    	formValidate();
    });
	
  //Modal验证销毁重构
    $('#importModal').on('hidden.bs.modal', function() {
    	$("#importForm").data('bootstrapValidator').destroy();
    	$('#importForm').data('bootstrapValidator',null);
    	$('#importForm').bootstrapValidator();
    });
  
    function formValidate()
	 {
		 var addForm = $("#addForm");
		 addForm.bootstrapValidator({//根据自己的formid进行更改
            message: 'This value is not valid',//默认提示信息
            group: '.rowGroup',//自定义校验class名称
            feedbackIcons: {//提示图标
                valid: 'glyphicon glyphicon-ok',
                invalid: 'glyphicon glyphicon-remove',
                validating: 'glyphicon glyphicon-refresh'
            },
            fields: {
            	giano: {//名称校验
                       message: 'This value is not valid',
                       verbose: false,//多验证的情况下默认第一验证错误，则提示当前错误信息后面的验证不执行
                       validators: {//验证条件
                           /* notEmpty: {
                               message: '附属品名称不能为空'
                           }, */
                           stringLength: {
                        	   min: 8,
                               max: 13,
                               message: 'length should be 13 digit!'
                           },regexp: {//自定义校验
                               regexp: /^[A-Za-z0-9]+$/,//匹配由数字和26个英文字母组成的字符串
                               message: 'Value should be number and letter!'
                           },remote:{
                        	   message: "Gia number already exists",
                        	   delay: 1000,
                        	   type:'POST',
                        	   url:'<%=basePath %>/diamond/checkGiaNo',
                        	   data: function(validator,$field, value) {
                                       return {
                                    	   giano:$("#giano").val(),
                                    	   tradeid:$("#tradeid").val(),
                                    	   userid:$("#userid").val()
                                       };
                        		},
                        	   //dataType:"json",
                        	   dataFilter:function(data,type){
                        		   return data;
                               } ,
                           }
                       }
                   },
                   size: {//名称校验
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
                        }
                    }
                },
                /* minedate: {//名称校验
                    message: 'This value is not valid',
                    validators: {//验证条件
                    	date : {  
                            format : 'YYYY-MM-DD',  
                            message : 'format should be yyyy-MM-dd'  
                        } 
                    }
                }, */
                /* origin2: {
             	   message: 'This value is not valid',
                    validators: {//验证条件
                        
                        stringLength: {
                            min: 1,
                            max: 10,
                            message: 'Max length 10!'
                        },regexp: {//自定义校验
                            regexp: /^[+]{0,1}(\d+)$|^[+]{0,1}(\d+\.\d+)$/,//
                            message: 'Value should bigger than 0!'
                        }
                    }
                }, */
               /*  origin3: {
             	   message: 'This value is not valid',
                    validators: {
                       
                        stringLength: {
                            min: 1,
                            max: 10,
                            message: 'Max length 10!'
                        },regexp: {//自定义校验
                            regexp: /^[+]{0,1}(\d+)$|^[+]{0,1}(\d+\.\d+)$/,
                            message: 'Value should bigger than 0!'
                        },
                        callback: {//自定义，可以在这里与其他输入项联动校验/
                       	 message: 'Should small than total weight',
                            callback:function(value, validator){
                           		
                            }
                        }
                    }
                }, */
            },
        });
		 
		 var importForm = $("#importForm");
		 importForm.bootstrapValidator();
	 }

</script>