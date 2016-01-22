package com.molecode.w2k.fetcher.evernote;

import com.evernote.auth.EvernoteService;
import com.molecode.w2k.daos.UserCredentialDao;
import com.molecode.w2k.fetcher.ArticleSource;
import com.molecode.w2k.models.UserCredential;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static com.molecode.w2k.TestConstants.*;

/**
 * Created by YP on 2015-12-30.
 */
public class EvernoteClientManagerTest {

	private EvernoteClientManager evernoteClientManager;

	@Mock
	private UserCredentialDao userCredentialDao;

	private UserCredential userCredential;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		evernoteClientManager = new EvernoteClientManager();
		evernoteClientManager.setUserCredentialDao(userCredentialDao);
		evernoteClientManager.setEvernoteService(EvernoteService.SANDBOX);
		userCredential = new UserCredential(USER_CREDENTIAL_ID, ArticleSource.EVERNOTE, USERNAME, PASSWORD);
	}

	@Test
	public void testGetEvernoteClientForFirstTime() {
		when(userCredentialDao.loadUserCredential(ArticleSource.EVERNOTE, USERNAME)).thenReturn(userCredential);

		EvernoteClient evernoteClient = evernoteClientManager.getEvernoteClient(USERNAME);

		verify(userCredentialDao).loadUserCredential(ArticleSource.EVERNOTE, USERNAME);

		assertNotNull(evernoteClient);
	}

	@Test
	public void testGetSameValidEvernoteClientTwice() {
		when(userCredentialDao.loadUserCredential(ArticleSource.EVERNOTE, USERNAME)).thenReturn(userCredential);

		EvernoteClient firstClient = evernoteClientManager.getEvernoteClient(USERNAME);
		EvernoteClient secondClient = evernoteClientManager.getEvernoteClient(USERNAME);

		verify(userCredentialDao, times(1)).loadUserCredential(ArticleSource.EVERNOTE, USERNAME);

		assertNotNull(firstClient);
		assertNotNull(secondClient);
		assertEquals(firstClient, secondClient);
	}

	@Test
	public void testGetInvalidEvernoteClientDueToNullCredential() {
		when(userCredentialDao.loadUserCredential(ArticleSource.EVERNOTE, USERNAME)).thenReturn(null);

		EvernoteClient client = evernoteClientManager.getEvernoteClient(USERNAME);

		assertNull(client);
	}

}