/**
 * 
 */
package com.vyiyun.common.weixin.controller.mobile;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.vyiyun.weixin.controller.AbstWebController;
import com.vyy.weixin.annotation.OAuth;
import com.vyy.weixin.annotation.Suite;

/**
 * @author tf
 * 
 * @date 上午11:45:32
 */
@Controller
@RequestMapping(value = "/mobile/game")
@Suite("4")
@OAuth
public class GameController extends AbstWebController {
	/**
	 * 随机选座位
	 * 
	 * @return
	 */
	@RequestMapping(value = "randomSeatView", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView randomSeatView(HttpServletRequest request) {
		ModelAndView modelView = createModelAndView("randomSeat");
		return modelView;
	}

	@Override
	protected String getPrefix() {
		return "jsp/mobile/game/";
	}
}
