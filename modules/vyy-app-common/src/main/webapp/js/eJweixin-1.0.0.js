
/*
@auther taofang
2015-08-07 14:33
 */

window.DEUG_MODE = true;

(function($) {
	function eWeixinJSUtil() {
		var _self = this;
		_self._wx_loaded = false;
	};
	// 属性
	eWeixinJSUtil.prototype = {
		init: function(signature,jsApiList) {
			var wxconfig = {
					    debug : window.DEUG_MODE,//开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
						appId : signature.appId, // 必填，企业号的唯一标识，此处填写企业号corpid
						timestamp : signature.timestamp, // 必填，生成签名的时间戳
						nonceStr : signature.nonceStr, // 必填，生成签名的随机串
						signature : signature.signature,// 必填，签名，见附录1
						jsApiList : jsApiList
			      };
			wx.config(wxconfig);
			var _self = this;
			// 初始化成功
			wx.ready(function() {
				_self._wx_loaded = true;
			});
			// 初始化失败
			wx.error(function(res) {
				_self._wx_loaded = false;
			});
		},
		getLocation : function(callback) {
			// 对象指针
			var _self = this;
			var interval = null;
			interval = setInterval(function() {
				if (_self._wx_loaded) {
					// loading 关闭
					wx.getLocation({
						"success" : function(res) {
							_self.get_address(res,callback);
							
						},
						"fail" : function(res) {
							if (typeof (callback) == "function") {
								callback(null);
							}
						}
					});
					// 退出循环
					clearInterval(interval);
					return false;
				}
			}, 500);
		},
		/**
		 * 获取经纬度所在位置信息
		 * 
		 * @param res
		 * @param options
		 * @returns {Object}
		 */
		get_address : function(res, callback) {
			// 初始化要输出的数据
			$.ajax({
				url : 'mobile/common/getLocation.action',
				type : 'post',
				data : {
					lat : res.latitude,
					lng : res.longitude
				},
				success : function(data, textStatus) {
					if (typeof (callback) == "function") {
						callback(data);
					}
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					callback(null);
				}
			});
		},
		cameraImg:function(callback){
			// 对象指针
			var _self = this;
			var interval = setInterval(function() {
				if (_self._wx_loaded) {
					var images = {
						localId: [],
						serverId: []
					};
					wx.chooseImage({
						"sourceType": ['camera'], // 可以指定来源是相册还是相机，默认二者都有
						"success": function (res) {
							images.localId = res.localIds;
							if (images.localId.length > 0) {
								$.eWeixinJSUtil.uploadImg(images, callback);
							}
						},
						"fail":function(res){
						}
					});
					// 退出循环
					clearInterval(interval);
					return false;
				}
			},500);
		},
		chooseImg : function(callback){
			// 对象指针
			var _self = this;
			var interval = setInterval(function() {
				if (_self._wx_loaded) {
					var images = {
						localId: [],
						serverId: []
					};
					wx.chooseImage({
						"sourceType": ['album'], // 可以指定来源是相册还是相机，默认二者都有
						"success": function (res) {
							images.localId = res.localIds;
							if (images.localId.length > 0) {
								$.eWeixinJSUtil.uploadImg(images, callback);
							}
						},
						"fail":function(res){
						}
					});
					// 退出循环
					clearInterval(interval);
					return false;
				}
			},500);
		},

		uploadImg : function($img,callback){
			if ($img.localId.length == 0) {
				alert('请先选择图片');
				return;
			}
			function upload() {
				var local_id = $img.localId.pop();
				wx.uploadImage({
					"isShowProgressTips" : 1,
					"localId" : local_id,
					"success" : function(res) {
						var $serverId = [];
						$serverId.push(res.serverId);
						$.ajax({
							url : 'mobile/common/downloadAccessory.action',
							type : 'post',
							data : {
								mediaId : JSON.stringify($serverId)
							},
							dataType : "json",
							success : function(data, textStatus) {
								if (typeof (callback) == "function") {
									callback(data);
								}
							},
							error : function(XMLHttpRequest, textStatus,
									errorThrown) {
								// callback(null);
								//alert(JSON.stringify(errorThrown));
							}
						});
					},
					"fail" : function(res) {
						alert(JSON.stringify(res));
					},
					"complete" : function(res) {
						// 如果上传不成功，则清理已传入的数据
						/*if (res.match(/:\s*ok$/i) == null) {
							// 失败
							alert(-1);
						}*/
						if (0 < $img.localId.length) {
							upload();
						}
					}
				});
			}
			upload();
		},
		// 点击看大图
		previewImg : function(imgUrl){
			// document.querySelector('#'+id).onclick = function () {
			console.log(imgUrl.bigImgUrl);
				wx.previewImage({
					current: imgUrl.currentImgUrl,
					urls: imgUrl.bigImgUrl
				});
			//};
		},
		// 扫描二维码
		scanQRCode : function(callback){
			wx.scanQRCode({
			    desc: 'scanQRCode desc',
			    needResult: 1, // 默认为0，扫描结果由微信处理，1则直接返回扫描结果，
			    scanType: ["qrCode","barCode"], // 可以指定扫二维码还是一维码，默认二者都有
			    success: function (res) {
			    	if (typeof (callback) == "function") {
						callback(res);
					}
			    }
			});
		}
	};
	$.eWeixinJSUtil = new eWeixinJSUtil();
})(jQuery);
