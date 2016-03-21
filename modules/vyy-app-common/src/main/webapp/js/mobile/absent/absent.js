/**
 * Created by fangtao on 2015/8/18.
 */
$(function() {
	var leaveWarp = $(".leaveWarp"); // 请假页面
	//promptModal = $(".promptModal");// 申请成功模态框
	/* 选择时间Start */
	var currYear = (new Date()).getFullYear();
	var opt={};
	opt.date = {preset : 'date'};
	opt.datetime = {preset : 'datetime'};
	opt.time = {preset : 'time'};
	opt.default = {
		theme: 'android-ics light', //皮肤样式
		display: 'modal', //显示方式
		mode: 'scroller', //日期选择模式
		dateFormat: 'yyyy-mm-dd',
		lang: 'zh',
		showNow: false,
		nowText: "今天",
		startYear: currYear - 10, //开始年份
		endYear: currYear + 10 //结束年份
	};
	var optDateTime = $.extend(opt['datetime'], opt['default']);
	$("#leaveStartTime,#leaveStartEnd").mobiscroll(optDateTime).datetime(optDateTime);
	/* 选择时间End */

	// 判断请假事由是否为“其他”
	$("select").change(function() {

	});
	//删除审核人
	delReviewer();

	/* 表单验证Start */
	$("#applyFor").click(function() {
		// 给审核人和抄送人的个数赋值对应的input

		if(!checkEndTime()){
			$("body").toast({
				msg:"开始时间不能晚于结束时间",
				type:2,
				callMode:"func"
			})
		}else{
			assignment();
			$("#leaveForm").verification({
				callback:function(){
					var entityUserInfo = [];
					/*$(".addedStaffList[type='0'] .staffList").each(function() {
						entityUserInfo.push({
							"uId" : $(this).attr("uId"),
							"uAvatar" : $(this).attr("uAvatar"),
							"uType" : 2,
							"personType" : '0',
							"uName" : $(this).attr("uName"),
							"entityType" : "1"
						});
					});*/
					$(".addedStaffList[type='0'] .staffList").each(function() {
				        var _div=$(this).children("div");
				        entityUserInfo.push({
				            "uId" : _div.attr("uId"),
				            "uAvatar" : _div.attr("uAvatar"),
				            "uType" : 2,
				            "personType" : '0',
				            "uName" : _div.attr("uName"),
				            "entityType" : "1"
				        });
				    });

					$(".addedPeopleList[type='1'] > div[uId]").each(function() {
						entityUserInfo.push({
							"uId" : $(this).attr("uId"),
							"uAvatar" : $(this).attr("uAvatar"),
							"uType" : 2,
							"personType" : '1',
							"uName" : $(this).attr("uName"),
							"entityType" : "1"
						});
					});
					

					var data = {
						"absentType" : $('#leaveReason option:selected').val(),
						"reason" : $('#leaveExplain').val(),
						"position" : $("#leaveJob").val(),
						"department" : $("#leaveDepartment").val(),
						"beginTime" : $("#leaveStartTime").val(),
						"endTime" : $("#leaveStartEnd").val(),
						"entityUserInfo" : entityUserInfo
					};
					$.ajax({
						url : 'mobile/absent/absentApply.action',
						type : 'post',
						data : {
							absentApplyInfo : JSON.stringify(data)
						},
						beforeSend : function() {
							//$("#onLoading").show();
							$("body").loading({
								move:"show"
							});
						},
						complete : function() {
							//$("#onLoading").hide();
							$("body").loading({
								move:"hide"
							});
						},
						success : function(resData, textStatus) {
							//$("#onLoading").hide();
							$("body").loading({
								move:"hide"
							});
							if (!!resData && "0" == resData.status) {
								$("body").toast({
									msg:"申请成功",
									type:1,
									callMode:"func",
									end : function(){
										//window.location.href="absent/absentDetailView.action?id="+resData.value;
										window.location.replace("mobile/absent/absentDetailView.action?id="+resData.value);
									}
								});
								//successPrompt("申请成功");

							} else {
								$("body").toast({
									msg:resData.errorMsg,
									type:2,
									callMode:"func"
								});
								/*successPrompt("申请失败!"+resData.errorMsg,function(){

								 });*/
							}
						},
						error : function(XMLHttpRequest, textStatus, errorThrown) {
							/*successPrompt("申请失败!"+resData.errorMsg,function(){
							 });*/
							$("body").toast({
								msg:"系统异常，请稍后再试",
								type:2,
								callMode:"func"
							});
						}
					});
				}
			});
		}


	});
	/* 表单验证End */
});
// 单选或多选
function peopleInput(selectInput) {
	selectInput
		.each(function() {
			if ($(this).is('[type=checkbox],[type=radio]')) {
				var input = $(this);

				// 使用输入的ID得到相关的标签
				var label = $('label[for=' + input.attr('id') + ']');

				// 绑定自定义事件，触发它，绑定点击，焦点，模糊事件
				input
					.bind(
					'updateState',
					function() {
						input.is(':checked') ? label
							.addClass('checked')
							: label
							.removeClass('checked checkedHover checkedFocus');
					}).trigger('updateState').click(
					function() {
						$(
							'input[name='
							+ $(this).attr('name')
							+ ']').trigger(
							'updateState');
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

// 表单验证提示
function promptSubmit(str,callback) {
	$(".onePrompt").children("span").text(str);
	$(".onePrompt").stop().animate({
		top : "0px"
	}, 500, function() {
		setTimeout(function() {
			$(".onePrompt").animate({
				top : "-40px"
			});
		}, 500,function(){
			if (typeof (callback) == "function") {
				callback(data);
			}
		});
	});
}
function checkEndTime(){
	var startTime=$("#leaveStartTime").val();
	var start=new Date(startTime.replace("-", "/").replace(" ", "/"));
	var endTime=$("#leaveStartEnd").val();
	var end=new Date(endTime.replace("-", "/").replace(" ", "/"));
	if(end<=start){
		return false;
	}
	return true;
}
function assignment(){
	var $AuditorListL=$("#selectAuditorList .staffList>div[uId]").length;
	if($AuditorListL!=0){
		$("#myAuditor").val($AuditorListL);
	}else{
		$("#myAuditor").val("");
	}
}
//删除审核人
function delReviewer(){
	$(document).on(touchEv,".staffList>b",function(){
		var $this=$(this);
		$this.parent().remove();
		$("#selectAuditorList .staffList").each(function(index){
			var $this=$(this);
			$this.find("em").text(digitalSwitchChart((index+1).toString()));
		});
	});
}



