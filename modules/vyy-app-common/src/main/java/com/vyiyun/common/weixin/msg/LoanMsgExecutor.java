/**
 * 
 */
package com.vyiyun.common.weixin.msg;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.vyiyun.common.weixin.dao.LoanMapper;
import com.vyiyun.common.weixin.entity.Loan;
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
import com.vyiyun.weixin.utils.WeixinUtil;

/**
 * 借款消息执行器
 * 
 * @author tf
 * @date 2015年11月3日 下午3:25:51
 */
public class LoanMsgExecutor extends AbstMsgExecutor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -981269425500639162L;
	private String corpId;
	private String id;
	private String templateId;
	private String toUser;
	private Map<String, Object> dataMap;
	private Map<String, Object> themeMap;

	/**
	 * @param corpId
	 *            企业ID
	 * @param id
	 *            借款id
	 * @param toUser
	 *            发给人
	 * @param templateId
	 *            模板id
	 * @param dataMap
	 *            数据
	 * @param themeMap
	 *            主题
	 */
	public LoanMsgExecutor(String corpId, String id, String toUser, String templateId, Map<String, Object> dataMap,
			Map<String, Object> themeMap) {
		this(corpId, id, toUser, templateId);
		this.dataMap = dataMap;
		this.themeMap = themeMap;
	}

	/**
	 * @param corpId
	 *            企业ID
	 * @param id
	 *            借款id
	 * @param toUser
	 *            发给人
	 * @param templateId
	 *            模板id
	 * @param dataMap
	 *            数据
	 * @param themeMap
	 *            主题
	 */
	public LoanMsgExecutor(String corpId, String id, String toUser, String templateId, Map<String, Object> dataMap) {
		this(corpId, id, toUser, templateId);
		this.dataMap = dataMap;
	}

	/**
	 * @param corpId
	 *            企业ID
	 * @param id
	 *            借款id
	 * @param toUser
	 *            发给人
	 * @param templateId
	 *            模板id
	 * @param dataMap
	 *            数据
	 * @param themeMap
	 *            主题
	 */
	public LoanMsgExecutor(String corpId, String id, String toUser, String templateId) {
		this.corpId = corpId;
		this.id = id;
		this.toUser = toUser;
		this.templateId = templateId;
	}

	@Override
	public void execute() throws Exception {
		SystemConfigCache<Object> sysConfigCache = SystemCacheUtil.getInstance().getSystemConfigCache();
		SystemConfig systemConfig = sysConfigCache.getSystemConfig("system","weburl");
		String loanAppId = convertAppId(ConfigUtil.get(Constants.WEIXIN_APP_PATH, "loan_agentid"));

		if (CollectionUtils.isEmpty(themeMap)) {
			themeMap = new HashMap<String, Object>();
		}
		if (CollectionUtils.isEmpty(dataMap)) {
			dataMap = new HashMap<String, Object>();
		}
		LoanMapper loanDao = (LoanMapper) SpringContextHolder.getBean(LoanMapper.class);
		Loan loan = loanDao.getLoanById(id);
		if(null != loan){
		themeMap.put("userName", loan.getUserName());
		themeMap.put("loanTypeDisplay",
				SystemCacheUtil.getInstance().getSystemStatusCache().getSystemStatusName("LoanType" , loan.getLoanType()));
		}
		if (StringUtils.isNotEmpty(toUser)) {

			String content = new WeixinMessageBase(toUser, WeixinMsgType.text, loanAppId,
					WeixinMessageUtil.generateLinkUrlMsg(templateId, themeMap,systemConfig.getValue(),
							new Object[] { corpId, id })).toJson();
			sendMessage(content);
		}
	}

	@Override
	public String getName() {
		return "借款";
	}
}
