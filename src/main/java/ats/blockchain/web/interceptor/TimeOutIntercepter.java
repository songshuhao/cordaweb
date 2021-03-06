package ats.blockchain.web.interceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import ats.blockchain.web.model.UserInfo;
import ats.blockchain.web.utils.AOCBeanUtils;

/**
 * session 超时及输入非法地址重登陆
 * add by shuhao.song
 * @author Administrator
 *
 */
public class TimeOutIntercepter implements HandlerInterceptor
{
	private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
	//可以随意访问的url
    public String[] allowUrls;
    
    private String defaultUrl = "/login";

    public void setAllowUrls(String[] allowUrls) 
    {
        this.allowUrls = allowUrls;
    }
    
    private List<String> allUrls;
    
    public List<String> getAllUrls()
	{
		return allUrls;
	}

	public void setAllUrls(List<String> allUrls)
	{
		this.allUrls = allUrls;
	}

	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
    	logger.debug("preHandle - {}", request.getRequestURI());
    	logger.debug("preHandle - {}", request.getParameterMap());
    	
    	String requestUrl = request.getRequestURI().replace(request.getContextPath(), "");
    	
        response.setContentType("text/html;charset=utf-8");
        if(StringUtils.isNoneBlank(requestUrl)){
            for(String url:allowUrls){
                if(requestUrl.contains(url)){
                    return true;
                }
            }
        }
        
        if(AOCBeanUtils.isEmpty(allUrls))
        {
        	initAllUrl(request);
        }
        
        HttpSession session = request.getSession(false);
    	if(allUrls.contains(requestUrl) && null != session && null != session.getAttribute("userInfo"))
    	{
    		UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
        	if(StringUtils.isNoneBlank(userInfo.getUserId()))
        	{
        		return true;
        	}else
        	{
            	return errorHanding(request, response);
        	}
        }else 
        {
        	return errorHanding(request, response);
		}
        
    }
    
    public boolean isAjaxRequest(HttpServletRequest request){
        boolean result = false;
        String headerX = request.getHeader("X-Requested-With");
        result = (headerX != null  && headerX.equalsIgnoreCase("XMLHttpRequest"));
        return  result;     
         
    }
    
    public boolean errorHanding(HttpServletRequest request,HttpServletResponse response) throws IOException
    {
    	if(isAjaxRequest(request))
    	{
    		response.setHeader("sessionstatus", "timeout"); 
    	}else
    	{
    		response.sendRedirect(request.getContextPath() + defaultUrl);
		}
    	return false;
    }
    
    
    //获取所有url，进行非法url校验，如果校验不通过，跳转到登录界面
    public List<String> initAllUrl(HttpServletRequest request)
    {
    	allUrls = new ArrayList<String>();
    	WebApplicationContext wac = (WebApplicationContext) request.getAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE);//获取上下文对象
    	RequestMappingHandlerMapping bean = wac.getBean(RequestMappingHandlerMapping.class);//通过上下文对象获取RequestMappingHandlerMapping实例对象
    	Map<RequestMappingInfo, HandlerMethod> handlerMethods = bean.getHandlerMethods();
    	for (RequestMappingInfo rmi : handlerMethods.keySet()) 
    	{
    		PatternsRequestCondition prc = rmi.getPatternsCondition();
    		Set<String> patterns = prc.getPatterns();
    		for (String uStr : patterns) 
    		{
    			allUrls.add(uStr);
    		}
    	}
    	logger.debug("RequestMappingHandlerMapping Url:" + allUrls);
		return allUrls;
    }
}
