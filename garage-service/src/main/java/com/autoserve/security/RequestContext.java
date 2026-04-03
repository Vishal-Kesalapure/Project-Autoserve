package com.autoserve.security;


import lombok.Getter;
import lombok.Setter;

/**
 * Thread-local holder for the user identity headers injected by the API Gateway
 * after JWT validation.
 *
 * The gateway strips the original Authorization header and injects:
 *   X-User-Id    → numeric user id
 *   X-User-Role  → e.g. ROLE_ADMIN | ROLE_SUPPLIER | ROLE_CUSTOMER
 *   X-Username   → the login username
 *
 * Usage in any Spring component:
 *   Long   userId   = RequestContext.get().getUserId();
 *   String role     = RequestContext.get().getRole();
 *   String username = RequestContext.get().getUsername();
 */
@Getter
@Setter
public class RequestContext {

    // ── Thread-local singleton ────────────────────────────────────────────────

    private static final ThreadLocal<RequestContext> HOLDER =
            ThreadLocal.withInitial(RequestContext::new);

    /**
     * Returns the RequestContext bound to the current thread.
     * Never null — ThreadLocal initialises a fresh instance on first access.
     */
    public static RequestContext get() {
        return HOLDER.get();
    }

    /**
     * Clears the thread-local context.
     * Must be called in the finally block of the request filter to prevent
     * data leaking across requests on pooled threads.
     */
    public static void clear() {
        HOLDER.remove();
    }

    // ── Fields (Lombok @Getter + @Setter generates all getters and setters) ───

    /** Numeric user id from X-User-Id header */
    private Long userId;

    /** Role string from X-User-Role header — e.g. "ROLE_ADMIN" */
    private String role;

    /** Login username from X-Username header */
    private String username;

    // ── Role helper methods ───────────────────────────────────────────────────

    public boolean isAdmin() {
        return "ROLE_ADMIN".equals(role);
    }

    public boolean isSupplier() {
        return "ROLE_SUPPLIER".equals(role);
    }

    public boolean isCustomer() {
        return "ROLE_CUSTOMER".equals(role);
    }

    // Private constructor — use RequestContext.get() to obtain the instance
    private RequestContext() {}
}