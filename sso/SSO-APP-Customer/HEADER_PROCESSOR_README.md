# HTTP Header Processor & Cookie Setter

## Overview

This project now includes a **HeaderProcessorServlet** that processes HTTP request headers and sets them as backend-only (HttpOnly) cookies for secure storage.

## Components

### 1. HeaderProcessorServlet
**Location:** `src/main/java/com/learn/sso/customer/servlet/HeaderProcessorServlet.java`

**Endpoint:** `/api/process-headers` (GET/POST)

**Features:**
- Extracts HTTP request headers (User-Agent, Accept, Content-Type, Authorization, etc.)
- Sets them as **HttpOnly cookies** (JavaScript cannot access)
- Sets **Secure flag** for HTTPS (disabled for localhost development)
- Sets **Max-Age** to 3600 seconds (1 hour)
- Returns JSON response with processed headers and cookies

**Headers Processed:**
- User-Agent
- Accept
- Accept-Language
- Accept-Encoding
- Connection
- Authorization
- X-Forwarded-For
- X-Real-IP

### 2. ValidationFilter
**Location:** `src/main/java/com/learn/sso/customer/filter/ValidationFilter.java`

Automatically intercepts all requests and:
- Parses incoming headers
- Generates session tokens
- Sets backend-only cookies
- Makes validation data available to all servlets

### 3. RequestValidationUtil
**Location:** `src/main/java/com/learn/sso/customer/util/RequestValidationUtil.java`

Utility class for servlets to easily access validation data:
```java
String sessionToken = RequestValidationUtil.getSessionToken(request);
String validationId = RequestValidationUtil.getValidationId(request);
String userAgent = RequestValidationUtil.getUserAgent(request);
String clientIp = RequestValidationUtil.getClientIp(request);
```

## Usage Examples

### Test with cURL
```bash
# Simple GET request
curl -i http://localhost:8081/user-management/api/process-headers

# POST with custom headers
curl -i -X POST http://localhost:8081/user-management/api/process-headers \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-token-here" \
  -d '{"test":"data"}'
```

### Test with Browser
1. Open: `http://localhost:8081/user-management/header-processor-test.html`
2. Click the test buttons to send requests
3. Open DevTools (F12) → Application → Cookies to see the backend-only cookies set

### Example Response
```json
{
  "status": "success",
  "message": "Headers processed and set as backend-only cookies",
  "processedHeaders": {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
    "Accept": "application/json",
    "Authorization": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
  },
  "cookiesSet": {
    "user_agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
    "accept": "application/json",
    "authorization": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
  },
  "totalHeadersReceived": 12,
  "headersProcessed": 3,
  "cookiesSet": 3
}
```

## Security Features

### HttpOnly Cookies
- **Prevents XSS attacks:** JavaScript cannot access these cookies via `document.cookie`
- Only accessible by server-side code
- Browser automatically sends cookies with requests

### Secure Flag
- Set to `false` for localhost development
- Set to `true` for production (requires HTTPS)
- Ensures cookies only sent over encrypted connections

### Cookie Configuration
- **Path:** `/` (available for entire application)
- **Max-Age:** 3600 seconds (1 hour)
- **Domain:** Not explicitly set (uses request domain)

## Integration with Existing Code

### UserListServlet Example
```java
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    // Access validation data set by ValidationFilter
    String sessionToken = RequestValidationUtil.getSessionToken(request);
    String validationId = RequestValidationUtil.getValidationId(request);
    String userAgent = RequestValidationUtil.getUserAgent(request);
    String clientIp = RequestValidationUtil.getClientIp(request);

    List<User> users = userDAO.getAllUsers();
    request.setAttribute("users", users);
    request.setAttribute("sessionToken", sessionToken);
    request.setAttribute("validationId", validationId);
    
    request.getRequestDispatcher("/WEB-INF/views/user-list.jsp").forward(request, response);
}
```

## Build and Deploy

### Build WAR
```bash
.\gradlew.bat clean build war
```

### Deploy to Tomcat
1. Copy `build/libs/user-management.war` to Tomcat's `webapps` folder
2. Start Tomcat
3. Access at: `http://localhost:8080/user-management`

### Configure Tomcat Port (8081)
Edit `TOMCAT_HOME/conf/server.xml`:
```xml
<Connector port="8081" protocol="HTTP/1.1"
    connectionTimeout="20000"
    redirectPort="8443" />
```

## Configuration

### Modify Headers to Process
Edit `HeaderProcessorServlet.java`:
```java
private static final String[] HEADERS_TO_PROCESS = {
    "User-Agent",
    "Accept",
    "Accept-Language",
    // Add more headers as needed
};
```

### Adjust Cookie Expiration
Edit `HeaderProcessorServlet.java` or `ValidationFilter.java`:
```java
cookie.setMaxAge(3600); // Change 3600 to desired seconds
```

## Files Modified/Created

### Created:
- `src/main/java/com/learn/sso/customer/servlet/HeaderProcessorServlet.java`
- `src/main/java/com/learn/sso/customer/filter/ValidationFilter.java`
- `src/main/java/com/learn/sso/customer/util/RequestValidationUtil.java`
- `src/main/webapp/header-processor-test.html`

### Modified:
- `src/main/webapp/WEB-INF/web.xml` - Added filter and servlet mappings
- `build.gradle` - Added servlet API dependency and providedCompile configuration
- `src/main/java/com/learn/sso/customer/servlet/UserListServlet.java` - Integrated validation utilities

## Testing

### Automated Tests
To add unit tests, create `src/test/java/com/learn/sso/customer/servlet/HeaderProcessorServletTest.java`

### Manual Testing Steps
1. Build: `.\gradlew.bat clean build war`
2. Deploy WAR to Tomcat
3. Open test page: `http://localhost:8081/user-management/header-processor-test.html`
4. Click test buttons
5. Check browser DevTools for cookies (F12 → Application → Cookies)
6. Verify JSON response

## Logging

All activity is logged using Java's built-in `java.util.logging.Logger`:
- Filter initialization/destruction
- Request validation events
- Header processing
- Cookie creation

View logs in Tomcat logs folder: `TOMCAT_HOME/logs/catalina.out`

## Future Enhancements

1. Add custom header filtering
2. Implement header validation rules
3. Add database logging for header audit trail
4. Create metrics/monitoring for header processing
5. Add support for header compression
6. Implement rate limiting per IP

## Troubleshooting

### Cookies Not Set
- Check browser DevTools (F12 → Application → Cookies)
- Verify HttpOnly flag is present (should show as "HttpOnly" in cookies table)
- Ensure Secure flag is appropriate for your environment

### Headers Not Processed
- Check logs for errors
- Verify header names match exactly (case-sensitive in some contexts)
- Ensure servlet is registered in web.xml

### JSON Response Invalid
- Check browser console for JavaScript errors
- Verify response headers include `Content-Type: application/json`
- Test with cURL to isolate client-side issues

## Support

For issues or questions, check:
1. Server logs: `TOMCAT_HOME/logs/catalina.out`
2. Browser console: F12
3. Network tab: F12 → Network
4. HTTP response status codes

