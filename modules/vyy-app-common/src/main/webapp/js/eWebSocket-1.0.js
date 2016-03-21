/**
 * 项目反馈消息
 * 
 * @param $
 */
(function($) {
	var eWebSocket = function(config) {
		this.isInit = false;
		this.defaults = {
			url : "",
			data : {
				status : '0'
			},
			open : function(msg) {
			},
			message : function(msg) {
			},
			close : function() {
			},
			onerror : function() {
			}
		};
		var _self = this;
		this.options = $.extend({}, this.defaults, config);
		try {
			this.socket = new WebSocket(this.options.url);
			this.socket.onopen = function(msg) {
				// 创建连接后发送消息数据
				//this.send(JSON.stringify(_self.options.data));
				//_self.options.open(msg);
			};
			this.socket.onmessage = function(msg) {
				_self.options.message(msg);
			};
			this.socket.onclose = function(msg) {
				console.log("connection closed.");
				_self.options.close(msg);
			};
			this.socket.onerror = function(msg) {
				_self.options.onerror(msg);
			};
		} catch (ex) {
			console.log(ex);
		}
	};
	eWebSocket.prototype = {
		sendMessage : function(message) {
			this.socket.send(JSON.stringify(message));
		}
	};
	$.fn.eWebSocket = function(options) {
		return new eWebSocket(options);
	};
})(jQuery);
