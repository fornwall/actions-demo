package com.example;

import com.google.actions.api.DialogflowApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handles request received via HTTP POST and delegates it to your Actions app. See: [Request
 * handling in Google WebApp
 * Engine](https://cloud.google.com/appengine/docs/standard/java/how-requests-are-handled).
 */
@RestController("/")
public class ActionsController {

  private static final Logger LOG = LoggerFactory.getLogger(MyActionsApp.class);
  private final DialogflowApp actionsApp = new MyActionsApp();

  @PostMapping
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    var requestHeaders = getHeadersMap(request);
    var requestBody = request.getReader().lines().collect(Collectors.joining());

    try {
      LOG.info("Request body = {}", requestBody);
      String jsonResponse = actionsApp.handleRequest(requestBody, requestHeaders).get();
      LOG.info("Response json = {}", jsonResponse);
      response.setContentType("application/json");
      response.getWriter().write(jsonResponse);
    } catch (Exception e) {
      handleError(response, e);
    }
  }

  private void handleError(HttpServletResponse res, Throwable throwable) {
    try {
      throwable.printStackTrace();
      LOG.error("Error in WebApp.handleRequest ", throwable);
      res.getWriter().write("Error handling the intent - " + throwable.getMessage());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Map<String, String> getHeadersMap(HttpServletRequest request) {
    Map<String, String> map = new HashMap<>();

    Enumeration headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String key = (String) headerNames.nextElement();
      String value = request.getHeader(key);
      map.put(key, value);
    }
    return map;
  }

}
