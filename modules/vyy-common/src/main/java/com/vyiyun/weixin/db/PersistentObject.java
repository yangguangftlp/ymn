/**
 * 
 */
package com.vyiyun.weixin.db;

/**
 * 数据实体
 * @author tf
 *
 * @date 2015年7月1日 下午3:06:28
 * @version 1.0
 */
import java.util.Map;

public interface PersistentObject extends Cloneable {
	Map<String, Object> getPersistentState();
}
