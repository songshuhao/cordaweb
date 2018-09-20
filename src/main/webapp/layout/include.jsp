<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="basePath" scope="request">${pageContext.request.contextPath}</c:set>
<%
response.setHeader("Cache-Control","no-catch");
response.setHeader("Pragma","no-catch");
response.setDateHeader("Expires",0);
 %>

