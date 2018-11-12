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
<!-- 
     add by shuhao.song
     1，系统各个界面导出功能抽取
 	 2，系统各个界面导入功能抽取
 	 3，系统各个界面操作成功失败提示
	 4，session超时ajax全局跳转
 -->
<!-- 操作成功提示模态框 -->
		<div id="opSuccessModal" class="modal fade " tabindex="-1" role="dialog" >
		  <div class="modal-dialog" role="document">
		
		    <div class="modal-content">
		        <div class="modal-header">
		            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
		            <h4 class="modal-title">success</h4>
		        </div>
		        <div class="modal-body" style="text-align: center;">
		            <p style="color:green" id="successMessage">Success</p>
		        </div>
		    </div>
		  </div>
		</div>
		<!-- 操作失败提示模态框 -->
		<div id="opFailedModal" class="modal fade " tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel">
		  <div class="modal-dialog" role="document">
		
		    <div class="modal-content">
		       <div class="modal-header">
		            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
		            <h4 class="modal-title">Failed</h4>
		        </div>
		        <div class="modal-body" style="text-align: center;">
		            <p style="color:red" id="failedMessage">Fail</p>
		            <p style="color:red" id="importMessage"></p>
		        </div>
		    </div>
		  </div>
		</div>
		
		
	<!-- 上传篮子的模态框 -->
		<div class="modal fade" id="importModal" tabindex="-1" role="dialog">
			<div class="modal-dialog" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<h3>Import</h3>
					</div>
					<div class="modal-body">
						<form id="importForm" action="<%=basePath %>/basket/importBasketInfo" class="form-horizontal required-validate" method="post" enctype="multipart/form-data">
							<div class="form-group">
								<label for="upfile" class="col-sm-4 control-label">Import File:</label>
								<div class="col-sm-6">
									<input type="file" name="files" class="form-control" id="upfile" accept=".xls,.xlsx"/>
								</div>
							</div>
						</form>
					</div>
					<div class="modal-footer">
						<input type="button" id="importFile" onclick="importFile()" class="btn btn-primary" value="Import" />  
						<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
					</div>
				</div>				
			</div>
		</div>
