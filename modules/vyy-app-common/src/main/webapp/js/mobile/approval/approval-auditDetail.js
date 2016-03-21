/**
 * Created by liyuxia on 2015/8/21.
 */
$(function(){
    //点击通过按钮
    $("#passBtn").click(function(){
        $("body").toast({
            msg:"通过说明",
            type:4,
            callMode: "func",
            autoFill: true,
            dataChange:function($returnData){//$returnData退回原因
                $.ajax({
                    url:$("#passBtn").attr("data-url"),
                    type:"post",
                    data:{
                        approvalAuditInfo:JSON.stringify(passGetData($returnData))
                    },
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
                            //成功
                            $("body").loading({
                                move:"hide"
                            });
                            //alert(window.location.href);
                            window.location.replace($("#go-url").val());
                        } else {
                            $("body").toast({
                                msg:resData.errorMsg,
                                type:2,
                                callMode:"func"
                            });
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

    });
    //退回请假申请
    $("#returnBtn").toast({
        msg:"退回原因",
        type:4,
        dataChange:function($returnData){//$returnData退回原因
            var data = {
                "approvalId":$("#returnBtn").attr("approvalId"),
                "entityAccountId":$("#returnBtn").attr("entityAccountId"),
                "remark":$returnData,
                "commandInfo":{
                    "commandType":"rollBack"
                }
            };
            $.ajax({
                url:$("#returnBtn").attr("data-url"),
                type:"post",
                data:{
                    approvalAuditInfo:JSON.stringify(data)
                },
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
                        //成功
                        $("body").loading({
                            move:"hide"
                        });
                        //alert(window.location.replace);
                        window.location.replace($("#go-url").val());

                    } else {
                        $("body").toast({
                            msg:resData.errorMsg,
                            type:2,
                            callMode:"func"
                        });
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
    var membersListWarp = $(".membersListWarp").eSelectPeople({
        btCance : function(_self) {
            $(".warp").show();
            _self.hide();
        },
        btOk : function(_self,data) {
            if(!!data && data.length > 0 ){
                var div = null;
                var img = null;
                var span = null;
                var elemI = null;
                var uId = null;
                //判断是否已存在选择的用户 这里需要去重
                var flag = false;
                var type = ("false" == _self.attr("_flag"))?'0':'1';
                for(var i = 0,size = data.length;i<size;i++){
                    uId = data[i].uId;
                    if("0" == type){
                        //审核人只能选择一个人
                        $(".addedPeopleList").find("div[uId]").remove();
                    }
                    else {
                        $(".addedPeopleList > div").each(function(){
                            if($(this).attr("uId") == uId){
                                flag = true;
                            }
                        });
                        if(flag){
                            continue;
                        }
                    }
                    div = $("<div>");
                    div.attr("uId",uId);
                    div.attr("uAvatar",data[i].uAvatar);
                    div.attr("uName",data[i].uName);
                    img = $("<img>");
                    img.attr("src",data[i].uAvatar);
                    img.on("click",function(){
                        $(this).parent().remove();
                    });
                    span = $("<span>");
                    span.append(data[i].uName);
                    elemI = $("<i>");
                    elemI.css("cursor","pointer");
                    elemI.on("click",function(){
                        $(this).parent().remove();
                    });
                    div.append(img);
                    div.append(span);
                    div.append(elemI);
                    $(".addedPeopleList").find(".addPeople").before(div);
                };
            }
            $(".warp").show();
            _self.hide();
        }
    });

    $("#addAuditor").on("click",function(){
        $(".warp").hide();
        membersListWarp.eshow({flag:false});
    });

    //点击看大图
    lookBigImg();
  /*//客户端上传图片
    $("body").clientUploadImg({
    		uploadUrl:"mobile/common/uploadAccessory.action",
            boxName :'#addScanCopy',
            maxImgLen :9,
            imgType:"jpg|png|JPG|PNG"
	});*/
  //pc预览图片
    $("body").pcPreviewImg({
            parentName : ".canningCopy",
            boxName:".canningCopyImg",
            tagName : "a"
      });
});
function passGetData(_returnData){
    var entityUserInfo = [];
    $(".addedPeopleList> div[uId]").each(function() {
        entityUserInfo.push({
            "uId" : $(this).attr("uId"),
            "uAvatar" : $(this).attr("uAvatar"),
            "uType" : 2,
            "personType" : '0',
            "uName" : $(this).attr("uName"),
            "entityType" : "3"
        });
    });
    var data = {
        "entityUserInfo" : entityUserInfo,
        "approvalId":$("#passBtn").attr("approvalId"),
        "entityAccountId":$("#passBtn").attr("entityAccountId"),
        "remark":_returnData,
        "commandInfo":{
            "commandType":"general"
        }
    };
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
