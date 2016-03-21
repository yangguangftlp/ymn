/**
 * 
 */
package com.vyiyun.common.weixin.service.impl;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.vyiyun.common.weixin.dao.SignMapper;
import com.vyiyun.common.weixin.entity.Sign;
import com.vyiyun.common.weixin.entity.SignUser;
import com.vyiyun.common.weixin.service.ISignService;
import com.vyiyun.common.weixin.service.ISignUserService;
import com.vyiyun.weixin.cache.impl.DefaultCache;
import com.vyiyun.weixin.constant.Constants;
import com.vyiyun.weixin.entity.EntityAccount;
import com.vyiyun.weixin.entity.SystemConfig;
import com.vyiyun.weixin.enums.WeixinMsgType;
import com.vyiyun.weixin.model.DataResult;
import com.vyiyun.weixin.model.SqlQueryParameter;
import com.vyiyun.weixin.model.WeixinUser;
import com.vyiyun.weixin.model.wxmessage.WeixinMessageBase;
import com.vyiyun.weixin.msg.impl.AbstMsgExecutor;
import com.vyiyun.weixin.service.IEntityAccessoryService;
import com.vyiyun.weixin.service.IEntityAccountService;
import com.vyiyun.weixin.service.impl.AbstractBaseService;
import com.vyiyun.weixin.utils.ConfigUtil;
import com.vyiyun.weixin.utils.DateUtil;
import com.vyiyun.weixin.utils.ExcelUtil;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.SystemCacheUtil;
import com.vyiyun.weixin.utils.WeixinMessageUtil;
import com.vyiyun.weixin.utils.WeixinUtil;

/**
 * @author tf
 * 
 *         2015年6月24日
 */
@Service("signService")
public class SignServiceImpl extends AbstractBaseService implements ISignService {

	@Resource
	private SignMapper signDao;
	@Autowired
	private ISignUserService signUserService;
	@Autowired
	private IEntityAccountService entityAccountService;

	/**
	 * 实体附件处理
	 */
	@Autowired
	private IEntityAccessoryService entityFileService;

	@Override
	public int addSign(Sign... sign) {
		if (sign.length < 1) {
			return 0;
		}
		return signDao.addSign(Arrays.asList(sign));
	}

	@Override
	public List<Map<String, Object>> getSign(Sign sign) {
		List<Sign> signList = signDao.getSign(sign);
		List<Map<String, Object>> signListMap = new ArrayList<Map<String, Object>>();
		Map<String, Object> dataMap = null;
		if (!CollectionUtils.isEmpty(signList)) {
			for (Sign signObj : signList) {
				dataMap = signObj.getPersistentState();
				dataMap.put("sbeginTime", DateUtil.dateToString((Date) dataMap.get("beginTime"), "HH:mm"));
				dataMap.put("sendTime", DateUtil.dateToString((Date) dataMap.get("endTime"), "HH:mm"));
				dataMap.put("sbeginDate", DateUtil.dateToString((Date) dataMap.get("beginTime"), "yyyy-MM-dd HH:mm"));
				dataMap.put("sendDate", DateUtil.dateToString((Date) dataMap.get("endTime"), "yyyy-MM-dd HH:mm"));
				signListMap.add(dataMap);
			}
		}
		return signListMap;
	}

	@Override
	public void deleteById(String id) {
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId();
		Sign signTmp = new Sign();
		signTmp.setId(id);
		signTmp.setCorpId(corpId);
		signDao.deleteById(signTmp);
	}

	@Override
	public void update(Sign sign) {
		signDao.update(sign);
	}

