package com.example;

import com.google.actions.api.App;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles request received via HTTP POST and delegates it to your Actions app. See: [Request
 * handling in Google WebApp
 * Engine](https://cloud.google.com/appengine/docs/standard/java/how-requests-are-handled).
 */
@RestController("/")
public class ActionsController {
  private static final Logger LOG = LoggerFactory.getLogger(MyActionsApp.class);
  private final App actionsApp = new MyActionsApp();


  @GetMapping
  public String helloGradle() {
    return "Hello Gradle!";
  }

  @PostMapping
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
    String body = req.getReader().lines().collect(Collectors.joining());
    LOG.info("doPost, body = {}", body);

    try {
      String jsonResponse = actionsApp.handleRequest(body, getHeadersMap(req)).get();
      LOG.info("Generated json = {}", jsonResponse);
      res.setContentType("application/json");
      writeResponse(res, jsonResponse);
    } catch (InterruptedException e) {
      handleError(res, e);
    } catch (ExecutionException e) {
      handleError(res, e);
    }
  }

  private void writeResponse(HttpServletResponse res, String asJson) {
    try {
      res.getWriter().write(asJson);
    } catch (IOException e) {
      e.printStackTrace();
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
    Map<String, String> map = new HashMap();

    Enumeration headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String key = (String) headerNames.nextElement();
      String value = request.getHeader(key);
      map.put(key, value);
    }
    return map;
  }

}
