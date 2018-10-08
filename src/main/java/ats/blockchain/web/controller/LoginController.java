package ats.blockchain.web.controller;

import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;

import ats.blockchain.web.config.DiamondApplicationRunner;
import ats.blockchain.web.dao.DataMapper;
import ats.blockchain.web.model.UserInfo;

@Controller
public class LoginController extends BaseController {
	private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	DataMapper dataMapper;

	@RequestMapping("/Hello")
	@ResponseBody
	public String sayHello() {
		return "hello";
	}

	@RequestMapping("/login")
	public String login() {
		return "login";
	}
	
	@RequestMapping("/welcome")
	public String welcome() {
		return "welcome";
	}
	
	@RequestMapping(value = "/logon", method = { RequestMethod.POST })
	@ResponseBody
	public String logon(@RequestParam("userid") String userid, @RequestParam("password") String password,
			HttpServletRequest request) throws JSONException {
		HttpSession session = request.getSession(false);
		JSONObject result = new JSONObject();
		
		if (session != null) {
			logger.debug("Invalidate old session");
			session.invalidate();
		}
		
		session = request.getSession(true);
		if (checkLogin(userid,password)) {
			session.setAttribute("supplierMap", DiamondApplicationRunner.getSupplierMap());
			session.setAttribute("giaMap", DiamondApplicationRunner.getGiaLMap());
			session.setAttribute("vaultMap", DiamondApplicationRunner.getVaultMap());
			session.setAttribute("redeemOwnerId", redeemOwnerId);
			session.setAttribute("userInfo", this.getCurrentUserInfo());
			session.setAttribute("productMap", this.getProductMap());
			session.setAttribute("productMapJson", JSON.toJSON(this.getProductMap()));
			result.put("state", "success");
			result.put("message","login success");
			return result.toString();
		} else {
			result.put("state", "fail");
			result.put("message","logon failed: username or password is wrong");
			return result.toString();
		}
	}

	@RequestMapping(value = "/logout")
	public String logout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if(session!=null) {
			Enumeration<String> attrEnum = session.getAttributeNames();
			while(attrEnum.hasMoreElements()) {
				String key = attrEnum.nextElement();
				session.removeAttribute(key);
			}
			session.invalidate();
		}
		return "login";
	}
	
	public boolean checkLogin(String userName,String password)
	{
		boolean isCheck = false;
		List<UserInfo> userList = this.getUserList();
		for(UserInfo info : userList)
		{
			if(info.getUserId().equals(userName) && info.getPassword().equals(password))
			{
				isCheck = true;
				this.setCurrentUserInfo(info);
				break;
			}else
			{
				isCheck = false;
			}
		}
		return isCheck;
	}
}