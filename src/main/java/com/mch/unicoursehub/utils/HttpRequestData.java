package com.mch.unicoursehub.utils;

import io.micrometer.core.instrument.util.IOUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


public class HttpRequestData {

    /**
     * Retrieves data from the current HTTP request, including URI, method, parameters, headers, and body.
     *
     * @return A map containing the request data, including:
     *         - "uri": The URI of the request.
     *         - "method": The HTTP method used.
     *         - "parameters": A map of query parameters.
     *         - "headers": A map of HTTP headers.
     *         - "body": The body of the request as a string.
     */
    public Map<String, Object> getData() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.put(headerName, headerValue);
        }

        Map<String, Object> data = new HashMap<>();

        data.put("uri", request.getRequestURI());
        data.put("method", request.getMethod());
        data.put("parameters", request.getParameterMap());
        data.put("headers", headers);
        try {
            data.put("body", IOUtils.toString(request.getInputStream()));
        } catch (IOException e) {
            data.put("body", null);
        }

        return data;

    }

}

