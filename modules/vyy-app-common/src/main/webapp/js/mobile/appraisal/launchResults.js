var cancelUrl="mobile/appraisal/appraisalOperate.action",
    jumpUrl="mobile/appraisal/launchAppraisalView.action",//取消成功后跳到发起页面
    refererUrl="mobile/appraisal/queryAppraisalView.action";//跳转列表页面的URL
$(function(){
    cancelEvaluation()
});
//取消评价
function cancelEvaluation(){
    $("#cancelEvaluation").on(touchEv,function(){
        var data={
    		id : $("#appraisalId").val(),
            commandInfo : {
                commandType : 2
            }
        };
        $.ajax({
            url: cancelUrl,
            data:{
                jsonAppraisalInfo:JSON.stringify(data)
            },
            type:"post",
            dataType:"json",
            beforeSend:function(){
                $("body").loading({
                    move:"show"
                });
            },
            success:function(resData, textStatus){
            	$("body").loading({
                    move:"hide"
                });
                if (0 == resData.status) {
                    $("body").toast({
                        msg:"取消成功",
                        type:1,
                        callMode:"func",
                        end:function(){
                        	var _url = window.location.href.split("#")[1];
	                    	if(_url == "flag=0"){
	                    		//window.location.replace("mobile/appraisal/queryAppraisalView.action");
	                    		window.history.go(-1);
	                    	}else{
	                    		WeixinJSBridge.call('closeWindow');
	                    	}
                        }
                    });
                } else {
                    
                    $("body").toast({
                        msg:resData.errorMsg,
                        type:2,
                        callMode:"func"
                    });
                }
            },
            error:function(XMLHttpRequest, textStatus, errorThrown) {
                $("body").loading({
                    move:"hide"
                });
                $("body").toast({
                    msg:"系统异常，请稍后再试",
                    type:2,
                    callMode:"func"
                });
            }
        });
    });
}
