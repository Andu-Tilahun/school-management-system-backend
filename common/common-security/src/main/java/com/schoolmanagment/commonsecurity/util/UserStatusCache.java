package com.schoolmanagment.commonsecurity.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserStatusCache {
    private static final String INACTIVE_USER_PREFIX = "user:status:";
    private static final String INACTIVE_STATUS = "INACTIVE";

    private final RedisTemplate<String, String> redisTemplate;

    public UserStatusCache(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Store user status in Redis with key pattern: user:status:{id}
     * Value will be "INACTIVE"
     */
    public void setUserStatus(String id) {
        String key = INACTIVE_USER_PREFIX + id;
        redisTemplate.opsForValue().set(key, INACTIVE_STATUS);
    }

    /**
     * Add user to inactive status
     */
    public void addInactiveUser(String id) {
        String key = INACTIVE_USER_PREFIX + id;
        redisTemplate.opsForValue().set(key, INACTIVE_STATUS);
    }

    /**
     * Check if user is inactive
     * Returns true if user exists in Redis with any non-active status
     */
    public boolean isUserInactive(String id) {
        String key = INACTIVE_USER_PREFIX + id;
        String status = redisTemplate.opsForValue().get(key);
        return status != null && (INACTIVE_STATUS.equals(status));
    }

    /**
     * Remove user from Redis (make active)
     */
    public void removeUser(String id) {
        String key = INACTIVE_USER_PREFIX + id;
        redisTemplate.delete(key);
    }

    /**
     * Update user status in Redis based on UserStatus enum
     */
    public void updateUserStatus(String id) {
        setUserStatus(id);
    }
}
