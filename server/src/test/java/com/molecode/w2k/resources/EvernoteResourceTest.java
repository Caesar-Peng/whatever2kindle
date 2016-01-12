package com.molecode.w2k.resources;

import com.molecode.w2k.fetcher.ArticleFetcher;
import com.molecode.w2k.fetcher.evernote.EvernoteArticleFetcher;
import com.molecode.w2k.fetcher.evernote.EvernoteClient;
import com.molecode.w2k.fetcher.evernote.EvernoteClientManager;
import com.molecode.w2k.services.ArticleTransferService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static com.molecode.w2k.TestConstants.*;

/**
 * Created by YP on 2015-12-19.
 */
public class EvernoteResourceTest {

	private EvernoteResource evernoteResource;


	private static final String NOTEBOOK_GUID = "CONSTANT_NOTEBOOK_GUID";
	private static final String NOTIFICATION_REASON_CREATE_NOTE = "create";
	private static final String NOTIFICATION_REASON_OTHERS = "other_reason";

	@Mock
	private ArticleTransferService articleTransferService;

	@Mock
	private ArticleFetcher articleFetcher;

	@Mock
	private EvernoteClientManager evernoteClientManager;

	@Mock
	private EvernoteClient evernoteClient;


	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		evernoteResource = new EvernoteResource();
		evernoteResource.setArticleTransferService(articleTransferService);
		evernoteResource.setEvernoteClientManager(evernoteClientManager);
	}

	@Test
	public void testReceivedNotificationResponse() {

		String responseJson = evernoteResource.receivedNotification(USERNAME, NOTE_GUID, NOTEBOOK_GUID, NOTIFICATION_REASON_CREATE_NOTE);

		assertResponseJson(responseJson);

	}

	private void assertResponseJson(String responseJson) {
		Assert.assertEquals("{\"status\":\"OK\",\"message\":\"Notification received, thanks.\"}", responseJson);
	}

	@Test
	public void testReceivedNotificationNoteCreated() {

		when(evernoteClientManager.getEvernoteClient(USERNAME)).thenReturn(evernoteClient);

		String responseJson = evernoteResource.receivedNotification(USERNAME, NOTE_GUID, NOTEBOOK_GUID, NOTIFICATION_REASON_CREATE_NOTE);
		verify(evernoteClientManager).getEvernoteClient(USERNAME);
		verify(articleTransferService).transferAndDeliverArticle(any(EvernoteArticleFetcher.class));

		assertResponseJson(responseJson);
	}

	@Test
	public void testReceivedNotificationOtherReason() {
		String responseJson = evernoteResource.receivedNotification(USERNAME, NOTE_GUID, NOTEBOOK_GUID, NOTIFICATION_REASON_OTHERS);
		verify(evernoteClientManager, never()).getEvernoteClient(USERNAME);
		verify(articleTransferService, never()).transferAndDeliverArticle(any(EvernoteArticleFetcher.class));
		assertResponseJson(responseJson);
	}

	@Test
	public void testReceivedNotificationForInvalidUser() {
		when(evernoteClientManager.getEvernoteClient(USERNAME)).thenReturn(null);

		String responseJson = evernoteResource.receivedNotification(USERNAME, NOTE_GUID, NOTEBOOK_GUID, NOTIFICATION_REASON_CREATE_NOTE);
		verify(evernoteClientManager).getEvernoteClient(USERNAME);
		verify(articleTransferService, never()).transferAndDeliverArticle(any(EvernoteArticleFetcher.class));
		assertResponseJson(responseJson);

	}

}