package com.molecode.w2k;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Created by YP on 2015-12-18.
 */
public class W2KApplication {

	private static final Logger LOG = LoggerFactory.getLogger(W2KApplication.class);

	public static void main(String[] args) throws Exception {

		Properties w2kProperties = new Properties();
		w2kProperties.load(W2KApplication.class.getResourceAsStream("/w2k.properties"));
		int port = Integer.parseInt(w2kProperties.getProperty("server.port", "8080"));
		String contextPath = w2kProperties.getProperty("server.contextPath", "/rest");

		Server server = new Server(port);
		WebAppContext context = new WebAppContext();
		context.setDescriptor(W2KApplication.class.getResource("/web.xml").toString());
		context.setResourceBase("./");
		context.setContextPath(contextPath);
		context.setParentLoaderPriority(true);
		server.setHandler(context);
		server.setStopAtShutdown(true);
		LOG.info("W2K server start on port: {}, with context path: {}", port, contextPath);
		server.start();

	}

}
