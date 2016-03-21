(function(){
	var isIE=!!window.ActiveXObject;
	var isIE6=isIE&&!window.XMLHttpRequest;
	if(isIE){if(isIE6){alert('您正在使用低版本浏览器,为了保证您能有更好的访问效果,我们建议您使用谷歌Chrome浏览器、火狐Firefox浏览器或者IE7以上版本浏览器！');}}
})();

var contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf("website"));
$(".code img").prop("src",contextPath + "validateCode");
var Component = (function($){
	var module = {};
	var options = {};	
	var $body = $("body");
	$.extend(module,{	
		tipsHtml:[
		      '<div class="modal-wrap">',
			   '<div class="modal-dialog">',
			      '<div class="modal-content">',       
			         '<div class="modal-body">',
			            '<div class="result-tips clearfix">',
						 	'<div class="cell"><img src="images/{0}.png" />&nbsp;&nbsp;</div>',
						 	'<div class="cell {1}">{2}</div></div></div></div></div></div>'        
		],
		showTips:function(type,icon,msg,callback){
			var $tips = $("#tipsModal");
			if($tips.length==0){	
				$tips = $(module.tipsHtml.join("").replace("{0}",icon).replace("{1}",type).replace("{2}",msg));
				$body.append($tips);
			}
			$tips.show();
			setTimeout(function(){
				$tips.hide();
				 if(callback){
			    	callback();
			     } 
			},2000);
			
		},
		regEmail:/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/,
		regTel:/^(1[0-9][0-9])\d{8}$/,
		isMobile:function() {
			return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent);
		},
		//服务端加载数据
		getDataFromServer:function(url,data,callback,option){
			var defaultOption = {
				type : 'post',
				url : url,
				data : data || {},
				dataType : "json",
				error : function(err) {
					Component.showTips("error","error","未知错误，请联系优客互联小伙伴。");
				},
				success : function(data) {
					if (callback) {
						callback(data);
					}
				}
			};

			if (option) {
				$.extend(defaultOption, option);
			}
			$.ajax(defaultOption);

		},
		//封装对象
		addParam : function(params,paramName,paramVal){
			params[paramName] = paramVal;
		}
		
		
	});
	
	return module;
})(window.jQuery);
/**首页**/
var IndexPage = (function($){
	var index = {};
	var options = {
		"regUrl":""
	};
	var $body = {};
	$.extend(index,{
		init:function(config){
			$.extend(options,config);
			_bindEvent();
		}
	});

	function _emailReg(){
		var $reg = $(".idx-reg");
		$(".idx-reg .btn").bind("click",function(){
			var $that = $(this),$parent = $that.parent().parent();
			var data = _validateIpt($parent);
			var str = $.base64.encode(data.email);
			if(data.bool){
				window.location = "reg.html?email="+str;
			}
		});


	}

	function _validateIpt(parent){
		var _email = parent.find(".form-control").val();
		var $error = parent.next(),_bool = true,objdata = new Array();
		_email = $.trim(_email);

		if(_email==null||_email==""){
			$error.text("请输入您的邮箱").show();
			_bool = false;
		}
		if(_email!=""&&!Component.regEmail.test(_email)){
			$error.text("请输入正确的邮箱").show();
			_bool = false;
		}
		Component.addParam(objdata,"email",_email);
		Component.addParam(objdata,"bool",_bool);
		return objdata;
	}

	function _bindEvent(){
		_emailReg();
		//$(".main-content").css("padding-bottom","0");
		
		if (Component.isMobile()){
			$(".phpro-icon").insertAfter("#indexTMark");
			$(".index .phpro-icon").show();
			
		}else {
			$(".idxpro-icon").insertAfter("#indexTMark");
			$(".index .idxpro-icon").show();
		}
		
		$(".swiper-pd ,.php-icon-pd").bind("click",function(){
			var $that = $(this),_divcls = $that.data("pro"),$div = $("."+_divcls);
			$(".idxpro-desc .show").removeClass("show").addClass("hidden");
			$div.show().removeClass("hidden").addClass("show");
		});
		var mySwiper = $('.swiper-container').swiper({
			slidesPerView: 7,
			spaceBetween: 10,
			slidesPerGroup:5,
			calculateHeight: true,
		    simulateTouch:false,
			paginationClickable: true
		});
		$('.swiper-button-prev').on('click', function (e) {
			e.preventDefault();
			var swiper = $('.swiper-container').data('swiper');
			swiper.swipePrev();
		});
		$('.swiper-button-next').on('click', function (e) {
			e.preventDefault();
			var swiper = $('.swiper-container').data('swiper');
			swiper.swipeNext();
		});
		var map = new BMap.Map("map");  // 创建Map实例
		map.enableScrollWheelZoom(true);     //开启鼠标滚轮缩放
		var point = new BMap.Point(118.784589,32.082798);
		map.centerAndZoom(point,15);      // 初始化地图,用城市名设置地图中心点
		var infoBox = new BMapLib.InfoBox(map,'<div class="map-box"><p>联系电话：025-85635699</p><p>QQ在线咨询：987861336</p><p>工作邮箱：hr@vyiyun.com</p><p>地址：南京市新模范马路5号南京科技广场B座13楼</p></div>',{
			boxStyle:{
				"height":"130px",width: "400px"
			},offset:new BMap.Size(0 ,45),closeIconUrl:"images/iw_close.gif",closeIconMargin:"10px 10px 0 0",enableAutoPan: true,align: INFOBOX_AT_TOP
		});
		var newicon = new BMap.Icon("images/gmap_marker.png",new BMap.Size(20,30),{
			infoWindowAnchor:new BMap.Size(10 ,10)
		});
		var marker = new BMap.Marker(point,{
		 icon:newicon
		});  // 创建标注
		map.addOverlay(marker);              // 将标注添加到地图中
		marker.addEventListener("click", function(){
			infoBox.open(marker);
		});
		infoBox.open(marker);

	}
   return index;
})(window.jQuery);

