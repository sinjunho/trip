package com.ssafy.trip.controller;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface ControllerHelper {

    public default String preProcessing(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("text/html;charset=UTF-8");
        String action = req.getParameter("action");
        if (action == null || action.isBlank()) {
            action = "index";
        }
        return action;
    }

    public default void redirect(HttpServletRequest request, HttpServletResponse response, String location) throws SecurityException, IOException {
        response.sendRedirect(request.getContextPath() + location);
    }

    public default void forward(HttpServletRequest request, HttpServletResponse response, String path) throws SecurityException, IOException, ServletException {
        RequestDispatcher disp = request.getRequestDispatcher(path);
        disp.forward(request, response);
    }
    
    public default Cookie setupCookie(String name, String value, int maxAge, String path, HttpServletResponse resp) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath(path);
        resp.addCookie(cookie);
        return cookie;
    }
}