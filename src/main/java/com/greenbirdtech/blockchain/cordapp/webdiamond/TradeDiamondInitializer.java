package com.greenbirdtech.blockchain.cordapp.webdiamond;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

@Configuration
public class TradeDiamondInitializer implements ServletContextInitializer 
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Override
	public void onStartup(ServletContext context) throws ServletException
	{
		logger.info("TradeDiamondInitializer is invoked");
		Enumeration<String> attributeNames = context.getAttributeNames();
		while (attributeNames.hasMoreElements()){
			String key = attributeNames.nextElement();
			logger.debug("TradeDiamondInit param: {} = {}",key,context.getAttribute(key));
		}
		
		AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
		ctx.register(AppConfig.class);
		ctx.setServletContext(context);
		/*
		ServletRegistration.Dynamic servlet = context.addServlet("dispatcherDiamond",new DispatcherServlet(ctx));
		servlet.setLoadOnStartup(1);
		servlet.addMapping("/");
		*/
	}
}
