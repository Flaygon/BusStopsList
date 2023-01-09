package com.publictransportschedules.PublicTransportSchedules;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class PublicTransportSchedulesApplication extends SpringBootServletInitializer
{
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
	{
		return application.sources(PublicTransportSchedulesApplication.class);
	}

	public static void main(String[] args)
	{
		SpringApplication.run(PublicTransportSchedulesApplication.class, args);
	}
}