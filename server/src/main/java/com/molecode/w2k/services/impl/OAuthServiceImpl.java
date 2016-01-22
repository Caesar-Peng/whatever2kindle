package com.molecode.w2k.services.impl;

import com.github.scribejava.core.model.Token;
import com.molecode.w2k.daos.UserCredentialDao;
import com.molecode.w2k.models.User;
import com.molecode.w2k.models.UserCredential;
import com.molecode.w2k.oauth.OAuthAssistant;
import com.molecode.w2k.oauth.TimedExpiringCache;
import com.molecode.w2k.services.OAuthService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Required;

import java.net.URI;

/**
 * Created by YP on 2016-01-06.
 */
public class OAuthServiceImpl implements OAuthService {

	private UserCredentialDao userCredentialDao;

	private OAuthAssistant oAuthAssistant;

	private TimedExpiringCache<String, String> tokenUserIdCache;

	private long requestTokenExpireTime = 10 * 60 * 1000;

	public OAuthServiceImpl() {
		tokenUserIdCache = new TimedExpiringCache<>(requestTokenExpireTime);
	}

	@Required
	public void setoAuthAssistant(OAuthAssistant oAuthAssistant) {
		this.oAuthAssistant = oAuthAssistant;
	}

	@Required
	public void setUserCredentialDao(UserCredentialDao userCredentialDao) {
		this.userCredentialDao = userCredentialDao;
	}

	@Override
	public URI generateOAuthRequestURI(String kindleEmail) {
		Pair<String, URI> authorizeTokenPair = oAuthAssistant.generateAuthorizeURI();
		if (authorizeTokenPair != null) {
			tokenUserIdCache.put(authorizeTokenPair.getKey(), kindleEmail);
			return authorizeTokenPair.getValue();
		}
		return null;
	}

	@Override
	public AuthorizationResult retrieveAndStoreAccessToken(String temporaryToken, String verifierValue) {
		String kindleEmail = tokenUserIdCache.get(temporaryToken);
		if (kindleEmail != null) {
			UserCredential userCredential = oAuthAssistant.retrieveCredential(temporaryToken, verifierValue);
			if (userCredential != null) {
				User user = new User();
				user.setKindleEmail(kindleEmail);
				Integer userId = userCredentialDao.insertUserOrSelectUserId(user);
				userCredential.setUserId(userId);
				try {
					userCredentialDao.insertUserCredential(userCredential);
				} catch (Exception e) {
					return new AuthorizationResult(false, "Failed to store user credential.");
				}
				return new AuthorizationResult(true);
			} else {
				return new AuthorizationResult(false, "Failed to retrieve user credential.");
			}

		} else {
			return new AuthorizationResult(false, "Authorization request is expired.");
		}
	}

}
