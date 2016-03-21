window.Meeting_Info = {
	// 会议相关访问地址
    "Meeting_Launch_View": "/energy-web/meeting/launchMeetingView.action"
};

$(function(){
	// 页面数据初始化
	$("#room_available_location").html(room_available_filters.buildingName);
	room_available_rooms = JSON.parse(JSON.stringify(room_available_rooms));
	if(room_available_rooms.length>0){
		for (var int = 0; int < room_available_rooms.length; int++) {
			var room = room_available_rooms[int];
			$(".meetRoomCon").append("<article id="+room.id+">"+
				"<header>"+room.roomName+"</header>"+
				"<div><i class=\"user\"></i>"+room.capacity+"人</div>"+
				"<div><i class=\"videoCamera\"></i>"+room.equipment+"</div>"+
				"<b class=\"fa fa-angle-right\"></b>"+
				"</article>");
		}
	} else {
		$("#noRecord").css("display","block");
	}
	// 点击事件
    $(".meetRoomCon article").click(function(){
        var params = "buildingId="+room_available_filters.buildingId+
		"&buildingName="+room_available_filters.buildingName+
		"&date="+room_available_filters.date+
		"&time="+room_available_filters.time+
		"&timeSpan="+room_available_filters.timeSpan+
		"&roomId="+$(this).attr("id")+
		"&roomName="+$(this).children("header").text();
		window.location.href=window.Meeting_Info.Meeting_Launch_View+"?"+params;
    });
	//筛选
	var $filter02 = $(".filter-m li").eq(2);//筛选条
	$filter02.toast({
        msg:"筛选条件",
        type:4,
        dataChange:function($returnData){
            var data = {
                "approvalId":$("#returnBtn").attr("approvalId"),
                "entityAccountId":$("#returnBtn").attr("entityAccountId"),
                "remark":$returnData,
                "commandInfo":{
                    "commandType":"rollBack"
                }
            };
            $.ajax({
                url:$filter02.attr("data-url"),
                type:"post",
                data:{
                    approvalAuditInfo:JSON.stringify(data)
                },
                dataType:"json",
                beforeSend:function(){
                    $("body").loading({
                        move:"show"
                    });
                },
                complete:function(){
                    $("body").loading({
                        move:"hide"
                    });
                },
                success:function(resData, textStatus){
                    if (!!resData && "0" == resData.status) {
                        //成功
                        $("body").loading({
                            move:"hide"
                        });
                        window.location.href=$("#go-url").val();

                    } else {
                        $("body").toast({
                            msg:resData.errorMsg,
                            type:2,
                            callMode:"func"
                        });
                    }
                },
                error:function(XMLHttpRequest, textStatus, errorThrown) {
                	$("body").toast({
                        msg:"系统异常，请稍后再试",
                        type:2,
                        callMode:"func"
                    });
                }
            });
        }
    });
});