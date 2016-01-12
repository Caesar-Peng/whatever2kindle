package com.molecode.w2k.resources;

import com.molecode.w2k.fetcher.ArticleFetcher;
import com.molecode.w2k.fetcher.ArticleWriter;
import com.molecode.w2k.fetcher.evernote.EvernoteArticleFetcher;
import com.molecode.w2k.fetcher.evernote.EvernoteClient;
import com.molecode.w2k.fetcher.evernote.EvernoteClientManager;
import com.molecode.w2k.services.ArticleTransferService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Created by YP on 2015-12-19.
 */
@Path("/evernote")
@Component
public class EvernoteResource {

	private static final Logger LOG = LoggerFactory.getLogger(EvernoteResource.class);

	private static final String RESPONSE_JSON = "{\"status\":\"OK\",\"message\":\"Notification received, thanks.\"}";

	private EvernoteClientManager evernoteClientManager;

	private ArticleTransferService articleTransferService;

	@Autowired
	@Required
	public void setEvernoteClientManager(EvernoteClientManager evernoteClientManager) {
		this.evernoteClientManager = evernoteClientManager;
	}

	@Autowired
	@Required
	public void setArticleTransferService(ArticleTransferService articleTransferService) {
		this.articleTransferService = articleTransferService;
	}

	@GET
	@Path("/notification")
	@Produces(MediaType.APPLICATION_JSON)
	public String receivedNotification(@QueryParam("userId") String username, @QueryParam("notebookGuid") String notebookGuid,
			@QueryParam("guid") String noteGuid, @QueryParam("reason") String reason) {
		LOG.info("Received evernote webhook notification, username: {}, noteGuid: {}, reason :{}", username, noteGuid, reason);
		if (StringUtils.equalsIgnoreCase("create", reason)) {
			EvernoteClient evernoteClient = evernoteClientManager.getEvernoteClient(username);
			if (evernoteClient != null) {
				ArticleFetcher articleFetcher = new EvernoteArticleFetcher(username, noteGuid, evernoteClient, new ArticleWriter());
				articleTransferService.transferAndDeliverArticle(articleFetcher);
				LOG.info("Successfully received and handed evernote webhook notification to ArticleTransferService: {}, {}", username, noteGuid);
			}
		}
		return RESPONSE_JSON;
	}
}
