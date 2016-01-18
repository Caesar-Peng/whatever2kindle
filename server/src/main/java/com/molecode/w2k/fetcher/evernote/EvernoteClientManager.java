package com.molecode.w2k.fetcher.evernote;

import com.evernote.auth.EvernoteService;
import com.molecode.w2k.daos.UserCredentialDao;
import com.molecode.w2k.fetcher.ArticleSource;
import com.molecode.w2k.models.UserCredential;
import com.syncthemall.enml4j.ENMLProcessor;
import org.springframework.beans.factory.annotation.Required;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by YP on 2015-12-29.
 */
public class EvernoteClientManager {

	private static final String EVERNOTE_DIR = "temp_file/evernote/";

	private UserCredentialDao userCredentialDao;

	private EvernoteService evernoteService;

	private final ENMLProcessor enmlProcessor;

	public EvernoteClientManager() {
		enmlProcessor = new ENMLProcessor();
		File evernoteDir = new File(EVERNOTE_DIR);
		if (!evernoteDir.exists()) {
			evernoteDir.mkdirs();
		}
	}

	@Required
	public void setUserCredentialDao(UserCredentialDao userCredentialDao) {
		this.userCredentialDao = userCredentialDao;
	}

	@Required
	public void setEvernoteService(EvernoteService evernoteService) {
		this.evernoteService = evernoteService;
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
					client = new EvernoteClient(userCredential.getPassword(), evernoteService, EVERNOTE_DIR, enmlProcessor);
					clientMap.put(username, client);
				}
			}
		}
		return client;
	}
}
