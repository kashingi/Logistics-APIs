package com.distributionAndLogisticsManagementPlatform.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

//Add your annotations here
public class LogisticUtils {

    //Private constructor to prevent instantiation
    private LogisticUtils() {}

    //Have your return message here
    public static ResponseEntity<String> getResponseEntity(String responseMessage, HttpStatus httpStatus) {
        return new ResponseEntity<>("{\"Message\":\"" + responseMessage + "\"}", httpStatus);
    }
}
