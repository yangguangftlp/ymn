/**
 * 
 */
package com.vyiyun.weixin.cache.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import com.vyiyun.weixin.cache.ICache;
import com.vyiyun.weixin.model.WeixinDept;
import com.vyiyun.weixin.model.WeixinUser;
import com.vyiyun.weixin.service.IWeixinContactsService;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.MemcacheUtil;
import com.vyiyun.weixin.utils.SpringContextHolder;
import com.vyiyun.weixin.utils.StringUtil;
import com.vyiyun.weixin.utils.SystemCacheUtil;

/**
 * @author tf
 * @param <T>
 * 
 * @date 下午5:50:19
 */
@SuppressWarnings("unchecked")
public class WeixinContactCache<T> implements ICache<T> {
	private static final Logger LOGGER = Logger.getLogger(WeixinContactCache.class);

	/**
	 * 默认过期时间1分钟
	 */
	private static int EXPIRY_TIME = 2 * 60 * 1000;

	protected Map<String, T> cache;
	private IWeixinContactsService iWeixinContactsService;

	/** Cache with no limit */
	public WeixinContactCache() {
		// this.cache = new HashMap<String, T>();
		iWeixinContactsService = (IWeixinContactsService) SpringContextHolder.getBean(IWeixinContactsService.class);
	}

	/**
	 * Cache which has a hard limit: no more elements will be cached than the
	 * limit.
	 */
	public WeixinContactCache(final int limit) {
		this.cache = new LinkedHashMap<String, T>(limit + 1, 0.75f, true) {
			// +1 is needed, because the entry is inserted first, before it is
			// removed
			// 0.75 is the default (see javadocs)
			// true will keep the 'access-order', which is needed to have a real
			// LRU cache
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean removeEldestEntry(java.util.Map.Entry<String, T> eldest) {
				boolean removeEldest = size() > limit;
				if (removeEldest) {
					LOGGER.trace("Cache limit is reached, {} will be evicted," + eldest.getKey());
				}
				return removeEldest;
			}
		};
	}

	@Override
	public T get(String id) {
		return (T) MemcacheUtil.getIns().getMemCached().get(id);
	}

	@Override
	public void add(String id, T object) {
		// 默认存放5分钟
		MemcacheUtil.getIns().getMemCached().set(id, object, new Date(EXPIRY_TIME));
		// cache.put(id, object);
	}

	/**
	 * 缓存
	 * 
	 * @param id
	 * @param object
	 * @param expire
	 *            毫秒数据
	 */
	public void add(String id, T object, int expire) {
		MemcacheUtil.getIns().getMemCached().set(id, object, new Date(expire));
	}

	@Override
	public void remove(String id) {
		// cache.remove(id);
		MemcacheUtil.getIns().getMemCached().delete(id);
	}

	@Override
	public void clear() {
		// cache.clear();
	}

	@Override
	public void init() {
	}

	/**
	 * 获取子部门信息 从缓存中获取
	 * 
	 * @param dept
	 *            父部门
	 * @param deptId
	 *            子部门Id
	 * @return
	 */
	public List<WeixinDept> getChildDeptById(WeixinDept dept, String deptId) {
		if (null != dept) {
			if (dept.getId().equals(deptId)) {
				return dept.getChild();
			}
			if (!CollectionUtils.isEmpty(dept.getChild())) {
				List<WeixinDept> childDeptList = null;
				for (WeixinDept childDept : dept.getChild()) {
					childDeptList = getChildDeptById(childDept, deptId);
					if (!CollectionUtils.isEmpty(childDeptList)) {
						return childDeptList;
					}
				}
			}
		}
		return null;
	}

	/**
	 * 根据用户id 从缓存中获取用户
	 * 
	 * @param userid
	 *            用户id
	 * @return 返回缓存中用户
	 */
	public WeixinUser getUserById(String userid) {
		if (StringUtil.isNotEmpty(userid)) {
			String corpId = HttpRequestUtil.getInst().getCurrentCorpId();
			List<WeixinUser> weixinUserList = (List<WeixinUser>) get(corpId + "_AllUser");
			if (CollectionUtils.isEmpty(weixinUserList)) {
				weixinUserList = initWeixinUser();
			}
			for (int i = 0, size = weixinUserList.size(); i < size; i++) {
				if (weixinUserList.get(i).getUserid().equals(userid)) {
					return weixinUserList.get(i);
				}
			}
		}
		return null;
	}

