package com.molecode.w2k.services.impl;

import com.molecode.w2k.daos.UserCredentialDao;
import com.molecode.w2k.models.UserCredential;
import com.molecode.w2k.oauth.OAuthAssistant;
import com.molecode.w2k.services.OAuthService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.net.URI;

/**
 * Created by YP on 2016-01-06.
 */
public class OAuthServiceImpl implements OAuthService {

	private UserCredentialDao userCredentialDao;

	private OAuthAssistant oAuthAssistant;

	@Required
	public void setoAuthAssistant(OAuthAssistant oAuthAssistant) {
		this.oAuthAssistant = oAuthAssistant;
	}

	@Required
	public void setUserCredentialDao(UserCredentialDao userCredentialDao) {
		this.userCredentialDao = userCredentialDao;
	}

	@Override
	public URI generateOAuthRequestURI() {
		return oAuthAssistant.generateAuthorizeURI();
	}

	@Override
	public AuthorizationResult retrieveAndStoreAccessToken(String temporaryToken, String verifierValue) {
		UserCredential userCredential = oAuthAssistant.retrieveCredential(temporaryToken, verifierValue);
		if (userCredential != null) {
			try {
				userCredentialDao.insertUserCredential(userCredential);
			} catch (Exception e) {
				return new AuthorizationResult(false, "Failed to store user credential.");
			}
			return new AuthorizationResult(true);
		} else {
			return new AuthorizationResult(false, "Failed to retrieve user credential.");
		}
	}

}
