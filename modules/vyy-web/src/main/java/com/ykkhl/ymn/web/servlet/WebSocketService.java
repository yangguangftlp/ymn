package com.ykkhl.ymn.web.servlet;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

/**
 * 对外提供的webSocket服务
 */
public class WebSocketService extends WebSocketServlet {

	/**
	 * 日志
	 */
	private static final Logger LOGGER = Logger.getLogger(WebSocketService.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 2519869037267502166L;

	/**
	 * 已经连上服务的客户端
	 */
	private static List<WSMessageInbound> wsClients = Collections.synchronizedList(new ArrayList<WSMessageInbound>(50));

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	@Override
	protected StreamInbound createWebSocketInbound(String subProtocol, HttpServletRequest request) {
		return new WSMessageInbound();
	}

	/**
	 * 当客户端退出链接时需要移除,该方法线程安全
	 * 
	 * @param wsMessageInbound
	 */
	protected synchronized void removeClient(WSMessageInbound wsMessageInbound) {
		for (WSMessageInbound messageInbound : wsClients) {
			if (messageInbound.sessionId.equals(wsMessageInbound.sessionId)) {
				wsClients.remove(wsMessageInbound);
			}
		}
	}

	/**
	 * 客户端出来
	 * 
	 * @author tf
	 * 
	 */
	private class WSMessageInbound extends MessageInbound {

		/**
		 * 上传文件使用 该字段只是临时变量
		 */
		private String fileName;

		/**
		 * 用户id
		 */
		private String uId;
		/**
		 * 会话id
		 */
		private String sessionId;
		/**
		 * 与客户端的链接通道句柄
		 */
		private WsOutbound outbound;

		/**
		 * 二进制数据
		 * 
		 * @param message
		 * @throws IOException
		 */
		@Override
		protected void onBinaryMessage(ByteBuffer message) throws IOException {
			byte[] bytes = message.array();
			// 通知业务处理 
		}

		/**
		 * 文本数据
		 * 
		 * @param message
		 * @throws IOException
		 */
		@Override
		protected void onTextMessage(CharBuffer message) throws IOException {
			String msgContent = message.toString();
			try {
				JSONObject params = JSONObject.parseObject(msgContent);
				for (WSMessageInbound msgInbound : wsClients) {
					if (msgInbound.uId.equals(params.getString("touid"))) {
						// 通知业务处理 
						msgInbound.outbound.writeTextMessage(CharBuffer.wrap(params.toJSONString()));
						msgInbound.outbound.flush();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void onOpen(WsOutbound outbound) {
			super.onOpen(outbound);
			this.outbound = outbound;

			// 通知业务处理 
		}

		@Override
		protected void onClose(int status) {
			super.onClose(status);
			removeClient(this);
		}

	}
}
