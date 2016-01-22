package com.molecode.w2k.oauth;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.Api;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuthService;
import com.molecode.w2k.models.UserCredential;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by YP on 2016-01-07.
 */
public class OAuthAssistantScribeImpl implements OAuthAssistant {

	private static final Logger LOG = LoggerFactory.getLogger(OAuthAssistantScribeImpl.class);

	private String apiKey;

	private String apiSecret;

	private String callback;

	private Class<? extends Api> apiClass;

	private OAuthResponseParser responseParser;

	private long requestTokenExpireTime = 10 * 60 * 1000;

	private TimedExpiringCache<String, Token> requestTokenCache;

	private OAuthService oAuthService;

	@Required
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	@Required
	public void setApiSecret(String apiSecret) {
		this.apiSecret = apiSecret;
	}

	@Required
	public void setApiClass(Class<? extends Api> apiClass) {
		this.apiClass = apiClass;
	}

	@Required
	public void setCallback(String callback) {
		this.callback = callback;
	}

	@Required
	public void setResponseParser(OAuthResponseParser responseParser) {
		this.responseParser = responseParser;
	}

	public void setRequestTokenExpireTime(int requestTokenExpireTime) {
		this.requestTokenExpireTime = requestTokenExpireTime * 1000;
	}

	public void init() {
		oAuthService = new ServiceBuilder().provider(apiClass).apiKey(apiKey).apiSecret(apiSecret).callback(callback).build();
		requestTokenCache = new TimedExpiringCache<>(requestTokenExpireTime);
	}

	@Override
	public Pair<String, URI> generateAuthorizeURI() {
		Token requestToken = oAuthService.getRequestToken();
		requestTokenCache.put(requestToken.getToken(), requestToken);
		try {
			return Pair.of(requestToken.getToken(), new URI(oAuthService.getAuthorizationUrl(requestToken)));
		} catch (URISyntaxException e) {
			LOG.error("Tried to build a wrong URI", e);
		}
		return null;
	}

	@Override
	public UserCredential retrieveCredential(String temporaryToken, String verifierValue) {
		Token requestToken = requestTokenCache.get(temporaryToken);
		if (requestToken != null) {
			Token accessToken = oAuthService.getAccessToken(requestToken, new Verifier(verifierValue));
			return responseParser.parse(accessToken.getRawResponse());
		}
		return null;
	}
}
