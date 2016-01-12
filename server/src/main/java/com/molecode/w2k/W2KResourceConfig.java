package com.molecode.w2k;

import com.molecode.w2k.resources.EvernoteOAuthResource;
import com.molecode.w2k.resources.EvernoteResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;

/**
 * Created by YP on 2015-12-18.
 */
public class W2KResourceConfig extends ResourceConfig {

	public W2KResourceConfig() {
		register(RequestContextFilter.class);
		register(EvernoteResource.class);
		register(EvernoteOAuthResource.class);
	}
}
