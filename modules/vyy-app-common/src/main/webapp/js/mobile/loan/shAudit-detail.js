var confirmUrl="mobile/loan/loanOperate.action",//通过url
    returnUrl="mobile/loan/loanOperate.action",//拒绝url
    successUrl="mobile/loan/loanDetailView.action";//拒绝成功/确认报销 之后的跳转链接
$(function(){
	if($("#loanType").val() == "0"){
        $("#selectAuditorList").removeClass("borderTop");
    }
	$("#bankAccount").text($.trim($("#bankAccount").text()).replace(/(\d{4})(?=\d)/g,"$1 "));
    //通过
    through();
    //拒绝
    refuse();
    //点击看大图
    lookBigImg();
   //pc预览图片
  $("body").pcPreviewImg({
          parentName : ".canningCopy",
          boxName:".canningCopyImg",
          tagName : "a"
    });
});
//通过
function through(){
    $("#sureBtn").click(function(){
        $("#sureBtn").toast({
            msg:"审核说明",
            type:4,
            callMode: "func",
            autoFill: true,
            dataChange:function($returnData){//$returnData退回原因
                $.ajax({
                    url:confirmUrl,
                    type:"post",
                    data:{
                        loanInfo:JSON.stringify(passGetData($returnData))
                    },
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
                            //成功
                            window.location.replace(successUrl+"?flag=1&id="+$("#loanId").val()+"&loanType="+$("#loanType").val());
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
            }
        });
    });
}
//拒绝
function refuse(){
    $("#returnBtn").toast({
        msg:"拒绝原因",
        type:4,
        dataChange:function($returnData){//$returnData退回原因
            var data = {
                "id":$("#loanId").val(),
                "loanType" : $("#loanType").val(),
                "remark":$returnData,
                "commandInfo":{
                    "commandType" : "2"
                }
            };
            if(!!$("#sureBtn").attr("entityAccountId")){
                data.eaId = $("#sureBtn").attr("entityAccountId");
            }
            $.ajax({
                url:returnUrl,
                type:"post",
                data:{
                    loanInfo:JSON.stringify(data)
                },
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
                        //成功
                        window.location.replace(successUrl+"?id="+$("#loanId").val()+"&loanType="+$("#loanType").val());
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
        }
    });
}
//确认报销得到数据
function passGetData(_returnData){
    var data = {
        "id":$("#loanId").val(),
        "loanType" : $("#loanType").val(),
        "remark":_returnData,
        "commandInfo":{
            "commandType" : "1"
        }
    };
    if(!!$("#sureBtn").attr("entityAccountId")){
        data.eaId = $("#sureBtn").attr("entityAccountId");
    }
    return data;
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

