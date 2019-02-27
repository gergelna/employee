package com.training.camel.cameltraining;

import java.net.InetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class CameltrainingApplication {

	private static final Logger log = LoggerFactory.getLogger(CameltrainingApplication.class);

	public static final String SERVER_CONTEXT_PATH = "server.servlet.context-path";
	public static final String SERVER_PORT = "server.port";
	public static final String SPRING_APPLICATION_NAME = "spring.application.name";

	public static void main(String[] args) {

		ApplicationContext applicationContext = SpringApplication.run(CameltrainingApplication.class, args);
		Environment env = applicationContext.getEnvironment();

		String protocol = "http";
		String hostAddress = "localhost";
		String port = env.getProperty(SERVER_PORT);
		String contextPath = env.getProperty(SERVER_CONTEXT_PATH);
		//String port = "8080";
		try {
			hostAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			log.warn("The host name could not be determined, using `localhost` as fallback");
		}

		log.info("\n------------------------------------------------------------\n\t" +
				 "Application '{}' is running! Access URLs:\n\t" +
					 "Local: \t\t{}://localhost:{}{}\n\t" +
					 "External: \t{}://{}:{}{}\n\t" +
					 "------------------------------------------------------------",
				 env.getProperty(SPRING_APPLICATION_NAME),
				 protocol,
				 port,
				 contextPath,
				 protocol,
				 hostAddress,
				 port,
				 contextPath
				 );
	}

}

