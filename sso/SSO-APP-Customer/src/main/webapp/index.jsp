<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="javax.servlet.http.Cookie" %>
<%
    String userCookieValue = null;
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for (Cookie cookie : cookies) {
            if ("USER".equals(cookie.getName())) {
                userCookieValue = cookie.getValue();
                break;
            }
        }
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Management System</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }

        .container {
            background: white;
            border-radius: 15px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            padding: 60px 40px;
            max-width: 600px;
            width: 100%;
            text-align: center;
        }

        h1 {
            font-size: 2.5em;
            color: #333;
            margin-bottom: 15px;
        }

        .subtitle {
            color: #666;
            font-size: 1.1em;
            margin-bottom: 40px;
        }

        .icon {
            font-size: 5em;
            margin-bottom: 20px;
        }

        .btn {
            display: inline-block;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 15px 40px;
            border-radius: 8px;
            text-decoration: none;
            font-size: 1.1em;
            font-weight: 500;
            transition: transform 0.3s ease, box-shadow 0.3s ease;
            box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
        }

        .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
        }

        .cookie-info {
            background: #f0f4ff;
            border-left: 4px solid #667eea;
            padding: 15px 20px;
            border-radius: 8px;
            margin: 30px 0;
            text-align: left;
        }

        .cookie-label {
            font-size: 0.95em;
            color: #667eea;
            font-weight: 600;
            margin-bottom: 8px;
        }

        .cookie-value {
            background: white;
            padding: 12px;
            border-radius: 5px;
            font-family: 'Courier New', monospace;
            font-size: 0.9em;
            color: #333;
            word-break: break-all;
            border: 1px solid #e0e7ff;
        }

        .features {
            margin-top: 40px;
            text-align: left;
        }

        .feature {
            padding: 12px 0;
            color: #555;
            font-size: 1em;
        }

        .feature:before {
            content: "✓ ";
            color: #667eea;
            font-weight: bold;
            margin-right: 8px;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="icon">👥</div>
        <h1>User Management System</h1>
        <p class="subtitle">Simple Servlet & JSP Web Application</p>

        <!-- Display USER Cookie if present -->
        <% if (userCookieValue != null && !userCookieValue.isEmpty()) { %>
            <div class="cookie-info">
                <p class="cookie-label">🔐 USER Cookie Value:</p>
                <p class="cookie-value"><%= userCookieValue %></p>
            </div>
        <% } else { %>
            <div class="cookie-info" style="background: #fff3cd; border-left-color: #ffc107;">
                <p class="cookie-label" style="color: #856404;">ℹ️ No USER cookie found</p>
                <p class="cookie-value" style="color: #856404;">Send a request to /api/process-headers to set cookies</p>
            </div>
        <% } %>

        <a href="<%= request.getContextPath() %>/users" class="btn">View Users</a>

        <div class="features">
            <div class="feature">View all users in the system</div>
            <div class="feature">Add new users with role assignment</div>
            <div class="feature">Edit existing user information</div>
            <div class="feature">Delete users from the system</div>
        </div>
    </div>
</body>
</html>