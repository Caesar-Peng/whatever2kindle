package com.molecode.w2k.fetcher.evernote;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static com.molecode.w2k.TestConstants.*;

/**
 * Created by YP on 2015-12-29.
 */
public class EvernoteArticleFetcherTest {

	private EvernoteArticleFetcher evernoteArticleFetcher;

	private static final String ARTICLE_CONTENT = "article content";

	@Mock
	private EvernoteClient evernoteClient;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		evernoteArticleFetcher = new EvernoteArticleFetcher(USERNAME, NOTE_GUID, evernoteClient);
	}

	@Test
	public void testFetchArticleSucceed() throws IOException {
		File noteFile = new File("~/note_file");

		when(evernoteClient.fetchNoteContent(NOTE_GUID, W2K_TAG)).thenReturn(noteFile);

		File returnedFile = evernoteArticleFetcher.fetchArticle(W2K_TAG);

		verify(evernoteClient).fetchNoteContent(NOTE_GUID, W2K_TAG);

		assertEquals(noteFile, returnedFile);
	}

	@Test
	public void testFetchArticleFailedDueToNullContent() throws IOException {
		when(evernoteClient.fetchNoteContent(NOTE_GUID, W2K_TAG)).thenReturn(null);

		File returnedFile = evernoteArticleFetcher.fetchArticle(W2K_TAG);

		verify(evernoteClient).fetchNoteContent(NOTE_GUID, W2K_TAG);
		assertNull(returnedFile);
	}

}