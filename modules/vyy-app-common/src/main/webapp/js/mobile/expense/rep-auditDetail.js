var confirmUrl="mobile/expense/expenseAudit.action",//确认报销url
    returnUrl="mobile/expense/expenseAudit.action",//拒绝报销url
    successUrl=$("#go-url").val();//拒绝成功/确认报销 之后的跳转链接
$(function(){
    //点击确认报销
    confirmExpense();
    //拒绝报销
    returnExpense();
    //进展反馈
    //realTimeFeedback();
    judgeBtnStyle();
    //查看大图
    lookBigImg();
  //pc预览图片
    $("body").pcPreviewImg({
         parentName : ".canningCopy",
         boxName:".canningCopyImg",
         tagName : "a"
    });
});
//确认报销
function confirmExpense(){
    var _href = window.location.href;
    $("#passBtn").click(function(){
        if(_href.indexOf("finance") > 0){
           /* if("" != $("#actualAmount").val()){
                $("#actualAmount").attr("data-con","m")
            }else{
                $("#actualAmount").attr("data-con","d");
            }*/
            $("form").verification({
                callback:function() {
                    $("#passBtn").toast({
                        msg: "备注",
                        type: 4,
                        callMode: "func",
                        autoFill: true,
                        dataChange: function ($returnData) {//$returnData退回原因
                            $.ajax({
                                url: confirmUrl,
                                type: "post",
                                data: {
                                    expenseAuditInfo: JSON.stringify(passGetData($returnData))
                                },
                                dataType: "json",
                                beforeSend: function () {
                                    $("body").loading({
                                        move: "show"
                                    });
                                },
                                success: function (resData, textStatus) {
                                    $("body").loading({
                                        move: "hide"
                                    });
                                    if (0 == resData.status) {
                                        //成功
                                        window.location.replace(successUrl);

                                    } else {
                                        $("body").toast({
                                            msg: resData.errorMsg,
                                            type: 2,
                                            callMode: "func"
                                        })
                                    }
                                },
                                error: function (XMLHttpRequest, textStatus, errorThrown) {
                                    $("body").loading({
                                        move: "hide"
                                    });
                                    $("body").toast({
                                        msg: textStatus,
                                        type: 2,
                                        callMode: "func"
                                    })
                                }
                            });
                        }
                    });
                }
            })
        }else{
            $("#passBtn").toast({
                msg: "备注",
                type: 4,
                callMode: "func",
                autoFill: true,
                dataChange: function ($returnData) {//$returnData退回原因
                    $.ajax({
                        url: confirmUrl,
                        type: "post",
                        data: {
                            expenseAuditInfo: JSON.stringify(passGetData($returnData))
                        },
                        dataType: "json",
                        beforeSend: function () {
                            $("body").loading({
                                move: "show"
                            });
                        },
                        success: function (resData, textStatus) {
                            $("body").loading({
                                move: "hide"
                            });
                            if (0 == resData.status) {
                                //成功
                                window.location.replace(successUrl);

                            } else {
                                $("body").toast({
                                    msg: resData.errorMsg,
                                    type: 2,
                                    callMode: "func"
                                })
                            }
                        },
                        error: function (XMLHttpRequest, textStatus, errorThrown) {
                            $("body").loading({
                                move: "hide"
                            });
                            $("body").toast({
                                msg: textStatus,
                                type: 2,
                                callMode: "func"
                            })
                        }
                    });
                }
            });
        }
    });
}
//拒绝报销
function returnExpense(){
    $("#returnBtn").toast({
        msg:"拒绝原因",
        type:4,
        dataChange:function($returnData){//$returnData退回原因
            var data = {
                expenseId:$("#passBtn").attr("expenseId"),
                entityAccountId:$("#passBtn").attr("entityAccountId"),
                remark:$returnData,
                commandInfo:{
                    commandType:"rollBack"
                }
            };
            $.ajax({
                url:returnUrl,
                type:"post",
                data:{
                    expenseAuditInfo:JSON.stringify(data)
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
                        window.location.replace(successUrl);
                    } else {
                        $("body").toast({
                            msg:resData.errorMsg,
                            type:2,
                            callMode:"func"
                        })
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
                    })
                }
            });
        }
    });
}
/* 进展反馈 */
function realTimeFeedback(){
    $("#commentFooter>img").click(function(){
        var $val= $.trim($("#commentFooter input").val());
        var $html='<div><b>'+formatDate()+'</b><span>'+$val+'</span></div>';
        if($val == ""){
            $("body").toast({
                msg:"反馈信息不能为空",
                type:2,
                callMode:"func",
                end : function(){
                    $("#commentFooter input").val("");
                }
            });
        }else{
            var $data={
                commandInfo:{
                    commandType:0
                },
                expenseId: $("#expenseId").val(),
                content:$val
            };
            $.ajax({
                url:"mobile/expense/sendMsg.action",
                data:{
                    feedbackInfo: JSON.stringify($data)
                },
                type:"post",
                dataType:"json",
                success:function(res){
                    if(0 == res.status){
                        $("#InfoDisplay").append($html);
                        $("#commentFooter input").val("");
                        scrollBottom();
                    }else{
                        $("body").toast({
                            msg:res.errorMsg,
                            type:2,
                            callMode:"func"
                        })
                    }
                },
                error:function(XMLHttpRequest, textStatus, errorThrown) {
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
//得到目前时间
function formatDate() {
    var now=new Date();
    var myyear = now.getFullYear();
    var mymonth = now.getMonth()+1;
    var myweekday = now.getDate();
    var myHour=now.getHours();
    var myMinute=now.getMinutes();
    var mySecond=now.getSeconds();

    if(mymonth < 10){
        mymonth = "0" + mymonth;
    }
    if(myweekday < 10){
        myweekday = "0" + myweekday;
    }

    return (myyear+"-"+mymonth + "-" + myweekday + " " +myHour + ":" + myMinute + ":" + mySecond);
}
//滚动到底部
function scrollBottom(){
    $('#InfoDisplay').scrollTop($('#InfoDisplay')[0].scrollHeight);
}
function passGetData(_returnData){
    var data = {
        "expenseId":$("#passBtn").attr("expenseId"),
        "entityAccountId":$("#passBtn").attr("entityAccountId"),
        "remark":_returnData,
        "actualCost":$("#actualAmount").val(),//实际报销金额
        "commandInfo":{
            "commandType":"general"
        }
    };
    return data;
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
//点击看大图
function lookBigImg(){
    $(document).on("click",".canningCopy>div[accessoryId] img",function(){
        var imgUrlArry=[],myIndex="";
        $(this).parent().attr("data-me","true");
        $(".canningCopy>div[accessoryId]").each(function(index,el){
            if(el.hasAttribute("data-me")){
                myIndex=index;
            }
            imgUrlArry.push($(this).children().attr("data-original"));
        });
        var selectUrl=$(".canningCopy>div[accessoryId]").eq(myIndex).children("img").attr("data-original");
        var imgUrl={
            currentImgUrl : selectUrl,//当前显示图片的http链接
            bigImgUrl : imgUrlArry//需要预览的图片http链接列表
        };
        $.eWeixinJSUtil.previewImg(imgUrl);
        $(this).parent().removeAttr("data-me");
    });
}
