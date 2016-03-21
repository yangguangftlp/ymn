<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> 
<!DOCTYPE html>
<html>
<head lang="en">
    <%@ include file="../../include/head.jsp" %>
    <title>借款管理</title>
    <link rel="stylesheet" href="css/font-awesome.min.css">
    <link rel="stylesheet" href="css/global.css">
    <link rel="stylesheet" href="css/common.css">
    <link rel="stylesheet" href="css/loan.css">
</head>
<body>
<body class="whiteBg">
<div class="warp">
<!--     <ul class="clear nav twoColumns">
        <li class="navSelected" flag="0" data-url="data/rep-record0.json">未处理</li>
        <li flag="0" data-url="data/rep-record0.json">已处理</li>
    </ul> -->
		<nav class="newSearchNav">
			<header
				class="newSearchHead whiteColor centrallyLocated mainBg newSearchBottom">
				<b>未处理<!--全部状态--></b>&emsp;&emsp; <input type="checkbox"> <span><i
					class="fa fa-angle-down projectIcon"></i></span>
			</header>
			<div class="newSearchBottom whiteBg switchCon myHide">
				<ul>
					<li class="newSearchBottom">未处理<!--<i class="fa fa-check mainColor"></i>--></li>
					<li>已处理</li>
					<!-- <li>全部状态</li> -->
				</ul>
			</div>

		</nav>
		<section>
        <ul>
            <!--<li class="twoLine" dataid="" data-loanType="">
                <header>
                    <span class="mainColor">江苏优客互联科技股份有限公司</span>
                    <span class="auditStatus greenColor">对公借款</span>
                    <i class="fa fa-angle-right"></i>
                </header>
                <div class="subColor clear">
                    <span>李玉霞 10月14日 14:40提交</span>
                    <span>单号:JK151102001</span>
                </div>
            </li>-->
        </ul>
    </section>
</div>
<footer class="copyright">© 微移云技术支持</footer>
<script src="js/jquery-1.8.3.min.js"></script>
<script src="js/public-plug-in.js"></script>
<script src="js/recordList.js"></script>
<script src="js/mobile/loan/borrowingRecord.js"></script>
</body>
</html>