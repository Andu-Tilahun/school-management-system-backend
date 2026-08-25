package com.schoolmanagment.commonsecurity.config;

import com.schoolmanagment.commonsecurity.checker.PermissionEvaluator;
import com.schoolmanagment.commonsecurity.security.GrantedScopesCache;
import com.schoolmanagment.commonsecurity.security.RemoteRbacPermissionEvaluator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RemoteRbacPermissionConfiguration {

    @Bean
    @ConditionalOnMissingBean(PermissionEvaluator.class)
    public RemoteRbacPermissionEvaluator remoteRbacPermissionEvaluator(GrantedScopesCache grantedScopesCache) {
        return new RemoteRbacPermissionEvaluator(grantedScopesCache);
    }
}
