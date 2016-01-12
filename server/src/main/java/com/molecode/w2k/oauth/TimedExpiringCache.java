package com.molecode.w2k.oauth;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by YP on 2016-01-08.
 */
public class TimedExpiringCache <K, V> {

	private static final Logger LOG = LoggerFactory.getLogger(TimedExpiringCache.class);

	private Map<K, V> cacheMap;

	private final long expireTime;

	private List<Pair<K, Long>> keyPairs;

	public TimedExpiringCache(long expireTime) {
		cacheMap = new HashMap<>();
		this.expireTime = expireTime;
		this.keyPairs = new ArrayList<>();
		new Timer("TimedExpiringCache timer", true).scheduleAtFixedRate(new TimerTask() {
			@Override public void run() {
				removeExpiredCache();
			}
		}, this.expireTime, this.expireTime);
	}

	private void removeExpiredCache() {
		synchronized (cacheMap) {
			long mostRecentExpiredTime = System.currentTimeMillis() - expireTime;
			Iterator<Pair<K, Long>> keyPairIterator = keyPairs.iterator();
			while (keyPairIterator.hasNext()) {
				Pair<K, Long> keyPair = keyPairIterator.next();
				if (keyPair.getValue() <= mostRecentExpiredTime) {
					keyPairIterator.remove();
					cacheMap.remove(keyPair.getKey());
				}
			}
		}
	}

	public void put(K key, V value) {
		synchronized (cacheMap) {
			long currentTime = System.currentTimeMillis();
			cacheMap.put(key, value);
			keyPairs.add(Pair.of(key, currentTime));
		}

	}

	public V get(K key) {
		return cacheMap.get(key);
	}

	public V remove(K key) {
		return cacheMap.remove(key);
	}
}
