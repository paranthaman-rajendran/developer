package com.learn.sso.customer.util;

import javax.servlet.http.HttpServletRequest;

/**
 * RequestValidationUtil - Utility class to extract validation data from requests.
 *
 * Provides convenient methods to access the validation data set by ValidationFilter.
 */
public class RequestValidationUtil {

    /**
     * Gets the session token from the request attributes (set by ValidationFilter).
     */
    public static String getSessionToken(HttpServletRequest request) {
        return (String) request.getAttribute("sessionToken");
    }

    /**
     * Gets the validation ID from the request attributes (set by ValidationFilter).
     */
    public static String getValidationId(HttpServletRequest request) {
        return (String) request.getAttribute("validationId");
    }

    /**
     * Gets the client's User-Agent from the request attributes (set by ValidationFilter).
     */
    public static String getUserAgent(HttpServletRequest request) {
        return (String) request.getAttribute("userAgent");
    }

    /**
     * Gets the client's IP address (X-Forwarded-For or RemoteAddr) from request attributes.
     */
    public static String getClientIp(HttpServletRequest request) {
        return (String) request.getAttribute("clientIp");
    }

    /**
     * Gets the Authorization header from the request (Bearer token, Basic auth, etc).
     */
    public static String getAuthorizationHeader(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    /**
     * Gets the Content-Type header from the request.
     */
    public static String getContentType(HttpServletRequest request) {
        return request.getHeader("Content-Type");
    }

    /**
     * Checks if the request has a valid session token cookie.
     */
    public static boolean hasValidSessionToken(HttpServletRequest request) {
        return getSessionToken(request) != null && !getSessionToken(request).isEmpty();
    }
}

