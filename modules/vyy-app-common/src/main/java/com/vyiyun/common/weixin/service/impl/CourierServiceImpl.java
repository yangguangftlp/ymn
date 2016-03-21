/**
 * 
 */
package com.vyiyun.common.weixin.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.vyiyun.common.weixin.dao.CourierMapper;
import com.vyiyun.common.weixin.entity.Courier;
import com.vyiyun.common.weixin.service.ICourierService;
import com.vyiyun.weixin.constant.Constants;
import com.vyiyun.weixin.entity.SystemConfig;
import com.vyiyun.weixin.enums.WeixinMsgType;
import com.vyiyun.weixin.exception.VyiyunException;
import com.vyiyun.weixin.model.DataResult;
import com.vyiyun.weixin.model.SqlQueryParameter;
import com.vyiyun.weixin.model.WeixinUser;
import com.vyiyun.weixin.model.wxmessage.WeixinMessageBase;
import com.vyiyun.weixin.msg.impl.AbstMsgExecutor;
import com.vyiyun.weixin.utils.CommonUtil;
import com.vyiyun.weixin.utils.ConfigUtil;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.StringUtil;
import com.vyiyun.weixin.utils.SystemCacheUtil;
import com.vyiyun.weixin.utils.WeixinMessageUtil;

/**
 * @author tf
 * 
 * @date 下午5:13:37
 */
@Service("courierService")
public class CourierServiceImpl implements ICourierService {
	private static final Logger LOGGER = Logger.getLogger(CourierServiceImpl.class);

	@Autowired
	private CourierMapper courierMapper;

	@Override
	public int addCourier(JSONObject jsonCourier) {
		String courierNum = jsonCourier.getString("courierNum");
		// 查询当前快递号是否已经存在
		long count = courierMapper.getCourierNumCount(courierNum);
		if (count > 0) {
			throw new VyiyunException("快递单号已存在");
		}
		WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
		Courier courier = new Courier();
		courier.setId(CommonUtil.GeneGUID());
		courier.setCreatorId(weixinUser.getUserid());
		courier.setCreatorName(weixinUser.getName());
		courier.setCorpId(weixinUser.getCorpId());
		courier.setCourierNum(courierNum);
		courier.setConsigneeName(jsonCourier.getString("consigneeName"));
		courier.setConsigneeId(jsonCourier.getString("consigneeId"));
		courier.setBelong(jsonCourier.getString("belong"));
		courier.setMoney(jsonCourier.getFloat("money"));
		courier.setCreateTime(new Date());
		courier.setStatus("0");
		int result = courierMapper.addCourier(Arrays.asList(courier));

		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("toUser", courier.getConsigneeId());
		dataMap.put("courierId", courier.getId());
		// 发送消息
		SystemCacheUtil.getInstance().add(new AbstMsgExecutor(dataMap) {
			/**
			 * 
			 */
			private static final long serialVersionUID = -6724394864376168173L;

			@SuppressWarnings("unchecked")
			@Override
			public void execute() throws Exception {
				Map<String, Object> dataMap = (Map<String, Object>) getObj();
				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
				String toUser = StringUtil.getString(dataMap.get("toUser"));
				String courierId = StringUtil.getString(dataMap.get("courierId"));
				// 需要考虑 用户数过大 微信一次最多1000 个用户
				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
				SystemConfig systemConfig = (SystemConfig) SystemCacheUtil.getInstance().getSystemConfigCache()
						.getSystemConfig("system", "weburl");
				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
				String courierAppId = convertAppId(ConfigUtil.get(Constants.WEIXIN_APP_PATH, "courier_agentid"));
				if (StringUtils.isNotEmpty(toUser.toString())) {
					String content = new WeixinMessageBase(toUser.toString(), WeixinMsgType.text, courierAppId,
							WeixinMessageUtil.generateLinkUrlMsg("template_courier_remind", dataMap,
									systemConfig.getValue(), new Object[] { this.corpId, courierId })).toJson();
					sendMessage(content);
				}
			}
		});
		return result;
	}

	@Override
	public int updateCourier(String id, String status) {
		Courier courier = new Courier();
		courier.setId(id);
		courier.setStatus(status);
		return updateCourier(courier);
	}

	@Override
	public int updateCourier(Courier courier) {
		return courierMapper.updateCourier(courier);
	}

	@Override
	public Courier getCourierById(String id) {
		return courierMapper.getCourierById(id);
	}

	@Override
	public DataResult getCourier(Courier courier, Map<String, Object> dataMap, int pageIndex, int pageSize) {
		DataResult dataResult = new DataResult();
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		if (pageIndex != -1) {
			sqlQueryParameter.setPageIndex(pageIndex);
			sqlQueryParameter.setPage(true);
		}
		if (pageSize != -1) {
			sqlQueryParameter.setPageSize(pageSize);
		}
		sqlQueryParameter.setParameter(courier);
		if (!CollectionUtils.isEmpty(dataMap)) {
			sqlQueryParameter.getKeyValMap().putAll(dataMap);
		}
		List<Courier> courierList = courierMapper.getCourier(sqlQueryParameter);
		if (!CollectionUtils.isEmpty(courierList)) {
			List<Map<String, Object>> dataMapList = new ArrayList<Map<String, Object>>();
			for (Courier c : courierList) {
				dataMapList.add(c.getPersistentState());
			}
			dataResult.setData(dataMapList);
			dataResult.setTotal(sqlQueryParameter.getTotalRecord());
		}
		return dataResult;
	}
}
