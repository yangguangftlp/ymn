<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head lang="en">
<title>签到</title>
<%@ include file="../../include/head.jsp"%>
<link rel="stylesheet" href="css/communal.css">
<link rel="stylesheet" href="css/signIn-mySignIn.css">
</head>
<body class="whiteBg">
	<nav class="newSearchNav">
        <header
                class="newSearchHead whiteColor centrallyLocated mainBg newSearchBottom">
            <b>
                <!--全部状态-->
            </b> <input type="checkbox"> <span><i
                class="fa fa-angle-down projectIcon"></i></span>
        </header>
        <div class="whiteBg newSearchBottom switchCon myHide">
            <ul>
                <li class="newSearchBottom">未结束</li>
                <li>已结束</li>
            </ul>
        </div>
    </nav>
    <section style="margin-top:61px;margin-bottom: 40px;">
    	<ul></ul>
    </section>
	<script src="js/jquery-1.8.3.min.js"></script>
	<script src="js/recordList.js"></script>
	<script src="js/mobile/sign/myLaunchSign.js"></script>
	<script src="js/public-plug-in.js"></script>
	<!-- 版本信息 -->
	<script type="text/javascript" src="js/foot.js"></script>
</body>
</html>