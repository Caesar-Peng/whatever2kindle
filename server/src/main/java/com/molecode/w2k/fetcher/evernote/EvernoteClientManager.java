package com.molecode.w2k.fetcher.evernote;

import com.molecode.w2k.daos.UserCredentialDao;
import com.molecode.w2k.fetcher.ArticleSource;
import com.molecode.w2k.models.UserCredential;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by YP on 2015-12-29.
 */
public class EvernoteClientManager {

	private UserCredentialDao userCredentialDao;

	public void setUserCredentialDao(UserCredentialDao userCredentialDao) {
		this.userCredentialDao = userCredentialDao;
	}

	private Map<String, EvernoteClient> clientMap = new HashMap<>();

	public EvernoteClient getEvernoteClient(String username) {
		EvernoteClient client = null;
		synchronized (this) {
			if (clientMap.containsKey(username)) {
				client = clientMap.get(username);
			} else {
				UserCredential userCredential = userCredentialDao.loadUserCredential(ArticleSource.EVERNOTE, username);
				if (userCredential != null) {
					client = new EvernoteClient(userCredential.getUsername(), userCredential.getPassword());
					clientMap.put(username, client);
				}
			}
		}
		return client;
	}
}
