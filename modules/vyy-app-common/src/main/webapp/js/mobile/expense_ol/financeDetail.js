/*确认报销 data 关键字 字段
 * 拒绝 data字段，url
 * */
var confirmUrl="mobile/expense/expenseAudit.action",//确认报销url
    returnUrl="mobile/expense/expenseAudit.action",//拒绝报销url
    successUrl="mobile/expense/expenseDetailView.action";//拒绝成功/确认报销 之后的跳转链接
$(function(){
    //点击确认报销
    confirmExpense();
    //拒绝报销
    returnExpense();
    //进展反馈
    realTimeFeedback();
});
//确认报销
function confirmExpense(){
    $("#sureBtn").click(function(){
        $("#textStatus").verification({
            callback : function(){
                $("#sureBtn").toast({
                    msg:"备注",
                    type:4,
                    callMode: "func",
                    autoFill:true,
                    dataChange:function($returnData){//$returnData退回原因
                        $.ajax({
                            url:confirmUrl,
                            type:"post",
                            data:{
                                expenseAuditInfo:JSON.stringify(passGetData($returnData))
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
                                    window.location.replace(successUrl+"?id="+$("#expenseId").val());

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
        });

    });
}
//拒绝报销
function returnExpense(){
    $("#returnBtn").toast({
        msg:"拒绝原因",
        type:4,
        dataChange:function($returnData){//$returnData退回原因
            var data = {
                "expenseId":$("#expenseId").val(),
                "entityAccountId":$("#returnBtn").attr("entityAccountId"),
                "remark":$returnData,
                "commandInfo":{
                    "commandType":"rollBack"
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
                        window.location.replace(successUrl+"?id="+$("#expenseId").val());
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
//确认报销得到数据
function passGetData(_returnData){
    var data = {
        "expenseId":$("#expenseId").val(),
        "entityAccountId":$("#sureBtn").attr("entityAccountId"),
        "actualCost":$("#actualAmount").val(),//实际报销金额
        "remark":_returnData,
        "commandInfo":{
            "commandType":"general"
        }
    };
    return data;
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

