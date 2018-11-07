<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	/*
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path;*/
	String basePath = request.getContextPath();
%>
<div class="navbar-header">
    <a class="navbar-brand" ><i class="fa fa-user fa-fw"></i><strong>${userInfo.nodeName}</strong></a>
</div>

<ul class="nav navbar-top-links navbar-right"> 
    <li><a href="<%=basePath %>/logout"><i class="fa fa-sign-out fa-fw"></i> Logout</a></li>
</ul>
