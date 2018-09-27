package ats.blockchain.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import ats.blockchain.web.interceptor.TimeOutIntercepter;

/**
 * add by shuhao.song
 * interceptor Config
 * @author Administrator
 *
 */
@Configuration
public class InterceptorsConfig implements WebMvcConfigurer
{
	@Override
	public void addInterceptors(InterceptorRegistry registry)
	{
		// TODO Auto-generated method stub
		String[] allowUrls = {"login","logon","logout"};
		TimeOutIntercepter timeOutIntercepter = new TimeOutIntercepter();
		timeOutIntercepter.setAllowUrls(allowUrls);
		registry.addInterceptor(timeOutIntercepter).excludePathPatterns("/static/**");
		WebMvcConfigurer.super.addInterceptors(registry);
	}
}
