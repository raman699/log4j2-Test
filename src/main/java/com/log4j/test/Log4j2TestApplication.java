package com.log4j.test;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.LoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Log4j2TestApplication {

	public static void main(String[] args) {
		SpringApplication.run(Log4j2TestApplication.class, args);
		String loggerName = "testLogger";
		final ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();

		final LoggerComponentBuilder loggerComp = builder.newLogger(loggerName, Level.ALL).addAttribute("additivity",
				false);

		builder.add(loggerComp);

		LoggerContext ctx = Configurator.initialize(builder.build());
		final Configuration config = ctx.getConfiguration();
		ctx.start();
		ctx.updateLoggers();
		System.out.println(ctx.hasLogger(loggerName));
		Logger logger = ctx.getLogger(loggerName);
		System.out.println(logger.getLevel());
		System.out.println(ctx.getLogger(loggerName).getLevel());
	}

}
