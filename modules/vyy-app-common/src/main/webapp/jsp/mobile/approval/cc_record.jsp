<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> 
<!DOCTYPE html>
<html>
<head lang="en">
    <%@ include file="../../include/head.jsp"%>
    <title>审批</title>
    <link rel="stylesheet" href="css/font-awesome.min.css">
    <link rel="stylesheet" href="css/global.css">
    <link rel="stylesheet" href="css/common.css">
    <link rel="stylesheet" href="css/approval.css">
    <link rel="stylesheet" href="css/mobiscroll.css">
</head>
<body class="whiteBg">
<div class="warp">
    <!-- 搜索 -->
    <nav class="newSearchNav">
        <header class="newSearchHead whiteColor centrallyLocated mainBg newSearchBottom">
            <span><i class="fa fa-filter"></i></span>&emsp;筛选
            <input type="checkbox">
        </header>
        <div class="filterForm newSearchBottom whiteBg switchCon myHide">
				<ul class="whiteBg">
					<li class="newSearchBottom"><label class="listNameWidth5">流程名称：</label><input
						name="flowName" id="flowName" value=""></li>
					<li class="newSearchBottom"><label class="listNameWidth4">申请人：</label><input
						name="userName" id="userName" value=""></li>
					<li class="newSearchBottom"><label class="listNameWidth5">申请日期：</label><input
						type="text" id="formDate" name="formDate" readonly> <label
						class="add-on" for="formDate">请选择 <i
							class="fa fa-angle-right"></i></label></li>
					<li class="newSearchBottom"><label for="statusFlag"
						class="listNameWidth3">状态：</label> <select name="statusFlag"
						id="statusFlag">
							<option value="-1"></option>
							<option value="0">已通过</option>
							<option value="1">未通过</option>
							<option value="2">已办</option>
							<option value="3">未办</option>
					</select> <label class="add-on" for="statusFlag">请选择 <i
							class="fa fa-angle-right"></i></label></li>
				</ul>
				<div style="text-align: center">
                <button class="mainBg whiteColor" id="searchBtn">搜 索</button>&emsp;&emsp;
                <button class="whiteColor yellowBg" id="resetSearch">重置</button>
            </div>
        </div>
    </nav>
    <!-- 列表 -->
    <section>
        <ul>
            <!--<li class="twoLine" dataid="" flowtype="0">
                <header>
                    <span class="mainColor">3</span>
                    <span class="auditStatus greenColor singleStatus">待审核</span>
                    <i class="fa fa-angle-right"></i>
                </header>
                <div class="subColor clear">
                    <span>李玉霞 10月14日 14:40提交</span>
                </div>
            </li>-->
        </ul>
    </section>
    <footer class="copyright">© 微移云技术支持</footer>
</div>
<script src="js/jquery-1.8.3.min.js"></script>
<script src="js/mobiscroll.js"></script>
<script src="js/mobiscroll_zh.js"></script>
<script src="js/recordList.js"></script>
<script src="js/public-plug-in.js"></script>
<script src="js/mobile/approval/myCopy.js"></script>
</body>
</html>