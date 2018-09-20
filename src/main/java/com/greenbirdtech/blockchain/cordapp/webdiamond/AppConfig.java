package com.greenbirdtech.blockchain.cordapp.webdiamond;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.greenbirdtech.blockchain.cordapp.webdiamond.impl.MenuImpl;
import com.greenbirdtech.blockchain.cordapp.webdiamond.impl.PermConfigHelper;
import com.greenbirdtech.blockchain.cordapp.webdiamond.inf.MenuInf;
import com.greenbirdtech.blockchain.cordapp.webdiamond.inf.PermConfigHelperInf;

//@Configuration
public class AppConfig{
	private Logger logger = LoggerFactory.getLogger(this.getClass());

//	@Bean
//	public HttpSessionListener httpSessionListener() {
//		logger.info("Injecting sessionlistener");
//		return (new SessionListener());
//	}

	/*
	 * @Bean public StandardServletMultipartResolver multipartResolver() {
	 * logger.info("Injecting servlet multipart resolver"); return(new
	 * StandardServletMultipartResolver()); }
	 */

//	@Override
//	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
//		logger.info("Default servlet handler is enabled");
//		configurer.enable();
//	}

//	@Override
//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		logger.info("Add resource handler for js");
//		registry.addResourceHandler("/js/**").addResourceLocations("/static/js/");
//	}

	@Bean
	public View jsonView() {
		logger.info("set MappingJackson2JsonView");
		MappingJackson2JsonView jsonview = new MappingJackson2JsonView();
		jsonview.setContentType("application/json");
		return (jsonview);
	}

//	@Bean(initMethod = "init")
	public PermConfigHelperInf permconfighelperinf() {
		logger.info("init PermConfigHelperInf");
		return (new PermConfigHelper());
	}

//	@Bean(initMethod = "init", destroyMethod = "cleanup")
	public MenuInf menuinf() {
		logger.info("init MenuInf");
		return (new MenuImpl());
	}
}
