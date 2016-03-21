$(function(){
    //点击重新报销
    ReApply();
    //催办
    reminders();
    //撤回
    withdraw();
    //进展反馈
    realTimeFeedback();
    //遍历每一个按钮组 显示的是几个按钮
    judgeBtnStyle();
});
//重新申请
function ReApply(){
    $("#ReApply").click(function(){
        //页面跳转
        window.location.replace($(this).attr("data-url")+"?id="+$("#expenseId").val());
    });
}
//催办
function reminders(){
    $("#urgeBtn").click(function(){
        var dataUrl=$(this).attr("data-url");
        var data={
            "expenseId":$("#expenseId").val(),
            "commandInfo":{
                "commandType":"remind"
            }
        };
        $.ajax({
            url:dataUrl,
            data:{
                expenseAuditInfo:JSON.stringify(data)
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
        var dataUrl=$(this).attr("data-url");
        var data={
            "expenseId":$("#expenseId").val(),
            "commandInfo":{
                "commandType":"undo"
            }
        };
        $.ajax({
            url:dataUrl,
            data:{
                expenseAuditInfo:JSON.stringify(data)
            },
            type:"post",
            dataType:"json",
            beforeSend:function(){/*
             $("body").loading({
             move:"show"
             });*/
            },
            success:function(resData, textStatus){
                /* $("body").loading({
                 move:"hide"
                 });*/
                if (0 == resData.status) {
                    //成功,提示
                    toastFun({
                        msg:"撤回成功",
                        type:1,
                        callMode:"func",
                        end : function(){
                            var _url = window.location.href.split("#")[1];
                            if(_url == "flag=0"){
                                //window.location.replace("mobile/expense/expenseRecordView.action");
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


/* 进展反馈 */
function realTimeFeedback(){
    $("#commentFooter>img").click(function(){
        var $val=$("#commentFooter input").val();
        var str = $.trim($val);
        if(str==""&&str.length==0){
            $("body").toast({
                msg:"反馈信息不能为空",
                type:2,
                callMode:"func",
                end : function(){
                    $("#commentFooter input").val("");
                }
            });
        }else{
            var $html='<div><b>'+formatDate()+'</b><span>'+$val+'</span></div>';
            var $data={
                commandInfo:{
                    commandType:0
                },
                expenseId: $("#expenseId").val(),
                content:str
            };
            $.ajax({
                url:$("#infoHeader").attr("data-url"),
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
                        msg:"系统异常，请稍后再试",
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
