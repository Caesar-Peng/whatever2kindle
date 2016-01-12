package com.molecode.w2k.services.impl;

import com.molecode.w2k.fetcher.ArticleFetcher;
import com.molecode.w2k.kindle.KindleGenerator;
import com.molecode.w2k.services.EmailService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by YP on 2015-12-28.
 */
public class ArticleTransferServiceImplTest {

	private static final File ORIGINAL_FILE = new File("original_file");
	private static final File KINDLE_FILE = new File("kindle_file");

	@Mock
	private KindleGenerator kindleGenerator;

	@Mock
	private EmailService emailService;

	@Mock
	private ArticleFetcher articleFetcher;

	private ArticleTransferServiceImpl articleTransferService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		articleTransferService = new ArticleTransferServiceImpl();
		articleTransferService.setKindleGenerator(kindleGenerator);
		articleTransferService.setEmailService(emailService);

	}

	@Test
	public void testTransferAndDeliverArticle() {
		when(articleFetcher.fetchArticle()).thenReturn(ORIGINAL_FILE);
		when(kindleGenerator.generate(ORIGINAL_FILE)).thenReturn(KINDLE_FILE);

		articleTransferService.transferAndDeliverArticle(articleFetcher);

		verify(articleFetcher).fetchArticle();
		verify(kindleGenerator).generate(ORIGINAL_FILE);
		verify(emailService).deliverArticle(KINDLE_FILE);

	}

	@Test
	public void testTransferAndDeliverArticleFetchFileFailed() {
		when(articleFetcher.fetchArticle()).thenReturn(null);

		articleTransferService.transferAndDeliverArticle(articleFetcher);

		verify(articleFetcher).fetchArticle();
		verify(kindleGenerator, never()).generate(any(File.class));
		verify(emailService, never()).deliverArticle(any(File.class));

	}

	@Test
	public void testTransferAndDeliverArticleGenerateKindleFileFailed() {
		when(articleFetcher.fetchArticle()).thenReturn(ORIGINAL_FILE);
		when(kindleGenerator.generate(ORIGINAL_FILE)).thenReturn(null);

		articleTransferService.transferAndDeliverArticle(articleFetcher);

		verify(articleFetcher).fetchArticle();
		verify(kindleGenerator).generate(ORIGINAL_FILE);
		verify(emailService, never()).deliverArticle(any(File.class));
	}

}