<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	/*
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path;*/
	String basePath = request.getContextPath();
%>
<!DOCTYPE html>  
<html>  
<head>  
    <meta charset="UTF-8">  
    <meta name="robots" content="noindex,nofollow">  
    <title>404 - The Page is not found</title>  
    <style>  
        body{font-size: 14px;font-family: 'helvetica neue',tahoma,arial,'hiragino sans gb','microsoft yahei','Simsun',sans-serif; background-color:#fff; color:#808080;}  
        .wrap{margin:200px auto;width:510px;}  
        td{text-align:left; padding:2px 10px;}  
        td.header{font-size:22px; padding-bottom:10px; color:#000;}  
        td.check-info{padding-top:20px;}  
        a{color:#328ce5; text-decoration:none;}  
        a:hover{text-decoration:underline;}  
    </style>  
</head>  
<body>  
    <div class="wrap">  
        <table>  
            <tr>  
                <td rowspan="5" style=""><img src="https://ws1.sinaimg.cn/large/a15b4afegy1fhsfdznep4j2020020web.jpg" alt="又一个极简的错误页面"></td>  
                <td class="header">Sorry！This Page or request is not right</td>  
            </tr>  
            <tr><td>Reason 1: you hit the wrong web site.</td></tr>  
            <tr><td>Reason 2: your request is illegal.</td></tr>  
            <tr><td>Please use normal submission mode. or re login and submit again!<a href="<%=basePath %>/login">Login Again</a></td></tr>  
        </table>  
    </div>  
</body>  
</html>