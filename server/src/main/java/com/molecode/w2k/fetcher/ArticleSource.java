package com.molecode.w2k.fetcher;

/**
 * Created by YP on 2015-12-30.
 */
public enum ArticleSource {
	EVERNOTE("evernote");

	private final String sourceId;

	ArticleSource (String sourceId) {
		this.sourceId = sourceId;
	}

	public String getSourceId() {
		return sourceId;
	}
}
