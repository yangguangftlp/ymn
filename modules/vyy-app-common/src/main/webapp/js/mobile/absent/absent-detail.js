/**
 * Created by liyuxia on 2015/8/11.
 */
$(function(){	
    var promptModal=$(".promptModal");//模态框
    //获得浏览器的高度
    var windowH=window.innerHeight;
    var windowW=window.innerWidth;
    //设置催办成功模态框的top、left
    promptModal.css({"top":(windowH/2-50),"left":(windowW/2-67)});

    //当列表的内容多于一行的时候，为父级元素加的样式
    multiLine();
    //遍历每一个按钮组 显示的是几个按钮
    judgeBtnStyle();

    //催办
    $("#urgeBtn").click(function(){
    	//获取待审核人信息
    	$.ajax({
			url : 'mobile/absent/doAbsent.action',
			type : 'post',
			data : {
				absentInfo:JSON.stringify({
					"uId" : $("#awaitUser").attr("uId"),
					"absentId":$("#awaitUser").attr("absentId"),
					"absentTypeName":$("#awaitUser").attr("absentTypeName"),
					"commandInfo":{
						"commandType":"remind"
					}
				 })
			},
			beforeSend : function() {
				//$("#onLoading").show();
				$("body").loading({
                    move:"show"
                });
			},
			success : function(resData, textStatus) {
				$("body").loading({
                    move:"hide"
                });
				if (!!resData && "0" == resData.status) {
				    //successPrompt("催办成功");
				    $("body").toast({
                        msg:"催办成功",
                        type:1,
                        callMode:"func"
                    })
                    
				} else {
					$("body").toast({
                        msg:resData.errorMsg,
                        type:2,
                        callMode:"func"
                    })
					//successPrompt(resData.errorMsg);
				};
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				//successPrompt("申请失败!"+errorThrown);
				$("body").loading({
                    move:"hide"
                });
				$("body").toast({
                    msg:"系统异常，请稍后再试",
                    type:2,
                    callMode:"func"
                })
			}
    	});
    	//成功之后
    });
    
    
  //撤回
    $("#withdrawBtn").click(function(){
    	//获取待审核人信息
    	$.ajax({
			url : 'mobile/absent/doAbsent.action',
			type : 'post',
			data : {
				absentInfo:JSON.stringify({
					"absentId":$("#awaitUser").attr("absentId"),
					"commandInfo":{
						"commandType":"undo"
					}
				 })
			},
			beforeSend : function() {
				//$("#onLoading").show();
				/*$("body").loading({
                    move:"show"
                });*/
			},
			complete : function() {
				//$("#onLoading").hide();
			},
			success : function(resData, textStatus) {
				/*$("body").loading({
                    move:"hide"
                });*/
				if (!!resData && "0" == resData.status) {
				    //successPrompt("撤回成功");
					$("body").toast({
	                    msg:"撤回成功",
	                    type:1,
	                    callMode:"func",
	                    end : function(){
	                    	var _url = window.location.href.split("#")[1];
	                    	if(_url == "flag=0"){
	                    		//window.location.replace("absent/absentRecordView.action");
	                    		window.history.go(-1);
	                    	}else{
	                    		WeixinJSBridge.call('closeWindow');
	                    	}                   	
	                    }
	                });
				    
				} else {
					$("body").toast({
	                    msg:resData.errorMsg,
	                    type:2,
	                    callMode:"func"
	                });
					//successPrompt(resData.errorMsg);
				};
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				//successPrompt("撤回失败!"+errorThrown);
				$("body").toast({
                    msg:"系统异常，请稍后再试",
                    type:2,
                    callMode:"func"
                });
			}
    	});
    	//成功之后
    });
    
    
});
//催办成功
function successPrompt(str,callback){
    var $screenModal=$('.screenModal');
    $(".promptModal").text(str);
    $screenModal.removeClass("myHide");
    setTimeout(function(){
        $screenModal.stop().animate({
            opacity:0
        },300,function(){
            $screenModal.addClass("myHide").css("opacity",1);
            if (typeof (callback) == "function") {
            	callback();
            }
        });
    },300);
}
//当列表的内容多于一行的时候，为父级元素加的样式
function multiLine(){
    $(".formList").each(function(index){
        if($(".formList").eq(index).find("span").outerHeight()>36){
            $(".formList").eq(index).addClass("formListHeightLg");
        };
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