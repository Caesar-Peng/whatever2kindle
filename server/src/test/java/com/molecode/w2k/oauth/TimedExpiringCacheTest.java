package com.molecode.w2k.oauth;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;


import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by YP on 2016-01-08.
 */
public class TimedExpiringCacheTest {

	private static final long EXPIRE_TIME = 500;

	private List<Pair<String, Integer>> pairs;

	private TimedExpiringCache<String, Integer> cache;

	@Before
	public void setUp() {
		pairs = new ArrayList<>();
		pairs.add(Pair.of("13rlao13u4kj", 0));
		pairs.add(Pair.of("adfp1934qnaer,qmenr", 1));
		pairs.add(Pair.of("adjqouoi1u34oi123kn4k", 2));
		pairs.add(Pair.of("1kk33kna9132k4jlq", 3));
		cache = new TimedExpiringCache<>(EXPIRE_TIME);
	}

	@Test
	public void testAddElementAndRetrieveBeforeItExpires() {
		pairs.stream().forEach(pair -> cache.put(pair.getKey(), pair.getValue()));
		pairs.stream().forEach(pair -> {
			Integer value = cache.get(pair.getKey());
			assertNotNull(value);
			assertEquals(pair.getValue(), value);
		});
	}

	@Test
	public void testRemoveElementBeforeItExpires() {
		Pair<String, Integer> pair = pairs.get(0);
		cache.put(pair.getKey(), pair.getValue());

		Integer value = cache.remove(pair.getKey());

		assertNotNull(value);
		assertEquals(pair.getValue(), value);

		assertNull(cache.remove(pair.getKey()));
		assertNull(cache.get(pair.getKey()));
	}

	@Test
	public void testGetOrRemoveElementAfterItExpires() throws InterruptedException {
		Pair<String, Integer> pair1 = pairs.get(0);
		cache.put(pair1.getKey(), pair1.getValue());

		Thread.sleep(550);

		assertNull(cache.get(pair1.getKey()));
		assertNull(cache.remove(pair1.getKey()));
	}

}