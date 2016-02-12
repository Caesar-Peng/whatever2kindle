package com.molecode.w2k.services.impl;

import com.molecode.w2k.daos.UserCredentialDao;
import com.molecode.w2k.fetcher.ArticleSource;
import com.molecode.w2k.models.User;
import com.molecode.w2k.models.UserCredential;
import com.molecode.w2k.oauth.OAuthAssistant;
import com.molecode.w2k.services.OAuthService;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
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
		when(oAuthAssistant.generateAuthorizeURI()).thenReturn(Pair.of(OAUTH_TEMPORARY_TOKEN, new URI(OAUTH_REQUEST_URI)));

		URI requestURI = oAuthService.generateOAuthRequestURI(KINDLE_EMAIL_ADDRESS, W2K_TAG);

		verify(oAuthAssistant).generateAuthorizeURI();
		assertEquals(OAUTH_REQUEST_URI, requestURI.toString());
	}

	@Test
	public void testGenerateOAuthRequestURIFailedToGenerateURI() {
		when(userCredentialDao.insertUserOrSelectUserId(any(User.class))).thenReturn(USER_ID);
		when(oAuthAssistant.generateAuthorizeURI()).thenReturn(null);

		URI requestURI = oAuthService.generateOAuthRequestURI(KINDLE_EMAIL_ADDRESS, W2K_TAG);

		verify(oAuthAssistant).generateAuthorizeURI();
		assertNull(requestURI);
	}

	@Test
	public void testRetrieveAndStoreCredential() throws Exception {
		generateOAuthRequestURIForRetrieveCredential();

		ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
		when(userCredentialDao.insertUserOrSelectUserId(any(User.class))).thenReturn(USER_ID);
		UserCredential userCredential = new UserCredential(USER_CREDENTIAL_ID, ArticleSource.EVERNOTE, USERNAME, PASSWORD);
		when(oAuthAssistant.retrieveCredential(OAUTH_TEMPORARY_TOKEN, OAUTH_VERIFIER_VALUE)).thenReturn(userCredential);

		OAuthService.AuthorizationResult result = oAuthService.retrieveAndStoreAccessToken(OAUTH_TEMPORARY_TOKEN, OAUTH_VERIFIER_VALUE);

		verify(userCredentialDao).insertUserCredential(userCredential);
		verify(userCredentialDao).insertUserOrSelectUserId(userArgumentCaptor.capture());

		User user = userArgumentCaptor.getValue();
		assertEquals(KINDLE_EMAIL_ADDRESS, user.getKindleEmail());
		assertEquals(W2K_TAG, user.getW2kTag());
		assertEquals(USER_ID, userCredential.getUserId());
		assertTrue(result.isSucceed());
		assertNull(result.getMessage());
	}

	private void generateOAuthRequestURIForRetrieveCredential() throws URISyntaxException {
		URI authorizationURI = new URI(OAUTH_REQUEST_URI);
		when(oAuthAssistant.generateAuthorizeURI()).thenReturn(Pair.of(OAUTH_TEMPORARY_TOKEN, authorizationURI));
		oAuthService.generateOAuthRequestURI(KINDLE_EMAIL_ADDRESS, W2K_TAG);
	}

	@Test
	public void testRetrieveAndStoreCredentialFailedRetrieve() throws Exception {
		generateOAuthRequestURIForRetrieveCredential();
		when(oAuthAssistant.retrieveCredential(OAUTH_TEMPORARY_TOKEN, OAUTH_VERIFIER_VALUE)).thenReturn(null);

		OAuthService.AuthorizationResult result = oAuthService.retrieveAndStoreAccessToken(OAUTH_TEMPORARY_TOKEN, OAUTH_VERIFIER_VALUE);

		verify(oAuthAssistant).retrieveCredential(OAUTH_TEMPORARY_TOKEN, OAUTH_VERIFIER_VALUE);
		verify(userCredentialDao, never()).insertUserOrSelectUserId(any(User.class));
		verify(userCredentialDao, never()).insertUserCredential(any(UserCredential.class));

		assertFalse(result.isSucceed());
		assertNotNull(result.getMessage());

	}

	@Test
	public void testRetrieveAndStoreCredentialFailedStore() throws Exception {
		generateOAuthRequestURIForRetrieveCredential();
		UserCredential userCredential = new UserCredential(USER_CREDENTIAL_ID, ArticleSource.EVERNOTE, USERNAME, PASSWORD);
		when(oAuthAssistant.retrieveCredential(OAUTH_TEMPORARY_TOKEN, OAUTH_VERIFIER_VALUE)).thenReturn(userCredential);
		doThrow(new Exception()).when(userCredentialDao).insertUserCredential(userCredential);

		OAuthService.AuthorizationResult result = oAuthService.retrieveAndStoreAccessToken(OAUTH_TEMPORARY_TOKEN, OAUTH_VERIFIER_VALUE);

		verify(oAuthAssistant).retrieveCredential(OAUTH_TEMPORARY_TOKEN, OAUTH_VERIFIER_VALUE);
		verify(userCredentialDao).insertUserCredential(userCredential);

		assertFalse(result.isSucceed());
		assertNotNull(result.getMessage());
	}

}