function aboutPage(){
	if (Component.isMobile()){
		$(".about .about-wrap").css("top","0");
	}
}
function customPage(){}
function helpPage(){
	/**help**/
	$(".help-btn-tab .btn").bind("click",function(){
		var $that = $(this),_cname = $that.data("type");
		$(".help-btn-tab .active").removeClass("active");
		$that.addClass("active");
		$(".htab-btn-menu.show").removeClass("show").addClass("hidden");
		$("."+_cname+"-menu").addClass("show").removeClass("hidden");
	});
	$(".htab-btn-menu .btn").bind("click",function(){
		var $that = $(this),_cname = $that.data("pro");
		$(".htab-btn-menu .active").removeClass("active");
		$that.addClass("active");
		$(".hrgtd-module.show").removeClass("show").addClass("hidden");
		$("."+_cname+"-desc").addClass("show").removeClass("hidden");
	});
}
function productPage(){
	var _url = "/wxAuth/wxService/expOa.action";
	/**product**/
	if (Component.isMobile()){
		$(".pro-tjdesc .btn-wrap").hide();
	}
	 $(".pro-tab .btn-tab").click(function(){
		$(".pro-tab .active").removeClass("active");
		 var $that = $(this),_index = $that.data("index"),_id = $that.prop("id");
		 $that.addClass("active");
		 $('#myCarousel').carousel({interval: false});
		 $("#myCarousel").carousel(_index);
		 var pannelId = _id.substring(3,_id.length);
		 $(".pro-tbody-wrap .show").removeClass("show").addClass("hidden");
		 $("#pannel"+pannelId).removeClass("hidden").addClass("show");
	  });
	$(".pro-tjdesc .btn-ty").click(function(){
		var that = $(this),_top = that.offset().top;
		var $parent = $(".proty-box");
		var _height = $parent.height(),_bheight = that.height()+30;
		var _top = _top-_height-_bheight;
		
		$parent.css({
			right:"60px",
			top:_top
		}).show();
		$(".firstStep").show();
		$(".secondStep").addClass("hide");
		$parent.find("#name").val("");
		$parent.find("#phone").val("");
	});
	
	$(".proty-box .close-p").bind("click",function(){
		$(".proty-box").hide();
	});

	$(".proty-box").on("click",".btn",function(event){
		var $btn = $(this);
		var $name = $(".proty-box #name"),_nameVal = $.trim($name.val());
		var $phone = $(".proty-box #phone"),_pVal = $.trim($phone.val());
		var obj = new Array(),
		$nameTips = $name.parents(".pepit-wrap").prev(".help-block");
		$pTips = $phone.parents(".pepit-wrap").prev(".help-block");
		if(_nameVal==""){
			$nameTips.removeClass("hide-v").addClass("show-v");
		}else{
			$nameTips.removeClass("show-v").addClass("hide-v");
			Component.addParam(obj,"name",_nameVal);
		}
		if(_pVal==""){
			$pTips .removeClass("hide-v").addClass("show-v");
		}else{
			$pTips.removeClass("show-v").addClass("hide-v");
			Component.addParam(obj,"phone",_pVal);
		}
		if(_nameVal!=""&&_pVal!=""){
			Component.getDataFromServer(_url,JSON.stringify({
				"name":obj.name,
				"mobileNum":obj.phone
			}),function(data){
				
				if(data.success){
					Component.showTips("success","ok","信息已经注入，请扫描二维码关注微移云体验号",function(){
						$(".firstStep").hide();
						$(".secondStep").removeClass("hide");
					});
					
				}else{
					//showTips:function(type,icon,msg,callback){
					if(data.errorCode=="231009"){
						Component.showTips("error","error","您的信息已经注入，请扫描二维码关注微移云体验号",function(){
							$(".firstStep").hide();
							$(".secondStep").removeClass("hide");
						});
					}else{
						Component.showTips("error","error",data.msg,function(){
							
						});
					}
					
				}
			},{
				beforeSend:function(){
					$btn.prop("disabled","disabled");
				},
				complete:function(){
					$btn.removeProp("disabled");
				},
				async:false
			});
		}
		
	});
	

	$(".proty-box").on("blur","input",function(event){
		var $that = $(this),_val = $.trim($that.val()),$errorTips = $that.parents(".pepit-wrap").prev(".help-block");
		if(_val==""){
			$errorTips.removeClass("hide-v").addClass("show-v");
		}else{
			$errorTips.removeClass("show-v").addClass("hide-v");
		}
	});
	$(".pro-cell").bind("click",function(){
		var $that = $(this),_pro = $that.data("pro");
		window.location = "pdetails.html?pro="+_pro;
	});
	 


}
//修改密码
function findPwd(){
	var editurl = contextPath + "website/retrievePwd.action";
	$(".find-pwd-wrap .btn").bind("click",function(event){
		var $btn = $(this),
		 	protocol = window.location.protocol,
		 	hostname = window.location.hostname,
		 	port = window.location.port ? ":" + window.location.port : "",
			_url = protocol + "//" + hostname + port + "/website/redirectLink.action",
			$email = $("#email"),
			_eVal = $.trim($email.val()),
			obj = new Array();
		$eTips = $email.parent().prev();
		if(_eVal==""){
			$eTips.text("请输入注册时使用的邮箱").removeClass("help-block hide-v").addClass("help-block show-v");
		}else{
			if(!Component.regEmail.test(_eVal)){
				$eTips.text("您输入的邮箱格式有误如：vyiyun@hr.com").removeClass("help-block hide-v").addClass("help-block show-v")
			}else{
				$eTips.text("请输入注册时使用的邮箱").removeClass("help-block show-v");
			}
			Component.addParam(obj,"email",_eVal);
			//Component.addParam(obj,"gourl","http://www.vyiyun.com/reset-pwd.html?u="+_eVal);
			Component.getDataFromServer(editurl,JSON.stringify({
				"email":obj.email,
				"url":_url
			}),function(data){
				if(data.success){
					Component.showTips("success","ok","邮件已发送");
				}else{
					//showTips:function(type,icon,msg,callback){
					Component.showTips("error","error",data.msg,function(){
						
					});
				}
			},{
				beforeSend:function(){
					$btn.text("邮件正在发送，请稍后。").prop("disabled","disabled");
				},
				complete:function(){
					$btn.text("发送").removeProp("disabled");
				}
			});
		}
		
		
	});
	

}
function getQueryString(name){
	var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
	var r = window.location.search.substr(1).match(reg);
	if(r!=null)return  unescape(r[2]); return null;
}
function pDetailPage(){	
	var myparam = getQueryString("pro"),$that = $("[data-pro="+myparam+"]");
	if(myparam != null && myparam.toString().length>1){
		showProGetMenu(myparam,$that);
	}

	function showProGetMenu(div,that){
		if(div!=""&&div!=undefined){
			$(".pd-pro-menu li.active").removeClass("active");
			that.addClass("active");
			$(".pd-pro-desc .show").removeClass("show").addClass("hidden");
			var _divClsStr = ".pd-"+div;
			$(_divClsStr).removeClass("hidden").addClass("show");
			if (!Component.isMobile()){
				var mySwiper = new Swiper(_divClsStr+' .swiper-container',{
					calculateHeight:true,
					grabCursor: true,
					paginationClickable: true,
					slidesPerView: 'auto'
				})
				$(_divClsStr).on('click', '.p-button-prev',function(e){
					mySwiper.swipePrev();
				})
				$(_divClsStr).on('click', '.p-button-next',function(e){
					mySwiper.swipeNext();
				});
			}

		}
	}
	if (Component.isMobile()){
		$(".pd-pro-desc").css("padding","30px 20px");
		$(".p-button-next").hide();
		$(".p-button-prev").hide();
		$(".swiper-container").height("auto");
		$(".swiper-slide").css({
			"width":"100%",
			"float":"none",
			"padding":"20px 0 0 0"
		});

	}else{
		var mySwiper = new Swiper('.pd-qiandao .swiper-container',{
			calculateHeight:true,
			grabCursor: true,
			paginationClickable: true,
			slidesPerView: 'auto'
		});
		$('.pd-qiandao').on('click', '.p-button-prev',function(e){
			mySwiper.swipePrev();
		});
		$('.pd-qiandao').on('click', '.p-button-next',function(e){
			mySwiper.swipeNext();
		});
	}
	$(".pd-pro-menu li").click(function(){
		var $that = $(this),_div = $that.data("pro"),_index = $that.index();
		showProGetMenu(_div,$that);	
	});
}

