package com.molecode.w2k.oauth;

import com.molecode.w2k.models.UserCredential;

/**
 * Created by YP on 2016-01-08.
 */
public interface OAuthResponseParser {
	UserCredential parse(String rawResponse);
}
