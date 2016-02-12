package com.molecode.w2k.services.impl;

import com.molecode.w2k.daos.UserCredentialDao;
import com.molecode.w2k.fetcher.ArticleFetcher;
import com.molecode.w2k.fetcher.ArticleSource;
import com.molecode.w2k.kindle.KindleGenerator;
import com.molecode.w2k.models.User;
import com.molecode.w2k.services.EmailService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;

import static com.molecode.w2k.TestConstants.KINDLE_EMAIL_ADDRESS;
import static com.molecode.w2k.TestConstants.USERNAME;
import static com.molecode.w2k.TestConstants.W2K_TAG;
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

	private User user;

	private ArticleTransferServiceImpl articleTransferService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		articleTransferService = new ArticleTransferServiceImpl();
		articleTransferService.setKindleGenerator(kindleGenerator);
		articleTransferService.setEmailService(emailService);
		articleTransferService.setUserCredentialDao(userCredentialDao);

		user = new User();
		user.setKindleEmail(KINDLE_EMAIL_ADDRESS);
		user.setW2kTag(W2K_TAG);

	}

	@Test
	public void testTransferAndDeliverArticle() {
		when(userCredentialDao.queryUserByCredential(ArticleSource.EVERNOTE, USERNAME)).thenReturn(user);
		when(articleFetcher.fetchArticle(W2K_TAG)).thenReturn(ORIGINAL_FILE);
		when(kindleGenerator.generate(ORIGINAL_FILE)).thenReturn(KINDLE_FILE);

		articleTransferService.transferAndDeliverArticle(ArticleSource.EVERNOTE, USERNAME, articleFetcher);

		verify(userCredentialDao).queryUserByCredential(ArticleSource.EVERNOTE, USERNAME);
		verify(articleFetcher).fetchArticle(W2K_TAG);
		verify(kindleGenerator).generate(ORIGINAL_FILE);
		verify(emailService).deliverArticle(KINDLE_EMAIL_ADDRESS, KINDLE_FILE);
	}

	@Test
	public void testTransferAndDeliverArticleFetchFileFailed() {
		when(articleFetcher.fetchArticle(W2K_TAG)).thenReturn(null);

		articleTransferService.transferAndDeliverArticle(ArticleSource.EVERNOTE, USERNAME, articleFetcher);

		verify(articleFetcher, never()).fetchArticle(W2K_TAG);
		verify(kindleGenerator, never()).generate(any(File.class));
		verify(emailService, never()).deliverArticle(any(String.class), any(File.class));

	}

	@Test
	public void testTransferAndDeliverArticleGenerateKindleFileFailed() {
		when(userCredentialDao.queryUserByCredential(ArticleSource.EVERNOTE, USERNAME)).thenReturn(user);
		when(articleFetcher.fetchArticle(W2K_TAG)).thenReturn(ORIGINAL_FILE);
		when(kindleGenerator.generate(ORIGINAL_FILE)).thenReturn(null);

		articleTransferService.transferAndDeliverArticle(ArticleSource.EVERNOTE, USERNAME, articleFetcher);

		verify(userCredentialDao).queryUserByCredential(ArticleSource.EVERNOTE, USERNAME);
		verify(articleFetcher).fetchArticle(W2K_TAG);
		verify(kindleGenerator).generate(ORIGINAL_FILE);
		verify(emailService, never()).deliverArticle(any(String.class), any(File.class));
	}

	@Test
	public void testTransferAndDeliverArticleQueryUserFailed() {
		when(userCredentialDao.queryUserByCredential(ArticleSource.EVERNOTE, USERNAME)).thenReturn(null);
		when(articleFetcher.fetchArticle(W2K_TAG)).thenReturn(ORIGINAL_FILE);
		when(kindleGenerator.generate(ORIGINAL_FILE)).thenReturn(KINDLE_FILE);

		articleTransferService.transferAndDeliverArticle(ArticleSource.EVERNOTE, USERNAME, articleFetcher);

		verify(userCredentialDao).queryUserByCredential(ArticleSource.EVERNOTE, USERNAME);
		verify(articleFetcher, never()).fetchArticle(W2K_TAG);
		verify(kindleGenerator, never()).generate(ORIGINAL_FILE);
		verify(emailService, never()).deliverArticle(any(String.class), any(File.class));
	}

}