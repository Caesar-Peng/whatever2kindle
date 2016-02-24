package com.molecode.w2k.fetcher.evernote;

import com.evernote.auth.EvernoteAuth;
import com.evernote.auth.EvernoteService;
import com.evernote.clients.ClientFactory;
import com.evernote.clients.NoteStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.Note;
import com.evernote.thrift.TException;
import com.syncthemall.enml4j.ENMLProcessor;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by YP on 2015-12-29.
 */
public class EvernoteClient {

	private NoteStoreClient noteStoreClient;

	private final String outputDir;

	private final ENMLProcessor enmlProcessor;

	public EvernoteClient(String password, EvernoteService evernoteService, String outputDir, ENMLProcessor enmlProcessor) {
		EvernoteAuth evernoteAuth = new EvernoteAuth(evernoteService, password);
		ClientFactory clientFactory = new ClientFactory(evernoteAuth);
		try {
			this.noteStoreClient = clientFactory.createNoteStoreClient();
		} catch (EDAMUserException e) {
			e.printStackTrace();
		} catch (EDAMSystemException e) {
			e.printStackTrace();
		} catch (TException e) {
			e.printStackTrace();
		}
		this.outputDir = outputDir;
		this.enmlProcessor = enmlProcessor;
	}

	public File fetchNoteContent(String noteGuid, String w2kTag) {
		File htmlFile = null;
		try {
			List<String> tagNames = noteStoreClient.getNoteTagNames(noteGuid);
			if (tagNames.contains(w2kTag.toLowerCase())) {
				Note note = noteStoreClient.getNote(noteGuid, true, true, false, false);
				note.setContent(noteStoreClient.getNoteContent(noteGuid));
				htmlFile = new File(outputDir + note.getGuid() + ".html");
				enmlProcessor.noteToInlineHTML(note, new FileOutputStream(htmlFile)).close();
			}
		} catch (EDAMUserException e) {
			e.printStackTrace();
		} catch (EDAMSystemException e) {
			e.printStackTrace();
		} catch (EDAMNotFoundException e) {
			e.printStackTrace();
		} catch (TException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return htmlFile;
	}
}
