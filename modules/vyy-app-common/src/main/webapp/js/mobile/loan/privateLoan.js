var subUrl="mobile/loan/lunchLoan.action";//提交申请的url
$(function(){

    //银行卡号4位为一组
    inputAccount();
    //金额转化
    convertCurrency();
    //上传资料
    uploadMat();
    //查看大图
    lookBigImg();
    //删除材料
    delImg();
    //提交申请
    subForm();
    //选择审核人
    selectReviewer();
    //删除审核人
    delReviewer();
    //客户端上传图片
    $("body").clientUploadImg({
            uploadUrl:"mobile/common/uploadAccessory.action",
            boxName :'#addScanCopy',
            maxImgLen :9,
            imgType:"jpg|png|JPG|PNG"
    });
  //pc预览图片
    var approvalId = $("#loanId").val();
    if(approvalId!=""){
        $("body").pcPreviewImg({
            parentName : ".canningCopy",
            boxName:".canningCopyImg",
            tagName : "a"
        });
    }
});

//金额转化
function convertCurrency() {
    $("#money").convertCurrency({
        targetSource : $("#getCapital")
    });
}
//银行卡号4位为一组
function inputAccount(){
    $("#accountNum").bankCardNumr();
}
//提交申请
function subForm(){
    $("#subBtn").click(function(){
        //计算相关文件和审核人的数目
        assignment();
        $("#loanForm").verification({
            callback:function(){
                $.ajax({
                    url: subUrl,
                    data:{
                        launchLoanInfo:JSON.stringify(getData())
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
                            window.location.replace("mobile/loan/loanDetailView.action?flag=1&id="+resData.value+"&loanType="+$("#loanType").val());
                        }else{
                            $("body").toast({
                                msg:resData.errorMsg,
                                type:2,
                                callMode:"func"
                            });
                        }
                    },
                    error:function(XMLHttpRequest, textStatus, errorThrown) {
                        $("body").toast({
                            msg:"提交失败，请稍后再试",
                            type:2,
                            callMode:"func"
                        });
                    }
                });
            }
        });
    });
}
//获取数据
function getData(){
    var accessoryId=[];
    $(".canningCopy>div[accessoryId]").each(function(){
        accessoryId.push($(this).attr("accessoryId"));
    });
    var entityUserInfo = [];
    /*    $(".addedPeopleList[type='0'] > div[uId]").each(function() {
     entityUserInfo.push({
     "uId" : $(this).attr("uId"),
     "uAvatar" : $(this).attr("uAvatar"),
     "uType" : 2,
     "personType" : '0',
     "uName" : $(this).attr("uName"),
     "entityType" : "6"
     });
     });*/
    $(".addedStaffList[type='0'] .staffList").each(function() {
        var _div=$(this).children("div");
        entityUserInfo.push({
            "uId" : _div.attr("uId"),
            "uAvatar" : _div.attr("uAvatar"),
            "uType" : 2,
            "personType" : '0',
            "uName" : _div.attr("uName"),
            "entityType" : "6"
        });
    });
    var data={
        loanType : $("#loanType").val(),//类型
        department : $.trim($("#department").val()),//部门
        userName : $.trim($("#borrower").val()),//借款人
        userId : $("#borrower").attr("data-id"),//借款人id
        applyDate : $.trim($("#loanDate").val()),//日期
        amount : $.trim($("#money").val()),//金额
        capitalAmount : $("#getCapital").text(),//大写金额
        subject : $.trim($("#missionArea").val().replaceAll("\r","<br/>")),//出差地
        details : $.trim($("#details").val().replaceAll("\r","<br/>")),//使用明细
        receiveAccount : $.trim($("#accountNum").val()).replace(/\s/g,''),//收款账号
        entityUserInfo:entityUserInfo,//审核人，
        accessoryInfo:{mediaIds:accessoryId},//材料
        commandInfo:{
            commandType : "general"
        }
    };
    if($("#loanId").val() != ""){
        data.id=$("#loanId").val();
    }
    return data;
}
String.prototype.replaceAll  = function(s1,s2){
    return this.replace(new RegExp(s1,"gm"),s2);
};
//选择审核人
function selectReviewer(){
    var membersListWarp = $(".membersListWarp").eSelectPeople({
        btClose : function(_self) {
            $("body").css("overflow-y","auto");
            $(".warp").show();
            _self.hide();
        },
        btCance : function(_self) {
            $("body").css("overflow-y","auto");
            $(".warp").show();
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
                };
            }
            $(".warp").show();
            _self.hide();
        }
    });
    $("#addAuditorBtn").click(function(){
        $(".warp").hide();
        $("body").css("overflow","hidden");
        membersListWarp.eshow({flag:"0",_isMultipleChoice:false});
    });
}
//计算相关文件和审核人的数目
function assignment(){
    var $AuditorListL=$("#selectAuditorList .staffList>div[uId]").length;
    if($AuditorListL!=0){
        $("#selectAuditor").val($AuditorListL);
    }else{
        $("#selectAuditor").val("");
    }
}
//上传资料
function uploadMat(){
    //上传图片
    $("#addScanCopy>img").click(function(){
        var $callback=function(data){
            for(var i=0;i<data.value.length;i++){
                var $str='<div accessoryId='+data.value[i].accessoryId+'>'+
                    '<img src='+data.value[i].thumb+' data-original='+data.value[i].original+'>'+
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
