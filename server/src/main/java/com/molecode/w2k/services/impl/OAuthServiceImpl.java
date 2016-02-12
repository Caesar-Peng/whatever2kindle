package com.molecode.w2k.services.impl;

import com.molecode.w2k.daos.UserCredentialDao;
import com.molecode.w2k.models.User;
import com.molecode.w2k.models.UserCredential;
import com.molecode.w2k.oauth.OAuthAssistant;
import com.molecode.w2k.oauth.TimedExpiringCache;
import com.molecode.w2k.services.OAuthService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Required;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by YP on 2016-01-06.
 */
public class OAuthServiceImpl implements OAuthService {

	private UserCredentialDao userCredentialDao;

	private OAuthAssistant oAuthAssistant;

	private TimedExpiringCache<String, User> tokenUserIdCache;

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
	public URI generateOAuthRequestURI(String kindleEmail, String w2kTag) {
		Pair<String, URI> authorizeTokenPair = oAuthAssistant.generateAuthorizeURI();
		if (authorizeTokenPair != null) {
			User user = new User();
			user.setKindleEmail(kindleEmail);
			user.setW2kTag(w2kTag);
			tokenUserIdCache.put(authorizeTokenPair.getKey(), user);
			return authorizeTokenPair.getValue();
		}
		return null;
	}

	@Override
	public AuthorizationResult retrieveAndStoreAccessToken(String temporaryToken, String verifierValue) {
		User cachedUser = tokenUserIdCache.get(temporaryToken);
		if (cachedUser != null) {
			UserCredential userCredential = oAuthAssistant.retrieveCredential(temporaryToken, verifierValue);
			if (userCredential != null) {
				Integer userId = userCredentialDao.insertUserOrSelectUserId(cachedUser);
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
