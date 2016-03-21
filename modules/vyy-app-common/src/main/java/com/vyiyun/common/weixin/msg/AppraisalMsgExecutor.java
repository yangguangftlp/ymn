/**
 * 
 */
package com.vyiyun.common.weixin.msg;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.vyiyun.common.weixin.dao.AppraisalMapper;
import com.vyiyun.common.weixin.entity.Appraisal;
import com.vyiyun.weixin.cache.ICache;
import com.vyiyun.weixin.cache.impl.SystemConfigCache;
import com.vyiyun.weixin.constant.Constants;
import com.vyiyun.weixin.entity.SystemConfig;
import com.vyiyun.weixin.enums.WeixinMsgType;
import com.vyiyun.weixin.model.wxmessage.WeixinMessageBase;
import com.vyiyun.weixin.msg.impl.AbstMsgExecutor;
import com.vyiyun.weixin.utils.ConfigUtil;
import com.vyiyun.weixin.utils.SpringContextHolder;
import com.vyiyun.weixin.utils.SystemCacheUtil;
import com.vyiyun.weixin.utils.WeixinMessageUtil;

/**
 * 评价消息执行器
 * 
 * @author tf
 * @date 2015年11月16日 下午4:35:43
 */
public class AppraisalMsgExecutor extends AbstMsgExecutor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7336807175412108744L;
	private String id;
	private String corpId;
	private String templateId;
	private String toUser;
	private Map<String, Object> dataMap;
	private Map<String, Object> themeMap;

	/**
	 * @param id
	 *            借款id
	 * @param corpId
	 *            企业ID
	 * @param toUser
	 *            发给人
	 * @param templateId
	 *            模板id
	 * @param dataMap
	 *            数据
	 * @param themeMap
	 *            主题
	 */
	public AppraisalMsgExecutor(String id, String corpId, String toUser, String templateId,
			Map<String, Object> dataMap, Map<String, Object> themeMap) {
		this.id = id;
		this.corpId = corpId;
		this.toUser = toUser;
		this.templateId = templateId;
		this.dataMap = dataMap;
		this.themeMap = themeMap;
	}

	/**
	 * @param id
	 *            借款id
	 * @param corpId
	 *            企业ID
	 * @param toUser
	 *            发给人
	 * @param templateId
	 *            模板id
	 * @param dataMap
	 *            数据
	 */
	public AppraisalMsgExecutor(String id, String corpId, String toUser, String templateId, Map<String, Object> dataMap) {
		this(id, corpId, toUser, templateId);
		this.dataMap = dataMap;
	}

	/**
	 * 
	 * @param id
	 *            借款id
	 * @param corpId
	 *            企业ID
	 * @param toUser
	 *            发给人
	 * @param templateId
	 *            模板id
	 */
	public AppraisalMsgExecutor(String id, String corpId, String toUser, String templateId) {
		this.id = id;
		this.corpId = corpId;
		this.toUser = toUser;
		this.templateId = templateId;
	}

	@Override
	public void execute() throws Exception {
		SystemConfigCache<Object> sysConfigCache = SystemCacheUtil.getInstance().getSystemConfigCache();
		SystemConfig systemConfig = sysConfigCache.getSystemConfig("system","weburl");
		// String corpId = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "corpId");
		String appraisal_agentid = convertAppId(ConfigUtil.get(Constants.WEIXIN_APP_PATH, "employeeAppraisal_agentid"));

		if (CollectionUtils.isEmpty(themeMap)) {
			themeMap = new HashMap<String, Object>();
		}
		if (CollectionUtils.isEmpty(dataMap)) {
			dataMap = new HashMap<String, Object>();
		}
		AppraisalMapper appraisalDao = (AppraisalMapper) SpringContextHolder.getBean(AppraisalMapper.class);
		Appraisal appraisal = appraisalDao.getAppraisalById(id);
		themeMap.put("theme", appraisal.getTheme());
		if (StringUtils.isNotEmpty(toUser)) {

			String content = new WeixinMessageBase(toUser, WeixinMsgType.text, appraisal_agentid,
					WeixinMessageUtil.generateLinkUrlMsg(templateId, themeMap, systemConfig.getValue(), new Object[] {
							this.corpId, id })).toJson();
			sendMessage(content);
		}
	}

	@Override
	public String getName() {
		return "评价";
	}
}
