/**
 * 
 */
package com.vyy.weixin.test.memcache;

import org.junit.Test;

import com.danga.MemCached.MemCachedClient;
import com.vyiyun.weixin.utils.SpringContextHolder;
import com.vyy.weixin.test.AbstractEnergyTestCase;

/**
 * @author tf
 * 
 * @date 下午2:48:12
 */
public class MemcacheUtilTest extends AbstractEnergyTestCase {
	@Test
	public void sTest() {
		MemCachedClient m = SpringContextHolder.getBean("memcachedClient");
		m.set("name", "yunhui");
		System.out.println(m.get("name"));
	}
}
