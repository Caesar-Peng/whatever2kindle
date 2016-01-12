package com.molecode.w2k.models;

import com.molecode.w2k.fetcher.ArticleSource;

/**
 * Created by YP on 2015-12-30.
 */
public class UserCredential {

	private final Integer userId;

	private final ArticleSource articleSource;

	private final String username;

	private final String password;

	public UserCredential(Integer userId, ArticleSource articleSource, String username, String password) {
		this.userId = userId;
		this.articleSource = articleSource;
		this.username = username;
		this.password = password;
	}

	public Integer getUserId() {
		return userId;
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
