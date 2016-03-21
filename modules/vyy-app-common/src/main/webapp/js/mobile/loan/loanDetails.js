var applicationUrl="mobile/loan/loanOperate.action",//重新申请地址
    remindersUrl="mobile/loan/loanOperate.action",//催办地址
    withdrawsUrl="mobile/loan/loanOperate.action";//撤回地址
$(function(){
	if($("#loanType").val() == "0"){
        $("#selectAuditorList").removeClass("borderTop");
    }
	$("#bankAccount").text($.trim($("#bankAccount").text()).replace(/(\d{4})(?=\d)/g,"$1 "));
    //点击重新申请
    ReApply();
    //催办
    reminders();
    //撤回
    withdraw();
    //查看大图
    lookBigImg();
    //遍历每一个按钮组 显示的是几个按钮
    judgeBtnStyle();
    //pc预览图片
    $("body").pcPreviewImg({
         parentName : ".canningCopy",
         boxName:".canningCopyImg",
         tagName : "a"
    });
});
//重新申请
function ReApply(){
    $("#ReApply").click(function(){
        //页面跳转
    	var loanType = $("#loanType").val();
    	if("0" == loanType){
    		 window.location.replace("mobile/loan/privateLoanView.action?flag=1"+"&id="+$("#loanId").val());
    	}else if("1" == loanType){
    		 window.location.replace("mobile/loan/publicLoanView.action?flag=1"+"&id="+$("#loanId").val());
    	}
       //其他需要提示
    });
}
//催办
function reminders(){
    $("#urgeBtn").click(function(){
        var _remindersPeople=traverse();
        var data={
            "uId" : _remindersPeople.uId,
            "eaId":_remindersPeople.eaId,
            "id":$("#loanId").val(),
            "loanType" : $("#loanType").val(),
            "commandInfo":{
                "commandType":"8"
            }
        };
        $.ajax({
            url:remindersUrl,
            data:{
            	loanInfo:JSON.stringify(data)
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
                    //成功,提示
                    toastFun({
                        msg:"催办成功",
                        type:1,
                        callMode:"func"
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
                    msg:textStatus,
                    type:2,
                    callMode:"func"
                });
            }
        });
    });
}

//撤回
function withdraw(){
    $("#withdrawBtn").click(function(){
        var data={
            "id":$("#loanId").val(),
            "loanType" : $("#loanType").val(),
            "commandInfo":{
                "commandType":"9"
            }
        };
        $.ajax({
            url:withdrawsUrl,
            data:{
            	loanInfo:JSON.stringify(data)
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
                    //成功,提示
                    toastFun({
                        msg:"撤回成功",
                        type:1,
                        callMode:"func",
                        end : function(){
                        	var _url = window.location.href.split("#")[1];
                        	if(_url == "flag=0"){
                        		//window.location.replace("mobile/loan/myLoanView.action");
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
                    msg:textStatus,
                    type:2,
                    callMode:"func"
                });
            }
        });
    });
}


//遍历审核人
function traverse(){
    var $auditors=new Array();
    //遍历审核
    $(".addedPeopleList:eq(0)>div").each(function(index){
        if("0" == $(this).attr("status")){
            $auditors.push({
                uId:$(this).attr("uId"),
                eaId:$(this).attr("id")
            });
        }
    });
    return $auditors[0];
}
//点击看大图
function lookBigImg(){
    $(document).on("click",".canningCopy>div[accessoryId] img",function(){
        var imgUrlArry=[],myIndex="";
        $(this).parents("div[accessoryId]").attr("data-me","true");
        $(".canningCopy>div[accessoryId]").each(function(index,el){
            if(el.hasAttribute("data-me")){
                myIndex=index;
            }
            imgUrlArry.push($(this).children("a").attr("data-original"));
        });
        var selectUrl=$(".canningCopy>div[accessoryId]").eq(myIndex).children("a").attr("data-original");
        var imgUrl={
            currentImgUrl : selectUrl,//当前显示图片的http链接
            bigImgUrl : imgUrlArry//需要预览的图片http链接列表
        };
        $.eWeixinJSUtil.previewImg(imgUrl);
        $(this).parents("div[accessoryId]").removeAttr("data-me");
    });
}
//遍历每一个按钮组 显示的是几个按钮
function judgeBtnStyle(){
    $(".btnGroup").each(function(){
        var $this=$(this);
        if($this.children("button").length == 2){
            $this.children("button").removeClass("singleBtn").addClass("doubleBtn");
        }else if($this.children("button").length == 1){
            $this.children("button").removeClass("doubleBtn").addClass("singleBtn");
        }
    });

}
