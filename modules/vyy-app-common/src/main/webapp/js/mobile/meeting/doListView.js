var $thisDataUrl = "mobile/meeting/getMeetingRoomsOfTheDay.action";

$(function() {
	var now = new Date();
	var y = now.getFullYear();
	var m = now.getMonth() + 1;
	var n = now.getDate();
	var a = m < 10 ? "0" + m : m;
	var d = n < 10 ? "0" + n : n;
	var _thisDay = y+'-'+a+'-'+d;
	// 加载界面数据
	console.log(_thisDay);
	 $("#save_Date").val(_thisDay);
	//隐藏域保存页码，默认加载第一页
	var pageNow = $("#JmeetRoomConPage").val();
	var isScroll = false,isSending = false;
	var postData = {"pageIndex":pageNow,"date":_thisDay};
	//进入页面绘制第一页数据
	ajaxServer($thisDataUrl,postData,viewingRoomList);
	
	
    $(document).scroll(function(){
        var $filter_mText = $(".filter-m").find("ul li").eq(0).find("div").text();
		var _thisNoSign = $(".meetRoomCon").find("article");
		//显示空闲情况下排除预定满的会议室
		if($filter_mText.indexOf("空闲") > 0){
			_thisNoSign.each(function(){
				var _that = $(this).find("span.timeCounts");
				if (_that.text().indexOf("满") > 0) {
					$(this).hide();
				}
			});
		}
        if($(document).scrollTop() >= $(document).height() - $(window).height()){
        	if(!isSending){
        		console.log(11);
        		isScroll = true;
        		var pageNow = parseInt($("#JmeetRoomConPage").val());
        		pageNow++;
        		$("#JmeetRoomConPage").val(pageNow);
        		_thisDay = $("#save_Date").val();
        		var postData = {"pageIndex":pageNow,"date":_thisDay};
        		ajaxServer($thisDataUrl,postData,viewingRoomList);
        	}	
        }
    });
  //请求数据
    function ajaxServer(url,data,callback,option){
    	var defaultOption = {
                type : 'post',
                url : url,
                data : data || {},
                dataType : "json",
                beforeSend:function(){
                	isSending = true;
                	$("body").loading({
    					move : "show"
    				});
        },
        complete:function(){
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
                //console.debug(err);
                $("body").toast({
    				msg : "操作失败",
    				type : 2,
    				callMode : "func"
    			});
        },
        success : function(data) {
        	isSending = false;
        	if(data.length){
        		if (callback) {
        			callback(data);
        		}
        	}else{
        		if(isScroll){
        			$("body").toast({
    					msg : "没有更多记录",
    					type : 1,
    					callMode : "func"
    				});
    	    	}else{
    	    		$("#noRecord").css("display","block");
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

function viewingRoomList($data) {
	// 页面数据初始化
	room_available_rooms = $data;
	if (room_available_rooms.length > 0) {
		for (var int = 0; int < room_available_rooms.length; int++) {
			var room = room_available_rooms[int];
			var _timeCounts = room.totalTimeEquation;
			if (_timeCounts == "0") {
				_timeCounts = "已预定满";
			} else {
				_timeCounts = "空闲" + _timeCounts + "小时";
			}
			$(".meetRoomCon")
					.append(
							"<article id="
									+ room.id
									+ ">"
									+ "<div class=\"listRight\"><div class=\"listNameWidth8\"><span class=\"title\">"
									+ room.roomName
									+ "</span></div></div>"
									+ "<div class=\"listLeft listRightPadding7 peopleLineH\"><div>"
									+ room.capacity
									+ "</div>人<span class=\"timeCounts\">"
									+ _timeCounts
									+ "</span></div>"
									+ "<div class=\"mEquipment\"><i class=\"videoCamera\"></i>"
									+ room.equipment + "</div>"
									+ "<header style=\"display:none\">"
									+ room.adress + "</header>" +
									"<b class=\"fa fa-angle-right\"></b>"
									+ "<button class=\"canBeReserve\" value=\""
									+ _timeCounts + "\">" + _timeCounts
									+ "</button>" + "</article>");
		}
$("#noRecord").hide();
		$(".meetRoomCon").find("article").each(
				function() {
					var _that = $(this).find("span.timeCounts");
					if (_that.text().indexOf("满") > 0) {
						_that.css("color", "red");
						_that.parent().siblings(".canBeReserve").text("不可预订")
								.css("color", "#d9d9d9");
						_that.parent().siblings("b").hide();
					} else {
						_that.css("color", "#3CC51F");
						_that.parents(".canBeReserve").css("color", "#fff");
					}
				});
	}
}
