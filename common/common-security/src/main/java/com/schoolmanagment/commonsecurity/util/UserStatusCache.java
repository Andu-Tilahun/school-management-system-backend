package com.schoolmanagment.commonsecurity.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Component
public class UserStatusCache {
    private static final String INACTIVE_USER_PREFIX = "user:status:";
    private static final String USER_POLICIES_PREFIX = "user:policies:";
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

    /**
     * Store the user's effective policy names with key pattern: user:policies:{id}
     */
    public void setUserPolicies(String id, Collection<String> policyNames) {
        String key = USER_POLICIES_PREFIX + id;
        redisTemplate.delete(key);
        if (policyNames == null || policyNames.isEmpty()) {
            return;
        }
        String[] values = policyNames.stream()
                .filter(name -> name != null && !name.isBlank())
                .distinct()
                .toArray(String[]::new);
        if (values.length > 0) {
            redisTemplate.opsForSet().add(key, values);
        }
    }

    /**
     * Read the user's effective policy names from Redis.
     */
    public Set<String> getUserPolicies(String id) {
        Set<String> policies = redisTemplate.opsForSet().members(USER_POLICIES_PREFIX + id);
        return policies != null ? policies : Collections.emptySet();
    }

    /**
     * Remove cached policy names for the user.
     */
    public void removeUserPolicies(String id) {
        redisTemplate.delete(USER_POLICIES_PREFIX + id);
    }
}
