package com.molecode.w2k.fetcher;

import java.io.File;

/**
 * Created by YP on 2015-12-26.
 */
public interface ArticleFetcher {
	File fetchArticle(String w2kTag);
}
