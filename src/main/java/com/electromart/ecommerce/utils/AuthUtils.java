package com.electromart.ecommerce.utils;

import com.electromart.ecommerce.services.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class AuthUtils {

    private AuthUtils() {}

    /** Returns the Mongo user id of the currently authenticated user. */
    public static String currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        return principal.getId();
    }
}
