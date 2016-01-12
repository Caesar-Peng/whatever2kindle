package com.molecode.w2k.fetcher.evernote;

import com.evernote.auth.EvernoteAuth;
import com.evernote.auth.EvernoteService;
import com.evernote.clients.ClientFactory;
import com.evernote.clients.NoteStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.thrift.TException;

/**
 * Created by YP on 2015-12-29.
 */
public class EvernoteClient {

	private NoteStoreClient noteStoreClient;

	public EvernoteClient(String password, EvernoteService evernoteService) {
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
	}

	public String fetchNoteContent(String noteGuid) {
		try {
			return noteStoreClient.getNoteContent(noteGuid);
		} catch (EDAMUserException e) {
			e.printStackTrace();
		} catch (EDAMSystemException e) {
			e.printStackTrace();
		} catch (EDAMNotFoundException e) {
			e.printStackTrace();
		} catch (TException e) {
			e.printStackTrace();
		}
		return null;
	}
}
