package com.darianngo.RiftCatcher.config;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Component
public class RedisManager {

	private final JedisPool pool;

	public RedisManager() {
		// Create a pool of Jedis connections
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(128); // Maximum active connections
		this.pool = new JedisPool(poolConfig, "localhost", 6379);
	}

	public Jedis getJedis() {
		return pool.getResource();
	}

	public static String createUserKey(String messageId) {
		return messageId + ":user";
	}

	public void setExpiringKey(String key, String value, int seconds) {
		try (Jedis jedis = getJedis()) {
			jedis.setex(key, seconds, value);
		} catch (Exception e) {
			System.out.println("Error setting key in Redis: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public String retrieveValue(String key) {
		try (Jedis jedis = getJedis()) {
			return jedis.get(key);
		} catch (Exception e) {
			System.out.println("Error getting value from Redis: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	@PostConstruct
	public void testConnectionAndStorage() {
		String testKey = "testKey";
		String testValue = "Hello, Redis!";

		// Test connection
		try (Jedis jedis = getJedis()) {
			jedis.ping();
			System.out.println("Successfully connected to Redis!");

			// Save test key-value
			jedis.setex(testKey, 300, testValue); // The value will expire after 300 seconds
			System.out.println("Saved test value to Redis!");

			// Retrieve and print test value
			String retrievedValue = jedis.get(testKey);
			System.out.println("Retrieved from Redis: " + retrievedValue);

		} catch (Exception e) {
			System.out.println("Error connecting to or interacting with Redis at startup: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void close() {
		pool.close();
	}
}