function regPage(){
	
	$('#myModal').modal('show');
	$("#address").citySelect({prov:"请选择", city:"请选择", dist:"请选择"}); 
	var industryData = arrIndustry,
		$industry = $("#industry"),
		optionArr = new Array();
	$industry.empty();
	for(var i=0;i<industryData.length;i++){
		var item = industryData[i];
		optionArr.push("<option value='"+item.value+"'>"+item.text+"</option>");	
	}
	$industry.append(optionArr.join(""));
	var regUrl = contextPath + "website/register.action";
	var $img = $(".code img");
	$img.click(function(){
		$img.prop("src",contextPath + "validateCode?time="+new Date().getTime());
	});
	/**reg**/
	$('#validateBtn').click(function() {
		var $btn = $(this),arr = validatorForm();
		var params = {
				"userName":arr.email,
				"password":arr.password,
				"corpName":arr.company,
				"contact":arr.contacts,
				"province":arr.province,
				"city":arr.city,
				"county":arr.area,
				"industry":arr.business,
				"mobileNum":arr.phone,
				"validateCode":arr.code
		}
		if(arr.bool){
			Component.getDataFromServer(regUrl,JSON.stringify(params),function(data){
				if(data.success){
					Component.showTips("success","ok","您已注册成功。",function(){
						//注册成功之后直接登陆
						Component.getDataFromServer(loginUrl,JSON.stringify({
							"userName":params.userName,
							"password":params.password
						}),function(data){
							if(data.success){
								window.location.href="http://www.vyiyun.com/manage/indexView.action";
							}else{
								Component.showTips("error","error",data.msg);
							}
						});
					});
				}else{
					//showTips:function(type,icon,msg){
					Component.showTips("error","error",data.msg);
				}
			},{
				beforeSend:function(){
					$btn.text("正在注册，请稍后。").prop("disabled","disabled");
				},
				complete:function(){
					$btn.text("提交").removeProp("disabled");
				}
			});
		};
	});
	$("#defaultForm input").keyup(function(){
		var $that = $(this);
		validatorInput($that);
	});
	$("#defaultForm input").blur(function(){
		var $that = $(this);
		validatorInput($that);
	});
	$("#defaultForm select").blur(function(){
		var $that = $(this);
		validatorSelect($that);
	});
	
}
var loginUrl = contextPath + "website/login.action";
$(function(){
	//<li class="zixun"><i>在线咨询</i></li>	
	var serviceHtml = ['<section class="scroll-fixed" id="scrolltop">',
				'<div class="scroll-wrap"><ul class="rgt-service"><li class="zixun"><a target="_blank" href="http://crm2.qq.com/page/portalpage/wpa.php?uin=800092812&aty=0&a=0&curl=&ty=1" ><i>在线咨询</i></a></li>',
				'<li class="qq"><i>用户QQ群</i><div class="rser-tips hide">',
				'<div class="popup"><span><em></em></span><div class="rstips-wrap"><p class="mt-10">',
				'<img src="images/qq-erweima.gif" /></p>',
				'<p><label>群名称：</label>微移云产品交流群</p><p><label>群&nbsp;&nbsp;&nbsp;号：</label>512030549</p>',
				'</div></div></div></li><li class="telphone"><i>客服电话</i><div class="rser-tips hide">',
				'<div class="popup"><span><em></em></span><div class="rstips-wrap"><h5>025-85635699</h5>',
				'<p class="mt-10">周一至周五 9：00-18：00</p></div></div></div></li><li class="erweima"><i>微信客服</i>',
				'<div class="rser-tips hide"><div class="popup"><span><em></em></span><div class="rstips-wrap">',
				'<img src="images/qq-erweima.gif" /></div></div></div></li></ul></div></section>'];
	if (!Component.isMobile()){
		$("body").append(serviceHtml.join(""));
	}
	//右侧公共部分
	var _ismenter = false, _seltimer = null;
	$(".rgt-service li").bind({
		"mouseenter":function(){
			var $that = $(this),$sub = $that.find(".rser-tips"),_ismenter = true;
			var index = $that.index();
			
			if(index==5){
				return;
			}else{
				$sub.removeClass("hide");
				var _sheight = $sub.height(),_height = $that.height();
				var _stop =_sheight/ 2,_top = _stop - (_height/2);
				$(".rser-tips").addClass("hide");
				if(index==3){
					$sub.animate({top:-(_sheight-_height-10)},10);
					$sub.find("span").animate({"top":"75%","margin-top":0},10);
				}else{
					$sub.animate({top:-_top},10);
				}
				$sub.removeClass("hide");
			}
		},"mouseleave":function(){
			var $that = $(this),$sub = $that.find(".rser-tips");
			_ismenter = false;
			if (_seltimer) clearTimeout(_seltimer);
			_seltimer = setTimeout(function () {
				if (!_ismenter) {
					$sub.addClass("hide");
				}
			}, 100);
		}
	});
	/**login**/
	
	var loginHtml = new Array();
	loginHtml.push('<div class="mark hidden"></div>');
	loginHtml.push('<div class="winbox-pos winbox-login hidden"><div class="winbox-tit"><ul id="myTab" class="nav nav-logtabs"><li class="active"><a data-toggle="tab" href="#account">账号登录</a></li><li><a href="#weixin" data-toggle="tab">微信登录</a></li></ul><button class="close-box"></button></div><div id="loginTabContent" class="tab-content"><div class="tab-pane fade in active login-ipt-wrap" id="account">');
	loginHtml.push('<div class="logipt-group"><p class="help-block hide-v">请输入账号</p> <div class="logipt-line clearfix"> <label class="control-label">账号：</label>');
	loginHtml.push('<span><input type="text" id="name" placeholder="请输入账号"/></span></div> </div> <div class="logipt-group"><p class="help-block hide-v"  >请输入密码</p>');
	loginHtml.push('<div class="logipt-line clearfix"><label>密码：</label><span><input type="password" id="password" placeholder="请输入密码"/></span></div></div>');
	loginHtml.push('<div class="logipt-zx mt-30 clearfix"><div class="pull-left"><input id="remember" type="checkbox">记住我</div> <div class="pull-right"><a href="reg.html">免费注册</a><a href="find-pwd.html">找回密码</a></div></div>');
	loginHtml.push('<div class="logipt-btn btns clearfix"><button class="btn btn-org btn-lg btn-block"  type="button">登录</button> </div> </div><div class="tab-pane fade" id="weixin"><div class="wxlogin-wrap"><a href="https://qy.weixin.qq.com/cgi-bin/loginpage?corp_id=wx903c9c016c6d75bc&redirect_uri=http://www.vyiyun.com/wxAuth/wxloginAuth.action"><img src="images/weixin.png" /><b>点击登录</b></a><div class="wxlog-footer">微信登录微移云</div></div></div></div></div>');
	var lhtml = loginHtml.join("");
	$("body").append(lhtml);
	$(".header-btn .btn-org").bind("click",function(event){
		$(".mark").removeClass("hidden");
		var $wbox = $(".winbox-pos");
		$wbox.removeClass("hidden");
		var name = localStorage.name;			
		if(name!=undefined){	
			$wbox.find("#remember").prop("checked",true);
			$wbox.find("#name").val(name);
			$wbox.find("#password").val(localStorage.password);
		}
	});

	$(".winbox-tit").on("click",".close-box",function(event){
		$(".mark").addClass("hidden");
		$(".winbox-pos").addClass("hidden");
	});

	$(".winbox-pos").on("click",".btn",function(event){
		var $btn = $(this),
			$name = $(".winbox-pos #name"),
			_nameVal = $.trim($name.val());
		var $pwd = $(".winbox-pos #password"),
			_pwdVal = $.trim($pwd.val());
		var obj = new Array();
		var $nameTips = $name.parents(".logipt-group").find(".help-block");
		var $pwdTips = $pwd.parents(".logipt-group").find(".help-block");
		if(_nameVal==""){
			$nameTips.removeClass("hide-v").addClass("show-v");
			
		}else{
			$nameTips.removeClass("show-v").addClass("hide-v");
			Component.addParam(obj,"name",_nameVal);
		}
		if(_pwdVal==""){
			$pwdTips .removeClass("hide-v").addClass("show-v");
			
		}else{
			$pwdTips.removeClass("show-v").addClass("hide-v");
			Component.addParam(obj,"password",_pwdVal);
		}
		var isRemember = $(".winbox-pos #remember").prop("checked");
		if(_nameVal!=""&&_pwdVal!=""){
			
			Component.getDataFromServer(loginUrl,JSON.stringify({
				"userName":obj.name,
				"password":obj.password
			}),function(data){
				if(data.success){
					if(isRemember){
						localStorage.name = _nameVal;
						localStorage.password = _pwdVal;	
					}
					Component.showTips("success","ok","登录成功");
					//登录成功
					window.location.href="http://www.vyiyun.com/manage/indexView.action";
				}else{
					if(data.errorCode==231099){
						Component.showTips("error","error","账号或密码有误");
					}else{
						Component.showTips("error","error",data.msg);
					}
					
				}
				
			},{
				beforeSend:function(){
					$btn.text("正在登录，请稍后。").prop("disabled","disabled");
				},
				complete:function(){
					$btn.text("登录").removeProp("disabled");
				}
			});
		}
	});

	$(".winbox-pos").on("blur","input",function(event){
		var $that = $(this),_val = $.trim($that.val()),$errorTips = $that.parents(".logipt-group").find(".help-block");
		if(_val==""){
			$errorTips.removeClass("hide-v").addClass("show-v");
		}else{
			$errorTips.removeClass("show-v").addClass("hide-v");
		}
	});
	if(!$.support.leadingWhitespace){
		$(".idx-reg-bot .idx-reg").removeClass("col-lg-7 col-md-7 col-sm-10 col-xs-12");
		$(".idx-reg-bot .idx-reg").width("58.3333%");
		$(".idxreg-bg").removeClass("idxreg-bg");
		return;
	}else{

		$.getScript("js/wow.js", function(){
			new WOW().init();
		});
	}
});