<script type="text/javascript">
	/**
	 * 设置未来(全局)的AJAX请求默认选项
	 * 主要设置了AJAX请求遇到Session过期的情况
	 * add by shuhao.song
	 */
	$.ajaxSetup({
	    type: 'POST',
	    complete: function(xhr,status) {
	        var sessionStatus = xhr.getResponseHeader('sessionstatus');
	        if(sessionStatus == 'timeout') {
	            var top = getTopWinow();
	            top.location.href = '<%=basePath %>/login';
	            <%-- layer.msg('Session timeout! Three seconds later, jump to login page!',{
	            	 icon: 2,
	                 //time: 3000 //2秒关闭（如果不配置，默认是3秒）
	               },function(){
	                   top.location.href = '<%=basePath %>/login';
	            });  --%>
	        }
	    }
	});
	 
	/**
	 * 在页面中任何嵌套层次的窗口中获取顶层窗口
	 * @return 当前页面的顶层窗口对象
	 */
	function getTopWinow(){
	    var p = window;
	    while(p != p.parent){
	        p = p.parent;
	    }
	    return p;
	}
	
	
	
	//export fuction,use some bootsrap-table-export style
	//add by shuhao.song
	
	/* $(".dropdown-menu").find('li').click(function () {
		var type = $(this).data('type');
		//console.log(type);
	}); */
    function exportData()
    {
    	var index = layer.load();
    	var param = {"step":step};
    	$.ajax({
			url:"<%=basePath %>/basket/createExportData",
			method:"post",
			data:param,
			dataType:"json",
			success:function(data){
				//console.log(data);		
				layer.close(index);
				if(data.state=="success"){
					downloadData(data);
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
	
	function downloadData(data)
	{
		//console.log(data);
    	var url="<%=basePath %>/basket/downloadExportData";
	    var form = $("<form></form>");
	    var input1 = $("<input type='hidden' name='filePath'/>");
	    input1.attr('value',data.filePath);
	    form.attr('action',url);
	    form.attr('method','post');
	    form.append(input1);
	    form.appendTo("body");
	    form.css('display','none');
	    //console.log(form);
	    form.submit().remove();
	}
	
	
	/**
	data：return data
	modalId：hidden div，id or null
	isSubmit：true false
	add by shuhao.song
	*/
	function messageShow(data,modalId,isSubmit)
	{
	  ////console.log(data);
	  ////console.log(modalId);
	  ////console.log(isSubmit);
	  if(data == null)
	  {
		  if(null != modalId)
		  {
		  	$(modalId).modal('hide');
		  }
		  $("#opFailedModal").modal('show').on('hidden.bs.modal', function() {
		    	search();
		  });
		  $("#failedMessage").text("Unknown exception");
	  }else if(data.state=='success')
	  {
		  if(!isSubmit)
		  {
		  	$(modalId).modal('hide');
		  }
		$("#successMessage").text(data.message);
		$("#opSuccessModal").modal('show').on('hidden.bs.modal', function() {
	    	search();
	    });
	  }else if(data.state=='fail')
	  {
		  if(!isSubmit)
		  {
		  	$(modalId).modal('hide');
		  }
		  //console.log(data.message);
		  if(data.message != null && data.message.trim() !="")
		  {
			  var showMessage = data.message.replace(/\n/g,'</br>');
			  $("#failedMessage").html(showMessage);
			  if(!isSubmit)
			  {
				  if(modalId.indexOf("importModal")>0)
				  {
					  $("#importMessage").html("Please check the import data or try to export again!");
				  }else
				  {
					  $("#importMessage").html("");
				  }
			  }
		  }
		  $("#opFailedModal").modal('show').on('hidden.bs.modal', function() {
		    	search();
		  });
		 
	  }
	}
	
	 function openImport()
    {
    	$("#importModal").modal('show');
    }
	    
    function importFile()
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
    	formData.append("step", step);
    	var urlArr = $("#upfile").val().split("\\");
    	var fileName = urlArr[urlArr.length-1];
    	formData.append("fileName",fileName);
    	//console.log(fileName);
    	var url = getImportUrlByStep(step); 
        $.ajax({
            //接口地址
            url: url,
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
    
    
    //根据不同的step获取url
    function getImportUrlByStep(step) 
    {
    	var url = "";
		if(step=="ats"){
			url = "<%=basePath %>/basket/importBasketInfo";
		}if(step=="sta"){
			url = "<%=basePath %>/diamond/importDiamondsInfo";
		}if(step=="atg"){
			
		}if(step=="gta"){
			
		}if(step=="atv"){
			
		}if(step=="vta"){
			
		}if(step=="atvo"){
			
		}if(step=="vota"){
			
		}if(step=="atau"){
			
		}if(step=="auta"){
			
		}
			
	    return url;
	}
    
    //导入校验
    function importValidate()
	 {
		 var addForm = $("#importForm");
		 addForm.bootstrapValidator({//根据自己的formid进行更改
			 //live: 'disabled',验证时机，enabled是内容有变化就验证（默认），disabled和submitted是提交再验证
            message: 'This value is not valid',//默认提示信息
            feedbackIcons: {//提示图标
                valid: 'glyphicon glyphicon-ok',
                invalid: 'glyphicon glyphicon-remove',
                validating: 'glyphicon glyphicon-refresh'
            },
            fields: {
           	 files: {//名称校验
                    message: 'Please upload files endwith xls or xlsx!',
                    validators: {//验证条件
                        callback: {//自定义，可以在这里与其他输入项联动校验/
                       	 //联动校验，修改此值会联动校验mimnumber
                            callback:function(value, validator){
                           	 var flag = false; //状态
                           	 var arr = ["xls","xlsx"];
                           	 //var arr = ["csv","xls","xlsx"];
                           	 //取出上传文件的扩展名
                           	 //console.log(value);
                           	 var index = value.lastIndexOf(".");
                           	 var ext = value.substr(index+1);
                           	 //循环比较
                           	 for(var i=0;i<arr.length;i++)
                           	 {
	                            	  if(ext == arr[i])
	                            	  {
	                            	   flag = true; //一旦找到合适的，立即退出循环
	                            	   break;
	                            	  }
                           	 }
                           	 
                           	 return flag;
                            }
                        }
                    }
                }
            },
        });
	 }
    
    //Modal验证销毁重构
    $('#importModal').on('hidden.bs.modal', function() {
    	document.getElementById("importForm").reset();
    	$("#importForm").data('bootstrapValidator').destroy();
    	$('#importForm').data('bootstrapValidator',null);
    	importValidate();
    	
    });
	
</script>
