package com.molecode.w2k.services.impl;

import com.molecode.w2k.daos.UserCredentialDao;
import com.molecode.w2k.fetcher.ArticleSource;
import com.molecode.w2k.models.UserCredential;
import com.molecode.w2k.oauth.OAuthAssistant;
import com.molecode.w2k.services.OAuthService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.URI;
import java.net.URISyntaxException;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static com.molecode.w2k.TestConstants.*;

/**
 * Created by YP on 2016-01-06.
 */
public class OAuthServiceImplTest {

	@Mock
	private OAuthAssistant oAuthAssistant;

	@Mock
	private UserCredentialDao userCredentialDao;

	private OAuthServiceImpl oAuthService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		oAuthService = new OAuthServiceImpl();
		oAuthService.setoAuthAssistant(oAuthAssistant);
		oAuthService.setUserCredentialDao(userCredentialDao);
	}

	@Test
	public void testGenerateOAuthRequestURI() throws URISyntaxException {
		when(oAuthAssistant.generateAuthorizeURI()).thenReturn(new URI(OAUTH_REQUEST_URI));

		URI requestURI = oAuthService.generateOAuthRequestURI();

		verify(oAuthAssistant).generateAuthorizeURI();
		assertEquals(OAUTH_REQUEST_URI, requestURI.toString());
	}

	@Test
	public void testGenerateOAuthRequestURIFailed() {
		when(oAuthAssistant.generateAuthorizeURI()).thenReturn(null);

		URI requestURI = oAuthService.generateOAuthRequestURI();

		verify(oAuthAssistant).generateAuthorizeURI();
		assertNull(requestURI);
	}

	@Test
	public void testRetrieveAndStoreCredential() throws Exception {
		UserCredential userCredential = new UserCredential(USER_ID, ArticleSource.EVERNOTE, USERNAME, PASSWORD);
		when(oAuthAssistant.retrieveCredential(OAUTH_TEMPORARY_TOKEN, OAUTH_VERIFIER_VALUE)).thenReturn(userCredential);

		OAuthService.AuthorizationResult result = oAuthService.retrieveAndStoreAccessToken(OAUTH_TEMPORARY_TOKEN, OAUTH_VERIFIER_VALUE);

		verify(oAuthAssistant).retrieveCredential(OAUTH_TEMPORARY_TOKEN, OAUTH_VERIFIER_VALUE);
		verify(userCredentialDao).insertUserCredential(userCredential);

		assertTrue(result.isSucceed());
		assertNull(result.getMessage());
	}

	@Test
	public void testRetrieveAndStoreCredentialFailedRetrieve() throws Exception {
		when(oAuthAssistant.retrieveCredential(OAUTH_TEMPORARY_TOKEN, OAUTH_VERIFIER_VALUE)).thenReturn(null);

		OAuthService.AuthorizationResult result = oAuthService.retrieveAndStoreAccessToken(OAUTH_TEMPORARY_TOKEN, OAUTH_VERIFIER_VALUE);

		verify(oAuthAssistant).retrieveCredential(OAUTH_TEMPORARY_TOKEN, OAUTH_VERIFIER_VALUE);
		verify(userCredentialDao, never()).insertUserCredential(any(UserCredential.class));

		assertFalse(result.isSucceed());
		assertNotNull(result.getMessage());

	}

	@Test
	public void testRetrieveAndStoreCredentialFailedStore() throws Exception {
		UserCredential userCredential = new UserCredential(USER_ID, ArticleSource.EVERNOTE, USERNAME, PASSWORD);
		when(oAuthAssistant.retrieveCredential(OAUTH_TEMPORARY_TOKEN, OAUTH_VERIFIER_VALUE)).thenReturn(userCredential);
		doThrow(new Exception()).when(userCredentialDao).insertUserCredential(userCredential);

		OAuthService.AuthorizationResult result = oAuthService.retrieveAndStoreAccessToken(OAUTH_TEMPORARY_TOKEN, OAUTH_VERIFIER_VALUE);

		verify(oAuthAssistant).retrieveCredential(OAUTH_TEMPORARY_TOKEN, OAUTH_VERIFIER_VALUE);
		verify(userCredentialDao).insertUserCredential(userCredential);

		assertFalse(result.isSucceed());
		assertNotNull(result.getMessage());
	}

}