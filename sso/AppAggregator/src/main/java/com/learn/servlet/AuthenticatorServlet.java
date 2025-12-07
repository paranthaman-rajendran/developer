package com.learn.servlet;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * AuthenticatorServlet - Processes HTTP headers and stores them as secure
 * backend-only cookies
 * 
 * This servlet:
 * 1. Reads incoming HTTP headers
 * 2. Processes specific headers (like USER, Authorization, etc.)
 * 3. Stores them as HttpOnly and Secure cookies
 * 4. Returns JSON response with processing status
 */
@RestController
@RequestMapping("/api/authenticate")
public class AuthenticatorServlet {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @GetMapping
    public Map<String, Object> authenticateGet(HttpServletRequest request, HttpServletResponse response) {
        return processRequest(request, response);
    }

    @PostMapping
    public Map<String, Object> authenticatePost(HttpServletRequest request, HttpServletResponse response) {
        return processRequest(request, response);
    }

    /**
     * Processes incoming request headers and creates secure cookies
     */
    private Map<String, Object> processRequest(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("X-Content-Type-Options", "nosniff");

        Map<String, Object> responseData = new HashMap<>();
        Map<String, String> processedHeaders = new HashMap<>();
        Map<String, String> setCookies = new HashMap<>();

        try {
            // Extract and process important headers
            String userHeader = request.getHeader("USER");
            String authorizationHeader = request.getHeader("Authorization");
            String userAgent = request.getHeader("User-Agent");
            String contentType = request.getHeader("Content-Type");

            // Process USER header
            if (userHeader != null && !userHeader.isEmpty()) {
                processedHeaders.put("USER", userHeader);
                createSecureCookie(response, "USER_ID", userHeader);
                setCookies.put("USER_ID", userHeader);
            }

            // Process Authorization header
            if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
                // Extract token from Authorization header (e.g., "Bearer token123")
                String token = extractTokenFromAuthHeader(authorizationHeader);
                processedHeaders.put("Authorization", authorizationHeader);
                createSecureCookie(response, "AUTH_TOKEN", token);
                setCookies.put("AUTH_TOKEN", token);
            }

            // Process User-Agent header
            if (userAgent != null && !userAgent.isEmpty()) {
                processedHeaders.put("User-Agent", userAgent);
            }

            // Process Content-Type header
            if (contentType != null && !contentType.isEmpty()) {
                processedHeaders.put("Content-Type", contentType);
            }

            // Log session info
            String sessionId = request.getSession().getId();
            processedHeaders.put("Session-ID", sessionId);

            // Build response
            responseData.put("status", "success");
            responseData.put("message", "Headers processed and secure cookies set successfully");
            responseData.put("processedHeaders", processedHeaders);
            responseData.put("setCookies", setCookies);
            responseData.put("sessionId", sessionId);
            responseData.put("timestamp", System.currentTimeMillis());

        } catch (Exception e) {
            responseData.put("status", "error");
            responseData.put("message", "Error processing headers: " + e.getMessage());
            responseData.put("error", e.getClass().getSimpleName());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return responseData;
    }

    /**
     * Creates a secure, HttpOnly cookie (backend-only)
     * 
     * @param response HttpServletResponse to add cookie to
     * @param name     Cookie name
     * @param value    Cookie value
     */
    private void createSecureCookie(HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);

        // Set cookie as HttpOnly (not accessible from JavaScript)
        cookie.setHttpOnly(true);

        // Set cookie as Secure (only sent over HTTPS)
        // For development, this can be conditionally set
        cookie.setSecure(isSecureContext());

        // Set cookie path to root
        cookie.setPath("/");

        // Set cookie max age to 1 hour (3600 seconds)
        cookie.setMaxAge(3600);

        // Set SameSite attribute for CSRF protection
        response.addHeader("Set-Cookie",
                String.format("%s=%s; Path=/; HttpOnly; Max-Age=3600; SameSite=Strict%s",
                        name, value, isSecureContext() ? "; Secure" : ""));

        // Also add the cookie normally
        response.addCookie(cookie);
    }

    /**
     * Determines if the current context is secure (HTTPS)
     */
    private boolean isSecureContext() {
        // In development, return false; in production, check actual protocol
        String env = System.getenv("ENVIRONMENT");
        return "production".equalsIgnoreCase(env);
    }

    /**
     * Extracts token from Authorization header
     * Handles formats like "Bearer token123" or "Token token123"
     */
    private String extractTokenFromAuthHeader(String authHeader) {
        if (authHeader == null || authHeader.trim().isEmpty()) {
            return authHeader;
        }

        String[] parts = authHeader.split(" ", 2);
        if (parts.length == 2) {
            return parts[1].trim();
        }
        return authHeader;
    }
}
