/**
 * Created by liyuxia on 2015/9/6.
 */
var isSelectTime,isRightTime;
var aryDay = new Array("日", "一", "二", "三", "四", "五", "六");// 显示星期
var d_l = aryDay.length;
window.Meeting_Info = {
	// 会议相关访问地址mobile/meetingBosum/ensureAttendMeeting.action
	"Meeting_Add" : "mobile/meeting/meeting_add_updateDate.action",
	"Meeting_AddThis" : "mobile/meeting/meetingBookSuccess_insert.action",
	"Meeting_Detail_View" : "mobile/meeting/meetingBookSuccess_detail.action",
	"Meeting_Init_View" : "meeting/meetingInitView.action",
};
$(function() {
	// 选择时间
	ystDateAndtorDate();
	// 会议时间段选择
	if (!chooseMeeTime()) {
		return;
	}
	// 是否通知参会人员
	// isInform();
	// 是否重复预定
	repeatReservation();
	// 是否通知参会人员
	sendRepeat();
	// 发起会议
	$("#passBtn")
			.click(
					function() {
						if (validation()) {
							var date = $("#mt-date").text().substr(0, 10);
							var data = {
								"roomId" : $meetingRoomId,
								"roomName" : $("#theme").val(),
								"date" : date,
								"startAndEndTimeJsonObj" : JSON
										.stringify(getMeetingTimeList()),
								"theme" : $("#theme").val(),
								"repeat" : $("#reservation").val(),
								"status" : $("#sendtopp").val(),
								"attendMeetingUsersJsonArray" : JSON
										.stringify(passGetData())
							};
							console.log(data);
							$
									.ajax({
										url : window.Meeting_Info.Meeting_AddThis,
										data : data,// 要发送的数据
										type : "post",
										dataType : "json",
										beforeSend : function() {
											$("body").loading({
												move : "show"
											});
										},
										complete : function() {
											$("body").loading({
												move : "hide"
											});
										},
										success : function(resData) {
											$("body").loading({
												move : "hide"
											});
											console.log(resData);
											if(resData.status == 0){
												setTimeout(function(){
													window.location.replace ( window.Meeting_Info.Meeting_Detail_View
															+ "?meetingId=" + resData.value);
											},300);
											}else {
												$("body").toast({
													msg : "发起会议出错，请稍后再试",
													type : 1,
													callMode : "func"
												});
											}
										},
										error : function() {
											$("body").toast({
												msg : "发起会议出错，请稍后再试",
												type : 1,
												callMode : "func"
											});
										}
									});
						}
					});

	// 取消
	$("#cancelBtn").click(
			function() {
				window.location.href = window.Meeting_Info.Meeting_Init_View
						+ "?flag=1";
			});

	// 选择人员
	var membersListWarp = $(".membersListWarp").eSelectPeople({
		btClose : function(_self) {
			$(".warp").show();
			_self.hide();
		},
		btCance : function(_self) {
			$(".warp").show();
			_self.hide();
		},
		btOk : function(_self, data) {
			if (!!data && data.length > 0) {
				var div = null;
				var img = null;
				var span = null;
				var elemI = null;
				var uId = null;
				// 判断是否已存在选择的用户 这里需要去重
				var flag = false;
				for (var i = 0, size = data.length; i < size; i++) {
					uId = data[i].uId;
					$(".addedPeopleList > div").each(function() {
						if ($(this).attr("uId") == uId) {
							flag = true;
						}
					});
					if (flag) {
						flag = false;
						continue;
					}
					div = $("<div>");
					div.attr("uId", uId);
					div.attr("uAvatar", data[i].uAvatar);
					div.attr("uName", data[i].uName);
					img = $("<img>");
					img.attr("src", data[i].uAvatar);
					img.on("click", function() {
						$(this).parent().remove();
					});
					span = $("<span>");
					span.append(data[i].uName);
					elemI = $("<i>");
					elemI.css("cursor", "pointer");
					elemI.on("click", function() {
						$(this).parent().remove();
					});
					div.append(img);
					div.append(span);
					div.append(elemI);
					$(".addedPeopleList").find(".addPeople").before(div);
				}
				;
			}
			$(".warp").show();
			_self.hide();
		}
	});
	// 选择人员
	$("#addPeople").on("click", function() {
		$(".warp").hide();
		membersListWarp.eshow({
			flag : "0",
			_isMultipleChoice : true,
			_isShowTag : true
		});
	});
});
// 关闭选择联系人界面
// 校验表单
function validation() {
	timeWrite();
	if (isSelectTime == false) {
		$("body").toast({
			msg : "请选择会议时间",
			type : 2,
			callMode : "func"
		});
		return false;
	}if (isRightTime == false) {
		$("body").toast({
			msg : "会议时间必须大于当前时间",
			type : 2,
			callMode : "func"
		});
		return false;
	} else if ($("#theme").val() === '') {
		$("body").toast({
			msg : "请填写会议主题",
			type : 2,
			callMode : "func"
		});
		return false;
	} else if ($(".addedPeopleList>div>i").size() == 0) {
		$("body").toast({
			msg : "请选择参会人员",
			type : 2,
			callMode : "func"
		});
		return false;
	}
	return true;
}
// 是否填写会议时间
function timeWrite() {
	var $_li = $(".choose-time li");
	$_li.each(function() {
		if ($(this).hasClass("toCheck")) {
			if(isThenthisTime()){
				isRightTime = true;
			}else{
				isRightTime = false;
			}
			isSelectTime = true;
			return false;
		} else {
			isSelectTime = false;
		}
	});
}
//判断选择时间是否大于当前时间
function isThenthisTime(){
	//选择时间段的最小时间
	var date = $("#mt-date").text().substr(0, 10);
	var StartTime = getMeetingTimeList().start;
	_thisTime = StartTime.split(":").join('');
	_thisDate = date.split("-").join('');
	var myChooseTime = parseInt(_thisDate+_thisTime);
	//当天日期时间
	var now = new Date();
	var y = now.getFullYear(),
	    m = now.getMonth() + 1,
	    n = now.getDate(),
	    hour = now.getHours(),
	    min = now.getMinutes(),
	    sec = now.getSeconds();
	    m = m < 10 ? "0" + m : m;
	    n = n < 10 ? "0" + n : n;
	    hour = hour < 10 ? "0" + hour : hour;
	    min = min < 10 ? "0" + min : min;
	    sec = sec < 10 ? "0" + sec : sec;
	var _thisDay = y+m+n+hour+min+sec;
	var thisDateTime = parseInt(_thisDay);
	if(myChooseTime - thisDateTime >= 0){
		return true;
	}else{
		return false;
	}
}
// 是否通知参会人员
function isInform() {
	$(".inform i,.inform label").click(
			function() {
				if ("1" == $("#informRadio").val()) {
					$(".inform i").removeClass("fa-check-circle-o").addClass(
							"fa-circle-o");
					$("#informRadio").val("0");
				} else {
					$(".inform i").removeClass("fa-circle-o").addClass(
							"fa-check-circle-o");
					$("#informRadio").val("1");
				}
			});
}
// 组装参会人员id数据
function passGetData() {
	var entityUserIdInfo, entityUserIdList = [];
	$(".addedPeopleList> div[uId]").each(function() {
		entityUserIdList.push({
			"userId" : $(this).attr("uId"),
			"avatar" : $(this).attr("uAvatar"),
			"userName" : $(this).attr("uName")
		});
	});
	entityUserIdInfo = {
		"users" : entityUserIdList
	};
	return entityUserIdInfo;
}
// 选择会议时间段数据
function getMeetingTimeList() {
	var $_li = ".choose-time li";
	var timeList = [], result = [], timeAllList = [], lastDataList = [], tmp, liLength = $($_li).length;
	$($_li).each(function() {
		if ($(this).attr("class").indexOf("toCheck") > 0) {
			timeList.push($(this).index() + 1);
		}
	});
	result = getChoosedArr(timeList);
	for (var i = 0; i < result.length; i++) {
		var maxIndex = Math.max.apply(Math, result[i]);
		var minIndex = Math.min.apply(Math, result[i]);
		if (maxIndex == liLength) {
			timeAllList = {
				"start" : $($_li).eq(minIndex - 1).find("span").text() + ":00",
				"end" : $("#thisEndTime").val() + ":00"
			};
		} else {
			timeAllList = {
					"start" : $($_li).eq(minIndex - 1).find("span").text() + ":00",
					"end" : $($_li).eq(maxIndex).find("span").text() + ":00"
			};
		}
	}
	/*lastDataList = {
		"startAndEnd" : timeAllList
	};*/
	return timeAllList;
}
// 把连续的字段提取出来
function getChoosedArr(timeList) {
	var result = [], tmp;
	while (tmp = timeList.shift()) {
		if (result.length == 0) {
			result.push([ tmp ]);
			continue;
		}
		var e = result[result.length - 1];
		if (tmp == e[e.length - 1] + 1) {
			e.push(tmp);
		} else {
			result.push([ tmp ]);
		}
	}
	return result;
}
// 选择会议时间段
function chooseMeeTime() {
	var $_li = ".choose-time li";
	$(document).on("click", $_li, function() {
		var _this = $(this);
		if (!_this.hasClass("Checked")) {
			_this.toggleClass("toCheck");
		}
		var _List = getChoosedArr(isContinuou());
		console.log(_List+"----"+_List.length);
		if(_List.length > 1){
			if (!_this.hasClass("Checked")) {
				_this.addClass("toCheck");
				_this.siblings().removeClass("toCheck");
			}
		}
	});
	return true;
}
// 判断选择的时间段是否连续
function isContinuou() {
	var $_li = ".choose-time li";
	var checkArr = [];
	$($_li).each(function() {
		if ($(this).hasClass("toCheck")) {
			checkArr.push($(this).index()+1);
		}
	});
	return checkArr;
}
// 是否重复预定
function repeatReservation() {
	$('#switchOne').bind("click", function() {
		var bt = $(this).find("span");
		if (bt.attr("class") == 'off') {
			bt.animate({
				left : "25px"
			});
			bt.removeClass("off").parent().addClass("on");
			$("#reservation").attr("value", "1");
		} else {
			bt.animate({
				left : "0"
			});
			bt.addClass("off").parent().removeClass("on");
			$("#reservation").attr("value", "0");
		}
		return false;
	});
}
// 是否通知参会人员
function sendRepeat() {
	$('#switchTow').bind("click", function() {
		var sr = $(this).find("span");
		if (sr.attr("class") == 'off') {
			sr.animate({
				left : "25px"
			});
			sr.removeClass("off").parent().addClass("on");
			$("#sendtopp").attr("value", "1");
		} else {
			sr.animate({
				left : "0"
			});
			sr.addClass("off").parent().removeClass("on");
			$("#sendtopp").attr("value", "0");
		}
		return false;
	});
}
function ystDateAndtorDate() {
	var choose_list = $(".choose-time ul");
	$("#yesterdayDt").on(
			"click",
			function() {
				var _thisDate = getBeforeDate($("#mt-date").text())[1];
				choose_list.empty();
				// 生成时间段表格初始化
				console.log(_thisDate);
				initIndex(window.Meeting_Info.Meeting_Add, _thisDate,
						$meetingRoomId);
				$("#mt-date").text(getBeforeDate($("#mt-date").text())[0]);// 前一天的日期
			});
	$("#tomorowDt").on(
			"click",
			function() {
				var _thisDate = getAfterDate($("#mt-date").text())[1];
				$("#mt-date").text(getAfterDate($("#mt-date").text())[0]);// 后一天的日期
				choose_list.empty();
				// 生成时间段表格初始化
				console.log(_thisDate);
				initIndex(window.Meeting_Info.Meeting_Add, _thisDate,
						$meetingRoomId);
			});
}
// 获取前一天日期；
function getBeforeDate($dt) {
	var year = $dt.substring(0, 4);
	var mon = $dt.substr(5, 2) - 1;
	var day = $dt.substr(8, 2);
	var week = $dt.substr(13, 1);
	var s, sOther, sArray = [];
	for (var i = 0; i <= 6; i++) {
		if (week == aryDay[i]) {
			if (i - 1 < 0) {
				i = aryDay.length - 1;
				week = aryDay[i % 7];
			} else {
				week = aryDay[(i - 1) % 7];
			}
		}
	}
	var d = new Date(year, mon, day - 1);
	year = d.getFullYear();
	mon = d.getMonth() + 1;
	day = d.getDate();
	s = year + "-" + (mon < 10 ? ('0' + mon) : mon) + "-"
			+ (day < 10 ? ('0' + day) : day) + " 星期" + week;
	sOther = year + "-" + (mon < 10 ? ('0' + mon) : mon) + "-"
			+ (day < 10 ? ('0' + day) : day);
	sArray = [ s, sOther ];
	return sArray;
}
// 获取后一天日期；
function getAfterDate($dt) {
	var year = $dt.substring(0, 4);
	var mon = $dt.substr(5, 2);
	var day = $dt.substr(8, 2);
	var week = $dt.substr(13, 1);
	var s, sOther, sArray = [];
	for (var i = 0; i <= 6; i++) {
		if (week == aryDay[i]) {
			if (i + 1 > 6) {
				i = d_l - i - 1;
				week = aryDay[i % 7];
			} else {
				week = aryDay[(i + 1) % 7];
			}
			break;
		}
	}
	var d = new Date(year, Number(mon) - 1, Number(day) + 1);
	var newyear = d.getFullYear();
	var newmon = d.getMonth() + 1;
	var newday = d.getDate();
	s = newyear + "-" + (newmon < 10 ? ('0' + newmon) : newmon) + "-"
			+ (newday < 10 ? ('0' + newday) : newday) + " 星期" + week;
	sOther = newyear + "-" + (newmon < 10 ? ('0' + newmon) : newmon) + "-"
			+ (newday < 10 ? ('0' + newday) : newday);
	sArray = [ s, sOther ];
	return sArray;
}
