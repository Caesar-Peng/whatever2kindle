package com.molecode.w2k.services.impl;

import com.molecode.w2k.daos.UserCredentialDao;
import com.molecode.w2k.fetcher.ArticleFetcher;
import com.molecode.w2k.fetcher.ArticleSource;
import com.molecode.w2k.kindle.KindleGenerator;
import com.molecode.w2k.services.EmailService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;

import static com.molecode.w2k.TestConstants.KINDLE_EMAIL_ADDRESS;
import static com.molecode.w2k.TestConstants.USERNAME;
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

	@Mock
	private UserCredentialDao userCredentialDao;

	private ArticleTransferServiceImpl articleTransferService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		articleTransferService = new ArticleTransferServiceImpl();
		articleTransferService.setKindleGenerator(kindleGenerator);
		articleTransferService.setEmailService(emailService);
		articleTransferService.setUserCredentialDao(userCredentialDao);

	}

	@Test
	public void testTransferAndDeliverArticle() {
		when(articleFetcher.fetchArticle()).thenReturn(ORIGINAL_FILE);
		when(kindleGenerator.generate(ORIGINAL_FILE)).thenReturn(KINDLE_FILE);
		when(userCredentialDao.queryKindleEmail(any(ArticleSource.class), eq(USERNAME))).thenReturn(KINDLE_EMAIL_ADDRESS);

		articleTransferService.transferAndDeliverArticle(ArticleSource.EVERNOTE, USERNAME, articleFetcher);

		verify(articleFetcher).fetchArticle();
		verify(kindleGenerator).generate(ORIGINAL_FILE);
		verify(userCredentialDao).queryKindleEmail(ArticleSource.EVERNOTE, USERNAME);
		verify(emailService).deliverArticle(KINDLE_EMAIL_ADDRESS, KINDLE_FILE);
	}

	@Test
	public void testTransferAndDeliverArticleFetchFileFailed() {
		when(articleFetcher.fetchArticle()).thenReturn(null);

		articleTransferService.transferAndDeliverArticle(ArticleSource.EVERNOTE, USERNAME, articleFetcher);

		verify(articleFetcher).fetchArticle();
		verify(kindleGenerator, never()).generate(any(File.class));
		verify(userCredentialDao, never()).queryKindleEmail(eq(ArticleSource.EVERNOTE), any(String.class));
		verify(emailService, never()).deliverArticle(any(String.class), any(File.class));

	}

	@Test
	public void testTransferAndDeliverArticleGenerateKindleFileFailed() {
		when(articleFetcher.fetchArticle()).thenReturn(ORIGINAL_FILE);
		when(kindleGenerator.generate(ORIGINAL_FILE)).thenReturn(null);

		articleTransferService.transferAndDeliverArticle(ArticleSource.EVERNOTE, USERNAME, articleFetcher);

		verify(articleFetcher).fetchArticle();
		verify(kindleGenerator).generate(ORIGINAL_FILE);
		verify(userCredentialDao, never()).queryKindleEmail(any(ArticleSource.class), any(String.class));
		verify(emailService, never()).deliverArticle(any(String.class), any(File.class));
	}

	@Test
	public void testTransferAndDeliverArticleQueryKindleEmailFailed() {
		when(articleFetcher.fetchArticle()).thenReturn(ORIGINAL_FILE);
		when(kindleGenerator.generate(ORIGINAL_FILE)).thenReturn(KINDLE_FILE);
		when(userCredentialDao.queryKindleEmail(ArticleSource.EVERNOTE, USERNAME)).thenReturn(null);

		articleTransferService.transferAndDeliverArticle(ArticleSource.EVERNOTE, USERNAME, articleFetcher);

		verify(articleFetcher).fetchArticle();
		verify(kindleGenerator).generate(ORIGINAL_FILE);
		verify(userCredentialDao).queryKindleEmail(ArticleSource.EVERNOTE, USERNAME);
		verify(emailService, never()).deliverArticle(any(String.class), any(File.class));
	}

}