window.Meeting_Info = {
	"Meeting_add_View" : "mobile/meeting/meeting_add.action",// 预定会议室地址
	"FreeMeetingRoom" : "mobile/meeting/getAvailableMeetingRoomsOfTheDay.action",// 显示空闲的会议室
	"AllMeetingRoom" : "mobile/meeting/getMeetingRoomsOfTheDay.action",// 显示全部的会议室，确认筛选会议室
	"FilterMeetingRoom" : "mobile/meeting/filterRoomNameAndCapacity.action",// 筛选会议室
};

$(function() {// 按条件筛选会议室
	var $filter = $(".filter-m li").eq(0);// 选择空闲或者全部
	var $filter01 = $(".filter-m li").eq(1);// 容纳人数筛选
	var $filter02 = $(".filter-m li").eq(2);// 筛选条件
	$filter.find("i").attr("class", "fa fa-meh-o");
	$filter.find("div").text("显示全部");
	// 只选空闲或者显示全部筛选
	$filter.on("click", function() {
		var _thisI = $(this).find("i");
		var _thisNoSign = $(".meetRoomCon").find("article");
		$("#JmeetRoomConPage").val("1");
		var pageNow = parseInt($("#JmeetRoomConPage").val());
		var postData = {
			"pageIndex" : pageNow,
			"date" : $("#save_Date").val()
		};
		if (_thisI.attr("class") == "fa fa-meh-o") {
			_thisI.attr("class", "fa fa-smile-o");
			$(this).find("div").text("显示空闲");
			/*$(".meetRoomCon").empty();
			// 进入页面绘制第一页数据
			ajaxServer(window.Meeting_Info.FreeMeetingRoom, postData,
					viewingRoomList);*/
			_thisNoSign.each(function(){
				var _that = $(this).find("span.timeCounts");
				if (_that.text().indexOf("满") > 0) {
					$(this).hide(300);
				}
			});
		} else {
			_thisI.attr("class", "fa fa-meh-o");
			$(this).find("div").text("显示全部");
			$(".meetRoomCon").empty();
			// 进入页面绘制第一页数据
			ajaxServer(window.Meeting_Info.AllMeetingRoom, postData,
					viewingRoomList);
		}
	});
	// 按人数排序
	$filter01.on("click", function() {
		var _fatherList = $(".meetRoomCon article");
		if (_fatherList.length > 0) {
			var mode = $(this).attr("mode");
			// 从大到小排序
			if (mode == 1) {
				var sortEle = $('.meetRoomCon>article').sort(function(a, b) {
					var a = parseInt($(a).find('.peopleLineH div').html());
					var b = parseInt($(b).find('.peopleLineH div').html());
					return a < b ? 1 : -1;
				});
				$('.meetRoomCon').empty().append(sortEle);
				$(this).attr("mode", 0);
				$(this).find("i").attr("class", "fa fa-long-arrow-down");
			}
			// 从小到大排序
			else if (mode == 0) {
				var sortEle = $('.meetRoomCon>article').sort(function(a, b) {
					var a = parseInt($(a).find('.peopleLineH div').html());
					var b = parseInt($(b).find('.peopleLineH div').html());
					return a > b ? 1 : -1;
				});
				$('.meetRoomCon').empty().append(sortEle);

				$(this).attr("mode", 1);
				$(this).find("i").attr("class", "fa fa-long-arrow-up");
			}
		}
		;

	});
	var _data = [
			'<div class="reasonMyModal">',
			'<div class="returnReasons">',
			'<form id="returnForm" method="post">',
			'<header>筛选条件</header>',
			'<div class="formList clear">',
			'<div class="listRight input-append controls">',
			'<select id="screenItems" class="listRightPadding6">',
			'<option value="">请选择</option>',
			'</select>',
			'<span class="add-on"><i class="icon-th fa fa-angle-down"></i></span>',
			'</div>',
			'<div class="listLeft listNameWidth6">',
			'<span class="title">会议室名称:</span>',
			'</div>',
			'</div>',
			'<div class="formList clear">',
			'<div class="listRight ">',
			'<input type="text" name="leaveDepartment" id="peopleSorts" class="listRightPadding5" placeholder="请填写人数" data-con="*" data-empty="请填写人数" value="">',
			'</div>', '<div class="listLeft listNameWidth5">',
			'<span class="title">容纳人数:</span>', '</div>', '</div>',
			'<footer class="screen1">', '<button type="reset">取消</button>',
			'<button type="button">确定</button>', '</footer>', '</form>',
			'</div>', '</div>' ];

	// 按条件筛选会议室
	$filter02.toast({
		msg : "筛选条件",
		type : 6,
		data : _data,
		apt : function() {
			loadFilterViewData(window.Meeting_Info.FilterMeetingRoom, $(
					"#save_Date").val());
		},
		dataChange : function($returnData) {
			var $data = {
				"date" : $("#save_Date").val(),// 当前日期
				"roomName" : $returnData[0],// 会议室名称
				"capacity" : $returnData[1]// 容纳人数
			};
			console.log($data);
			$.ajax({
				url : window.Meeting_Info.AllMeetingRoom,
				data : $data,// 要发送的数据
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
					$(".meetRoomCon").empty();
					$("#JmeetRoomConPage").val("1");
					viewingRoomList(resData);
					// 默认显示全部会议室
					var $filter = $(".filter-m li").eq(0);
					$filter.find("i").attr("class", "fa fa-meh-o");
					$filter.find("div").text("显示全部");
					if (resData == "") {
						$(".meetRoomCon").empty();
						$("#noRecord").css("display", "block");
					} else {
						$("#noRecord").css("display", "none");
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
	function loadFilterViewData($dataUrl, $date) {
		$.ajax({
			url : $dataUrl,
			data : $date,// 要发送的数据
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
				doAppendList(resData);
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
	// 添加筛选条件每条的内容
	function doAppendList($dataList) {
		var ckSize;
		var _selectItemId = [];

		ckSize = $dataList.length;
		for (var i = 0; i < ckSize; i++) {
			_selectItemId.push($dataList[i].roomName);
		}
		var st = _selectItemId;
		for (var s = 0; s < st.length; s++) {
			$("#screenItems").append(
					'<option value="' + st[s] + '">' + st[s] + '</option>');
		}
	}

	// 点击预定会议室
	$(".meetRoomCon").on(
			"click",
			"article",
			function() {
				$("#JmeetRoomConPage").val("1");
				var _dateStr = $("#save_Date").val();
				var _id = $(this).attr("id");
				var $_this = $(this).find("button").text();
				if ($_this.indexOf("不可预订") < 0) {
					window.location.href = window.Meeting_Info.Meeting_add_View
							+ "?meetingRoomId=" + _id + "&flag=1"
							+ "&date=" + _dateStr;
				}
			});
	// 请求数据
	function ajaxServer(url, data, callback, option) {
		var isScroll = false, isSending = false;
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
				$("body").toast({
					msg : "操作失败",
					type : 2,
					callMode : "func"
				});
			},
			success : function(data) {
				console.log(data);
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
});
