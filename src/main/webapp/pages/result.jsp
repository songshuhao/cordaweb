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
<!-- 操作成功提示模态框 -->
		<div id="opSuccessModal" class="modal fade " tabindex="-1" role="dialog" >
		  <div class="modal-dialog" role="document">
		
		    <div class="modal-content">
		        <div class="modal-header">
		            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
		            <h4 class="modal-title">success</h4>
		        </div>
		        <div class="modal-body" style="text-align: center;">
		            <p  class="p1" style="color:green" id="successMessage">Success</p>
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
		            <p class="p1" style="color:red" id="failedMessage">fail</p>
		        </div>
		    </div>
		  </div>
		</div>
<script type="text/javascript">
	/**
	data：return data
	modalId：hidden div，id or null
	isSubmit：true false
	*/
	function messageShow(data,modalId,isSubmit)
	{
	  console.log(data);
	  console.log(modalId);
	  console.log(isSubmit);
	  if(data == null)
	  {
		  if(null != modalId)
		  {
		  	$(modalId).modal('hide');
		  }
		  $("#opFailedModal").modal('show');
		  $("#failedMessage").text("unknown exception");
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
		  $("#opFailedModal").modal('show');
		 $("#failedMessage").text(data.message);
	  }
	}
</script>
