package com.molecode.w2k.daos.impl;

import com.molecode.w2k.daos.UserCredentialDao;
import com.molecode.w2k.fetcher.ArticleSource;
import com.molecode.w2k.models.UserCredential;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by YP on 2016-01-03.
 */
public class UserCredentialDaoImpl implements UserCredentialDao {

	private NamedParameterJdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public UserCredential loadUserCredential(ArticleSource evernote, String username) {
		return null;
	}

	@Override
	public void insertUserCredential(UserCredential userCredential) {

		String sql = "INSERT INTO user_credential (article_source, username, password) VALUES (:article_source, :username, :password)";
		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("article_source", userCredential.getArticleSource().getSourceId());
		parameterMap.put("username", userCredential.getUsername());
		parameterMap.put("password", userCredential.getPassword());
		jdbcTemplate.update(sql, parameterMap);

	}
}
