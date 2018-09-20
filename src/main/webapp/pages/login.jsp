<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path;
%>
<!DOCTYPE html>
<html>
<head>
	<title>Login</title>
	<meta name="keywords" content="" />
	<meta name="description" content="" />
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	
	<link href="<%=basePath %>/static/css/bootstrap/font-awesome.min.css" rel="stylesheet" type="text/css">
	<link href="<%=basePath %>/static/css/bootstrap/bootstrap.min.css" rel="stylesheet" type="text/css">
	<link href="<%=basePath %>/static/css/bootstrap/bootstrap-theme.min.css" rel="stylesheet" type="text/css">
	<link href="<%=basePath %>/static/css/baseLayout/longin_style.css" rel="stylesheet" type="text/css">
	
	<script src="<%=basePath %>/static/js/jquery-1.11.3.min.js"></script>
	<script src="<%=basePath %>/static/plugin/layer/layer.js"></script>
	
	
</head>
<body class="templatemo-bg-gray">
	<div class="container">
		<div class="col-md-12">
			<h1 class="margin-bottom-15"></h1><br>
			<h1 class="margin-bottom-15"></h1><br>
			<h1 class="margin-bottom-15"></h1>
			<form class="form-horizontal templatemo-container templatemo-login-form-1 margin-bottom-30" method="POST" action="" name="logon" id="loginForm">				
		        <div class="form-group">
		          <div class="col-xs-12">		            
		            <div class="control-wrapper">
		            	<label for="username" class="control-label fa-label"><i class="fa fa-user fa-medium"></i></label>
		            	<input type="text" class="form-control" name="userid" placeholder="User Id" id="userid" autocomplete="off">
		            </div>		            	            
		          </div>              
		        </div>
		        <div class="form-group">
		          <div class="col-md-12">
		          	<div class="control-wrapper">
		            	<label for="password" class="control-label fa-label"><i class="fa fa-lock fa-medium"></i></label>
		            	<input type="password" class="form-control" name="password" placeholder="Password" id="password" autocomplete="off">
		            </div>
		          </div>
		        </div>
		        <div class="form-group">
		          <div class="col-md-12">
		          	<div class="control-wrapper" align="center">
		          		<button class="btn btn-info" id="login">Login</button>
		          		<!-- <input type="submit" class="btn btn-info" value="Login" id="login"> -->
		          	</div>
		          </div>
		        </div>
		      </form>
		</div>
	</div>
	
<script type="text/javascript">
$("#login").click(function(){
    var userid = $.trim($("#userid").val());
    var password = $.trim($("#password").val());
     if(userid == ""){
    	 layer.msg('Please enter user id');
         return false;
     }else if(password == ""){
    	 layer.msg('Please enter password');
         return false;
     }
     var param = $("#loginForm").serializeArray();
     var url = "<%=basePath %>/logon";
     $.ajax({
    	url:url,
    	type:"POST",//为post请求
		data:param,
		dataType:"json",
        success:function(data){
			if(data.state=="success"){
				window.location.href = "<%=basePath %>/welcome";   
			}
			if(data.state=="fail"){
				layer.msg(data.message);
			}
		},
		error:function(){
			layer.msg("There is something wrong");
		}
		});
     return false;
	});
</script>
</body>
</html>