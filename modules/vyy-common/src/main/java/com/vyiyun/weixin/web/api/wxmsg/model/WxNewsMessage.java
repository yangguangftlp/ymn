package com.vyiyun.weixin.web.api.wxmsg.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.vyiyun.weixin.utils.StringUtil;

/**
 * news消息
 * 
 * @author tf
 * 
 * @date 下午4:24:26
 */
public class WxNewsMessage extends WxBaseMessage {

	public WxNewsMessage() {
		super("news");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8148276841232059083L;

	/**
	 * 图文数据
	 */
	private List<Map<String, String>> dataMapList = new ArrayList<Map<String, String>>();

	/**
	 * 
	 * @param title
	 *            标题
	 * @param description
	 *            描述
	 * @param url
	 *            点击后跳转的链接
	 * @param picurl
	 *            图文消息的图片链接，支持JPG、PNG格式，较好的效果为大图640*320，小图80*80。如不填，在客户端不显示图片
	 * @return
	 */
	public WxNewsMessage addArticles(String title, String description, String url, String picurl) {
		Map<String, String> dataMap = new HashMap<String, String>();
		if (StringUtil.isNotEmpty(title)) {
			dataMap.put("title", title);
		}
		if (StringUtil.isNotEmpty(description)) {
			dataMap.put("description", description);
		}
		if (StringUtil.isNotEmpty(url)) {
			dataMap.put("url", url);
		}
		if (StringUtil.isNotEmpty(picurl)) {
			dataMap.put("picurl", picurl);
		}
		if (dataMap.size() > 0) {
			dataMapList.add(dataMap);
		}
		return this;
	}

	public JSONObject toJSON() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.putAll(super.toJSON());
		JSONObject jsonNewsObject = new JSONObject();
		jsonNewsObject.put("articles", dataMapList);
		jsonObject.put("news", jsonNewsObject);
		return jsonObject;
	}
}
