package com.learn.sso.customer.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * ValidationFilter - Validates incoming requests by parsing headers and setting backend-only cookies.
 *
 * This filter:
 * 1. Parses the User-Agent and X-Forwarded-For headers
 * 2. Generates a session token if not present
 * 3. Sets backend-only (HttpOnly) cookies to secure the session
 * 4. Logs validation details
 */
public class ValidationFilter implements Filter {
    private static final Logger logger = Logger.getLogger(ValidationFilter.class.getName());
    private static final String SESSION_TOKEN_COOKIE = "SESSION_TOKEN";
    private static final String VALIDATION_COOKIE = "VALIDATION_ID";
    private static final int COOKIE_MAX_AGE = 3600; // 1 hour in seconds

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("ValidationFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

       /* if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            // Parse headers
            String userAgent = httpRequest.getHeader("User-Agent");
            //String xForwardedFor = httpRequest.getHeader("X-Forwarded-For");
            String remoteAddr = httpRequest.getRemoteAddr();
            String requestPath = httpRequest.getRequestURI();

            logger.info("Request received - Path: " + requestPath +
                       ", UserAgent: " + (userAgent != null ? userAgent : "N/A") +
                     //  ", XForwardedFor: " + (xForwardedFor != null ? xForwardedFor : "N/A") +
                       ", RemoteAddr: " + remoteAddr);

            // Generate or retrieve session token
           /* String sessionToken = getSessionToken(httpRequest);
            if (sessionToken == null) {
                sessionToken = UUID.randomUUID().toString();
                logger.info("Generated new session token: " + sessionToken);
            }*/

            // Generate validation ID
          /*  String validationId = UUID.randomUUID().toString();

            // Set backend-only cookies (HttpOnly flag prevents JavaScript access)
            setBackendOnlyCookie(httpResponse, SESSION_TOKEN_COOKIE, userAgent);
            setBackendOnlyCookie(httpResponse, VALIDATION_COOKIE, validationId);

            // Store in request attributes for downstream use
           // httpRequest.setAttribute("sessionToken", sessionToken);
          //  httpRequest.setAttribute("validationId", validationId);
            //httpRequest.setAttribute("userAgent", userAgent);
            //httpRequest.setAttribute("clientIp", xForwardedFor != null ? xForwardedFor : remoteAddr);

            logger.info("Validation cookies set - userAgent: " + userAgent +
                       ", ValidationId: " + validationId);
        }
*/
        // Continue filter chain
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        logger.info("ValidationFilter destroyed");
    }

    /**
     * Retrieves the session token from the request cookies, if present.
     */
    private String getSessionToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (SESSION_TOKEN_COOKIE.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * Sets a backend-only (HttpOnly and Secure) cookie on the response.
     * HttpOnly flag prevents JavaScript from accessing the cookie (XSS protection).
     * Secure flag ensures the cookie is only sent over HTTPS.
     */
    private void setBackendOnlyCookie(HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);           // Backend only - JavaScript cannot access
        cookie.setSecure(false);            // Set to true if using HTTPS (localhost dev = false)
        cookie.setMaxAge(COOKIE_MAX_AGE);   // 1 hour expiration
        cookie.setPath("/");                // Available for entire application
        response.addCookie(cookie);
    }
}

