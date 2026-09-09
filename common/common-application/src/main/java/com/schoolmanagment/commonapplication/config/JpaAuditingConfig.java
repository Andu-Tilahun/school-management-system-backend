package com.schoolmanagment.commonapplication.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@ConditionalOnClass(name = "jakarta.persistence.EntityManager")
@EnableJpaAuditing
public class JpaAuditingConfig {
}
