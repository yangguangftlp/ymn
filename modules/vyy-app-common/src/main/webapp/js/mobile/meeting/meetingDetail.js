/**
 * Created by 大猫 on 2016/1/5.
 */
window.Meeting_Info = {
	// 会议相关访问地址
	"Meeting_Ensure": "mobile/meeting/ensureAttendMeeting.action",
    "Meeting_Record_View": "mobile/meeting/meetingsView.action",
    "Meeting_Cancel": "mobile/meeting/deleteMeeting.action"
};
$(function(){
	var startAndEnd = $("#startAndEnd").val();
	startAndEnd = startAndEnd.substr(0,5)+"~"+startAndEnd.substr(9,5);
	$("#startAndEndSpan").text(startAndEnd);
    $("#cancelBtn").click(function(){
    	$("body").toast({
            msg:"确认取消该会议吗？",
            type:5,
            callMode:"func",
            end:function(){
            	var meetingId = $("#meetingId").val();
            	$.ajax({
            		type: "post",//使用post方法访问后台
                    dataType: "json",//返回json格式的数据
                    url: window.Meeting_Info.Meeting_Cancel,//要访问的后台地址
                    data:{meetingId: meetingId},//要发送的数据
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
                    success:function(resData){
                        $("body").loading({
                            move:"hide"
                        });
                        console.log(resData);
                        var errorCode = resData;
                        if(errorCode==1){
	                        $("body").toast({
	                            msg:"取消会议成功",
	                            type:1,
	                            callMode:"func",
	                            end:function(){
	                            	WeixinJSBridge.call('closeWindow');
	                            }
	                        });
                        } /*else if(errorCode==1) {
                        	$("body").toast({
	                            msg:"取消会议出错，请稍后再试",
	                            type:1,
	                            callMode:"func"
	                        });
                        } else if(errorCode==2){
                        	var errorMsg = resData.errorMsg;
                        	$("body").toast({
	                            msg:errorMsg,
	                            type:1,
	                            callMode:"func",
	                            end:function(){
	                            	WeixinJSBridge.call('closeWindow');
	                            }
	                        });
                        }*/
                    },
                    error:function(){
                    	$("body").toast({
                            msg:"取消会议出错，请稍后再试",
                            type:1,
                            callMode:"func"
                        });
                    }
                });
            }
        });
    });
    
    // 确认参加会议事件
    $("#affirmBtn").click(function(){
    	var meetingId = $("#meetingId").val();
		$.ajax({
        	type: "post",//使用post方法访问后台
            dataType: "json",//返回json格式的数据
            url: window.Meeting_Info.Meeting_Ensure,//要访问的后台地址
            data:{meetingId: meetingId},//要发送的数据
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
            success: function(resData){
            	$("body").loading({
                    move:"hide"
                });
                if(resData==1){
                	$("body").toast({
                        msg:"确认参加会议成功",
                        type:1,
                        callMode:"func",
                        end:function(){
                        	setTimeout(function(){
                        		location.reload();
                        	},200);
                        }
                    });
                } 
            },
            error:function(){
            	$("body").toast({
                    msg:"确认会议出错，请稍后再试",
                    type:1,
                    callMode:"func"
                });
            }
		});
    });
});
