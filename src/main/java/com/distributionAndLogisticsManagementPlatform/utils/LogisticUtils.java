package com.distributionAndLogisticsManagementPlatform.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.Map;

//Add your annotations here
public class LogisticUtils {

    //Private constructor to prevent instantiation
    private LogisticUtils() {}

    public static ResponseEntity<Map<String, Object>> getResponseEntity(
            String message, Object payLoad, HttpStatus httpStatus) {

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", message);
        response.put("payLoad", payLoad);
        response.put("status", httpStatus.is2xxSuccessful() ? "SUCCESS" : "ERROR");

        return new ResponseEntity<>(response, httpStatus);
    }
    //Have your return message here
    public static ResponseEntity<String> getResponseEntity(String responseMessage, HttpStatus httpStatus) {
        return new ResponseEntity<>("{\"Message\":\"" + responseMessage + "\"}", httpStatus);
    }
}
