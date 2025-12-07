package com.learn.sso.customer.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * HeaderProcessorServlet - Processes HTTP request headers and sets them as backend-only cookies.
 *
 * This servlet:
 * 1. Extracts specific headers from the incoming request (User-Agent, Accept, Content-Type, etc.)
 * 2. Sets them as HttpOnly cookies for backend-only use (JavaScript cannot access)
 * 3. Returns a JSON response showing which headers were processed and which cookies were set
 *
 * Endpoint: /api/process-headers
 */
public class HeaderProcessorServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(HeaderProcessorServlet.class.getName());

    // Headers to extract and set as cookies
    private static final String[] HEADERS_TO_PROCESS = {
        "User-Agent",
        "Accept",
        "Accept-Language",
        "Accept-Encoding",
        "Connection",
        "Authorization",
        "X-Forwarded-For",
        "X-Real-IP"
    };

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, String> processedHeaders = new HashMap<>();
        Map<String, String> setCookies = new HashMap<>();

        logger.info("HeaderProcessorServlet - Processing request headers");

        // Extract specific headers and set them as cookies
        for (String headerName : HEADERS_TO_PROCESS) {
            String headerValue = request.getHeader(headerName);
            if (headerValue != null && !headerValue.isEmpty()) {
                processedHeaders.put(headerName, headerValue);

                // Create a cookie name from the header (replace special chars)
                String cookieName = sanitizeCookieName(headerName);

                // Set as backend-only cookie
                setBackendOnlyCookie(response, cookieName, headerValue);
                setCookies.put(cookieName, headerValue);

                logger.info("Header set as cookie - " + cookieName + ": " +
                           (headerValue.length() > 50 ? headerValue.substring(0, 50) + "..." : headerValue));
            }
        }

        // Get all headers for logging
        Map<String, String> allHeaders = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            allHeaders.put(headerName, headerValue);
        }

        // Build JSON response
        StringBuilder jsonResponse = new StringBuilder();
        jsonResponse.append("{\n");
        jsonResponse.append("  \"status\": \"success\",\n");
        jsonResponse.append("  \"message\": \"Headers processed and set as backend-only cookies\",\n");
        jsonResponse.append("  \"processedHeaders\": {\n");

        // Add processed headers
        int headerCount = 0;
        for (Map.Entry<String, String> entry : processedHeaders.entrySet()) {
            jsonResponse.append("    \"").append(entry.getKey()).append("\": \"")
                       .append(escapeJson(entry.getValue())).append("\"");
            if (++headerCount < processedHeaders.size()) {
                jsonResponse.append(",");
            }
            jsonResponse.append("\n");
        }

        jsonResponse.append("  },\n");
        jsonResponse.append("  \"cookiesSet\": {\n");

        // Add set cookies
        int cookieCount = 0;
        for (Map.Entry<String, String> entry : setCookies.entrySet()) {
            jsonResponse.append("    \"").append(entry.getKey()).append("\": \"")
                       .append(escapeJson(entry.getValue())).append("\"");
            if (++cookieCount < setCookies.size()) {
                jsonResponse.append(",");
            }
            jsonResponse.append("\n");
        }

        jsonResponse.append("  },\n");
        jsonResponse.append("  \"totalHeadersReceived\": ").append(allHeaders.size()).append(",\n");
        jsonResponse.append("  \"headersProcessed\": ").append(processedHeaders.size()).append(",\n");
        jsonResponse.append("  \"cookiesSet\": ").append(setCookies.size()).append("\n");
        jsonResponse.append("}\n");

        // Send response
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse.toString());
        }

        logger.info("HeaderProcessorServlet - Response sent. Headers processed: " + processedHeaders.size() +
                   ", Cookies set: " + setCookies.size());
    }

    /**
     * Sets a backend-only (HttpOnly) cookie on the response.
     * HttpOnly flag prevents JavaScript from accessing the cookie (XSS protection).
     */
    private void setBackendOnlyCookie(HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);           // Backend only - JavaScript cannot access
        cookie.setSecure(false);            // Set to true if using HTTPS (localhost dev = false)
        cookie.setMaxAge(3600);             // 1 hour expiration
        cookie.setPath("/");                // Available for entire application
        response.addCookie(cookie);
    }

    /**
     * Sanitizes a header name to make it a valid cookie name.
     * Removes special characters and converts to a safe format.
     */
    private String sanitizeCookieName(String headerName) {
        return headerName
            .replace("-", "_")              // Replace hyphens with underscores
            .replace(" ", "")               // Remove spaces
            .toLowerCase();                 // Convert to lowercase
    }

    /**
     * Escapes special characters in JSON strings.
     */
    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }
}

