package com.schoolmanagment.userservice.menu.service;

import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.commonsecurity.checker.ScopeSatisfaction;
import com.schoolmanagment.userservice.menu.dto.NavigationMenuItemDto;
import com.schoolmanagment.userservice.group.entity.Group;
import com.schoolmanagment.userservice.menu.entity.Menu;
import com.schoolmanagment.userservice.permission.entity.Permission;
import com.schoolmanagment.userservice.policy.entity.Policy;
import com.schoolmanagment.userservice.resource.entity.Resource;
import com.schoolmanagment.userservice.scope.entity.Scope;
import com.schoolmanagment.userservice.user.entity.User;
import com.schoolmanagment.userservice.menu.repository.MenuRepository;
import com.schoolmanagment.userservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Builds the sidebar from {@code menu} rows. Scopes on each permission (via {@code permission_scopes})
 * drive visibility and are returned as {@code grantedScopes} lists (no fixed action enum in the API).
 * <p>Menu rows for a resource appear only when the user has {@code VIEW} on that resource; {@code READ}
 * alone allows API access (see {@link ScopeSatisfaction}) but does not add sidebar items.
 */
@Service
@RequiredArgsConstructor
public class NavigationMenuService {

    private final UserRepository userRepository;
    private final MenuRepository menuRepository;

    @Transactional(readOnly = true)
    public List<NavigationMenuItemDto> buildMenuForUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        touchPolicyGraph(user);

        Set<Permission> granted = computeEffectivePermissions(user);
        Map<String, List<String>> scopesByResource = buildGrantedScopesByResource(granted);
        Set<UUID> menuVisibleResourceIds = resourceIdsWithViewMenu(scopesByResource, granted);

        List<Menu> all = menuRepository.findAllForNavigationOrdered();
        Map<Long, List<Menu>> childrenByParent = new HashMap<>();
        for (Menu m : all) {
            Long key = m.getParentId();
            childrenByParent.computeIfAbsent(key, k -> new ArrayList<>()).add(m);
        }
        childrenByParent.values().forEach(list -> list.sort(Comparator.comparingInt(Menu::getSortOrder).thenComparing(Menu::getId)));