//修改密码
function resetPwd(){
	var url = contextPath + "website/resetPwd.action";
	$(".reset-pwd input").bind("blur",function(event){
		var $that = $(this),_val = $.trim($that.val());
		var $errorTps = $that.next();
		if(_val==""){
			$errorTps.removeClass("hide-v").addClass("show-v");
		}else{
			$errorTps.removeClass("show-v").addClass("hide-v");
		}
	});

	$(".reset-pwd .btn").bind("click",function(event){
		var $pwd = $("#password"),_pwdVal = $.trim($pwd.val());
		var $spwd = $("#surepassword"),_spwdVal = $.trim($spwd.val());
		var obj = new Array();
		$pwdTips = $pwd.next();
		$spwdTips = $spwd.next();
		if(_pwdVal==""){
			$pwdTips.text("请输入密码").removeClass("hide-v").addClass("show-v");
		}else{
			if(_pwdVal.length<6||_pwdVal.length>16){
				$pwdTips.text("密码长度有误").removeClass("hide-v").addClass("show-v");
			}else{
				$pwdTips.removeClass("show-v").addClass("hide-v");
			}
			Component.addParam(obj,"password",_pwdVal);
		}
		if(_spwdVal==""){
			$spwdTips.text("请再次输入密码").removeClass("hide-v").addClass("show-v");
		}else{
			if(_spwdVal!=_pwdVal){
				$spwdTips.text("两次密码不同，请确认").removeClass("hide-v").addClass("show-v");
			}	
		}
		if(_pwdVal!=""&&_spwdVal==_pwdVal){	
			Component.getDataFromServer(url,JSON.stringify({
				"password":obj.password
			}),function(data){
				if(data.success){
					Component.showTips("success","ok","重置密码成功",function(){
						var protocol = window.location.protocol,
					 	hostname = window.location.hostname,
					 	port = window.location.port ? ":" + window.location.port : "",
						_url = protocol + "//" + hostname + port + "/index.html";
						window.location.href = _url;
						//window.location = "http://www.vyiyun.com/";
					});
				}else{
					//showTips:function(type,icon,msg){
					Component.showTips("error","error",data.msg);
				}
			});
		}
		
	});

}

