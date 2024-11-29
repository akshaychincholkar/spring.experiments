package com.game.org.spring.experiments.controller;
//
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.slf4j.MDC;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/forward")
public class ExampleController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    RestTemplateBuilder restTemplateBuilder;

    @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
//    @RequestMapping(value = "/**")
    public ResponseEntity<?> forwardRequest(
//            @RequestParam(required = false) String targetUrl,
            @RequestBody(required = false) String body,
            @RequestHeader HttpHeaders headers,
            HttpMethod method,
            HttpServletRequest request) {
//        public ResponseEntity<?> forwardRequest
//        (HttpServletRequest request,HttpMethod method, @RequestHeader HttpHeaders headers) throws IOException {
//        HttpHeaders headers = getDefaultHeaders(null);
//        String body = getRequestBody(request);
        String targetUrl = urlBuilder(request);
        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                URI.create(targetUrl),
                method,
                requestEntity,
                String.class
        );
        return ResponseEntity.status(response.getStatusCode())
                .headers(response.getHeaders())
                .body(response.getBody());
//        return forward(targetUrl, headers, body, method);
    }
        @GetMapping("/greet")
    public ResponseEntity<?> forward(HttpServletRequest request) throws IOException {
        return ResponseEntity.ok("Hello, Welcome to Spring Boot!");
    }
    private ResponseEntity<?> forward(String targetUrl, HttpHeaders headers, String body, HttpMethod httpMethod) {
        try {
            // Prepare request entity with headers and body
        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);

//        RestTemplate restTemplate = restTemplateBuilder.build();
//        HttpHeaders header = getDefaultHeaders(null);
//        HttpMethod method = HttpMethod.valueOf(((HttpServletRequest) request).getMethod());
//        String requestBody = getRequestBody(request);
//        ResponseEntity response = restTemplate.exchange(urlBuilder(request),method, new HttpEntity<>(requestBody,header),String.class);
//        System.out.println("Response: "+response.getStatusCode());
//        return response;

            // Send the request to the target URL using RestTemplate
            ResponseEntity<String> response = restTemplate.exchange(
                    URI.create(targetUrl),
                    httpMethod,
                    requestEntity,
                    String.class
            );

            // Return the response from the target service
            return ResponseEntity.status(response.getStatusCode())
                    .headers(response.getHeaders())
                    .body(response.getBody());
        } catch (Exception e) {
            // Handle exceptions and return meaningful error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error forwarding request: " + e.getMessage());
        }
    }
    private HttpHeaders getDefaultHeaders(HttpHeaders headers){
        if(headers == null){
            headers = new HttpHeaders();
        }
        headers.add("X-UI-Request-ID", MDC.get("requestId"));
        headers.add("X-User-ID", MDC.get("reqUser"));
        return headers;
    }
    public static class RestTemplateConfig {
    }
    private String urlBuilder(HttpServletRequest request){
        Map<String,String[]> paramMap = request.getParameterMap();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:8080"+request.getRequestURI());
        paramMap.forEach((key,values) -> {
            for(String value:values){
                builder.queryParam(key,value);
            }
        });
        URI uri = builder.build().toUri();
        return uri.toString();
    }
    private String getRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder requestBody = new StringBuilder();
//        request.setCharacterEncoding();
        BufferedReader reader = request.getReader();
        String line;
        while((line = reader.readLine()) != null){
            requestBody.append(line);
        }
        return requestBody.toString();
    }
}

//import jakarta.servlet.http.HttpServletRequest;
//import org.slf4j.MDC;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.web.client.RestTemplateBuilder;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.net.URI;
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/example")
//public class ExampleController {
//    @Autowired
//    RestTemplateBuilder restTemplateBuilder;
//
////    @GetMapping("/**")
////    public ResponseEntity<?> forward(HttpServletRequest request) throws IOException {
////
//////        return ResponseEntity.ok("Hello, Welcome to Spring Boot!");
////        RestTemplate restTemplate = restTemplateBuilder.build();
////        HttpHeaders header = getDefaultHeaders(null);
////        HttpMethod method = HttpMethod.valueOf(((HttpServletRequest) request).getMethod());
////        String requestBody = getRequestBody(request);
////        ResponseEntity response = restTemplate.exchange(urlBuilder(request),method, new HttpEntity<>(requestBody,header),String.class);
////        System.out.println("Response: "+response.getStatusCode());
////        return response;
////    }
//
//    private String getRequestBody(HttpServletRequest request) throws IOException {
//        StringBuilder requestBody = new StringBuilder();
////        request.setCharacterEncoding();
//        BufferedReader reader = request.getReader();
//        String line;
//        while((line = reader.readLine()) != null){
//            requestBody.append(line);
//        }
//        return requestBody.toString();
//    }
//    private String urlBuilder(HttpServletRequest request){
//        Map<String,String[]> paramMap = request.getParameterMap();
//        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:8080"+request.getRequestURI());
//        paramMap.forEach((key,values) -> {
//            for(String value:values){
//                builder.queryParam(key,value);
//            }
//        });
//        URI uri = builder.build().toUri();
//        return uri.toString();
//    }
//
//
//    private HttpHeaders getDefaultHeaders(HttpHeaders headers){
//        if(headers == null){
//            headers = new HttpHeaders();
//        }
//        headers.add("X-UI-Request-ID", MDC.get("requestId"));
//        headers.add("X-User-ID", MDC.get("reqUser"));
//        return headers;
//    }
//    @GetMapping("/user/{id}")
//    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
//        // Mocking a user response
//        Map<String, Object> user = new HashMap<>();
//        user.put("id", id);
//        user.put("name", "John Doe");
//        user.put("email", "john.doe@example.com");
//
//        return ResponseEntity.ok(user);
//    }
//
//    @GetMapping("/search")
//    public ResponseEntity<Map<String, Object>> searchUsers(@RequestParam String name) {
//        // Mocking a search response
//        Map<String, Object> response = new HashMap<>();
//        response.put("search", name);
//        response.put("count", 1); // Just a mocked count
//        response.put("result", "Found 1 user matching the name: " + name);
//
//        return ResponseEntity.ok(response);
//    }
//}
