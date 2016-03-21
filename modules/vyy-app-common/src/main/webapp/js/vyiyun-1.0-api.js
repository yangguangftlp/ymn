/**
 * 微移云前端api
 * 
 * @author tf
 * @date 下午1:45:59
 */

window.DEUG_MODE = true;
(function($) {
	/**
	 * 系统初始化
	 */
	function VyiyunUtil() {
		var _self = this;
		_self._config = {
			resPath : ''
		};
	}
	// 属性
	VyiyunUtil.prototype = {
		/**
		 * 系统初始化
		 */
		init : function(config) {
			// 对象指针
			var _self = this;
			_self._config = config;
		},
		/**
		 * 获取附件资源 例如：文件、图片资源路径等
		 */
		getResPath : function() {
		},
	};
	$.VyiyunUtil = new VyiyunUtil();
})(jQuery);