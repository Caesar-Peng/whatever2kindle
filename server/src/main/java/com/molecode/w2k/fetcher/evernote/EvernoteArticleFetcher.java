package com.molecode.w2k.fetcher.evernote;

import com.molecode.w2k.fetcher.ArticleFetcher;
import com.molecode.w2k.fetcher.ArticleWriter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by YP on 2015-12-29.
 */
public class EvernoteArticleFetcher implements ArticleFetcher {

	private static final String EVERNOTE_DIR = "temp_file/evernote/";

	private final String username;

	private final String noteGuid;

	private final EvernoteClient evernoteClient;

	private final ArticleWriter articleWriter;

	public EvernoteArticleFetcher(String username, String noteGuid, EvernoteClient evernoteClient, ArticleWriter articleWriter) {
		this.username = username;
		this.noteGuid = noteGuid;
		this.evernoteClient = evernoteClient;
		this.articleWriter = articleWriter;
		File evernoteDir = new File(EVERNOTE_DIR);
		if (!evernoteDir.exists()) {
			evernoteDir.mkdirs();
		}
	}

	@Override
	public File fetchArticle() {
		String articleContent = evernoteClient.fetchNoteContent(noteGuid);
		if (StringUtils.isNotBlank(articleContent)) {
			File articleFile = new File(EVERNOTE_DIR + noteGuid + ".html");
			try {
				articleWriter.write(articleFile, articleContent);
			} catch (IOException e) {
				return null;
			}
			return articleFile;
		}
		return null;
	}
}
