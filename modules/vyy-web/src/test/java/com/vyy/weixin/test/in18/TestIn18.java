package com.vyy.weixin.test.in18;

import org.junit.Test;

import com.vyiyun.weixin.utils.SpringContextHolder;
import com.vyy.weixin.test.AbstractEnergyTestCase;

public class TestIn18 extends AbstractEnergyTestCase {

	@Test
	public void sayIn18() {

		System.out.println(SpringContextHolder.getI18n("aa"));
		System.out.println(SpringContextHolder.getI18n("a"));
	}
}
