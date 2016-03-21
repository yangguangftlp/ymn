/**
 * Created by 大猫 on 2015/12/7.
 */
var $seatViewUrl = "mobile/meeting/meeting_add_updateDate.action";
var $thisHref = window.location.href;
var $onDate = $thisHref.split("date=")[1];
var $meetingRoomId = $thisHref.substring($thisHref.indexOf("=") + 1,
		$thisHref.indexOf("&flag"));
var defualtTime = [ '00:00', '00:30', '01:00', '01:30', '02:00', '02:30',
		'03:00', '03:30', '04:00', '04:30', '05:00', '05:30', '06:00', '06:30',
		'07:00', '07:30', '08:00', '08:30', '09:00', '09:30', '10:00', '10:30',
		'11:00', '11:30', '12:00', '12:30', '13:00', '13:30', '14:00', '14:30',
		'15:00', '15:30', '16:00', '16:30', '17:00', '17:30', '18:00', '18:30',
		'19:00', '19:30', '20:00', '20:30', '21:00', '21:30', '22:00', '22:30',
		'23:00', '23:30' ];
var choose_list = $(".choose-time ul");
$(function() {
	initIndex($seatViewUrl, $onDate, $meetingRoomId);
	$("#thisDate").val($onDate);
});
function initIndex($url, $date, $id) {
	$.ajax({
		url : $url,
		data : {
			date : $date,
			meetingRoomId : $id
		},// 要发送的数据
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
			seachChecked(resData);
		},
		error : function() {
			$("body").toast({
				msg : "数据加载失败...",
				type : 1,
				callMode : "func"
			});
		}
	});
}
// 遍历选中的时间
function seachChecked($data) {
	var indexTime = $data.totalStartAndEndTime;
	startTime = indexTime[0].substr(0, 5), endTime = indexTime[1].substr(0, 5);
	console.log(startTime + "__" + endTime);
	$("#thisEndTime").val(endTime);
	//绘制空表格
	drawTimeList(startTime, endTime);
	//填充已选会议时间段
	var data = $data.startAndEndList;
	var dataSize = data.length;
	var _that = choose_list.find("li");
	for (var i = 0; i < dataSize; i++) {
		console.log(data[i]);
		var sTime = data[i].start.substr(0, 5), eTime = data[i].end
				.substr(0, 5);
		var tbSId = sTime.substr(0, 2) + sTime.substr(3, 2);
		var tbEId = eTime.substr(0, 2) + eTime.substr(3, 2);
		var $s1 = choose_list.find("#" + tbSId);
		var $s2 = choose_list.find("#" + tbEId);
		
		if($s2.index() == "-1"&& $s1.index() != "-1"){
			 for (var j = $s1.index(); j < _that.length; j++) {
					_that.eq(j).addClass("Checked");
				}
		}
		for (var j = $s1.index(); j < $s2.index(); j++) {
			_that.eq(j).addClass("Checked");
		}
	}
}
// 生成表格
function drawTimeList(startTime, endTime) {
	var len = defualtTime.length;
	var _index = 0;
	var _chooseItem = [];
	for (var i = 0; i < len; i++) {
		for (var j = 0; j < len; j++) {
			if (defualtTime[j] == endTime) {
				if (defualtTime[i] == startTime) {
					for (var k = 0; k < j - i; k++) {
						_index = i + k;
						var liId = defualtTime[_index].substr(0, 2)
								+ defualtTime[_index].substr(3, 2);
						_chooseItem.push('<li id="' + liId
								+ '" class="bord-l"><span>'
								+ defualtTime[_index] + '</span></li>');
					}
					break;
				}
			}
		}
	}
	choose_list.append(_chooseItem.join(""));
	var _that = choose_list.find("li");
	var thatlen = _that.length;
	for (var i = 0; i < thatlen; i++) {
		if ((_that.eq(i).index() + 1) % 6 == 0 && _that.eq(i).index() != 0) {
			_that.eq(i).addClass("bord-r");
		}
	}
}
