package com.darianngo.RiftCatcher.services;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisStateManagementService {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	public void setUserPage(String userId, int page) {
		redisTemplate.opsForValue().set("starterChampionPage:" + userId, page, 5, TimeUnit.MINUTES); // expires in 5
																										// minutes
	}

	public int getUserPage(String userId) {
		return (int) redisTemplate.opsForValue().get("starterChampionPage:" + userId);
	}
}
