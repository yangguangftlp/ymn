<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head lang="en">
   <%@ include file="../../include/head.jsp"%>
    <title>报销</title>
    <link rel="stylesheet" href="css/font-awesome.min.css">
    <link rel="stylesheet" href="css/global.css">
    <link rel="stylesheet" href="css/common.css">
</head>
<body>
    <div class="warp">
        <nav class="newSearchNav">
			<header
				class="newSearchHead whiteColor centrallyLocated mainBg newSearchBottom">
				<b><!--全部状态--></b> <input type="checkbox"> <span><i
					class="fa fa-angle-down projectIcon"></i></span>
			</header>
			<div class="whiteBg newSearchBottom switchCon myHide">
				<ul>
					<li class="newSearchBottom">未报销<!--<i class="fa fa-check mainColor"></i>--></li>
					<li>已报销</li>
					<!-- <li>全部状态</li> -->
				</ul>
			</div>

		</nav>
		<section>
			<ul>
			</ul>
		</section>
    </div>
    <!-- 隐藏域 -->
    <input type="hidden" value="mobile/expense/expenseDetailView.action" id="go-url">
    <script src="js/jquery-1.8.3.min.js"></script>
    <script src="js/public-plug-in.js"></script>
    <script src="js/recordList.js"></script>
    <script src="js/mobile/expense/rep-record.js"></script>
    <!-- 版本信息 -->
	<script type="text/javascript" src="js/foot.js"></script>
</body>
</html>
