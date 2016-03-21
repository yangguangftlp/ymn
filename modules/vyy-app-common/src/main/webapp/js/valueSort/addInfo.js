window.Meeting_Info = {
    // 配置
    "pageSize": 10
};
window.Current_PageIndex=2;
var isSlideBottom=false,
	scrollPage= -1,
    $flagMark,
    $url='mobile/systemStatus/configOperate.action',
    windowInH=window.innerHeight;
$(function(){
    $flagMark=$("#flagMark").val(),
        //删除记录
        delRecord();
    //添加纪录
    addInfo();
    //取消添加
    cancelAdd();
    //确认添加
    confirmAdd();
    //编辑信息
    editInfo();
    //判断是否滚动到底部，加载更多数据
    judgeScroll();
    //去掉项目财务和项目营销支持的添加和删除按钮
    /*isFixedPosition();*/
});
//删除记录
function delRecord(){
    $(".editValueSort").on("click",".delDiv",function(){
        var $this=$(this);
        $("body").toast({
            msg:"是否确认删除该条记录",
            type:5,
            callMode:"func",
            end:function(){
                $.ajax({
                    url:$url,
                    data:{
                        configInfo : JSON.stringify(delInfo($this.prev()))
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
                    success:function(resData){
                        if(0 == resData.status){
                            $("body").loading({
                                move:"hide"
                            });
                            $this.parent().remove();
                            window.location.reload();
                        }else {
                            $("body").toast({
                                msg:resData.errorMsg,
                                type:2,
                                callMode:"func"
                            });
                        }
                    },
                    error:function(a,b,c){
                        $("body").toast({
                            msg:b,
                            type:2,
                            callMode:"func"
                        });
                    }
                });
            }
        });
    });
}
//添加纪录
function addInfo(){
    $("#addInfoBtn").click(function(){
        //显示模态框
        $("#marketInfoForm").attr("commandType",1);
        $(".addMarketInfoModal").addClass("boxModel").show();
        scrollBan($(".warp"));
        $("body").css("overflow","auto");
    });
}
//取消添加
function cancelAdd(){
    $(".cancelBtn").click(function(){
        $(".addModal").hide();
        unblock($(".warp"));
    });
}
//确认添加
function confirmAdd(){
    $(".saveBtn").click(function(){
        var $this=$(this);
        //判断提交那个信息
        var myData;
        ($flagMark == "ProgramMarket" || $flagMark == "ProgramOperation" || $flagMark == "ProagramCW" || $flagMark == "ProagramYX" || $flagMark == "ProagramSZ" || $flagMark == "ProagramFK" ) ? myData = getMarketData() : myData = getOtherData();
        if(!!$("#marketInfoForm").attr("data-id")){
            myData.id = $("#marketInfoForm").attr("data-id");
            //modify by tf 2015-10-22
            myData.commandInfo.commandType = $("#marketInfoForm").attr("commandType");
            //$("#marketInfoForm").attr("data-id","");
        }
        if(0 == parseInt(myData.px)){
            $("body").toast({
                msg:"排序不可以为0",
                type:2,
                callMode:"func"
            });
        }else{
            $this.parents("form").verification({
                callback : function(){
                    $.ajax({
                        url:$url,
                        data:{
                            configInfo : JSON.stringify(myData)
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
                        success:function(resData){
                            $("body").loading({
                                move:"hide"
                            });
                            if(0 == resData.status){
                                window.location.reload();

                            }else {
                                $("body").toast({
                                    msg:resData.errorMsg,
                                    type:2,
                                    callMode:"func"
                                });
                            }
                            $("#marketInfoForm input").val("");
                            $(".addModal").hide();
                            unblock($(".warp"));
                        },
                        error:function(a,b,c){
                            $("body").toast({
                                msg:b,
                                type:2,
                                callMode:"func"
                            });
                        }
                    });
                }
            });
        }

    });
}
//编辑信息
function editInfo(){
    $(document).on("click",".nameDiv",function(){
        var $this=$(this);
        $(".addMarketInfoModal").addClass("boxModel").show();
        scrollBan($(".warp"));
        //回填值
        $("#marketInfoForm").attr("data-id",$this.attr("id"));
        $("#marketInfoForm").attr("commandType",2);
        var $text=$this.text().split("(");
        var $name= $.trim($text[0]);
        $("#sequence").val($this.attr("px"));//序号
        if($flagMark == "ProgramMarket" || $flagMark == "ProgramOperation" || $flagMark == "ProagramCW" || $flagMark == "ProagramYX" || $flagMark == "ProagramSZ" || $flagMark == "ProagramFK"){
            $("#employeeName").val($name);//姓名
            $("#jobNum").val($this.attr("status"));//工号

        }else{
            $("#appellation").val($name);//名称
        }

    });
}
//维护营销负责人
function getMarketData(){
    var data={
        status : $.trim($("#jobNum").val()),//工号
        name : $.trim($("#employeeName").val()),//姓名
        px : $.trim($("#sequence").val()),//排序
        type : $flagMark,
        commandInfo:{
            commandType : 1
        }
    };
    return data;
}
//其他信息
function getOtherData(){
    var data={
        name : $.trim($("#appellation").val()),//名称
        px : $.trim($("#sequence").val()),//排序
        type : $flagMark,
        commandInfo:{
            commandType : 1
        }
    };
    return data;
}
//删除信息
function delInfo(_this){
    var $text=_this.text().split("(");
    var data={
        name : $.trim($text[0]),//名称，姓名
        px : _this.attr("px"),//排序
        type : $flagMark,
        id : _this.attr("id"),
        commandInfo:{
            commandType : 3
        }
    };
    if($flagMark == "ProgramMarket" || $flagMark == "ProgramOperation"){
        data.status=_this.attr("status");//工号
    }
    return data;
}
//判断是否滚动到底部，加载更多数据
function judgeScroll(){
    $(document).scroll(function(){
        var resizeWindowH=window.innerHeight;
        if($(window).scrollTop() + $(window).height() +82 >= $(document).height() && resizeWindowH == windowInH){
            if(!isSlideBottom){
                var pageIndex = window.Current_PageIndex;
                if(scrollPage != pageIndex){
                    scrollPage = pageIndex;
                    $.ajax({
                        url:$url,
                        data: {
                            pageIndex: pageIndex,
                            pageSize: window.Meeting_Info.pageSize
                        },//要发送的数据
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
                        success:function(resData){
                            $("body").loading({
                                move:"hide"
                            });
                            addRecord(resData);
                        },
                        error:function(){
                            $("body").toast({
                                msg:"获取记录出错，请稍后再试",
                                type:2,
                                callMode:"func"
                            });
                        }
                    });
                }
                
            }
        }
    });
}
//增加记录
function addRecord(resData){
    var data = resData.value;
    var auditList = $(".warp>div").eq(0);
    if (!!data) {
        var html = "";
        var size = data.length;
        if (size > 0) {
            for (var i = 0; i < size; i++) {
                html='<div class="editValueSort">' +
                    '<div id='+resData.value[i].id+' type='+resData.value[i].type+' status='+resData.value[i].status+' px='+resData.value[i].px+' class="nameDiv">' +
                    resData.value[i].name +
                    '</div>' +
                    '<div class="delDiv">' +
                    '<b class="fa fa-trash-o"></b>' +
                    '</div>' +
                    '</div>'
                auditList.append(html);
            }
            // 增加页码
            window.Current_PageIndex++;
        } else {
            $("body").toast({
                msg:"没有更多记录...",
                type:1,
                callMode:"func"
            });
            isSlideBottom=true;
        }
    } else {
        $("body").toast({
            msg:"没有更多记录...",
            type:1,
            callMode:"func"
        });
        isSlideBottom=true;
    }

}
//是否是固定职位，不可删除
function isFixedPosition(){
    var _flagMark = $("#flagMark").val();
    if("ProagramCW" == _flagMark || "ProagramYX" == _flagMark){
        $(".btnGroup.addBt").remove();
        if(!!$(".delDiv")){
            $(".delDiv").remove();
        }
        $(".btnGroup.addBt").remove();
    }
}
//禁止滚动
function scrollBan(_ele){
    var windowH=window.innerHeight;
    _ele.css("height",windowH+"px");
}
//取消
function unblock(_ele){
    _ele.css("height","auto");
}

