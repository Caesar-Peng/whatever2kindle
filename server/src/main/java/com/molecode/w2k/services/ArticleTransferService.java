package com.molecode.w2k.services;

import com.molecode.w2k.fetcher.ArticleFetcher;

/**
 * Created by YP on 2015-12-26.
 */
public interface ArticleTransferService {
	void transferAndDeliverArticle(ArticleFetcher articleFetcher);
}
