package com.molecode.w2k.daos;

import com.molecode.w2k.fetcher.ArticleSource;
import com.molecode.w2k.models.UserCredential;

/**
 * Created by YP on 2015-12-30.
 */
public interface UserCredentialDao {
	UserCredential loadUserCredential(ArticleSource evernote, String username);

	void insertUserCredential(UserCredential userCredential) throws Exception;
}
