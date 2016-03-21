<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> 
<!DOCTYPE html>
<html>
<head lang="en">
<%@ include file="../../include/head.jsp"%>
<title>快递到了</title>
<link rel="stylesheet" href="css/expressage.css">
<link rel="stylesheet" href="css/contact.css">
</head>
<body>
<div class="warp">
    <header class="blurBg">
        在此录入快递信息
    </header>
   <!--  <form id="registerForm"> -->
        <div class="formList clear">
            <div class="listRight addRecipients">
                <input name="recipients" id="recipients" class="listRightPadding5" value="" readonly data-con="*" data-empty="请选择收件人" placeholder="请选择收件人">
                <b class="fa fa-plus-circle addIcon"></b>
            </div>
            <div class="listLeft listNameWidth5">
                <span>收&nbsp;件&nbsp;人&nbsp;：</span>
            </div>
        </div>
        <div class="formList clear">
            <div class="listRight addTrackingNum">
                <input type="text" name="trackingNum" id="trackingNum" class="listRightPadding5" placeholder="请输入单号" onkeyup="return clearNoNum(this);" onchange="return clearNoNum(this);"/>
                <button class="b01" id="b01"><b class="fa fa-qrcode addIcon" id="scanning"></b></button>
            </div>
            <div class="listLeft listNameWidth5">
                <span>快递单号：</span>
            </div>
        </div>
        <div class="formList clear">
            <div class="listRight addTrackingNum">
           	 	<!-- <input type="text" name="trackingNum" id="trackingBy" class="listRightPadding5"> -->
                <select name="trackingBy" id="trackingBy" class="listRightPadding5">
               		<option>请选择</option>
                </select>
               <!--  <b class="fa fa-qrcode addIcon"></b> -->
            </div>
            <div class="listLeft listNameWidth5">
                <span>所属快递：</span>
            </div>
        </div>
        <div class="formList clear">
            <div class="listRight">
                <input name="payAmount" id="payAmount" class="listRightPadding8"  placeholder="请输入金额" onkeyup="return clearNoNum(this)" onchange="return clearNoNum(this);">
                <b class="addIcon wordIcon">元</b>
            </div>
            <div class="listLeft listNameWidth8">
                <span>到付金额(选填)：</span>
            </div>
        </div>
    <!-- </form> -->
    <div class="btnGroup">
        <button type="button" class="singleBtn warpBtn mainBg" id="submitBtn" data-url="">确认录入</button>
    </div>
    <!-- 隐藏域 -->
    <input type="hidden" id="courierId"  value="${courier.id}"/><!--  -->
    <!-- 隐藏域 -->
</div>
<footer class="copyright">© 微移云技术支持</footer>
<!-- 人员列表End -->
<div class="membersListWarp myHide"></div>
<script src="js/jquery-1.8.3.min.js"></script>
<script src="js/eSelectPeople-1.1.js"></script>
<script src="js/jweixin-1.0.0.js"></script>
<script src="js/eJweixin-1.0.0.js"></script>
<script src="js/public-plug-in.js"></script>
<script src="js/mobile/expressage/register.js"></script>
<script type="text/javascript">
    var signature = ${signature};
    var jsApiList = ['chooseImage','scanQRCode','previewImage','closeWindow'];
    var flag = '${flag}';
    $(function(){
    	$.eWeixinJSUtil.init(signature,jsApiList);
    });
    </script>
</body>
</html>
