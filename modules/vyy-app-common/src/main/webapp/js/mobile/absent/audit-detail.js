/**
 * Created by liyuxia on 2015/8/11.
 */
$(function() {
	// 获得浏览器的高度
	var windowH = window.innerHeight;
	var windowW = window.innerWidth;
	// 当列表的内容多于一行的时候，为父级元素加的样式
	multiLine();
	// 成功通过
	$("#passBtn").click(function() {
		var $this=$(this);
		$("body").toast({
			msg:"通过说明",
			type:4,
			callMode: "func",
			autoFill: true,
			dataChange:function($returnData){//$returnData退回原因
				// 获取待审核人信息
				var data = {
					"absentId" : $this.attr("absentId"),
					"entityId" : $this.attr("entityId"),
					"remark" : $returnData,
					"commandInfo" : {
						"commandType" : "general"
					}
				};
				$.ajax({
					url : 'mobile/absent/doAbsent.action',
					type : 'post',
					data : {
						absentInfo : JSON.stringify(data)
					},
					beforeSend : function() {
						$("body").loading({
							move:"show"
						});
					},
					success : function(resData, textStatus) {
						$("body").loading({
							move:"hide"
						});
						if (!!resData && "0" == resData.status) {
							$("body").toast({
								msg:"成功通过",
								type:1,
								callMode:"func",
								end : function(){
									window.location.replace("mobile/absent/absentDetailView.action?flag=1&id="+ data.absentId+"#audit");
									
								}
							});

						} else {
							$("body").toast({
								msg:resData.errorMsg,
								type:2,
								callMode:"func"
							})
						}
						;
					},
					error : function(XMLHttpRequest,textStatus, errorThrown) {
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
			}
		});
	});

	// 退回请假申请
	returnLeave();
});
// 退回请假申请
function returnLeave() {
	// 点击退回弹出模态框

	$("#returnBtn").click(function() {
		// 显示模态框
		$("body").toast({
			msg:"退回原因",
			type:4,
			callMode: "func",
			dataChange:function($returnData){//$returnData退回原因
				var data = {
					"absentId" : $("#returnBtn").attr("absentId"),
					"entityId" : $("#returnBtn").attr("entityId"),
					"remark" : $returnData,
					"commandInfo" : {
						"commandType" : "rollBack"
					}
				};
				$.ajax({
					url : 'mobile/absent/doAbsent.action',
					type : 'post',
					data : {
						absentInfo : JSON.stringify(data)
					},
					beforeSend:function(){
						$("body").loading({
							move:"show"
						});
					},
					success : function(resData, textStatus) {
						if (!!resData && "0" == resData.status) {
							window.location.replace("mobile/absent/absentDetailView.action?flag=1&id="+ data.absentId+"#audit");
						} else {
							$("body").loading({
								move:"hide"
							});
							$("body").toast({
								msg:resData.errorMsg,
								type:2,
								callMode:"func"
							})
						}
						;
					},
					error : function(XMLHttpRequest,textStatus, errorThrown) {
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
			}
		});
	});
}

//当列表的内容多于一行的时候，为父级元素加的样式
function multiLine() {
	$(".formList").each(function(index) {
		if ($(".formList").eq(index).find("span").outerHeight() > 36) {
			$(".formList").eq(index).addClass("formListHeightLg");
		}
		;
	});
}