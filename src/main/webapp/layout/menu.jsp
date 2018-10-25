<%@ include file="/layout/include.jsp" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	/*
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path;*/
	String basePath = request.getContextPath();
%>
  <div id="menu_div" class="sidebar-collapse">
    <ul class="nav" id="main-menu">
	<c:if test="${userInfo.role=='AOC'}">
         <li>
             <a href="<%=basePath %>/basket/findBasketList"><i class="fa fa-dashboard"></i> Create Package List</a>
         </li>
          <li>
             <a href="<%=basePath %>/confirm/confirmList"><i class="fa fa-desktop"></i> Confirm Diamonds List</a>
         </li>
         <li>
             <a href="<%=basePath %>/move/moveList"><i class="fa fa-desktop"></i> Move Diamonds List</a>
         </li>
		 <li>
             <a href="<%=basePath %>/audit/auditList"><i class="fa fa-bar-chart-o"></i> Audit Diamonds List</a>
         </li>
         <li>
             <a href="<%=basePath %>/transfer/transferList"><i class="fa fa-bar-chart-o"></i> Transfer Diamonds List</a>
         </li> 
        <!--  <li>
             <a href="#"><i class="fa fa-edit"></i> Forms </a>
         </li>
         <li>
             <a href="#"><i class="fa fa-sitemap"></i> Multi-Level Dropdown<span class="fa arrow"></span></a>
             <ul class="nav nav-second-level">
                 <li>
                     <a href="#">Second Level Link</a>
                 </li>
                 <li>
                     <a href="#">Second Level Link</a>
                 </li>
                 <li>
                     <a href="#">Second Level Link<span class="fa arrow"></span></a>
                     <ul class="nav nav-third-level">
                         <li>
                             <a href="#">Third Level Link</a>
                         </li>
                         <li>
                             <a href="#">Third Level Link</a>
                         </li>
                         <li>
                             <a href="#">Third Level Link</a>
                         </li>

                     </ul>

                 </li>
             </ul>
         </li> -->
       </c:if>
      <c:if test="${userInfo.role=='Supplier'}">
        <li>
            <a href="<%=basePath %>/diamond/findDiamondList"><i class="fa fa-fw fa-file"></i> Create Diamonds List</a>
        </li>
      </c:if>
      
       <c:if test="${userInfo.role=='Lab'}">
        <li>
            <a href="<%=basePath %>/confirm/confirmgiaList" ><i class="fa fa-fw fa-file"></i> Confirm Diamonds List</a>
        </li>
      </c:if>
        <c:if test="${userInfo.role=='Vault'}">
        <li>
            <a href="<%=basePath %>/move/movevaultList" ><i class="fa fa-fw fa-file"></i> Move Diamonds List</a>
        </li>
         <li>
            <a href="<%=basePath %>/transfer/transfervaultList" ><i class="fa fa-fw fa-file"></i> Transfer Diamonds List</a>
        </li>
      </c:if>
      <c:if test="${userInfo.role=='Auditor'}">
        <li>
            <a href="<%=basePath %>/audit/auditauditerList" ><i class="fa fa-fw fa-file"></i> Audit Diamonds List</a>
        </li>
      </c:if>
      
       <li>
             <a href="<%=basePath %>/history/historyList"><i class="fa fa-bar-chart-o"></i>Diamonds History List</a>
         </li>    
    </ul>
</div>
<script type="text/javascript">
/* $(document).ready(function(){
	  $(".btn1").click(function(){
	    $("p").slideToggle();
	  });
	});
	function onMenuClick(aMenu) {
		var urlPath = window.location.pathname;
		alert(urlPath);
	} */
</script>