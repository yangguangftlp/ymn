/**
 * 
 */
package com.vyy.weixin.test;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * 单元测试类
 * 
 * @author tf
 * 
 * @date 2015年7月21日 上午10:20:34
 * @version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:config/spring*.xml")
/*
 * 事务配置 默认操作都将会回滚
 */
@Transactional
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class AbstractEnergyTestCase extends AbstractTransactionalJUnit4SpringContextTests {

}
