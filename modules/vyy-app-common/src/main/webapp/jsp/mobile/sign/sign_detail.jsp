<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<!DOCTYPE html>
<html>
<head lang="en">
<title>签到</title>
<%@ include file="../../include/head.jsp"%>
<link rel="stylesheet" href="css/communal.css">
<link rel="stylesheet" href="css/signIn-theme.css">
<script type="text/javascript" src="js/jquery-1.8.3.min.js"></script>
<script type="text/javascript" src="js/public-plug-in.js"></script>
<script src='js/jweixin-1.0.0.js'></script>
<script src='js/eJweixin-1.0.0.js'></script>
<style type="text/css">
.loading {
	width: 134px;
	height: 94px;
	text-align: center;
	background: rgba(0, 0, 0, 0.6);
	color: #fff;
	border-radius: 4px;
	margin: auto;
	font-size: 14px;
	z-index: 1000;
	position: absolute;
}

.oneLoading>div:first-child {
	padding-top: 20px;
}
/* 第二种提示 */
.twoPrompt {
	position: fixed;
	top: 0;
	right: 0;
	bottom: 0;
	left: 0;
	background: rgba(0, 0, 0, 0.4);
	z-index: 5000;
	font-size: 14px;
}

.twoPromptCon {
	width: 240px;
	border: 1px solid #aeaeae;
	border-radius: 4px;
	background: #fff;
	margin: auto;
}

.twoPromptCon header {
	/* font-weight: 600; */
	text-align: center;
	margin-top: 15px;
}

.twoPromptCon div {
	width: 200px;
	outline: none;
	resize: none;
	margin-left: 20px;
	margin-top: 20px;
	padding-bottom: 10px;
}

.twoPromptCon footer {
	margin-top: 20px;
	border-top: 1px solid #aeaeae;
	height: 50px;
}

.twoPromptCon footer button {
	width: 50%;
	height: 50px;
	line-height: 50px;
	text-align: center;
	box-sizing: border-box;
	float: left;
	background: #fff;
	border-radius: 0px 0px 4px 4px;
	color: #1f55db;
}

.twoPromptCon footer button:first-child {
	border-right: 1px solid #aeaeae;
}

/*zedd 12.1  */
.signInWrap {
	background: #fff;
}

.theme-address {
	padding: 10px;
	font-size: 14px;
	line-height: 26px;
	color: #222;
	border-bottom: solid 1px #ddd;
}

.theme-address a {
	font-size: 13px;
	color: #00A795
}

.theme-address span {
	color: #666
}

.theme-address i {
	margin-right: 5px;
	font-size: 16px;
	color: #F65C09
}

.theme-cover {
	display: none;
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background: rgba(0, 0, 0, 0.5);
	z-index: 5001;
}

.theme-nearby {
	display: none;
	position: fixed;
	top: 20%;
	left: 5%;
	width: 90%;
	height: 60%;
	background: #fff;
	border-radius: 5px;
	z-index: 5002;
}

.theme-nearby li {
	border-bottom: solid 1px #ddd;
	padding: 12px 10px 12px 15px;;
	font-size: 14px;
	color: #333;
	cursor: pointer;
}

.theme-nearby ul {
	height: 85%;
	overflow-y: auto;
}

.theme-nearby h5 {
	padding: 10px;
	background: #eee;
	border-radius: 5px 5px 0 0;
	font-size: 14px;
	color: #333;
}

.theme-photo h5 {
	font-size: 14px;
	font-weight: normal;
}

.selectPeopleList,.canningCopyList {
	background: none
}

.addCanningCopy {
	border-bottom: solid 1px #ddd;
}

#addScanCopy {
	border: dashed 2px #ccc;
	line-height: 50px;
	margin-top: 16px;
	box-sizing: border-box;
}

#addScanCopy em {
	color: #aaa;
	font-size: 26px;
	background: none;
}

.signTool {
	background: #EBEBEB;
	text-align: center;
}

.signTool a {
	display: inline-block;
	font-size: 17px;
	height: 38px;
	line-height: 38px;
	width: 95%;
	color: #fff;
	margin: 20px 2%
}

