package com.schoolmanagment.userservice.user.service;

import com.schoolmanagment.userservice.group.entity.Group;
import com.schoolmanagment.userservice.policy.entity.Policy;
import com.schoolmanagment.userservice.user.entity.User;
import com.schoolmanagment.userservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findWithPoliciesGraphByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Set<String> authorityStrings = new LinkedHashSet<>();
        if (user.getPolicies() != null) {
            for (Policy p : user.getPolicies()) {
                authorityStrings.add("POLICY_" + p.getName());
            }
        }
        if (user.getGroups() != null) {
            for (Group g : user.getGroups()) {
                if (g.getPolicies() != null) {
                    for (Policy p : g.getPolicies()) {
                        authorityStrings.add("POLICY_" + p.getName());
                    }
                }
            }
        }

        var authorities = authorityStrings.stream().map(SimpleGrantedAuthority::new).toList();

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getEnabled(),
                user.getAccountNonExpired(),
                user.getCredentialsNonExpired(),
                user.getAccountNonLocked(),
                authorities
        );
    }
}
