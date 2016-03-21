$(function(){
    //发起审批
    var $isJump = false;
    $("em[name='level']").each(function(){
    	var index = $(this).attr("index");
    	$(this).html(digitalSwitchChart(index));
    });
    $("#approval").click(function(){
        $isJump = true;
        assignment();
        $("form").verification({
            callback:function(){
                var $data=getData();
                $data.operationType="0";
                $data.commandInfo={
                    commandType:"general"
                };
                //表单验证
                var resp={
                    chaining:$("#go-url").val(),//跳转链接
                    dataUrl:$("#approval").attr("data-url"),//url链接
                    data:{
                        launchApprovalInfo:JSON.stringify($data)
                    }
                };
                mySubmit(resp,$isJump);
            }
        });
    });
    //暂存
    $("#tempStorage").click(function(){
        $isJump = false,$obj = $("#flowName"),name = $obj.val();
        if($.trim(name)==""||$.trim(name)==null){
        	toastFun({
                msg:$obj.data("empty"),
                type:2,
                callMode:"func",
                end:function(){
                    $("html,body").animate({scrollTop: $obj.offset().top}, 500);
                    $obj.focus();
                }
            });
        	return;
        }
        var $tempStorageData=getData();
        $tempStorageData.commandInfo={
            commandType:"draft"
        };
        var resp={
            chaining:"",//跳转链接
            dataUrl:$("#tempStorage").attr("data-url"),//url链接
            data:{
                launchApprovalInfo:JSON.stringify($tempStorageData)
            }
        };
        mySubmit(resp,$isJump);
    });
    //删除审核人
    delReviewer();
    var membersListWarp = $(".membersListWarp").eSelectPeople({
    	btCance : function(_self) {
            $(".warp").show();
            $("body").css("overflow-y","auto");
            _self.hide();    
        },
        btClose : function(_self) {
        	$(".warp").show();
        	$("body").css("overflow-y","auto");
	        _self.hide();
        },
        btOk : function(_self,data) {
        	$("body").css("overflow-y","auto");
            if(!!data && data.length > 0 ){
                var div = null;
                var img = null;
                var span = null;
                var elemI = null;
                var uId = null;
                //判断是否已存在选择的用户 这里需要去重
                var type = _self.attr("flag");
                for(var i = 0,size = data.length;i<size;i++){
                    uId = data[i].uId;
                    if("0" == type){
                        var _num=parseInt($(".addedStaffList>section").length)+1;
                        if(judgeRepeat(uId,_num)){

                            var _html='<section class="staffList">' +
                                '<span>第<em>'+digitalSwitchChart(_num.toString())+'</em>级审核人：</span>' +
                                '<div uId='+uId+' uAvatar='+data[i].uAvatar+' uName='+data[i].uName+'>' +
                                '<img src='+data[i].uAvatar+'>' +
                                '<span>' +
                                data[i].uName +
                                '</span>' +
                                '</div>' +
                                '<b class="delStaffList"><i class="fa fa-minus-square-o"></i></b>' +
                                '</section>';
                            $(".addedStaffList").append(_html);
                        }
                    }
                    else {
                    	var $parent = $(".addedPeopleList[type='"+type+"']");
                    	var $obj = $parent.find("[uid="+uId+"]");
                        if($obj.length>0){
                        	continue;
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
                        $parent.find(".addPeople").before(div);
                    }
                };
            }
            $(".warp").show();
            _self.hide();
        }
    });

    /*    $("#addAuditor").on("click",function(){
     $(".warp").hide();
     membersListWarp.eshow({flag:false});
     });*/
    $("#addPrincipal").on("click",function(){
        $(".warp").hide();
        $("body").css("overflow","hidden");
        membersListWarp.eshow({flag:"1",_isMultipleChoice:true});
    });
    $("#addAuditorBtn").click(function(){
    	$("body").css("overflow","hidden");
        $(".warp").hide();
        membersListWarp.eshow({flag:"0",_isMultipleChoice:false});
    });

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
    //pc预览图片
    var approvalId = $("#approvalId").val();
    if(approvalId!=""){
    	$("body").pcPreviewImg({
            parentName : ".canningCopy",
            boxName:".canningCopyImg",
            tagName : "a"
    	});
    }
    //暂存成功后的函数

    //删除审核人或抄送人
    $(".addedPeopleList>div>i").click(function(){
        var $this=$(this);
        var $parent=$this.parent();
        if("1" == $parent.attr("type")){
            var peopleData={
                id:$parent.attr("id")
            };
            var myUrl="mobile/common/deleteEntityAccount.action";
            myDelete($this,myUrl,peopleData);
        }
    });
});

function assignment(){
    var $canningCopyL=$("#scanCopyList .canningCopy>div[accessoryId]").length;
    if($canningCopyL!=0){
        $("#scanCopy").val($canningCopyL);
    }else{
        $("#scanCopy").val("");
    }

    var $AuditorListL=$("#selectAuditorList .staffList>div[uId]").length;
    if($AuditorListL!=0){
        $("#selectAuditor").val($AuditorListL);
    }else{
        $("#selectAuditor").val("");
    }

    var $principalL=$("#principalList .addedPeopleList>div[uId]").length;
    if($principalL!=0){
        $("#principal").val($principalL);
    }else{
        $("#principal").val("");
    }
}

function getData(){
    var accessoryId=[];
    $(".canningCopy>div[accessoryId]").each(function(){
        accessoryId.push($(this).attr("accessoryId"));
    });
    var entityUserInfo = [];
    $(".addedStaffList[type='0'] .staffList").each(function() {
        var _div=$(this).children("div");
        entityUserInfo.push({
            "uId" : _div.attr("uId"),
            "uAvatar" : _div.attr("uAvatar"),
            "uType" : 2,
            "personType" : '0',
            "uName" : _div.attr("uName"),
            "entityType" : "3"
        });
    });
    $(".addedPeopleList[type='1'] > div[uId]").each(function() {
        entityUserInfo.push({
            "uId" : $(this).attr("uId"),
            "uAvatar" : $(this).attr("uAvatar"),
            "uType" : 2,
            "personType" : '1',
            "uName" : $(this).attr("uName"),
            "entityType" : "3"
        });
    });
    var data = {
        flowName:$("#flowName").val(),
        department:$("#attrDepartment").val(),
        content:$("#content").val(),
        remark:$("#remark").val(),
        entityUserInfo:entityUserInfo,
        accessoryInfo:{mediaIds:accessoryId}
    };
    //流程类型
    data.approvalType=$("#approvalType").val();
    //如果存在（approvalId）元素id
    if($("#approvalId").length>0){
        data.approvalId = $("#approvalId").val();
    }
    //合作编号
    if($("#contractNum").length>0){
        data.contractNumber = $("#contractNum").val();
    }
    //合作方
    if($("#partner").length>0){
        data.partner = $("#partner").val();
    }
    return data;
}

function mySubmit(ops,isJump){
    $.ajax({
        url:ops.dataUrl,
        data:ops.data,
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
            $("body").loading({
                move:"hide"
            });
            if (!!resData && "0" == resData.status) {
                //成功,提示
                if(isJump){
                	window.location.replace(ops.chaining+"?id="+resData.value+"&flag=1");
                }else{
                	$("body").toast({
                        msg:"暂存成功",
                        type:1,
                        time:1500,
                        callMode:"func",
                        end:function(){
                        	window.location.replace("mobile/approval/approvalView.action?id="+resData.value+"&approvalType="+$("#approvalType").val());
                        }
                    })
                   
                }
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
                    url:"mobile/common/deleteAccessory.action",
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
                        	$("body").toast({
                                msg:res.errorMsg,
                                type:2,
                                callMode:"func"
                            });
                        }
                    },
                    error:function(){
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
}

function myDelete(_this,_url,_data){
    $.ajax({
        url:_url,
        data:_data,
        type:"post",
        dataType:"json",
        beforeSend:function(){
            $("body").loading({
                msg:"删除中",
                move:"show"
            });
        },
        complete:function(){
            $("body").loading({
                move:"hide"
            });
        },
        success:function(resData, textStatus){
            $("body").loading({
                move:"hide"
            });
            if (!!resData && "0" == resData.status){
                _this.parent().remove();
            }else{
                alert("删除失败");
            }
        },
        error:function(){
            $("body").loading({
                move:"hide"
            });
            alert("删除失败");
        }

    });
}

//删除审核人
function delReviewer(){
    $(document).on(touchEv,".staffList>b",function(){
        var $this=$(this);
        $this.parent().remove();
        $("#selectAuditorList .staffList").each(function(index){
            var $this=$(this);
            $this.find("em").text(digitalSwitchChart((index+1).toString()));
        });
    });
}
//判断审核人是否重复
function judgeRepeat(_uId,_len){
	var $parent = $(".addedStaffList[type='0']");
	var $obj = $parent.find("[uid="+_uId+"]");
	if($obj.length>0){
		return false;
	}else{
		 return true;
	}
}

