package com.schoolmanagment.userservice.menu.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NavigationMenuItemDto {
    private String label;
    /** Leaf route; null for grouping-only rows (not rendered as a link). */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String route;
    private String icon;
    /** RBAC resource name when this row maps to a feature; null for grouping-only nodes. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String resourceName;
    /**
     * Scope names granted for this resource (dynamic; any new scope in DB appears here).
     * Null for grouping-only menu rows; may be an empty list when the resource is visible but has no scopes.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> grantedScopes;
    @Builder.Default
    private List<NavigationMenuItemDto> children = new ArrayList<>();
}
