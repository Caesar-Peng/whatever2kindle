package com.molecode.w2k.fetcher.evernote;

import com.molecode.w2k.fetcher.ArticleFetcher;

import java.io.File;

/**
 * Created by YP on 2015-12-29.
 */
public class EvernoteArticleFetcher implements ArticleFetcher {

	private final String username;

	private final String noteGuid;

	private final EvernoteClient evernoteClient;

	public EvernoteArticleFetcher(String username, String noteGuid, EvernoteClient evernoteClient) {
		this.username = username;
		this.noteGuid = noteGuid;
		this.evernoteClient = evernoteClient;
	}

	@Override
	public File fetchArticle() {
		return  evernoteClient.fetchNoteContent(noteGuid);
	}
}