.signTool a i {
	margin-right: 15px;
	color: #fff;
	font-size: 17px;
}

.theme-signIn {
	background: #00A795
}

.theme-signOut {
	background: #D7CCAD
}

.theme-note {
	margin: 10px;
}

.theme-note textarea {
	width: 100%;
	height: 100px;
	border: solid 1px #ddd;
	padding: 10px;
	box-sizing: border-box;
	resize: none;
	font-size: 14px;
	color: #333;
}

.theme-sign-info {
	color: #888;
	position: relative;
}

.theme-sign-info p,.theme-sign-info div {
	display: block;
	font-size: 14px;
	line-height: 26px;
	word-break: break-all;
}

.theme-photo,.signInCon,.signOutCon {
	padding: 0 10px;
}

.signInCon,.signOutCon {
	padding-bottom: 10px;
	padding-top: 6px;
	border-bottom: solid 1px #ddd;
}

.add-remark {
	font-size: 13px;
	color: #00A795
}

.theme-sign-info p.signOk {
	position: absolute;
	right: 10px;
	top: 10px;
	color: #ddd;
	font-size: 40px;
}

.theme-remark {
	color: #333
}

.theme-remark span {
	color: #888
}

.theme-photo ul {
	clear: both;
	overflow: hidden;
}

.theme-photo li {
	float: left;
	margin: 10px 10px 10px 0;
	width: 50px;
	height: 50px;
	border-radius: 5px;
	box-sizing: border-box;
	overflow: hidden;
}

.theme-photo img {
	width: 50px;
	height: 50px;
}

.nearByClose {
	float: right;
	font-size: 16px;
	color: #444;
	margin-top: -2px;
}

.themeTime {
	margin-top: 5px;
	color: #eee
}

.themeWarp>header {
	height: auto;
	padding: 10px 10px 20px;
	background: #06b596;
	color: #fff;
}

.modalRemark {
	z-index: 88
}

.remarkBox textarea {
	font-size: 14px
}

header .themeTit {
	margin-bottom: 10px;
	font-size: 20px;
	line-height: 30px;
	word-break: break-all;
	word-wrap: break-word;
}

.themeTime i {
	font-size: 16px;
}

.selectPeopleTit,.canningTit {
	font-size: 14px;
}

.btnDisable {
	background: #777 !important;
}

