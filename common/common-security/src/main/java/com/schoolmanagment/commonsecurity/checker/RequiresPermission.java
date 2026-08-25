package com.schoolmanagment.commonsecurity.checker;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPermission {
    /** Resource name from {@code resources.name} (e.g. {@code USERS}). */
    String resource();

    /**
     * Scope name from {@code scopes.name}. Use {@code READ} for read-only APIs; {@code VIEW} implies {@code READ}
     * when evaluating access. Use {@code VIEW} only when the operation must require menu-tier access.
     */
    String scope();
}
