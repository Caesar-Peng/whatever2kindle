package com.molecode.w2k.resources;

import com.molecode.w2k.services.OAuthService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static com.molecode.w2k.TestConstants.*;

/**
 * Created by YP on 2016-01-06.
 */
public class EvernoteOAuthResourceTest {

	@Mock
	private OAuthService oAuthService;

	private EvernoteOAuthResource evernoteOAuthResource;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		evernoteOAuthResource = new EvernoteOAuthResource();
		evernoteOAuthResource.setOAuthService(oAuthService);

	}

	@Test
	public void testRequestAuthorization() throws URISyntaxException {

		when(oAuthService.generateOAuthRequestURI(KINDLE_EMAIL_ADDRESS)).thenReturn(new URI(OAUTH_REQUEST_URI));

		Response response = evernoteOAuthResource.requestAuthorization(KINDLE_EMAIL_ADDRESS);

		verify(oAuthService).generateOAuthRequestURI(KINDLE_EMAIL_ADDRESS);

		assertEquals(Response.Status.MOVED_PERMANENTLY.getStatusCode(), response.getStatus());
		assertEquals("http://evernote.com/oauth", response.getHeaderString("Location"));
	}

	@Test
	public void testReceivedAuthorizationCallback() {
		OAuthService.AuthorizationResult authorizationResult = new OAuthService.AuthorizationResult(true);
		when(oAuthService.retrieveAndStoreAccessToken(OAUTH_TEMPORARY_TOKEN, OAUTH_VERIFIER_VALUE)).thenReturn(authorizationResult);

		Response response = evernoteOAuthResource.receivedAuthorizationCallback(OAUTH_TEMPORARY_TOKEN, OAUTH_VERIFIER_VALUE);

		verify(oAuthService).retrieveAndStoreAccessToken(OAUTH_TEMPORARY_TOKEN, OAUTH_VERIFIER_VALUE);

		assertEquals(Response.Status.MOVED_PERMANENTLY.getStatusCode(), response.getStatus());
		assertEquals("/w2k/oauth_result.html#succeed=true", response.getHeaderString("Location"));

	}

	@Test
	public void testReceivedAuthorizationCallbackFailed() {
		OAuthService.AuthorizationResult authorizationResult = new OAuthService.AuthorizationResult(false, "message");
		when(oAuthService.retrieveAndStoreAccessToken(OAUTH_TEMPORARY_TOKEN, OAUTH_VERIFIER_VALUE)).thenReturn(authorizationResult);

		Response response = evernoteOAuthResource.receivedAuthorizationCallback(OAUTH_TEMPORARY_TOKEN, OAUTH_VERIFIER_VALUE);

		verify(oAuthService).retrieveAndStoreAccessToken(OAUTH_TEMPORARY_TOKEN, OAUTH_VERIFIER_VALUE);

		assertEquals(Response.Status.MOVED_PERMANENTLY.getStatusCode(), response.getStatus());
		assertEquals("/w2k/oauth_result.html#succeed=false&message=message", response.getHeaderString("Location"));

	}

}