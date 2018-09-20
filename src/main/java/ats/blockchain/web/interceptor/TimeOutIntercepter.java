package ats.blockchain.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import ats.blockchain.web.model.UserInfo;

/**
 * session 超时重登陆
 * add by shuhao.song
 * @author Administrator
 *
 */
public class TimeOutIntercepter implements HandlerInterceptor
{
	private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
	//可以随意访问的url
    public String[] allowUrls;

    public void setAllowUrls(String[] allowUrls) 
    {
        this.allowUrls = allowUrls;
    }
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
    	logger.debug(this.getClass().getName()+":preHandle begin");
    	
    	String requestUrl = request.getRequestURI().replace(request.getContextPath(), "");
        response.setContentType("text/html;charset=utf-8");
        if(StringUtils.isNoneBlank(requestUrl)){
            for(String url:allowUrls){
                if(requestUrl.contains(url)){
                    return true;
                }
            }
        }
        HttpSession session = request.getSession(false);
        if(null != session && null != session.getAttribute("userInfo"))
        {
        	UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
        	if(StringUtils.isNoneBlank(userInfo.getUserId()))
        	{
        		return true;
        	}else
        	{
        		response.sendRedirect("/login");
            	return false;
        	}
        }else
        {
        	response.sendRedirect("/login");
        	return false;
        }
    }
}
