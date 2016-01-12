package com.molecode.w2k.oauth;

import com.evernote.auth.EvernoteAuth;
import com.evernote.auth.EvernoteService;
import com.molecode.w2k.fetcher.ArticleSource;
import com.molecode.w2k.models.UserCredential;
import org.springframework.beans.factory.annotation.Required;

/**
 * Created by YP on 2016-01-11.
 */
public class EvernoteOAuthResponseParser implements OAuthResponseParser {

	private EvernoteService evernoteService;

	@Required
	public void setEvernoteService(EvernoteService evernoteService) {
		this.evernoteService = evernoteService;
	}

	@Override
	public UserCredential parse(String rawResponse) {
		EvernoteAuth evernoteAuth = EvernoteAuth.parseOAuthResponse(evernoteService, rawResponse);
		return new UserCredential(null, ArticleSource.EVERNOTE, evernoteAuth.getUserId() + "", evernoteAuth.getToken());
	}
}
