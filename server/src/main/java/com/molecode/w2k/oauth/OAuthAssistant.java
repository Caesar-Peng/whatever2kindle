package com.molecode.w2k.oauth;

import com.molecode.w2k.models.UserCredential;

import java.net.URI;

/**
 * Created by YP on 2016-01-07.
 */
public interface OAuthAssistant {
	URI generateAuthorizeURI();

	UserCredential retrieveCredential(String temporaryToken, String verifierValue);
}
