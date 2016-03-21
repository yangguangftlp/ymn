/**
 * Created by liyuxia on 2015/8/21.
 */
var eventProcessingUrl="mobile/approval/approvalAudit.action";
$(function(){
    //重新发起审批
    $("#detailAudit").click(function(){
        //页面跳转
        window.location.replace($(this).attr("data-url")+"&approvalType="+$(this).attr("approvalType"));
    });
    //催办
    $("#urgeBtn").click(function(){
        eventProcessing($(this),"remind","催办成功");
    });
    //撤回
    $("#withdrawBtn").click(function(){
        eventProcessing($(this),"undo","撤回成功");
    });
    //已办
    $("#hasHandledBtn").on(touchEv,function(){
        eventProcessing($(this),5,"成功标记为已办事项");
    });
    //点击看大图
    lookBigImg();
    //遍历每一个按钮组 显示的是几个按钮
    judgeBtnStyle();
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
//事件处理
function eventProcessing(_this,_type,_msg){
    var data={
        "uId" : traverse(),
        "approvalId":_this.attr("approvalId"),
        "commandInfo":{
            "commandType":_type
        }
    };
    $.ajax({
        url:eventProcessingUrl,
        data:{
            approvalAuditInfo:JSON.stringify(data)
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
                //如果是撤回执行以下的代码
                if(_type=="undo"){
                	//成功,提示
					toastFun({
						msg:_msg,
						type:1,
						callMode:"func",
						end: function(){
							if($("#flag").attr("data-flag") == "0"){
								//window.location.replace("approval/approvalRecordView.action");
								window.history.go(-1);
							}else{
								WeixinJSBridge.call('closeWindow');
							}
	                        
						}
					});
                }else{
					//成功,提示
					toastFun({
						msg:_msg,
						type:1,
						callMode:"func",
						end: function(){
							if("hasHandledBtn" == _this.attr("id")){
								window.location.reload();
							}
						}
					});
                }
            } else {
            	if(resData.status=="-1"){
            		toastFun({
	                    msg:resData.errorMsg,
	                    type:2,
	                    callMode:"func"
	                });
	           	}else{
	           		 toastFun({
                        msg:resData.errorMsg,
                        type:1,
                        callMode:"func"
                    });
	           	}
               
            }
        },
        error:function(XMLHttpRequest, textStatus, errorThrown) {
            toastFun({
                msg:"系统异常",
                type:1,
                callMode:"func"
            });
        }
    });
}
//遍历审核人
function traverse(){
    var $auditors=new Array();
    //遍历审核
    $(".addedPeopleList:eq(0)>div").each(function(index){
        if("0" == $(this).attr("status")){
            $auditors.push($(this).attr("uId"));
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