	public List<WeixinUser> initWeixinUser() {
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId();
		ICache<Object> icache = SystemCacheUtil.getInstance().getWeixinContactCache();
		List<WeixinUser> weixinUserList = (List<WeixinUser>) get(corpId + "_AllUser");
		if (CollectionUtils.isEmpty(weixinUserList)) {
			weixinUserList = new ArrayList<WeixinUser>();
			List<WeixinDept> weixinDeptList = (List<WeixinDept>) get(corpId + "_RootDept");
			if (CollectionUtils.isEmpty(weixinDeptList)) {
				weixinDeptList = initWeixinDept();
			}
			for (WeixinDept weixinDept : weixinDeptList) {
				weixinUserList.addAll(iWeixinContactsService.getWeixinUserByDeptId(weixinDept.getId()));
			}
			//考虑到人员变动比较大
			icache.add(corpId + "_AllUser", weixinUserList, 1000 * 60 * 2);
		}
		return weixinUserList;
	}

	public WeixinDept getDeptById(String deptId) {
		List<WeixinDept> weixinDeptList = initWeixinDept();
		if (!CollectionUtils.isEmpty(weixinDeptList)) {
			for (WeixinDept weixinDept : weixinDeptList) {
				return getDeptById(weixinDept, deptId);
			}
		}
		return null;
	}

	public List<WeixinDept> initWeixinDept() {
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId();
		List<WeixinDept> weixinDeptList = (List<WeixinDept>) get(corpId + "_RootDept");
		if (CollectionUtils.isEmpty(weixinDeptList)) {
			weixinDeptList = iWeixinContactsService.getWeixinDeptIds();
			List<String> ids = new ArrayList<String>();
			for (WeixinDept dept : weixinDeptList) {
				ids.add(dept.getId());
			}
			List<WeixinDept> roots = new ArrayList<WeixinDept>();
			WeixinDept sysMenu = null;
			WeixinDept temp = null;
			int size = weixinDeptList.size();
			for (int i = 0; i < size; i++) {
				sysMenu = weixinDeptList.get(i);
				if (!ids.contains(sysMenu.getParentId()) || "1".equals(sysMenu.getId())) {
					roots.add(sysMenu);
				}
				for (int j = i + 1; j < size; j++) {
					temp = weixinDeptList.get(j);
					if (sysMenu.getId().equals(temp.getParentId())) {
						sysMenu.addChild(temp);
					} else if (temp.getId().equals(sysMenu.getParentId())) {
						temp.addChild(sysMenu);
					}
				}
			}
			weixinDeptList.clear();
			weixinDeptList.addAll(roots);
			SystemCacheUtil.getInstance().getDefaultCache().add(corpId + "_RootDept", weixinDeptList, 1000 * 60 * 5);
		}
		return weixinDeptList;
	}

	/**
	 * 获取部门信息 从缓存中获取
	 * 
	 * @param dept
	 *            父部门
	 * @param deptId
	 *            子部门Id
	 * @return
	 */
	public static WeixinDept getDeptById(WeixinDept parentDept, String deptId) {
		if (null != parentDept) {
			if (parentDept.getId().equals(deptId)) {
				return parentDept;
			}
			if (!CollectionUtils.isEmpty(parentDept.getChild())) {
				WeixinDept dept = null;
				for (WeixinDept childDept : parentDept.getChild()) {
					dept = getDeptById(childDept, deptId);
					if (null != dept) {
						return dept;
					}
				}
			}
		}
		return null;
	}

	public List<WeixinUser> getWeixinUsers() {
		return initWeixinUser();
	}

	/**
	 * 获取部门以及子部门id
	 * 
	 * @return
	 */
	public List<String> getDeptIds(String deptId) {
		// 获取deptId 下所有部门
		WeixinDept dept = (WeixinDept) get("AllDept");
		if (null == dept) {
			this.initWeixinDept();
			dept = (WeixinDept) get("AllDept");
		}

		List<String> deptIdList = new ArrayList<String>();
		deptIdList.add(deptId);
		List<WeixinDept> departmentList = getChildDeptById(dept, deptId);
		// 这里需要递归遍历所有部门以及子部门
		if (!CollectionUtils.isEmpty(departmentList)) {
			for (WeixinDept department : departmentList) {
				recurseDepartment(department, deptIdList);
			}
		}
		return deptIdList;
	}

	public String getUserDept(String... deptId) {
		StringBuffer depts = new StringBuffer();
		WeixinDept department = null;
		for (int i = 0; i < deptId.length; i++) {
			department = getDeptById(deptId[i]);
			if (null != department) {
				depts.append(',').append(department.getName());
			}
		}
		if (depts.length() > 0) {
			depts.deleteCharAt(0);
		}
		return depts.toString();
	}

	private void recurseDepartment(WeixinDept department, List<String> deptIdList) {
		deptIdList.add(department.getId());
		if (!CollectionUtils.isEmpty(department.getChild())) {
			for (WeixinDept childDept : department.getChild()) {
				recurseDepartment(childDept, deptIdList);
			}
		}
	}
}
