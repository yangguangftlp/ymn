/**
 * Created by liyuxia on 2015/8/20.
 */
$(function(){
    //申请审核
    $("#audit").click(function(){
        //将扫描件数和财务报销负责人数储存在input中
        $("#selectAuditor").attr({"data-con":"*","data-empty":"申请审核时，审核人不能为空"});
        assignment();
        $(".warp>form").verification({
            callback:function(){
                var $data=getData();
                $data.operationType="0";
                //表单验证
                var resp={
                    chaining:$("#go-url").val(),//跳转链接
                    dataUrl:$("#audit").attr("data-url"),
                    data:{
                        expenseApplyInfo:JSON.stringify($data)
                    }
                };
                console.log(resp);
                mySubmit(resp);
            }
        });
    });

    //直接申请报销
    $("#repExp").click(function(){
        $("#selectAuditor").removeAttr("data-con");
        $("#selectAuditor").removeAttr("data-empty");
        //将扫描件数和财务报销负责人数储存在input中
        assignment();
        //相关材料

        $(".warp>form").verification({
            callback:function(){
                if($("#selectAuditor").val()!="")
                {
                    $("body").toast({
                        msg:"直接申请报销无需添加审核人，确认继续吗",
                        type:5,
                        callMode:"func",
                        end:function(){
                            var $data=getData();
                            $data.operationType="1";
                            console.log($data);
                            //表单验证
                            var resp={
                                chaining:$("#go-url").val(),//跳转链接
                                dataUrl:$("#repExp").attr("data-url"),
                                data:{
                                    expenseApplyInfo:JSON.stringify($data)
                                }
                            };
                            console.log(resp);
                            mySubmit(resp);
                        }
                    });
                }else{
                    var $data=getData();
                    $data.operationType="1";
                    //表单验证
                    var resp={
                        chaining:$("#go-url").val(),//跳转链接
                        dataUrl:$("#repExp").attr("data-url"),
                        data:{
                            expenseApplyInfo:JSON.stringify($data)
                        }
                    };
                    console.log(resp);
                    mySubmit(resp);
                }
            }
        });
    });

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
                        $parent.find("div[uid]").remove();
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
    $("#addAuditorBtn").click(function(){
        $("body").css("overflow","hidden");
        $(".warp").hide();
        membersListWarp.eshow({flag:"0",_isMultipleChoice:false});
    });
    $("#addPrincipal").on("click",function(){
        $("body").css("overflow","hidden");
        $(".warp").hide();
        membersListWarp.eshow({flag:"1",_isMultipleChoice:false});
    });

    //上传资料
    uploadMat();
    //查看大图
    lookBigImg();
  //客户端上传图片
    $("body").clientUploadImg({
    		uploadUrl:"mobile/common/uploadAccessory.action",
            boxName :'#addScanCopy',
            maxImgLen :9,
            imgType:"jpg|png|JPG|PNG"
	});
  //pc预览图片
    var expenseId = $("#expenseId").val();
    if(expenseId!=""){
    	$("body").pcPreviewImg({
            parentName : ".canningCopy",
            boxName:".canningCopyImg",
            tagName : "a"
    	});
    }
    //删除材料
    delImg();
    //删除审核人
    delReviewer();
    //添加费用
    addReimbursement();
    //保存费用
    saveReimbursement();
    //取消费用
    cancelReimbursement();
    //删除费用
    delReimbursement();
});
function assignment(){
    //审核人
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
    $(".canningCopy > div[accessoryId]").each(function(){
        accessoryId.push($(this).attr("accessoryId"));
    });
    var entityUserInfo = [];
    /*$(".addedPeopleList[type='0'] > div[uId]").each(function() {
     entityUserInfo.push({
     "uId" : $(this).attr("uId"),
     "uAvatar" : $(this).attr("uAvatar"),
     "uType" : 2,
     "personType" : '0',
     "uName" : $(this).attr("uName"),
     "entityType" : "2"
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
            "entityType" : "2"
        });
    });

    $(".addedPeopleList[type='1'] > div[uId]").each(function() {
        entityUserInfo.push({
            "uId" : $(this).attr("uId"),
            "uAvatar" : $(this).attr("uAvatar"),
            "uType" : 2,
            "personType" : '2',
            "uName" : $(this).attr("uName"),
            "entityType" : "2"
        });
    });
    //费用类别
    var expensefee = [];
    $(".stepwiseListModal .stepwiseList").each(function(index){
        if(!!!$(this).attr("data-id")){
            expensefee.push({
                "category" : $(this).attr("data-type"),//类型
                "money" : $(this).children("div").find("span").text()//金额
            });
        }
    });
    var data = {
        theme:$("#repExpTheme").val(),
        department:$("#department").val(),
        reason:$("#reasons").val(),
        amount:$("#money").val(),
        annexCount:$("#accessoryNum").val(),
        entityUserInfo:entityUserInfo,
        expenseId:$("#expenseId").val(),
        expensefeeInfo:expensefee,
        accessoryInfo:{mediaIds:accessoryId}
    };
    //如果存在（entityId）元素id
    if($("#expenseId").length>0){
        data.id = $("#expenseId").val();
    }
    return data;
}

function mySubmit(ops){
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
            if (!!resData && "0" == resData.status) {
                //成功,提示
                $("body").loading({
                    move:"hide"
                });
                window.location.replace(ops.chaining+"?id="+resData.value);

            } else {
                alert("申请失败!");
            }
        },
        error:function(XMLHttpRequest, textStatus, errorThrown) {
            alert("申请失败!");
        }
    });
}

function uploadMat(){
    //上传图片
    $("#addScanCopy>img").click(function(){
        var $callback=function(data){
            for(var i=0;i<data.value.length;i++){
                var $str='<div class="canningCopyImg" accessoryId='+data.value[i].accessoryId+'>'+
                    '<a href="javascript:void(0)" data-original='+data.value[i].original+'><img src='+data.value[i].thumb+' data-original='+data.value[i].original+'>'+
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
//添加费用
function addReimbursement(){
    $("#addQuestion>span,#addQuestion>b").click(function(){
        $("#reimbursement").addClass("boxModel").show();
        //判断是否已添加费用类别
        $("#reimbursementForm select option").removeClass("myHide");
        var feeListLen=$(".stepwiseListModal .stepwiseList").length;
        if(feeListLen>0){
            $(".stepwiseListModal .stepwiseList").each(function(index){
                var dataType=parseInt($(this).attr("data-type"));
                $("#reimbursementForm select option").each(function(){
                    if(parseInt($(this).val()) == dataType){
                        $(this).addClass("myHide");
                    }
                });
            });
        }
    });
}
//保存费用
function saveReimbursement(){
    $("#saveBtn").click(function(){
        $("#reimbursementForm").verification({
            callback: function(){
                var $selectVal=$("#reimbursement select").val();
                var $feeType=$("#reimbursement select option:selected").text();
                var $money=parseFloat($("#feeAmount").val()).toFixed(2);

                /*报销总额叠加*/
                var $nowMoney;
                if(!!!$("#money").val()){
                    $nowMoney=0;
                }else{
                    $nowMoney=parseFloat($("#money").val()).toFixed(2);
                }
                $("#money").val(parseFloat(Number($nowMoney)+Number($money)).toFixed(2))

                $("#reimbursement input").val("");
                $("#reimbursement select").val("-1");
                $("#reimbursement").hide();

                //插入数据
                /*var $html='<div class="feeList" data-type='+$selectVal+'>' +
                 '<div>'+$feeType+'  <span>'+$money+'</span>元</div>' +
                 '<i class="fa fa-minus-square-o"></i>' +
                 '</div>';*/
                var $html=['<div class="stepwiseList" data-type='+$selectVal+'>' +
                    //<div>交通费用<span>165.00</span></div>
                '<div>'+$feeType+'  <span>'+$money+'</span>元</div>' +
                '<b><i class="fa fa-minus-square-o listDelBtn"></i>'+
                '</b>'+
                '</div>'];
                $(".stepwiseListModal").append($html.join(""));
                //$(".reimModal").append($html.join(""));
            }
        })
    });
}
//取消费用
function cancelReimbursement(){
    $("#cancelBtn").click(function(){
        $("#reimbursement").hide();
    });
}
//删除费用
function delReimbursement(){
    $(document).on(touchEv,".stepwiseList b",function(){
        var $this=$(this);
        $prev=$this.prev();
        $("body").toast({
            msg:"是否确认删除该条记录",
            type:5,
            callMode:"func",
            end:function(){
                if(!!!$this.parent().attr("data-id")){
                    /*报销总额递减*/
                    var $money=parseFloat($("#money").val()).toFixed(2);
                    var $delMoney=parseFloat($prev.children("span").text()).toFixed(2);
                    $("#money").val(parseFloat(Number($money)-Number($delMoney)).toFixed(2));
                    if($("#money").val() == 0){
                        $("#money").val("")
                    }
                    //删除数据
                    $prev.parent().remove();
                }else{
                    $.ajax({
                        url:"mobile/expense/deleteExpenseFee.action",
                        data:{
                            id:$this.parent().attr("data-id")
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
                                /*报销总额递减*/
                                var $money=parseFloat($("#money").val()).toFixed(2);
                                var $delMoney=parseFloat($prev.children("span").text()).toFixed(2);
                                $("#money").val(parseFloat(Number($money)-Number($delMoney)).toFixed(2));
                                if($("#money").val() == 0){
                                    $("#money").val("")
                                }
                                //删除数据
                                $prev.parent().remove();
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
                                msg:"系统异常，请稍后再试",
                                type:2,
                                callMode:"func"
                            });
                        }
                    });
                }
            }
        });


    });
}