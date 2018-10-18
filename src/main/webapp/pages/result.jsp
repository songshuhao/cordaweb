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
	
	
	/**
	data：return data
	modalId：hidden div，id or null
	isSubmit：true false
	add by shuhao.song
	*/
	function messageShow(data,modalId,isSubmit)
	{
	  //console.log(data);
	  //console.log(modalId);
	  //console.log(isSubmit);
	  if(data == null)
	  {
		  if(null != modalId)
		  {
		  	$(modalId).modal('hide');
		  }
		  $("#opFailedModal").modal('show').on('hidden.bs.modal', function() {
		    	search();
		  });
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
		  $("#opFailedModal").modal('show').on('hidden.bs.modal', function() {
		    	search();
		  });
		 $("#failedMessage").text(data.message);
	  }
	}
</script>
