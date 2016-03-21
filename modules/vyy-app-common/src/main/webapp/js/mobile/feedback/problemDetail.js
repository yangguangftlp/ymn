$(function(){
    //点击看大图
    lookBigImg();
    /* 实时反馈信息 */
    realTimeFeedback();
    //已解决
    resolvedProblem();

    formatDate();
    /* 滚动条滚动到底部 */
    scrollBottom();
  //pc预览图片
    $("body").pcPreviewImg({
            parentName : ".canningCopy",
            boxName:"div",
            tagName : "img"
      });
});
/* 实时反馈信息 */
function realTimeFeedback(){
    $("#commentFooter>img").click(function(){
        var $val=$.trim($("#commentFooter input").val());
        if("" == $val){
            $("body").toast({
                msg:"评论不可以为空",
                type:2,
                callMode:"func"
            })
        }else{
            var $html='<div><b>'+formatDate()+'</b><span>'+$val+'</span></div>';
            var $data={
                commandInfo:{
                    commandType:0
                },
                feedbackId: $.trim($("#feedbackId").val()),
                content:$val
            };
            $.ajax({
                url:$("input#go-url").val(),
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
                error:function(){
                    $("body").toast({
                        msg:"系统连接异常，请稍后再试",
                        type:2,
                        callMode:"func"
                    })
                }
            });
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

//已解决
function resolvedProblem(){
    $("#resolved").click(function(){
        var $data={
            commandInfo:{
                commandType:2
            },
            feedbackId: $.trim($("#feedbackId").val())
        };
        $.ajax({
            url: $("input#go-url").val(),
            data:{
                feedbackInfo:JSON.stringify($data)
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
            success:function(res){
                if(0 == res.status){
                    $("body").loading({
                        move:"hide"
                    });
                    window.location.replace("mobile/feedback/feedbackDetail.action?id="+$("#feedbackId").val());
                }
            },
            error:function(XMLHttpRequest,errorText){
                console.log(errorText);
                $("body").toast({
                    msg:"确认失败，请稍后再试",
                    type:2,
                    callMode:"func"
                })
            }
        });
    });
}

function formatDate(date) {
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

function scrollBottom(){
    $('#InfoDisplay').scrollTop($('#InfoDisplay')[0].scrollHeight);
}
