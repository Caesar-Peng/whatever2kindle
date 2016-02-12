package com.molecode.w2k.services;

import java.net.URI;

/**
 * Created by YP on 2016-01-06.
 */
public interface OAuthService {
	URI generateOAuthRequestURI(String kindleEmail, String w2kTag);

	AuthorizationResult retrieveAndStoreAccessToken(String temporaryToken, String verifierValue);

	final class AuthorizationResult {

		private final boolean succeed;

		private final String message;

		public AuthorizationResult(boolean succeed) {
			this.succeed = succeed;
			this.message = null;
		}

		public AuthorizationResult(boolean succeed, String message) {
			this.succeed = succeed;
			this.message = message;
		}

		public boolean isSucceed() {
			return succeed;
		}

		public String getMessage() {
			return message;
		}
	}
}
