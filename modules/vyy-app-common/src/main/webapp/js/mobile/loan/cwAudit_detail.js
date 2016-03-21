
var operatingUrl="mobile/loan/loanOperate.action",//通过url
    successUrl="mobile/loan/loanDetailView.action",//拒绝成功/确认报销 之后的跳转链接
    flagId="",
    titleText="";

$(function(){
    $("#bankAccount").text($.trim($("#bankAccount").text()).replace(/(\d{4})(?=\d)/g,"$1 "));
    //点击看大图
    lookBigImg();
    //确认借款
    confirmLoan();
    //拒绝借款
    rejectLoan();
    //确认执行
    confirmingExecution();
    //拒绝执行
    //refuseExecution();
    //加签
    plusSign();
    //选择后续审核人
    followAudit();
    //pc预览图片
    $("body").pcPreviewImg({
        parentName : ".canningCopy",
        boxName:".canningCopyImg",
        tagName : "a"
    });
});
//确认借款
function confirmLoan(){
    $("#sureBtn").click(function(){
        explanation($(this),"说明",true);
    });
}
//拒绝借款
function rejectLoan(){
    $("#returnBtn").click(function(){
        explanation($(this),"拒绝原因");
    });
}
//确认执行
function confirmingExecution(){
    $("#executionBtn").click(function(){
        var $this=$(this);
        //判断是对私页面还是对公页面
        var _len=$("#followAudit .addedPeopleList>div").length;
        //modify by tf case by 对私借款添加 沈可
        if( $("#loanType").val() == "0" && _len > 2){
            $("body").toast({
                msg:"请选择后续审核人",
                type:2,
                callMode:"func"
            });
        }else{
            $.ajax({
                url:operatingUrl,
                type:"post",
                data:{
                    loanInfo:JSON.stringify(getData("",$this))
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
                        window.location.replace(successUrl+"?flag=1&id="+$("#loanId").val());
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

//拒绝执行
/*function refuseExecution(){
 $("#unExecutionBtn").click(function(){
 explanation($(this),"拒绝原因");
 });
 }*/
//数据提交
function explanation(_this,_text,_autoFill){
    $("body").toast({
        msg:_text,
        type:4,
        autoFill:_autoFill,
        callMode: "func",
        dataChange:function($returnData){//$returnData退回原因
            $.ajax({
                url:operatingUrl,
                type:"post",
                data:{
                    loanInfo:JSON.stringify(getData($returnData,_this))
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
                        if($("#loanType").val() == "0" && _this.attr("id") == "sureBtn"){
                            window.location.reload();
                        }else{
                            window.location.replace(successUrl+"?flag=1&id="+$("#loanId").val());
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
    });
}
//data
function getData(_returnData,_dom){
    var _num=_dom.attr("data-type");
    var data = {
        id:$("#loanId").val(),
        loanType : $("#loanType").val(),
        remark:_returnData,
        eaId: _dom.attr("entityAccountId"),
        commandInfo:{
            commandType:_num
        }
    };
    if(_returnData != ""){
        data.remark=_returnData;
    }
    return data;
}
//加签
function plusSign(){
    var membersListWarp = $(".membersListWarp").eSelectPeople({
        btClose : function(_self) {
            $(".warp").show();
            $("body").css("overflow-y","auto");
            _self.hide();
        },
        btCance : function(_self) {
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
                var flag = false,
                    addFlag=true;
                var type = ("false" == _self.attr("_isMultipleChoice"))?'0':'1';
                for(var i = 0,size = data.length;i<size;i++){
                    uId = data[i].uId;
                    if(addFlag){
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
                        $(flagId + " .addedPeopleList").find(".addPeople").before(div);
                    }

                };
            }
            $(".warp").show();
            _self.hide();
            //向后抬发送加签的消息
            if(addFlag){
                sendMessage();
            }
        }
    });
    $("#plusSignBtn").on("click",function(){
        flagId =  "#" + $(this).parents(".selectPeople").attr("id");
        titleText = $(flagId).find(".selectPeopleTit").text().replaceAll(" ","").replaceAll(":","").replaceAll("：","").split("(")[0];
        $(".warp").hide();
        $("body").css("overflow","hidden");
        membersListWarp.eshow({_flag:0,_isMultipleChoice:false});
    });
}
//向后台发送加签的消息
function sendMessage(){
    $.ajax({
        url:operatingUrl,
        type:"post",
        data:{
            loanInfo:JSON.stringify(plusSignData())
        },
        dataType:"json",
        beforeSend:function(){
            $("body").loading({
                move:"show"
            });
        },
        complete:function(){
            //加签发送数据的过程中不可以点击按钮
            inoperable();
        },
        success:function(resData, textStatus){
            $("body").loading({
                move:"hide"
            });
            if (0 == resData.status) {
                //成功
                window.location.reload();
            } else {
                $("body").toast({
                    msg:resData.errorMsg,
                    type:2,
                    callMode:"func"
                })
                $("#plusSignBtn").prev().remove();
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
            $("#plusSignBtn").prev().remove();
        }
    });
}
//加签data
function plusSignData(){
    var entityUserInfo = [];
    var _lastEle=$("#selectAuditorList .addedPeopleList[type='0'] > div[uId]:last");
    entityUserInfo.push({
        "uId" : _lastEle.attr("uId"),
        "uAvatar" : _lastEle.children("img").attr("src"),
        "uType" : 2,
        "personType" : '0',
        "uName" : _lastEle.attr("uName"),
        "entityType" : "6"
    });
    var data = {
        id:$("#loanId").val(),
        loanType : $("#loanType").val(),
        entityUserInfo:entityUserInfo,
        commandInfo:{
            commandType:"7"
        }
    };
    return data;
}
//选择后续审核人
function followAudit(){
    $("#followAudit .addedPeopleList img").click(function(){
        var $this=$(this);
        $("body").toast({
            msg:"确认添加"+$this.parent().attr("uName")+"审核借款流程？",
            type:5,
            callMode:"func",
            start:function(){
                $this.prop("checked",false);
            },
            end:function(){
                $.ajax({
                    url:operatingUrl,
                    data:{
                        loanInfo : JSON.stringify(followAuditData($this.parent()))
                    },
                    type:"post",
                    dataType:"json",
                    beforeSend:function(){
                        $("body").loading({
                            move:"show"
                        });
                    },
                    complete:function(){
                        //加签发送数据的过程中不可以点击按钮
                        inoperable();
                    },
                    success:function(resData){
                        $("body").loading({
                            move:"hide"
                        });
                        if(0 == resData.status){
                            window.location.reload();

                        }else {
                            $this.prop("checked",false);
                            $("body").toast({
                                msg:resData.errorMsg,
                                type:2,
                                callMode:"func"
                            })
                        }
                    },
                    error:function(XMLHttpRequest, textStatus, errorThrown){
                        $this.prop("checked",false);
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
    });

}
//后续审核人data
function followAuditData(_dom){
    var entityUserInfo = [{
        "uId" : _dom.attr("uId"),
        "uAvatar" : _dom.attr("uAvatar"),
        "uType" : 2,
        "personType" : '0',
        "uName" : _dom.attr("uName"),
        "entityType" : "6"
    }];
    var data = {
        id:$("#loanId").val(),
        loanType : $("#loanType").val(),
        entityUserInfo:entityUserInfo,
        commandInfo:{
            commandType:"7"
        }
    };
    return data;
}
//加签发送数据的过程中不可以点击按钮
function inoperable(){
    $("#sureBtn,#returnBtn,#executionBtn,#unExecutionBtn,#followAudit input,#plusSignBtn").click(function(){
        $("body").toast({
            msg:"数据加载中，请稍后再操作",
            type:2,
            callMode:"func"
        });
        return false;
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
