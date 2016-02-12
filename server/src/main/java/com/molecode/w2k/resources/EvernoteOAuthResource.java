package com.molecode.w2k.resources;

import com.molecode.w2k.services.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by YP on 2016-01-06.
 */

@Path("/evernote")
@Component
public class EvernoteOAuthResource {

	private OAuthService oAuthService;

	private String serviceHost;

	private static final Logger LOG = LoggerFactory.getLogger(EvernoteOAuthResource.class);

	@Autowired
	@Required
	public void setOAuthService(OAuthService oAuthService) {
		this.oAuthService = oAuthService;
	}

	@Required
	@Value("${service.host}")
	public void setServiceHost(String serviceHost) {
		this.serviceHost = serviceHost;
	}

	@POST
	@Path("/request_authorization")
	public Response requestAuthorization(@FormParam("kindle_email") String kindleEmail, @FormParam("w2k_tag") String w2kTag) {
		URI authorizationUrl = oAuthService.generateOAuthRequestURI(kindleEmail, w2kTag);
		return Response.seeOther(authorizationUrl).status(Response.Status.MOVED_PERMANENTLY).build();
	}

	@GET
	@Path("/oauth_callback")
	public Response receivedAuthorizationCallback(@QueryParam("oauth_token") String temporaryToken,
			@QueryParam("oauth_verifier") String verifierValue) {
		OAuthService.AuthorizationResult authorizationResult = oAuthService.retrieveAndStoreAccessToken(temporaryToken, verifierValue);
		return buildResponseWithAuthorizationResult(authorizationResult);
	}

	private Response buildResponseWithAuthorizationResult(OAuthService.AuthorizationResult result) {
		StringBuilder redirectURIBuilder = new StringBuilder(serviceHost).append("/oauth_result.html#");
		redirectURIBuilder.append("succeed=").append(result.isSucceed());
		if (!result.isSucceed()) {
			redirectURIBuilder.append("&message=").append(result.getMessage());
		}
		try {
			return Response.seeOther(new URI(redirectURIBuilder.toString())).status(Response.Status.MOVED_PERMANENTLY).build();
		} catch (URISyntaxException e) {
			LOG.error("Tried to build a wrong URI", e);
		}

		return null;
	}
}
