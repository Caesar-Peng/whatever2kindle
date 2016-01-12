package com.molecode.w2k.fetcher.evernote;

import com.molecode.w2k.fetcher.ArticleWriter;
import com.molecode.w2k.fetcher.evernote.EvernoteArticleFetcher;
import com.molecode.w2k.fetcher.evernote.EvernoteClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

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

	@Mock
	private ArticleWriter articleWriter;

	@Captor
	private ArgumentCaptor<File> articleFileCaptor;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		evernoteArticleFetcher = new EvernoteArticleFetcher(USERNAME, NOTE_GUID, evernoteClient, articleWriter);
	}

	@Test
	public void testFetchArticleSucceed() throws IOException {
		when(evernoteClient.fetchNoteContent(NOTE_GUID)).thenReturn(ARTICLE_CONTENT);

		File returnedFile = evernoteArticleFetcher.fetchArticle();

		verify(evernoteClient).fetchNoteContent(NOTE_GUID);
		verify(articleWriter).write(articleFileCaptor.capture(), eq(ARTICLE_CONTENT));

		File specifiedFile = articleFileCaptor.getValue();

		assertEquals(NOTE_GUID + ".html", specifiedFile.getName());
		assertEquals(specifiedFile, returnedFile);
	}

	@Test
	public void testFetchArticleFailedDueToNullContent() throws IOException {
		when(evernoteClient.fetchNoteContent(NOTE_GUID)).thenReturn(null);

		File returnedFile = evernoteArticleFetcher.fetchArticle();

		verify(evernoteClient).fetchNoteContent(NOTE_GUID);
		verify(articleWriter, never()).write(any(File.class), eq(ARTICLE_CONTENT));
		assertNull(returnedFile);
	}

	@Test
	public void testFetchArticleFailedDueToIOException() throws IOException {
		when(evernoteClient.fetchNoteContent(NOTE_GUID)).thenReturn(ARTICLE_CONTENT);
		doThrow(new IOException()).when(articleWriter).write(any(File.class), eq(ARTICLE_CONTENT));

		File returnedFile = evernoteArticleFetcher.fetchArticle();

		assertNull(returnedFile);
	}

}