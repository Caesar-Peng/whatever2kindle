package com.molecode.w2k.models;

import com.molecode.w2k.fetcher.ArticleSource;

/**
 * Created by YP on 2015-12-30.
 */
public class UserCredential {

	private Integer credentialId;

	private final ArticleSource articleSource;

	private final String username;

	private final String password;

	private Integer userId;

	public UserCredential(Integer credentialId, ArticleSource articleSource, String username, String password) {
		this.credentialId = credentialId;
		this.articleSource = articleSource;
		this.username = username;
		this.password = password;
	}

	public Integer getUserId() {
		return userId;
	}

	public Integer getCredentialId() {
		return credentialId;
	}

	public void setCredentialId(Integer credentialId) {
		this.credentialId = credentialId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public ArticleSource getArticleSource() {
		return articleSource;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
}
