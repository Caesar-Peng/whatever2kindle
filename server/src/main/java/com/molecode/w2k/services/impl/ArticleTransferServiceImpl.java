package com.molecode.w2k.services.impl;

import com.molecode.w2k.fetcher.ArticleFetcher;
import com.molecode.w2k.kindle.KindleGenerator;
import com.molecode.w2k.services.ArticleTransferService;
import com.molecode.w2k.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

/**
 * Created by YP on 2015-12-28.
 */
public class ArticleTransferServiceImpl implements ArticleTransferService {

	private KindleGenerator kindleGenerator;

	private EmailService emailService;

	public void setKindleGenerator(KindleGenerator kindleGenerator) {
		this.kindleGenerator = kindleGenerator;
	}

	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

	@Override
	public void transferAndDeliverArticle(ArticleFetcher articleFetcher) {
		File originalFile = articleFetcher.fetchArticle();
		if (originalFile != null) {
			File kindleFile = kindleGenerator.generate(originalFile);
			if (kindleFile != null) {
				emailService.deliverArticle(kindleFile);
			}
		}
	}

}
