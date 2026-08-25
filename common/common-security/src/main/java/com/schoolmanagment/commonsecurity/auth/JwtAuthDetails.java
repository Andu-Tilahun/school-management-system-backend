package com.schoolmanagment.commonsecurity.auth;

import java.util.UUID;

/**
 * Stored on {@link org.springframework.security.core.Authentication#setDetails(Object)} so services can read
 * scope claims (for example region / organization id for representatives) without re-parsing the JWT.
 */
public record JwtAuthDetails(UUID externalId) {}