	@Override
	public void launchSign(JSONObject jsonLaunchSignInfo) {
		if (null != jsonLaunchSignInfo) {
			String theme = jsonLaunchSignInfo.getString("theme");
			String beginTime = jsonLaunchSignInfo.getString("beginTime");
			String endTime = jsonLaunchSignInfo.getString("endTime");
			Sign sign = new Sign();
			// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
			String corpId = HttpRequestUtil.getInst().getCurrentCorpId();
			sign.setCorpId(corpId);
			// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
			sign.setId(jsonLaunchSignInfo.getString("id"));
			sign.setUserId(HttpRequestUtil.getInst().getCurrentWeixinUser().getUserid());
			sign.setUserName(HttpRequestUtil.getInst().getCurrentWeixinUser().getName());
			sign.setBeginTime(DateUtil.stringToDate(beginTime));
			sign.setEndTime(DateUtil.stringToDate(endTime));
			sign.setTheme(theme);
			sign.setSignType("1");
			addSign(sign);
			// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
			List<EntityAccount> entityAccountList = generateEntityAccount(jsonLaunchSignInfo, sign.getId(), corpId);
			// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
			if (!CollectionUtils.isEmpty(entityAccountList)) {
				// 添加目标时间
				for (EntityAccount entityAccount : entityAccountList) {
					entityAccount.setTargetDate(sign.getBeginTime());
				}
				entityAccountService.addEntityAccount(entityAccountList.toArray(new EntityAccount[] {}));
				// 发送消息

				Map<String, String> dataMap = new HashMap<String, String>();
				dataMap.put("theme", theme);
				dataMap.put("userName", sign.getUserName());
				dataMap.put("beginTime", beginTime);
				dataMap.put("endTime", endTime);

				Map<String, Object> msgParams = new HashMap<String, Object>();
				msgParams.put("signInfo", dataMap);
				msgParams.put("entityAccountList", entityAccountList);
				// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
				msgParams.put("corpId", corpId);
				// 获取当前企业对应的应用id
				String appId = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "sign_agentid");
				DefaultCache<Object> icahe = (DefaultCache<Object>) SystemCacheUtil.getInstance().getDefaultCache();
				appId = icahe.convertAppId(appId);
				msgParams.put("appId", appId);
				// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
				SystemCacheUtil.getInstance().add(new AbstMsgExecutor(msgParams) {
					/**
					 * 
					 */
					private static final long serialVersionUID = -6182755959949964215L;

					@Override
					public void set(Object obj) {

					}

					@Override
					public String getName() {
						return "发布签到信息...";
					}

					@SuppressWarnings("unchecked")
					@Override
					public void execute() throws Exception {
						Map<String, Object> msgParams = (Map<String, Object>) getObj();
						List<EntityAccount> entityAccountList = (List<EntityAccount>) msgParams
								.get("entityAccountList");
						int size = entityAccountList.size();
						// 需要考虑 用户数过大 微信一次最多1000 个用户
						StringBuffer users = new StringBuffer();
						for (int i = 0; i < size; i++) {
							users.append('|');
							users.append(entityAccountList.get(i).getAccountId());
						}
						if (users.length() > 1) {
							users.deleteCharAt(0);
						}
						Map<String, Object> dataMap = (Map<String, Object>) msgParams.get("signInfo");
						// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
						String _corpId = msgParams.get("corpId").toString();
						String _appId =convertAppId( msgParams.get("appId").toString());
						// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
						// 你有一条签到任务 时间：
						// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
						SystemConfig systemConfig = (SystemConfig) SystemCacheUtil.getInstance().getSystemConfigCache()
								.getSystemConfig("system","weburl");
						// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
						String entityId = entityAccountList.get(0).getEntityId();
						// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
						String content = new WeixinMessageBase(users.toString(), WeixinMsgType.text, _appId,
								WeixinMessageUtil.generateLinkUrlMsg("template_sign", dataMap, systemConfig.getValue(),
										new Object[] { _corpId, entityId })).toJson();
						// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
						WeixinUtil.sendMessage(content, getAccessToken());
					}
				});
			}

		}
	}

	@Override
	public DataResult querySignRecord(Sign sign, Map<String, Object> params, int pageIndex, int pageSize) {
		DataResult dataResult = new DataResult();
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		if (pageIndex != -1) {
			sqlQueryParameter.setPageIndex(pageIndex);
			sqlQueryParameter.setPage(true);
		}
		sqlQueryParameter.setParameter(sign);
		if (null == sign) {
			sqlQueryParameter.setParameter(new Sign());
		} else {
			sqlQueryParameter.setParameter(sign);
		}
		if (!CollectionUtils.isEmpty(params)) {
			sqlQueryParameter.getKeyValMap().putAll(params);
		}
		List<Sign> signList = signDao.querySignByPage(sqlQueryParameter);
		if (!CollectionUtils.isEmpty(signList)) {
			List<Map<String, Object>> signListMap = new ArrayList<Map<String, Object>>();
			Map<String, Object> dataMap = null;

			for (int i = 0, size = signList.size(); i < size; i++) {
				dataMap = signList.get(i).getPersistentState();
				dataMap.put("sbeginTime", DateUtil.dateToString((Date) dataMap.get("beginTime"), "yyyy-MM-dd HH:mm"));
				dataMap.put("sendTime", DateUtil.dateToString((Date) dataMap.get("endTime"), "yyyy-MM-dd HH:mm"));
				dataMap.put("sbeginHour", DateUtil.dateToString((Date) dataMap.get("beginTime"), "HH:mm"));
				dataMap.put("sendHour", DateUtil.dateToString((Date) dataMap.get("endTime"), "HH:mm"));
				signListMap.add(dataMap);
			}
			dataResult.setData(signListMap);
		}
		if (sqlQueryParameter.isPage()) {
			dataResult.setTotal((int) signDao.querySignCount(sqlQueryParameter));
		}
		// 处理日期
		return dataResult;
	}

	@Override
	public long querySignCount(SqlQueryParameter sqlQueryParameter) {
		return signDao.querySignCount(sqlQueryParameter);
	}

	@Override
	public void doSign(JSONObject jsonSignInfoObj) {
		if (null != jsonSignInfoObj) {
			WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
			SignUser signUser = new SignUser();
			// 微依云 公共应用 CorpId追加修正 2015-12-28 By zb.shen start.
			signUser.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
			// 微依云 公共应用 CorpId追加修正 2015-12-28 By zb.shen end.
			signUser.setId(jsonSignInfoObj.getString("id"));
			signUser.setSignId(jsonSignInfoObj.getString("signId"));
			signUser.setUserId(weixinUser.getUserid());
			signUser.setUserName(weixinUser.getName());
			signUser.setLocation(jsonSignInfoObj.getString("location"));
			signUser.setSignTime(jsonSignInfoObj.getDate("signTime"));
			signUser.setAttendType(jsonSignInfoObj.getString("attendType"));
			// 附件信息
			if (jsonSignInfoObj.containsKey("accessoryInfo")) {
				entityFileService.addEntityAccessory(signUser.getId(), jsonSignInfoObj.getJSONObject("accessoryInfo"));
			}
			signUserService.addSignUser(signUser);
		}
	}

	@Override
	public void doRemark(JSONObject jsonRemarkObj) {
		if (null != jsonRemarkObj) {
			WeixinUser currentUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
			SignUser signUser = new SignUser();
			signUser.setSignId(jsonRemarkObj.getString("signId"));
			// 微依云 公共应用 CorpId追加修正 2015-12-28 By zb.shen start.
			signUser.setUserId(currentUser.getUserid());
			signUser.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
			// 微依云 公共应用 CorpId追加修正 2015-12-28 By zb.shen end.
			signUser.setAttendType(jsonRemarkObj.getString("attendType"));
			signUser.setRemark(jsonRemarkObj.getString("remark"));
			signUserService.updateSignUser(signUser);
		}
	}

	@Override
	public Map<String, Object> getSignById(String id) {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId();
		Sign signTmp = new Sign();
		signTmp.setId(id);
		signTmp.setCorpId(corpId);
		Sign sign = signDao.getSignById(signTmp);
		if (null != sign) {
			dataMap = sign.getPersistentState();
			dataMap.put("sbeginTime", DateUtil.dateToString((Date) dataMap.get("beginTime"), "HH:mm"));
			dataMap.put("sendTime", DateUtil.dateToString((Date) dataMap.get("endTime"), "HH:mm"));
			dataMap.put("sbeginDate", DateUtil.dateToString((Date) dataMap.get("beginTime"), "yyyy-MM-dd HH:mm"));
			dataMap.put("sendDate", DateUtil.dateToString((Date) dataMap.get("endTime"), "yyyy-MM-dd HH:mm"));
		}
		return dataMap;
	}

	@Override
	public Map<String, Object> queryAttendanceList(JSONObject jsonSearchConditions) {

		Map<String, Object> retMap = new HashMap<String, Object>();

		String sPageIndex = HttpRequestUtil.getInst().getString("start"); // 页码
		String sPageSize = HttpRequestUtil.getInst().getString("length"); // 页面大小
		int pageIndex = -1;
		int pageSize = -1;
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		if (StringUtils.isNotEmpty(sPageSize) && sPageSize.matches("\\d+")) {
			pageSize = Integer.parseInt(sPageSize);
			sqlQueryParameter.setPageSize(pageSize);
		}
		if (StringUtils.isNotEmpty(sPageIndex) && sPageIndex.matches("\\d+")) {
			pageIndex = Integer.parseInt(sPageIndex);
			if (pageIndex == 0) {
				pageIndex = 1;
			} else {
				pageIndex = pageIndex / pageSize + 1;
			}
			sqlQueryParameter.setPageIndex(pageIndex);
			sqlQueryParameter.setPage(true);
		}
		sqlQueryParameter.getKeyValMap().put("userName", jsonSearchConditions.getString("userName"));
		sqlQueryParameter.getKeyValMap().put("startTime", jsonSearchConditions.getDate("startTime"));
		// BUG#179修改  by zb.shen 2016-01-25 start.
		Date endTime = jsonSearchConditions.getDate("endTime");
		if (null != endTime) {
			endTime.setTime(endTime.getTime() + 24 * 3600 * 1000 - 1);
		}
		sqlQueryParameter.getKeyValMap().put("endTime", endTime);
		// BUG#179修改  by zb.shen 2016-01-25 end.
		sqlQueryParameter.getKeyValMap().put("corpId", jsonSearchConditions.getString("corpId"));
		// 查询
		List<Map<String, Object>> attendanceList = signDao.queryAttendanceList(sqlQueryParameter);
		retMap.put("totalCount", sqlQueryParameter.getTotalRecord());
		retMap.put("list", attendanceList);
		return retMap;
	}

	@Override
	public ResponseEntity<byte[]> exportAttendanceListToExcel(JSONObject jsonSearchConditions) throws Exception {

		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();

		String userName = jsonSearchConditions.getString("userName");
		Date startTime = jsonSearchConditions.getDate("startTime");
		Date endTime = jsonSearchConditions.getDate("endTime");

		sqlQueryParameter.getKeyValMap().put("userName", userName);
		sqlQueryParameter.getKeyValMap().put("startTime", startTime);
		sqlQueryParameter.getKeyValMap().put("endTime", endTime);
		sqlQueryParameter.getKeyValMap().put("corpId", jsonSearchConditions.getString("corpId"));
		// 查询
		List<Map<String, Object>> attendanceList = signDao.queryAttendanceList(sqlQueryParameter);
		Iterator<Map<String, Object>> iterator = attendanceList.iterator();
		while (iterator.hasNext()) {
			Map<String, Object> next = iterator.next();
			Date signTime = (Date) next.get("signTime");
			next.put("signTime", DateUtil.dateToString(signTime, "yyyy-MM-dd HH:mm"));
			String attentType = next.get("attendType").toString();
			next.put("attendType", attentType.equals("0") ? "签到" : "签退");
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		LinkedHashMap<String, String> fieldMap = new LinkedHashMap<String, String>();
		fieldMap.put("userId", "员工工号");
		fieldMap.put("userName", "员工姓名");
		fieldMap.put("location", "签到地点");
		fieldMap.put("signTime", "签到时间");
		fieldMap.put("attendType", "签到/签退");
		fieldMap.put("remark", "备注");
		ExcelUtil.listToExcel(attendanceList, fieldMap, "考勤统计", bos);
		HttpHeaders headers = new HttpHeaders();
		String fileName = new String("考勤统计.xls".getBytes("UTF-8"), "iso-8859-1");// 为了解决中文名称乱码问题
		headers.setContentDispositionFormData("attachment", fileName);
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM); // application/vnd.ms-excel
		return new ResponseEntity<byte[]>(bos.toByteArray(), headers, HttpStatus.CREATED);
	}
}
