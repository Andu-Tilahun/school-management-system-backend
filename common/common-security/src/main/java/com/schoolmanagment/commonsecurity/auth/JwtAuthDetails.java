package com.schoolmanagment.commonsecurity.auth;

import java.util.UUID;

public record JwtAuthDetails(UUID externalId, String username) {}
