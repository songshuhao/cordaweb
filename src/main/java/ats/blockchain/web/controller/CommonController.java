package ats.blockchain.web.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ats.blockchain.web.DiamondWebException;
import ats.blockchain.web.corda.CordaApi;

@Controller

@RequestMapping("/common")
public class CommonController {

	private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private CordaApi cordaApi;
	
	@RequestMapping("/getOtherUser")
	public ModelAndView getOtherUser(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		HttpSession session = request.getSession(false);
		if(session ==null) {
			logger.debug("session is null, relogin");
			mv.setViewName("login");
			return mv;
		}
		
		Map<String, List<String>> userMap;
		try {
			userMap = cordaApi.getTradediamondinf().getOtherUser();
			mv.addObject("userList", userMap);
		} catch (DiamondWebException e) {
			mv.addObject("state","fail");
			mv.addObject("message","get user failed.");
			logger.error("get user failed.",e);
		}
		return mv;
	}
}
