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
<center>©2018 MDT | <a style="cursor:pointer;" href="#" onclick="showAboutUs()">ABOUT US</a></center>
<script type="text/javascript">
function showAboutUs() 
{
	var webVersion='${webVersion}';
	layer.alert('System Vsersion:' + webVersion, {
        icon: 1,
        skin: 'layer-ext-moon',
        btn: ['Close'],
        title:'About Us'
    });
}
</script>