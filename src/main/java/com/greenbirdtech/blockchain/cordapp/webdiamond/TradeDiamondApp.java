package com.greenbirdtech.blockchain.cordapp.webdiamond;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TradeDiamondApp
{
	public static void main(String[] args)
	{
		System.out.println("Trade Diamond Application v0.1");
		SpringApplication app = new SpringApplication(new Class[] {TradeDiamondApp.class,TradeDiamondInitializer.class});
		app.setWebApplicationType(WebApplicationType.SERVLET);
		try
		{
			app.run(args);
		}
		catch (Exception ex)
		{
			System.err.println("Failure to start Spring boot application:"+ex.toString());
		}
	}
}
