<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head lang="en">
    <%@ include file="../../include/head.jsp"%>
    <title>报销</title>
    <link rel="stylesheet" href="css/font-awesome.min.css">
    <link rel="stylesheet" href="css/global.css">
    <link rel="stylesheet" href="css/common.css">
    <link rel="stylesheet" href="css/mobiscroll.css">
    <link rel="stylesheet" href="css/newExpense.css">
    
</head>
<body class="whiteBg">
    <div class="warp">    
    <nav class="newSearchNav">
        <div class="clear newSearchBottom publicNav mainBg">
            <div class="newSearchHead whiteColor newSearchHeaderL" id="1">
                <b></b>
                <input type="checkbox">
                <i class="fa fa-angle-down projectIcon"></i>
            </div>
            <div class="newSearchHeaderC"></div>
            <div class="newSearchHead whiteColor centrallyLocated newSearchHeaderR" id="2">
                <span><i class="fa fa-filter"></i></span>&emsp;筛选
                <input type="checkbox">
            </div>
        </div>
        <div class="newSearchBottom whiteBg switchCon myHide">
            <ul>
                <li class="newSearchBottom">待审核</li>
                <li>已审核</li>
                <!-- <li>全部状态</li> -->
            </ul>
        </div>
        <div class="filterForm newSearchBottom whiteBg switchCon myHide">
            <ul class="whiteBg">
                <li class="newSearchBottom">
                    <label class="listNameWidth5">开始日期：</label>
                    <input type="text" name="startDate" id="startDate">
                    <label class="add-on" for="startDate">请选择 <i
                            class="fa fa-angle-right"></i></label>
                </li>
                <li class="newSearchBottom">
                    <label class="listNameWidth5">结束日期：</label>
                    <input type="text" name="endDate" id="endDate">
                    <label class="add-on" for="endDate">请选择 <i
                            class="fa fa-angle-right"></i></label>
                </li>
                <li class="newSearchBottom">
                    <label class="listNameWidth5">人员姓名：</label>
                    <!-- 营销负责人 -->
                    <input name="userName" id="userName" value="">
                </li>
                <li class="newSearchBottom">
                    <label class="listNameWidth5">报销单号：</label>
                    <!-- 客户经理 -->
                    <input name="number" id="number" value="">
                </li>               
            </ul>
            <div style="text-align: center">
                <button class="mainBg whiteColor" id="searchBtn">搜 索</button>&emsp;&emsp;
                <button class="whiteColor yellowBg" id="resetSearch">重置</button>
            </div>
        </div>
    </nav>
	<section>
		<ul>
		</ul>
	</section>
		<input id="personType" value="${personType}" type="hidden">
    </div>
    <script src="js/jquery-1.8.3.min.js"></script>
    <script src="js/public-plug-in.js"></script>
    <script src="js/mobiscroll.js"></script>
	<script src="js/mobiscroll_zh.js"></script>
	<script src="js/recordList.js"></script>
    <script src="js/mobile/expense/rep-auditRecord.js"></script>
    <!-- 版本信息 -->
	<script type="text/javascript" src="js/foot.js"></script>
</body>