function hideErrorLable(obj){
	var $that = obj,$parentDom = $that.parents(".form-group"),$nextDom = $that.next();
	var _type = obj[0].tagName;
	if(_type=="INPUT"){
		$parentDom.removeClass("has-feedback has-error").addClass("has-feedback has-success");
		$nextDom.show().removeClass("glyphicon-remove").addClass("glyphicon-ok");
	}else{
		$parentDom.removeClass("has-error").addClass("has-success");
	}
	$parentDom.find(".help-block").removeClass("show-v").addClass("hide-v");
}

var codeUrl = contextPath + "website/checkValidCode.action";
function validatorInput(obj){
	var $that = obj,_trimval = $.trim($that.val()),$parentDom = $that.parents(".form-group"),$nextDom = $that.next();
   var _iname = $that.prop("id");
	if(_trimval==""){
		showErrorLable($that);
	}else{
		hideErrorLable($that);
		switch (_iname){
			case "email":
				if(!Component.regEmail.test(_trimval)){
					showErrorLable($that,"您输入的邮箱格式有误如：vyiyun@hr.com");
				}
				break;
			case "password":
				if(_trimval.length<6||_trimval.length>16){
					showErrorLable($that,"密码长度为6~16位字符");

				}
				break;
			case "surepwd":
				var _pwd = $("#password").val();
				if(_trimval!=_pwd){
					showErrorLable($that,"两次密码不同，请确认");
				}
				break;
			case "phone":
				if(!Component.regTel.test(_trimval)){
					showErrorLable($that,"您输入的手机号码有误");
				}
				break;
			case "code":
				if(_trimval.length<4||_trimval.length>4){
					showErrorLable($that,"验证输入有误");
				}else{
					Component.getDataFromServer(codeUrl,JSON.stringify({
						validateCode:_trimval
					}),function(data){
						if(!data.success){
							showErrorLable($that,"验证输入有误");
						}
					},{
						async:false
					});
				}
				break;

		}

	}
}

