/**
 * 
 */
package com.vyiyun.common.weixin.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.vyiyun.common.weixin.dao.SignUserMapper;
import com.vyiyun.common.weixin.entity.SignUser;
import com.vyiyun.common.weixin.service.ISignUserService;
import com.vyiyun.weixin.model.SqlQueryParameter;
import com.vyiyun.weixin.utils.DateUtil;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.StringUtil;

/**
 * @author tf
 * 
 *         2015年6月24日
 */
@Service("signUserService")
public class SignUserServiceImpl implements ISignUserService {

	@Autowired
	private SignUserMapper signUserDao;

	@Override
	public int addSignUser(SignUser... signUser) {
		if (signUser.length < 1) {
			return 0;
		}
		return signUserDao.addSignUser(Arrays.asList(signUser));
	}

	@Override
	public void deleteById(String id) {
		signUserDao.deleteById(id);
	}

	@Override
	public List<Map<String, Object>> getSignUser(SignUser signUser) {
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		sqlQueryParameter.setParameter(signUser);
		List<SignUser> signUserList = signUserDao.getSignUser(sqlQueryParameter);
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		if (!CollectionUtils.isEmpty(signUserList)) {
			Map<String, Object> temp = null;
			for (int i = 0, size = signUserList.size(); i < size; i++) {
				signUser = signUserList.get(i);
				temp = signUser.getPersistentState();
				temp.put("ssignTime", DateUtil.dateToString(signUser.getSignTime(), "HH:mm"));
				dataList.add(temp);
			}
		}
		return dataList;
	}

	@Override
	public List<Map<String, Object>> getSignCountByMonth(Map<String, Object> singCountInfo) {
		if (!CollectionUtils.isEmpty(singCountInfo)) {
			SignUser signUser = new SignUser();
			signUser.setUserId(StringUtil.getString(singCountInfo.get("userId")));
			// 微依云 公共应用 CorpId追加修正 2015-12-28 By zb.shen start.
			signUser.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
			// 微依云 公共应用 CorpId追加修正 2015-12-28 By zb.shen end.
			signUser.setAttendType(StringUtil.getString(singCountInfo.get("attendType")));
			SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
			sqlQueryParameter.setParameter(signUser);
			if (singCountInfo.containsKey("signType")) {
				sqlQueryParameter.getKeyValMap().put("signType", StringUtil.getString(singCountInfo.get("signType")));
			}
			if (singCountInfo.containsKey("monthDate")) {
				sqlQueryParameter.getKeyValMap().put("monthDate", singCountInfo.get("monthDate"));
			}
			if (singCountInfo.containsKey("isSignException")) {
				sqlQueryParameter.getKeyValMap().put("isSignException",
						StringUtil.getString(singCountInfo.get("isSignException")));
			}
			return signUserDao.getSignCountByMonth(sqlQueryParameter);
		}
		return null;
	}

	@Override
	public String getTotalHourByMonth(Map<String, Object> singCountInfo) {
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		sqlQueryParameter.getKeyValMap().putAll(singCountInfo);
		Map<String, Object> resuletData = signUserDao.getTotalHourByMonth(sqlQueryParameter);
		if (!CollectionUtils.isEmpty(resuletData) && resuletData.containsKey("totalSecond")) {
			long totalSecond = Long.parseLong(StringUtil.getString(resuletData.get("totalSecond")));
			return DateUtil.getTimeDiff(totalSecond);
		}
		return "";
	}

	@Override
	public Map<String, Object> getAttendanceRateByMonth(Map<String, Object> singCountInfo) {
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		sqlQueryParameter.getKeyValMap().putAll(singCountInfo);
		List<Map<String, Object>> resuletData = signUserDao.getAttendanceRateByMonth(sqlQueryParameter);
		if (!CollectionUtils.isEmpty(resuletData)) {
			return resuletData.get(0);
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> getExceptionKQSignByMonth(Map<String, Object> singCountInfo) {
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		if (singCountInfo.containsKey("userId")) {
			sqlQueryParameter.getKeyValMap().put("userId", StringUtil.getString(singCountInfo.get("userId")));
		}
		if (singCountInfo.containsKey("monthDate")) {
			sqlQueryParameter.getKeyValMap().put("monthDate", singCountInfo.get("monthDate"));
		}
		// 微依云 公共应用 CorpId追加修正 2015-12-28 By zb.shen start.
		sqlQueryParameter.getKeyValMap().put("corpId", HttpRequestUtil.getInst().getCurrentWeixinUser().getCorpId());
		// 微依云 公共应用 CorpId追加修正 2015-12-28 By zb.shen end.
		List<Map<String, Object>> dataList = signUserDao.getExceptionKQSignByMonth(sqlQueryParameter);
		// 合并处理
		if (!CollectionUtils.isEmpty(dataList)) {
			String[] attendType = null;
			String[] signTime = null;
			Date kqBeginTime = null;
			Date kqEendTime = null;
			// 记录考勤签到、签退使用

			Date beginTime = null;
			Date endTime = null;
			for (Map<String, Object> map : dataList) {
				kqBeginTime = (Date) map.get("BeginTime");
				kqEendTime = (Date) map.get("EndTime");
				map.put("kqDate", DateUtil.dateToString(kqBeginTime, "yyyy-MM-dd"));
				map.put("attendType0", "0");
				map.put("attendType1", "0");
				map.put("bSignTime", "");
				map.put("eSignTime", "");
				// 说明未打卡
				if (StringUtils.isEmpty(StringUtil.getString(map.get("SignID")))) {
					map.put("attendType0", "-1");
					map.put("attendType1", "-1");
				} else {
					attendType = StringUtil.getString(map.get("AttendType")).split(",");
					signTime = StringUtil.getString(map.get("SignTime")).split(",");
					if (attendType.length == 2) {
						for (int i = 0; i < 2; i++) {
							/** 早上打卡 判断是否迟到 */
							if ("0".equals(attendType[i])) {
								beginTime = DateUtil.stringToDate(signTime[i], "yyyy-MM-dd HH:mm");
								if (beginTime.after(kqBeginTime)) {
									map.put("attendType0", "1");
								}
								map.put("bSignTime", DateUtil.dateToString(beginTime, "HH:mm"));
							} else /** 下班打卡 判断是否早退 */
							if ("1".equals(attendType[i])) {
								endTime = DateUtil.stringToDate(signTime[i], "yyyy-MM-dd HH:mm");
								if (endTime.before(kqEendTime)) {
									map.put("attendType1", "1");// 下午异常 上午正常
								}
								map.put("eSignTime", DateUtil.dateToString(endTime, "HH:mm"));
							}
							// 这里判断 上午打卡 下午未打卡
						}

					} else {
						/** 早上打卡 */
						if ("0".equals(attendType[0])) {
							beginTime = DateUtil.stringToDate(signTime[0], "yyyy-MM-dd HH:mm");
							if (beginTime.after(kqBeginTime)) {
								map.put("attendType0", "1");
							}
							map.put("flag", "1");
							map.put("bSignTime", DateUtil.dateToString(beginTime, "HH:mm"));
						} else if ("1".equals(attendType[0])) {
							beginTime = DateUtil.stringToDate(signTime[0], "yyyy-MM-dd HH:mm");
							if (beginTime.before(kqBeginTime)) {
								map.put("attendType1", "1");
							}
							map.put("flag", "0");
							map.put("eSignTime", DateUtil.dateToString(beginTime, "HH:mm"));
						}
					}

				}
			}
		}
		return dataList;
	}

	@Override
	public void updateSignUser(SignUser signUser) {
		signUserDao.updateSignUser(signUser);
	}

}
