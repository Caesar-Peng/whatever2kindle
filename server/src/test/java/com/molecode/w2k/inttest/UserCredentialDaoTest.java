package com.molecode.w2k.inttest;

import com.molecode.w2k.daos.UserCredentialDao;
import com.molecode.w2k.fetcher.ArticleSource;
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

	}

	@Test
	public void testInsertUserCredential() throws Exception {

		UserCredential userCredential = new UserCredential(null, ArticleSource.EVERNOTE, USERNAME, PASSWORD);

		userCredentialDao.insertUserCredential(userCredential);

		int recordCount = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,
				"user_credential", "article_source = '" + ArticleSource.EVERNOTE.getSourceId()
						+ "' AND username = '" + USERNAME + "' AND password = '" + PASSWORD + "'");

		assertEquals(1, recordCount);
	}

	@Test
	public void testQueryUserCredential() throws Exception {

		UserCredential userCredential = new UserCredential(null, ArticleSource.EVERNOTE, USERNAME, PASSWORD);

		userCredentialDao.insertUserCredential(userCredential);

	}

}
