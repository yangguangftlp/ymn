<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head lang="en">
    <%@ include file="../../include/head.jsp" %>
    <title>问题反馈</title>
    <link rel="stylesheet" href="css/font-awesome.min.css">
    <link rel="stylesheet" href="css/global.css">
    <link rel="stylesheet" href="css/common.css">
    <link rel="stylesheet" href="css/feedback.css">
</head>
<body>
    <div class="warp">
        <header>
            在这填写你的问题
        </header>
        <form id="fromFeedback">
            <div class="feedbackList clear">
                <div class="listRight">
                    <textarea rows="4" class="listRightPadding5" name="problem" id="problem" placeholder="例如：xx页面交互不合理" data-con="*" data-empty="请填写需要提交的问题"></textarea>
                </div>
                <div class="listLeft listNameWidth5">
                    <span>问题内容：</span>
                </div>
            </div>
            <div class="feedbackList clear">
                <div class="listRight">
                    <textarea rows="4" class="listRightPadding5" name="suggest" id="suggest" placeholder="例如：去掉弹出框"></textarea>
                </div>
                <div class="listLeft listNameWidth5">
                    <span>修改建议：</span>
                </div>
            </div>
        </form>

        <div class="addCanningCopy" id="scanCopyList">
            <div class="canningTit">页面截图：</div>
            <div class="canningCopyList">
                <div class="canningCopy">
                    <div class="addCanningCopyBtn" id="addScanCopy">
                        <img src="images/addImg.png">
                    </div>
                </div>
            </div>
        </div>
        <div class="btnGroup">
            <button type="button" class="doubleBtn warpBtn blurBg" id="submitBtn" data-url="mobile/feedback/submitFeedback.action">提交</button>
            <button type="button" class="doubleBtn warpBtn grayBg" id="cancelBtn" data-url="">取消</button>
        </div>
    </div>
    <script src="js/jquery-1.8.3.min.js"></script>
    <script src="js/public-plug-in.js"></script>
    <script src="js/zoom_app.js"></script>
    <script src="js/mobile/feedback/index.js"></script>
    <script src='js/jweixin-1.0.0.js'></script>
	<script src='js/eJweixin-1.0.0.js'></script>
    <script type="text/javascript">
    	var signature = ${signature};
    	var jsApiList = ['chooseImage','uploadImage','previewImage'];
    </script>
    <!-- 版本信息 -->
<script type="text/javascript" src="js/foot.js"></script>
</body>
</html>