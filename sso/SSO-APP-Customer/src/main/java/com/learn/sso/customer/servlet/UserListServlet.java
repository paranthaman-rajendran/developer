package com.learn.sso.customer.servlet;

import com.learn.sso.customer.model.User;
import com.learn.sso.customer.dao.UserDAO;
import com.learn.sso.customer.util.RequestValidationUtil;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class UserListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(UserListServlet.class.getName());
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = UserDAO.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Access validation data set by ValidationFilter
        String sessionToken = RequestValidationUtil.getSessionToken(request);
        String validationId = RequestValidationUtil.getValidationId(request);
        String userAgent = RequestValidationUtil.getUserAgent(request);
        String clientIp = RequestValidationUtil.getClientIp(request);

        logger.info("UserListServlet - SessionToken: " + sessionToken +
                   ", ValidationId: " + validationId +
                   ", UserAgent: " + userAgent +
                   ", ClientIp: " + clientIp);

        List<User> users = userDAO.getAllUsers();
        request.setAttribute("users", users);
        request.setAttribute("sessionToken", sessionToken);
        request.setAttribute("validationId", validationId);

        request.getRequestDispatcher("/WEB-INF/views/user-list.jsp").forward(request, response);
    }
}