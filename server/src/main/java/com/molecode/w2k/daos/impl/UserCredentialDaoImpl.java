package com.molecode.w2k.daos.impl;

import com.molecode.w2k.daos.UserCredentialDao;
import com.molecode.w2k.fetcher.ArticleSource;
import com.molecode.w2k.models.UserCredential;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
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
	public UserCredential loadUserCredential(ArticleSource articleSource, String username) {
		String sql = "SELECT * FROM user_credential WHERE article_source=:article_source AND username=:username";
		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("article_source", articleSource.name());
		parameterMap.put("username", username);

		return jdbcTemplate.queryForObject(sql, parameterMap, new RowMapper<UserCredential>() {
			@Override
			public UserCredential mapRow(ResultSet resultSet, int rowNum) throws SQLException {
				return new UserCredential(resultSet.getInt("user_id"),
						ArticleSource.valueOf(resultSet.getString("article_source")),
						resultSet.getString("username"),
						resultSet.getString("password"));
			}
		});
	}

	@Override
	public void insertUserCredential(UserCredential userCredential) {

		String sql = "INSERT INTO user_credential (article_source, username, password) VALUES (:article_source, :username, :password)";
		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("article_source", userCredential.getArticleSource().name());
		parameterMap.put("username", userCredential.getUsername());
		parameterMap.put("password", userCredential.getPassword());
		jdbcTemplate.update(sql, parameterMap);

	}
}
