/**
 * Created by liyuxia on 2015/8/21.
 */
$(function(){
    //申请审核
    $("#detailAudit").click(function(){
        //页面跳转
        window.location.replace($(this).attr("data-url"));
    });

    //催办
    $("#urgeBtn").click(function(){
        var dataUrl=$(this).attr("data-url");
        var data={
            "uId" : traverse(),
            "expenseId":$(this).attr("expenseId"),
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
                        msg:"催办成功",
                        type:1,
                        callMode:"func"
                    });

                } else {
                    alert("申请失败");
                }
            },
            error:function(XMLHttpRequest, textStatus, errorThrown) {
            }
        });

    });
    //点击看大图
    lookBigImg();
  //pc预览图片
    $("body").pcPreviewImg({
         parentName : ".canningCopy",
         boxName:".canningCopyImg",
         tagName : "a"
    });
    //撤回
    withdraw();
    judgeBtnStyle();
    //点击看大图
    lookBigImg();
});
//遍历审核人和财务
function traverse(){
    var $auditors=new Array();
    //遍历审核
    $(".addedPeopleList:eq(0)>div").each(function(index){
        if("0" == $(this).attr("status")){
            $auditors.push($(this).attr("uId"));
        }
    });

    var $uId = "";
    if(0 == $auditors.length){
        //遍历财务
        var $finance=new Array();
        $(".addedPeopleList:eq(1)>div").each(function(index){
            if("0" == $(this).attr("status")){
                $finance.push($(this).attr("uId"));
            }
        });
        if($finance.length > 0){
            $uId=$finance[0];
        }
    }else if($auditors.length>0){
        $uId=$auditors[0];
    }
    return $uId;
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

//撤回
function withdraw(){
    $("#withdrawBtn").click(function(){
        var dataUrl=$("#urgeBtn").attr("data-url");
        var data={
            "expenseId":$(this).attr("expenseId"),
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
                        msg:"成功撤回",
                        type:1,
                        callMode:"func",
                        end: function(){
                            var _url = window.location.href;
                            if(_url.indexOf("#flag") > 0){
                                window.history.go(-1);
                            }else{
                                WeixinJSBridge.call('closeWindow');
                            }

                        }
                    });

                } else {
                    toastFun({
                        msg:resData.errorMsg,
                        type:2,
                        callMode:"func"
                    });
                }
            },
            error:function(XMLHttpRequest, textStatus, errorThrown) {
                toastFun({
                    msg:"系统异常，请稍后再试",
                    type:2,
                    callMode:"func"
                });
            }
        });

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

