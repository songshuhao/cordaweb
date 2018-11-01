<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ page pageEncoding="UTF-8"%>
<%
	/*
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path;*/
	String basePath = request.getContextPath();
%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
	<!-- Bootstrap Styles-->
    <link rel="stylesheet" type="text/css" href="<%=basePath %>/static/css/bootstrap/bootstrap.min.css">
     <!-- FontAwesome Styles-->
    <link rel="stylesheet" type="text/css" href="<%=basePath %>/static/css/bootstrap/font-awesome.css">
        <!-- layout base Styles-->
    <link href="<%=basePath %>/static/css/baseLayout/baseLayout.css" rel="stylesheet" /> 
    <link href="<%=basePath %>/static/css/bootstrap/bootstrap-table.css" rel="stylesheet" />
    <link href="<%=basePath %>/static/css/bootstrap/bootstrapValidator.css" rel="stylesheet" />
    <link href="<%=basePath %>/static/css/webdiamond/body.css" rel="stylesheet" />
    <link href="<%=basePath %>/static/plugin/layer/mobile/need/layer.css" rel="stylesheet" />
    
    <script src="<%=basePath %>/static/js/jquery-1.11.3.min.js"></script>
    <script src="<%=basePath %>/static/js/bootstrap/bootstrap.min.js"></script>
    <script src="<%=basePath %>/static/js/bootstrap/bootstrap-table.js"></script>	    
   <%--  <script src="<%=basePath %>/static/js/jquery.dataTables.min.js"></script> --%>
    <script src="<%=basePath %>/static/js/bootstrap/bootstrap-table-export_new.js"></script>
    <script src="<%=basePath %>/static/js/jquery.metisMenu.js"></script>
    <script src="<%=basePath %>/static/js/layout-scripts.js"></script>  
    <script src="<%=basePath %>/static/js/bootstrap/bootstrapValidator.js"></script>
    <script src="<%=basePath %>/static/plugin/laydate/laydate.js"></script>
    <script src="<%=basePath %>/static/plugin/layer/layer.js"></script>
   
	<title><tiles:insertAttribute name="title" ignore="true"/></title> 
</head>
<body>
    <div id="wrapper">
        <nav class="navbar navbar-default top-navbar" role="navigation">
            <tiles:insertAttribute name="header"/>
        </nav>
        <!--/. NAV TOP  -->
        <nav class="navbar-default navbar-side" role="navigation">
            <tiles:insertAttribute name="menu"/>
        </nav>
        <!-- /. NAV SIDE  -->
        <div id="page-wrapper" >
            <div id="page-inner">
            	<tiles:insertAttribute name="body"/>
			</div>
             <!-- /. PAGE INNER  -->
        </div>
         <!-- /. PAGE WRAPPER  -->
    </div>
    <!-- /. WRAPPER  -->   
</body>
</html>