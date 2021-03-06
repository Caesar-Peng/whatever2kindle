package com.molecode.w2k.inttest;

import com.molecode.w2k.daos.UserCredentialDao;
import com.molecode.w2k.fetcher.ArticleSource;
import com.molecode.w2k.models.User;
import com.molecode.w2k.models.UserCredential;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;

import static org.junit.Assert.*;
import static com.molecode.w2k.TestConstants.*;

/**
 * Created by YP on 2016-01-04.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring-db-connector-test.xml", "/applicationContext.xml"})
public class UserCredentialDaoTest {

	@Autowired
	private UserCredentialDao userCredentialDao;

	@Autowired
	private DataSource dataSource;

	private JdbcTemplate jdbcTemplate;

	@Before
	public void setUp() {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@After
	public void tearDown() {

		jdbcTemplate.update("DELETE FROM user_credential");
		jdbcTemplate.update("DELETE FROM user");

	}

	@Test
	public void testInsertUserOrSelectUserIdForInsert() {
		Integer userId = preInsertUser(KINDLE_EMAIL_ADDRESS, W2K_TAG);

		int recordCount = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "user",
				"user_id = " + userId + " AND kindle_email = '" + KINDLE_EMAIL_ADDRESS + "' AND w2k_tag='" + W2K_TAG + "'");

		assertEquals(1, recordCount);
	}

	private Integer preInsertUser(String kindleEmail, String w2kTag) {
		User user = new User();
		user.setKindleEmail(kindleEmail);
		user.setW2kTag(w2kTag);

		Integer userId = userCredentialDao.insertUserOrSelectUserId(user);
		assertNotNull(user.getId());
		assertEquals(user.getId(), userId);

		return userId;
	}

	@Test
	public void testInsertUserOrSelectUserIdForSelect() {
		Integer userId = preInsertUser(KINDLE_EMAIL_ADDRESS, W2K_TAG);
		User user = new User();
		user.setKindleEmail(KINDLE_EMAIL_ADDRESS);

		Integer returnedId = userCredentialDao.insertUserOrSelectUserId(user);

		assertEquals(userId, returnedId);

	}

	@Test
	public void testInsertUserCredential() throws Exception {
		UserCredential userCredential = preInsertUserCredential(KINDLE_EMAIL_ADDRESS, W2K_TAG, ArticleSource.EVERNOTE, USERNAME, PASSWORD);

		int recordCount = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,
				"user_credential", "credential_id = " + userCredential.getCredentialId() + " AND article_source = '" + ArticleSource.EVERNOTE.name()
						+ "' AND username = '" + USERNAME + "' AND password = '" + PASSWORD
						+ "' AND ref_user_id = " + userCredential.getUserId());

		assertEquals(1, recordCount);
	}

	private UserCredential preInsertUserCredential(String kindleEmail, String w2kTag, ArticleSource articleSource, String username, String password)
			throws Exception {
		Integer userId = preInsertUser(kindleEmail, w2kTag);

		UserCredential userCredential = new UserCredential(null, articleSource, username, password);
		userCredential.setUserId(userId);

		userCredentialDao.insertUserCredential(userCredential);
		return userCredential;
	}

	@Test
	public void testQueryUserCredential() throws Exception {
		UserCredential userCredential = preInsertUserCredential(KINDLE_EMAIL_ADDRESS, W2K_TAG, ArticleSource.EVERNOTE, USERNAME, PASSWORD);

		UserCredential userCredentialQueried = userCredentialDao.loadUserCredential(ArticleSource.EVERNOTE, USERNAME);

		assertNotNull(userCredentialQueried);
		assertEquals(userCredential.getCredentialId(), userCredentialQueried.getCredentialId());
		assertEquals(userCredential.getArticleSource(), userCredentialQueried.getArticleSource());
		assertEquals(userCredential.getUsername(), userCredentialQueried.getUsername());
		assertEquals(userCredential.getPassword(), userCredentialQueried.getPassword());

	}

	@Test
	public void testQueryUserByCredential() throws Exception {
		preInsertUserCredential(KINDLE_EMAIL_ADDRESS, W2K_TAG, ArticleSource.EVERNOTE, USERNAME, PASSWORD);

		User user = userCredentialDao.queryUserByCredential(ArticleSource.EVERNOTE, USERNAME);

		assertNotNull(user);
		assertEquals(KINDLE_EMAIL_ADDRESS, user.getKindleEmail());
		assertEquals(W2K_TAG, user.getW2kTag());
	}

	@Test
	public void testQueryUserByCredentialUserNotExist() {
		User user = userCredentialDao.queryUserByCredential(ArticleSource.EVERNOTE, USERNAME);

		assertNull(user);
	}

}
