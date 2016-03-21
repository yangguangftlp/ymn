$(function() {
	// 选择收件人
	selectRecipients();
	// 添加快递单号
	addTrackingNum();
	// 添加所属快递
	addTrackingBy();
	// 表单提交
	submitForm();
});
// 选择收件人
function selectRecipients() {
	var membersListWarp = $(".membersListWarp").eSelectPeople({
		btCance : function(_self) {
			$(".warp").show();
			_self.hide();
		},
		btClose : function(_self) {
			$(".warp").show();
			$("body").css("overflow-y", "auto");
			_self.hide();
		},
		btOk : function(_self, data) {
			if (!!data && data.length > 0) {
				for (var i = 0, size = data.length; i < size; i++) {
					$(".addRecipients>input").attr({
						"uId" : data[i].uId,
						"uAvatar" : data[i].uAvatar
					}).val(data[i].uName);
				}
			}
			$(".warp").show();
			_self.hide();
		}
	});
	$(".addRecipients>input,.addRecipients>b").on("click", function() {
		$(".warp").hide();
		membersListWarp.eshow({
			flag : false
		});
	});
}
// 扫描添加快递单号
function addTrackingNum() {
	$(".b01").on("click", function() {
		$.eWeixinJSUtil.scanQRCode(function(data) {
			var $res = data.resultStr;
			$res = $res.split(",");
			if ($res[1] == "" || $res[1] == undefined) {
				alert("扫码失败，请重试！");
			} else {
				$("#trackingNum").val($res[1]);
			}
		});
	});
}
// 添加所属快递
function addTrackingBy() {
	var trackArr = [ "EMS", "申通快递", "顺丰速运", "圆通速递", "韵达速递", "中通快递", "天天快递",
			"百世汇通", "宅急送", "全峰快递", "德邦", "包裹信件", "中铁物流", "AAE全球专递", "安能物流",
			"Aramex", "百福东方", "快捷快递", "民邦速递", "万象物流" ], lastArr = [];
	for (var i = 0; i < trackArr.length; i++) {
		lastArr.push('<option value="' + trackArr[i] + '" id="' + i + '">'
				+ trackArr[i] + '</option>');
	}
	$("#trackingBy").append(lastArr.join(""));
}
// 判断是否为空
function receivePeople() {
	if ($("#recipients").val() == "") {
		$("body").toast({
			msg : "收件人不能为空！",
			type : 2,
			callMode : "func"
		});
		return false;
	} else if ($("#trackingNum").val() == "") {
		$("body").toast({
			msg : "快递单号不能为空！",
			type : 2,
			callMode : "func"
		});
		return false;
	} else if ($("#trackingBy").val() == "请选择") {
		$("body").toast({
			msg : "所属快递不能为空！",
			type : 2,
			callMode : "func"
		});
		return false;
	} else {
		return true;
	}
}
// 验证输入格式
function clearNoNum(e) {
	var reg = /^[0-9a-zA-Z]+$/;
	if (e.name == "payAmount") {
		reg = /^([0-9.]+)$/;
		/*e.value = e.value.replace(/[^\d.]/g, "");*/
	}
	var str = e.value;
	if (!reg.test(str)) {
		e.focus();
	}
};
// 判断到付金额是否有值
function judgeHaspayAmount() {
	if ($("#payAmount").val() != "") {
		$("#payAmount").attr({
			"data-con" : "m",
			"data-error" : "到付金额格式不正确"
		});
	} else {
		$("#payAmount").removeAttr("data-con data-error");
	}
}
// 表单提交
function submitForm() {
	$("#submitBtn").click(function() {
		// 判断到付金额是否有值
		judgeHaspayAmount();
		// 判断是否为空
		if (!receivePeople()) {
			return false;
		}
		/*
		 * $("#registerForm").verification({ callback:function(){
		 */
		$.ajax({
			url : "mobile/courier/doRegister.action",//
			data : {
				courierInfo : JSON.stringify(getData())
			},
			type : "post",
			dataType : "json",
			beforeSend : function() {
				$("body").loading({
					move : "show"
				});
			},
			complete : function() {
				$("body").loading({
					move : "hide"
				});
			},
			success : function(res) {
				$("body").loading({
					move : "hide"
				});
				if (0 == res.status) {
					$("body").toast({
						msg : "快递录入成功！",
						type : 1,
						callMode : "func"
					});
					setTimeout(function() {
						window.location.reload();
					}, 200);
				} else {
					$("body").toast({
						msg : res.errorMsg,
						type : 2,
						callMode : "func"
					});
				}
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				$("body").toast({
					msg : textStatus,
					type : 2,
					callMode : "func"
				});
			}
		});
		/*
		 * } })
		 */
	});

}
// 获得数据
function getData() {
	var _recipients = $("#recipients");
	/*
	 * var entityUserInfo={ "uId" : _recipients.attr("uId"), "uAvatar" :
	 * _recipients.attr("uAvatar"), "uType" : 2, "personType" : '0',//待确认
	 * "uName" : _recipients.val(), "entityType" : "3" };
	 */
	var data = {
		consigneeId : _recipients.attr("uId"),// 收件人编号
		consigneeName : _recipients.val(),// 收件人
		courierNum : $("#trackingNum").val(),// 快递单号
		belong : $("#trackingBy").val(),// 所属快递
		money : $("#payAmount").val()
	// 付款金额
	};
	return data;
}
