/**
 * Created by liyuxia on 2015/8/7.
 */
$(function() {
	var currYear = (new Date()).getFullYear();
    var opt={};
    opt.date = {preset : 'date'};
    opt.datetime = {preset : 'datetime'};
    opt.time = {preset : 'time'};
    opt.default = {
        theme: 'android-ics light', //皮肤样式
        display: 'modal', //显示方式
        mode: 'scroller', //日期选择模式
        dateFormat: 'yyyy-mm-dd ',
        lang: 'zh',
        showNow: false,
        nowText: "今天",
        startYear: currYear - 10, //开始年份
        endYear: currYear + 10 //结束年份
    };
    $("#signStartTime,#signEndTime").mobiscroll($.extend(opt['datetime'], opt['default']));
	var modalSignLaunched = $(".modalSignLaunched"), //发起签到模态框
	signLaunchedWarp = $(".signLaunchedWarp"), //发起签到模块
	membersListWarp = $(".membersListWarp").eSelectPeople({
		btClose : function(_self) {
			$(".signLaunchedWarp").show();
            $("body").css("overflow-y","auto");
            _self.hide();
        },
        btCance : function(_self) {
        	$(".signLaunchedWarp").show();
            $("body").css("overflow-y","auto");
            _self.hide();
        },
		btOk : function(_self,data) {
			$("body").css("overflow-y","auto");
			if(!!data && data.length > 0 ){
				var div = null;
				var img = null;
				var span = null;
				var elemI = null;
				var uId = null;
				//判断是否已存在选择的用户 这里需要去重
				var flag = false;
				for(var i = 0,size = data.length;i<size;i++){
					uId = data[i].uId;
					$(".addedPeopleList > div[uId]").each(function(){
						if($(this).attr("uId") == uId){
							flag = true;
						}
					});
					if(flag){
						flag = false;
						continue;
					}
					
					div = $("<div>");
					div.attr("uId",uId);
					div.attr("uAvatar",data[i].uAvatar);
					div.attr("uName",data[i].uName);
					img = $("<img>");
					img.attr("src",data[i].uAvatar);
					img.on("click",function(){
						$(this).parent().remove();
					});
					span = $("<span>");
					span.append(data[i].uName);
					
					elemI = $("<i>");
					elemI.css("cursor","pointer");
					
					elemI.on("click",function(){
						$(this).parent().remove();
					});
					
					div.append(img);
					div.append(span);
					div.append(elemI);
					 $(".addedPeopleList").find(".addPeople").before(div);
				}
			}
			
			$(".signLaunchedWarp").show();
			_self.hide();
		}
	}), //人员列表模块
	searchInput = $("#searchMembers"), //搜索input
	circleInput = $(".searchBox .fa-times-circle"), //清空搜索input
	searchButton = $(".searchBox button");//搜索按钮
	/*        $('.form_datetime').datetimepicker({
	 language:  'zh-CN',
	 weekStart: 1,
	 todayBtn:  1,
	 autoclose: 1,
	 todayHighlight: 1,
	 startView: 2,
	 forceParse: 0,
	 showMeridian: 1
	 });*/

	//获得浏览器的高度
	var windowH = window.innerHeight;
	var windowW = window.innerWidth;
	//设置发起签到模态框的top、left
	modalSignLaunched.css({
		"top" : (windowH / 2 - 50),
		"left" : (windowW / 2 - 67)
	});

	//单选或多选
	peopleInput($(".peopleList>input"));

	/*	$(document).on("click", ".firstTit>input,.secondTit>input,.thirdTit>input",
			function() {
				if ($(this).attr("checked")) {
					$(this).parent().next().stop().animate({
						height : 'show'
					}, 500);
				} else {
					$(this).parent().next().stop().animate({
						height : 'hide'
					}, 500);
				}
			}
	);*/
	//添加签到人员
	$(".addPeople").click(function() {
		signLaunchedWarp.hide();
		$("body").css("overflow","hidden");
		membersListWarp.eshow({_isMultipleChoice:true,_isShowTag:true});
	});
	/* 搜索Start */
	$(".searchPeople").click(function() {
		$(this).addClass("myHide");
		$(".searchBox").removeClass("myHide");
	});
	$(document).on("input", "#searchMembers", function() {
		var $this = $(this);
		if ($this.val() != "") {
			$this.next().removeClass("myHide");
			searchButton.text("确认");
		} else {
			$this.next().addClass("myHide");
			searchButton.text("取消");
		}
	});
	circleInput.click(function() {
		$(this).addClass("myHide");
		searchButton.text("取消");
		searchInput.val("");
	});
	searchButton.click(function() {
		$text = $(this).text();
		if ($text == "取消") {
			$(".searchBox").addClass("myHide");
			$(".searchPeople").removeClass("myHide");
		} else {
			//进行搜索操作
		}
	});
	/* 搜索End */

	//取消签到人员
	$(".membersListBtn button:eq(0)").click(function() {
		// signLaunchedWarp.removeClass("myHide");
		// membersListWarp.addClass("myHide");
	});
	//确认签到人员
	$(".membersListBtn button:eq(1)").click(function() {
		//把人员回填到表单中
		// signLaunchedWarp.removeClass("myHide");
		// membersListWarp.addClass("myHide");
	});
});
//单选或多选
function peopleInput(selectInput) {
	selectInput
			.each(function() {
				if ($(this).is('[type=checkbox],[type=radio]')) {
					var input = $(this);

					// 使用输入的ID得到相关的标签
					var label = $('label[for=' + input.attr('id') + ']');

					//绑定自定义事件，触发它，绑定点击，焦点，模糊事件
					input.bind('updateState',function() {input.is(':checked') ? label.addClass('checked'): label.removeClass('checked checkedHover checkedFocus');
									}).trigger('updateState').click(
									function() {
										$('input[name='+ $(this).attr('name')+ ']').trigger('updateState');
									}).focus(function() {
								label.addClass('focus');
								if (input.is(':checked')) {
									$(this).addClass('checkedFocus');
								}
							}).blur(function() {
								label.removeClass('focus checkedFocus');
							});
				}
			});
}