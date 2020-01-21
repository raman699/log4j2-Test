package com.log4j.test;

import java.nio.charset.StandardCharsets;

import org.apache.commons.csv.CSVFormat;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter.Result;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.LoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.filter.ThresholdFilter;
import org.apache.logging.log4j.core.layout.CsvParameterLayout;
import org.apache.logging.log4j.core.layout.PatternLayout;
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
		builder.setConfigurationSource(null);
		Configuration configuration = builder.build();
		LoggerContext ctx = Configurator.initialize(builder.build());
		ctx.start(configuration);
		ctx.updateLoggers(configuration);
		System.out.println(ctx.hasLogger(loggerName));
		Logger logger = ctx.getLogger(loggerName);
		System.out.println(logger.isAdditive());
		System.out.println(ctx.getLogger(loggerName).getLevel());
		Appender csvAppender = createCsvAppender(configuration);
		Appender textAppender = createTextAppender(configuration);
		csvAppender.start();
		textAppender.start();
		logger.addAppender(csvAppender);
		logger.addAppender(textAppender);
		logger.error("the error message", "Test Paramter");
		logger.error("text message", "Test Paramter");
		csvAppender.stop();
		textAppender.stop();
	}

	private static Layout<String> getCsvLayout(final Configuration config) {

		return new CsvParameterLayout(config, StandardCharsets.UTF_8, CSVFormat.DEFAULT.withDelimiter(';'), getHeader(),
				null);
	}

	private static String getHeader() {
		return "column1;coloumn2";
	}

	private static Appender createCsvAppender(final Configuration config) {

		final Layout<String> layout = getCsvLayout(config);

		return RollingFileAppender.newBuilder().setConfiguration(config).setName("csvAppender")
				.withFileName("TestFile.csv").withFilePattern("TestFile.csv")
				.withPolicy(SizeBasedTriggeringPolicy.createPolicy("100M"))
				.withStrategy(DefaultRolloverStrategy.newBuilder().withConfig(config).build()).withImmediateFlush(true)
				.setFilter(ThresholdFilter.createFilter(Level.ALL, Result.ACCEPT, Result.DENY)).setLayout(layout)
				.build();
	}

	private static Appender createTextAppender(final Configuration config) {

		final Layout<String> layout = getTextLayout(config, "header");

		return RollingFileAppender.newBuilder().setConfiguration(config).setName("txtAppender")
				.withFileName("TestFile.text").withFilePattern("TestFile.txt")
				.withPolicy(SizeBasedTriggeringPolicy.createPolicy("100M"))
				.withStrategy(DefaultRolloverStrategy.newBuilder().withConfig(config).build()).withImmediateFlush(true)
				.setFilter(ThresholdFilter.createFilter(Level.ALL, Result.ACCEPT, Result.DENY)).setLayout(layout)
				.build();
	}

	private static Layout<String> getTextLayout(final Configuration config, final String header) {
		return PatternLayout.newBuilder().withConfiguration(config).withCharset(StandardCharsets.UTF_8)
				.withPattern("[%d][%-5.-5p] - %m%n").withHeader(header).build();
	}
}
