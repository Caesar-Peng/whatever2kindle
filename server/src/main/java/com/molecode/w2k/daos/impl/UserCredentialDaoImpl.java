package com.molecode.w2k.daos.impl;

import com.molecode.w2k.daos.UserCredentialDao;
import com.molecode.w2k.fetcher.ArticleSource;
import com.molecode.w2k.models.User;
import com.molecode.w2k.models.UserCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by YP on 2016-01-03.
 */
public class UserCredentialDaoImpl implements UserCredentialDao {

	private static final Logger LOG = LoggerFactory.getLogger(UserCredentialDaoImpl.class);

	private NamedParameterJdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	private static final String SELECT_CREDENTIAL_SQL = "SELECT * FROM user_credential WHERE article_source=:article_source AND username=:username";
	@Override
	public UserCredential loadUserCredential(ArticleSource articleSource, String username) {
		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("article_source", articleSource.name());
		parameterMap.put("username", username);

		return jdbcTemplate.queryForObject(SELECT_CREDENTIAL_SQL, parameterMap, (resultSet, rowNum) -> {
			return new UserCredential(resultSet.getInt("credential_id"),
					ArticleSource.valueOf(resultSet.getString("article_source")),
					resultSet.getString("username"),
					resultSet.getString("password"));
		});
	}

	private static final String INSERT_USER_CREDENTIAL_SQL =
			"INSERT INTO user_credential (article_source, username, password, ref_user_id) VALUES (:article_source, :username, :password, :ref_user_id)";
	@Override
	public void insertUserCredential(UserCredential userCredential) {
		KeyHolder credentialIdHolder = new GeneratedKeyHolder();
		SqlParameterSource parameterSource = new MapSqlParameterSource()
				.addValue("article_source", userCredential.getArticleSource().name())
				.addValue("username", userCredential.getUsername())
				.addValue("password", userCredential.getPassword())
				.addValue("ref_user_id", userCredential.getUserId());

		jdbcTemplate.update(INSERT_USER_CREDENTIAL_SQL, parameterSource, credentialIdHolder);
		userCredential.setCredentialId(credentialIdHolder.getKey().intValue());

	}

	private static final String SELECT_USER_ID_SQL = "SELECT user_id FROM user WHERE kindle_email = :kindle_email";
	private static final String INSERT_USER_SQL = "INSERT INTO user (kindle_email, w2k_tag) VALUES (:kindle_email, :w2k_tag)";
	@Override
	public Integer insertUserOrSelectUserId(User user) {
		Map<String, Object> selectParameterMap = new HashMap<>();
		selectParameterMap.put("kindle_email", user.getKindleEmail());
		List<Integer> userIds = jdbcTemplate.queryForList(SELECT_USER_ID_SQL, selectParameterMap, Integer.class);
		Integer userId = null;
		if (userIds.size() == 0) {
			KeyHolder userIdHolder = new GeneratedKeyHolder();
			SqlParameterSource insertParameterSource = new MapSqlParameterSource()
					.addValue("kindle_email", user.getKindleEmail())
					.addValue("w2k_tag", user.getW2kTag());
			jdbcTemplate.update(INSERT_USER_SQL, insertParameterSource, userIdHolder);
			userId = userIdHolder.getKey().intValue();
			user.setId(userId);
		} else if (userIds.size() == 1) {
			userId = userIds.get(0);
		}
		return userId;
	}

	private static final String SELECT_USER_BY_CREDENTIAL_SQL = "SELECT u.* "
					+ "FROM user u INNER JOIN user_credential uc ON u.user_id = uc.ref_user_id "
					+ "WHERE uc.article_source = :article_source AND uc.username = :username";
	@Override
	public User queryUserByCredential(ArticleSource articleSource, String username) {
		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("article_source", articleSource.name());
		parameterMap.put("username", username);
		List<User> users =  jdbcTemplate.query(SELECT_USER_BY_CREDENTIAL_SQL, parameterMap, (resultSet, rowNumber) -> {
			User user = new User();
			user.setId(resultSet.getInt("user_id"));
			user.setKindleEmail(resultSet.getString("kindle_email"));
			user.setW2kTag(resultSet.getString("w2k_tag"));
			return user;
		});
		if (users.size() == 1) {
			return users.get(0);
		} else {
			LOG.warn("Found unexpected quantity of user: {}", users.size());
			return null;
		}
	}
}
