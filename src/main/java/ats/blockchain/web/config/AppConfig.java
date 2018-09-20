package ats.blockchain.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ats.blockchain.web.corda.PermConfigHelperInf;
import ats.blockchain.web.corda.impl.CordaApiImpl;
import ats.blockchain.web.corda.impl.PermConfigHelper;

@Configuration
public class AppConfig{
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/*@Bean(initMethod = "init")
	public PermConfigHelperInf permconfighelperinf() {
		logger.info("init PermConfigHelperInf");
		return (new PermConfigHelper());
	}

	@Bean(initMethod = "init", destroyMethod = "cleanup")
	public CordaApiImpl cordaApi() {
		logger.info("init cordaApi");
		return (new CordaApiImpl());
	}*/
}
