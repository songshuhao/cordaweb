package ats.blockchain.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {MultipartAutoConfiguration.class})
@ComponentScan(basePackages = {"ats.blockchain.web"})
public class DiamondApplication
{
	public static void main(String[] args)
	{
		System.out.println("spring boot running ...");
		SpringApplication.run(DiamondApplication.class, args);
//		SpringApplication app = new SpringApplication(new Class[] {DiamondApplication.class});
//		app.setWebApplicationType(WebApplicationType.SERVLET);
//		try
//		{
//			app.run(args);
//		}
//		catch (Exception ex)
//		{
//			System.err.println("Failure to start Spring boot application:"+ex.toString());
//		}
	}
}
