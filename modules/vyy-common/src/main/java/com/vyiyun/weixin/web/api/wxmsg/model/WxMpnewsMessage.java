/**
 * 
 */
package com.vyiyun.weixin.web.api.wxmsg.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.vyiyun.weixin.utils.StringUtil;

/**
 * @author tf
 * 
 * @date 下午4:36:37
 */
public class WxMpnewsMessage extends WxMediaMessage {

	public WxMpnewsMessage(String msgtype) {
		super(msgtype);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8701892271942272840L;

	/**
	 * 图文数据
	 */
	private List<Map<String, String>> dataMapList = new ArrayList<Map<String, String>>();

	/**
	 * 
	 * @param title
	 *            图文消息的标题
	 * @param thumb_media_id
	 *            图文消息缩略图的media_id,
	 *            可以在上传多媒体文件接口中获得。此处thumb_media_id即上传接口返回的media_id
	 * @param author
	 *            图文消息的作者
	 * @param content_source_url
	 *            图文消息点击“阅读原文”之后的页面链接
	 * @param content
	 *            图文消息的内容，支持html标签
	 * @param digest
	 *            图文消息的描述
	 * @param show_cover_pic
	 *            是否显示封面，1为显示，0为不显示
	 * @return
	 */
	public WxMpnewsMessage addx(String title, String thumb_media_id, String author, String content_source_url,
			String content, String digest, String show_cover_pic) {
		Map<String, String> dataMap = new HashMap<String, String>();
		if (StringUtil.isNotEmpty(title)) {
			dataMap.put("title", title);
		}
		if (StringUtil.isNotEmpty(thumb_media_id)) {
			dataMap.put("thumb_media_id", thumb_media_id);
		}
		if (StringUtil.isNotEmpty(author)) {
			dataMap.put("author", author);
		}
		if (StringUtil.isNotEmpty(content_source_url)) {
			dataMap.put("content_source_url", content_source_url);
		}
		if (StringUtil.isNotEmpty(content)) {
			dataMap.put("content", content);
		}
		if (StringUtil.isNotEmpty(digest)) {
			dataMap.put("digest", digest);
		}
		if (StringUtil.isNotEmpty(show_cover_pic)) {
			dataMap.put("show_cover_pic", show_cover_pic);
		}
		if (dataMap.size() > 0) {
			dataMapList.add(dataMap);
		}
		return this;
	}

	public JSONObject toJSON() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.putAll(super.toJSON());
		jsonObject.put("msgtype", "news");
		JSONObject jsonNewsObject = new JSONObject();
		if (!CollectionUtils.isEmpty(dataMapList)) {
			jsonNewsObject.put("articles", dataMapList);
		} else {
			jsonNewsObject.put("media_id", getMedia_id());
		}
		jsonObject.put("mpnews", jsonNewsObject);
		return jsonObject;
	}
}
