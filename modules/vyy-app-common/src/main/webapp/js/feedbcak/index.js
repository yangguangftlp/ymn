$(function(){
    //提交
    submitForm();
    //取消
    cancelForm();
    //上传资料
    uploadMat();
    //查看大图
    lookBigImg();
    //删除材料
    delImg();
  //客户端上传图片
    $("body").clientUploadImg({
    		uploadUrl:"mobile/common/uploadAccessory.action",
            boxName :'#addScanCopy',
            maxImgLen :9,
            imgType:"jpg|png|JPG|PNG"
	});
});
function getData(){
    var accessoryId=[];
    $(".canningCopy>div[accessoryId]").each(function(){
        accessoryId.push($(this).attr("accessoryId"));
    });
    var data={
        problem:$.trim($("#problem").val()),//问题内容
        suggest :$.trim($("#suggest").val()),//修改意见
        accessoryInfo:{mediaIds:accessoryId}
    };
    return data;
}

//提交
function submitForm(){
    $("#submitBtn").click(function(){
        $("#fromFeedback").verification({
            callback:function(){
                var feedbackData=getData();
                $.ajax({
                    url: $("#submitBtn").attr("data-url"),
                    data:{
                        feedbackInfo:JSON.stringify(feedbackData)
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
                        if(0 == resData.status){
                            $("body").toast({
                                msg:"感谢您的反馈！我们会尽快给您回复。",
                                type:1,
                                callMode:"func",
                                end: function(){
                                    window.location.href="mobile/feedback/feedbackDetail.action?id="+resData.value;
                                }
                            })
                        }else{
                            $("body").toast({
                                msg:resData.errorMsg,
                                type:2,
                                callMode:"func"
                            })
                        }
                    },
                    error:function(XMLHttpRequest, textStatus, errorThrown) {
                        $("body").toast({
                            msg:"提交失败，请稍后再试",
                            type:2,
                            callMode:"func"
                        })
                    }
                });
            }
        })
    });
}

//取消
function cancelForm(){
    $("#cancelBtn").click(function(){
        $("#fromFeedback textarea").val("");
        $(".canningCopy div[accessoryId]").remove();
        $("body").toast({
            msg:"取消成功",
            type:1,
            callMode:"func"
        })
    });
}

//上传图片
function uploadMat(){
    //上传图片
    $("#addScanCopy>img").click(function(){
        var $callback=function(data){
            for(var i=0;i<data.value.length;i++){
                var $str='<div class="canningCopyImg" accessoryId='+data.value[i].accessoryId+'>'+
                    '<a href="javascript:void(0)" data-original='+data.value[i].original+'><img src='+data.value[i].thumb+' data-original='+data.value[i].original+'></a>'+
                    '<i></i>'+
                    '</div>';
                $("#scanCopyList .canningCopy").prepend($str);
            }
        };
        $.eWeixinJSUtil.chooseImg($callback);
    });

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

//删除材料
function delImg(){
    $(document).on("click",".canningCopy>div i",function(){
        var $this=$(this);
        $("body").toast({
            msg:"是否确认删除该图片",
            type:5,
            callMode:"func",
            end:function(){
                var accessoryId=$this.parent().attr("accessoryId");
                $.ajax({
                    url:"common/deleteAccessory.action",
                    data:{
                        accessoryId:accessoryId
                    },
                    type:"post",
                    dataType:"json",
                    beforeSend:function(){
                        $("body").loading({
                            msg:"删除中...",
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
                            var $JphotoNum = $this.parent().siblings(".JphotoNum");
                            var photoNum = parseInt($JphotoNum.html())-1;
                            $JphotoNum.html(photoNum);
                            $this.parent().remove();
                            $("body").zoom({
                                parentName : ".canningCopy",
                                boxName:".canningCopyImg",
                                tagName : "a"
                            });
                        }else {
                            alert("删除失败");
                        }
                    },
                    error:function(){
                        alert("删除失败");
                    }
                });
            }
        });

    });
}