function validatorForm(){
	var $email = $("#defaultForm #email"),_emailVal = $.trim($email.val());
	var $pwd = $("#defaultForm #password"),_pwdVal = $.trim($pwd.val());
	var $surePwd = $("#defaultForm #surepwd"),_surePwdVal = $.trim($surePwd.val());
	var $company = $("#defaultForm #companyName"),_comVal = $.trim($company.val());
	var $contacts = $("#defaultForm #contactsName"),_contVal = $.trim($contacts.val());
	var $pro = $("#defaultForm #province"),_prolVal = $.trim($pro.val());
	var $city = $("#defaultForm #city"),_cityVal = $.trim($city.val());
	var $area = $("#defaultForm #area"),_areaVal = $.trim($area.val());
	var $business = $("#defaultForm #industry"),_busVal = $.trim($business.val());
	var $phone = $("#defaultForm #phone"),_phoneVal = $.trim($phone.val());
	var $code = $("#defaultForm #code"),_codeVal = $.trim($code.val());
	var obj = new Array(),_bool = true;
	if(_emailVal==""){
		showErrorLable($email,"请输入邮箱");
		_bool = false;
	}else{
		if(!Component.regEmail.test(_emailVal)){
			showErrorLable($email,"您输入的邮箱格式有误如：vyiyun@hr.com");
			_bool = false;
		}
	}
	if(_pwdVal==""){
		showErrorLable($pwd,"请输入密码");
		_bool = false;
	}else{
		if(_pwdVal.length<6||_pwdVal.length>16){
			showErrorLable($pwd,"密码长度为6~16位字符");
			_bool = false;
		}
	}
	if(_surePwdVal==""){
		showErrorLable($surePwd,"两次密码不同，请确认");
		_bool = false;
	}
	if(_surePwdVal!=_pwdVal){
		showErrorLable($surePwd,"两次密码不同，请确认");
		_bool = false;
	}
	if(_comVal==""){
		showErrorLable($company,"请输入企业名称");
		_bool = false;
	}
	if(_contVal==""){
		showErrorLable($contacts,"请输入联系人");
		_bool = false;
	}
	if(_prolVal=="请选择"||_cityVal=="请选择"||_areaVal=="请选择"){
		showErrorLable($pro,"请选择企业所在地");
		_bool = false;
	}
	if(_busVal==""){
		showErrorLable($business,"请选择所属行业");
		_bool = false;
	}
	if(_phoneVal==""){
		showErrorLable($phone,"请输入手机号码");
		_bool = false;
	}else{
		if(!Component.regTel.test(_phoneVal)){
			showErrorLable($phone,"您输入的手机号码有误");
			_bool = false;
		}
	}
	if(_codeVal==""){
		showErrorLable($code,"请输入验证码");
		_bool = false;
	}
	Component.addParam(obj,"email",_emailVal);
	Component.addParam(obj,"password",_pwdVal);
	Component.addParam(obj,"company",_comVal);
	Component.addParam(obj,"contacts",_contVal);
	Component.addParam(obj,"province",_prolVal);
	Component.addParam(obj,"city",_cityVal);
	Component.addParam(obj,"area",_areaVal);
	Component.addParam(obj,"business",_busVal);
	Component.addParam(obj,"phone",_phoneVal);
	Component.addParam(obj,"code",_codeVal);
	Component.addParam(obj,"bool",_bool);
	return obj;
}

function showErrorLable(obj,str){	
	var $that = obj,$parentDom = $that.parents(".form-group"),$nextDom = $that.next();
	var _type = obj.get(0).tagName;
	if(_type=="INPUT"){
		$parentDom.removeClass("has-feedback has-error").addClass("has-feedback has-error");
		$nextDom.show().removeClass("glyphicon-ok").addClass("glyphicon-remove");
	}else{
		$parentDom.removeClass("has-error").addClass("has-error");
	}
	if(str){
		$parentDom.find(".help-block").text(str);
	}
	$parentDom.find(".help-block").removeClass("hide-v").addClass("show-v");
}

function validatorSelect(obj){
	var $that = obj,_trimval = $.trim($that.val()),$parentDom = $that.parents(".form-group");
	if(_trimval==""){
		showErrorLable($that);
	}else {
		hideErrorLable($that);
	}
}
