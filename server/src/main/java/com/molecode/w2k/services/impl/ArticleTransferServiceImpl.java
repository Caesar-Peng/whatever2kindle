package com.molecode.w2k.services.impl;

import com.molecode.w2k.daos.UserCredentialDao;
import com.molecode.w2k.fetcher.ArticleFetcher;
import com.molecode.w2k.fetcher.ArticleSource;
import com.molecode.w2k.kindle.KindleGenerator;
import com.molecode.w2k.services.ArticleTransferService;
import com.molecode.w2k.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.io.File;

/**
 * Created by YP on 2015-12-28.
 */
public class ArticleTransferServiceImpl implements ArticleTransferService {

	private static final Logger LOG = LoggerFactory.getLogger(ArticleTransferServiceImpl.class);

	private KindleGenerator kindleGenerator;

	private EmailService emailService;

	private UserCredentialDao userCredentialDao;

	@Required
	public void setKindleGenerator(KindleGenerator kindleGenerator) {
		this.kindleGenerator = kindleGenerator;
	}

	@Required
	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

	@Required
	public void setUserCredentialDao(UserCredentialDao userCredentialDao) {
		this.userCredentialDao = userCredentialDao;
	}

	@Override
	public void transferAndDeliverArticle(ArticleSource articleSource, String username, ArticleFetcher articleFetcher) {
		File originalFile = articleFetcher.fetchArticle();
		if (originalFile != null) {
			LOG.info("Successfully fetched article content with ArticleFetcher.");
			File kindleFile = kindleGenerator.generate(originalFile);
			if (kindleFile != null) {
				String kindleEmail = userCredentialDao.queryKindleEmail(articleSource, username);
				if (kindleEmail != null) {
					emailService.deliverArticle(kindleEmail, kindleFile);
				}
			}
		}
	}

}
