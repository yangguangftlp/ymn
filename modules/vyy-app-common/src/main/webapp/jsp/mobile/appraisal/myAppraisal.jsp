<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> 
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head lang="en">
    <%@ include file="../../include/head.jsp" %>
    <title>员工评价</title>
    <link rel="stylesheet" href="css/font-awesome.min.css">
    <link rel="stylesheet" href="css/global.css">
    <link rel="stylesheet" href="css/common.css">
</head>
<body class="whiteBg">
<!-- 隐藏字段 -->
<input name="operateType" type="hidden" id="operateType" value="">

<div class="warp">
    <nav class="newSearchNav">
		<header
			class="newSearchHead whiteColor centrallyLocated mainBg newSearchBottom">
			<b></b>&emsp;&emsp; <input type="checkbox"> <span><i
				class="fa fa-angle-down projectIcon"></i></span>
		</header>
		<div class="newSearchBottom whiteBg switchCon myHide">
			<ul>
				<li class="newSearchBottom">未结束<!--<i class="fa fa-check mainColor"></i>--></li>
				<li>已结束</li>
			</ul>
		</div>
	</nav>    
    <section>
        <ul>
            <!--<li class="twoLine" dataid="" flowtype="0">
                <header>
                    <span class="mainColor">公司司机月底考核评价</span>
                    <span class="auditStatus greenColor singleStatus">待审核</span>
                    <i class="fa fa-angle-right"></i>
                </header>
                <div class="subColor clear">
                    <span>10月14日发起</span>
                </div>
            </li>-->
        </ul>
    </section>
</div>
<footer class="copyright">© 微移云技术支持</footer>
<script src="js/jquery-1.8.3.min.js"></script>
<script src="js/recordList.js"></script>
<script src="js/public-plug-in.js"></script>
<script src="js/mobile/appraisal/myAppraisal.js"></script>
</body>
</html>
