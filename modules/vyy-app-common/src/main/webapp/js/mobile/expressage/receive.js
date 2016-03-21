var receiveUrl = "mobile/courier/receive.action";
$(function(){
	//点击领取
	doReceive(receiveUrl);
});
//点击领取
function doReceive(dataUrl){
	$("#receive").on("click",function(){
		$.ajax({
            url:dataUrl,
            data:{
            	id:$(this).attr("data-val")
            },
            type:"post",
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
                    $("body").loading({
                        move:"hide"
                    });
                    //成功,提示
                    toastFun({
                        msg:"领取成功！",
                        type:1,
                        callMode:"func"
                    });
                    $("#receive").hide();
                    $(".status-on").removeClass("unrcv").addClass("rcv");
                    setTimeout(function(){
                    	window.history.go(-1);
                    	window.location.reload();
                    },500);

                } else {
                	toastFun({
                        msg:"领取失败！",
                        type:1,
                        callMode:"func"
                    });
                }
            },
            error:function(XMLHttpRequest, textStatus, errorThrown) {
            }
        });
	});
}