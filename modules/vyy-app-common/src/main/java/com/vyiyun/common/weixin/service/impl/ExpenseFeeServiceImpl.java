/**
 * 
 */
package com.vyiyun.common.weixin.service.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vyiyun.common.weixin.dao.ExpenseFeeMapper;
import com.vyiyun.common.weixin.entity.ExpenseFee;
import com.vyiyun.common.weixin.service.IExpenseFeeService;

/**
 * 报销费用
 * 
 * @author tf
 * @date 2015年10月23日 下午4:44:07
 */
@Service("expenseFeeService")
public class ExpenseFeeServiceImpl implements IExpenseFeeService {

	@Autowired
	private ExpenseFeeMapper expenseFeeDao;

	@Override
	public int addExpenseFee(ExpenseFee... expenseFee) {
		if (expenseFee.length < 1) {
			return 0;
		}
		return expenseFeeDao.addExpenseFee(Arrays.asList(expenseFee));
	}

	@Override
	public int deleteExpenseFeeById(String... id) {
		return expenseFeeDao.deleteExpenseFeeById(Arrays.asList(id));
	}

	@Override
	public int deleteExpenseFeeByExpenseId(String... expenseId) {
		return expenseFeeDao.deleteExpenseFeeByExpenseId(Arrays.asList(expenseId));
	}

	@Override
	public int updateExpenseFeeById(ExpenseFee expenseFee) {
		return expenseFeeDao.updateExpenseFeeById(expenseFee);
	}

	@Override
	public List<ExpenseFee> getExpenseFee(ExpenseFee expenseFee) {
		return expenseFeeDao.getExpenseFee(expenseFee);
	}

}