.switch-address {
	height: 24px;
}
</style>
</head>
<body>
	<!-- 加载样式 -->
	<div class="screenModal myHide" id="onLoading">
		<div class="oneLoading loading" id="loading">
			<div>
				<img src="images/smallLoading.gif">
			</div>
			<div>加载中...</div>
		</div>
	</div>
	<!-- 提示 -->
	<div class="twoPrompt myHide">
		<div class="twoPromptCon">
			<header>温馨提示</header>
			<div>当前未到签退时间,是否确定签退?</div>
			<footer>
				<button type="reset">取消</button>
				<button type="submit">确定</button>
			</footer>
		</div>
	</div>
	<!--  -->
	<div class="themeWarp">
		<header>
			<div class="themeTit">${signInfo.theme}</div>
			<div class="themeTime">
				<i class="fa fa-clock-o"></i> <span>${time}</span> <span>${date}</span>
			</div>
		</header>
		<!-- signInWrap -->
		<div class="signInWrap">
			<!-- 备注弹出框Start -->
			<div class="modalRemark myHide">
				<div class="remarkBox">
					<form method="get" action="">
						<header>签到备注</header>
						<textarea id="remarkText"></textarea>
						<footer>
							<span id="cancel">取消</span> <span id="save">保存</span>
						</footer>
					</form>
				</div>
			</div>
			<!-- 备注弹出框End -->
			<div id="theme-address" class="theme-address">
				<input type="hidden" value="0" id="JhiddenAddress" />
				<div>
					<i class="fa fa-map-marker"></i>您当前的位置：
					<p>
						<span id="Jaddress">正在定位...</span>
					</p>
				</div>
				<c:if
					test="${(sign_in eq null) or (sign_in ne null and sign_out eq null)}">
					<div class="switch-address">
						<a href="javascript:;" id="Jswitch" style="display: none">地址微调</a>
					</div>
				</c:if>
			</div>
			<!-- 附近 Start -->
			<div class="theme-cover"></div>
			<div id="Jnearby" class="theme-nearby"></div>
			<!-- 附近 End -->
			<!-- 照片 Start -->
			<c:if
				test="${(sign_in eq null) or (sign_in ne null and sign_out eq null)}">
				<div id="scanCopyList" class="addCanningCopy">
					<div class="canningTit">
						添加照片 （<span id="JphotoNum">0</span> / 3）
					</div>
					<div class="canningCopyList">
						<div class="canningCopy" id="addCanningCopy">

							<div id="addScanCopy" class="addCanningCopyBtn">
								<em class="fa fa-camera"></em>
							</div>

						</div>
					</div>
				</div>
			</c:if>
			<!-- 照片 End -->

			<!-- 签到签退按钮 Start -->
			<div class="signTool">
				<!-- 进入页面情况 -->
				<a href="javascript:;" id="JsignInBtn" style="display: none;"
					class="theme-signIn" btype="signButton" signType="0"
					startTime="${signInfo.beginTime.getTime()}"><i
					class="fa fa-sun-o"></i>签到</a> <a href="javascript:;" id="JsignOutBtn"
					style="display: none;" class="theme-signOut" btype="signButton"
					signType="1" endTime="${signInfo.endTime.getTime()}"><i
					class="fa fa-moon-o"></i>签退</a>
			</div>
			<!-- 签到签退按钮 End -->
			<!-- 签到签退信息 Start -->
			<div class="theme-sign-info JsignInInfo" id="JsignInInfo">
				<c:if test="${sign_in ne null}">
					<c:if
						test="${ signInAccessoryInfor != null and signInAccessoryInfor.size() gt 0}">
						<div class="theme-photo">
							<ul>
								<c:forEach items="${ signInAccessoryInfor}" var="item">
									<li accessoryId="${item.id}"><img
										src="${resPath}/01/imageZoom/${item.fileName}"
										data-original="${resPath}/01/resource/${item.fileName}">
									</li>
								</c:forEach>
							</ul>
						</div>
					</c:if>
					<div class="signInCon">
						<p class="signOk">
							<i class="fa fa-sun-o"></i>
						</p>
						<span class="signInAdd">${sign_in.location}</span> <span>${sign_in.ssignTime}</span>
						<div id="JsignInRemark" class="theme-remark">
							<c:if test="${(sign_in.remark ne null and sign_in.remark ne '')}">
								<div>备注：${sign_in.remark}</div>
							</c:if>
							<a href="javascript:;" class="add-remark" id="signInRemark"
								attendType="0">添加备注</a>
						</div>
					</div>
				</c:if>
			</div>

			<div class="theme-sign-info JsignOutInfo" id="JsignOutInfo">
				<c:if test="${sign_out ne null}">
					<c:if
						test="${ signOutAccessoryInfor != null and signOutAccessoryInfor.size() gt 0}">
						<div class="theme-photo">
							<ul>
								<c:forEach items="${ signOutAccessoryInfor}" var="item">
									<li accessoryId="${item.id}"><img
										src="${basePath}res/imageZoom/${item.fileName}"
										data-original="${basePath}res/resource/${item.fileName}">
									</li>
								</c:forEach>
							</ul>
						</div>
					</c:if>
					<div class="signOutCon">
						<p class="signOk">
							<i class="fa fa-moon-o"></i>
						</p>
						<span class="signOutAdd">${sign_out.location}</span> <span>${sign_out.ssignTime}</span>
						<div id="JsignOutRemark" class="theme-remark">
							<c:if
								test="${(sign_out.remark ne null and sign_out.remark ne '')}">
								<div>备注：${sign_out.remark}</div>
							</c:if>
							<a href="javascript:;" class="add-remark" id="signOutRemark"
								attendType="1">添加备注</a>
						</div>
					</div>
				</c:if>
			</div>
			<!-- 签到签退信息 End -->
		</div>
		<!-- /signInWrap  -->
	</div>
	<script src="js/zoom_app.js"></script>
	<script>
	var signature = ${signature};
	var jsApiList = ['getLocation', 'chooseImage', 'uploadImage', 'previewImage'];
	$(function() {
	    $.eWeixinJSUtil.init(signature, jsApiList);
	    var handleSignIn = function() { //根据腾讯地图获取地址和附近地址
	        var showAddress = function(data) { //展示地址和附近地址列表拼接
	            var address = data.result.formatted_addresses.recommend;
	            var nearby = data.result.pois;
	            var nLen = nearby.length;
	            $("#Jaddress").html(address);
	            if(nLen){
	                var nearbyWrap = $("#Jnearby");
	                var $title = $('<h5>请选择合适的地址<a href="javascript:;" class="nearByClose" id="JnearByClose"><i class="fa fa-times"></i></a></h5>');
	                var $ul = $('<ul></ul>');
	                var currentAddress = $("#Jaddress").html();
	                $('<li style="color:#F65C09;">'+currentAddress+'</li>').appendTo($ul);
	                $title.appendTo(nearbyWrap);
	                $ul.appendTo(nearbyWrap);
	                var n= [],nearbyAry=[],nearbyHtml;
	                for(var i=0;i<nLen;i++){
	                  var nAddress = nearby[i].address;
	                  n.push(nAddress);
	                }
	             	  //去重算法
	                var json = {};  
	                for (var i = 0; i < n.length; i++) {  
	                    json["a"+n[i]] = n[i];  
	                }  
	                // 去重结果  
	                var str = "";  
	                for (var key in json) {  
	              	  nearbyAry.push('<li>'+json[key]+'</li>');
	                }
	                nearbyHtml = nearbyAry.join("");
	                $(nearbyHtml).appendTo($ul);
	              }
	        }
	        var switchAddress = function() { //地址微调
	            $(".theme-cover").click(function() {
	                if ($("#Jnearby").is(":visible")) {
	                    $("#Jnearby,.theme-cover").hide();
	                }
	            });

	            $("#Jnearby").on("click", "li",
	            function() {
	                var thisAddress = $(this).html();
	                $("#Jaddress").html(thisAddress);
	                $("#Jnearby,.theme-cover").hide();
	            });
	            $("#Jnearby").on("click", "#JnearByClose",
	            function() {
	                $("#Jnearby,.theme-cover").hide();
	            });

	            $("#Jswitch").on("click",
	            function() {
	                $("#Jnearby,.theme-cover").show();
	            });
	        }
	        var doSign = function(address, signType) { //签到签退数据提交
	            var accessoryIds = [];
	            $(".canningCopy>div[accessoryId]").each(function() {
	                accessoryIds.push($(this).attr("accessoryId"));
	            });
	            $.ajax({
	                url: 'mobile/sign/doSign.action',
	                type: 'post',
	                data: {
	                    signInfo: encodeURI(JSON.stringify({
	                        signId: "${signInfo.id}",
	                        location: address,
	                        attendType: signType,
	                        accessoryInfo: {
	                            mediaIds: accessoryIds
	                        }
	                    }))
	                },
	                beforeSend: function() {
	                    $("#onLoading").show();
	                },
	                complete: function() {
	                    $("#onLoading").hide();
	                    $("a[btype=signButton]").removeClass("btnDisable");
	                },
	                success: function(data, textStatus) { //成功后提示
	                    if (data.status == 0) { //签到
	                        var value = data.value;
	                        if ("0" == signType) {
	                            var photoLen = $("#addCanningCopy>div[accessoryId]").find("img").length;
	                            var picList = $('<div class="theme-photo"><ul></ul></div>');
	                            $("#JsignInInfo").append(picList);
	                            if (photoLen != 0) {
	                                for (var i = 0; i < photoLen; i++) {
	                                    var img = $("#addCanningCopy>div[accessoryId]").find("img").eq(i);
	                                    var imgSrc = img.attr("src");
	                                    var imgOriginal = img.attr("data-original");
	                                    var imgli = $('<li><img src="' + imgSrc + '" data-original="' + imgOriginal + '"/></li>');

	                                    $("#JsignInInfo .theme-photo ul").append(imgli);
	                                }
	                            }

	                            var signInInfo = '<div class="signInCon"><p class="signOk"><i class="fa fa-sun-o"></i></p><p class="address" id="JsignInAddress">' + address + '</p>' + '<p class="time" id="JsignInTime">' + value.signTime + '</p>' + '<div id="JsignInRemark" class="theme-remark"><a href="javascript:;" class="add-remark" attendType="0">添加备注</a></div></div>';
	                            $("#JsignInInfo").append($(signInInfo));

	                            var scanCopyListHtml = '<div class="canningTit">添加照片 （<span id="JphotoNum">0</span> / 3）</div>' + '<div class="canningCopyList">' + '<div class="canningCopy" id="addCanningCopy">' + '<div id="addScanCopy" class="addCanningCopyBtn">' + '<em class="fa fa-camera"></em>' + '</div></div></div>';
	                            $("#scanCopyList").html(scanCopyListHtml);

	                            $("a[signType=0]").hide();
	                            $("a[signType=1]").show();
	                            $(".addRemark").show();
	                            //成功提示
	                            $("body").toast({
	                                msg: "签到成功",
	                                type: 1,
	                                callMode: "func",
	                                end: function() {
	                                    $("#JsignInBtn").remove();
	                                    $("#JsignOutBtn").show();
	                                }
	                            });
	                        } else if ("1" == signType) { //签退
	                            var photoLen = $("#addCanningCopy>div[accessoryId]").find("img").length;
	                            var picList = $('<div class="theme-photo"><ul></ul></div>');
	                            $("#JsignOutInfo").append(picList);
	                            if (photoLen != 0) {
	                                for (var i = 0; i < photoLen; i++) {
	                                    var img = $("#addCanningCopy>div[accessoryId]").find("img").eq(i);
	                                    var imgSrc = img.attr("src");
	                                    var imgOriginal = img.attr("data-original");
	                                    var imgli = $('<li><img src="' + imgSrc + '" data-original="' + imgOriginal + '"/></li>');

	                                    $("#JsignOutInfo .theme-photo ul").append(imgli);
	                                }
	                            }
	                            var signOutInfo = '<div class="signOutCon"><p class="signOk"><i class="fa fa-moon-o"></i></p><p class="address" id="JsignInAddress">' + address + '</p>' + '<p class="time" id="JsignInTime">' + value.signTime + '</p>' + '<div id="JsignOutRemark" class="theme-remark"><a href="javascript:;" class="add-remark" attendType="1">添加备注</a></div></div>';
	                            $("#JsignOutInfo").append($(signOutInfo));

	                            $("a[signType=1]").hide();
	                            //成功提示
	                            $("body").toast({
	                                msg: "签退成功",
	                                type: 1,
	                                callMode: "func",
	                                end: function() {
	                                    //成功签退删除上传照片功能
	                                    $("#scanCopyList,#Jswitch,#JsignOutBtn,#theme-address").remove();
	                                }
	                            });
	                        }

	                    } else {
	                        $("body").toast({
	                            msg: "请求失败，请稍后再试",
	                            type: 1,
	                            callMode: "func"
	                        });

	                    }
	                    $("a[btype=signButton]").removeClass("btnDisable");
	                },
	                error: function(XMLHttpRequest, textStatus, errorThrown) {
	                    $("body").toast({
	                        msg: "请求失败，请稍后再试",
	                        type: 1,
	                        callMode: "func"
	                    });
	                    $("a[btype=signButton]").removeClass("btnDisable");
	                }

	            });

	        }
	        var signAction = function() {
	            $("a[btype=signButton]").on("click",
	            function() {
	                // $("body").append($("<div id='Jsigning'></div>"));//遮罩，防止重复点击
	                if (!$(this).hasClass("btnDisable")) { //按钮置灰
	                    $(this).addClass("btnDisable");
	                    var signType = $(this).attr("signType");
	                    var isGetAddress = $("#JhiddenAddress").val();
	                    var isGetPhoto = $("#JphotoNum").html();
	                    if ("0" == isGetAddress && "0" == isGetPhoto) { //没有定位成功且没有上传照片，提示
	                        $("body").toast({
	                            msg: "没有定位成功，可拍照片签到",
	                            type: 1,
	                            callMode: "func",
	                            end: function() {
	                                $("a[btype=signButton]").removeClass("btnDisable");
	                            }
	                        });
	                        return false;
	                    } else if ("0" != isGetPhoto && "0" == isGetAddress) {
	                        var address = '没有定位到您的准确位置';
	                        doSign(address, signType);
	                    } else if ("0" != isGetAddress) {
	                        if ("1" == signType) {
	                            //需要提示
	                            var endTime = $(this).attr("endTime");
	                            var nowTime = new Date().getTime();
	                            if (nowTime < endTime) {
	                                //获得浏览器的高度
	                                var windowH = window.innerHeight;
	                                var windowW = window.innerWidth;
	                                $(".twoPromptCon").css({
	                                    "top": (windowH / 2 - 50),
	                                    "left": (windowW / 2 - 120)
	                                });
	                                $(".twoPrompt").show();
	                            } else {
	                                var address = $("#Jaddress").html();
	                                doSign(address, signType);
	                            }
	                        } else {
	                            var address = $("#Jaddress").html();
	                            doSign(address, signType);
	                        }
	                    }
	                }

	            });
	          //根据情况判断显示是添加备注，还是修改备注(李玉霞)
                if($("#JsignInRemark>div").length > 0){
                    $("#signInRemark").text("修改备注");
                }
                if($("#JsignOutRemark>div").length > 0){
                    $("#signOutRemark").text("修改备注");
                }
	            /* 第二种提示 */
	            $(".twoPromptCon footer button").click(function() {
	                $(".twoPrompt").hide();
	                var type = $(this).attr("type");
	                var address = $("#Jaddress").html();
	                if ("submit" == type) {
	                    doSign(address, "1");
	                } else {
	                    $("a[btype=signButton]").removeClass("btnDisable");
	                }
	            });
	            //添加备注
	            $("#JsignInInfo,#JsignOutInfo").on("click", ".add-remark",
	            function() {
	                var papa = $(this).parents(".theme-remark").attr("id"); 
	                var $parent = $(this).parents(".theme-sign-info").attr("id"),
                    _text = "";
	                if($parent == "JsignInInfo" && $("#JsignInRemark>div").length > 0){
	                    _text = $("#JsignInRemark>div").text().substring(3);
	                }else if($parent == "JsignOutInfo" && $("#JsignOutRemark>div").length > 0){
	                    _text = $("#JsignOutRemark>div").text().substring(3);
	                }
	                $("#remarkText").val($(".remarkDetail").find("div").html());
	                $(".modalRemark").show().attr("data-for", papa);
	                $(".modalRemark").show().attr("attendType", $(this).attr("attendType"));
	                $("#remarkText").val(_text);
	            });
	            //备注 
	            $("#cancel").on("click",
	            function() {
	                $(".modalRemark").hide();
	            });
	            //保存备注
	            $("#save").on("click",
	            function() {
	                var remarkText = $("#remarkText").val();
	                if (!remarkText || "" == remarkText) {
	                    $(".modalRemark").hide();
	                    return;
	                }

	                $.ajax({
	                    url: 'mobile/sign/doRemark.action',
	                    type: 'post',
	                    data: {
	                        remarkInfo: encodeURI(JSON.stringify({
	                            signId: "${signInfo.id}",
	                            remark: $("#remarkText").val(),
	                            attendType: $(".modalRemark").attr("attendType"),
	                            type: $("#addRemark").attr("btype")
	                        }))
	                    },
	                    beforeSend: function() {
	                        $("#onLoading").show();
	                    },
	                    complete: function() {
	                        $("#onLoading").hide();
	                    },
	                    success: function(data, textStatus) {
	                        if (data.status == 0) {
	                            var remarkText = $("#remarkText").val();
	                            if ( !! remarkText && "" != remarkText) {
	                                var forIt = $(".modalRemark").attr("data-for");
	                                if ("JsignInRemark" == forIt) {
	                                    $("#JsignInRemark").html('<div>备注：' + remarkText + '</div><a class="add-remark" attendType="0" href="javascript:;">修改备注</a>');
	                                } else {
	                                    $("#JsignOutRemark").html('<div>备注：' + remarkText + '</div><a class="add-remark" attendType="1" href="javascript:;">修改备注</a>');
	                                }
	                                $("#remarkText").val("");
	                                $(".remarkCon").show();
	                            }
	                            $(".modalRemark").hide();
	                        } else {
	                            $("body").toast({
	                                msg: "请求失败，请稍后再试",
	                                type: 1,
	                                callMode: "func"
	                            });
	                        }
	                    },
	                    error: function(XMLHttpRequest, textStatus, errorThrown) {
	                        $("body").toast({
	                            msg: "请求失败，请稍后再试",
	                            type: 1,
	                            callMode: "func"
	                        });
	                    }
	                });
	            });

	        }
	        var uploadMat = function() {
	            //上传图片
	            $("#scanCopyList").on("click", "#addScanCopy",
	            function() {
	                var $callback = function(data) {
	                    for (var i = 0; i < data.value.length; i++) {
	                        var $str = '<div class="" accessoryId=' + data.value[i].accessoryId + '>' + '<a href="javascript:void(0)" data-original=' + data.value[i].original + '><img src=' + data.value[i].thumb + ' data-original=' + data.value[i].original + '></a>' + '<i></i>' + '</div>';
	                        $("#scanCopyList .canningCopy").prepend($str);
	                        var photoNum = parseInt($("#JphotoNum").html()) + 1;
	                        $("#JphotoNum").html(photoNum);
	                        if (photoNum == 3) {
	                            $("#addScanCopy").hide();
	                        }
	                    }
	                };

	                if ($.eWeixinJSUtil._wx_loaded) {
	                    $.eWeixinJSUtil.cameraImg($callback);
	                } else {
	                    $("body").toast({
	                        msg: "加载中，请稍后再试",
	                        type: 1,
	                        callMode: "func"
	                    });
	                }

	            });
	        }
	        //点击看大图
	        var lookBigImg = function() {
	            $(document).on("click", ".canningCopy>div[accessoryId] img",
	            function() {
	                var imgUrlArry=[],myIndex="";
                    $(this).parents("div[accessoryId]").attr("data-me","true");
                    $(".canningCopy>div[accessoryId]").each(function(index,el){
                        if(el.hasAttribute("data-me")){
                            myIndex=index;
                        }
                        imgUrlArry.push($(this).children("a").attr("data-original"));
                    });
                    var selectUrl=$(".canningCopy>div[accessoryId]").eq(myIndex).children("a").attr("data-original");
                    var imgUrl={
                        currentImgUrl : selectUrl,//当前显示图片的http链接
                        bigImgUrl : imgUrlArry//需要预览的图片http链接列表
                    };
                    $.eWeixinJSUtil.previewImg(imgUrl);
                    $(this).parents("div[accessoryId]").removeAttr("data-me");
	            });
	            $(document).on("click", ".theme-photo img",
	            function() {
	                var imgUrlArry = [],
	                myIndex = "";
	                $(this).parent().attr("data-me", "true");
	                $(".theme-photo li").each(function(index, el) {
	                    if (el.hasAttribute("data-me")) {
	                        myIndex = index;
	                    }
	                    imgUrlArry.push($(this).children().attr("data-original"));
	                });
	                var selectUrl = $(".theme-photo li").eq(myIndex).children("img").attr("data-original");
	                var imgUrl = {
	                    currentImgUrl: selectUrl,
	                    //当前显示图片的http链接
	                    bigImgUrl: imgUrlArry
	                    //需要预览的图片http链接列表
	                };
	                $.eWeixinJSUtil.previewImg(imgUrl);
	                $(this).parent().removeAttr("data-me");
	            });
	        }
	        //删除照片
	        var delImg = function() {
	            $(document).on("click", ".canningCopy>div i",
	            function() {
	                var $this = $(this);
	                $("body").toast({
	                    msg: "是否确认删除该图片",
	                    type: 5,
	                    callMode: "func",
	                    end: function() {
	                        var accessoryId = $this.parent().attr("accessoryId");
	                        $.ajax({
	                            url: "mobile/common/deleteAccessory.action",
	                            data: {
	                                accessoryId: accessoryId
	                            },
	                            type: "post",
	                            dataType: "json",
	                            beforeSend: function() {
	                                $("body").loading({
	                                    msg: "删除中...",
	                                    move: "show"
	                                });
	                            },
	                            complete: function() {
	                                $("body").loading({
	                                    move: "hide"
	                                });
	                            },
	                            success: function(res) {
	                                if (0 == res.status) {
	                                    $("body").loading({
	                                        move: "hide"
	                                    });
	                                    var photoNum = parseInt($("#JphotoNum").html()) - 1;
	                                    $("#JphotoNum").html(photoNum);
	                                    $this.parent().remove();
	                                    $("body").zoom({
                                            parentName : ".canningCopy",
                                            boxName:".canningCopyImg",
                                            tagName : "a"
                                        });
	                                    /*if (photoNum < 3) {
	                                        $("#addScanCopy").show();
	                                    }*/
	                                } else {
	                                    $("body").toast({
	                                        msg: res.errorMsg,
	                                        type: 1,
	                                        callMode: "func"
	                                    });
	                                }
	                            },
	                            error: function() {
	                                $("body").toast({
	                                    msg: "系统异常，请稍后再试",
	                                    type: 1,
	                                    callMode: "func"
	                                });
	                            }
	                        });
	                    }
	                });

	            });
	        }
	        var signIn = "${sign_in ne null?0:1}";
	        var signOut = "${sign_out ne null?0:1}";
	        return {
	            init: function() {
	                if ("1" == signIn || "1" == signOut) { //没有签订 或 没有签退 才获取当前位置
	                    if ("1" == signIn) {
	                        $("#JsignInBtn").show();
	                    }
	                    if ("1" == signOut && "0" == signIn) {
	                        $("#JsignOutBtn").show();
	                    }
	                    $.eWeixinJSUtil.getLocation(function(data) {
	                        if (data.status == 0) {
	                            $("#JhiddenAddress").val("1");
	                            $("#Jswitch").show();
	                            showAddress(data.value);
	                        } else {
	                            $("body").toast({
	                                msg: "系统异常，请稍后再试",
	                                type: 1,
	                                callMode: "func"
	                            });
	                        }
	                    });
	                } else {
	                    $("#theme-address").remove();
	                }
	                switchAddress(); //地址微调				
	                uploadMat(); //上传资料				   
	                delImg(); //删除材料
	                signAction(); //按钮点击
	                lookBigImg(); //查看大图	
	            }
	        }
	    } ();
	    handleSignIn.init();
	});
	</script>
	<script type="text/javascript">
		$(document).ready(function() {
			//获得浏览器的高度
			var windowH = window.innerHeight;
			var windowW = window.innerWidth;
			//设置发起签到模态框的top、left
			$("#loading").css({
				"top" : (windowH / 2 - 50),
				"left" : (windowW / 2 - 67)
			});
			//客户端上传图片
        $("body").clientUploadImg({
            uploadUrl:"mobile/common/uploadAccessory.action",
              boxName :'#addScanCopy',
              numBoxName:"#JphotoNum",
              maxImgLen : 3,
              imgType:"jpg|png|JPG|PNG"
        });
      //pc预览图片
      $("body").pcPreviewImg({
           parentName : ".JsignOutInfo",
           boxName:"li",
           tagName : "img"
      });
      $("body").pcPreviewImg({
          parentName : ".JsignInInfo",
          boxName:"li",
          tagName : "img"
      });
		});
	</script>
	<!-- 版本信息 -->
	<script type="text/javascript" src="js/foot.js"></script>
</body>