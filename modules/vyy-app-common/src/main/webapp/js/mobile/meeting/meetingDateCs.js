/**
 * Created by 大猫 on 2015/12/7.
 */
var meetingDateCs = (function($) {
	var currDT;
	var aryDay = new Array("日", "一", "二", "三", "四", "五", "六");// 显示星期
	var lastDay;// 页面显示的最后一天
	var firstDay;// 页面显示的第一天
	var module = {};
	var options = {};
	var isScroll = false, isSending = false;
	var $thisDataUrl = "mobile/meeting/getMeetingRoomsOfTheDay.action";// 点击日期切换界面数据接口
	$.extend(module, {
		init : function(opt) {
			var that = this;
			$.extend(options, opt);
			initDate();
			return that;
		},
		bindEvent : function() {
			var that = this;
			$("#previousweek").bind("click", function() {
				previousWeek();
			});
			$("#nextweek").bind("click", function() {
				nextWeek();
			});
			$("#mytable td").on("click", function() {
				$(this).css({
					"color" : "#2aaf2a",
					"font-weight" : "bold"
				}).siblings().css({
					"color" : "#565656",
					"font-weight" : "normal"
				});
				// 默认显示全部会议室
				var $filter = $(".filter-m li").eq(0);
				$filter.find("i").attr("class", "fa fa-meh-o");
				$filter.find("div").text("显示全部");
				// 判断月份节点日期
				_thisY = $(this).attr("id");
				console.log(_thisY);
				$("#save_Date").val(_thisY);
				$("#JmeetRoomConPage").val("1");
				var pageNow = parseInt($("#JmeetRoomConPage").val());
				var postData = {
					"pageIndex" : pageNow,
					"date" : _thisY
				};
				// 进入页面绘制第一页数据
				$(".meetRoomCon").empty();
				ajaxServer($thisDataUrl, postData, viewingRoomList);
			});
			return that;
		}
	});
	// 请求数据
	function ajaxServer(url, data, callback, option) {
		var defaultOption = {
			type : 'post',
			url : url,
			data : data || {},
			dataType : "json",
			beforeSend : function() {
				isSending = true;
				$("body").loading({
					move : "show"
				});
			},
			complete : function() {
				isSending = false;
				$("body").loading({
					move : "hide"
				});
			},
			error : function(err) {
				isSending = false;
				$("body").loading({
					move : "hide"
				});
				// console.debug(err);
				$("body").toast({
					msg : "操作失败",
					type : 2,
					callMode : "func"
				});
			},
			success : function(data) {
				isSending = false;
				if (data.length) {
					if (callback) {
						callback(data);
					}
				} else {
					if (isScroll) {
						$("body").toast({
							msg : "没有更多记录",
							type : 1,
							callMode : "func"
						});
					} else {
						$("#noRecord").css("display", "block");
					}
				}
			}
		};
		if (option) {
			$.extend(defaultOption, option);
		}
		$.ajax(defaultOption);

	}

	// 初始化日期加载
	function initDate() {
		currDT = new Date();
		var _this = (currDT.getMonth() + 1);
		var a = _this < 10 ? "0" + _this : _this;
		showdate.innerHTML = currDT.getFullYear() + "年" + a + "月"; // 显示日期/*currDT.toLocaleDateString()*/
		var dw = currDT.getDay();// 从Date对象返回一周中的某一天(0~6)
		var tdDT;// 日期

		// 在表格中显示一周的日期
		var objTB = document.getElementById("mytable");// 取得表格对象
		for (var i = 0; i < 7; i++) {
			tdDT = getDays()[i];
			if (tdDT.toLocaleDateString() == currDT.toLocaleDateString()) {
				objTB.rows[0].cells[i].style.color = "#2aaf2a";// currDT突出显示
				objTB.rows[0].cells[i].style.fontWeight = "bold";
			}
			dw = tdDT.getDay();// 星期几
			var _thisDate = tdDT.getDate();
			mons = tdDT.getMonth() + 1;
			mons = mons < 10 ? "0" + mons : mons;
			_thisDate = _thisDate < 10 ? "0" + _thisDate : _thisDate;
			objTB.rows[0].cells[i].innerHTML = "周" + aryDay[dw] + "<br />"
					+ _thisDate;// 显示
			objTB.rows[0].cells[i].id = tdDT.getFullYear() + "-" + mons + "-"
					+ _thisDate;
		}
		// 重新赋值
		lastDay = getDays()[6];// 本周的最后一天
		firstDay = getDays()[0];// 本周的第一天
	}
	// 取得当前日期一周内的某一天
	function getWeek(i) {
		var now = new Date();
		var n = now.getDay();
		var start = new Date();
		start.setDate(now.getDate() - n + i);// 取得一周内的第一天、第二天、第三天...
		return start;
	}

	// 取得当前日期一周内的七天
	function getDays() {
		var days = new Array();
		for (var i = 1; i <= 7; i++) {
			days[i - 1] = getWeek(i);
		}
		return days;
	}

	// 取得下一周的日期数(共七天)
	function getNextWeekDatas(ndt) {
		var days = new Array();
		for (var i = 1; i <= 7; i++) {
			var dt = new Date(ndt);
			days[i - 1] = getNextWeek(dt, i);
		}
		return days;
	}

	// 指定日期的下一周(后七天)
	function getNextWeek(dt, i) {
		var today = dt;
		today.setDate(today.getDate() + i);
		return today;
	}

	// 取得上一周的日期数(共七天)
	function getPreviousWeekDatas(ndt) {
		var days = new Array();
		for (var i = -7; i <= -1; i++) {
			var dt = new Date(ndt);
			days[7 + i] = getPreviousWeek(dt, i);
		}
		return days;
	}

	// 指定日期的上一周(前七天)
	function getPreviousWeek(dt, i) {
		var today = dt;
		today.setDate(today.getDate() + i);
		return today;
	}

	// 下一周
	function nextWeek() {
		setCurrDTAfter();// 重设时间
		var _this = (currDT.getMonth() + 1);
		_this = _this < 10 ? "0" + _this : _this;
		showdate.innerHTML = currDT.getFullYear() + "年" + _this + "月";
		// 在表格中显示一周的日期
		var objTB = document.getElementById("mytable");// 取得表格对象
		var dw = currDT.getDay();// 从Date对象返回一周中的某一天(0~6)
		var tdDT;// 日期
		$(objTB).find("td").css({
			"color" : "",
			"font-weight" : "normal"
		});
		for (var i = 0; i < 7; i++) {
			tdDT = getNextWeekDatas(lastDay)[i];
			dw = tdDT.getDay();// 星期几
			var _thisDate = tdDT.getDate();
			mons = tdDT.getMonth() + 1;
			mons = mons < 10 ? "0" + mons : mons;
			_thisDate = _thisDate < 10 ? "0" + _thisDate : _thisDate;
			objTB.rows[0].cells[i].innerHTML = "周" + aryDay[dw] + "<br />"
					+ _thisDate;// 显示
			objTB.rows[0].cells[i].id = tdDT.getFullYear() + "-" + mons + "-"
					+ _thisDate;
		}
		// 重新赋值
		firstDay = getNextWeekDatas(lastDay)[0];// 注意赋值顺序1
		lastDay = getNextWeekDatas(lastDay)[6];// 注意赋值顺序2
	}

	// 上一周
	function previousWeek() {
		settCurrDTBefore();
		var _this = (currDT.getMonth() + 1);
		_this = _this < 10 ? "0" + _this : _this;
		showdate.innerHTML = currDT.getFullYear() + "年" + _this + "月";
		// 在表格中显示一周的日期
		var objTB = document.getElementById("mytable");// 取得表格对象
		var dw = currDT.getDay();// 从Date对象返回一周中的某一天(0~6)
		var tdDT;// 日期
		$(objTB).find("td").css({
			"color" : "",
			"font-weight" : "normal"
		});
		for (var i = 0; i < 7; i++) {
			tdDT = getPreviousWeekDatas(firstDay)[i];
			dw = tdDT.getDay();// 星期几
			var _thisDate = tdDT.getDate();
			mons = tdDT.getMonth() + 1;
			mons = mons < 10 ? "0" + mons : mons;
			_thisDate = _thisDate < 10 ? "0" + _thisDate : _thisDate;
			objTB.rows[0].cells[i].innerHTML = "周" + aryDay[dw] + "<br />"
					+ _thisDate;// 显示
			objTB.rows[0].cells[i].id = tdDT.getFullYear() + "-" + mons + "-"
					+ _thisDate;
		}
		// 重新赋值
		lastDay = getPreviousWeekDatas(firstDay)[6];// 注意赋值顺序1
		firstDay = getPreviousWeekDatas(firstDay)[0];// 注意赋值顺序2
	}

	// 当前日期后第七天
	function setCurrDTAfter() {
		currDT.setDate(currDT.getDate() + 7);
	}

	// 当前日期前第七天
	function settCurrDTBefore() {
		currDT.setDate(currDT.getDate() - 7);
	}

	return module;
})(window.jQuery);