        List<Menu> roots = childrenByParent.getOrDefault(null, List.of());
        List<NavigationMenuItemDto> out = new ArrayList<>();
        for (Menu root : roots) {
            NavigationMenuItemDto dto = toMenuDto(root, childrenByParent, menuVisibleResourceIds, scopesByResource);
            if (dto != null) {
                out.add(dto);
            }
        }
        return out;
    }

    @Transactional(readOnly = true)
    public Map<String, List<String>> buildGrantedScopesByResourceForUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        touchPolicyGraph(user);
        return buildGrantedScopesByResource(computeEffectivePermissions(user));
    }

    /**
     * True if the user's effective permissions satisfy the required scope for the resource
     * ({@code READ} is also satisfied by {@code VIEW}; {@code VIEW} requires {@code VIEW}).
     */
    @Transactional(readOnly = true)
    public boolean userHasScope(UUID userId, String resourceName, String scopeName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        touchPolicyGraph(user);
        String res = resourceName.trim();
        Set<Permission> granted = computeEffectivePermissions(user);
        Map<String, List<String>> byResource = buildGrantedScopesByResource(granted);
        List<String> scopes = null;
        for (Map.Entry<String, List<String>> e : byResource.entrySet()) {
            if (e.getKey() != null && e.getKey().equalsIgnoreCase(res)) {
                scopes = e.getValue();
                break;
            }
        }
        return ScopeSatisfaction.satisfiedBy(scopes != null ? scopes : List.of(), scopeName);
    }

    /**
     * Resources that appear in the sidebar: user must have {@code VIEW} (not {@code READ} alone).
     */
    private static Set<UUID> resourceIdsWithViewMenu(
            Map<String, List<String>> scopesByResource,
            Set<Permission> granted
    ) {
        Set<UUID> ids = new LinkedHashSet<>();
        for (Permission p : granted) {
            Resource resource = p.getResource();
            if (resource == null) {
                continue;
            }
            List<String> scopes = scopesByResource.get(resource.getName());
            if (scopes != null && ScopeSatisfaction.satisfiedBy(scopes, "VIEW")) {
                ids.add(resource.getId());
            }
        }
        return ids;
    }

    private void touchPolicyGraph(User user) {
        for (Group g : user.getGroups()) {
            for (Policy pol : g.getPolicies()) {
                for (Permission perm : pol.getPermissions()) {
                    perm.getResource();
                    perm.getScopes().size();
                }
            }
        }
        for (Policy pol : user.getPolicies()) {
            for (Permission perm : pol.getPermissions()) {
                perm.getResource();
                perm.getScopes().size();
            }
        }
    }

    private NavigationMenuItemDto toMenuDto(
            Menu node,
            Map<Long, List<Menu>> childrenByParent,
            Set<UUID> menuVisibleResourceIds,
            Map<String, List<String>> scopesByResource
    ) {
        List<Menu> rawChildren = childrenByParent.getOrDefault(node.getId(), List.of());
        List<NavigationMenuItemDto> childDtos = new ArrayList<>();
        for (Menu c : rawChildren) {
            NavigationMenuItemDto child = toMenuDto(c, childrenByParent, menuVisibleResourceIds, scopesByResource);
            if (child != null) {
                childDtos.add(child);
            }
        }

        Resource res = node.getResource();
        boolean selfGranted = res != null && menuVisibleResourceIds.contains(res.getId());
        boolean show = selfGranted || !childDtos.isEmpty();
        if (!show) {
            return null;
        }

        String resourceName = res != null ? res.getName() : null;
        List<String> grantedScopes = res != null
                ? scopesByResource.getOrDefault(resourceName, List.of())
                : null;

        return NavigationMenuItemDto.builder()
                .label(node.getLabel())
                .route(node.getRoute())
                .icon(node.getIcon())
                .resourceName(resourceName)
                .grantedScopes(grantedScopes)
                .children(childDtos)
                .build();
    }

    private Map<String, List<String>> buildGrantedScopesByResource(Set<Permission> granted) {
        Map<String, Set<String>> acc = new HashMap<>();
        for (Permission p : granted) {
            Resource resource = p.getResource();
            if (resource == null) {
                continue;
            }
            String resourceName = resource.getName();
            if (p.getScopes() == null) {
                continue;
            }
            for (Scope s : p.getScopes()) {
                if (s.getName() == null || s.getName().isBlank()) {
                    continue;
                }
                acc.computeIfAbsent(resourceName, k -> new LinkedHashSet<>())
                        .add(s.getName().trim().toUpperCase(Locale.ROOT));
            }
        }

        Map<String, List<String>> out = new LinkedHashMap<>();
        acc.forEach((name, scopeSet) -> {
            List<String> sorted = scopeSet.stream().sorted().collect(Collectors.toList());
            out.put(name, sorted);
        });
        return out;
    }

    private Set<Permission> computeEffectivePermissions(User user) {
        Map<UUID, Permission> allowed = new LinkedHashMap<>();
        Map<UUID, Permission> denied = new LinkedHashMap<>();

        for (Policy p : user.getPolicies()) {
            applyPolicy(p, allowed, denied);
        }
        for (Group g : user.getGroups()) {
            for (Policy p : g.getPolicies()) {
                applyPolicy(p, allowed, denied);
            }
        }

        allowed.keySet().removeAll(denied.keySet());
        return Set.copyOf(allowed.values());
    }

    private void applyPolicy(Policy policy, Map<UUID, Permission> allowed, Map<UUID, Permission> denied) {
        for (Permission perm : policy.getPermissions()) {
            Objects.requireNonNull(perm.getResource(), "permission without resource");
            if (policy.getEffect() == Policy.PolicyEffect.ALLOW) {
                allowed.putIfAbsent(perm.getId(), perm);
            } else {
                denied.putIfAbsent(perm.getId(), perm);
            }
        }
    }
}
